package me.pigicial.wikiwriter.config;

import com.google.gson.FieldNamingPolicy;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Objects;

@Getter
public class WikiWriterConfig {

    public static final ConfigClassHandler<WikiWriterConfig> HANDLER = ConfigClassHandler.createBuilder(WikiWriterConfig.class)
            .id(new Identifier("wikiwriter", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("wikiwriter.json"))
                    .appendGsonBuilder(builder -> builder
                            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                            .registerTypeHierarchyAdapter(Identifier.class, new Identifier.Serializer()))
                    .setJson5(false)
                    .build())
            .build();

    public Screen createGui(Screen parent) {

        YetAnotherConfigLib gui = HANDLER.generateGui();
        return YetAnotherConfigLib.create(HANDLER, (defaults, config, builder) -> builder
                        .title(gui.title())
                        .categories(gui.categories())
                        .save(() -> {
                            gui.saveFunction();
                            MinecraftClient.getInstance().setScreen(parent);
                        }))
                .generateScreen(parent);


       // return HANDLER.generateGui().generateScreen(parent);
    }

    @AutoGen(category = "general", group = "general")
    @MasterTickBox(value = {
            "setAmountsToOne",
            "disableClicking",
            "menuReferenceModeScenario",
            "shopMenuMode",
            "removeDungeonStats",
            "removeTextBelowRarityWhenCopyingItems",
            "removeTextBelowRarityWhenCopyingMenus",
            "removeClickNotices",
            "removeShopNPCPriceText",
            "removeShopNPCStockText",
            "removeShopNPCTradeText",
            "removePickaxeAbilities",
            "removePetItems",
            "autoOpenSuggestedBrowserEditLinks",
            "skipConfirmScreenForBrowserEditLinks"
    })
    @SerialEntry
    public boolean modEnabled = true;

    @AutoGen(category = "general", group = "copyingItems")
    @Boolean(colored = true)
    @SerialEntry
    public boolean setAmountsToOne = true;

    @AutoGen(category = "general", group = "copyingItems")
    @Boolean(colored = true)
    @SerialEntry
    public boolean disableClicking = true;

    @AutoGen(category = "general", group = "copyingInventories")
    @EnumCycler
    @SerialEntry
    public ReferenceModeScenario menuReferenceModeScenario = ReferenceModeScenario.WHEN_COPYING_SHOP_ITEMS;

    @AutoGen(category = "general", group = "copyingInventories")
    @Boolean(colored = true)
    @SerialEntry
    public boolean shopMenuMode = true;

    @AutoGen(category = "general", group = "textFilters")
    @Boolean(colored = true)
    @SerialEntry
    public boolean removeDungeonStats = true;

    @AutoGen(category = "general", group = "textFilters")
    @Boolean(colored = true)
    @SerialEntry
    public boolean removeTextBelowRarityWhenCopyingItems = true;

    @AutoGen(category = "general", group = "textFilters")
    @Boolean(colored = true)
    @SerialEntry
    public boolean removeTextBelowRarityWhenCopyingMenus = false;

    @AutoGen(category = "general", group = "textFilters")
    @Boolean(colored = true)
    @SerialEntry
    public boolean removeClickNotices = false;

    @AutoGen(category = "general", group = "textFilters")
    @Boolean(colored = true)
    @SerialEntry
    public boolean removeShopNPCPriceText = false;

    @AutoGen(category = "general", group = "textFilters")
    @Boolean(colored = true)
    @SerialEntry
    public boolean removeShopNPCStockText = true;

    @AutoGen(category = "general", group = "textFilters")
    @Boolean(colored = true)
    @SerialEntry
    public boolean removeShopNPCTradeText = true;

    @AutoGen(category = "general", group = "textFilters")
    @Boolean(colored = true)
    @SerialEntry
    public boolean removePickaxeAbilities = true;

    @AutoGen(category = "general", group = "textFilters")
    @Boolean(colored = true)
    @SerialEntry
    public boolean removePetItems = true;

    @AutoGen(category = "general", group = "links")
    @MasterTickBox(value = {
            "skipConfirmScreenForBrowserEditLinks"
    })
    @Boolean
    @SerialEntry
    public boolean autoOpenSuggestedBrowserEditLinks = false;

    @AutoGen(category = "general", group = "links")
    @Boolean(colored = true)
    @SerialEntry
    public boolean skipConfirmScreenForBrowserEditLinks = true;

    public enum ReferenceModeScenario implements NameableEnum {
        ALWAYS("Always", Formatting.GREEN),
        WHEN_COPYING_SHOP_ITEMS("When Copying Shop Items", Formatting.YELLOW),
        @SuppressWarnings("unused")
        NEVER("Never", Formatting.RED);

        private final Text text;

        ReferenceModeScenario(String text, Formatting color) {
            this.text = Text.literal(text).withColor(Objects.requireNonNull(color.getColorValue()));
        }

        @Override
        public Text getDisplayName() {
            return text;
        }
    }
}
