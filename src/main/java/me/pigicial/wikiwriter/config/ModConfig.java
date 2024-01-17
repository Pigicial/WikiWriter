package me.pigicial.wikiwriter.config;


import lombok.Getter;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "wikiwriter")
@Getter
public class ModConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public boolean modEnabled = true;

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    private final CopyingItemsConfig copyingItemsConfig = new CopyingItemsConfig();

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    private final CopyingInventoriesConfig copyingInventoriesConfig = new CopyingInventoriesConfig();

    @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
    private final TextFiltersConfig textFiltersConfig = new TextFiltersConfig();

    public static class CopyingItemsConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean setAmountsToOne = true;

        @ConfigEntry.Gui.Tooltip
        public boolean disableClicking = true;
    }

    public static class CopyingInventoriesConfig {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.EnumHandler
        public ReferenceModeScenario menuReferenceModeScenario = ReferenceModeScenario.WHEN_COPYING_SHOP_ITEMS;

        @ConfigEntry.Gui.Tooltip
        public boolean shopMenuMode = true;
    }

    public static class TextFiltersConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean removeDungeonStats = true;

        @ConfigEntry.Gui.Tooltip
        public boolean removeTextBelowRarityWhenCopyingItems = true;

        @ConfigEntry.Gui.Tooltip
        public boolean removeTextBelowRarityWhenCopyingMenus = false;

        @ConfigEntry.Gui.CollapsibleObject(startExpanded = true)
        @Getter
        private final LoreFiltersConfig loreFiltersConfig = new LoreFiltersConfig();
    }

    public static class LoreFiltersConfig {
        @ConfigEntry.Gui.Tooltip
        public boolean removeClickNotices = true;

        @ConfigEntry.Gui.Tooltip
        public boolean removeShopNPCPriceText = false;

        @ConfigEntry.Gui.Tooltip
        public boolean removeShopNPCStockText = true;

        @ConfigEntry.Gui.Tooltip
        public boolean removeShopNPCTradeText = true;

        @ConfigEntry.Gui.Tooltip
        public boolean removePickaxeAbilities = true;

        @ConfigEntry.Gui.Tooltip
        public boolean removePetItems = true;
    }

    public enum ReferenceModeScenario {
        ALWAYS,
        WHEN_COPYING_SHOP_ITEMS,
        NEVER
    }
}
