package me.pigicial.wikiwriter.config;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.JVMAnnotationPropertyCollector;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;

import java.io.File;

public class Config extends Vigilant {

    public static final int MENU_REFERENCE_MODE_ALWAYS = 0;
    public static final int MENU_REFERENCE_MODE_COPYING_ITEMS = 1;

    @Property(
            type = PropertyType.SWITCH,
            name = "Mod Enabled",
            description = "Global toggle for the features of this mod.",
            category = "General"
    )
    public boolean modEnabled = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Set Item Amounts to 1",
            description = "Toggle whether or not individually-copied items will have their amounts set to 1.",
            category = "General",
            subcategory = "Copying Items"
    )
    public boolean setAmountsToOne = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Disable Clicking on Certain Items",
            description = "Disables the ability to click on items in menus that don't have an item ID or name. Items without a name and any lore already cannot be clicked nor hovered over, this doesn't change that.",
            category = "General",
            subcategory = "Copying Items"
    )
    public boolean disableClicking = true;

    @Property(
            type = PropertyType.SELECTOR,
            name = "Item Reference Mode (Copying GUIs)",
            description = "If enabled, copied items will use their template references instead (i.e. {{Item_diamond_sword}}), and if they're shop items, their shop lore will be placed at the bottom, if they're not removed (see Text Filters).\n\nNote: This setting does not affect recipe menus copied, those items always try to use this format, assuming their automatic formats are enabled (see below).",
            category = "General",
            subcategory = "Copying Inventories",
            options = {"Always", "When Copying Shop Items", "Never"}
    )
    public int menuReferenceModeScenario = 1;

    @Property(
            type = PropertyType.SWITCH,
            name = "Automatic Shop Menu Format",
            description = "Automatically converts copied shop GUIs to use the required shop menu format.\n\nNote: Shop prices are affected by certain talismans, so make sure to remove them first!",
            category = "General",
            subcategory = "Copying Inventories"
    )
    public boolean shopMenuMode = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Dungeon Stats",
            description = "Toggles whether or not dungeon stats in dark gray should be stripped from item lore.",
            category = "Text Filters"
    )
    public boolean removeDungeonStats = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Item Amounts (in names) from Shop Menus",
            description = "When enabled, the \"x64\" (or lower) text will be removed from item names, which can be found in shop menus.",
            category = "Text Filters"
    )
    public boolean removeItemAmountsFromItemNames = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Text Below Item Rarities in Items",
            description = "When enabled, text below an item rarity will be stripped from item lore.",
            category = "Text Filters"
    )
    public boolean removeTextBelowRarityWhenCopyingItems = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Text Below Item Rarities in Menus",
            description = "When enabled, text below item rarities will be stripped from item lore.",
            category = "Text Filters"
    )
    public boolean removeTextBelowRarityWhenCopyingMenus = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Crafting Table Data",
            description = "Toggles whether or not crafting table info below recipes should be stripped from item lore.",
            category = "Text Filters",
            subcategory = "Lore Filters"
    )
    public boolean removeCraftingTableData = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Pickaxe Abilities",
            description = "Toggles whether or not pickaxe abilities should be stripped from item lore.",
            category = "Text Filters",
            subcategory = "Lore Filters"
    )
    public boolean removePickaxeAbilities = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Right Click Notices",
            description = "Toggles whether or not specific text lines that reference the act of clicking should be stripped from item lore.",
            category = "Text Filters",
            subcategory = "Lore Filters"
    )
    public boolean removeClickNotices = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Shop Price",
            description = "When toggled, lore that mentions the prices and requirements of purchasing items will be removed when copied.",
            category = "Text Filters",
            subcategory = "Lore Filters"
    )
    public boolean removeShopNPCPriceText = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Shop Stock Text",
            description = "When toggled, lore that mentions how much leftover stock you can purchase of an item will be removed.",
            category = "Text Filters",
            subcategory = "Lore Filters"
    )
    public boolean removeShopNPCStockText = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Shop Buy Text",
            description = "When toggled, lore that mentions clicking to purchase items will be removed when copied.",
            category = "Text Filters",
            subcategory = "Lore Filters"
    )
    public boolean removeShopNPCTradeText = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Pet Items",
            description = "Toggles whether or not pet items on pets should be stripped from item lore.",
            category = "Text Filters",
            subcategory = "Lore Filters"
    )
    public boolean removePetItems = true;

    public Config() {
        super(new File("./config/wikiwriter.toml"), "WikiWriter", new JVMAnnotationPropertyCollector(), new CustomSortingBehavior());
        initialize();
    }
}