package me.pigicial.wikiwriter.features;

import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.core.Config;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public enum LoreRemovalFeature {
    CLICK_1(config -> config.removeRightClickNotices, "Right-click to view recipes!", ""),
    CLICK_2(config -> config.removeRightClickNotices, "Right click to view recipes", ""),
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

    ENCHANTMENTS_1(config -> config.removeEnchantmentRequirementNotices, "You do not have a high enough", "Enchantment level to use some of", "the enchantments on this item", ""),
    ENCHANTMENTS_2(config -> config.removeEnchantmentRequirementNotices, "Requires Enchanting", "apply.", ""),
    ENCHANTMENTS_3(config -> config.removeEnchantmentRequirementNotices, "Requires Enchanting", ""),

    SHOP_1(config -> config.removeShopNPCTradeText, "Right-Click for more trading options!"),
    SHOP_2(config -> config.removeShopNPCTradeText, "", "Cost", "{anything}", "", "Click to trade!"),
    SHOP_3(config -> config.removeShopNPCTradeText, "", "Cost", "{anything}", "{anything}", "", "Click to trade!"),
    SHOP_4(config -> config.removeShopNPCTradeText, "", "Cost", "{anything}", "{anything}", "{anything}", "", "Click to trade!"),
    SHOP_5(config -> config.removeShopNPCTradeText, "", "Cost", "{anything}", "{anything}", "{anything}", "{anything}", "", "Click to trade!"),

    PET_CANDY(config -> config.removePetCandy, "Pet Candy Used", ""),

    PET_ITEMS_1(config -> config.removePetItems, "Held Item:", "{anything}", ""),
    PET_ITEMS_2(config -> config.removePetItems, "Held Item:", "{anything}", "{anything}", ""),
    PET_ITEMS_3(config -> config.removePetItems, "Held Item:", "{anything}", "{anything}", "{anything}", ""),
    PET_ITEMS_4(config -> config.removePetItems, "Held Item:", "{anything}", "{anything}", "{anything}", "{anything}", ""),
    PET_ITEMS_5(config -> config.removePetItems, "Held Item:", "{anything}", "{anything}", "{anything}", "{anything}", "{anything}", ""),

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

    private static final Pattern AUCTION_COUNT_PATTERN = Pattern.compile("(-?[0-9]|[1-9][0-9]|[1-9][0-9][0-9])x");

    private final Predicate<Config> settingsFilter;
    private final List<String> textToFilter;

    LoreRemovalFeature(Predicate<Config> settingsFilter, String... textToFilter) {
        this.settingsFilter = settingsFilter;
        this.textToFilter = Arrays.asList(textToFilter);
    }

    LoreRemovalFeature(String... textToFilter) {
        this.settingsFilter = config -> true;
        this.textToFilter = Arrays.asList(textToFilter);
    }

    public static void filterLore(List<String> lore) {
        Config config = WikiWriter.getInstance().getConfig();

        for (LoreRemovalFeature removal : values()) {
            if (!removal.settingsFilter.test(config)) continue;
            List<String> textToFilter = removal.getTextToFilter();
            if (textToFilter.isEmpty() || textToFilter.size() > lore.size()) continue;

            List<Integer> toRemove = new ArrayList<>(textToFilter.size());
            int offsetPosition = 0;
            int totalRemoved = 0;
            List<String> strings = new ArrayList<>(lore);

            for (int i = 0, stringsSize = strings.size(); i < stringsSize; i++) {
                String s = EnumChatFormatting.getTextWithoutFormattingCodes(strings.get(i));
                String checkingAgainst = textToFilter.get(i - offsetPosition);
                if ((s.replace(" ", "").length() == 0 && checkingAgainst.replace(" ", "").length() == 0)
                        || (s.contains(checkingAgainst) && checkingAgainst.length() >= 1) || checkingAgainst.equals("{anything}")) {
                    toRemove.add(i + totalRemoved);

                    if (toRemove.size() == textToFilter.size()) {
                        totalRemoved += toRemove.size();
                        Collections.reverse(toRemove); // remove from the end to avoid messing up the indices
                        for (int positionToRemove : toRemove) {
                            lore.remove(positionToRemove);
                        }
                        offsetPosition = i + 1;
                        toRemove.clear();
                    }
                } else {
                    offsetPosition = i + 1;
                    toRemove.clear();
                }
            }
        }
    }

    public List<String> getTextToFilter() {
        return textToFilter;
    }
}
