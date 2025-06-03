package me.pigicial.wikiwriter.features.items.types;

import com.google.common.collect.Iterators;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public record RuneData(String id, String texture, int level) implements TextureAndReferenceData {

    @Nullable
    public static RuneData getRuneData(NbtCompound extraAttributes) {
        NbtCompound runeInfo = extraAttributes.getCompound("runes");
        if (runeInfo == null || runeInfo.isEmpty()) {
            return null;
        }

        Set<String> strings = runeInfo.getKeys();
        if (strings.isEmpty()) {
            return null;
        }

        String runeId = Iterators.get(strings.iterator(), 0);
        String texture = "rune_" + switch (runeId) { // textures on wiki use names, and these ids convert to these names
            case "BLOOD_2" -> "blood";
            case "DRAGON" -> "end";
            case "zombie_slayer" -> "pestilence";
            case "magic" -> "magical";
            default -> runeId.toLowerCase();
        };

        int runeLevel = runeInfo.getInt(runeId);
        return new RuneData(runeId, texture, runeLevel);
    }

    @Override
    public String getLoreTemplateReference() {
        return "{{Item/RUNE_" + id.toUpperCase() + "_" + level + "}}";
    }

    @Override
    public String getTextureLink() {
        return texture;
    }

    @Override
    public String getTextureType() {
        return "sb";
    }
}
