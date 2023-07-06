package me.pigicial.wikiwriter.features;

import net.minecraft.item.ItemStack;

import java.util.List;

public final class RecipeUtils {

    public static final int CRAFTING_TABLE_OR_RECIPE_REQUIRED_SLOT = 23;
    public static final int FORCE_RECIPE_RESULT_SLOT = 16;

    static final int[] CRAFTING_TABLE_INGREDIENT_SLOTS = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30};
    static final int[] FORGE_POSITION_TO_SLOT_MAP = new int[]{10, 11, 19, 20, 28, 29, 37, 38};

    private RecipeUtils() {

    }

    public static boolean isRecipeMenu(int rows, String inventoryName, List<ItemStack> items) {
        return (inventoryName.equalsIgnoreCase("Craft Item") || inventoryName.endsWith("Recipe"))
                && rows == 6
                && !items.get(RecipeUtils.CRAFTING_TABLE_OR_RECIPE_REQUIRED_SLOT).isEmpty();
    }


}
