package me.pigicial.wikiwriter.utils;

import me.pigicial.wikiwriter.features.items.TextReplacementPipeline;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.List;

public class StyleConversions {

    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder().build();


    public static boolean hasMultipleStyles(String text) {
        String rawLegacyText = text.replace("&", "§");
        TextComponent component = LEGACY_COMPONENT_SERIALIZER.deserialize(rawLegacyText);

        List<TextComponent> sections = getSections(component);
        return sections.size() >= 2;
    }

    public static TextComponent toComponent(String text) {
        return LEGACY_COMPONENT_SERIALIZER.deserialize(text.replace("&", "§"));
    }

    public static String toLegacyText(List<TextComponent> components) {
        StringBuilder merged = new StringBuilder();
        for (TextComponent textComponent : components) {
            merged.append(LEGACY_COMPONENT_SERIALIZER.serialize(textComponent).replace("§", "&"));
        }

        String legacyText = merged.toString();
        String colorlessText = stripColor(legacyText).replace(String.valueOf(TextReplacementPipeline.THIN_SPACE), "");
        if (colorlessText.isBlank()) {
            return "";
        }
        return legacyText;
    }

    public static String toLegacyText(Component component) {
        return LEGACY_COMPONENT_SERIALIZER.serialize(component).replace("§", "&");
    }

    public static List<TextComponent> combineSameStyles(List<TextComponent> components) {
        for (int i = 0; i < components.size() - 1; i++) {
            TextComponent component = components.get(i);

            for (int j = i + 1; j < components.size(); j++) {
                TextComponent otherComponent = components.get(j);
                Style style = component.style();
                if (style.equals(otherComponent.style())) {
                    component = Component.text(component.content() + otherComponent.content()).style(style);
                    components.set(i, component);

                    components.remove(j);
                    j--;
                } else {
                    // don't want to add later on components to earlier on components and basically skip the line
                    break;
                }
            }
        }

        return components;
    }

    public static void main(String[] args) {
        TextComponent test = Component.text("Test").color(NamedTextColor.BLUE);
        TextComponent test2 = Component.text(" And Test 2").color(NamedTextColor.BLUE);
        TextComponent test3 = Component.text(" and Test 3").color(NamedTextColor.GREEN);
        List<TextComponent> sections = getSections(test.append(test2.append(test3).append(test2)));
        System.out.println(sections);
        System.out.println();
        System.out.println(combineSameStyles(sections));
        System.out.println();
        System.out.println(toLegacyText(sections));
    }

    // From https://gist.github.com/Minikloon/e6a7679d171b90dc4e0731db46d77c84
    public static List<TextComponent> getSections(TextComponent component) {
        List<TextComponent> flattened = new ArrayList<>();

        Style enforcedState = enforceStates(component.style());
        component = component.style(enforcedState);

        Stack<TextComponent> toCheck = new Stack<>();
        toCheck.add(component);

        while (!toCheck.empty()) {
            TextComponent parent = toCheck.pop();

            List<Component> children = new ArrayList<>(parent.children());
            Collections.reverse(children);

            // dealing with child-components is annoying
            parent = parent.children(List.of());

            if (!parent.content().isEmpty()) {
                flattened.add(parent);
            }

            for (Component child : children) {
                if (child instanceof TextComponent text) {
                    Style style = parent.style();
                    style = style.merge(child.style());
                    toCheck.add(text.style(style));
                } // Unsupported otherwise
            }
        }

        return combineSameStyles(flattened);
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

    public static String stripColor(String text) {
        return Objects.requireNonNull(Formatting.strip(text.replace('&', '§'))).replace('§', '&');
    }
}
