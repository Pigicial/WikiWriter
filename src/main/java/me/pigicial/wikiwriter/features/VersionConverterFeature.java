package me.pigicial.wikiwriter.features;

import me.pigicial.wikiwriter.WikiWriter;
import org.apache.logging.log4j.Logger;

public enum VersionConverterFeature {
    A("button", "oak_button", true),
    B("gold_horse_armor", "golden_horse_armor", true),
    C("end_portal", "end_portal_frame", true),
    D("sign", "oak_sign"),
    E("stained_clay", "terracotta"),
    E_1("hard_clay", "terracotta"),
    F("wool", "white_wool", true),
    G("shrub", "dead_bush", true),
    H("double_tallgrass", "tall_grass", true),
    I("weighted_pressure_plate_(heavy)", "heavy_weighted_pressure_plate", true),
    J("weighted_pressure_plate_(light)", "light_weighted_pressure_plate", true),
    K("wooden_pressure_plate", "oak_pressure_plate", true),
    L("slightly_damaged_anvil", "chipped_anvil", true),
    M("very_damaged_anvil", "broken_anvil", true),
    SPAWN("spawn", "ghast_spawn_egg", true),
    N("wooden_trapdoor", "oak_trapdoor", true),
    O("nether_stalk", "nether_wart"),
    BED("bed", "red_bed"),
    BOAT("boat", "oak_boat"),
    MILK("milk", "milk_bucket", true),
    JACK_O_LANTERN("jack_o'lantern", "jack_olantern"),
    BRICK_SLAB("bricks_slab", "brick_slab"),
    STONE_BRICK_SLAB("stone_bricks_slab", "stone_brick_slab"),
    NETHER_BRICK("nether_brick", "nether_bricks", true),
    PUMPKIN("pumpkin", "carved_pumpkin", true),

    FIREWORK_ROCKET("firework", "firework_rocket", true, true),
    P("bottle_o'_enchanting", "bottle_o_enchanting"),
    RAW_FISH_1("raw_fish:1", "raw_salmon", true),
    RAW_FISH_2("raw_fish:2", "clownfish", true),
    RAW_FISH_3("raw_fish:3", "pufferfish", true),
    COOKED_FISH_1("cooked_fish:1", "cooked_salmon"),
    GOLDEN_APPLE_1("golden_apple:1", "enchanted_golden_apple", true),

    ROSE_RED_1("red_rose", "poppy", true, true),
    ROSE_RED_2("red_rose:1", "blue_orchid", true),
    ROSE_RED_3("red_rose:2", "allium", true),
    ROSE_RED_4("red_rose:3", "azure_bluet", true),
    ROSE_RED_5("red_rose:4", "red_tulip", true),
    ROSE_RED_6("red_rose:5", "orange_tulip", true),
    ROSE_RED_7("red_rose:6", "white_tulip", true),
    ROSE_RED_8("red_rose:7", "pink_tulip", true),
    ROSE_RED_9("red_rose:8", "oxeye_daisy", true),

    INK_SACK_1("ink_sack:1", "rose_red", true),
    INK_SACK_2("ink_sack:2", "cactus_green", true),
    INK_SACK_3("ink_sack:3", "cocoa_beans", true),
    INK_SACK_4("ink_sack:4", "lapis_lazuli", true),
    INK_SACK_5("ink_sack:5", "purple_dye", true),
    INK_SACK_6("ink_sack:6", "cyan_dye", true),
    INK_SACK_7("ink_sack:7", "light_gray_dye", true),
    INK_SACK_8("ink_sack:8", "gray_dye", true),
    INK_SACK_9("ink_sack:9", "pink_dye", true),
    INK_SACK_10("ink_sack:10", "lime_dye", true),
    INK_SACK_11("ink_sack:11", "dandelion_yellow", true),
    INK_SACK_12("ink_sack:12",  "light_blue_dye", true),
    INK_SACK_13("ink_sack:13", "magenta_dye", true),
    INK_SACK_14("ink_sack:14", "orange_dye", true),
    INK_SACK_15("ink_sack:15", "bone_meal", true),
    INK_SACK_16("ink_sack:16", "ink_sac", true),

    LONG_GRASS_1("long_grass:1", "long_grass", true),
    LONG_GRASS_2("long_grass:2", "fern", true),
    DOUBLE_PLANT_1("double_plant", "sunflower", true, true),
    DOUBLE_PLANT_2("double_plant:1", "lilac", true),
    DOUBLE_PLANT_3("double_plant:2", "tall_grass", true),
    DOUBLE_PLANT_4("double_plant:3", "large_fern", true),
    DOUBLE_PLANT_5("double_plant:4", "rose_bush", true),
    DOUBLE_PLANT_6("double_plant:5", "peony", true),
    LOGS_1("log", "oak_log", true, true),
    LOGS_2("log:2", "birch_log", true),
    LOGS_3("log:1", "spruce_log", true),
    LOGS_4("log_2:1", "dark_oak_log", true),
    LOGS_5("log_2", "acacia_log", true, true),
    LOG_6("log:3", "jungle_log", true),
    WOOD_1("planks", "oak_wood_plank", true, true),
    WOOD_2("planks:1", "spruce_wood_plank", true),
    WOOD_3("planks:2", "birch_wood_plank", true),
    WOOD_4("planks:3", "jungle_wood_plank", true),
    WOOD_5("planks:4", "acacia_wood_plank", true),
    WOOD_6("planks:5", "dark_oak_wood_plank", true),

    WOOD_TO_LOG_1("oak_wood", "oak_log", true),
    WOOD_TO_LOG_2("birch_wood", "birch_log", true),
    WOOD_TO_LOG_3("spruce_wood", "spruce_log", true),
    WOOD_TO_LOG_4("dark_oak_wood", "dark_oak_log", true),
    WOOD_TO_LOG_5("jungle_wood", "jungle_log", true),
    WOOD_TO_LOG_6("acacia_wood", "acacia_log", true),

    SAPLING_1("sapling", "oak_sapling", true),
    SAPLING_2("sapling:1", "spruce_sapling", true),
    SAPLING_3("sapling:2", "birch_sapling", true),
    SAPLING_4("sapling:3", "jungle_sapling", true),
    SAPLING_5("sapling:4", "acacia_sapling", true),
    SAPLING_6("sapling:5", "dark_oak_sapling", true),
    RED_SAND("sand:1", "red_sand", true),
    SANDSTONE_1("sandstone:1", "chiseled_sandstone", true),
    SANDSTONE_2("sandstone:2", "smooth_sandstone", true),
    RED_SANDSTONE_1("red_sandstone:1", "red_chiseled_sandstone", true),
    RED_SANDSTONE_2("red_sandstone:2", "red_smooth_sandstone", true),
    DIRT_1("dirt:1", "course_dirt", true),
    DIRT_2("dirt:2", "podzol", true),
    PRISMARINE_1("prismarine:1", "prismarine_bricks", true),
    PRISMARINE_2("prismarine:2", "dark_prismarine", true),

    LEAVES_1("leaves", "oak_leaves", true, true),
    LEAVES_2("leaves:1", "spruce_leaves", true),
    LEAVES_3("leaves:2", "birch_leaves", true),
    LEAVES_4("leaves:3", "jungle_leaves", true),
    LEAVES_5("leaves_2", "acacia_leaves", true, true),
    LEAVES_6("leaves_2:1", "dark_oak_leaves", true),
    // 0 skeleton 1 wither skull 2 zombie 4 creeper
    SKULL_ITEM_1("skull", "skeleton_skull", true, true),
    SKULL_ITEM_2("skull:1", "wither_skeleton_skull", true),
    SKULL_ITEM_3("skull:2", "zombie_head", true),
    SKULL_ITEM_4("skull:4", "creeper_head", true),
    STONE_1("stone:1", "granite", true),
    STONE_2("stone:2", "polished_granite", true),
    STONE_3("stone:3", "diorite", true),
    STONE_4("stone:4", "polished_diorite", true),
    STONE_5("stone:5", "andesite", true),
    STONE_6("stone:6", "polished_andesite", true),
    STEP_1("step", "stone_slab", true, true),
    STEP_2("step:1", "sandstone_slab", true),
    STEP_3("step:3", "cobblestone_slab", true),
    STEP_4("step:4", "brick_slab", true),
    STEP_5("step:5", "stone_brick_slab", true),
    STEP_6("step:6", "nether_brick_slab", true),
    STEP_7("step:7", "quartz_slab", true),
    STEP_8("stone_slab2", "red_sandstone_slab", true, true),
    WOOD_SLAB_1("wood_step", "oak_wood_slab", true, true),
    WOOD_SLAB_2("wood_step:1", "spruce_wood_slab", true),
    WOOD_SLAB_3("wood_step:2", "birch_wood_slab", true),
    WOOD_SLAB_4("wood_step:3", "jungle_wood_slab", true),
    WOOD_SLAB_5("wood_step:4", "acacia_wood_slab", true),
    WOOD_SLAB_6("wood_step:5", "dark_oak_wood_slab", true),
    SMOOTH_BRICK_1("smooth_brick:1", "mossy_stone_bricks", true),
    SMOOTH_BRICK_2("smooth_brick:2", "cracked_stone_bricks", true),
    SMOOTH_BRICK_3("smooth_brick:3", "chiseled_stone_bricks", true),
    WET_SPONGE("sponge:1", "wet_sponge", true),
    STAINED_CLAY_0("stained_clay", "white_hardened_clay", true, true),
    STAINED_CLAY_1("stained_clay:1", "orange_hardened_clay", true),
    STAINED_CLAY_2("stained_clay:2", "magenta_hardened_clay", true),
    STAINED_CLAY_3("stained_clay:3", "light_blue_hardened_clay"),
    STAINED_CLAY_4("stained_clay:4", "yellow_hardened_clay", true),
    STAINED_CLAY_5("stained_clay:5", "lime_hardened_clay", true),
    STAINED_CLAY_6("stained_clay:6", "pink_hardened_clay", true),
    STAINED_CLAY_7("stained_clay:7", "gray_hardened_clay", true),
    STAINED_CLAY_8("stained_clay:8", "light_gray_hardened_clay", true),
    STAINED_CLAY_9("stained_clay:9", "cyan_hardened_clay", true),
    STAINED_CLAY_10("stained_clay:10", "purple_hardened_clay", true),
    STAINED_CLAY_11("stained_clay:11", "blue_hardened_clay", true),
    STAINED_CLAY_12("stained_clay:12", "brown_hardened_clay", true),
    STAINED_CLAY_13("stained_clay:13", "green_hardened_clay", true),
    STAINED_CLAY_14("stained_clay:14", "red_hardened_clay", true),
    STAINED_CLAY_15("stained_clay:15", "black_hardened_clay", true),

    STAINED_GLASS_PANE("stained_glass_pane", "white_stained_glass_pane", true, true),
    STAINED_GLASS_PANE_1("stained_glass_pane:1", "orange_stained_glass_pane", true),
    STAINED_GLASS_PANE_2("stained_glass_pane:2", "magenta_stained_glass_pane", true),
    STAINED_GLASS_PANE_3("stained_glass_pane:3", "light_blue_stained_glass_pane"),
    STAINED_GLASS_PANE_4("stained_glass_pane:4", "yellow_stained_glass_pane", true),
    STAINED_GLASS_PANE_5("stained_glass_pane:5", "lime_stained_glass_pane", true),
    STAINED_GLASS_PANE_6("stained_glass_pane:6", "pink_stained_glass_pane", true),
    STAINED_GLASS_PANE_7("stained_glass_pane:7", "gray_stained_glass_pane", true),
    STAINED_GLASS_PANE_8("stained_glass_pane:8", "light_gray_stained_glass_pane", true),
    STAINED_GLASS_PANE_9("stained_glass_pane:9", "cyan_stained_glass_pane", true),
    STAINED_GLASS_PANE_10("stained_glass_pane:10", "purple_stained_glass_pane", true),
    STAINED_GLASS_PANE_11("stained_glass_pane:11", "blue_stained_glass_pane", true),
    STAINED_GLASS_PANE_12("stained_glass_pane:12", "brown_stained_glass_pane", true),
    STAINED_GLASS_PANE_13("stained_glass_pane:13", "green_stained_glass_pane", true),
    STAINED_GLASS_PANE_14("stained_glass_pane:14", "red_stained_glass_pane", true),
    STAINED_GLASS_PANE_15("stained_glass_pane:15", "black_stained_glass_pane", true),

    STAINED_GLASS("stained_glass", "white_stained_glass", true, true),
    STAINED_GLASS_1("stained_glass:1", "orange_stained_glass", true),
    STAINED_GLASS_2("stained_glass:2", "magenta_stained_glass", true),
    STAINED_GLASS_3("stained_glass:3", "light_blue_stained_glass"),
    STAINED_GLASS_4("stained_glass:4", "yellow_stained_glass", true),
    STAINED_GLASS_5("stained_glass:5", "lime_stained_glass", true),
    STAINED_GLASS_6("stained_glass:6", "pink_stained_glass", true),
    STAINED_GLASS_7("stained_glass:7", "gray_stained_glass", true),
    STAINED_GLASS_8("stained_glass:8", "light_gray_stained_glass", true),
    STAINED_GLASS_9("stained_glass:9", "cyan_stained_glass", true),
    STAINED_GLASS_10("stained_glass:10", "purple_stained_glass", true),
    STAINED_GLASS_11("stained_glass:11", "blue_stained_glass", true),
    STAINED_GLASS_12("stained_glass:12", "brown_stained_glass", true),
    STAINED_GLASS_13("stained_glass:13", "green_stained_glass", true),
    STAINED_GLASS_14("stained_glass:14", "red_stained_glass", true),
    STAINED_GLASS_15("stained_glass:15", "black_stained_glass", true),

    CARPET("carpet", "white_carpet", true, true),
    CARPET_1("carpet:1", "orange_carpet", true),
    CARPET_2("carpet:2", "magenta_carpet", true),
    CARPET_3("carpet:3", "light_blue_carpet"),
    CARPET_4("carpet:4", "yellow_carpet", true),
    CARPET_5("carpet:5", "lime_carpet", true),
    CARPET_6("carpet:6", "pink_carpet", true),
    CARPET_7("carpet:7", "gray_carpet", true),
    CARPET_8("carpet:8", "light_gray_carpet", true),
    CARPET_9("carpet:9", "cyan_carpet", true),
    CARPET_10("carpet:10", "purple_carpet", true),
    CARPET_11("carpet:11", "blue_carpet", true),
    CARPET_12("carpet:12", "brown_carpet", true),
    CARPET_13("carpet:13", "green_carpet", true),
    CARPET_14("carpet:14", "red_carpet", true),
    CARPET_15("carpet:15", "black_carpet", true),

    PLANKS_1("wood", "oak_wood_plank", true, true),
    PLANKS_2("wood:1", "spruce_wood_plank", true),
    PLANKS_3("wood:2", "birch_wood_plank", true),
    PLANKS_4("wood:3", "jungle_wood_plank", true),
    PLANKS_5("wood:4", "acacia_wood_plank", true),
    PLANKS_6("wood:5", "dark_oak_wood_plank", true),

    WOOL("wool", "white_wool", true, true),
    WOOL_1("wool:1", "orange_wool", true),
    WOOL_2("wool:2", "magenta_wool", true),
    WOOL_3("wool:3", "light_blue_wool"),
    WOOL_4("wool:4", "yellow_wool", true),
    WOOL_5("wool:5", "lime_wool", true),
    WOOL_6("wool:6", "pink_wool", true),
    WOOL_7("wool:7", "gray_wool", true),
    WOOL_8("wool:8", "light_gray_wool", true),
    WOOL_9("wool:9", "cyan_wool", true),
    WOOL_10("wool:10", "purple_wool", true),
    WOOL_11("wool:11", "blue_wool", true),
    WOOL_12("wool:12", "brown_wool", true),
    WOOL_13("wool:13", "green_wool", true),
    WOOL_14("wool:14", "red_wool", true),
    WOOL_15("wool:15", "black_wool", true),

    QUARTZ_1("quartz_block:1", "chiseled_quartz_block", true),
    QUARTZ_2("quartz_block:2", "pillar_quartz_block", true);

    private final String key;
    private final String replacement;
    private final boolean exact;
    private final boolean colonCheck;

    VersionConverterFeature(String key, String replacement) {
        this(key, replacement, false);
    }

    VersionConverterFeature(String key, String replacement, boolean exact) {
        this(key, replacement, exact, key.contains(":"));
    }

    VersionConverterFeature(String key, String replacement, boolean exact, boolean colonCheck) {
        this.key = key;
        this.replacement = replacement;
        this.exact = exact;
        this.colonCheck = colonCheck;
    }

    public static String replace(String text, boolean colonCheck) {
        for (VersionConverterFeature feature : values()) {
            if (colonCheck != feature.colonCheck) continue;
            if (feature.exact) {
                if (text.equalsIgnoreCase(feature.key)) {
                    text = feature.replacement;
                }
            } else {
                if (text.contains(feature.key)) {
                    text = text.replace(feature.key, feature.replacement);
                }
            }
        }

        return text;
    }
}
