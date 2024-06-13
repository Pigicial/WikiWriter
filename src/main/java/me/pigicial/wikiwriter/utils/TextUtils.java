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

public final class TextUtils {

    private TextUtils() {

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
