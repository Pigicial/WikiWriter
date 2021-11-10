package me.pigicial.wikiwriter;

import gg.essential.api.EssentialAPI;
import me.pigicial.wikiwriter.commands.MainCommand;
import me.pigicial.wikiwriter.core.Config;
import lombok.Getter;
import me.pigicial.wikiwriter.core.LoginNotifications;
import me.pigicial.wikiwriter.features.CopyLoreFeature;
import me.pigicial.wikiwriter.features.GUIStealerFeature;
import me.pigicial.wikiwriter.features.RawNBTExtractor;
import me.pigicial.wikiwriter.features.StatGenerationFeature;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

@Mod(
    modid = WikiWriter.MODID,
    version = WikiWriter.VERSION,
    name = WikiWriter.NAME,
    clientSideOnly = true
)
public class WikiWriter {
    public static final String NAME = "WikiWriter";
    public static final String MODID = "wikiwriter";
    public static final String VERSION = "1.7.2";
    public static final String configLocation = "./config/wikiwriter.toml";

    @Getter private static WikiWriter instance;
    @Getter private final Logger logger;
    @Getter private final Config config;

    public WikiWriter() {
        instance = this;
        logger = LogManager.getLogger();
        config = new Config();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        EventBus eventBus = MinecraftForge.EVENT_BUS;
        eventBus.register(this);
        config.preload();
        new MainCommand().register();
        eventBus.register(new CopyLoreFeature(this));
        eventBus.register(new GUIStealerFeature(this));
        eventBus.register(new RawNBTExtractor(this));
        eventBus.register(new StatGenerationFeature(this));

        this.logger.info("WikiWriter loaded.");
        LoginNotifications.sendLoginNotification();
    }

    public void sendMessage(String... messages) {
        for (String message : messages) {
            EssentialAPI.getMinecraftUtil().sendMessage(EnumChatFormatting.GRAY + "[" + EnumChatFormatting.RED + "WikiWriter"
                    + EnumChatFormatting.GRAY + "]" + EnumChatFormatting.RESET + " ", message);
        }
    }

    public void debug(String... messages) {
        if (config.debugMode) {
            for (String s : messages) {
                logger.info("[DEBUG] " + s);
            }
        }
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