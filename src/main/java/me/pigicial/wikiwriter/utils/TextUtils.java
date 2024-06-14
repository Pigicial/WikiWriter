package me.pigicial.wikiwriter.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.translate.UnicodeUnescaper;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public final class TextUtils {

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

    public static List<String> parseJsonLore(NbtCompound displayTag) {
        NbtList loreTag = displayTag.getList("Lore", NbtElement.STRING_TYPE);

        List<String> lore = new ArrayList<>();
        for (int i = 0; i < loreTag.size(); i++) {
            String jsonLine = loreTag.getString(i);
            String legacyLine = TextUtils.convertJsonTextToLegacy(jsonLine);

            lore.add(legacyLine);
        }

        return lore;
    }

    public static String convertJsonTextToLegacy(String jsonLine) {
        Component component = JSONComponentSerializer.json().deserialize(jsonLine);
        String legacyLine = LegacyComponentSerializer.legacyAmpersand().serialize(component);
        return legacyLine.replace('&', '§');
    }
}
