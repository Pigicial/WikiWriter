package me.pigicial.wikiwriter.features;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ColorReplacementFeature {
    UNKNOWN(" ", "", "", false),
    RESET("&r", "", "", true),
    BLACK("&0", "&0", ""),
    DARK_BLUE("&1", "&1", ""),
    DARK_GREEN("&2", "&2", ""),
    DARK_AQUA("&3", "&3", ""),
    DARK_RED("&4", "&4", ""),
    DARK_PURPLE("&5", "&5", ""),
    GOLD("&6", "&6", ""),
    GRAY("&7", "&7", ""),
    DARK_GRAY("&8", "&8", ""),
    BLUE("&9", "&9", ""),
    GREEN("&a", "&a", ""),
    AQUA("&b", "&b", ""),
    RED("&c", "&c", ""),
    LIGHT_PURPLE("&d", "&d", ""),
    YELLOW("&e", "&e", ""),
    WHITE("&f", "&f", ""),
    BOLD("&l", "'''", "'''", false),
    UNDERLINE("&n", "<ins>", "</ins>", false),
    ITALICS("&o", "''", "''", false),
    OBFUSCATION("&k", "", "", false),
    STRIKETHROUGH("&m", "<s>", "</s>", false);

    public static final Pattern STRIPPED_COLOR_PATTERN = Pattern.compile("(?i)&[0-9A-FK-ORX]");

    private final String key;
    private final String start;
    private final String end;
    private final boolean reset;
    private final boolean addSpaces;

    ColorReplacementFeature(String key, String start, String end) {
        this(key, start, end, true);
    }

    ColorReplacementFeature(String key, String start, String end, boolean reset) {
        this.key = key;
        this.start = start;
        this.end = end;
        this.reset = reset;
        this.addSpaces = start.equals("'''") || start.equals("''");
    }

    public static String replace(String text) {
        text = text.replace('§', '&');
        List<ReplacementSection> replacementSections = getReplacementSections(text);
        if (replacementSections.size() == 0) {
            return text;
        }
        List<ReplacementSection> currentlyAppliedSections = new LinkedList<>();

        StringBuilder newString = new StringBuilder();
        int lastEnd = 0;

        for (ReplacementSection replacementSection : replacementSections) {
            int start = replacementSection.getStart();
            int end = replacementSection.getEnd();

            ColorReplacementFeature feature = replacementSection.getFeature();
            newString.append(text, lastEnd, start);

            if (feature.reset) {
                for (ReplacementSection currentlyAppliedSection : currentlyAppliedSections) {
                    newString.append(currentlyAppliedSection.getFeature().end);
                }

                currentlyAppliedSections.clear();
            }

            newString.append(feature.start).append(lastEnd == start && feature.addSpaces ? " " : "");

            currentlyAppliedSections.add(replacementSection);
            lastEnd = end;
        }

        newString.append(text.substring(lastEnd));

        for (ReplacementSection currentlyAppliedSection : currentlyAppliedSections) {
            newString.append(currentlyAppliedSection.getFeature().end);
        }

        return newString.toString();
    }

    private static List<ReplacementSection> getReplacementSections(String text) {
        Matcher matcher = STRIPPED_COLOR_PATTERN.matcher(text);
        List<ReplacementSection> sections = new LinkedList<>();
        while (matcher.find()) {
            String check = matcher.group();
            ReplacementSection replacementSection = byCode(check, matcher.start(), matcher.end());

            if (replacementSection.getFeature() != UNKNOWN) {
                sections.add(replacementSection);

                // remove unknown characters, basically
                //text = text.substring(0, matcher.start()) + text.substring(matcher.end());
                //matcher = stripColorPattern.matcher(text);
            }// else {
             //   sections.add(replacementSection);
            //}
        }

        return sections;
    }

    public static ReplacementSection byCode(String key, int start, int end) {
        for (ColorReplacementFeature feature : values()) {
            if (feature.key.equals(key)) {
                return new ReplacementSection(feature, start, end);
            }
        }

        return new ReplacementSection(UNKNOWN, start, end);
    }

    @Data
    private static class ReplacementSection {
        private final ColorReplacementFeature feature;
        private final int start;
        private final int end;
    }

}
