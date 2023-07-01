package me.pigicial.wikiwriter.utils;

import net.minecraft.util.Formatting;
import org.apache.commons.lang3.text.WordUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum StatType {
    DAMAGE,
    STRENGTH,
    BREAKING_POWER,
    CRIT_CHANCE,
    CRIT_DAMAGE,
    BONUS_ATTACK_SPEED,
    ABILITY_DAMAGE,
    SEA_CREATURE_CHANCE,
    HEALTH,
    DEFENSE,
    SPEED,
    MAGIC_FIND,
    PET_LUCK,
    TRUE_DEFENSE,
    INTELLIGENCE,
    FEROCITY,
    MINING_SPEED,
    MINING_FORTUNE,
    FARMING_FORTUNE,
    FORAGING_FORTUNE,
    PRISTINE;

    private static final Pattern VALUE_PATTERN = Pattern.compile("[+-]?[\\d,.]+%?");
    private final String loreName;

    StatType() {
        this.loreName = WordUtils.capitalize(name().toLowerCase().replace("_", " "));
    }

    public static String generateStats(List<String> lore) {
        StringBuilder builder = new StringBuilder();

        if (lore.isEmpty()) {
            return "";
        }

        lore = lore.stream().map(Formatting::strip).collect(Collectors.toList());

        Set<StatType> foundStatTypes = new HashSet<>();

        loreLoop: for (String s : lore) {
            for (StatType statType : values()) {
                if (foundStatTypes.contains(statType)) continue;
                Matcher matcher;
                if (s.startsWith(statType.loreName + ": ") && (matcher = VALUE_PATTERN.matcher(s.substring(statType.loreName.length() + 2))).find()) {
                    builder.append(builder.length() > 0 ? "<br>" : "").append(statType.loreName).append(" ").append(matcher.group(0));
                    foundStatTypes.add(statType);
                    continue loreLoop;
                }
            }
        }

        return builder.toString();
    }
}
