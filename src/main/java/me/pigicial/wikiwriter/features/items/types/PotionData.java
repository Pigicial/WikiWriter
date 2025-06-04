package me.pigicial.wikiwriter.features.items.types;

import me.pigicial.wikiwriter.features.items.WikiItem;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public record PotionData(WikiItem item, NbtCompound extraAttributes) implements TextureAndReferenceData {

    @Nullable
    public static PotionData getPotionData(WikiItem wikiItem, NbtCompound extraAttributes) {
        if (extraAttributes.getString("id").equals("POTION")) {
            return new PotionData(wikiItem, extraAttributes);
        }

        return null;
    }

    @Override
    public String getLoreTemplateReference() {
        if (extraAttributes.contains("potion")) {
            return "{{Item_" + item.getName().replace("_", " ").toLowerCase() + "}}";
        }
        return "{{Item/POTION|lore}}";
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
