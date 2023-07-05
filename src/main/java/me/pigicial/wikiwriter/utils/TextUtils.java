package me.pigicial.wikiwriter.utils;

import me.pigicial.wikiwriter.features.ColorReplacementFeature;
import me.pigicial.wikiwriter.features.RegexTextReplacements;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.translate.UnicodeUnescaper;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {

    public static String unescapeText(String text) {
        return text.isEmpty() ? "" : new UnicodeUnescaper().translate(StringEscapeUtils.escapeJava(text).replace("\\\"", "\""));
    }

    public static String convertListToString(List<String> textList) {
        List<String> newList = new ArrayList<>();
        if (textList.isEmpty()) {
            return "";
        }

        for (String text : textList) {
            newList.add(ColorReplacementFeature.replace(text));
        }

        String s = RegexTextReplacements.replaceEverything(String.join("\", \"", newList), false);
        if (s.startsWith("{") && s.endsWith("}")) {
            s = s.substring(1, s.length() - 1);
        }

        return unescapeText(s);
    }

    public static String convertJsonTextToLegacy(String jsonLine) {
        Component asComponent = JSONComponentSerializer.json().deserialize(jsonLine);
        String legacyLine = LegacyComponentSerializer.legacyAmpersand().serialize(asComponent);
        return legacyLine.replace('&', '§');
    }
}
