package me.pigicial.wikiwriter.features;

import me.pigicial.wikiwriter.WikiWriter;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public abstract class KeyBindFeature implements ScreenKeyboardEvents.AfterKeyPress {

    protected final WikiWriter wikiWriter;
    private final KeyBinding keyBinding;

    protected KeyBindFeature(WikiWriter wikiWriter, String name, int code) {
        this.wikiWriter = wikiWriter;
        this.keyBinding = new KeyBinding(name, InputUtil.Type.KEYSYM, code, "WikiWriter");
    }

    public void register() {
        KeyBindingHelper.registerKeyBinding(keyBinding);

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight)
                -> ScreenKeyboardEvents.afterKeyPress(screen).register(this));
    }

    @Override
    public void afterKeyPress(Screen screen, int key, int scancode, int modifiers) {
        int currentlyBoundCode = KeyBindingHelper.getBoundKeyOf(keyBinding).getCode();
        if (key == currentlyBoundCode && wikiWriter.getConfig().modEnabled) {
            onKeyPress(MinecraftClient.getInstance());
        }
    }

    protected abstract void onKeyPress(MinecraftClient client);
}
