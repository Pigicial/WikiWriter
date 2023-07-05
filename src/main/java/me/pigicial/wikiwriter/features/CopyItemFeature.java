package me.pigicial.wikiwriter.features;

import me.pigicial.wikiwriter.WikiItem;
import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.config.Config;
import me.pigicial.wikiwriter.mixins.FocusedSlotAccessor;
import me.pigicial.wikiwriter.utils.Action;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

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

            WikiItem wikiItem = new WikiItem(itemUnderCursor, Action.COPYING_STANDALONE_ITEM, false);
            String text = wikiItem.generateText(Action.COPYING_STANDALONE_ITEM);

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
