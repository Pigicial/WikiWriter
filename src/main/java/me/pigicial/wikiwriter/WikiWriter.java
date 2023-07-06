package me.pigicial.wikiwriter;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import gg.essential.universal.UScreen;
import me.pigicial.wikiwriter.config.Config;
import me.pigicial.wikiwriter.features.CopyItemFeature;
import me.pigicial.wikiwriter.features.GUIStealerFeature;
import me.pigicial.wikiwriter.features.RawNBTExtractorFeature;
import me.pigicial.wikiwriter.features.RecipeTreeFeature;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class WikiWriter implements ModInitializer {
    private static WikiWriter instance;

    public static WikiWriter getInstance() {
        return instance;
    }

    private final String messagePrefix;
    private final Logger logger;
    private final Config config;

    public WikiWriter() {
        this.messagePrefix = Formatting.GRAY + "[" + Formatting.RED + "WikiWriter" + Formatting.GRAY + "]";
        this.logger = LogManager.getLogger(WikiWriter.class);
        this.config = new Config();
    }

    @Override
    public void onInitialize() {
        instance = this;

        config.initialize();

        registerCommand();

        new CopyItemFeature(this).register();
        new GUIStealerFeature(this).register();
        new RecipeTreeFeature(this).register();
        new RawNBTExtractorFeature(this).register();

        this.logger.info("WikiWriter loaded.");
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
                UScreen.displayScreen(config.gui());
            }
        });
    }

    public void sendMessage(String... messages) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        for (String message : messages) {
            player.sendMessage(Text.of(messagePrefix + Formatting.RESET + " " + message));
        }
    }

    public Config getConfig() {
        return config;
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