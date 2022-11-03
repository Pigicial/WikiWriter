package me.pigicial.wikiwriter.utils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Rarity {
    VERY_SPECIAL("veryspecial", 'c'),
    SPECIAL("special", 'c'),
    DIVINE("divine", 'b'),
    MYTHIC("mythic", 'd'),
    LEGENDARY("legendary", '6'),
    EPIC("epic", '5'),
    RARE("rare", '9'),
    UNCOMMON("uncommon", 'a'),
    COMMON("common", 'f'),
    NONE("", ' ');

    public static final Set<Character> COLOR_CODES = Arrays.stream(values()).filter(rarity -> rarity != NONE).map(Rarity::getColorCode).collect(Collectors.toSet());

    private final String name;
    private final char code;

    Rarity(String name, char code) {
        this.name = name;
        this.code = code;
    }

    public char getColorCode() {
        return code;
    }

    @Override
    public String toString() {
        return this == NONE ? "" : name;
    }

    public Rarity getPreviousRarity() {
        switch (this) {
            case COMMON:
            case UNCOMMON:
                return COMMON;
            case RARE: return UNCOMMON;
            case EPIC: return RARE;
            case LEGENDARY: return EPIC;
            case MYTHIC: return LEGENDARY;
            case DIVINE: return MYTHIC;
            case VERY_SPECIAL: return DIVINE;
            default: return this;

        }
    }
}
