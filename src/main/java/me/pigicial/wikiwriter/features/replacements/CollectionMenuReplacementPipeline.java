package me.pigicial.wikiwriter.features.replacements;

import me.pigicial.wikiwriter.features.items.StyleReplacer;
import me.pigicial.wikiwriter.features.items.TextReplacementPipeline;
import me.pigicial.wikiwriter.features.items.WikiItem;
import me.pigicial.wikiwriter.utils.Action;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class CollectionMenuReplacementPipeline implements MenuModification {

    private final String highestTier;

    public CollectionMenuReplacementPipeline(String highestTier) {
        this.highestTier = highestTier;
    }

    @Override
    public Consumer<WikiItem> getItemConsumer() {
        return item -> {
            Item material = item.getItemStack().getItem();
            if (material.equals(Items.YELLOW_STAINED_GLASS_PANE) || material.equals(Items.RED_STAINED_GLASS_PANE)) {
                item.setMinecraftId("green_stained_glass_pane");
            }
        };
    }

    @Override
    public Consumer<TextReplacementPipeline> getTextPipelineConsumer() {
        return this::modifyText;
    }

    public void modifyText(TextReplacementPipeline textReplacementPipeline) {
        textReplacementPipeline.registerInitialLineModification(list -> {
            for (int i = 0; i < list.size(); i++) {
                TextComponent component = list.get(i);

                // Set progress bars to green for 100%
                String text = component.content();
                if (!text.isEmpty() && text.isBlank() && component.hasDecoration(TextDecoration.STRIKETHROUGH)) {
                    component = component.color(NamedTextColor.GREEN);
                }

                // Replace numbers
                int index = i;
                component = (TextComponent) component.replaceText(replacementBuilder ->
                        replacementBuilder
                                .match(Pattern.compile("\\d+(\\.\\d+)?"))
                                .replacement((matchResult, newTextBuilder) -> {
                                    // percentages above progress bars
                                    if (index + 1 == list.size() - 1 && list.get(index + 1).content().equals("%")) {
                                        return newTextBuilder
                                                .content("100")
                                                .color(NamedTextColor.GREEN)
                                                .build();
                                    } else if (index > 0 && list.get(0).content().contains("Collected")) {
                                        return newTextBuilder
                                                .content(highestTier)
                                                .build();
                                    } else if (index != list.size() - 1) {
                                        // don't replace last number after progress bar
                                        return newTextBuilder
                                                .content(highestTier)
                                                .build();
                                    } else {
                                        return newTextBuilder.build();
                                    }
                                }));

                list.set(i, component);
            }
        });
    }

    @Nullable
    public static String detectHighestTierCollectionRequirement(List<ItemStack> items) {
        // due to the way items are found, the last one found is automatically the highest
        String latestCollectionRequirement = null;
        for (ItemStack itemStack : items) {
            String collectionRequirement = detectCollectionRequirement(itemStack);
            if (collectionRequirement != null) {
                latestCollectionRequirement = collectionRequirement;
            }
        }

        return latestCollectionRequirement;
    }

    private static String detectCollectionRequirement(ItemStack itemStack) {
        WikiItem item = new WikiItem(itemStack, Action.COPYING_INVENTORY);
        for (String lore : item.getLore()) {
            TextComponent component = StyleReplacer.toComponent(lore);
            List<TextComponent> sections = StyleReplacer.separateStyleSections(component);
            if (sections.size() < 4) { // guaranteed to be at least 4
                continue;
            }

            // checks for progress bar
            TextComponent firstSection = sections.get(0);
            if (!firstSection.content().isBlank() || !firstSection.hasDecoration(TextDecoration.STRIKETHROUGH)) {
                continue;
            }

            // checks for number
            TextComponent lastSection = sections.get(sections.size() - 1);
            String text = lastSection.content();

            double multiplier = 1;
            if (text.endsWith("k")) {
                multiplier = 1_000;
                text = text.replace("k", "");
            } else if (text.endsWith("M")) {
                multiplier = 1_000_000;
                text = text.replace("M", "");
            }

            try {
                double value = Double.parseDouble(text) * multiplier;
                return NumberFormat.getNumberInstance().format(value);
            } catch (NumberFormatException ignored) {
            }
        }

        return null;
    }
}
