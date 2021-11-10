package me.pigicial.wikiwriter.utils;

import lombok.Getter;
import me.pigicial.wikiwriter.core.Config;

import java.util.function.BiPredicate;

public enum LorePredicates implements BiPredicate<Config, Action> {
    SHOP_PRICE_PREDICATE((config, action) -> {
        if (action == Action.COPYING_STANDALONE_ITEM) {
            return config.removeShopNPCPriceText == 0 || config.removeShopNPCPriceText == 2;
        } else {
            // because it's copying the full inventory, and it's a shop item, it won't show since the item is a reference and actually will be removed
            return config.removeShopNPCPriceText != 0 && config.removeShopNPCPriceText != 1;

            // 3 = never remove it, and 2 = remove it if it's the full inv (which this is called on), but since it's a shop item,
            // this will actually remove it and add it back since the item is a shop item and the main item is a reference
        }
    }),
    SHOP_CLICK_PREDICATE((config, action) -> {
        if (action == Action.COPYING_STANDALONE_ITEM) {
            return config.removeShopNPCTradeText == 0 || config.removeShopNPCTradeText == 2;
        } else {
            // because it's copying the full inventory, and it's a shop item, it won't show since the item is a reference and actually will be removed
            return config.removeShopNPCTradeText != 0 && config.removeShopNPCTradeText != 1;

            // 3 = never remove it, and 2 = remove it if it's a standalone item (which this is called on), but since it's a shop item,
            // this will actually remove it and add it back since the item is a shop item and the main item is a reference
        }
    }),

    QUIVER_SHOP_ADD_NOTICE_PREDICATE(((config, action) -> {
        if (action == Action.COPYING_STANDALONE_ITEM) {
            return config.removeFillQuiverNotice == 0 || config.removeFillQuiverNotice == 2;
        } else {
            // because it's copying the full inventory, and it's a shop item, it won't show since the item is a reference and actually will be removed
            return config.removeFillQuiverNotice != 0 && config.removeFillQuiverNotice != 1;

            // 3 = never remove it, and 2 = remove it if it's the full inv (which this is called on), but since it's a shop item,
            // this will actually remove it and add it back since the item is a shop item and the main item is a reference
        }
    })),

    QUIVER_SHOP_FILL_QUIVER_COST(((config, action) -> {
        if (action == Action.COPYING_STANDALONE_ITEM) {
            return config.removeFillQuiverShopPrice == 0 || config.removeFillQuiverShopPrice == 2;
        } else {
            // because it's copying the full inventory, and it's a shop item, it won't show since the item is a reference and actually will be removed
            return config.removeFillQuiverShopPrice != 0 && config.removeFillQuiverShopPrice != 1;

            // 3 = never remove it, and 2 = remove it if it's the full inv (which this is called on), but since it's a shop item,
            // this will actually remove it and add it back since the item is a shop item and the main item is a reference
        }
    }));

    @Getter
    private final BiPredicate<Config, Action> predicate;

    LorePredicates(BiPredicate<Config, Action> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(Config config, Action action) {
        return predicate.test(config, action);
    }
}
