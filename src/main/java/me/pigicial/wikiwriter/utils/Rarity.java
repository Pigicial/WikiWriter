package me.pigicial.wikiwriter.utils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum Rarity {
    NONE(' '),
    COMMON('f'),
    UNCOMMON('a'),
    RARE('9'),
    EPIC('5'),
    LEGENDARY('6'),
    MYTHIC('d'),
    DIVINE('b'),
    SPECIAL('c'),
    VERY_SPECIAL('c');

    public static final Set<Character> COLOR_CODES = Arrays.stream(values()).filter(rarity -> rarity != NONE).map(Rarity::getColorCode).collect(Collectors.toSet());

    private final char code;

    Rarity(char code) {
        this.code = code;
    }

    public char getColorCode() {
        return code;
    }

    @Override
    public String toString() {
        return this == NONE ? "" : name();
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
