package me.pigicial.wikiwriter;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.Getter;
import me.pigicial.wikiwriter.config.WikiWriterConfig;
import me.pigicial.wikiwriter.features.CopyItemFeature;
import me.pigicial.wikiwriter.features.GUIStealerFeature;
import me.pigicial.wikiwriter.features.RawNBTExtractorFeature;
import me.pigicial.wikiwriter.features.RecipeTreeFeature;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class WikiWriter implements ModInitializer {

    @Getter
    private static WikiWriter instance;

    private final String messagePrefix;
    private final Logger logger;

    public WikiWriter() {
        this.messagePrefix = Formatting.GRAY + "[" + Formatting.RED + "WikiWriter" + Formatting.GRAY + "]";
        this.logger = LogManager.getLogger(WikiWriter.class);

        WikiWriterConfig.HANDLER.load();
    }

    @Override
    public void onInitialize() {
        instance = this;

        registerCommand();

        new CopyItemFeature(this).register();
        new GUIStealerFeature(this).register();
        new RecipeTreeFeature(this).register();
        new RawNBTExtractorFeature(this).register();

        this.logger.info("WikiWriter loaded.");
    }

    public WikiWriterConfig getConfig() {
        // the instance changes when load() is called, so getting a fresh instance is just for safety
        return WikiWriterConfig.HANDLER.instance();
    }

    private boolean menuQueued = false;

    private void registerCommand() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {

            LiteralArgumentBuilder<FabricClientCommandSource> mainCommand = literal("wikiwriter").executes(context -> {
                // minecraft tries to close the gui on the same tick chat is closed, so
                // if you try and open a gui in a command in the same tick, it won't open - therefore, you have to
                // delay it by a tick
                menuQueued = true;
                return 1;
            });

            dispatcher.register(mainCommand);
            dispatcher.register(literal("ww").executes(mainCommand.getCommand()));
        });

        ClientTickEvents.END_WORLD_TICK.register(world -> {
            if (menuQueued) {
                menuQueued = false;
                // Screen screen = AutoConfig.getConfigScreen(ModConfig.class, MinecraftClient.getInstance().currentScreen).get();

                Screen screen = getConfig().createGui(MinecraftClient.getInstance().currentScreen);
                MinecraftClient.getInstance().setScreen(screen);
            }
        });
    }

    public void sendMessage(String... messages) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        for (String message : messages) {
            player.sendMessage(Component.text(messagePrefix + Formatting.RESET + " " + message));
        }
    }

    public void suggestPageLink(String pageName) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        String editUrl = "https://wiki.hypixel.net/index.php?title=" + pageName + "&action=edit&section=0";
        TextComponent message = getTextComponent(pageName, editUrl);

        player.sendMessage(message);
        if (getConfig().autoOpenSuggestedBrowserEditLinks) {
            if (getConfig().skipConfirmScreenForBrowserEditLinks) {
                Util.getOperatingSystem().open(editUrl);
            } else {
                ConfirmLinkScreen.open(MinecraftClient.getInstance().currentScreen, editUrl);
            }
        }
    }

    @NotNull
    private TextComponent getTextComponent(String pageName, String editUrl) {
        TextComponent hoverText = Component.text("Click to edit ")
                .append(Component.text(pageName).color(NamedTextColor.GRAY));

        return Component.text(messagePrefix)
                .append(Component.text((getConfig().autoOpenSuggestedBrowserEditLinks ? " Opening" : "") + " Suggested Page: "))
                .append(Component.text(pageName).color(NamedTextColor.GRAY)
                        .hoverEvent(HoverEvent.showText(hoverText))
                        .clickEvent(ClickEvent.openUrl(editUrl)));
    }

    public void copyToClipboard(String text) {
        try {
            MinecraftClient.getInstance().keyboard.setClipboard(text);
        } catch (Exception e) {
            sendMessage("Failed to copy message to clipboard");
            e.printStackTrace();
        }
    }
}