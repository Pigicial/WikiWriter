package me.pigicial.wikiwriter.utils;

import lombok.Getter;
import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.features.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.translate.UnicodeUnescaper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Getter
public class WikiItem {
    public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)§[0-9A-FK-ORX]");
    private final String name;
    private final String minecraftId;
    private final List<String> lore;
    private final String skyblockId;
    private final Rarity currentItemRarity;
    private final Rarity baseItemRarity;
    private final int rarityUpgrades;
    private final int stackSize;
    private final boolean enchanted;
    private final long color;

    public WikiItem(String name, String minecraftId, List<String> lore, String skyblockId, Rarity currentItemRarity, Rarity baseItemRarity, int rarityUpgrades, int stackSize, boolean enchanted, long color) {
        this.name = name;
        this.minecraftId = minecraftId;
        this.lore = lore;
        this.skyblockId = skyblockId;
        this.currentItemRarity = currentItemRarity;
        this.baseItemRarity = baseItemRarity;
        this.rarityUpgrades = rarityUpgrades;
        this.stackSize = stackSize;
        this.enchanted = enchanted;
        this.color = color;
    }

    private WikiItem() {
        this.name = "";
        this.minecraftId = "";
        this.lore = new ArrayList<>();
        this.skyblockId = "";
        this.currentItemRarity = Rarity.NONE;
        this.baseItemRarity = Rarity.NONE;
        this.rarityUpgrades = 0;
        this.stackSize = 1;
        this.enchanted = false;
        this.color = 0;
    }

    public static WikiItem fromItemStack(ItemStack stack) {
        if (stack == null) return new WikiItem();
        NBTTagCompound nbt = stack.getTagCompound();
        // I don't think this can be null but there's null checks in ItemStack so just in case
        if (nbt == null) nbt = new NBTTagCompound();

        NBTTagCompound extraAttributes = nbt.getCompoundTag("ExtraAttributes");

        NBTTagCompound display = nbt.getCompoundTag("display");
        NBTTagList loreTag = display.getTagList("Lore", 8); // 8 is NBTTagString from NBTBase#createNewByType

        List<String> lore = new ArrayList<>();
        for (int i = 0; i < loreTag.tagCount(); i++)
            lore.add(loreTag.getStringTagAt(i));

        LoreRemovalFeature.filterLore(lore);

        String name = stack.getDisplayName();
        Rarity currentItemRarity = Rarity.NONE;
        Rarity baseItemRarity = Rarity.NONE;

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

        String skyblockId = extraAttributes.getString("id");

        boolean hasEnchantments = nbt.getTagList("ench", 8).tagCount() > 0;
        String minecraftId = stack.getItem().getItemStackDisplayName(stack).toLowerCase().replace(" ", "_");

        boolean head = minecraftId.contains("head");
        boolean skyblockItem = head && !minecraftId.equalsIgnoreCase("Zombie_Head") && !minecraftId.equalsIgnoreCase("Creeper_Head") ;

        if (skyblockItem) {
            minecraftId = "head";
        }

        if (!minecraftId.equalsIgnoreCase("head") && hasEnchantments) {
            minecraftId = "enchanted_" + minecraftId;
        }

        long color = display.getLong("color");

        return new WikiItem(name, minecraftId, lore, skyblockId, currentItemRarity, baseItemRarity, rarityUpgrades, stack.stackSize, hasEnchantments, color);

    }

    public String convertToWikiItem() {
        if (minecraftId.equals("")) return "";

        String lore = JsonTextReplacementsFeature.replaceEverything(this.lore.stream().map(s -> "\"" + ColorReplacementFeature.replace(s) + "\"")
                        .collect(Collectors.joining(", ")));

        String name = ColorReplacementFeature.replace(JsonTextReplacementsFeature.replaceEverything(this.name.replaceAll("(?i)§[0-9A-FK-ORX]", "")));
        String goodName = EnumChatFormatting.getTextWithoutFormattingCodes(name);

        String minecraftId = this.minecraftId;
        String skyblockId = this.skyblockId;

        boolean head = minecraftId.equalsIgnoreCase("head");
        boolean skyblockItem = head && !skyblockId.equals("");
        String headReplacement = HeadReplacements.replaceHeadIDByItemName(goodName);
        if (!headReplacement.equalsIgnoreCase("head") && !skyblockItem) {
            skyblockItem = true;
            skyblockId = headReplacement;
        }
        String typeText = skyblockItem ? "sb" : "mc";

        boolean emptyTitle = name.replace(" ", "").isEmpty();

        String initialChar = emptyTitle ? "?" : "";

        /*
        if (minecraftId.startsWith("leather") && color != 0) {
            WikiWriter.getInstance().sendMessage("scanning for color " + color);
            LeatherColorFinderFeature color = LeatherColorFinderFeature.findColor((int) this.color);
            WikiWriter.getInstance().sendMessage("color " + color.name() + " found");
            if (color != LeatherColorFinderFeature.DEFAULT) {
                minecraftId = minecraftId + "_" + color.name().toLowerCase();
            }
        }

         */

        //WikiWriter.getInstance().sendMessage("Minecraft ID: " + minecraftId + ", good name: " + goodName);

        return initialChar + typeText + "," + currentItemRarity.toString().toLowerCase() + "," + (skyblockItem ? skyblockId.toLowerCase() : minecraftId)
                + (emptyTitle ? "" : (minecraftId.equals(goodName) ? "" : ":" + goodName))
                + (lore.isEmpty() && stackSize == 1 ? "" : ("," + NumberFormat.getInstance().format(stackSize) + (emptyTitle || lore.isEmpty() ? "" : "," + new UnicodeUnescaper().translate(StringEscapeUtils.escapeJava(lore.substring(1, lore.length() - 1))).replace("\\\"", "\""))));
    }
}
