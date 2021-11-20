package me.pigicial.wikiwriter.features;

import gg.essential.universal.UScreen;
import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.core.Config;
import me.pigicial.wikiwriter.utils.Action;
import me.pigicial.wikiwriter.utils.WikiItem;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class CopyItemFeature {
    private final KeyBinding copyLoreKeybind = new KeyBinding("Copy Item", Keyboard.KEY_H, "Wiki Writer");
    private final WikiWriter wikiWriter;

    public CopyItemFeature(WikiWriter wikiWriter) {
        this.wikiWriter = wikiWriter;
        ClientRegistry.registerKeyBinding(copyLoreKeybind);
    }

    @SubscribeEvent
    public void onKey(GuiScreenEvent.KeyboardInputEvent.Post event) {
        try {
            Config config = wikiWriter.getConfig();
            if (!config.modEnabled || !config.copyItems) return;
            if (Keyboard.getEventKey() != copyLoreKeybind.getKeyCode()) return;
            if (!(event.gui instanceof GuiContainer)) return;
            if (Keyboard.getEventKeyState()) return; // only activate on key release

            GuiScreen currentScreen = UScreen.getCurrentScreen();
            if (currentScreen instanceof GuiEditSign || currentScreen instanceof GuiContainerCreative && ((GuiContainerCreative) currentScreen).getSelectedTabIndex() == 5) {
                return;
            }

            Slot slotUnderMouse = ((GuiContainer) event.gui).getSlotUnderMouse();
            if (slotUnderMouse == null) return; // if they press H while outside a slot, don't do anything
            ItemStack stack = slotUnderMouse.getStack();
            if (stack == null) return;

            WikiItem wikiItem = new WikiItem(null, stack, Action.COPYING_STANDALONE_ITEM, false);
            String text = wikiItem.convertToWikiItem();

            if (config.itemTemplatesMode) {
                text = "<noinclude>[[Category:Item UI Templates]]</noinclude><includeonly>\n" + text + "\n</includeonly>";
            }

            wikiWriter.copyToClipboard(text);
            wikiWriter.sendMessage("Copied hovered item to clipboard" + (config.itemTemplatesMode ? " as template item" : "") + ".");
        } catch (Exception e) {
            wikiWriter.sendMessage("Something went wrong when trying to copy this item, please report this with your latest.log file!");
            e.printStackTrace();
        }

    }
}
