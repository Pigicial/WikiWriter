package me.pigicial.wikiwriter.features;

public enum HeadReplacements {
    ACCESSORY_BAG("Accessory Bag", "talisman"),
    POTION_BAG("Potion Bag", "god_potion_2"),
    SACK_OF_SACKS("Sack of Sacks", "sacks"),
    FISHING_BAG("Fishing Bag", "fishing"),
    QUIVER("Quiver", "quiver"),
    PERSONAL_BANK("Personal Bank", "personal_bank_item");

    private final String name;
    private final String idReplacement;

    HeadReplacements(String name, String idReplacement) {
        this.name = name;
        this.idReplacement = idReplacement;
    }

    public static String replaceHeadIDByItemName(String name) {
        for (HeadReplacements replacement : values()) {
            if (replacement.name.equalsIgnoreCase(name)) {
                return replacement.idReplacement;
            }
        }
        return "head";
    }
}
