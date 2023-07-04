package me.pigicial.wikiwriter.utils;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.Nullable;

public record PetInfo(String type, @SerializedName("hideInfo") @Getter boolean mysteryPet) {

    @Nullable
    public static PetInfo getPetInfo(NbtCompound extraAttributes) {
        NbtElement petInfoNbt = extraAttributes.get("petInfo");
        if (petInfoNbt instanceof NbtCompound petInfo) {
            String type = petInfo.getString("type").toLowerCase();
            boolean hideInfo = petInfo.getBoolean("hideInfo");
            return new PetInfo(type, hideInfo);
        }

        return null;
    }
}
