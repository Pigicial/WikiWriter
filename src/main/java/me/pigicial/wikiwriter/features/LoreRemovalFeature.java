package me.pigicial.wikiwriter.features;

import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.config.Config;
import me.pigicial.wikiwriter.utils.Action;
import me.pigicial.wikiwriter.utils.Rarity;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public enum LoreRemovalFeature {
    CLICK_1(config -> config.removeClickNotices, "Right-click to view recipes!", ""),
    CLICK_2(config -> config.removeClickNotices, "Right click to view recipes", ""),
    CLICK_3(config -> config.removeClickNotices, "Right-click to view recipes!"),
    CLICK_4(config -> config.removeClickNotices, "Right click to view recipes"),
    VIEW_RECIPE(config -> config.removeClickNotices, "", "Click to view recipe!"),
    VIEW_RECIPES(config -> config.removeClickNotices, "", "Click to view recipes!"),
    CLICK_7(config -> config.removeClickNotices, "Right click on your pet to", "give it this item!", ""),
    CLICK_8(config -> config.removeClickNotices, "Right click on your pet to", "feed it this candy!", ""),
    CLICK_9(config -> config.removeClickNotices, "Right-click to add this pet to", "your pet menu!", ""),
    CLICK_10(config -> config.removeClickNotices, "Right-click to use your class", "ability", ""),
    CLICK_11(config -> config.removeClickNotices, "Right-click to use your class ability", ""),
    CLICK_12(config -> config.removeClickNotices, "Right-click to use Class Ability", ""),
    CLICK_13(config -> config.removeClickNotices, "Right click to use Class", "Ability", ""),
    CLICK_14(config -> config.removeClickNotices, "Right-click to consume!", ""),
    CLICK_15(config -> config.removeClickNotices, "Right click on your pet to", "apply this skin!", ""),
    CLICK_16(config -> config.removeClickNotices, "Right click on your griffin to", "upgrade it!", ""),
    CLICK_17(config -> config.removeClickNotices, "", "Click to toggle"),
    CLICK_18(config -> config.removeClickNotices, "", "Right-Click a block to use!"),
    CLICK_19(config -> config.removeClickNotices, "", "Right-click to open!"),
    CLICK_20(config -> config.removeClickNotices, "", "Right-click on Kat to use"),
    CLICK_21(config -> config.removeClickNotices, "", "Right-Click a block to use!"),
    CLICK_22(config -> config.removeClickNotices, "", "Right-click to configure!"),
    CLICK_23(config -> config.removeClickNotices, "", "Click to view upgrades"),
    CLICK_24(config -> config.removeClickNotices, "", "Click to summon"),

    SHOP_1(config -> config.removeShopNPCPriceText, "", "Cost:"),
    SHOP_2(config -> config.removeShopNPCPriceText, "", "Cost", "{any-not-empty}"),
    SHOP_3(config -> config.removeShopNPCPriceText, "", "Cost", "{any-not-empty}", "{any-not-empty}"),
    SHOP_4(config -> config.removeShopNPCPriceText, "", "Cost", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}"),
    SHOP_5(config -> config.removeShopNPCPriceText, "", "Cost", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}"),
    SHOP_6(config -> config.removeShopNPCPriceText, "", "Cost", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}"),
    SHOP_7(config -> config.removeShopNPCPriceText, "", "Cost", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}"),
    SHOP_8(config -> config.removeShopNPCPriceText, "", "Cost", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}"),
    SHOP_9(config -> config.removeShopNPCPriceText, "", "Cost", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}"),

    SHOP_STOCK_1(config -> config.removeShopNPCStockText, "", "Stock", "remaining"),

    BOTTOM_SHOP_1(config -> config.removeShopNPCTradeText, "", "Click to purchase"),
    BOTTOM_SHOP_2(config -> config.removeShopNPCTradeText, "", "Click to trade"),
    BOTTOM_SHOP_3(config -> config.removeShopNPCTradeText, "", "You don't have enough coins"),
    BOTTOM_SHOP_4(config -> config.removeShopNPCTradeText, "trading options"),
    BOTTOM_SHOP_5(config -> config.removeShopNPCTradeText, "trading", "options"), // originally was in 1 line (above), now 2 it seems
    BOTTOM_SHOP_6(config -> config.removeShopNPCTradeText, "Right-click to fill quiver"),
    BOTTOM_SHOP_7(config -> config.removeShopNPCTradeText, "", "Click to buy into quiver"),
    BOTTOM_SHOP_8(config -> config.removeShopNPCTradeText, "", "Not unlocked"),
    BOTTOM_SHOP_9(config -> config.removeShopNPCTradeText, "", "You don't have the required items"),
    BOTTOM_SHOP_10(config -> config.removeShopNPCTradeText, "", "Click to craft"),
    BOTTOM_SHOP_11(config -> config.removeShopNPCTradeText, "", "Can't afford this"),

    CRAFTING_TABLE_1(config -> config.removeCraftingTableData, "--------", "This is the item you are", "crafting"),
    QUICK_CRAFTING_1(config -> config.removeCraftingTableData, "", "--------", "Ingredients", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "", "Click to craft"),
    QUICK_CRAFTING_2(config -> config.removeCraftingTableData, "", "--------", "Ingredients", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "Click to craft"),
    QUICK_CRAFTING_3(config -> config.removeCraftingTableData, "", "--------", "Ingredients", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "", "Click to craft"),
    QUICK_CRAFTING_4(config -> config.removeCraftingTableData, "", "--------", "Ingredients", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "", "Click to craft"),
    QUICK_CRAFTING_5(config -> config.removeCraftingTableData, "", "--------", "Ingredients", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "", "Click to craft"),
    QUICK_CRAFTING_6(config -> config.removeCraftingTableData, "", "--------", "Ingredients", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "", "Click to craft"),
    QUICK_CRAFTING_7(config -> config.removeCraftingTableData, "", "--------", "Ingredients", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "", "Click to craft"),
    QUICK_CRAFTING_8(config -> config.removeCraftingTableData, "", "--------", "Ingredients", "{any-not-empty}", "{any-not-empty}", "", "Click to craft"),
    QUICK_CRAFTING_9(config -> config.removeCraftingTableData, "", "--------", "Ingredients", "{any-not-empty}", "", "Click to craft"),

    PICKAXE_ABILITY_1(config -> config.removePickaxeAbilities, "Ability: Mining Speed Boost", "Grants", "Speed", "Cooldown", ""),
    PICKAXE_ABILITY_2(config -> config.removePickaxeAbilities, "Ability: Maniac Miner", "Spends", "{any-not-empty}", "every", "{any-not-empty}", "Cooldown", ""),
    PICKAXE_ABILITY_3(config -> config.removePickaxeAbilities, "Ability: Pickobulus", "Throw your pickaxe", "explosion on impact", "ores within a", "radius", "Cooldown", ""),
    PICKAXE_ABILITY_4(config -> config.removePickaxeAbilities, "Ability: Vein Seeker", "Points in the direction", "nearest vein", "Mining Spread", "Cooldown", ""),

    PET_ITEMS_1(config -> config.removePetItems, "Held Item:", "{any-not-empty}", ""),
    PET_ITEMS_2(config -> config.removePetItems, "Held Item:", "{any-not-empty}", "{any-not-empty}", ""),
    PET_ITEMS_3(config -> config.removePetItems, "Held Item:", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", ""),
    PET_ITEMS_4(config -> config.removePetItems, "Held Item:", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", ""),
    PET_ITEMS_5(config -> config.removePetItems, "Held Item:", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "{any-not-empty}", "");

    public static final LoreRemovalFeature[] SHOP_FILTERS = Arrays.stream(LoreRemovalFeature.values())
            .filter(feature -> feature.name().contains("SHOP"))
            .sorted(Comparator.comparing(Enum::ordinal))
            .toArray(LoreRemovalFeature[]::new);

    private final Predicate<Config> settingsFilter;
    private final List<String> textToFilter;

    LoreRemovalFeature(Predicate<Config> settingsFilter, String... textToFilter) {
        this.settingsFilter = settingsFilter;
        this.textToFilter = Arrays.asList(textToFilter);
    }

    public static RemovedLore checkAndFilter(List<String> lore, Action action) {
        // This doesn't necessarily mean removed features, just ones that were in the lore. They are then
        // used for shop item and recipe detection systems
        Set<LoreRemovalFeature> detectedFeatures = new HashSet<>();

        List<String> loreAfterRarityToPossibleAdd = new ArrayList<>();
        OptionalInt rarityIndex = Rarity.getRarityIndexFromLore(lore);

        for (LoreRemovalFeature removalType : LoreRemovalFeature.values()) {
            RemovedSectionData sectionData = removalType.remove(lore);
            if (sectionData == null) {
                continue;
            }

            detectedFeatures.add(removalType);
            if (!sectionData.actuallyRemoved) {
                continue;
            }

            int startIndex = sectionData.startIndex;
            int endIndex = sectionData.endIndex;

            if (rarityIndex.isPresent()) {
                int index = rarityIndex.getAsInt();
                if (index >= startIndex && index < endIndex) {
                    // rarity was removed
                    rarityIndex = OptionalInt.empty();
                } else if (index >= endIndex) {
                    // rarity was after removed text, therefore its index changed
                    rarityIndex = OptionalInt.of(index - sectionData.amountOfLines);
                } // otherwise, rarity index is the same
            }
        }

        boolean includeTextAfterRarity = action.shouldIncludeTextAfterRarity();
        boolean hasTextAfterRarity = rarityIndex.isPresent() && lore.size() > rarityIndex.getAsInt() + 1;

        if (hasTextAfterRarity) {
            List<String> textAfterRarity = lore.subList(rarityIndex.getAsInt() + 1, lore.size());

            if (includeTextAfterRarity) {
                loreAfterRarityToPossibleAdd.addAll(textAfterRarity);
            }

            // clears from original list
            textAfterRarity.clear();
        }

        return new RemovedLore(loreAfterRarityToPossibleAdd, detectedFeatures);
    }

    @Nullable
    private RemovedSectionData remove(List<String> lore) {
        if (textToFilter.isEmpty() || textToFilter.size() > lore.size()) {
            return null;
        }

        boolean shouldRemoveFromLore = settingsFilter.test(WikiWriter.getInstance().getConfig());
        int linesMatched = 0;

        for (int currentIndex = 0, loreSize = lore.size(); currentIndex < loreSize; currentIndex++) {
            String sanitizedText = Formatting.strip(lore.get(currentIndex));
            if (sanitizedText == null) {
                return null;
            }

            String textToCompareAgainst = textToFilter.get(linesMatched);
            if (!matches(sanitizedText, textToCompareAgainst)) {
                linesMatched = 0;
                continue;
            }

            if (++linesMatched == textToFilter.size()) {
                int toIndex = currentIndex + 1;
                int fromIndex = toIndex - textToFilter.size();
                if (shouldRemoveFromLore) {
                    lore.subList(fromIndex, toIndex).clear();
                }

                return new RemovedSectionData(fromIndex, toIndex, textToFilter.size(), shouldRemoveFromLore);
            }
        }

        return null;
    }

    private static boolean matches(String textFromLore, String checkingAgainst) {
        boolean bothLinesEmpty = textFromLore.replace(" ", "").length() == 0 && checkingAgainst.replace(" ", "").length() == 0;
        boolean containsText = textFromLore.contains(checkingAgainst) && checkingAgainst.length() > 0;
        boolean isAnythingAndNotEmpty = checkingAgainst.equals("{any-not-empty}") && textFromLore.replace(" ", "").length() > 0;

        return bothLinesEmpty || containsText || isAnythingAndNotEmpty;
    }

    public record RemovedLore(List<String> loreBelowRarityToPossibleAdd, Set<LoreRemovalFeature> detectedFeatures) {

        public boolean detectedShopLore() {
            return hasFeatures(SHOP_FILTERS);
        }

        public boolean hasFeatures(LoreRemovalFeature... features) {
            for (LoreRemovalFeature feature : features) {
                if (detectedFeatures.contains(feature)) {
                    return true;
                }
            }

            return false;
        }
    }

    private record RemovedSectionData(int startIndex, int endIndex, int amountOfLines, boolean actuallyRemoved) {

    }
}
