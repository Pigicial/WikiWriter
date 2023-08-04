package me.pigicial.wikiwriter.features.items;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import me.pigicial.wikiwriter.WikiWriter;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public record PetInfo(String type, @SerializedName("hideInfo") boolean mysteryPet) {

    @Nullable
    public static PetInfo getPetInfo(NbtCompound extraAttributes) {
        String petInfoNbt = extraAttributes.getString("petInfo");
        if (petInfoNbt.isEmpty()) {
            return null;
        }

        try {
            return new Gson().fromJson(petInfoNbt, PetInfo.class);
        } catch (JsonSyntaxException e) {
            WikiWriter.getInstance().sendMessage("Invalid petInfo NBT detected");
            e.printStackTrace();
            return null;
        }
    }
}
