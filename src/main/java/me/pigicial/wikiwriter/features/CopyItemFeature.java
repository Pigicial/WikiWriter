package me.pigicial.wikiwriter.features;

import me.pigicial.wikiwriter.features.items.WikiItem;
import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.utils.Action;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class CopyItemFeature extends KeyBindFeature {

    public CopyItemFeature(WikiWriter wikiWriter) {
        super(wikiWriter, "Copy Item", GLFW.GLFW_KEY_H);
    }

    @Override
    protected void onKeyPress(MinecraftClient client) {
        ItemStack itemUnderCursor = getHoveredSlot(client);
        if (itemUnderCursor == null) {
            return;
        }

        WikiItem wikiItem = new WikiItem(itemUnderCursor, Action.COPYING_STANDALONE_ITEM);
        String baseText = wikiItem.generateText(Action.COPYING_STANDALONE_ITEM);
        String text = "<noinclude>[[Category:Item UI Templates]]</noinclude><includeonly>\n" + baseText + "\n</includeonly>";

        wikiWriter.copyToClipboard(text);
        wikiWriter.sendMessage("Copied hovered item to clipboard.");
    }

    @Override
    protected void handleException(Exception exception) {
        wikiWriter.sendMessage("Something went wrong when trying to copy this item, please report this with your latest.log file!");
        exception.printStackTrace();
    }
}
