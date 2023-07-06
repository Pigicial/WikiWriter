package me.pigicial.wikiwriter.features;

import lombok.AllArgsConstructor;
import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.utils.Action;
import me.pigicial.wikiwriter.utils.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.*;

import static me.pigicial.wikiwriter.features.RecipeUtils.CRAFTING_TABLE_INGREDIENT_SLOTS;

public class RecipeTreeFeature extends KeyBindFeature {

    public RecipeTreeFeature(WikiWriter wikiWriter) {
        super(wikiWriter, "Copy Recipe Tree", GLFW.GLFW_KEY_L);
    }

    @Override
    protected void onKeyPress(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }

        ScreenHandler currentScreenHandler = player.currentScreenHandler;
        Screen currentScreen = MinecraftClient.getInstance().currentScreen;
        if (currentScreen == null || !(currentScreenHandler instanceof GenericContainerScreenHandler chestHandler)) {
            return;
        }

        String inventoryName = currentScreen.getTitle().getString();

        List<ItemStack> items = ((SimpleInventory) chestHandler.getInventory()).stacks;
        int size = items.size();
        int rows = size / 9;

        if (RecipeUtils.isRecipeMenu(rows, inventoryName, items)) {
            processRecipe(inventoryName, items);
        }
    }

    protected void processRecipe(String inventoryName, List<ItemStack> items) {
        boolean isCraftingTable = inventoryName.equalsIgnoreCase("Craft Item");
        ItemStack product = isCraftingTable ? items.get(23) : items.get(25);
        if (isCraftingTable && !product.isEmpty() && product.getItem() == Items.BARRIER) {
            wikiWriter.sendMessage("Cannot copy invalid recipe tree." + Formatting.GRAY + " (Barrier Detected)");
            return;
        }

        if (product.isEmpty()) {
            wikiWriter.sendMessage("Cannot copy invalid recipe tree." + Formatting.GRAY + " (No Result)");
            return;
        }

        ItemInfo output = ItemInfo.getItemInfo(product);

        StringBuilder builder = new StringBuilder();
        builder.append("{{CollapsibleTree|{{{1|Item}}}\n");
        builder.append("|itemimage = {{formatnum:{{{2|1|}}}}} [[File:").append(output.fileName)
                .append("|20px|link=").append(output.name).append("]]\n");
        builder.append("|itemname = [[").append(output.name).append("]]\n\n");
        builder.append("|resources = \n");

        List<ItemInfo> ingredients = new ArrayList<>();

        for (int slot : CRAFTING_TABLE_INGREDIENT_SLOTS) {
            ItemStack ingredientItem = items.get(slot);
            if (ingredientItem.isEmpty()) {
                continue;
            }

            ItemInfo ingredientInfo = ItemInfo.getItemInfo(ingredientItem);

            Optional<ItemInfo> matchingIngredient = ingredients.stream()
                    .filter(itemInfo -> itemInfo.id.equals(ingredientInfo.id))
                    .findFirst();

            // Ingredients of the same type should be grouped together
            if (matchingIngredient.isEmpty()) {
                ingredients.add(ingredientInfo);
            } else {
                matchingIngredient.get().amount += ingredientInfo.amount;
            }
        }

        ingredients.sort(Collections.reverseOrder(Comparator.comparing(ItemInfo::getAmount)));
        for (ItemInfo ingredient : ingredients) {
            builder.append(ingredient.convertToCollapsibleTreeResource()).append("\n");
        }

        builder.append("}}\n<noinclude>[[Category:CollapsibleTree]]</noinclude>");
        wikiWriter.sendMessage("Copied recipe tree to clipboard.");
        wikiWriter.copyToClipboard(builder.toString());
    }

    @Override
    protected void handleException(Exception exception) {
        wikiWriter.sendMessage("Something went wrong when generate this recipe tree");
        exception.printStackTrace();
    }

    @AllArgsConstructor
    private static final class ItemInfo {
        private final String name;
        private final String fileName;
        private final String id;
        private final boolean hasRecipes;
        private int amount;

        private int getAmount() {
            return amount;
        }

        private String convertToCollapsibleTreeResource() {
            if (hasRecipes) {
                return "{{CollapsibleTree/Item/" + id.toLowerCase() + "|Item|{{#expr:" + amount + " * {{{2|1}}}}}}}";
            } else {
                return "{{CollapsibleTree/Base|{{formatnum:{{#expr:" + amount + " * {{{2|1}}}}}}} {{Item/" + id + "|is=20}}}}";
            }
        }

        public static ItemInfo getItemInfo(ItemStack itemStack) {
            NbtCompound nbt = itemStack.getNbt();
            if (nbt == null) {
                nbt = new NbtCompound();
            }

            String name = Formatting.strip(TextUtils.convertJsonTextToLegacy(Text.Serializer.toJson(itemStack.getName())));

            NbtCompound extraAttributes = nbt.getCompound("ExtraAttributes");
            String skyBlockId = extraAttributes.getString("id").replace(":", ".");
            int amount = itemStack.getCount();

            List<String> lore = TextUtils.parseJsonLore(nbt.getCompound("display"));
            boolean hasRecipes = LoreRemovalFeature.checkAndFilter(lore, Action.COPYING_RECIPE_INVENTORY)
                    .hasFeatures(LoreRemovalFeature.VIEW_RECIPE, LoreRemovalFeature.VIEW_RECIPES);

            String minecraftId = itemStack.getItem().getName(itemStack).getString()
                    .toLowerCase().replace(" ", "_").replace("'", "");

            // not perfect, but good enough for 98% of items
            String fileName;
            if (itemStack.getItem() == Items.PLAYER_HEAD) {
                fileName = "SkyBlock_items_" + skyBlockId + ".png";
            } else if (itemStack.hasGlint()) {
                fileName = "SkyBlock_items_enchanted_" + minecraftId + ".gif";
            } else {
                fileName = "Minecraft_items_" + minecraftId + ".png";
            }

            return new ItemInfo(name, fileName, skyBlockId, hasRecipes, amount);
        }
    }
}
