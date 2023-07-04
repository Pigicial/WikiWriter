package me.pigicial.wikiwriter;

import lombok.Getter;
import me.pigicial.wikiwriter.core.Config;
import me.pigicial.wikiwriter.features.ColorReplacementFeature;
import me.pigicial.wikiwriter.features.LeatherColorFinderFeature;
import me.pigicial.wikiwriter.features.LoreRemovalFeature;
import me.pigicial.wikiwriter.features.RegexTextReplacements;
import me.pigicial.wikiwriter.utils.Action;
import me.pigicial.wikiwriter.utils.Rarity;
import me.pigicial.wikiwriter.utils.TextUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiItem {
    private static final Pattern AUCTION_ITEM_COUNT_PATTERN = Pattern.compile("§7(-?[1-9][0-9]?[0-9]?[0-9]?)x ");
    private static final Pattern PET_NUMBER_COUNTER = Pattern.compile("\\[Lvl ([01]?[0-9][0-9]?|200)] ");
    private static final Pattern PET_NUMBER_COUNTER_WITH_COLOR = Pattern.compile("§7\\[Lvl ([01]?[0-9][0-9]?|200)] ");
    private static final Pattern SHOP_NAME_ITEM_COUNT = Pattern.compile("§8x([0-9][0-9]?)");

    private static final Config CONFIG = WikiWriter.getInstance().getConfig();

    private String minecraftId = "";
    private String skyBlockId;

    private final Rarity rarity;
    private final List<String> lore;

    @Getter
    private int stackSize = 1;

    @Nullable
    private String petId = null;
    private String nameWithoutColor;
    private final String typeText;
    private final String initialChar;
    private String loreAsString = "";
    private String nameWithColor;
    private final String referenceId;
    private final String textureLink;
    @Getter
    private String removedLore = "";

    private final boolean emptyTitle;
    private boolean showRarity = false;
    private boolean pet = false;
    private final boolean recipeMode;
    private boolean mysteryPet = false;
    private boolean skyBlockItem = false;
    @Getter
    private boolean hasSkyBlockItemID = false;
    @Getter
    private boolean shopItem = false;

    public WikiItem(@Nullable String guiName, @NotNull ItemStack stack, Action action, boolean referenceMode) {
        NbtCompound nbt = stack.getNbt();
        // I don't think this can be null but there's null checks in ItemStack so just in case
        if (nbt == null) {
            nbt = new NbtCompound();
        }

        NbtCompound extraAttributes = nbt.getCompound("ExtraAttributes");
        skyBlockId = extraAttributes.getString("id").toLowerCase();

        NbtCompound display = nbt.getCompound("display");
        NbtList loreTag = display.getList("Lore", NbtElement.STRING_TYPE);

        lore = new ArrayList<>();
        for (int i = 0; i < loreTag.size(); i++) {
            String jsonLine = loreTag.getString(i);
            String legacyLine = TextUtils.convertJsonTextToLegacy(jsonLine);

            lore.add(legacyLine);
        }

        nameWithColor = TextUtils.convertJsonTextToLegacy(Text.Serializer.toJson(stack.getName()));

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
        rarity = Rarity.parseRarity(lore, nameWithColor);

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

        if (skyBlockItem && !hasSkyBlockItemID && nameWithoutColor.endsWith(" Minion")) {
            // for crafted minions, really
            skyBlockId = nameWithoutColor.substring(0, nameWithoutColor.length() - 7) + "_GENERATOR_1";
            hasSkyBlockItemID = true;
        }

        if (skyBlockItem && !hasSkyBlockItemID) {
            skyBlockItem = false;
        }

        typeText = skyBlockItem ? pet ? "sbpet" : "sb" : "mc";
        initialChar = emptyTitle && lore.isEmpty() ? "?" : CONFIG.disableClicking && !hasSkyBlockItemID ? "!" : "";

        if (nameWithColor.contains("[") || nameWithColor.contains("]") || nameWithColor.contains("{") || nameWithColor.contains("}")) {
            lore.add(0, RegexTextReplacements.LINE_SEPARATORS.replace(nameWithColor));
            showRarity = false;
            nameWithColor = "e"; // TODO: 7/1/2023 find better alternative fake name
            nameWithoutColor = "e";
        }

        this.recipeMode = referenceMode;

        this.textureLink = skyBlockItem ? pet ? petId : skyBlockId.toLowerCase() : minecraftId;
        boolean potion = skyBlockId.equalsIgnoreCase("potion");
        String baseReferenceId = skyBlockItem || (hasSkyBlockItemID && !potion) ? pet ? petId : skyBlockId.toLowerCase() : potion ? nameWithoutColor : minecraftId;
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
        minecraftId = stack.getItem().getName(stack).getString().toLowerCase().replace(" ", "_");

        skyBlockItem = stack.getItem() == Items.PLAYER_HEAD;

        if (guiName != null && skyBlockId.equals("") && (guiName.contains("Oringo") || (guiName.contains("Pets") && skyBlockItem && !lore.isEmpty() && lore.get(lore.size() - 1).contains("Click to summon")))) {
            skyBlockId = "pet";
        }

        pet = skyBlockId.equalsIgnoreCase("pet") || (skyBlockId.equals("") && !lore.isEmpty() && lore.get(0).contains("Pet") && skyBlockItem);
        hasSkyBlockItemID = !skyBlockId.equals("");

        if (pet) {
            // update if it didn't include the id but is a pet
            skyBlockId = "pet";
            hasSkyBlockItemID = true;
        } else if (skyBlockItem && skyBlockId.equals("")) {
            skyBlockId = "head";
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

    private void fixIDs(ItemStack stack, NbtCompound display, NbtCompound extraAttributes) {
        boolean hasEnchantments = stack.hasEnchantments() || skyBlockId.equalsIgnoreCase("potion");

        if (skyBlockItem) {
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

        if (skyBlockItem && skyBlockId.contains("backpack") && CONFIG.backpackColors && extraAttributes.contains("backpack_color", 8)) {
            String backpackColor = extraAttributes.getString("backpack_color").toLowerCase();
            if (!backpackColor.equals("") && !backpackColor.equalsIgnoreCase("default")) {
                skyBlockId = backpackColor + "_" + skyBlockId;
            }
        }

        if (!minecraftId.equalsIgnoreCase("head") && hasEnchantments && !minecraftId.startsWith("enchanted_")) {
            minecraftId = "enchanted_" + minecraftId;
        }
    }

    private void updateNameAndPetInfo(boolean petNameChanged) {
        if (!skyBlockItem || !pet) {
            return;
        }

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

    private void updateStackSizes(boolean referenceMode, String guiName, NbtCompound extraAttributes) {
        showRarity = rarity != null;
        if (referenceMode && CONFIG.recipeMode || !showRarity || !CONFIG.guaranteedStackSizeToggled || !hasSkyBlockItemID) {
            return;
        }

        int maxStackSize = stackSize;

        if (!pet && extraAttributes.contains("uuid", NbtElement.STRING_TYPE)) {
            maxStackSize = 1;
        }

        int setStackSize = CONFIG.setStackSize;
        if (!CONFIG.bypassStackSizeLimit && setStackSize > maxStackSize) {
            setStackSize = maxStackSize;
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

    public String convertToReference() {
        if (minecraftId.equals("") || minecraftId.equals("air")) return "";

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
        if (minecraftId.equals("") || minecraftId.equals("air")) return "";

        return initialChar + typeText + "," + (!showRarity ? "" : rarity.toString()) + "," + textureLink + (emptyTitle ? "" : (minecraftId.equals(nameWithoutColor) ? "" : ":" + (showRarity ? nameWithoutColor : nameWithColor))) + (lore.isEmpty() && stackSize == 1 ? "" : ("," + NumberFormat.getInstance().format(stackSize) + (emptyTitle || lore.isEmpty() ? "" : "," + loreAsString)));
    }
}
