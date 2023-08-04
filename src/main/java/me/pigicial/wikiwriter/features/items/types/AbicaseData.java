package me.pigicial.wikiwriter.features.items.types;

import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public record AbicaseData(String model) implements TextureAndReferenceData {

    @Nullable
    public static AbicaseData getAbicaseData(NbtCompound extraAttributes) {
        String id = extraAttributes.getString("id");
        String model = extraAttributes.getString("model");
        return id.contains("ABICASE") && !model.isEmpty() ? new AbicaseData(model.toLowerCase()) : null;
    }

    @Override
    public String getTemplateReference() {
        return "{{Item_abicase_" + model + "}}";
    }

    @Override
    public String getTextureLink() {
        return "abicase_" + model;
    }

    @Override
    public String getTextureType() {
        return "sb";
    }
}
