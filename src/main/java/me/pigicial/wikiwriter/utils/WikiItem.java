package me.pigicial.wikiwriter.utils;

import lombok.Getter;
import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.core.Config;
import me.pigicial.wikiwriter.features.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.translate.UnicodeUnescaper;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WikiItem {
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-ORX]");
    private static final Pattern AUCTION_ITEM_COUNT_PATTERN = Pattern.compile("(-?[0-9]|[1-9][0-9]|[1-9][0-9][0-9])x ");
    private static final Pattern PET_NUMBER_COUNTER = Pattern.compile("\\[Lvl ([01]?[0-9][0-9]?|200)] ");
    private static final Pattern PET_NUMBER_COUNTER_WITH_COLOR = Pattern.compile("§7\\[Lvl ([01]?[0-9][0-9]?|200)] ");

    private final String name;
    private final String minecraftId;
    private final List<String> lore;
    private final String skyblockId;
    private final Rarity currentItemRarity;
    @Getter
    private final int stackSize;
    @Nullable private final String petId;
    private final String nameWithoutColor;
    private final boolean emptyTitle;
    private final String typeText;
    private final String initialChar;
    private final boolean showRarity;
    private final boolean skyblockItem;
    private final boolean pet;
    private final String loreAsString;
    private final String nameWithColor;
    private final String referenceId;

    public WikiItem(@Nullable String guiName, ItemStack stack) {
        if (stack == null) {
            this.name = "";
            this.minecraftId = "";
            this.lore = new ArrayList<>();
            this.skyblockId = "";
            this.currentItemRarity = Rarity.NONE;
            this.stackSize = 1;
            this.petId = null;
            this.nameWithoutColor = "";
            this.emptyTitle = true;
            this.typeText = "";
            this.initialChar = "";
            this.showRarity = false;
            this.skyblockItem = false;
            this.pet = false;
            this.loreAsString = "";
            this.nameWithColor = "";
            this.referenceId = "";
            return;
        }

        NBTTagCompound nbt = stack.getTagCompound();
        // I don't think this can be null but there's null checks in ItemStack so just in case
        if (nbt == null) nbt = new NBTTagCompound();

        NBTTagCompound extraAttributes = nbt.getCompoundTag("ExtraAttributes");
        String skyblockId = extraAttributes.getString("id");

        NBTTagCompound display = nbt.getCompoundTag("display");
        NBTTagList loreTag = display.getTagList("Lore", 8); // 8 is NBTTagString from NBTBase#createNewByType

        List<String> lore = new ArrayList<>();
        for (int i = 0; i < loreTag.tagCount(); i++)
            lore.add(loreTag.getStringTagAt(i));

        LoreRemovalFeature.filterLore(lore);

        String name = stack.getDisplayName();
        String minecraftId = stack.getItem().getItemStackDisplayName(stack).toLowerCase().replace(" ", "_");

        boolean head = minecraftId.contains("head");
        boolean skyblockItem = head && !minecraftId.equalsIgnoreCase("Zombie_Head") && !minecraftId.equalsIgnoreCase("Creeper_Head") ;

        boolean pet = skyblockId.equalsIgnoreCase("pet") || (skyblockId.equals("") && !lore.isEmpty() && lore.get(0).contains("Pet") && skyblockItem);
        boolean hasSkyblockItemID = !skyblockId.equals("");

        if (pet) {
            // update if it didn't include the id but is a pet
            skyblockId = "pet";
        } else if (skyblockItem && skyblockId.equals("")) {
            skyblockId = "head";
        }

        boolean petNameChanged = false;
        Config config = WikiWriter.getInstance().getConfig();
        if (config.updatePetName && pet) {
            Matcher matcher = PET_NUMBER_COUNTER_WITH_COLOR.matcher(name);
            while (matcher.find()) {
                petNameChanged = true;
                name = name.replace(matcher.group(), "");
            }
        }

        Rarity currentItemRarity = Rarity.NONE;
        Rarity baseItemRarity;

        int rarityUpgrades = extraAttributes.getInteger("rarity_upgrades");
        loreLoop: for (String s : lore) {
            int offset = rarityUpgrades > 0 ? 2 : 0;
            String lineWithoutColor = EnumChatFormatting.getTextWithoutFormattingCodes(s);
            if (lineWithoutColor.length() <= 1) continue;
            if (rarityUpgrades > 0 && lineWithoutColor.length() < 4) continue;

            for (Rarity r : Rarity.values()) {
                if (r == Rarity.NONE) continue;
                String rarityName = r.name();
                String textToCheck = lineWithoutColor.substring(offset, lineWithoutColor.length() - offset);
                if (textToCheck.startsWith(rarityName)) { // using startsWith instead of equals since it can have text after rarity
                    currentItemRarity = r;
                    baseItemRarity = r;
                    if (rarityUpgrades > 0) {
                        for (int i = 0; i < rarityUpgrades; i++) {
                            baseItemRarity = baseItemRarity.getPreviousRarity();
                        }
                    }
                    break loreLoop;
                }
            }
        }

        if (currentItemRarity == Rarity.NONE && !name.isEmpty()) {

            int lastEnd = -1;
            Matcher matcher = STRIP_COLOR_PATTERN.matcher(name);

            // Only check for custom colors if they're supported by the version of Minecraft used, otherwise it's unnecessary
            colorLoop: while (matcher.find()) {
                int start = matcher.start();
                if (lastEnd != -1 && start > lastEnd) {
                    break;
                }
                int end = matcher.end();
                for (EnumChatFormatting colorCode : EnumChatFormatting.values()) {
                    if (colorCode.isColor() && Rarity.COLOR_CODES.contains(name.charAt(end - 1))) {
                        for (Rarity r : Rarity.values()) {
                            if (r == Rarity.NONE) continue;
                            if (r.getColorCode() == name.charAt(end - 1)) {
                                currentItemRarity = r;
                                baseItemRarity = r;
                                if (rarityUpgrades > 0) {
                                    for (int i = 0; i < rarityUpgrades; i++) {
                                        baseItemRarity = baseItemRarity.getPreviousRarity();
                                    }
                                }
                                break colorLoop;
                            }
                        }
                    }
                }
                lastEnd = end;
            }
        }

        boolean hasEnchantments = stack.isItemEnchanted();

        if (skyblockItem) {
            minecraftId = "head";
        }

        long color = display.getLong("color");
        if (!display.hasKey("color", 99)) {
            color = -1;
        }

        int maxStackSize = stack.getMaxStackSize();
        if (extraAttributes.hasKey("uuid, 8")) {
            maxStackSize = 1;
        }

        if (minecraftId.startsWith("leather") && color != -1) {
            LeatherColorFinderFeature colorFinderFeature = LeatherColorFinderFeature.findColor((int) color);
            WikiWriter.getInstance().getLogger().info("Found leather color " + colorFinderFeature.name() + " for " + minecraftId);
            if (colorFinderFeature != LeatherColorFinderFeature.DEFAULT) {
                minecraftId = minecraftId + "_" + colorFinderFeature.name().toLowerCase();
            }
        }

        minecraftId = VersionConverterFeature.replace(minecraftId);

        // spawn eggs are formatted like ghast_spawn_egg instead of spawn_ghast (which it normally gets)
        if (minecraftId.startsWith("spawn_")) {
            minecraftId = minecraftId.substring(6) + "_spawn_egg";
        }

        if (minecraftId.contains("chain_")) {
            minecraftId = minecraftId.replace("chain_", "chainmail_");
        }

        if (!minecraftId.equalsIgnoreCase("head") && hasEnchantments) {
            minecraftId = "enchanted_" + minecraftId;
        }

        String petId = null;
        if (skyblockItem && pet) {

            String rawName = EnumChatFormatting.getTextWithoutFormattingCodes(name);
            Matcher matcher = PET_NUMBER_COUNTER.matcher(rawName);
            boolean found = petNameChanged;

            while (matcher.find()) {
                found = true;
                rawName = rawName.replace(matcher.group(), "");
            }

            if (rawName.endsWith("✦")) {
                rawName = rawName.substring(0, rawName.length() - 2);
            }

            if (petNameChanged) {
                name = name.replace(rawName, rawName + " Pet");
            }

            if (found) {
                petId = rawName.replace(" ", "_").toLowerCase();
            }
        }

        int stackSize = stack.stackSize;

        String nameWithColor = name;

        boolean showRarity = currentItemRarity != Rarity.NONE;

        if (showRarity && config.guaranteedStackSizeToggled && hasSkyblockItemID) {
            int setStackSize = config.setStackSize;
            if (!config.bypassStackSizeLimit) {
                setStackSize = Math.min(setStackSize, maxStackSize);
            }

            if (setStackSize != stackSize) {
                Matcher matcher = AUCTION_ITEM_COUNT_PATTERN.matcher(nameWithColor);
                while (matcher.find()) {
                    nameWithColor = nameWithColor.replace(matcher.group(), "");
                }

                if (setStackSize != 1) {
                    showRarity = false;
                }

                // only change stack amounts in auction menus, since that's only where they show up in
                if (guiName != null && guiName.toLowerCase().contains("auction")) {
                    String amountString = EnumChatFormatting.GRAY + NumberFormat.getInstance().format(setStackSize) + "x " + EnumChatFormatting.RESET;
                    nameWithColor = amountString + nameWithColor;
                }

                stackSize = setStackSize;
            }
        }

        String nameWithReplacements = JsonTextReplacementsFeature.replaceEverything(nameWithColor);
        nameWithColor = ColorReplacementFeature.replace(nameWithReplacements);
        String rawNameWithoutColor = EnumChatFormatting.getTextWithoutFormattingCodes(nameWithReplacements);
        String nameWithoutColor = ColorReplacementFeature.replace(rawNameWithoutColor);
        boolean emptyTitle = nameWithoutColor.replace(" ", "").isEmpty();

        HeadReplacements headReplacement = HeadReplacements.replaceHeadIDByItemName(rawNameWithoutColor);

        if (headReplacement != null && skyblockItem) {
            skyblockItem = headReplacement.isTurnToSbItem();
            if (skyblockItem) {
                skyblockId = headReplacement.getIdReplacement();
            } else {
                minecraftId = headReplacement.getIdReplacement();
            }
        } else {
            if (skyblockItem && !hasSkyblockItemID) {
                skyblockItem = false;
                minecraftId = "head";
            }
        }
        String typeText = skyblockItem ? pet ? "sbpet" : "sb" : "mc";
        String initialChar = emptyTitle ? "?" : "";

        WikiWriter.getInstance().getLogger().info("Writing item " + nameWithColor + " to wiki");
        if (nameWithColor.contains("[") || nameWithColor.contains("]") || nameWithColor.contains("{") || nameWithColor.contains("}")) {
            lore.add(0, nameWithColor);
            name = "e";
            baseItemRarity = Rarity.NONE;
            currentItemRarity = Rarity.NONE;
            nameWithColor = "e";
            nameWithoutColor = "e";
        }

        String loreAsString = JsonTextReplacementsFeature.replaceEverything(lore.stream().map(s -> "\"" + ColorReplacementFeature.replace(s) + "\"")
                .collect(Collectors.joining(", ")));

        this.petId = petId;
        this.minecraftId = minecraftId;
        this.skyblockId = skyblockId;
        this.name = name;
        this.nameWithoutColor = nameWithoutColor;
        this.lore = lore;
        this.loreAsString = loreAsString;
        this.stackSize = stackSize;
        this.currentItemRarity = currentItemRarity;
        this.emptyTitle = emptyTitle;
        this.typeText = typeText;
        this.showRarity = showRarity;
        this.pet = pet;
        this.initialChar = initialChar;
        this.skyblockItem = skyblockItem;
        this.nameWithColor = nameWithColor;
        this.referenceId = (skyblockItem ? pet ? petId : skyblockId.toLowerCase() : minecraftId);
    }

    public String convertToRecipeReference() {
        if (minecraftId.equals("")) return "";

        if (pet) {
            return "{{Item_" + petId + "_" + currentItemRarity.name().toLowerCase() + "}}";
        } else {
            return "{{Item_" + referenceId.replace(" ", "_").toLowerCase() + "}}";
        }
    }

    public String convertToWikiItem() {
        if (minecraftId.equals("")) return "";

        return initialChar + typeText + "," + (!showRarity ? "" : currentItemRarity.toString().toLowerCase()) + "," + referenceId
                + (emptyTitle ? "" : (minecraftId.equals(nameWithoutColor) ? "" : ":" + (showRarity ? nameWithoutColor : nameWithColor)))
                + (lore.isEmpty() && stackSize == 1 ? "" : ("," + NumberFormat.getInstance().format(stackSize) + (emptyTitle || lore.isEmpty() ? "" : ","
                + new UnicodeUnescaper().translate(StringEscapeUtils.escapeJava(loreAsString.substring(1, loreAsString.length() - 1))).replace("\\\"", "\""))));
    }
}
