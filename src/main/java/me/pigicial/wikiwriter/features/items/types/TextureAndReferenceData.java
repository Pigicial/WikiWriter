package me.pigicial.wikiwriter.features.items.types;

import me.pigicial.wikiwriter.features.items.WikiItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public interface TextureAndReferenceData {
    String getLoreTemplateReference();

    String getTextureLink();

    String getTextureType();

    static TextureAndReferenceData getFromExtraAttributes(WikiItem wikiItem, ItemStack itemStack, NbtCompound extraAttributes) {
        PetData petInfo = PetData.getPetInfo(extraAttributes);
        if (petInfo != null) {
            return petInfo;
        }

        RuneData runeData = RuneData.getRuneData(extraAttributes);
        if (runeData != null) {
            return runeData;
        }

        AbicaseData abicaseData = AbicaseData.getAbicaseData(extraAttributes);
        if (abicaseData != null) {
            return abicaseData;
        }

        if (extraAttributes.getString("id").equals("POTION")) {
            return new PotionData(wikiItem, extraAttributes);
        }

        return EnchantedBookData.getEnchantedBookData(itemStack, extraAttributes);
    }
}
