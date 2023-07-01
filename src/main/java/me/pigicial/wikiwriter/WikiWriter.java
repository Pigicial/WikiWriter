package me.pigicial.wikiwriter;

import gg.essential.api.EssentialAPI;
import me.pigicial.wikiwriter.commands.MainCommand;
import me.pigicial.wikiwriter.core.Config;
import me.pigicial.wikiwriter.features.CopyItemFeature;
import me.pigicial.wikiwriter.features.GUIStealerFeature;
import me.pigicial.wikiwriter.features.KeyBindFeature;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;

public class WikiWriter implements ModInitializer {
    private static WikiWriter instance;

    public static WikiWriter getInstance() {
        return instance;
    }

    private final String messagePrefix;
    private final Logger logger;
    private final Config config;

    public WikiWriter() {
        WikiWriter.instance = this;
        this.messagePrefix = Formatting.GRAY + "[" + Formatting.RED + "WikiWriter" + Formatting.GRAY + "]";
        this.logger = LogManager.getLogger(WikiWriter.class);
        this.config = new Config();
    }

    @Override
    public void onInitialize() {
        config.preload();
        new MainCommand().register();

        List<? extends KeyBindFeature> features = List.of(
                new CopyItemFeature(this),
                new GUIStealerFeature(this)
        );

        for (KeyBindFeature feature : features) {
            feature.register();
        }

        // eventBus.register(new RawNBTExtractor(this));

        this.logger.info("WikiWriter loaded.");
    }

    public void sendMessage(String... messages) {
        for (String message : messages) {
            EssentialAPI.getMinecraftUtil().sendMessage(messagePrefix + Formatting.RESET + " ", message);
        }
    }

    public Config getConfig() {
        return config;
    }

    public void copyToClipboard(String text) {
        try {
            StringSelection selection = new StringSelection(text);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        } catch (Exception e) {
            sendMessage("Failed to copy message to clipboard");
            e.printStackTrace();
        }
    }
}