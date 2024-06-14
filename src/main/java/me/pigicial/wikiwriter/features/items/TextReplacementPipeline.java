package me.pigicial.wikiwriter.features.items;

import me.pigicial.wikiwriter.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class TextReplacementPipeline {

    private static final char THIN_SPACE = ' ';

    private final List<Consumer<List<TextComponent>>> firstRunPerLineModifications = new ArrayList<>();

    public TextReplacementPipeline() {
        registerInitialLineModification(TextReplacementPipeline::modifySingleLineAsComponents);

        // Register skyblock level modifications
        registerInitialLineModification(list -> {
            for (int i = list.size() - 1; i >= 0; i--) {
                TextComponent component = list.get(i);

                // Set progress bars to white
                String text = component.content();
                if (!text.isEmpty() && text.isBlank() && component.hasDecoration(TextDecoration.STRIKETHROUGH)) {
                    component = component.color(NamedTextColor.WHITE);
                }

                // Replace numbers
                component = (TextComponent) component.replaceText(replacementBuilder ->
                        replacementBuilder
                                .match(Pattern.compile("\\d+(\\.\\d+)?(%)?"))
                                .replacement((matchResult, newTextBuilder) -> newTextBuilder
                                        .content("0" + matchResult.group(2))
                                        .build()
                                ));

                list.set(i, component);
            }
        });
    }

    public void registerInitialLineModification(Consumer<List<TextComponent>> separatedComponentsConsumer) {
        firstRunPerLineModifications.add(separatedComponentsConsumer);
    }

    public String replaceTextListAndConvertToString(List<String> list) {
        if (list.isEmpty()) {
            return "";
        }

        makeModificationsToList(list);
        return TextUtils.unescapeText(String.join("\n", list));
    }

    private static void makeModificationsToList(List<String> lines) {
        System.out.println("lines (" + lines.size() + ") = " + lines);
        for (int i = 0, linesSize = lines.size(); i < linesSize; i++) {
            String text = lines.get(i);

            String newText = replaceText(text);
            lines.set(i, newText);
        }
    }

    public static String replaceText(String text) {
        TextComponent component = StyleReplacer.toComponent(text);
        List<TextComponent> separatedComponents = StyleReplacer.separateStyleSections(component);

        modifySingleLineAsComponents(separatedComponents);

        String string = StyleReplacer.toString(separatedComponents);
        System.out.println("text = " + text);
        System.out.println("string = " + string);
        return string;
    }

    private static void modifySingleLineAsComponents(List<TextComponent> separatedComponents) {
        for (int i = 0, separatedComponentsSize = separatedComponents.size(); i < separatedComponentsSize; i++) {
            TextComponent component = separatedComponents.get(i);
            System.out.println("component section = " + component);

            // Strikethrough and spaces
            String colorlessText = component.content();
            boolean onlyContainsSpaces = !colorlessText.isEmpty() && colorlessText.isBlank();
            if (onlyContainsSpaces) {
                int length = colorlessText.length();

                if (component.hasDecoration(TextDecoration.STRIKETHROUGH)) {
                    System.out.println("1");
                    component = convertToStrikethrough(component, length);
                } else if (component.style().color() == null) {
                    System.out.println("2");
                    component = convertToThinSpaces(component, length);
                }
            }
            component = component.decoration(TextDecoration.STRIKETHROUGH, false);

            component = replaceWithMediaWikiDecorations(component);
            component = (TextComponent) component.replaceText(builder -> builder.matchLiteral(",").replacement("<nowiki>,</nowiki>"));

            separatedComponents.set(i, component);
        }
    }


    private static TextComponent convertToStrikethrough(TextComponent component, int length) {
        Style style = component.style().decoration(TextDecoration.STRIKETHROUGH, false);
        return Component.text("-".repeat(length).replace("-----", "----")).style(style);
    }

    private static TextComponent convertToThinSpaces(TextComponent component, int length) {
        Style style = component.style();
        String thinSpace = String.valueOf(THIN_SPACE).repeat(length * 2);
        return Component.text(thinSpace).style(style).color(NamedTextColor.WHITE);
    }

    private static TextComponent replaceWithMediaWikiDecorations(TextComponent component) {
        // MediaWiki modifications:
        List<String> prefixList = new ArrayList<>();
        List<String> suffixList = new ArrayList<>();

        Style style = component.style();
        String colorlessText = component.content();

        for (DecorationReplacements replacement : DecorationReplacements.values()) {
            TextDecoration decoration = replacement.decoration;
            if (style.hasDecoration(decoration)) {
                prefixList.add(replacement.start);
                suffixList.add(replacement.end);
            }
        }

        Collections.reverse(suffixList);
        String prefixText = String.join("", prefixList);
        String suffixText = String.join("", suffixList);

        // Add MediaWiki modifications
        component = Component.text(prefixText + colorlessText + suffixText).style(style);
        for (DecorationReplacements replacement : DecorationReplacements.values()) {
            component = component.decoration(replacement.decoration, false);
        }
        return component;
    }

    /*
    private static TextComponent replaceProgressBarsAndValues(TextComponent component) {
        component = component.replaceText(builder -> {
            return builder
                    .match(Pattern.compile("\\d*.?\\d*(%?)"))
                    .replacement((matchResult, builder1) -> {
                        return "0" +
                    });
        });

        return component;
    }
     */

    private enum DecorationReplacements {
        UNDERLINE(TextDecoration.UNDERLINED, "<ins>", "</ins>"),
        BOLD(TextDecoration.BOLD, "''' ", "'''"),
        ITALICS(TextDecoration.ITALIC, "'' ", "''");
        // the spaces on the bold and italic starts are to avoid weird syntax render errors

        private final TextDecoration decoration;
        private final String start;
        private final String end;

        DecorationReplacements(TextDecoration decoration, String start, String end) {
            this.decoration = decoration;
            this.start = start;
            this.end = end;
        }
    }
}
