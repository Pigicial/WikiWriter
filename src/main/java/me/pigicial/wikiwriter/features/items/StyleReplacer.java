package me.pigicial.wikiwriter.features.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class StyleReplacer {

    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder().build();


    public static boolean hasMultipleStyles(String text) {
        String rawLegacyText = text.replace("&", "§");
        TextComponent component = LEGACY_COMPONENT_SERIALIZER.deserialize(rawLegacyText);

        List<TextComponent> sections = separateStyleSections(component);
        return sections.size() >= 2;
    }

    public static TextComponent toComponent(String text) {
        return LEGACY_COMPONENT_SERIALIZER.deserialize(text.replace("&", "§"));
    }

    public static String toString(List<TextComponent> components) {
        StringBuilder merged = new StringBuilder();
        for (TextComponent textComponent : components) {
            merged.append(LEGACY_COMPONENT_SERIALIZER.serialize(textComponent).replace("§", "&"));
        }

        return merged.toString();
    }

    // From https://gist.github.com/Minikloon/e6a7679d171b90dc4e0731db46d77c84
    public static List<TextComponent> separateStyleSections(TextComponent component) {
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
}
