package com.antonio32a.examplemod;

import com.antonio32a.examplemod.commands.ExampleCommand;
import com.antonio32a.examplemod.core.Config;
import lombok.Getter;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = ExampleMod.MODID,
    version = ExampleMod.VERSION,
    name = ExampleMod.NAME,
    clientSideOnly = true
)
public class ExampleMod {
    public static final String NAME = "ExampleMod";
    public static final String MODID = "examplemod";
    public static final String VERSION = "1.0";
    public static final String configLocation = "./config/examplemod.toml";

    @Getter private static ExampleMod instance;
    @Getter private final Logger logger;
    @Getter private final Config config;

    public ExampleMod() {
        instance = this;
        logger = LogManager.getLogger();
        config = new Config();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        config.preload();
        new ExampleCommand().register();
        this.logger.info("ExampleMod loaded.");
    }
}