package me.pigicial.wikiwriter.utils;

import me.pigicial.wikiwriter.features.ColorReplacementFeature;
import me.pigicial.wikiwriter.features.RegexTextReplacements;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.translate.UnicodeUnescaper;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {

    public static String unescapeText(String text) {
        return text.isEmpty() ? "" :  new UnicodeUnescaper().translate(StringEscapeUtils.escapeJava(text).replace("\\\"", "\""));
    }

    public static String convertListToString(List<String> text) {
        List<String> newList = new ArrayList<>();
        if (text.isEmpty()) return "";

        if (text.size() == 1) {
            newList.add(ColorReplacementFeature.replace(text.get(0)));
        } else for (String s : text) {
            String added = ColorReplacementFeature.replace(s);
            newList.add(added);
        }

        String s = RegexTextReplacements.replaceEverything(String.join("\", \"", newList), false);
        if (s.startsWith("{") && s.endsWith("}")) s = s.substring(1, s.length() - 1);

        return unescapeText(s);
    }
}
