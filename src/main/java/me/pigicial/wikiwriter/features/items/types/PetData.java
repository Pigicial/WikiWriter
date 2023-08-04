package me.pigicial.wikiwriter.features.items.types;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import me.pigicial.wikiwriter.WikiWriter;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public record PetData(String type, String tier, @SerializedName("hideInfo") boolean mysteryPet) implements TextureAndReferenceData {

    @Nullable
    public static PetData getPetInfo(NbtCompound extraAttributes) {
        String petInfoNbt = extraAttributes.getString("petInfo");
        if (petInfoNbt.isEmpty()) {
            return null;
        }

        try {
            return new Gson().fromJson(petInfoNbt, PetData.class);
        } catch (JsonSyntaxException e) {
            WikiWriter.getInstance().sendMessage("Invalid petInfo NBT detected");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getTemplateReference() {
        if (mysteryPet) {
            return "{{Item_" + "pet_craft_" + type.toLowerCase() + "}}";
        }

        return "{{Item_pet_" + type.toLowerCase() + "_" + tier.toLowerCase() + "}}";
    }

    @Override
    public String getTextureLink() {
        return type.toLowerCase();
    }

    @Override
    public String getTextureType() {
        return "sbpet";
    }
}
