package me.pigicial.wikiwriter.features;

import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.core.Config;
import me.pigicial.wikiwriter.utils.WikiItem;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class CopyLoreFeature {
    private final KeyBinding copyLoreKeybind = new KeyBinding("Copy Lore", Keyboard.KEY_H, "Wiki Writer");
    private final WikiWriter wikiWriter;

    public CopyLoreFeature(WikiWriter wikiWriter) {
        this.wikiWriter = wikiWriter;
        ClientRegistry.registerKeyBinding(copyLoreKeybind);
    }

    @SubscribeEvent
    public void onKey(GuiScreenEvent.KeyboardInputEvent.Post event) {
        Config config = wikiWriter.getConfig();
        if (!config.modEnabled || !config.copyItems) return;
        if (Keyboard.getEventKey() != copyLoreKeybind.getKeyCode()) return;
        if (!(event.gui instanceof GuiContainer)) return;
        if (Keyboard.getEventKeyState()) return; // only activate on key release

        Slot slotUnderMouse = ((GuiContainer) event.gui).getSlotUnderMouse();
        if (slotUnderMouse == null) return; // if they press H while outside a slot, don't do anything
        ItemStack stack = slotUnderMouse.getStack();
        if (stack == null) return;

        WikiItem wikiItem = WikiItem.fromItemStack(stack);
        wikiWriter.copyToClipboard(wikiItem.convertToWikiItem());
        wikiWriter.sendMessage("Copied hovered item to clipboard.");

    }
}
