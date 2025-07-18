package me.pigicial.wikiwriter.features.items.types;

import com.google.common.collect.Iterators;
import me.pigicial.wikiwriter.utils.TextUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public record EnchantedBookData(String onlyEnchantmentId, int onlyEnchantmentLevel) implements TextureAndReferenceData {

    @Nullable
    public static EnchantedBookData getEnchantedBookData(ItemStack itemStack, NbtCompound extraAttributes) {
        if (itemStack.getItem() != Items.ENCHANTED_BOOK) {
            return null;
        }

        NbtCompound enchantmentInfo = extraAttributes.getCompound("enchantments");
        if (enchantmentInfo == null || enchantmentInfo.getSize() != 1) { // only allow enchanted books with 1 size
            return null;
        }

        String enchantment = Iterators.get(enchantmentInfo.getKeys().iterator(), 0);
        int level = enchantmentInfo.getInt(enchantment);

        return new EnchantedBookData(enchantment.toUpperCase(), level);
    }

    @Override
    public String getLoreTemplateReference() {
        return "{{Enchantment/" + onlyEnchantmentId + "|lore|" + TextUtils.convertToRomanNumeral(onlyEnchantmentLevel) + "}}";
    }

    @Override
    public String getTextureLink() {
        return "enchanted_book";
    }

    @Override
    public String getTextureType() {
        return "mc";
    }
}
