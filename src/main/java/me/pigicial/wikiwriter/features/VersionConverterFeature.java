package me.pigicial.wikiwriter.features;

public enum VersionConverterFeature {
    A("button", "oak_button", true),
    B("gold_horse_armor", "golden_horse_armor", true),
    C("end_portal", "end_portal_frame", true),
    D("sign", "oak_sign"),
    E("stained_clay", "terracotta"),
    F("wool", "white_wool", true),
    G("shrub", "dead_bush", true),
    H("double_tallgrass", "tall_grass", true),
    I("weighted_pressure_plate_(heavy)", "heavy_weighted_pressure_plate", true),
    J("weighted_pressure_plate_(light)", "light_weighted_pressure_plate", true),
    K("wooden_pressure_plate", "oak_pressure_plate", true),
    L("slightly_damaged_anvil", "chipped_anvil", true),
    M("very_damaged_anvil", "broken_anvil", true),
    N("wooden_trapdoor", "oak_trapdoor", true),
    O("nether_stalk", "nether_wart");


    private final String key;
    private final String replacement;
    private final boolean exact;

    VersionConverterFeature(String key, String replacement) {
        this(key, replacement, false);
    }

    VersionConverterFeature(String key, String replacement, boolean exact) {
        this.key = key;
        this.replacement = replacement;
        this.exact = exact;
    }

    public static String replace(String text) {
        for (VersionConverterFeature feature : values()) {
            if (feature.exact) {
                if (text.equalsIgnoreCase(feature.key)) {
                    text = feature.replacement;
                }
            } else {
                text = text.replace(feature.key, feature.replacement);
            }
        }

        return text;
    }
}
