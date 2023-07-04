package me.pigicial.wikiwriter.features;

import lombok.Data;
import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.config.Config;
import me.pigicial.wikiwriter.utils.Action;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum LoreRemovalFeature {
    CLICK_1(config -> config.removeRightClickNotices, "Right-click to view recipes!", ""),
    CLICK_2(config -> config.removeRightClickNotices, "Right click to view recipes", ""),
    CLICK_1A(config -> config.removeRightClickNotices, "Right-click to view recipes!"),
    CLICK_2A(config -> config.removeRightClickNotices, "Right click to view recipes"),
    CLICK_3(config -> config.removeRightClickNotices, "", "Click to view recipe!"),
    CLICK_4(config -> config.removeRightClickNotices, "", "Click to view recipes!"),
    CLICK_5(config -> config.removeRightClickNotices, "Right click on your pet to", "give it this item!", ""),
    CLICK_6(config -> config.removeRightClickNotices, "Right click on your pet to", "feed it this candy!", ""),
    CLICK_7(config -> config.removeRightClickNotices, "Right-click to add this pet to", "your pet menu!", ""),
    CLICK_8(config -> config.removeRightClickNotices, "Right-click to use your class", "ability", ""),
    CLICK_9(config -> config.removeRightClickNotices, "Right-click to use your class ability", ""),
    CLICK_10(config -> config.removeRightClickNotices, "Right-click to use Class Ability", ""),
    CLICK_11(config -> config.removeRightClickNotices, "Right click to use Class", "Ability", ""),
    CLICK_12(config -> config.removeRightClickNotices, "Right-click to consume!", ""),
    CLICK_13(config -> config.removeRightClickNotices, "Right click on your pet to", "apply this skin!", ""),
    CLICK_14(config -> config.removeRightClickNotices, "Right click on your griffin to", "upgrade it!", ""),
    CLICK_15(config -> config.removeClickToToggle, "", "Click to toggle"),
    CLICK_16(config -> config.removeRightClickNotices, "", "Right-Click a block to use!"),
    CLICK_17(config -> config.removeRightClickNotices, "", "Right-click to open!"),
    CLICK_18(config -> config.removeRightClickNotices, "", "Right-click on Kat to use"),
    CLICK_19(config -> config.removeRightClickNotices, "", "Right-Click a block to use!"),
    CLICK_20(config -> config.removeRightClickNotices, "", "Right-click to configure!"),
    CLICK_21(config -> config.removeRightClickNotices, "", "Click to view upgrades"),

    CLICK_TO_SUMMON(config -> config.removeClickToSummon, "", "Click to summon"),

    CRAFTING_TABLE_1(config -> config.removeCraftingTableData, "--------", "This is the item you are", "crafting"),
    QUICK_CRAFTING_1(config -> config.removeCraftingTableData, "", "--------", "Ingredients", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "", "Click to craft"),
    QUICK_CRAFTING_2(config -> config.removeCraftingTableData, "", "--------", "Ingredients", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "Click to craft"),
    QUICK_CRAFTING_3(config -> config.removeCraftingTableData, "", "--------", "Ingredients", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "", "Click to craft"),
    QUICK_CRAFTING_4(config -> config.removeCraftingTableData, "", "--------", "Ingredients", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "", "Click to craft"),
    QUICK_CRAFTING_5(config -> config.removeCraftingTableData, "", "--------", "Ingredients", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "", "Click to craft"),
    QUICK_CRAFTING_6(config -> config.removeCraftingTableData, "", "--------", "Ingredients", "{anything}", "{anything}", "{anything}", "{anything}", "", "Click to craft"),
    QUICK_CRAFTING_7(config -> config.removeCraftingTableData, "", "--------", "Ingredients", "{anything}", "{anything}", "{anything}", "", "Click to craft"),
    QUICK_CRAFTING_8(config -> config.removeCraftingTableData, "", "--------", "Ingredients", "{anything}", "{anything}", "", "Click to craft"),
    QUICK_CRAFTING_9(config -> config.removeCraftingTableData, "", "--------", "Ingredients", "{anything}", "", "Click to craft"),

    PICKAXE_ABILITY_1(config -> config.removePickaxeAbilities, "Ability: Mining Speed Boost", "Grants", "Speed", "Cooldown", ""),
    PICKAXE_ABILITY_2(config -> config.removePickaxeAbilities, "Ability: Maniac Miner", "Spends", "{anything}", "every", "{anything}", "Cooldown", ""),
    PICKAXE_ABILITY_3(config -> config.removePickaxeAbilities, "Ability: Pickobulus", "Throw your pickaxe", "explosion on impact", "ores within a", "radius", "Cooldown", ""),
    PICKAXE_ABILITY_4(config -> config.removePickaxeAbilities, "Ability: Vein Seeker", "Points in the direction", "nearest vein", "Mining Spread", "Cooldown", ""),

    ENCHANTMENTS_1(config -> config.removeEnchantmentRequirementNotices, "You do not have a high enough", "Enchantment level to use some of", "the enchantments on this item", ""),
    ENCHANTMENTS_2(config -> config.removeEnchantmentRequirementNotices, "Requires Enchanting", "apply.", ""),
    ENCHANTMENTS_3(config -> config.removeEnchantmentRequirementNotices, "Requires Enchanting", ""),

    SHOP_1(config -> config.removeShopNPCPriceText, "", "Cost", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}"),
    SHOP_2(config -> config.removeShopNPCPriceText, "", "Cost", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}"),
    SHOP_3(config -> config.removeShopNPCPriceText, "", "Cost", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}"),
    SHOP_4(config -> config.removeShopNPCPriceText, "", "Cost", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}"),
    SHOP_5(config -> config.removeShopNPCPriceText, "", "Cost", "{anything}", "{anything}", "{anything}", "{anything}"),
    SHOP_6(config -> config.removeShopNPCPriceText, "", "Cost", "{anything}", "{anything}", "{anything}"),
    SHOP_7(config -> config.removeShopNPCPriceText, "", "Cost", "{anything}", "{anything}"),
    SHOP_8(config -> config.removeShopNPCPriceText, "", "Cost", "{anything}"),
    SHOP_9(config -> config.removeShopNPCPriceText, "", "Cost:"),

    BOTTOM_SHOP_1(config -> config.removeShopNPCTradeText, "", "Click to purchase"),
    BOTTOM_SHOP_2(config -> config.removeShopNPCTradeText, "", "Click to trade"),
    BOTTOM_SHOP_3(config -> config.removeShopNPCTradeText, "", "You don't have enough coins"),
    BOTTOM_SHOP_4(config -> config.removeShopNPCTradeText, "Right-Click for more trading options"),
    BOTTOM_SHOP_5(config -> config.removeShopNPCTradeText, "Right-click to fill quiver"),
    BOTTOM_SHOP_6(config -> config.removeShopNPCTradeText, "", "Click to buy into quiver"),
    BOTTOM_SHOP_7(config -> config.removeShopNPCTradeText, "", "Not unlocked"),
    BOTTOM_SHOP_8(config -> config.removeShopNPCTradeText, "", "You don't have the required items"),
    BOTTOM_SHOP_9(config -> config.removeShopNPCTradeText, "", "Click to craft"),

    PET_CANDY(config -> config.removePetCandy, "Pet Candy Used", ""),

    PET_ITEMS_1(config -> config.removePetItems, "Held Item:", "{anything}", ""),
    PET_ITEMS_2(config -> config.removePetItems, "Held Item:", "{anything}", "{anything}", ""),
    PET_ITEMS_3(config -> config.removePetItems, "Held Item:", "{anything}", "{anything}", "{anything}", ""),
    PET_ITEMS_4(config -> config.removePetItems, "Held Item:", "{anything}", "{anything}", "{anything}", "{anything}", ""),
    PET_ITEMS_5(config -> config.removePetItems, "Held Item:", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "");

    public static final LoreRemovalFeature[] SHOP_FILTERS = new LoreRemovalFeature[]{
            SHOP_1,
            SHOP_2,
            SHOP_3,
            SHOP_4,
            SHOP_5,
            SHOP_6,
            SHOP_7,
            SHOP_8,
            BOTTOM_SHOP_1,
            BOTTOM_SHOP_2,
            BOTTOM_SHOP_3,
            BOTTOM_SHOP_4,
            BOTTOM_SHOP_5,
            BOTTOM_SHOP_6,
            BOTTOM_SHOP_7,
            BOTTOM_SHOP_8,
            BOTTOM_SHOP_9
    };

    private final BiPredicate<Config, Action> settingsFilter;
    private final List<String> textToFilter;

    LoreRemovalFeature(Predicate<Config> settingsFilter, String... textToFilter) {
        this.settingsFilter = (config, action) -> settingsFilter.test(config);
        this.textToFilter = Arrays.asList(textToFilter);
    }

    LoreRemovalFeature(BiPredicate<Config, Action> settingsFilter, String... textToFilter) {
        this.settingsFilter = settingsFilter;
        this.textToFilter = Arrays.asList(textToFilter);
    }

    public static RemoveData checkAndFilter(Action action, List<String> lore, LoreRemovalFeature... toCheck) {
        return checkAndFilter(action, lore, true, toCheck);
    }

    public static RemoveData checkAndFilter(Action action, List<String> lore, boolean predicateCheck, LoreRemovalFeature... toCheck) {
        Config config = WikiWriter.getInstance().getConfig();

        List<String> removedLore = new ArrayList<>();

        for (LoreRemovalFeature removalType : Arrays.stream(toCheck).sorted(Comparator.comparing(Enum::ordinal)).collect(Collectors.toList())) {
            boolean pass = !predicateCheck || removalType.settingsFilter.test(config, action);
            if (!pass) {
                continue;
            }

            List<String> textToFilter = removalType.getTextToFilter();
            if (textToFilter.isEmpty() || textToFilter.size() > lore.size()) {
                continue;
            }

            int matchCount = 0;
            int lastResetIndex = 0;

            for (int i = 0, loreSize = lore.size(); i < loreSize; i++) {
                if (i - lastResetIndex >= textToFilter.size()) {
                    break;
                }

                String sanitizedText = Formatting.strip(lore.get(i));
                if (sanitizedText == null) {
                    break;
                }

                String textToCompareAgainst = textToFilter.get(i - lastResetIndex);

                if (!matches(sanitizedText, textToCompareAgainst)) {
                    matchCount = 0;

                    // add 1 for the next iteration
                    lastResetIndex = i + 1;
                    continue;
                }

                matchCount++;
                if (matchCount != textToFilter.size()) {
                    continue;
                }

                for (int j = 0; j <= matchCount; j++) {
                    // remove it from lastResetIndex each time, since each time it does it the next text that's removed is different
                    String removeText = lore.remove(lastResetIndex);
                    removedLore.add(removeText);
                }

                // Reset index data so the loop doesn't break
                i -= matchCount;
                lastResetIndex -= matchCount;
                matchCount = 0;
                loreSize = lore.size();
            }
        }

        return new RemoveData(lore, removedLore);
    }

    private static boolean matches(String textFromLore, String checkingAgainst) {
        return (textFromLore.replace(" ", "").length() == 0 && checkingAgainst.replace(" ", "").length() == 0)
                || (textFromLore.contains(checkingAgainst) && checkingAgainst.length() >= 1) || (checkingAgainst.equals("{anything}") && textFromLore.replace(" ", "").length() > 0);
    }

    public List<String> getTextToFilter() {
        return textToFilter;
    }

    @Data
    public static class RemoveData {
        private final List<String> newLore;
        private final List<String> removedLore;

        public boolean hasRemovedLore() {
            return !removedLore.isEmpty();
        }
    }

    @Data
    public static class RemovedLore {
        private final List<String> removedLoreBeforeRarity;
        private final List<String> removedLoreAfterRarity;
    }
}
