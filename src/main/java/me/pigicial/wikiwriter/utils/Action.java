package me.pigicial.wikiwriter.utils;

import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.config.Config;

public enum Action {
    COPYING_INVENTORY,
    COPYING_SHOP_INVENTORY,
    COPYING_RECIPE_INVENTORY,
    COPYING_STANDALONE_ITEM;

    public boolean shouldIncludeTextAfterRarity() {
        Config config = WikiWriter.getInstance().getConfig();
        return switch (this) {
            case COPYING_RECIPE_INVENTORY -> false;
            case COPYING_STANDALONE_ITEM -> !config.removeTextBelowRarityWhenCopyingItems;
            case COPYING_INVENTORY, COPYING_SHOP_INVENTORY -> !config.removeTextBelowRarityWhenCopyingMenus;
        };
    }
}
