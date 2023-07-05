package me.pigicial.wikiwriter;

import me.pigicial.wikiwriter.config.Config;
import me.pigicial.wikiwriter.features.ColorReplacementFeature;
import me.pigicial.wikiwriter.features.LeatherColorFinderFeature;
import me.pigicial.wikiwriter.features.LoreRemovalFeature;
import me.pigicial.wikiwriter.features.RegexTextReplacements;
import me.pigicial.wikiwriter.utils.Action;
import me.pigicial.wikiwriter.utils.PetInfo;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiItem {

    private static final String DONT_SHOW_TOOLTIP = "?";
    private static final String NOT_CLICKABLE = "!";

    private static final Pattern AUCTION_ITEM_COUNT_PATTERN = Pattern.compile("§7-?(\\d+)x ");
    private static final Pattern SHOP_NAME_ITEM_COUNT = Pattern.compile("§8x(\\d\\d?)");

    private final Config config = WikiWriter.getInstance().getConfig();

    private final Rarity rarity;
    private final List<String> lore;

    @Nullable
    private final PetInfo petInfo;

    private final boolean shopItem;
    private final boolean hasCustomSkullTexture;
    private final boolean emptyTitle;

    private String minecraftId;
    private String skyBlockId;

    private final int originalStackSize;
    private int currentStackSize;

    private String nameWithColor;
    private final String nameWithoutColor;

    private final String loreAsString;
    private final String extraLoreBelowRarity;

    private boolean showRarity = true;

    public WikiItem(@NotNull ItemStack stack, Action action, boolean referenceMode) {
        NbtCompound nbt = stack.getNbt();
        // I don't think this can be null but there's null checks in ItemStack so just in case
        if (nbt == null) {
            nbt = new NbtCompound();
        }

        originalStackSize = stack.getCount();
        currentStackSize = originalStackSize;
        minecraftId = stack.getItem().getName(stack).getString().toLowerCase().replace(" ", "_").replace("'", "");
        hasCustomSkullTexture = stack.getItem() == Items.PLAYER_HEAD;

        NbtCompound extraAttributes = nbt.getCompound("ExtraAttributes");
        skyBlockId = extraAttributes.getString("id").toLowerCase().replace(":", ".");
        // 1.7 ids use dots instead of colon symbols with tor's automation system

        NbtCompound display = nbt.getCompound("display");
        NbtList loreTag = display.getList("Lore", NbtElement.STRING_TYPE);

        lore = new ArrayList<>();
        for (int i = 0; i < loreTag.size(); i++) {
            String jsonLine = loreTag.getString(i);
            String legacyLine = TextUtils.convertJsonTextToLegacy(jsonLine);

            lore.add(legacyLine);
        }

        nameWithColor = TextUtils.convertJsonTextToLegacy(Text.Serializer.toJson(stack.getName()));

        // fix brackets in name plus replace amounts if necessary
        updateName();

        // Removes certain lines of lore based on config settings, then stores them
        LoreRemovalFeature.RemovedLore removeData = LoreRemovalFeature.checkAndFilter(lore, action);
        loreAsString = TextUtils.convertListToString(lore);
        extraLoreBelowRarity = TextUtils.convertListToString(removeData.loreBelowRarityToPossibleAdd());
        shopItem = removeData.hasShopLore();

        // Figures out the rarity of the item based on the item name or lore
        rarity = Rarity.parseRarity(lore, nameWithColor);
        showRarity = showRarity && rarity != null;

        petInfo = PetInfo.getPetInfo(extraAttributes);

        // Updates the stack sizes (and name if so) of the item based on various factors
        updateStackSizes(referenceMode, extraAttributes);

        String nameWithReplacements = RegexTextReplacements.replaceEverything(nameWithColor, true);
        nameWithColor = ColorReplacementFeature.replace(nameWithReplacements);
        nameWithoutColor = Objects.requireNonNull(Formatting.strip(nameWithReplacements)).replace('§', '&');

        if (showRarity && ColorReplacementFeature.hasMultipleStyles(nameWithColor)) {
            showRarity = false;
        }

        // Fixes various item ID quirks (and handles colors and whatnot)
        fixIDs(stack, display, extraAttributes);

        emptyTitle = nameWithoutColor.replace(" ", "").isEmpty();
    }

    private void updateName() {
        if (nameWithColor.contains("[") || nameWithColor.contains("]") || nameWithColor.contains("{") || nameWithColor.contains("}")) {
            lore.add(0, RegexTextReplacements.LINE_SEPARATORS.replace(nameWithColor));
            showRarity = false;
            nameWithColor = "INSERT_LINK_HERE";
        }

        // Removes the item amount from the name
        if (config.removeItemAmountsFromItemNames) {
            Matcher matcher = SHOP_NAME_ITEM_COUNT.matcher(nameWithColor);
            while (matcher.find()) {
                nameWithColor = nameWithColor.replace(matcher.group(), "");
            }
        }
    }

    private void fixIDs(ItemStack stack, NbtCompound display, NbtCompound extraAttributes) {
        boolean hasEnchantments = stack.hasEnchantments() || stack.hasGlint() || skyBlockId.equalsIgnoreCase("potion");

        if (hasCustomSkullTexture) {
            minecraftId = "player_head";
        }

        long color = display.getLong("color");
        if (minecraftId.startsWith("leather") && color != 0) {
            LeatherColorFinderFeature colorFinderFeature = LeatherColorFinderFeature.findColor((int) color);
            if (colorFinderFeature != LeatherColorFinderFeature.DEFAULT) {
                minecraftId = minecraftId + "_" + colorFinderFeature.name().toLowerCase();
            }
        }

        if (hasCustomSkullTexture && skyBlockId.contains("backpack") && config.backpackColors && extraAttributes.contains("backpack_color", 8)) {
            String backpackColor = extraAttributes.getString("backpack_color").toLowerCase();
            if (!backpackColor.equals("") && !backpackColor.equalsIgnoreCase("default")) {
                skyBlockId = backpackColor + "_" + skyBlockId;
            }
        }

        if (hasEnchantments && !minecraftId.equalsIgnoreCase("player_head")) {
            if (minecraftId.equals("book")) {
                minecraftId = "enchanted_enchanted_book"; // the base book item is called this when its glowing
            } else {
                minecraftId = "enchanted_" + minecraftId;
            }
        }

        if (hasCustomSkullTexture && skyBlockId.isEmpty() && nameWithoutColor.endsWith(" Minion")) {
            // for the crafted minions menu basically
            skyBlockId = nameWithoutColor.substring(0, nameWithoutColor.length() - 7) + "_GENERATOR_1";
        }
    }

    private void updateStackSizes(boolean referenceMode, NbtCompound extraAttributes) {
        if (referenceMode && config.recipeMode || !showRarity || !config.guaranteedStackSizeToggled || skyBlockId.isEmpty()) {
            return;
        }

        int maxStackSize = currentStackSize;

        if (extraAttributes.contains("uuid", NbtElement.STRING_TYPE)) {
            maxStackSize = 1;
        }

        int setStackSize = config.setStackSize;
        if (!config.bypassStackSizeLimit && setStackSize > maxStackSize) {
            setStackSize = maxStackSize;
        }

        if (setStackSize != currentStackSize) {
            currentStackSize = setStackSize;

            Matcher matcher = AUCTION_ITEM_COUNT_PATTERN.matcher(nameWithColor);
            nameWithColor = matcher.replaceAll(result -> {
                String numberText = result.group(1);
                return result.group().replace(numberText, String.valueOf(currentStackSize));
            });
        }
    }

    public String generateText(Action action) {
        return switch (action) {
            case COPYING_STANDALONE_ITEM -> convertToWikiItem();
            case COPYING_RECIPE_INVENTORY -> {
                String amountString = originalStackSize == 1 ? "" : "," + originalStackSize;
                yield convertToReference() + amountString;
            }
            case COPYING_INVENTORY, COPYING_SHOP_INVENTORY -> {
                boolean referenceMode = config.menuReferenceModeScenario == Config.MENU_REFERENCE_MODE_ALWAYS
                        || (config.menuReferenceModeScenario == Config.MENU_REFERENCE_MODE_COPYING_ITEMS && shopItem);

                yield referenceMode ? convertToReferenceWithExtraText() : convertToWikiItem();
            }
        };
    }

    private String convertToReference() {
        if (minecraftId.equals("") || minecraftId.equals("air")) {
            return "";
        }

        if (petInfo != null) {
            boolean mysteryPet = petInfo.mysteryPet();
            return "{{Item_" + (mysteryPet ? "pet_craft_" : "pet_") + petInfo.type().toLowerCase() + (!mysteryPet ? "_" + rarity.toString() : "") + "}}";
        }

        boolean potion = skyBlockId.equalsIgnoreCase("potion");
        String referenceId;
        if (!hasCustomSkullTexture && (skyBlockId.isEmpty() || potion)) {
            referenceId = potion ? nameWithoutColor.replace("_", " ").toLowerCase() : minecraftId.toLowerCase();
        } else {
            referenceId = skyBlockId.toLowerCase();
        }

        return "{{Item_" + referenceId.replace(" ", "_").toLowerCase() + "}}";
    }

    private String convertToReferenceWithExtraText() {
        String reference = convertToReference();
        String extraLore = extraLoreBelowRarity.isEmpty() ? "" : TextUtils.unescapeText("\n") + extraLoreBelowRarity;
        String alternateAmountString = currentStackSize == 1 ? "" : "," + currentStackSize;

        return reference + extraLore + alternateAmountString;
    }

    private String convertToWikiItem() {
        if (minecraftId.equals("") || minecraftId.equals("air")) {
            return "";
        }

        String modifier = this.generateModifier();
        String textureType = this.generateTextureType();
        String rarityString = showRarity ? rarity.toString() : "";
        String textureLink = this.generateTextureLink();

        String name = "";
        if (!emptyTitle && !minecraftId.equals(nameWithoutColor)) {
            name = ":" + (showRarity ? nameWithoutColor : nameWithColor);
        }

        String amountString = lore.isEmpty() && extraLoreBelowRarity.isEmpty() && currentStackSize == 1 ? "" : "," + currentStackSize;
        String loreString = emptyTitle || lore.isEmpty() ? "" : "," + loreAsString;
        String potentialExtraLore = emptyTitle || extraLoreBelowRarity.isEmpty() ? "" : "<nowiki>,</nowiki>" + extraLoreBelowRarity;

        return modifier + textureType + "," + rarityString + "," + textureLink + name + amountString + loreString + potentialExtraLore;
    }

    private String generateModifier() {
        if (emptyTitle && lore.isEmpty()) {
            return DONT_SHOW_TOOLTIP;
        } else if (skyBlockId.isEmpty() && config.disableClicking) {
            return NOT_CLICKABLE;
        } else {
            return "";
        }
    }

    private String generateTextureType() {
        if (petInfo != null) {
            return "sbpet";
        } else if (hasCustomSkullTexture) {
            return "sb";
        } else {
            return "mc";
        }
    }

    private String generateTextureLink() {
        if (!hasCustomSkullTexture) {
            return minecraftId;
        } else if (petInfo != null) {
            return petInfo.type().toLowerCase();
        } else {
            return skyBlockId.isEmpty() ? "unknown_item" : skyBlockId.toLowerCase();
        }
    }
}
