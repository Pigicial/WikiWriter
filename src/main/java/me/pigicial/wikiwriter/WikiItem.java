package me.pigicial.wikiwriter;

import lombok.Getter;
import me.pigicial.wikiwriter.core.Config;
import me.pigicial.wikiwriter.features.*;
import me.pigicial.wikiwriter.utils.Action;
import me.pigicial.wikiwriter.utils.Rarity;
import me.pigicial.wikiwriter.utils.TextUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.text.Format;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiItem {
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-ORX]");
    private static final Pattern AUCTION_ITEM_COUNT_PATTERN = Pattern.compile("§7(-?[1-9][0-9]?[0-9]?[0-9]?)x ");
    private static final Pattern PET_NUMBER_COUNTER = Pattern.compile("\\[Lvl ([01]?[0-9][0-9]?|200)] ");
    private static final Pattern PET_NUMBER_COUNTER_WITH_COLOR = Pattern.compile("§7\\[Lvl ([01]?[0-9][0-9]?|200)] ");
    private static final Pattern SHOP_NAME_ITEM_COUNT = Pattern.compile("§8x([0-9][0-9]?)");

    private static final Config CONFIG = WikiWriter.getInstance().getConfig();

    private String minecraftId = "";
    private String registryName = "";
    private String skyblockId = "";

    private Rarity rarity = Rarity.NONE;
    private List<String> lore = new ArrayList<>();

    @Getter
    private int stackSize = 1;

    @Nullable
    private String petId = null;
    private String nameWithoutColor = "";
    private String typeText = "";
    private String initialChar = "";
    private String loreAsString = "";
    private String nameWithColor = "";
    private String referenceId = "";
    private String textureLink = "";
    @Getter
    private String removedLore = "";

    private boolean emptyTitle = true;
    private boolean showRarity = false;
    private boolean pet = false;
    private boolean recipeMode = false;
    private boolean mysteryPet = false;
    private boolean skyblockItem = false;
    @Getter
    private boolean hasSkyblockItemID = false;
    @Getter
    private boolean shopItem = false;

    public WikiItem(@Nullable String guiName, ItemStack stack, Action action, boolean referenceMode) {
        if (stack == null) {
            return;
        }

        NbtCompound nbt = stack.getNbt();
        // I don't think this can be null but there's null checks in ItemStack so just in case
        if (nbt == null) {
            nbt = new NbtCompound();
        }

        NbtCompound extraAttributes = nbt.getCompound("ExtraAttributes");
        skyblockId = extraAttributes.getString("id").toLowerCase();

        NbtCompound display = nbt.getCompound("display");
        NbtList loreTag = display.getList("Lore", 8); // 8 is NBTTagString from NBTBase#createNewByType

        lore = new ArrayList<>();
        for (int i = 0; i < loreTag.size(); i++) {
            lore.add(loreTag.getString(i));
        }

        nameWithColor = stack.getName().getString();

        // Removes lore (if not a reference or shop item), and tracks removed lore if a reference or shop item
        parseLore(action, referenceMode);

        // Update reference mode (for usage below)
        if (!referenceMode && shopItem) {
            referenceMode = true;
        }

        // Sets the minecraftId, registryName, skyblockId, and more
        parseIDs(stack, guiName);

        // Potentially removes pet levels from the name, as well as the item amount from the name
        // petNameChanged is used below
        boolean petNameChanged = updateName(stack);

        // Figures out the rarity of the item based on the lore and item name
        parseRarity(extraAttributes, nameWithColor);

        // Fixes various item ID quirks (and handles colors and whatnot)
        fixIDs(stack, display, extraAttributes);

        // Check for mystery pet and pet number stuff
        updateNameAndPetInfo(petNameChanged);

        // Updates the stack sizes (and name if so) of the item based on various factors
        updateStackSizes(referenceMode, guiName, extraAttributes);

        String nameWithReplacements = RegexTextReplacements.replaceEverything(nameWithColor, true);
        nameWithColor = ColorReplacementFeature.replace(nameWithReplacements);

        String rawNameWithoutColor = Formatting.strip(nameWithReplacements);
        nameWithoutColor = ColorReplacementFeature.replace(rawNameWithoutColor);

        emptyTitle = nameWithoutColor.replace(" ", "").isEmpty();
        boolean checkLore = true;

        // If "Hide All" or "Show Go Back to SkyBlock Menu"
        if (CONFIG.removeCloseGoBackAndPageItems <= 1) {
            if (nameWithoutColor.contains("Close") && registryName.equalsIgnoreCase("barrier")) {
                emptyTitle = true;
                checkLore = false;
            } else if (registryName.equalsIgnoreCase("arrow") && (nameWithoutColor.contains("Page") || nameWithoutColor.contains("Level") || (nameWithoutColor.contains("Go Back") && (lore.isEmpty() || !lore.get(0).toLowerCase().contains("skyblock menu") || CONFIG.removeCloseGoBackAndPageItems == 0)))) {
                emptyTitle = true;
                checkLore = false;
            }
        }

        if (skyblockItem && !hasSkyblockItemID && nameWithoutColor.endsWith(" Minion")) {
            // for crafted minions, really
            skyblockId = nameWithoutColor.substring(0, nameWithoutColor.length() - 7) + "_GENERATOR_1";
            hasSkyblockItemID = true;
        }

        if (skyblockItem && !hasSkyblockItemID) {
            skyblockItem = false;
            minecraftId = "head";
        }

        typeText = skyblockItem ? pet ? "sbpet" : "sb" : "mc";
        initialChar = emptyTitle && (!checkLore || lore.isEmpty()) ? "?" : CONFIG.disableClicking && !hasSkyblockItemID ? "!" : "";

        if (nameWithColor.contains("[") || nameWithColor.contains("]") || nameWithColor.contains("{") || nameWithColor.contains("}")) {
            lore.add(0, RegexTextReplacements.LINE_SEPARATORS.replace(nameWithColor));
            showRarity = false;
            nameWithColor = "e";
            nameWithoutColor = "e";
        }

        this.recipeMode = referenceMode;

        this.textureLink = skyblockItem ? pet ? petId : skyblockId.toLowerCase() : minecraftId;
        boolean potion = skyblockId.equalsIgnoreCase("potion");
        String baseReferenceId = skyblockItem || (hasSkyblockItemID && !potion) ? pet ? petId : skyblockId.toLowerCase() : potion ? nameWithoutColor : minecraftId;
        if (referenceMode) {
            this.referenceId = (emptyTitle || minecraftId.equals(nameWithoutColor) ? (showRarity ? nameWithoutColor.toLowerCase() : nameWithColor.toLowerCase()) : baseReferenceId);
        } else {
            this.referenceId = baseReferenceId;
        }
    }

    private void parseLore(Action action, boolean referenceMode) {
        List<String> removedLore;

        if (referenceMode) {
            // If reference mode is enabled, then the only lore that should be removed is shop lore. This is because:
            //   a. none of the other filters match (I'm pretty sure at least)
            //   b. the shop filters need to be added after the reference text (depending on settings)

            LoreRemovalFeature.RemoveData shopFilterData = LoreRemovalFeature.checkAndFilter(action, new ArrayList<>(lore), LoreRemovalFeature.SHOP_FILTERS);
            removedLore = shopFilterData.getRemovedLore();

            // SHOP_9 isn't checked for in SHOP_FILTERS, since it needs to be handled separately since it can break stuff if both bits of text are
            // removed (Elegant Tuxedo for example)
            if (removedLore.isEmpty()) {
                LoreRemovalFeature.RemoveData extraShopFilterData = LoreRemovalFeature.checkAndFilter(action, new ArrayList<>(lore), LoreRemovalFeature.SHOP_9);
                removedLore = extraShopFilterData.getRemovedLore();
            }

            if (!removedLore.isEmpty()) {
                shopItem = true;
            }
        } else {
            LoreRemovalFeature.RemoveData removeData = LoreRemovalFeature.checkAndFilter(action, lore, LoreRemovalFeature.values());
            removedLore = removeData.getRemovedLore();
            loreAsString = TextUtils.convertListToString(lore);
        }

        this.removedLore = TextUtils.convertListToString(removedLore);
        loreAsString = TextUtils.convertListToString(lore);
    }

    private void parseIDs(ItemStack stack, String guiName) {
        minecraftId = stack.getItem().getItemStackDisplayName(stack).toLowerCase().replace(" ", "_");

        registryName = stack.getItem().getRegistryName().replace("minecraft:", "");
        int dataValue = stack.getItem().getMetadata(stack);
        if (dataValue != 0) {
            registryName = registryName + ":" + NumberFormat.getInstance().format(dataValue);
        }

        boolean head = minecraftId.contains("head") || minecraftId.contains("skull");
        skyblockItem = head && !minecraftId.equalsIgnoreCase("zombie_head") && !minecraftId.equalsIgnoreCase("skeleton_skull") && !minecraftId.equalsIgnoreCase("creeper_head") && !minecraftId.equalsIgnoreCase("wither_skeleton_skull");

        if (guiName != null && skyblockId.equals("") && (guiName.contains("Oringo") || (guiName.contains("Pets") && skyblockItem && !lore.isEmpty() && lore.get(lore.size() - 1).contains("Click to summon")))) {
            skyblockId = "pet";
        }

        pet = skyblockId.equalsIgnoreCase("pet") || (skyblockId.equals("") && !lore.isEmpty() && lore.get(0).contains("Pet") && skyblockItem);
        hasSkyblockItemID = !skyblockId.equals("");

        if (pet) {
            // update if it didn't include the id but is a pet
            skyblockId = "pet";
            hasSkyblockItemID = true;
        } else if (skyblockItem && skyblockId.equals("")) {
            skyblockId = "head";
        }
    }

    // Returns if the pet name was changed
    private boolean updateName(ItemStack stack) {
        nameWithColor = stack.getName().getString();

        boolean petNameChanged = false;
        if (CONFIG.removePetLevelsAndChangePetName && pet) {
            Matcher matcher = PET_NUMBER_COUNTER_WITH_COLOR.matcher(nameWithColor);
            while (matcher.find()) {
                petNameChanged = true;
                nameWithColor = nameWithColor.replace(matcher.group(), "");
            }
        }

        if (CONFIG.removeItemAmountsFromItemNames) {
            Matcher matcher = SHOP_NAME_ITEM_COUNT.matcher(nameWithColor);
            while (matcher.find()) {
                nameWithColor = nameWithColor.replace(matcher.group(), "");
            }
        }

        return petNameChanged;
    }

    private void parseRarity(NbtCompound extraAttributes, String name) {
        Rarity baseItemRarity;

        int rarityUpgrades = extraAttributes.getInt("rarity_upgrades");

        loreLoop:
        for (String s : lore) {
            String lineWithoutColor = Formatting.strip(s);
            if (lineWithoutColor == null) continue;
            if (lineWithoutColor.length() <= 1) continue;
            if (rarityUpgrades > 0 && lineWithoutColor.length() < 4) continue;

            for (Rarity r : Rarity.values()) {
                if (r == Rarity.NONE) continue;
                String rarityName = r.name().replace("_", " ");
                if (lineWithoutColor.contains(rarityName)) {
                    rarity = r;
                    baseItemRarity = r;
                    if (rarityUpgrades > 0) {
                        for (int i = 0; i < rarityUpgrades; i++) {
                            baseItemRarity = baseItemRarity.getPreviousRarity();
                        }
                    }
                    break loreLoop;
                }
            }
        }

        if (rarity != Rarity.NONE || name.isEmpty()) {
            return;
        }

        int lastEnd = -1;
        Matcher matcher = STRIP_COLOR_PATTERN.matcher(name);

        Rarity lastFound = null;
        while (matcher.find()) {
            int start = matcher.start();
            if (lastEnd != -1 && start > lastEnd) {
                break;
            }
            String match = matcher.group();
            char o = match.charAt(1);
            for (Rarity rarity : Rarity.values()) {
                if (rarity == Rarity.NONE) continue;
                if (o == rarity.getColorCode()) {
                    lastFound = rarity;
                    break;
                }
            }

            lastEnd = matcher.end();
        }

        if (lastFound != null) {
            rarity = lastFound;
            return;
        }

        matcher = STRIP_COLOR_PATTERN.matcher(name);

        // Only check for custom colors if they're supported by the version of Minecraft used, otherwise it's unnecessary
        colorLoop:
        while (matcher.find()) {
            int start = matcher.start();
            if (lastEnd != -1 && start > lastEnd) {
                break;
            }
            int end = matcher.end();
            for (Formatting colorCode : Formatting.values()) {
                if (colorCode.isColor() && Rarity.COLOR_CODES.contains(name.charAt(end - 1))) {
                    for (Rarity r : Rarity.values()) {
                        if (r == Rarity.NONE) continue;
                        if (r.getColorCode() == name.charAt(end - 1)) {
                            rarity = r;
                            baseItemRarity = r;
                            if (rarityUpgrades > 0) {
                                for (int i = 0; i < rarityUpgrades; i++) {
                                    baseItemRarity = baseItemRarity.getPreviousRarity();
                                }
                            }
                            break colorLoop;
                        }
                    }
                }
            }
            lastEnd = end;
        }
    }

    private void fixIDs(ItemStack stack, NbtCompound display, NbtCompound extraAttributes) {
        boolean hasEnchantments = stack.isItemEnabled(FeatureSet.of(FeatureFlags.VANILLA)) || skyblockId.equalsIgnoreCase("potion");

        if (skyblockItem) {
            minecraftId = "head";
        }

        stackSize = stack.getCount();

        long color = display.getLong("color");
        if (minecraftId.startsWith("leather") && color != 0) {
            LeatherColorFinderFeature colorFinderFeature = LeatherColorFinderFeature.findColor((int) color);
            if (colorFinderFeature != LeatherColorFinderFeature.DEFAULT) {
                minecraftId = minecraftId + "_" + colorFinderFeature.name().toLowerCase();
            }
        }

        if (skyblockItem && skyblockId.contains("backpack") && CONFIG.backpackColors && extraAttributes.contains("backpack_color", 8)) {
            String backpackColor = extraAttributes.getString("backpack_color").toLowerCase();
            if (!backpackColor.equals("") && !backpackColor.equalsIgnoreCase("default")) {
                skyblockId = backpackColor + "_" + skyblockId;
            }
        }

        if (minecraftId.equalsIgnoreCase("music_disc")) {
            minecraftId = registryName;
        }

        if (minecraftId.equalsIgnoreCase("button")) {
            minecraftId = registryName.equalsIgnoreCase("wooden_button") ? "oak_button" : registryName;
        }

        skyblockId = VersionConverterFeature.replace(skyblockId, true);
        minecraftId = VersionConverterFeature.replace(minecraftId, false);

        // spawn eggs are formatted like ghast_spawn_egg instead of spawn_ghast (which it normally gets)
        if (minecraftId.startsWith("spawn_")) {
            minecraftId = minecraftId.substring(6) + "_spawn_egg";
        }

        if (minecraftId.contains("chain_")) {
            minecraftId = minecraftId.replace("chain_", "chainmail_");
        }

        if (!minecraftId.equalsIgnoreCase("head") && hasEnchantments && !minecraftId.startsWith("enchanted_")) {
            minecraftId = "enchanted_" + minecraftId;
        }
    }

    private void updateNameAndPetInfo(boolean petNameChanged) {
        if (skyblockItem && pet) {

            String rawName = Formatting.strip(nameWithColor);
            mysteryPet = rawName.startsWith("Mystery ");
            if (mysteryPet) {
                rawName = rawName.substring(8);
            }

            if (mysteryPet && rawName.endsWith(" Pet")) {
                rawName = rawName.substring(0, rawName.length() - 4);
            }

            Matcher matcher = PET_NUMBER_COUNTER.matcher(rawName);
            boolean found = petNameChanged;

            while (matcher.find()) {
                found = true;
                rawName = rawName.replace(matcher.group(), "");
            }

            if (rawName.endsWith("✦")) {
                rawName = rawName.substring(0, rawName.length() - 2);
            }

            if (petNameChanged && !mysteryPet) {
                nameWithColor = nameWithColor.replace(rawName, rawName + " Pet");
            }

            if (found || mysteryPet) {
                petId = rawName.replace(" ", "_").toLowerCase();
            }
        }
    }

    private void updateStackSizes(boolean referenceMode, String guiName, NbtCompound extraAttributes) {
        showRarity = rarity != Rarity.NONE;
        if (!(referenceMode && CONFIG.recipeMode) && showRarity && CONFIG.guaranteedStackSizeToggled && hasSkyblockItemID) {

            int maxStackSize = stackSize;

            if (!pet && extraAttributes.contains("uuid", 8)) {
                maxStackSize = 1;
            }

            int setStackSize = CONFIG.setStackSize;
            if (!CONFIG.bypassStackSizeLimit) {
                if (setStackSize > maxStackSize) {
                    setStackSize = maxStackSize;
                }
            }

            if (setStackSize != stackSize) {
                Matcher matcher = AUCTION_ITEM_COUNT_PATTERN.matcher(nameWithColor);
                while (matcher.find()) {
                    nameWithColor = nameWithColor.replace(matcher.group(), "");
                }

                if (setStackSize != 1) {
                    showRarity = false;
                }

                // only change stack amounts in auction menus, since that's only where they show up in
                if (guiName != null && guiName.toLowerCase().contains("auction")) {
                    String amountString = Formatting.GRAY + NumberFormat.getInstance().format(setStackSize) + "x " + Formatting.RESET;
                    nameWithColor = amountString + nameWithColor;
                }

                stackSize = setStackSize;
            }
        }
    }

    public String convertToReference() {
        if (minecraftId.equals("")) return "";

        if (pet) {
            return "{{Item_" + (recipeMode ? "pet_craft_" : "pet_") + petId + (!mysteryPet ? "_" + rarity.toString() : "") + "}}";
        } else {
            return "{{Item_" + referenceId.replace(" ", "_").toLowerCase() + "}}";
        }
    }

    public String convertToReferenceWithPotentialShopLore() {
        return convertToReference() + (removedLore.length() == 0 ? "" : TextUtils.unescapeText("\n") + removedLore) + (stackSize != 1 ? "," + NumberFormat.getInstance().format(stackSize) : "");
    }

    public String convertToWikiItem() {
        if (minecraftId.equals("")) return "";

        return initialChar + typeText + "," + (!showRarity ? "" : rarity.toString()) + "," + textureLink + (emptyTitle ? "" : (minecraftId.equals(nameWithoutColor) ? "" : ":" + (showRarity ? nameWithoutColor : nameWithColor))) + (lore.isEmpty() && stackSize == 1 ? "" : ("," + NumberFormat.getInstance().format(stackSize) + (emptyTitle || lore.isEmpty() ? "" : "," + loreAsString)));
    }
}
