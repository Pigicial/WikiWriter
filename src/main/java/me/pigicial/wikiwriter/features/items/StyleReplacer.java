package me.pigicial.wikiwriter.features.items;

import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StyleReplacer {

    public static final Pattern STRIPPED_COLOR_PATTERN = Pattern.compile("(?i)&[0-9A-FK-ORX]");

    private static final Style[] STYLE_VALUES = Style.values();

    public static String replace(String text) {
        text = text.replace('§', '&');
        List<ReplacementSection> replacementSections = getReplacementSections(text);
        if (replacementSections.isEmpty()) {
            return text;
        }

        text = placeInitialSpacingAfterStyling(text);

        List<ReplacementSection> currentlyAppliedSections = new LinkedList<>();
        StringBuilder newString = new StringBuilder();
        int lastEnd = 0;

        for (ReplacementSection replacementSection : replacementSections) {
            int start = replacementSection.start();
            int end = replacementSection.end();

            Style feature = replacementSection.feature();
            newString.append(text, lastEnd, start);

            if (feature.reset) {
                for (ReplacementSection currentlyAppliedSection : currentlyAppliedSections) {
                    newString.append(currentlyAppliedSection.feature().end);
                }

                currentlyAppliedSections.clear();
            }

            newString.append(feature.start).append(lastEnd == start && feature.addSpaces ? " " : "");

            currentlyAppliedSections.add(replacementSection);
            lastEnd = end;
        }

        newString.append(text.substring(lastEnd));

        for (ReplacementSection currentlyAppliedSection : currentlyAppliedSections) {
            newString.append(currentlyAppliedSection.feature().end);
        }

        return newString.toString();
    }

    // For lore on wiki, if there are spaces at the start of a line and there's no style applied
    private static String placeInitialSpacingAfterStyling(String text) {
        if (!text.startsWith(" ")) {
            return text;
        }

        char[] charArray = text.toCharArray();
        boolean isStyle = false;
        boolean hasHadStyleBefore = false;
        int amountOfSpaces = 0;

        for (int index = 0, charArrayLength = charArray.length; index < charArrayLength; index++) {
            char character = charArray[index];
            if (Character.isSpaceChar(character)) {
                amountOfSpaces++;
                continue;
            }

            if (character == '&') {
                isStyle = true;
                continue;
            }

            if (isStyle) {
                isStyle = false;
                hasHadStyleBefore = true;
                continue;
            }

            // has had style before, not in style now, and not a space character
            if (hasHadStyleBefore) {
                if (amountOfSpaces == 0) {
                    // no spaces detected
                    return text;
                }

                String styles = text.substring(0, index).replace(" ", "");
                String spaceText = " ".repeat(amountOfSpaces);
                String textAfterStyles = text.substring(index);

                return styles + spaceText + textAfterStyles;
            }
        }

        return text;
    }

    private static List<ReplacementSection> getReplacementSections(String text) {
        Matcher matcher = STRIPPED_COLOR_PATTERN.matcher(text);
        List<ReplacementSection> sections = new LinkedList<>();

        while (matcher.find()) {
            String check = matcher.group();
            Style style = getStyleByCode(check);

            if (style != null) {
                sections.add(new ReplacementSection(style, matcher.start(), matcher.end()));
            }
        }

        return sections;
    }

    @Nullable
    private static StyleReplacer.Style getStyleByCode(String key) {
        for (Style feature : STYLE_VALUES) {
            if (feature.key.equals(key)) {
                return feature;
            }
        }

        return null;
    }

    public static boolean hasMultipleStyles(String text) {
        Matcher matcher = STRIPPED_COLOR_PATTERN.matcher(text);

        Set<Style> currentStyles = new LinkedHashSet<>();
        int lastEndIndex = 0;
        int amountOfStylizedSections = 0;

        while (matcher.find()) {
            String code = matcher.group();
            Style style = getStyleByCode(code);
            if (style == null) {
                continue;
            }

            if (currentStyles.isEmpty() && (style == Style.RESET || style == Style.WHITE)) {
                lastEndIndex = matcher.end();
                continue;
            }

            if (style.reset) {
                currentStyles.clear();
            } else if (!currentStyles.add(style)) {
                lastEndIndex = matcher.end();
                continue;
            }

            String textBetweenLastSearch = text.substring(lastEndIndex, matcher.start());
            if (!textBetweenLastSearch.trim().isEmpty() && ++amountOfStylizedSections >= 2) {
                return true;
            }

            lastEndIndex = matcher.end();
        }

        // accommodates for any remaining text that didn't have its style changed before the end
        return text.length() > lastEndIndex && !text.substring(lastEndIndex).trim().isEmpty() && ++amountOfStylizedSections >= 2;
    }

    private record ReplacementSection(Style feature, int start, int end) {

    }

    private enum Style {
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

        private final String key;
        private final String start;
        private final String end;
        private final boolean reset;
        private final boolean addSpaces;

        Style(String key, String start, String end) {
            this(key, start, end, true);
        }

        Style(String key, String start, String end, boolean reset) {
            this.key = key;
            this.start = start;
            this.end = end;
            this.reset = reset;
            this.addSpaces = start.equals("'''") || start.equals("''");
        }
    }

}
