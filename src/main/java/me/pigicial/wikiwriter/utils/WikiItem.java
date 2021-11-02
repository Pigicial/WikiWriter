package me.pigicial.wikiwriter.utils;

import lombok.Getter;
import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.features.ColorReplacementFeature;
import me.pigicial.wikiwriter.features.JsonTextReplacementsFeature;
import me.pigicial.wikiwriter.features.LoreRemovalFeature;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.StringEscapeUtils;
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
    private final Rarity skyblockRarity;
    private final int rarityUpgrades;
    private final int stackSize;
    private final boolean enchanted;

    public WikiItem(String name, String minecraftId, List<String> lore, String skyblockId, Rarity skyblockRarity, int rarityUpgrades, int stackSize, boolean enchanted) {
        this.name = name;
        this.minecraftId = minecraftId;
        this.lore = lore;
        this.skyblockId = skyblockId;
        this.skyblockRarity = skyblockRarity;
        this.rarityUpgrades = rarityUpgrades;
        this.stackSize = stackSize;
        this.enchanted = enchanted;
    }

    private WikiItem() {
        this.name = "";
        this.minecraftId = "";
        this.lore = new ArrayList<>();
        this.skyblockId = null;
        this.skyblockRarity = Rarity.NONE;
        this.rarityUpgrades = 0;
        this.stackSize = 1;
        this.enchanted = false;
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
        Rarity rarity = Rarity.NONE;

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
                    rarity = r;
                    if (rarityUpgrades > 0) {
                        for (int i = 0; i < rarityUpgrades; i++) {
                            rarity = rarity.getPreviousRarity();
                        }
                    }
                    break loreLoop;
                }
            }
        }

        if (rarity == Rarity.NONE && !name.isEmpty()) {

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
                                rarity = r;
                                if (rarityUpgrades > 0) {
                                    for (int i = 0; i < rarityUpgrades; i++) {
                                        rarity = rarity.getPreviousRarity();
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
        if (!minecraftId.equalsIgnoreCase("head") && hasEnchantments) {
            minecraftId = "enchanted_" + minecraftId;
        }

        return new WikiItem(name, minecraftId, lore, skyblockId, rarity, rarityUpgrades, stack.stackSize, hasEnchantments);

    }

    public String convertToWikiItem() {
        if (minecraftId.equals("")) return "";

        String lore = JsonTextReplacementsFeature.replaceEverything(this.lore.stream().map(s -> "\"" + ColorReplacementFeature.replace(s) + "\"")
                        .collect(Collectors.joining(", ")));

        boolean skyblockItem = minecraftId.equalsIgnoreCase("head");
        String typeText = skyblockItem ? "sb" : "mc";

        String name = ColorReplacementFeature.replace(JsonTextReplacementsFeature.replaceEverything(this.name.replaceAll("(?i)§[0-9A-FK-ORX]", "")));
        WikiWriter.getInstance().getLogger().info(name);

        boolean emptyTitle = name.replace(" ", "").isEmpty();

        String initialChar = emptyTitle ? "?" : "";

        String goodName = EnumChatFormatting.getTextWithoutFormattingCodes(name);
        //WikiWriter.getInstance().sendMessage("Minecraft ID: " + minecraftId + ", good name: " + goodName);

        return initialChar + typeText + "," + skyblockRarity.toString().toLowerCase() + "," + (skyblockItem ? skyblockId : minecraftId)
                + (emptyTitle ? "" : (minecraftId.equals(goodName) ? "" : ":" + goodName))
                //
                + (lore.isEmpty() && stackSize == 1 ? "" : ("," + NumberFormat.getInstance().format(stackSize) + (emptyTitle ? "" : "," + StringEscapeUtils.escapeJava(lore.substring(1, lore.length() - 1)))));
    }

    public void logData() {
        Logger logger = WikiWriter.getInstance().getLogger();
        logger.log(Level.INFO, "Name: " + this.name);
        logger.log(Level.INFO, "Minecraft ID: " + this.minecraftId);
        logger.log(Level.INFO, "Skyblock ID: " + this.skyblockId);
        logger.log(Level.INFO, "Skyblock Rarity: " + this.skyblockRarity);
        logger.log(Level.INFO, "Lore: " + this.lore);
        logger.log(Level.INFO, "Rarity Upgrades: " + this.rarityUpgrades);
        logger.log(Level.INFO, "Stack Size: " + this.stackSize);
    }
}
