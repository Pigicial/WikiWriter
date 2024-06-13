package me.pigicial.wikiwriter.features.items.replacements;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StyleReplacer {

    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder().build();

    private static final char THIN_SPACE = ' ';
    public static final Pattern STRIPPED_COLOR_PATTERN = Pattern.compile("(?i)&[0-9A-FK-ORX]");

    private static final MCStyle[] MC_STYLE_VALUES = MCStyle.values();

    public static String applyStyleAndTextModifications(String text) {
        text = text.replace('§', '&');
        text = placeInitialSpacingAfterStyling(text);

        List<StyleSection> styleSections = getStyleSections(text);
        if (styleSections.isEmpty()) {
            return text;
        }

        List<StyleSection> currentlyAppliedSections = new LinkedList<>();
        StringBuilder newString = new StringBuilder();
        int lastEnd = 0;

        for (StyleSection styleSection : styleSections) {
            int start = styleSection.start();
            int end = styleSection.end();

            MCStyle style = styleSection.MCStyle();

            String textBetweenLastEndAndThisStart = text.substring(lastEnd, start);
            // basically a fix for space strikethroughs not showing up
            if (containsStrikethrough(currentlyAppliedSections)) {
                textBetweenLastEndAndThisStart = textBetweenLastEndAndThisStart.replace(" ", "-");
            }
            newString.append(textBetweenLastEndAndThisStart);

            if (style.reset) {
                Collections.reverse(currentlyAppliedSections);
                for (StyleSection currentlyAppliedSection : currentlyAppliedSections) {
                    newString.append(currentlyAppliedSection.MCStyle().end);
                }

                currentlyAppliedSections.clear();
            }

            newString.append(style.start).append(lastEnd == start && style.addSpaces ? " " : "");

            currentlyAppliedSections.add(styleSection);
            lastEnd = end;
        }

        newString.append(text.substring(lastEnd));

        Collections.reverse(currentlyAppliedSections);
        for (StyleSection currentlyAppliedSection : currentlyAppliedSections) {
            newString.append(currentlyAppliedSection.MCStyle().end);
        }

        String result = newString.toString();
        // Fix progress bars appearing a bit too long (4/5 of the length appears to be roughly the same length)
        result = result.replace("-----", "----");

        return result;
    }

    private static boolean containsStrikethrough(List<StyleSection> sections) {
        for (StyleSection section : sections) {
            if (section.MCStyle == MCStyle.STRIKETHROUGH) {
                return true;
            }
        }

        return false;
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
                // wiki spaces seem more accurate when there's about twice as many of them
                // thin space also actually works compared to regular space
                String spaceText = String.valueOf(THIN_SPACE).repeat(amountOfSpaces * 2);
                String textAfterStyles = text.substring(index);

                return styles + spaceText + textAfterStyles;
            }
        }

        return text;
    }

    private static List<StyleSection> getStyleSections(String text) {
        Matcher matcher = STRIPPED_COLOR_PATTERN.matcher(text);
        List<StyleSection> sections = new LinkedList<>();

        while (matcher.find()) {
            String check = matcher.group();
            MCStyle MCStyle = getStyleByCode(check);

            if (MCStyle != null) {
                sections.add(new StyleSection(MCStyle, matcher.start(), matcher.end()));
            }
        }

        return sections;
    }

    @Nullable
    private static StyleReplacer.MCStyle getStyleByCode(String key) {
        for (MCStyle feature : MC_STYLE_VALUES) {
            if (feature.key.equals(key)) {
                return feature;
            }
        }

        return null;
    }

    public static boolean hasMultipleStyles(String text) {
        String rawLegacyText = text.replace("&", "§");
        TextComponent component = LEGACY_COMPONENT_SERIALIZER.deserialize(rawLegacyText);

        List<TextComponent> sections = separateStyleSections(component);
        return sections.size() >= 2;
    }

    // From https://gist.github.com/Minikloon/e6a7679d171b90dc4e0731db46d77c84
    private static List<TextComponent> separateStyleSections(TextComponent component) {
        List<TextComponent> flattened = new ArrayList<>();

        Style enforcedState = enforceStates(component.style());
        component = component.style(enforcedState);

        Stack<TextComponent> toCheck = new Stack<>();
        toCheck.add(component);

        while (!toCheck.empty()) {
            TextComponent parent = toCheck.pop();
            if (!parent.content().isEmpty()) {
                flattened.add(parent);
            }

            List<Component> children = new ArrayList<>(parent.children());
            Collections.reverse(children);
            for (Component child : children) {
                if (child instanceof TextComponent text) {
                    Style style = parent.style();
                    style = style.merge(child.style());
                    toCheck.add(text.style(style));
                } // Unsupported otherwise
            }
        }
        return flattened;
    }

    private static Style enforceStates(Style style) {
        Style.Builder builder = style.toBuilder();
        style.decorations().forEach((decoration, state) -> {
            if (state == TextDecoration.State.NOT_SET) {
                builder.decoration(decoration, false);
            }
        });
        return builder.build();
    }

    private record StyleSection(MCStyle MCStyle, int start, int end) {

    }

    private enum MCStyle {
        RESET("&r", "", ""),
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
        // STRIKETHROUGH("&m", "<s>", "</s>", false);
        STRIKETHROUGH("&m", "", "", false);

        private final String key;
        private final String start;
        private final String end;
        private final boolean reset;
        private final boolean addSpaces;

        MCStyle(String key, String start, String end) {
            this(key, start, end, true);
        }

        MCStyle(String key, String start, String end, boolean reset) {
            this.key = key;
            this.start = start;
            this.end = end;
            this.reset = reset;
            this.addSpaces = start.equals("'''") || start.equals("''");
        }
    }

}
