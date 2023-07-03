package me.pigicial.wikiwriter.utils;

import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-ORX]");
    private static final String RECOMBOBULATOR_SYMBOL = "#";

    private static final Set<Rarity> REGULAR_ITEM_RARITIES;

    static {
        REGULAR_ITEM_RARITIES = new HashSet<>(Arrays.asList(Rarity.values()));
        REGULAR_ITEM_RARITIES.remove(Rarity.NONE);
    }

    private final String name;
    private final String check;
    private final char code;

    Rarity(String name, char code) {
        this.name = name;
        this.check = "&" + code + "&l" + name().replace("_", " ");
        this.code = code;
    }

    public String getCheck() {
        return check;
    }

    public char getColorCode() {
        return code;
    }

    @Override
    public String toString() {
        return this == NONE ? "" : name;
    }

    public Rarity getPreviousRarity() {
        return switch (this) {
            case COMMON, UNCOMMON -> COMMON;
            case RARE -> UNCOMMON;
            case EPIC -> RARE;
            case LEGENDARY -> EPIC;
            case MYTHIC -> LEGENDARY;
            case DIVINE -> MYTHIC;
            case VERY_SPECIAL -> DIVINE;
            default -> this;
        };
    }

    public static Rarity parseRarity(List<String> lore, String name) {
        Rarity rarity = getRarityFromLore(lore);
        if (rarity == null) {
            rarity = getRarityFromName(name);
        }

        return rarity;
    }

    public static Rarity getRarityFromLore(List<String> lore) {
        for (String line : lore) {
            Rarity rarity = getRarityFromLine(line);
            if (rarity != null) {
                return rarity;
            }
        }

        return Rarity.NONE;
    }

    public static Rarity getRarityFromLine(String line) {
        line = Formatting.strip(line);
        if (line == null) {
            return Rarity.NONE;
        }

        line = line.replace(RECOMBOBULATOR_SYMBOL, "").trim();

        for (Rarity rarity : REGULAR_ITEM_RARITIES) {
            if (line.contains(rarity.name())) {
                return rarity;
            }
        }

        return Rarity.NONE;
    }

    public static Rarity getRarityFromName(String name) {
        Matcher colorCodeMatcher = STRIP_COLOR_PATTERN.matcher(name);

        while (colorCodeMatcher.find()) {
            String match = colorCodeMatcher.group();
            char o = match.charAt(1);

            for (Rarity rarity : REGULAR_ITEM_RARITIES) {
                if (o == rarity.getColorCode()) {
                    return rarity;
                }
            }
        }

        return Rarity.NONE;
    }
}
