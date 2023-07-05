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
            name = "Copying Items",
            description = "Enable the ability to copy hovered items either individually or into a single-slot menu using a set keybind, which you can find in your controls menu.\n\nNote: Don't worry about any mods or settings that add additional lore to items. The data is taken directly from NBT, which mods don't affect!",
            category = "Copying Items"
    )
    public boolean copyItems = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Item Templates Mode",
            description = "When enabled, individually copied items will include the text <noinclude>[[Category:Item UI Templates]]</noinclude><includeonly> and </includeonly> to allow them to be used as item templates.",
            category = "Copying Items"
    )
    public boolean itemTemplatesMode = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Guaranteed Stack Size Toggle",
            description = "Toggle whether or not items copied should have their stack sizes forcefully set, up to their vanilla stack size limit.\n\nThis feature is disabled for recipes while recipe mode is enabled.",
            category = "Copying Items",
            subcategory = "Changing Item Amounts"
    )
    public boolean guaranteedStackSizeToggled = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Disable Clicking on Certain Items",
            description = "Disables the ability to click on items in menus that don't have an item ID or name. Items without a name and any lore already cannot be clicked nor hovered over, this doesn't change that.",
            category = "Copying Items"
    )
    public boolean disableClicking = true;

    @Property(
            type = PropertyType.NUMBER,
            name = "Guaranteed Stack Size",
            description = "The guaranteed stack size of items. Items will not go higher than their vanilla stack size limit.\n\nWarning: Items copied from Auction GUIs that have a stack size that isn't one will have their references break.",
            category = "Copying Items",
            subcategory = "Changing Item Amounts",
            min = 1,
            max = 64
    )
    public int setStackSize = 1;

    @Property(
            type = PropertyType.SWITCH,
            name = "Bypass Stack Size Limit",
            description = "Toggle whether or not items with the amounts changed can have their stack sizes be forcefully set to above their vanilla stack size limit. Items with a UUID have a max stack size of 1.",
            category = "Copying Items",
            subcategory = "Changing Item Amounts"
    )
    public boolean bypassStackSizeLimit = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Backpack Colors",
            description = "Toggles whether or not backpack colors should be referenced in item references / textures.", // When copying recipe GUIs, the bottom Go Back arrow and Close barrier will simply be replaced with a lore-less arrow, which is required for recipes.
            category = "Copying Items",
            subcategory = "Misc"
    )
    public boolean backpackColors = true;

    @Property(
            type = PropertyType.SELECTOR,
            name = "Item Reference Mode (Copying GUIs)",
            description = "If enabled, copied items will use their template references instead (i.e. {{Item_diamond_sword}}), and if they're shop items, their shop lore will be placed at the bottom, if they're not removed (see Text Filters).\n\nNote: This setting does not affect recipe menus copied, those items always try to use this format, assuming their automatic formats are enabled (see below).",
            category = "Copying Inventories",
            options = {"Always", "When Copying Shop Items", "Never"}
    )
    public int menuReferenceModeScenario = 1;

    @Property(
            type = PropertyType.SWITCH,
            name = "Copying Top GUI",
            description = "Enable the ability to copy every item in your top GUI (so when you're in an inventory) using a set keybind, which you can find in your controls menu.\n\nNote: Don't worry about any mods or settings that add additional lore to items. The data is taken directly from NBT, which mods don't affect!",
            category = "Copying Inventories"
    )
    public boolean copyGUI = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Automatic Recipe Format",
            description = "Automatically converts copied recipe GUIs to use the required recipe format.",
            category = "Copying Inventories"
    )
    public boolean recipeMode = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Automatic Forge Recipe Format",
            description = "Automatically converts copied forge recipe GUIs to use the required forge recipe format.",
            category = "Copying Inventories"
    )
    public boolean forgeRecipeMode = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Automatic Shop Menu Format",
            description = "Automatically converts copied shop GUIs to use the required shop menu format.\n\nNote: Shop prices are affected by certain talismans, so make sure to remove them first!",
            category = "Copying Inventories"
    )
    public boolean shopMenuMode = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Copying Raw NBT",
            description = "Toggles whether or not the raw NBT of hovered items can be copied using a set keybind.",
            category = "Copying Raw NBT"
    )
    public boolean rawNbtExtractionEnabled = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Dungeon Stats",
            description = "Toggles whether or not dungeon stats in dark gray should be stripped from item lore.",
            category = "Text Filters"
    )
    public boolean removeDungeonStats = true;

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
            name = "Remove Item Amounts (in names) from Shop Menus",
            description = "When enabled, the \"x64\" (or lower) text will be removed from item names, which can be found in shop menus.",
            category = "Text Filters"
    )
    public boolean removeItemAmountsFromItemNames = true;

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

    public Config() {
        super(new File("./config/wikiwriter.toml"), "WikiWriter", new JVMAnnotationPropertyCollector(), new CustomSortingBehavior());
        initialize();
    }
}