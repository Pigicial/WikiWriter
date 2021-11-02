package me.pigicial.wikiwriter.core;

import gg.essential.vigilance.Vigilant;
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
            name = "Copying Top GUI",
            description = "Enable the ability to copy every item in your top GUI (so when you're in an inventory) using a set keybind, which you can find in your controls menu.\n\nNote: Don't worry about any mods or settings that add additional lore to items. The data is taken directly from NBT, which mods don't affect!",
            category = "Copying Inventories"
    )
    public boolean copyGUI = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Make Copied Inventories Templates",
            description = "When enabled, inventories copied will automatically include the text [[Category:Inventory_Templates]] to mark them as a template.",
            category = "Copying Inventories"
    )
    public boolean copiedInventoriesAreTemplates = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Guaranteed Stack Size Toggle (WIP)",
            description = "Toggle whether or not items copied should have their stack sizes forcefully set, up to their vanilla stack size limit.",
            category = "Copying Items"
    )
    public boolean guaranteedStackSizeToggled = false;

    @Property(
            type = PropertyType.NUMBER,
            name = "Guaranteed Stack Size (WIP)",
            description = "The guaranteed stack size of items. Items will not go higher than their vanilla stack size limit.",
            category = "Copying Items",
            min = 1,
            max = 64
    )
    public int setStackSize = 1;

    @Property(
            type = PropertyType.SWITCH,
            name = "Vanilla Stack Size Limit Bypass (WIP)",
            description = "Toggle whether or not items copied can have their stack sizes be forcefully set to above their stack size limit. To be honest I don't know why you would need this but hey it's an option!",
            category = "Copying Items"
    )
    public boolean allowHigherThanVanillaStackSizes = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Remove Auction Data",
            description = "Toggles whether or not auction data should be stripped from item lore.",
            category = "Lore Filters"
    )
    public boolean removeAuctionData = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Remove Right Click Notices",
            description = "Toggles whether or not specific text lines that reference the act of right clicking should be stripped from item lore.",
            category = "Lore Filters"
    )
    public boolean removeRightClickNotices = true;

    public Config() {
        super(new File(WikiWriter.configLocation));
        initialize();
    }
}