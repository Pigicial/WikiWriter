package me.pigicial.wikiwriter.features;

import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.config.Config;

public enum RegexTextReplacements {
    DUNGEON_STATS("&8\\([+-]?[\\d,.]+%?\\)", "", true),
    LINE_SEPARATORS("\", \"", "\n", false),
    STRAY_COMMAS(",", "<nowiki>,</nowiki>", false);

    private final String toReplace;
    private final String replacement;
    private final boolean regex;

    RegexTextReplacements(String toReplace, String replacement, boolean regex) {
        this.toReplace = toReplace;
        this.replacement = replacement;
        this.regex = regex;
    }

    public static String replaceEverything(String text, boolean name) {
        Config config = WikiWriter.getInstance().getConfig();
        for (RegexTextReplacements replacement : RegexTextReplacements.values()) {
            if (replacement == DUNGEON_STATS && (!config.removeDungeonStats || !text.contains(":"))) continue;
            if (name && replacement == STRAY_COMMAS) continue;
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
