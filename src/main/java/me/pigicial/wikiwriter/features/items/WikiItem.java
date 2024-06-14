package me.pigicial.wikiwriter.features.items;

import lombok.Getter;
import lombok.Setter;
import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.config.WikiWriterConfig;
import me.pigicial.wikiwriter.features.items.types.TextureAndReferenceData;
import me.pigicial.wikiwriter.utils.Action;
import me.pigicial.wikiwriter.utils.TextUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiItem {

    private static final String DONT_SHOW_TOOLTIP = "?";
    private static final String NOT_CLICKABLE = "!";

    private static final Pattern AUCTION_ITEM_COUNT_PATTERN = Pattern.compile("§7-?(\\d+)x ");
    private static final Pattern SHOP_NAME_ITEM_COUNT = Pattern.compile("§8x(\\d\\d?)");

    private final WikiWriterConfig config = WikiWriter.getInstance().getConfig();

    @Getter
    private final ItemStack itemStack;
    private final Rarity rarity;
    @Getter
    private final List<String> lore;

    @Nullable
    private final TextureAndReferenceData textureAndReferenceData;

    private final boolean shopItem;
    private final boolean hasCustomSkullTexture;
    private final boolean emptyTitle;

    @Setter
    private String minecraftId;
    @Getter
    private String skyBlockId;

    private final int originalStackSize;
    private int currentStackSize;

    @Getter
    private String nameWithColor;
    private final String nameWithoutColor;

    @Getter
    private final TextReplacementPipeline textPipeline;
    private final String loreAsString;
    private final String extraLoreBelowRarityAsString;

    private boolean showRarity = true;

    public WikiItem(@NotNull ItemStack itemStack, Action action) {
        this(itemStack, action, null);
    }

    public WikiItem(@NotNull ItemStack itemStack, Action action, @Nullable Consumer<TextReplacementPipeline> textModifications) {
        this.itemStack = itemStack;

        NbtCompound nbt = itemStack.getNbt();
        // I don't think this can be null but there's null checks in ItemStack so just in case
        if (nbt == null) {
            nbt = new NbtCompound();
        }

        originalStackSize = itemStack.getCount();
        currentStackSize = originalStackSize;
        minecraftId = itemStack.getItem().getName(itemStack).getString().toLowerCase().replace(" ", "_").replace("'", "");
        hasCustomSkullTexture = itemStack.getItem() == Items.PLAYER_HEAD;

        NbtCompound extraAttributes = nbt.getCompound("ExtraAttributes");
        skyBlockId = extraAttributes.getString("id").toLowerCase();

        NbtCompound display = nbt.getCompound("display");
        lore = TextUtils.parseJsonLore(display);

        nameWithColor = TextUtils.convertJsonTextToLegacy(Text.Serialization.toJsonString(itemStack.getName()));
        rarity = Rarity.getRarityFromName(nameWithColor);

        updateNameAndStackSize(); // fix brackets in name plus replace amounts if necessary

        // Removes certain lines of lore based on config settings, then stores them
        LoreFilters.RemovedLore removeData = LoreFilters.checkAndFilter(lore, action);
        shopItem = removeData.detectedShopLore();

        textPipeline = new TextReplacementPipeline();
        if (textModifications != null) {
            textModifications.accept(textPipeline);
        }

        loreAsString = textPipeline.replaceTextListAndConvertToString(lore);
        extraLoreBelowRarityAsString = textPipeline.replaceTextListAndConvertToString(removeData.loreBelowRarityToPossibleAdd());

        showRarity = showRarity && rarity != null;
        textureAndReferenceData = TextureAndReferenceData.getFromExtraAttributes(this, itemStack, extraAttributes);

        String nameWithReplacements = nameWithColor.replace(":", "<nowiki>:</nowiki>");
        nameWithColor = TextReplacementPipeline.replaceText(nameWithReplacements);
        nameWithoutColor = Objects.requireNonNull(Formatting.strip(nameWithReplacements)).replace('§', '&');

        if (showRarity && StyleReplacer.hasMultipleStyles(nameWithColor)) {
            showRarity = false;
        }

        // Fixes various item ID quirks (and handles colors and whatnot)
        fixIDs(itemStack, display, extraAttributes);

        emptyTitle = nameWithoutColor.replace(" ", "").isEmpty();
    }

    private void updateNameAndStackSize() {
        if (nameWithColor.contains("[") || nameWithColor.contains("]") || nameWithColor.contains("{") || nameWithColor.contains("}")) {
            // non-clickable lore supports brackets
            if (!generateModifier().equals(NOT_CLICKABLE)) {
                lore.add(0, nameWithColor);
                showRarity = false;
                nameWithColor = "INSERT_LINK_HERE";
                return;
            }
        }

        boolean setToOne = config.setAmountsToOne;
        if (setToOne) {
            currentStackSize = 1;

            Matcher matcher = AUCTION_ITEM_COUNT_PATTERN.matcher(nameWithColor);
            nameWithColor = matcher.replaceAll("");

            matcher = SHOP_NAME_ITEM_COUNT.matcher(nameWithColor);
            nameWithColor = matcher.replaceAll("");
        }
    }

    private void fixIDs(ItemStack stack, NbtCompound display, NbtCompound extraAttributes) {
        boolean hasEnchantments = stack.hasEnchantments() || stack.hasGlint() || skyBlockId.equalsIgnoreCase("potion");

        if (hasCustomSkullTexture) {
            minecraftId = "player_head";
        }

        long color = display.getLong("color");
        if (minecraftId.startsWith("leather") && color != 0) {
            LeatherArmorColor armorColor = LeatherArmorColor.findColor(color);
            if (armorColor != LeatherArmorColor.DEFAULT) {
                minecraftId = minecraftId + "_" + armorColor.name().toLowerCase();
            }
        }

        if (hasCustomSkullTexture && skyBlockId.contains("backpack") && extraAttributes.contains("backpack_color", 8)) {
            String backpackColor = extraAttributes.getString("backpack_color").toLowerCase();
            if (!backpackColor.isEmpty() && !backpackColor.equalsIgnoreCase("default")) {
                skyBlockId = backpackColor + "_" + skyBlockId;
            }
        }

        if (hasEnchantments && !minecraftId.equalsIgnoreCase("player_head")) {
            if (minecraftId.equals("book")) {
                minecraftId = "enchanted_enchanted_book"; // the base book item is called this when its glowing
            } else if (!minecraftId.equals("enchanted_book")) { // don't turn enchanted books into enchanted_enchanted_book
                minecraftId = "enchanted_" + minecraftId;
            }
        }

        if (hasCustomSkullTexture && skyBlockId.isEmpty() && nameWithoutColor.endsWith(" Minion")) {
            // for the crafted minions menu basically
            skyBlockId = nameWithoutColor.substring(0, nameWithoutColor.length() - 7) + "_GENERATOR_1";
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
                WikiWriterConfig.ReferenceModeScenario mode = config.menuReferenceModeScenario;

                boolean alwaysReference = mode == WikiWriterConfig.ReferenceModeScenario.ALWAYS;
                boolean onlyOnShopItemsAndIsShopItem = mode == WikiWriterConfig.ReferenceModeScenario.WHEN_COPYING_SHOP_ITEMS
                                                       && shopItem;
                boolean hasAmountInTitle = currentStackSize != 1;

                if (skyBlockId.isEmpty()) {
                    yield convertToWikiItem();
                }

                boolean referenceMode = alwaysReference || (onlyOnShopItemsAndIsShopItem && !hasAmountInTitle);
                yield referenceMode ? convertToReferenceWithExtraText() : convertToWikiItem();
            }
        };
    }

    private String convertToReference() {
        if (minecraftId.isEmpty() || minecraftId.equals("air")) {
            return "";
        }

        if (textureAndReferenceData != null) {
            return textureAndReferenceData.getLoreTemplateReference();
        }

        if (!hasCustomSkullTexture && skyBlockId.isEmpty()) {
            return "{{Item_" + minecraftId.toLowerCase() + "}}";
        } else {
            return "{{Item/" + skyBlockId.toUpperCase() + "|real_lore}}";
        }
    }

    private String convertToReferenceWithExtraText() {
        String reference = convertToReference();
        String extraLore = extraLoreBelowRarityAsString.isEmpty() ? "" : TextUtils.unescapeText("\n") + extraLoreBelowRarityAsString;
        String alternateAmountString = currentStackSize == 1 ? "" : "," + currentStackSize;

        return reference + extraLore + alternateAmountString;
    }

    private String convertToWikiItem() {
        if (minecraftId.isEmpty() || minecraftId.equals("air")) {
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

        String amountString = lore.isEmpty() && extraLoreBelowRarityAsString.isEmpty() && currentStackSize == 1 ? "" : "," + currentStackSize;
        String loreString = emptyTitle || loreAsString.isEmpty() ? "" : "," + loreAsString;
        String potentialExtraLore = emptyTitle || extraLoreBelowRarityAsString.isEmpty() ? "" : TextUtils.unescapeText("\n") + extraLoreBelowRarityAsString;

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
        if (textureAndReferenceData != null) {
            return textureAndReferenceData.getTextureType();
        }

        return hasCustomSkullTexture ? "sb" : "mc";
    }

    private String generateTextureLink() {
        if (textureAndReferenceData != null) {
            return textureAndReferenceData.getTextureLink();
        } else if (!hasCustomSkullTexture) {
            return minecraftId;
        } else {
            return skyBlockId.isEmpty() ? "unknown_item" : skyBlockId.toLowerCase();
        }
    }

    public String getBaseTextureLink() {
        return minecraftId;
    }

}
