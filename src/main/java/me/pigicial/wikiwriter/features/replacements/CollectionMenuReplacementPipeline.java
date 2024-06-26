package me.pigicial.wikiwriter.features.replacements;

import me.pigicial.wikiwriter.features.items.TextReplacementPipeline;
import me.pigicial.wikiwriter.features.items.WikiItem;
import me.pigicial.wikiwriter.utils.Action;
import me.pigicial.wikiwriter.utils.StyleConversions;
import me.pigicial.wikiwriter.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectionMenuReplacementPipeline implements MenuModification {

    private static final int TOP_COLLECTION_ITEM_INDEX = 4;

    private final String highestTier;
    private final int highestTierNumber;
    private final String highestTierRomanNumeral;

    public CollectionMenuReplacementPipeline(String highestTierAmount, int highestTierNumber, String highestTierRomanNumeral) {
        this.highestTier = highestTierAmount;
        this.highestTierNumber = highestTierNumber;
        this.highestTierRomanNumeral = highestTierRomanNumeral;
    }

    @Override
    public BiConsumer<WikiItem, Integer> getItemConsumer() {
        return (item, index) -> {
            Item material = item.getItemStack().getItem();
            if (material.equals(Items.YELLOW_STAINED_GLASS_PANE) || material.equals(Items.RED_STAINED_GLASS_PANE)) {
                item.setMinecraftId("lime_stained_glass_pane");

                String name = item.getName();
                TextComponent componentWithNewColor = StyleConversions.toComponent(name).color(NamedTextColor.GREEN);
                String replacedName = StyleConversions.toLegacyText(componentWithNewColor);
                item.setName(replacedName);
            }

            // Replace roman numeral at the top
            if (!material.toString().contains("glass") && index == TOP_COLLECTION_ITEM_INDEX) {
                String name = item.getName();
                TextComponent component = StyleConversions.toComponent(name);
                String rawText = component.content();

                // Find the index of the last space
                int lastSpaceIndex = rawText.lastIndexOf(' ');
                String collectionName = rawText.substring(0, lastSpaceIndex).strip();

                // Replace roman numeral
                Component replacedComponent = Component.text(collectionName + " " + highestTierRomanNumeral).style(component.style());
                String replacedName = StyleConversions.toLegacyText(replacedComponent);
                item.setName(replacedName);
            }
        };
    }

    @Override
    public Consumer<TextReplacementPipeline> getTextPipelineConsumer() {
        return this::modifyText;
    }

    public void modifyText(TextReplacementPipeline textReplacementPipeline) {
        textReplacementPipeline.registerInitialPerLineModification(this::modifyText);
        textReplacementPipeline.registerInitialMultiLineModification(this::fixContributionsList);
    }

    private void modifyText(List<TextComponent> list) {
        for (int index = 0; index < list.size(); index++) {
            TextComponent component = list.get(index);

            // Set progress bars to green for 100%
            String text = component.content();
            if (!text.isEmpty() && text.isBlank() && component.hasDecoration(TextDecoration.STRIKETHROUGH)) {
                component = component.color(NamedTextColor.GREEN);
            }

            // Replace numbers
            if (text.strip().equals("%")) {
                component = component.color(NamedTextColor.GREEN);
            }

            Pattern numberPattern = Pattern.compile("\\d+(\\.\\d+)?");
            Matcher numberMatcher = numberPattern.matcher(text);
            if (numberMatcher.find()) {
                boolean nextSectionIsPercentage = index + 1 == list.size() - 1 && list.get(index + 1).content().equals("%");
                boolean isTopCollectionAmountText = index > 0 && list.get(0).content().contains("Collect");
                boolean isBeforeSlash = index < list.size() - 1 && list.get(index + 1).content().contains("/");

                if (nextSectionIsPercentage) {
                    component = Component.text("100").style(component.style()).color(NamedTextColor.GREEN);
                } else if (isTopCollectionAmountText) {
                    // Top center item has total collection/collected text
                    component = Component.text(highestTier).style(component.style());
                } else if (isBeforeSlash) {
                    // Is before slash
                    component = Component.text(highestTier).style(component.style());
                }
            }

            list.set(index, component);
        }
    }

    private void fixContributionsList(List<TextComponent> components) {
        boolean removingLines = false;
        int contributionsIndex = -1;
        for (int index = 0; index < components.size(); index++) {
            TextComponent component = components.get(index);
            String text = StyleConversions.toLegacyText(component);
            if (text.contains("Contributions:")) {
                removingLines = true;
                contributionsIndex = index;
                continue;
            }

            boolean blank = StyleConversions.stripColor(text).isBlank();
            if (removingLines) {
                if (blank) {
                    // hit an empty spot - might not even be needed
                    removingLines = false;
                } else {
                    components.remove(index);
                    index--;
                }
            }
        }

        if (contributionsIndex != -1) {
            TextComponent steveContributions = generateContributionsText("Steve", 0.8);
            TextComponent alexContributions = generateContributionsText("Alex", 0.2);
            components.add(contributionsIndex + 1, steveContributions);
            components.add(contributionsIndex + 2, alexContributions);
        }
    }

    private TextComponent generateContributionsText(String name, double multiplier) {
        return Component.text("[MVP").color(NamedTextColor.AQUA)
                .append(Component.text("+").color(NamedTextColor.RED))
                .append(Component.text("] " + name).color(NamedTextColor.AQUA))
                .append(Component.text(": ").color(NamedTextColor.GRAY))
                .append(Component.text(formatNumber((int) (highestTierNumber * multiplier))).color(NamedTextColor.YELLOW));
    }

    public static String formatNumber(int number) {
        if (number >= 1_000_000) {
            double millions = number / 1_000_000.0;
            return (millions % 1 == 0)
                    ? String.format("%dm", (int) millions)
                    : String.format("%.1fm", millions);
        } else if (number >= 1_000) {
            double thousands = number / 1_000.0;
            return (thousands % 1 == 0)
                    ? String.format("%dk", (int) thousands)
                    : String.format("%.1fk", thousands);
        } else {
            return Integer.toString(number);
        }
    }

    @Nullable
    public static CollectionMenuReplacementPipeline generatePipelineIsApplicable(List<ItemStack> items) {
        // due to the way items are found, the last one found is automatically the highest
        int amount = 0;
        int tiers = 0;
        for (ItemStack itemStack : items) {
            int collectionRequirement = detectCollectionRequirement(itemStack);
            if (collectionRequirement != 0) {
                amount = collectionRequirement;
                tiers++;
            }
        }

        if (amount == 0) {
            return null;
        }

        String formattedAmount = NumberFormat.getNumberInstance().format(amount);
        return new CollectionMenuReplacementPipeline(formattedAmount, amount, TextUtils.convertToRomanNumeral(tiers));
    }

    private static int detectCollectionRequirement(ItemStack itemStack) {
        WikiItem item = new WikiItem(itemStack, Action.COPYING_INVENTORY);
        for (String lore : item.getLore()) {
            TextComponent component = StyleConversions.toComponent(lore);
            List<TextComponent> sections = StyleConversions.getSections(component);
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
                return (int) (Double.parseDouble(text) * multiplier);
            } catch (NumberFormatException ignored) { }
        }

        return 0;
    }
}
