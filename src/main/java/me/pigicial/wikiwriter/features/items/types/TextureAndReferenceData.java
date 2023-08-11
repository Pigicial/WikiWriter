package me.pigicial.wikiwriter.features.items.types;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public interface TextureAndReferenceData {
    String getTemplateReference();

    String getTextureLink();

    String getTextureType();

    static TextureAndReferenceData getFromExtraAttributes(ItemStack itemStack, NbtCompound extraAttributes) {
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

        return EnchantedBookData.getEnchantedBookData(itemStack, extraAttributes);
    }
}
