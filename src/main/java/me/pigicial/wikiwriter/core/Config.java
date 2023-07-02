package me.pigicial.wikiwriter.core;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.JVMAnnotationPropertyCollector;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;

import java.io.File;

public class Config extends Vigilant {

    public static final String configLocation = "./config/wikiwriter.toml";
    @Property(
            type = PropertyType.SWITCH,
            name = "Mod Enabled",
            description = "Global toggle for the features of this mod.",
            category = "General",
            options = {"Enabled", "Disabled"}
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
            type = PropertyType.SELECTOR,
            name = "Item Reference Mode (Copying Items into Single-Slot GUIs)",
            description = "If enabled, when copying items into single-slot menus, their actual item template pages will be referenced (i.e. {{Item_diamond_sword}}), and if they're shop items, their shop lore will be placed at the bottom, if they're not removed (see Text Filters).\n\nNote: This setting does not affect recipe menus copied, those items always try to use this format, assuming their automatic formats are enabled (see Copying Inventories).",
            category = "Copying Items",
            options = {"Always", "When Copying Shop Items", "Never"}
    )
    public int referenceModeForSingleSlotItems = 0;

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
            description = "If enabled, when copying items, their actual item template pages will be referenced (i.e. {{Item_diamond_sword}}), and if they're shop items, their shop lore will be placed at the bottom, if they're not removed (see Text Filters).\n\nNote: This setting does not affect recipe menus copied, those items always try to use this format, assuming their automatic formats are enabled (see below).",
            category = "Copying Inventories",
            options = {"Always", "When Copying Shop Items", "Never"}
    )
    public int modifiedShopItemFormat = 1;

    @Property(
            type = PropertyType.SWITCH,
            name = "Copying Top GUI",
            description = "Enable the ability to copy every item in your top GUI (so when you're in an inventory) using a set keybind, which you can find in your controls menu.\n\nNote: Don't worry about any mods or settings that add additional lore to items. The data is taken directly from NBT, which mods don't affect!",
            category = "Copying Inventories"
    )
    public boolean copyGUI = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Make Copied Inventories Templates",
            description = "When enabled, inventories copied will automatically include the text [[Category:Inventory_Templates]] to mark them as a template.",
            category = "Copying Inventories"
    )
    public boolean copiedInventoriesAreTemplates = true;

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
            name = "Copying Item Stats",
            description = "Toggles whether or not the item stats of hovered items can be copied using a set keybind.",
            category = "Copying Item Stats"
    )
    public boolean copyingItemStatsEnabled = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Pet Levels and Change Pet Name",
            description = "Toggle whether or not pet levels should be removed from pet names, as well as if pets should have the word \"Pet\" included in their name.",
            category = "Text Filters"
    )
    public boolean removePetLevelsAndChangePetName = false;

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
            name = "Remove Auction Data",
            description = "Toggles whether or not auction data should be stripped from item lore.",
            category = "Text Filters",
            subcategory = "Lore Filters"
    )
    public boolean removeAuctionData = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Right Click Notices",
            description = "Toggles whether or not specific text lines that reference the act of right clicking should be stripped from item lore.",
            category = "Text Filters",
            subcategory = "Lore Filters"
    )
    public boolean removeRightClickNotices = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Click to Toggle",
            description = "Toggles whether or not \"Click to toggle\" should be stripped from item lore.",
            category = "Text Filters",
            subcategory = "Lore Filters"
    )
    public boolean removeClickToToggle = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Click to Summon Pet Notice",
            description = "Toggles whether or not \"Click to Summon.\" should be stripped from item lore.",
            category = "Text Filters",
            subcategory = "Lore Filters"
    )
    public boolean removeClickToSummon = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Enchantment Requirement Notices",
            description = "Toggles whether or not specific text lines that reference enchantment requirements should be stripped from item lore.",
            category = "Text Filters",
            subcategory = "Lore Filters"
    )
    public boolean removeEnchantmentRequirementNotices = true;

    @Property(
            type = PropertyType.SELECTOR,
            name = "Remove Shop Buy Text",
            description = "When toggled, lore that mentions clicking to purchase items will be removed when copied.",
            category = "Text Filters",
            subcategory = "Lore Filters",
            options = {"Always", "When Copying Full Inventories", "When Copying Individual Items", "Never"}
    )
    public int removeShopNPCTradeText = 0;

    @Property(
            type = PropertyType.SELECTOR,
            name = "Remove Shop Price",
            description = "When toggled, lore that mentions the prices and requirements of purchasing items will be removed when copied.",
            category = "Text Filters",
            subcategory = "Lore Filters",
            options = {"Always", "When Copying Full Inventories", "When Copying Individual Items", "Never"}
    )
    public int removeShopNPCPriceText = 2;

    @Property(
            type = PropertyType.SELECTOR,
            name = "Remove Fill Quiver Shop Price",
            description = "Toggles whether or not specific text lines that involve the fill quiver shop price items should be stripped from item lore.",
            category = "Text Filters",
            subcategory = "Lore Filters",
            options = {"Always", "When Copying Full Inventories", "When Copying Individual Items", "Never"}
    )
    public int removeFillQuiverShopPrice = 0;

    @Property(
            type = PropertyType.SELECTOR,
            name = "Remove Fill Quiver Notice",
            description = "When toggled, the \"Added directly to your quiver\" text will be removed from Jax's shop menu items",
            category = "Text Filters",
            subcategory = "Lore Filters",
            options = {"Always", "When Copying Full Inventories", "When Copying Individual Items", "Never"}
    )
    public int removeFillQuiverNotice = 2;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Pet Candy",
            description = "Toggles whether or not pet candy on pets should be stripped from item lore.",
            category = "Text Filters",
            subcategory = "Lore Filters"
    )
    public boolean removePetCandy = true;

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
            name = "Remove Fire Sale Data",
            description = "Toggles whether or not fire sale info should be stripped from item lore.\n\nNote: Fire Sales already have additional lore above the item name and lore, and this does not remove that. You'll have to do that manually.",
            category = "Text Filters",
            subcategory = "Lore Filters"
    )
    public boolean removeFireSaleData = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Essence Shop Data",
            description = "Toggles whether or not essence shop info should be stripped from item lore.",
            category = "Text Filters",
            subcategory = "Lore Filters"
    )
    public boolean removeEssenceShopData = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Gemstone Guide Data",
            description = "Toggles whether or not info from the Gemstone Guide menus should be stripped from item lore.",
            category = "Text Filters",
            subcategory = "Lore Filters"
    )
    public boolean removeGemstoneGuideData = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Museum Item Data",
            description = "Toggles whether or not info from the Museum menus should be stripped from item lore.",
            category = "Text Filters",
            subcategory = "Lore Filters"
    )
    public boolean removeMuseumData = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Debug Mode",
            description = "Toggles console logs for certain actions.",
            category = "Other"
    )
    public boolean debugMode = false;

    public Config() {
        super(new File(configLocation), "WikiWriter", new JVMAnnotationPropertyCollector(), new CustomSortingBehavior());
        initialize();
    }
}