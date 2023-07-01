package me.pigicial.wikiwriter.features;

import lombok.Data;
import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.core.Config;
import me.pigicial.wikiwriter.utils.Action;
import me.pigicial.wikiwriter.utils.LorePredicates;
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

    SHOP_1(LorePredicates.SHOP_PRICE_PREDICATE, "", "Cost", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}"),
    SHOP_2(LorePredicates.SHOP_PRICE_PREDICATE, "", "Cost", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}"),
    SHOP_3(LorePredicates.SHOP_PRICE_PREDICATE, "", "Cost", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}"),
    SHOP_4(LorePredicates.SHOP_PRICE_PREDICATE, "", "Cost", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}"),
    SHOP_5(LorePredicates.SHOP_PRICE_PREDICATE, "", "Cost", "{anything}", "{anything}", "{anything}", "{anything}"),
    SHOP_6(LorePredicates.SHOP_PRICE_PREDICATE, "", "Cost", "{anything}", "{anything}", "{anything}"),
    SHOP_7(LorePredicates.SHOP_PRICE_PREDICATE, "", "Cost", "{anything}", "{anything}"),
    SHOP_8(LorePredicates.SHOP_PRICE_PREDICATE, "", "Cost", "{anything}"),
    SHOP_9(LorePredicates.SHOP_PRICE_PREDICATE, "", "Cost:"),

    BOTTOM_SHOP_1(LorePredicates.SHOP_CLICK_PREDICATE, "", "Click to purchase"),
    BOTTOM_SHOP_2(LorePredicates.SHOP_CLICK_PREDICATE, "", "Click to trade"),
    BOTTOM_SHOP_3(LorePredicates.SHOP_CLICK_PREDICATE, "", "You don't have enough coins"),
    BOTTOM_SHOP_4(LorePredicates.SHOP_CLICK_PREDICATE, "Right-Click for more trading options"),
    BOTTOM_SHOP_5(LorePredicates.SHOP_CLICK_PREDICATE, "Right-click to fill quiver"),
    BOTTOM_SHOP_6(LorePredicates.SHOP_CLICK_PREDICATE, "", "Click to buy into quiver"),
    BOTTOM_SHOP_7(LorePredicates.SHOP_CLICK_PREDICATE, "", "Not unlocked"),

    BOTTOM_SHOP_8(LorePredicates.SHOP_CLICK_PREDICATE, "", "You don't have the required items"),
    BOTTOM_SHOP_9(LorePredicates.SHOP_CLICK_PREDICATE, "", "Click to craft"),

    PET_CANDY(config -> config.removePetCandy, "Pet Candy Used", ""),

    PET_ITEMS_1(config -> config.removePetItems, "Held Item:", "{anything}", ""),
    PET_ITEMS_2(config -> config.removePetItems, "Held Item:", "{anything}", "{anything}", ""),
    PET_ITEMS_3(config -> config.removePetItems, "Held Item:", "{anything}", "{anything}", "{anything}", ""),
    PET_ITEMS_4(config -> config.removePetItems, "Held Item:", "{anything}", "{anything}", "{anything}", "{anything}", ""),
    PET_ITEMS_5(config -> config.removePetItems, "Held Item:", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", ""),

    FIRE_SALES_1(config -> config.removeFireSaleData, "", "-------", "", "Cost", "Items Sold", "", "This sale recently ended"),
    FIRE_SALES_2(config -> config.removeFireSaleData, "", "-------", "", "Cost", "Amount for Sale", "", "Starts in"),
    FIRE_SALES_3(config -> config.removeFireSaleData, "", "-------", "", "Cost", "Duration", "", "This sale recently ended"),
    FIRE_SALES_4(config -> config.removeFireSaleData, "", "-------", "", "Cost", "Duration", "", "Starts in"),

    FIRE_SALES_5(config -> config.removeFireSaleData, "", "-------", "", "Cost", "Items Sold", "", "Sold out in", "In"),
    FIRE_SALES_6(config -> config.removeFireSaleData, "", "-------", "", "Cost", "Duration", "", "Amount Sold", "In"),

    ESSENCE_SHOP_1(config -> config.removeEssenceShopData, "", "Convert to Dungeon Item", "Upgrade to", "Upgrade to", "Upgrade to", "Upgrade to", "Upgrade to"),
    ESSENCE_SHOP_2(config -> config.removeEssenceShopData, "", "Upgrade to", "Upgrade to", "Upgrade to", "Upgrade to", "Upgrade to"),

    GEMSTONE_GUIDE_1(config -> config.removeGemstoneGuideData, "", "Available Gemstone Slot", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}"),
    GEMSTONE_GUIDE_2(config -> config.removeGemstoneGuideData, "", "Available Gemstone Slot", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}"),
    GEMSTONE_GUIDE_3(config -> config.removeGemstoneGuideData, "", "Available Gemstone Slot", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}"),
    GEMSTONE_GUIDE_4(config -> config.removeGemstoneGuideData, "", "Available Gemstone Slot", "{anything}", "{anything}", "{anything}", "{anything}"),
    GEMSTONE_GUIDE_5(config -> config.removeGemstoneGuideData, "", "Available Gemstone Slot", "{anything}", "{anything}", "{anything}"),
    GEMSTONE_GUIDE_6(config -> config.removeGemstoneGuideData, "", "Available Gemstone Slot", "{anything}", "{anything}"),
    GEMSTONE_GUIDE_7(config -> config.removeGemstoneGuideData, "", "Available Gemstone Slot", "{anything}"),

    MUSEUM_1(config -> config.removeMuseumData, "-------", "Armor Set Donated", "{anything}", "", "Armor Set Completed", "{anything}", "{anything}", "", "Armor Set Value", "{anything}", "", "Display Slot", "{anything}", "", "Left-click to retrieve items", "Right-click to view armor set"),
    MUSEUM_2(config -> config.removeMuseumData, "-------", "Armor Set Donated", "{anything}", "", "Armor Set Completed", "{anything}", "{anything}", "", "Armor Set Value", "{anything}", "", "Left-click to retrieve items", "Right-click to view armor set"),

    MUSEUM_3(config -> config.removeMuseumData, "-------", "Item Donated", "{anything}", "", "Item Created", "{anything}", "{anything}", "", "Item Value", "{anything}", "", "Display Slot", "{anything}", "", "Click to remove"),
    MUSEUM_4(config -> config.removeMuseumData, "-------", "Item Donated", "{anything}", "", "Item Created", "{anything}", "{anything}", "", "Item Value", "{anything}", "", "Click to remove"),

    MUSEUM_5(config -> config.removeMuseumData, "-------", "Item Donated", "{anything}", "", "Item Created", "{anything}", "{anything}", "", "Item Value", "{anything}", "", "Display Slot", "{anything}"),
    MUSEUM_6(config -> config.removeMuseumData, "-------", "Item Donated", "{anything}", "", "Item Created", "{anything}", "{anything}", "", "Item Value", "{anything}"),

    BIN_1(config -> config.removeAuctionData, "-----------------", "Seller", "Buy it now", "", "Ends in", "", "Click to inspect!"),
    BIN_2(config -> config.removeAuctionData, "-----------------", "Seller", "Buy it now", "", "Ends in"),
    ENDED_BIN_1(config -> config.removeAuctionData, "-----------------", "Seller", "Buy it now", "", "Status", "", "Click to inspect!"),
    ENDED_BIN_2(config -> config.removeAuctionData, "-----------------", "Seller", "Buy it now", "", "Status"),

    PURCHASED_BIN_1(config -> config.removeAuctionData, "-----------------", "Seller", "Buyer", "", "Sold for", "", "Status", "", "Click to inspect!"),
    PURCHASED_BIN_2(config -> config.removeAuctionData, "-----------------", "Seller", "Buyer", "", "Sold for", "", "Status"),

    SELF_PURCHASED_BIN_1(config -> config.removeAuctionData, "-----------------", "Seller", "Buyer", "", "Sold for", "", "This is your own auction!", "", "Status", "", "Click to inspect!"),
    SELF_PURCHASED_BIN_2(config -> config.removeAuctionData, "-----------------", "Seller", "Buyer", "", "Sold for", "", "This is your own auction!", "", "Status"),

    SELF_BIN_1(config -> config.removeAuctionData, "-----------------", "Seller", "Buy it now", "", "This is your own auction", "", "Ends in", "", "Click to inspect"),
    SELF_BIN_2(config -> config.removeAuctionData, "-----------------", "Seller", "Buy it now", "", "This is your own auction", "", "Ends in"),
    ENDED_SELF_BIN_1(config -> config.removeAuctionData, "-----------------", "Seller", "Buy it now", "", "This is your own auction", "", "Status", "", "Click to inspect"),
    ENDED_SELF_BIN_2(config -> config.removeAuctionData, "-----------------", "Seller", "Buy it now", "", "This is your own auction", "", "Status"),

    NO_BIDS_AUCTION_1(config -> config.removeAuctionData, "-----------------", "Seller", "Starting bid", "", "Ends in", "", "Click to inspect!"),
    NO_BIDS_AUCTION_2(config -> config.removeAuctionData, "-----------------", "Seller", "Starting bid", "", "Ends in"),
    NO_BIDS_ENDED_AUCTION_1(config -> config.removeAuctionData, "-----------------", "Seller", "Starting bid", "", "Status", "", "Click to inspect!"),
    NO_BIDS_ENDED_AUCTION_2(config -> config.removeAuctionData, "-----------------", "Seller", "Starting bid", "", "Status"),

    SELF_NO_BIDS_AUCTION_1(config -> config.removeAuctionData, "-----------------", "Seller", "Starting bid", "", "This is your own auction", "", "Ends in", "", "Click to inspect!"),
    SELF_NO_BIDS_AUCTION_2(config -> config.removeAuctionData, "-----------------", "Seller", "Starting bid", "", "This is your own auction", "", "Ends in"),
    SELF_NO_BIDS_ENDED_AUCTION_1(config -> config.removeAuctionData, "-----------------", "Seller", "Starting bid", "", "This is your own auction", "", "Status", "", "Click to inspect!"),
    SELF_NO_BIDS_ENDED_AUCTION_2(config -> config.removeAuctionData, "-----------------", "Seller", "Starting bid", "", "This is your own auction", "", "Status"),

    SELF_AUCTION_1(config -> config.removeAuctionData, "-----------------", "Seller", "Bids", "", "Top bid", "Bidder", "", "This is your own auction", "", "Ends in", "", "Click to inspect!"),
    SELF_AUCTION_2(config -> config.removeAuctionData, "-----------------", "Seller", "Bids", "", "Top bid", "Bidder", "", "This is your own auction", "", "Ends in"),
    SELF_ENDED_AUCTION_1(config -> config.removeAuctionData, "-----------------", "Seller", "Bids", "", "Top bid", "Bidder", "", "This is your own auction", "", "Status", "", "Click to inspect!"),
    SELF_ENDED_AUCTION_2(config -> config.removeAuctionData, "-----------------", "Seller", "Bids", "", "Top bid", "Bidder", "", "This is your own auction", "", "Status"),

    BID_ON_AUCTION_1(config -> config.removeAuctionData, "-----------------", "Seller", "Bids", "", "Top bid", "Bidder", "", "Ends in", "", "Click to inspect!"),
    BID_ON_AUCTION_2(config -> config.removeAuctionData, "-----------------", "Seller", "Bids", "", "Top bid", "Bidder", "", "Ends in"),
    ENDED_BID_ON_AUCTION_1(config -> config.removeAuctionData, "-----------------", "Seller", "Bids", "", "Top bid", "Bidder", "", "Status", "", "Click to inspect!"),
    ENDED_BID_ON_AUCTION_2(config -> config.removeAuctionData, "-----------------", "Seller", "Bids", "", "Top bid", "Bidder", "", "Status"),

    SELF_BID_ON_AUCTION_1(config -> config.removeAuctionData, "-----------------", "Seller", "Bids", "", "Top bid", "Bidder", "Profile", "", "Ends in", "", "Click to inspect"),
    SELF_BID_ON_AUCTION_2(config -> config.removeAuctionData, "-----------------", "Seller", "Bids", "", "Top bid", "Bidder", "Profile", "", "Ends in"),
    SELF_BID_ON_ENDED_AUCTION_1(config -> config.removeAuctionData, "-----------------", "Seller", "Bids", "", "Top bid", "Bidder", "Profile", "", "Status", "", "Click to inspect"),
    SELF_BID_ON_ENDED_AUCTION_2(config -> config.removeAuctionData, "-----------------", "Seller", "Bids", "", "Top bid", "Bidder", "Profile", "", "Status");

    public static final LoreRemovalFeature[] SHOP_FILTERS = new LoreRemovalFeature[]{SHOP_1, SHOP_2, SHOP_3, SHOP_4, SHOP_5, SHOP_6, SHOP_7, SHOP_8, BOTTOM_SHOP_1, BOTTOM_SHOP_2, BOTTOM_SHOP_3, BOTTOM_SHOP_4, BOTTOM_SHOP_5, BOTTOM_SHOP_6, BOTTOM_SHOP_7, BOTTOM_SHOP_8, BOTTOM_SHOP_9};

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
}
