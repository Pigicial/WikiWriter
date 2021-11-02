package me.pigicial.wikiwriter.features;

public enum JsonTextReplacementsFeature {
    LINE_SEPARATORS("\", \"", "\n"),
    STRAY_COMMAS(",", "<nowiki>,</nowiki>");

    private final String toReplace;
    private final String replacement;

    JsonTextReplacementsFeature(String toReplace, String replacement) {
        this.toReplace = toReplace;
        this.replacement = replacement;
    }

    public static String replaceEverything(String text) {
        for (JsonTextReplacementsFeature replacement : JsonTextReplacementsFeature.values()) {
            text = replacement.replace(text);
        }

        return text;
    }

    public String replace(String text) {
        return text.replace(this.toReplace, this.replacement);
    }
}
