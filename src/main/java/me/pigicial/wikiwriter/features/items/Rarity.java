package me.pigicial.wikiwriter.features.items;

import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.*;
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
    COMMON("common", 'f');

    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-ORX]");

    private static final Set<Rarity> REGULAR_ITEM_RARITIES = new HashSet<>(Arrays.asList(Rarity.values()));

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
        return name;
    }

    public static Rarity parseRarity(List<String> lore, String name) {
        Rarity rarity = getRarityFromName(name);
        if (rarity == null) {
            rarity = getRarityFromLore(lore);
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

        return null;
    }

    public static OptionalInt getRarityIndexFromLore(List<String> lore) {
        for (int i = 0, loreSize = lore.size(); i < loreSize; i++) {
            String line = lore.get(i);
            Rarity rarity = getRarityFromLine(line);
            if (rarity != null) {
                return OptionalInt.of(i);
            }
        }

        return OptionalInt.empty();
    }

    @Nullable
    public static Rarity getRarityFromLine(String line) {
        line = Formatting.strip(line);
        if (line == null) {
            return null;
        }

        for (Rarity rarity : REGULAR_ITEM_RARITIES) {
            if (line.contains(rarity.name())) {
                return rarity;
            }
        }

        return null;
    }

    @Nullable
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

        return null;
    }
}
