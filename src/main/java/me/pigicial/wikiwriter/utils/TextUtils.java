package me.pigicial.wikiwriter.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.translate.UnicodeUnescaper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public final class TextUtils {

    private static final RegistryWrapper.WrapperLookup LOOKUP = BuiltinRegistries.createWrapperLookup();
    private final static TreeMap<Integer, String> ROMAN_NUMERAL_MAP = new TreeMap<>();

    static {
        ROMAN_NUMERAL_MAP.put(1000, "M");
        ROMAN_NUMERAL_MAP.put(900, "CM");
        ROMAN_NUMERAL_MAP.put(500, "D");
        ROMAN_NUMERAL_MAP.put(400, "CD");
        ROMAN_NUMERAL_MAP.put(100, "C");
        ROMAN_NUMERAL_MAP.put(90, "XC");
        ROMAN_NUMERAL_MAP.put(50, "L");
        ROMAN_NUMERAL_MAP.put(40, "XL");
        ROMAN_NUMERAL_MAP.put(10, "X");
        ROMAN_NUMERAL_MAP.put(9, "IX");
        ROMAN_NUMERAL_MAP.put(5, "V");
        ROMAN_NUMERAL_MAP.put(4, "IV");
        ROMAN_NUMERAL_MAP.put(1, "I");
    }

    public static String convertToRomanNumeral(int number) {
        int l = ROMAN_NUMERAL_MAP.floorKey(number);
        if (number == l) {
            return ROMAN_NUMERAL_MAP.get(number);
        }
        return ROMAN_NUMERAL_MAP.get(l) + convertToRomanNumeral(number - l);
    }

    public static String unescapeText(String text) {
        return text.isEmpty() ? "" : new UnicodeUnescaper().translate(StringEscapeUtils.escapeJava(text).replace("\\\"", "\""));
    }

    public static List<String> parseJsonLore(@Nullable LoreComponent loreComponent) {
        if (loreComponent == null) {
            return new ArrayList<>();
        }

        List<String> lore = new ArrayList<>();
        for (Text line : loreComponent.styledLines()) {
            lore.add(convertToLegacyText(line));
        }

        return lore;
    }

    public static String convertToLegacyText(Text text) {
        String asJson = Text.Serialization.toJsonString(text, LOOKUP);
        Component asComponent = JSONComponentSerializer.json().deserialize(asJson);
        String asLegacy = LegacyComponentSerializer.legacyAmpersand().serialize(asComponent);
        return asLegacy.replace('&', '§');
    }
}
