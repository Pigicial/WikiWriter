package me.pigicial.wikiwriter.features;

import me.pigicial.wikiwriter.WikiWriter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.lwjgl.glfw.GLFW;

public class RawNBTExtractorFeature extends KeyBindFeature {

    public RawNBTExtractorFeature(WikiWriter wikiWriter) {
        super(wikiWriter, "Copy NBT", GLFW.GLFW_KEY_J);
    }

    @Override
    protected void onKeyPress(MinecraftClient client) {
        ItemStack itemUnderCursor = getHoveredSlot(client);
        if (itemUnderCursor == null) {
            return;
        }

        NbtCompound nbt = itemUnderCursor.getNbt();
        if (nbt != null) {
            WikiWriter.getInstance().copyToClipboard(nbt.toString());
            wikiWriter.sendMessage("Copied hovered item NBT to clipboard.");
        }
    }

    @Override
    protected void handleException(Exception exception) {
        wikiWriter.sendMessage("Something went wrong when trying to copy this item's NBT, please report this with your latest.log file!");
        exception.printStackTrace();
    }
}
