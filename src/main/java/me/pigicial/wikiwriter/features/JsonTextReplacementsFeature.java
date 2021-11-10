package me.pigicial.wikiwriter.features;

import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.core.Config;

public enum JsonTextReplacementsFeature {
    DUNGEON_STATS("&8\\([+-]?[\\d,.]+%?\\)", "", true),
    LINE_SEPARATORS("\", \"", "\n", false),
    STRAY_COMMAS(",", "<nowiki>,</nowiki>", false);

    private final String toReplace;
    private final String replacement;
    private final boolean regex;

    JsonTextReplacementsFeature(String toReplace, String replacement, boolean regex) {
        this.toReplace = toReplace;
        this.replacement = replacement;
        this.regex = regex;
    }

    public static String replaceEverything(String text) {
        Config config = WikiWriter.getInstance().getConfig();
        for (JsonTextReplacementsFeature replacement : JsonTextReplacementsFeature.values()) {
            if (replacement == DUNGEON_STATS && (!config.removeDungeonStats || !text.contains(":"))) continue;
            text = replacement.replace(text);
        }

        return text;
    }

    public String replace(String text) {
        if (regex) {
            return text.replaceAll(toReplace, replacement);
        } else {
            return text.replace(this.toReplace, this.replacement);
        }
    }
}
