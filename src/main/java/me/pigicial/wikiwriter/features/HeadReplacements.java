package me.pigicial.wikiwriter.features;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public enum HeadReplacements {
    ACCESSORY_BAG("Accessory Bag", "talisman"),
    POTION_BAG("Potion Bag", "god_potion_2"),
    SACK_OF_SACKS("Sack of Sacks", "sacks"),
    FISHING_BAG("Fishing Bag", "fishing"),
    QUIVER("Quiver", "quiver"),
    PERSONAL_BANK("Personal Bank", "personal_bank_item"),
    ACESSORIES("Accessories", "emerald_ring"),
    BONZO("Bonzo", "bonzo_mask"),
    THORN("Thorn", "ghast_head"),
    NECRON("Necron", "wither_skeleton_skull", false),
    ENRICHMENTS("Enrichments", "special,talisman_enrichment_defense"),

    STOOL("Stool", "stool"),
    COFFEE_TABLE("Coffee Table", "coffee_table"),
    DINING_CHAIR("Dining Chair", "dining_chair"),
    DINING_TABLE("Dining Table", "dining_table"),
    MINION_CHAIR("Minion Chair", "minion_chair"),
    SKYBLOCK_CHAIR("Dark Oak Chair", "skyblock_chair"),
    FANCY_FLOWER_POT("Flower Pot", "fancy_flower_pot"),
    SKYBLOCK_BENCH("Dark Oak Bench", "skyblock_bench"),
    SKYBLOCK_TABLE("Dark Oak Table", "skyblock_table"),
    ARMOR_SHOWCASE("Armor Stand", "armor_showcase"),
    SCARECROW("Scarecrow", "scarecrow"),
    DESK("Desk", "desk"),
    BOOKCASE("Bookcase", "bookcase"),
    SMALL_SHELVES("Small Shelves", "small_shelves"),
    WEAPON_RACK("Weapon Rack", "weapon_rack"),
    CAMPFIRE("Fire Pit", "campfire"),
    TIKI_TORCH("Tiki Torch", "tiki_torch"),
    FIREPLACE("Fireplace", "fireplace"),
    FURNACE_PLUS("Furnace+", "furnace_plus"),
    CHEST_SHELVES("Chest Storage", "chest_shelves"),
    BEST_WEAPON_RACK("Weapon Rack+", "best_weapon_rack"),
    HAY_BED("Hay Bed", "hay_bed"),
    LARGE_BED("Large Bed", "large_bed"),
    WATER_TROUGH("Water Trough", "water_trough"),
    FOOD_TROUGH("Food Trough", "food_trough"),
    MEDIUM_SHELVES("Medium Shelves", "medium_shelves"),
    CRAFTING_PLUS("Crafting Table+", "crafting_plus"),
    WOOD_CHEST("Wood Chest+", "wood_chest"),
    DIAMOND_CHEST("Diamond Chest+", "diamond_chest"),
    EMERALD_CHEST("Emerald Chest+", "emerald_chest"),
    IRON_CHEST("Iron Chest+", "iron_chest"),
    GOLD_CHEST("Gold Chest+", "gold_chest"),
    LAPIS_CHEST("Lapis Chest+", "lapis_chest"),
    REDSTONE_CHEST("Redstone Chest+", "redstone_chest"),
    ENDER_PLUS("Ender Chest+", "ender_plus"),
    ENDSTONE_CHEST("Endstone Chest+", "endstone_chest"),
    BREWING_PLUS("Brewing+", "brewing_plus"),
    ENCHANTING_PLUS("Enchanting Table+", "enchanting_plus"),
    BLACKSMITH_PLUS("Blacksmith+", "blacksmith_plus"),
    SKULL_CHEST("Skull Chest++", "skull_chest"),
    GRANDFATHER_CLOCK("Grandfather Clock", "grandfather_clock");

    private final String name;
    private final String idReplacement;
    private final boolean turnToSbItem;

    HeadReplacements(String name, String idReplacement) {
        this(name, idReplacement, true);
    }

    HeadReplacements(String name, String idReplacement, boolean turnToSbItem) {
        this.name = name;
        this.idReplacement = idReplacement;
        this.turnToSbItem = turnToSbItem;
    }

    @Nullable
    public static HeadReplacements replaceHeadIDByItemName(String name) {
        for (HeadReplacements replacement : values()) {
            if (replacement.name.equalsIgnoreCase(name) || name.toLowerCase().startsWith(replacement.name.toLowerCase())) {
                return replacement;
            }
        }
        return null;
    }
}
