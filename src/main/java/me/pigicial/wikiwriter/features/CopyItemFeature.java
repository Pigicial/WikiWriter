package me.pigicial.wikiwriter.features;

import me.pigicial.wikiwriter.WikiItem;
import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.core.Config;
import me.pigicial.wikiwriter.utils.Action;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

public class CopyItemFeature extends KeyBindFeature {

    public CopyItemFeature(WikiWriter wikiWriter) {
        super(wikiWriter, "Copy Item", GLFW.GLFW_KEY_H);
    }

    @Override
    protected void onKeyPress(MinecraftClient client) {
        try {
            Config config = wikiWriter.getConfig();
            if (!config.copyItems) {
                return;
            }

            ItemStack itemUnderCursor = getHoveredSlot(client);
            if (itemUnderCursor == null) {
                return;
            }

            WikiItem wikiItem = new WikiItem(null, itemUnderCursor, Action.COPYING_STANDALONE_ITEM, false);
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

    @Nullable
    public ItemStack getHoveredSlot(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return null;
        }

        ScreenHandler currentScreenHandler = player.currentScreenHandler;
        if (currentScreenHandler != null) {
            int mouseX = (int) (client.mouse.getX() * client.getWindow().getScaledWidth() / client.getWindow().getWidth());
            int mouseY = (int) (client.mouse.getY() * client.getWindow().getScaledHeight() / client.getWindow().getHeight());

            Slot hoveredSlot = getHoveredSlot(currentScreenHandler, mouseX, mouseY);
            if (hoveredSlot != null) {
                return hoveredSlot.getStack();
            }
        }

        return null;
    }

    private Slot getHoveredSlot(ScreenHandler screenHandler, int mouseX, int mouseY) {
        for (Slot slot : screenHandler.slots) {
            if (isMouseOverSlot(slot, mouseX, mouseY)) {
                return slot;
            }
        }
        return null;
    }

    private boolean isMouseOverSlot(Slot slot, int mouseX, int mouseY) {
        int slotX = slot.x;
        int slotY = slot.y;
        return mouseX >= slotX && mouseY >= slotY && mouseX < slotX + 16 && mouseY < slotY + 16;
    }
}
