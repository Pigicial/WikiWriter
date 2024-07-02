package me.pigicial.wikiwriter.features;

import lombok.AllArgsConstructor;
import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.features.items.LoreFilters;
import me.pigicial.wikiwriter.features.items.types.PetData;
import me.pigicial.wikiwriter.utils.Action;
import me.pigicial.wikiwriter.utils.StyleConversions;
import me.pigicial.wikiwriter.utils.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.time.Period;
import java.util.*;

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

        ScreenHandler handler = player.currentScreenHandler;
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen instanceof GenericContainerScreen && handler instanceof GenericContainerScreenHandler chestHandler) {
            String inventoryName = screen.getTitle().getString();

            List<ItemStack> items = ((SimpleInventory) chestHandler.getInventory()).getHeldStacks();
            int size = items.size();
            int rows = size / 9;

            if (isCraftingRecipeMenu(rows, inventoryName, items) || isForceRecipeMenu(inventoryName, rows)) {
                processRecipe(inventoryName, items);
            }
        }
    }

    protected void processRecipe(String inventoryName, List<ItemStack> items) {
        boolean isCraftingTable = inventoryName.equalsIgnoreCase("Craft Item");
        boolean isForceRecipe = isForceRecipeMenu(inventoryName, items.size() / 9);
        ItemStack product = isForceRecipe ? items.get(16) : (isCraftingTable ? items.get(23) : items.get(25));
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
        builder.append("|itemimage = {{formatnum:{{{2|1}}}}} [[File:").append(output.fileName)
                .append("|20px|link=").append(output.name).append("]]\n");
        builder.append("|itemname = [[").append(output.name).append("]]\n\n");
        builder.append("|resources = \n");

        List<ItemInfo> ingredients = new ArrayList<>();

        for (int slot : isForceRecipe ? FORGE_POSITION_TO_SLOT_MAP : CRAFTING_TABLE_INGREDIENT_SLOTS) {
            ItemStack ingredientItem = items.get(slot);
            if (ingredientItem.isEmpty() || ingredientItem.getItem() == Items.BLACK_STAINED_GLASS_PANE) {
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
        if (isForceRecipe) {
            builder.append("<!--- Before publishing, delete invalid ingredient templates! --->\n");
        }
        for (ItemInfo ingredient : ingredients) {
            if (isForceRecipe) {
                // Try both, since there's no way of knowing if it's forgable on its own
                // (force menu doesn't have "click to view recipe" or anything)
                builder.append(ingredient.convertToBaseTreeResource())
                        .append("\n")
                        .append(ingredient.convertToTemplateTreeResource())
                        .append("\n");

            } else {
                builder.append(ingredient.convertToCollapsibleTreeResource()).append("\n");
            }
        }

        builder.append("}}<noinclude>[[Category:CollapsibleTree]]</noinclude>");
        wikiWriter.sendMessage("Copied " + (isForceRecipe ? "forge" : "crafting") + " recipe tree to clipboard.");
        wikiWriter.copyToClipboard(builder.toString());

        if (!output.id.isEmpty()) {
            String recipeTreeTemplate = "Template:CollapsibleTree/Item/" + output.id.toLowerCase();
            wikiWriter.suggestPageLink(recipeTreeTemplate);
        }
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

        private String convertToTemplateTreeResource() {
            return "{{CollapsibleTree/Item/" + id.toLowerCase() + "|Item|{{#expr:" + amount + " * {{{2|1}}}}}}}";
        }

        private String convertToBaseTreeResource() {
            return "{{CollapsibleTree/Base|{{formatnum:{{#expr:" + amount + " * {{{2|1}}}}}}} {{Item/" + id + "|is=20}}}}";
        }

        public static ItemInfo getItemInfo(ItemStack itemStack) {
            ComponentMap components = itemStack.getComponents();
            if (components == null) {
                components = ComponentMap.EMPTY;
            }

            Text nameAsText = components.get(DataComponentTypes.CUSTOM_NAME);
            String name = nameAsText == null ? "" : StyleConversions.stripColor(TextUtils.convertToLegacyText(nameAsText));

            NbtComponent customDataComponent = components.get(DataComponentTypes.CUSTOM_DATA);
            NbtCompound customData = customDataComponent == null
                    ? new NbtCompound()
                    : customDataComponent.copyNbt();

            String skyBlockId = customData.getString("id");
            PetData petData = PetData.getPetInfo(customData);
            if (petData != null) {
                // fix id and brackets in name
                skyBlockId = petData.type().toLowerCase() + "_pet";
                name = name.contains("]") ? name.substring(name.indexOf("]") + 1).trim() + " Pet" : name;
            }
            int amount = itemStack.getCount();

            List<String> lore = TextUtils.parseJsonLore(components.get(DataComponentTypes.LORE));
            boolean hasRecipes = LoreFilters.checkAndFilter(lore, Action.COPYING_RECIPE_INVENTORY)
                    .hasFeatures(LoreFilters.VIEW_RECIPE, LoreFilters.VIEW_RECIPES);

            String minecraftId = itemStack.getItem().getName(itemStack).getString()
                    .toLowerCase().replace(" ", "_").replace("'", "");

            // not perfect, but good enough for 98% of items
            String fileName;
            if (petData != null) {
                fileName = "SkyBlock_pets_" + petData.type().toLowerCase() + ".png";
            } else if (itemStack.getItem() == Items.PLAYER_HEAD) {
                fileName = "SkyBlock_items_" + skyBlockId.toLowerCase() + ".png";
            } else if (itemStack.hasGlint()) {
                fileName = "SkyBlock_items_enchanted_" + minecraftId + ".gif";
            } else {
                fileName = "Minecraft_items_" + minecraftId + ".png";
            }

            return new ItemInfo(name, fileName, skyBlockId, hasRecipes, amount);
        }
    }

    private boolean isForceRecipeMenu(String inventoryName, int rows) {
        return inventoryName.equals("Confirm Process") && rows == 6;
    }
}
