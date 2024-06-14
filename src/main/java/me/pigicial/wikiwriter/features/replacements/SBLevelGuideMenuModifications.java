package me.pigicial.wikiwriter.features.replacements;

import me.pigicial.wikiwriter.features.items.TextReplacementPipeline;
import me.pigicial.wikiwriter.features.items.WikiItem;
import me.pigicial.wikiwriter.features.items.types.TextureAndReferenceData;
import me.pigicial.wikiwriter.utils.StyleConversions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SBLevelGuideMenuModifications implements MenuModification {

    private static final String X_SYMBOL = "✖";
    private static final String CHECKMARK_SYMBOL = "✔";

    @Override
    public BiConsumer<WikiItem, Integer> getItemConsumer() {
        // probably not the most necessary idea but eh why not
        Map<String, SimpleTextureReplacement> textureMap = new HashMap<>();
        textureMap.put("Accessories", replacement("talisman"));
        textureMap.put("Minion", replacement("cobblestone_generator_1"));
        textureMap.put("Bestiary", replacement("bestiary_milestone"));
        textureMap.put("Arachne", replacement("spider"));
        textureMap.put("Community Shop", replacement("elizabeth_skull"));
        textureMap.put("Rock", petReplacement("rock"));
        textureMap.put("Dungeon", replacement("wither_artifact"));
        textureMap.put("Catacombs", replacement("wither_artifact"));
        textureMap.put("Heart of the Mountain", replacement("heart_of_the_mountain"));
        textureMap.put("Accessory Bag Upgrades", replacement("jacobus_register"));
        textureMap.put("Divine Chocolate", replacement("rabbit_divine_1"));
        textureMap.put("Mythic Chocolate", replacement("rabbit_mythic_zorro"));
        textureMap.put("Trophy Fish", replacement("slugfish"));
        textureMap.put("Personal Bank", replacement("personal_bank_item"));
        textureMap.put("Dolphin", petReplacement("dolphin"));
        textureMap.put("Chocolate", replacement("rabbit_chocolate"));
        textureMap.put("Boss Collection", replacement("boss_collection"));
        textureMap.put("Anita", replacement("anita_skull"));
        textureMap.put("Taming", replacement("george_skull"));
        textureMap.put("Bank Upgrades", replacement("bank_upgrade_palatial"));
        textureMap.put("Bank Account", replacement("bank_upgrade_palatial"));
        textureMap.put("Infused Dragon", petReplacement("ender_dragon"));
        textureMap.put("Kuudra Infernal", replacement("kuudra_infernal_tier_key"));
        for (String essence : new String[]{"Gold", "Diamond", "Ice", "Undead", "Wither", "Spider", "Dragon", "Crimson"}) {
            textureMap.put(essence + " Essence", replacement(essence.toLowerCase() + "_essence"));
        }

        return (item, index) -> {
            item.setForcedNotClickable(true);
            String name = StyleConversions.stripColor(item.getName());

            for (Map.Entry<String, SimpleTextureReplacement> entry : textureMap.entrySet()) {
                if (name.contains(entry.getKey())) {
                    item.setTextureAndReferenceData(entry.getValue());
                    break;
                }
            }
        };
    }

    private SimpleTextureReplacement replacement(String id) {
        return new SimpleTextureReplacement(id, false);
    }

    private SimpleTextureReplacement petReplacement(String id) {
        return new SimpleTextureReplacement(id, true);
    }

    @Override
    public Consumer<TextReplacementPipeline> getTextPipelineConsumer() {
        return this::modifyText;
    }

    public void modifyText(TextReplacementPipeline textReplacementPipeline) {
        textReplacementPipeline.registerInitialPerLineModification(this::modifyText);
    }

    private void modifyText(List<TextComponent> list) {
        for (int index = 0; index < list.size(); index++) {
            TextComponent component = list.get(index);

            // Set progress bars to white for 0%
            String text = component.content();
            if (!text.isEmpty() && text.isBlank() && component.hasDecoration(TextDecoration.STRIKETHROUGH)) {
                component = component.color(NamedTextColor.WHITE);
            }

            // Replace checkmark text
            if (text.contains(CHECKMARK_SYMBOL)) {
                if (index == 0 && list.size() == 1) {
                    // is name in starter (green name, green checkmark)
                    // separates out the components so the red checkmark is first, the green text is next
                    list.add(Component.text(text.replace(CHECKMARK_SYMBOL, "").trim()).style(component.style()));
                    component = Component.text(X_SYMBOL + " ").color(NamedTextColor.RED);
                } else {
                    component = Component.text(text.replace(CHECKMARK_SYMBOL, X_SYMBOL)).style(component.style()).color(NamedTextColor.RED);

                    if (index + 1 == list.size() - 1) {
                        TextComponent nextComponent = list.get(index + 1);
                        if (nextComponent.color() == NamedTextColor.DARK_GRAY) {
                            // Replaces list of tasks from dark gray text (complete) to white text (not complete)
                            list.set(index + 1, nextComponent.color(NamedTextColor.WHITE));
                        }
                    }
                }
            }

            // Replace numbers
            Pattern numberPattern = Pattern.compile("\\d+(\\.\\d+)?%?");
            Matcher numberMatcher = numberPattern.matcher(text);
            if (numberMatcher.find()) {
                boolean isPercentage = text.endsWith("%");
                boolean isBeforeSlash = index < list.size() - 1 && list.get(index + 1).content().contains("/");

                if (isPercentage) {
                    component = Component.text("0%").style(component.style());
                }  else if (isBeforeSlash) {
                    component = Component.text("0").style(component.style());
                }
            }

            list.set(index, component);
        }
    }

    private record SimpleTextureReplacement(String id, boolean pet) implements TextureAndReferenceData {

        @Override
        public String getLoreTemplateReference() {
            return null;
        }

        @Override
        public String getTextureLink() {
            return id;
        }

        @Override
        public String getTextureType() {
            return pet ? "sbpet" : "sb";
        }
    }
}
