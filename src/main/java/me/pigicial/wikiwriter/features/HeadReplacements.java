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
    ENRICHMENTS("Enrichments", "special,talisman_enrichment_defense");

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
