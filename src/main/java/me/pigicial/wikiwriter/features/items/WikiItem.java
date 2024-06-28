package me.pigicial.wikiwriter.features.items;

import lombok.Getter;
import lombok.Setter;
import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.config.WikiWriterConfig;
import me.pigicial.wikiwriter.features.items.types.TextureAndReferenceData;
import me.pigicial.wikiwriter.utils.Action;
import me.pigicial.wikiwriter.utils.StyleConversions;
import me.pigicial.wikiwriter.utils.TextUtils;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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

    @Nullable @Setter
    private TextureAndReferenceData textureAndReferenceData;

    private final boolean shopItem;
    private final boolean hasCustomSkullTexture;

    @Setter
    private String minecraftId;
    @Getter @Setter
    private String skyBlockId;

    private final int originalStackSize;
    private int currentStackSize;

    @Getter @Setter
    private String name;

    @Getter
    private final TextReplacementPipeline textPipeline;

    private final LoreFilters.RemovedLore removeData;

    private boolean showRarity = true;
    @Setter
    private boolean forcedNotClickable = false;

    public WikiItem(@NotNull ItemStack itemStack, Action action) {
        this(itemStack, action, null);
    }

    public WikiItem(@NotNull ItemStack itemStack, Action action, @Nullable Consumer<TextReplacementPipeline> textModifications) {
        this.itemStack = itemStack;

        ComponentMap components = itemStack.getComponents();
        // I don't think this can be null but there's null checks in ItemStack so just in case
        if (components == null) {
            components = ComponentMap.EMPTY;
        }

        originalStackSize = itemStack.getCount();
        currentStackSize = originalStackSize;
        minecraftId = itemStack.getItem().getName(itemStack).getString().toLowerCase().replace(" ", "_").replace("'", "");
        hasCustomSkullTexture = itemStack.getItem() == Items.PLAYER_HEAD;

        NbtComponent extraAttributesAsComponent = components.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound extraAttributes = extraAttributesAsComponent == null
                ? new NbtCompound()
                : extraAttributesAsComponent.copyNbt();

        skyBlockId = extraAttributes.getString("id").toLowerCase();

        lore = TextUtils.parseJsonLore(components.get(DataComponentTypes.LORE));

        Text nameAsText = components.get(DataComponentTypes.CUSTOM_NAME);
        name = nameAsText == null ? "" : TextUtils.convertToLegacyText(nameAsText);

        rarity = Rarity.getRarityFromName(name);

        updateNameAndStackSize(); // fix brackets in name plus replace amounts if necessary

        // Removes certain lines of lore based on config settings, then stores them
        removeData = LoreFilters.checkAndFilter(lore, action);
        shopItem = removeData.detectedShopLore();

        textPipeline = new TextReplacementPipeline();
        if (textModifications != null) {
            textModifications.accept(textPipeline);
        }

        showRarity = showRarity && rarity != null;
        textureAndReferenceData = TextureAndReferenceData.getFromExtraAttributes(this, itemStack, extraAttributes);

        String nameWithReplacements = name.replace(":", "<nowiki>:</nowiki>");
        name = textPipeline.replaceText(nameWithReplacements);

        if (showRarity && StyleConversions.hasMultipleStyles(name)) {
            showRarity = false;
        }

        // Fixes various item ID quirks (and handles colors and whatnot)
        fixIDs(itemStack, components, extraAttributes);
    }

    private void updateNameAndStackSize() {
        if (name.contains("[") || name.contains("]") || name.contains("{") || name.contains("}")) {
            // non-clickable lore supports brackets
            if (!generateModifier().equals(NOT_CLICKABLE)) {
                lore.add(0, name);
                showRarity = false;
                name = "INSERT_LINK_HERE";
                return;
            }
        }

        boolean setToOne = config.setAmountsToOne;
        if (setToOne) {
            currentStackSize = 1;

            Matcher matcher = AUCTION_ITEM_COUNT_PATTERN.matcher(name);
            name = matcher.replaceAll("");

            matcher = SHOP_NAME_ITEM_COUNT.matcher(name);
            name = matcher.replaceAll("");
        }
    }

    private void fixIDs(ItemStack stack, ComponentMap components, NbtCompound extraAttributes) {
        boolean hasEnchantments = stack.hasEnchantments() || stack.hasGlint() || skyBlockId.equalsIgnoreCase("potion");

        if (hasCustomSkullTexture) {
            minecraftId = "player_head";
        }

        DyedColorComponent colorComponent = components.get(DataComponentTypes.DYED_COLOR);
        long color = colorComponent == null ? 0 : colorComponent.rgb();
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

        String nameWithoutColor = StyleConversions.stripColor(name);
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
        String extraLore = this.getExtraLoreBelowRarity();
        if (!extraLore.isEmpty()) {
            extraLore = TextUtils.unescapeText("\n") + extraLore;
        }

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

        boolean emptyTitle = isEmptyTitle();
        String nameWithoutColor = StyleConversions.stripColor(this.name);
        String usedName = "";
        if (!emptyTitle && !minecraftId.equals(nameWithoutColor)) {
            usedName = ":" + (showRarity ? nameWithoutColor : this.name);
        }

        String loreAsString = textPipeline.replaceTextListAndConvertToString(lore);
        String extraLoreBelowRarityAsString = this.getExtraLoreBelowRarity();

        String amountString = lore.isEmpty() && extraLoreBelowRarityAsString.isEmpty() && currentStackSize == 1 ? "" : "," + currentStackSize;
        String loreString = emptyTitle || loreAsString.isEmpty() ? "" : "," + loreAsString;
        String potentialExtraLore = emptyTitle || extraLoreBelowRarityAsString.isEmpty() ? "" : TextUtils.unescapeText("\n") + extraLoreBelowRarityAsString;

        return modifier + textureType + "," + rarityString + "," + textureLink + usedName + amountString + loreString + potentialExtraLore;
    }

    public boolean isEmptyTitle() {
        return StyleConversions.stripColor(name).replace(" ", "").isEmpty();
    }

    public String getExtraLoreBelowRarity() {
        return textPipeline.replaceTextListAndConvertToString(removeData.loreBelowRarityToPossibleAdd());
    }

    private String generateModifier() {
        if (isEmptyTitle() && lore.isEmpty()) {
            return DONT_SHOW_TOOLTIP;
        } else if ((hasCustomSkullTexture && skyBlockId.isEmpty()) && config.disableClicking) {
            return NOT_CLICKABLE;
        } else if (forcedNotClickable || config.disableClicking) {
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
