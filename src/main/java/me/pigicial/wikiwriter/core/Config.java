package me.pigicial.wikiwriter.core;

import com.sun.org.apache.bcel.internal.generic.SWITCH;
import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.JVMAnnotationPropertyCollector;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;
import me.pigicial.wikiwriter.WikiWriter;

import java.io.File;

public class Config extends Vigilant {

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
            description = "Enable the ability to copy hovered items using a set keybind, which you can find in your controls menu.\n\nNote: Don't worry about any mods or settings that add additional lore to items. The data is taken directly from NBT, which mods don't affect!",
            category = "Copying Items"
    )
    public boolean copyItems = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Item Templates Mode",
            description = "When enabled, individually copied items will include the text <noinclude>[[Category:Item UI Templates]]</noinclude><includeonly> and </includeonly> to allow them to be used as item templates.",
            category = "Copying Items"
    )
    public boolean itemTemplatesMode = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Guaranteed Stack Size Toggle",
            description = "Toggle whether or not items copied should have their stack sizes forcefully set, up to their vanilla stack size limit.",
            category = "Copying Items",
            subcategory = "Changing Item Amounts"
    )
    public boolean guaranteedStackSizeToggled = false;

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
            name = "Recipe Mode",
            description = "Automatically converts copied recipe GUIs to use the required recipe format.", // When copying recipe GUIs, the bottom Go Back arrow and Close barrier will simply be replaced with a lore-less arrow, which is required for recipes.
            category = "Copying Inventories"
    )
    public boolean recipeMode = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Pet Levels and Change Pet Name",
            description = "Toggle whether or not pet levels should be removed from pet names, as well as if pets should have the word \"Pet\" included in their name.",
            category = "Text Filters"
    )
    public boolean updatePetName = false;

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
            name = "Remove Enchantment Requirement Notices",
            description = "Toggles whether or not specific text lines that reference enchantment requirements should be stripped from item lore.",
            category = "Text Filters",
            subcategory = "Lore Filters"
    )
    public boolean removeEnchantmentRequirementNotices = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Remove Shop Notices",
            description = "Toggles whether or not specific text lines that purchasing shop/trade items as well as the requirements to do so should be stripped from item lore.",
            category = "Text Filters",
            subcategory = "Lore Filters"
    )
    public boolean removeShopNPCTradeText = true;

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


    public Config() {
        super(new File(WikiWriter.configLocation), "WikiWriter", new JVMAnnotationPropertyCollector(), new CustomSortingBehavior());
        initialize();
    }
}