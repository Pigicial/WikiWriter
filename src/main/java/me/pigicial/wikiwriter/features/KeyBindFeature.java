package me.pigicial.wikiwriter.features;

import me.pigicial.wikiwriter.WikiWriter;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public abstract class KeyBindFeature implements ClientTickEvents.EndTick {

    protected final WikiWriter wikiWriter;
    private final KeyBinding keyBinding;

    protected KeyBindFeature(WikiWriter wikiWriter, String name, int code) {
        this.wikiWriter = wikiWriter;
        this.keyBinding = new KeyBinding(name, InputUtil.Type.KEYSYM, code, "WikiWriter");
    }

    public void register() {
        KeyBindingHelper.registerKeyBinding(keyBinding);
        ClientTickEvents.END_CLIENT_TICK.register(this);
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        if (keyBinding.wasPressed() && wikiWriter.getConfig().modEnabled) {
            onKeyPress(client);
        }
    }

    protected abstract void onKeyPress(MinecraftClient client);
}
