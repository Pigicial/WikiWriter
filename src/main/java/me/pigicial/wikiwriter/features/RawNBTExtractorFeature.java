package me.pigicial.wikiwriter.features;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.JsonOps;
import me.pigicial.wikiwriter.WikiWriter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemStack;
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

        ComponentMap nbt = itemUnderCursor.getComponents();
        if (nbt != null) {
            // Serialize using Mojang's codec if available
            var result = ComponentMap.CODEC.encodeStart(JsonOps.INSTANCE, nbt);
            result.result().ifPresent(json -> {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String prettyJson = gson.toJson(json);
                WikiWriter.getInstance().copyToClipboard(prettyJson);
                wikiWriter.sendMessage("Copied hovered item NBT to clipboard.");
            });
        }
    }

    @Override
    protected void handleException(Exception exception) {
        wikiWriter.sendMessage("Something went wrong when trying to copy this item's NBT, please report this with your latest.log file!");
        exception.printStackTrace();
    }
}
