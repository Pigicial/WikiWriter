package me.pigicial.wikiwriter.utils;

import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.config.ModConfig;

public enum Action {
    COPYING_STANDALONE_ITEM,
    COPYING_INVENTORY,
    COPYING_SHOP_INVENTORY,
    COPYING_RECIPE_INVENTORY;

    public boolean shouldIncludeTextAfterRarity() {
        ModConfig config = WikiWriter.getInstance().getConfig();
        ModConfig.TextFiltersConfig textFiltersConfig = config.getTextFiltersConfig();

        return switch (this) {
            case COPYING_RECIPE_INVENTORY -> false;
            case COPYING_STANDALONE_ITEM -> !textFiltersConfig.removeTextBelowRarityWhenCopyingItems;
            case COPYING_INVENTORY, COPYING_SHOP_INVENTORY -> !textFiltersConfig.removeTextBelowRarityWhenCopyingMenus;
        };
    }
}
