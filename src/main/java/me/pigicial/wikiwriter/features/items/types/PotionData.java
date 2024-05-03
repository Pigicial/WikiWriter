package me.pigicial.wikiwriter.features.items.types;

import me.pigicial.wikiwriter.features.items.WikiItem;
import net.minecraft.nbt.NbtCompound;

public record PotionData(WikiItem item, NbtCompound extraAttributes) implements TextureAndReferenceData {
    @Override
    public String getLoreTemplateReference() {
        if (extraAttributes.contains("potion")) {
            return "{{Item_" + item.getNameWithColor().replace("_", " ").toLowerCase() + "}}";
        }
        return "{{Item/POTION|real_lore}}";
    }

    @Override
    public String getTextureLink() {
        return item.getBaseTextureLink();
    }

    @Override
    public String getTextureType() {
        return "mc";
    }
}
