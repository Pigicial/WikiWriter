package me.pigicial.wikiwriter.features;

import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.features.items.LoreFilters;
import me.pigicial.wikiwriter.features.items.TextReplacementPipeline;
import me.pigicial.wikiwriter.features.items.WikiItem;
import me.pigicial.wikiwriter.features.replacements.CollectionMenuReplacementPipeline;
import me.pigicial.wikiwriter.features.replacements.MenuModification;
import me.pigicial.wikiwriter.utils.Action;
import me.pigicial.wikiwriter.utils.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GUIStealerFeature extends KeyBindFeature {

    private final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public GUIStealerFeature(WikiWriter wikiWriter) {
        super(wikiWriter, "Copy Top GUI", GLFW.GLFW_KEY_I);
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
            List<ItemStack> items = ((SimpleInventory) chestHandler.getInventory()).getHeldStacks();
            String inventoryName = screen.getTitle().getString();

            process(items, inventoryName);
        }
    }

    @Override
    protected void handleException(Exception exception) {
        wikiWriter.sendMessage("Something went wrong when trying to clone this GUI, please report this with your latest.log file!");
        exception.printStackTrace();
    }

    protected void process(List<ItemStack> items, String inventoryName) {
        int size = items.size();
        int rows = size / 9;

        StringBuilder builder = new StringBuilder();

        if (isShopMode(items, size, rows)) {
            processShop(builder, inventoryName, items, rows);
            return;
        }

        if (isRecipeMenu(rows, inventoryName, items)) {
            processRecipe(builder, inventoryName, items);
            return;
        }

        ForgeRecipeType forgeRecipeType = getForgeRecipeType(inventoryName, items, rows);
        if (forgeRecipeType != null) {
            processForgeRecipe(builder, forgeRecipeType, items);
            return;
        }

        MenuModification modification = generateApplicableTextModifications(inventoryName, items);
        Consumer<TextReplacementPipeline> pipelineConsumer = modification == null ? null : modification.getTextPipelineConsumer();

        builder.append("<noinclude>[[Category:Inventory Templates]]</noinclude>\n{{Inventory\n")
                .append("|name=").append(inventoryName).append("\n")
                .append("|rows=").append(rows).append("\n");

        for (int i = 0; i < size; i++) {
            ItemStack itemStack = items.get(i);
            int horizontalPosition = 1 + (i % 9);
            int verticalPosition = (i / 9);

            WikiItem item = new WikiItem(itemStack, Action.COPYING_INVENTORY, pipelineConsumer);
            if (modification != null) {
                modification.getItemConsumer().accept(item, i);
            }

            String text = item.generateText(Action.COPYING_INVENTORY);

            builder.append("|")
                    .append(alphabet[verticalPosition])
                    .append(horizontalPosition).append("=")
                    .append(text)
                    .append("\n");
        }

        builder.append("}}");
        wikiWriter.sendMessage("Copied top GUI to clipboard.");
        wikiWriter.copyToClipboard(builder.toString());
    }

    @Nullable
    private MenuModification generateApplicableTextModifications(String inventoryName, List<ItemStack> items) {
        if (inventoryName.endsWith(" Collection")) {
            WikiWriter.getInstance().sendMessage("Test-B");
            return CollectionMenuReplacementPipeline.generatePipelineIsApplicable(items);
        }

        WikiWriter.getInstance().sendMessage("Fail-B 2");
        return null;
    }

    private boolean isShopMode(List<ItemStack> items, int size, int rows) {
        if (!wikiWriter.getConfig().shopMenuMode || rows < 3) {
            return false;
        }

        ItemStack firstPotentialShopItem = items.get(10);
        NbtCompound shopItemNbt = firstPotentialShopItem.getNbt();
        boolean doesFirstItemExist = !firstPotentialShopItem.isEmpty() && shopItemNbt != null;

        if (doesFirstItemExist) {
            NbtList loreTag = shopItemNbt.getCompound("display").getList("Lore", NbtElement.STRING_TYPE);
            List<String> lore = new ArrayList<>();
            for (int i = 0; i < loreTag.size(); i++) {
                lore.add(TextUtils.convertJsonTextToLegacy(loreTag.getString(i)));
            }

            LoreFilters.RemovedLore removeData = LoreFilters.checkAndFilter(lore, Action.COPYING_SHOP_INVENTORY);
            return removeData.detectedShopLore() && areTopAndBottomRowsCorrectForShop(items, size);
        }

        return false;
    }

    private boolean areTopAndBottomRowsCorrectForShop(List<ItemStack> items, int size) {
        for (int i = 0; i <= 8; i++) {
            ItemStack itemStack = items.get(i);
            if (!itemStack.isEmpty() && itemStack.getItem() != Items.BLACK_STAINED_GLASS_PANE) {
                return false;
            }
        }

        // Checks the bottom row
        int bottomRowPosition = 0;
        for (int i = size - 9; i <= size - 1; i++) {
            bottomRowPosition++;

            ItemStack itemStack = items.get(i);
            if (itemStack.isEmpty()) {
                continue;
            }

            Item targetItem = bottomRowPosition == 5 ? Items.HOPPER : Items.BLACK_STAINED_GLASS_PANE;
            if (itemStack.getItem() != targetItem) {
                return false;
            }
        }

        return true;
    }

    protected void processShop(StringBuilder builder, String inventoryName, List<ItemStack> items, int rows) {
        builder.append("{{Merchant")
                .append("\n")
                .append("|name=").append(inventoryName)
                .append("\n");

        for (int i = 0, max = (rows - 2) * 7; i < max; i++) {
            int row = 2 + (i / 7);
            int across = 2 + (i % 7);
            int slot = row * 9 - 9 + across - 1;

            ItemStack itemStack = items.get(slot);
            if (itemStack == null || itemStack.isEmpty()) {
                // no more shop items
                break;
            }

            WikiItem item = new WikiItem(itemStack, Action.COPYING_SHOP_INVENTORY);
            builder.append("|item").append(i + 1).append("=").append(item.generateText(Action.COPYING_SHOP_INVENTORY)).append("\n");
        }

        builder.append("}}\n<noinclude>[[Category:NPC UI Templates]]</noinclude>");
        wikiWriter.sendMessage("Copied top GUI shop to clipboard.");
        wikiWriter.copyToClipboard(builder.toString());
    }

    protected void processRecipe(StringBuilder builder, String inventoryName, List<ItemStack> items) {
        boolean craftItem = inventoryName.equalsIgnoreCase("Craft Item");
        ItemStack product = craftItem ? items.get(23) : items.get(25);
        if (craftItem && !product.isEmpty() && product.getItem() == Items.BARRIER) {
            wikiWriter.sendMessage("Cannot copy invalid recipe." + Formatting.GRAY + " (Barrier Detected)");
            return;
        }

        builder.append("{{Craft Item").append("\n");
        for (int i = 0; i < CRAFTING_TABLE_INGREDIENT_SLOTS.length; i++) {
            int integer = CRAFTING_TABLE_INGREDIENT_SLOTS[i];
            ItemStack itemStack = items.get(integer);
            if (itemStack.isEmpty()) {
                continue;
            }

            WikiItem item = new WikiItem(itemStack, Action.COPYING_RECIPE_INVENTORY);
            builder.append("|in").append(i + 1).append("=").append(item.generateText(Action.COPYING_RECIPE_INVENTORY)).append("\n");
        }

        WikiItem resultItem = product.isEmpty() ? null : new WikiItem(product, Action.COPYING_RECIPE_INVENTORY);
        if (resultItem != null) {
            builder.append("|out=").append(resultItem.generateText(Action.COPYING_RECIPE_INVENTORY)).append("\n");
        }

        builder.append("}}\n<noinclude>[[Category:Recipe Templates]]</noinclude>");

        wikiWriter.sendMessage("Copied top GUI recipe to clipboard.");
        wikiWriter.copyToClipboard(builder.toString());

        if (resultItem != null) {
            String itemID = resultItem.getSkyBlockId();
            if (!itemID.isEmpty()) {
                String templatePageName = "Template:Recipe/" + itemID;
                wikiWriter.suggestPageLink(templatePageName);
            }
        }
    }

    private ForgeRecipeType getForgeRecipeType(String inventoryName, List<ItemStack> items, int rows) {
        if (rows == 6 && inventoryName.equalsIgnoreCase("Confirm Process")) {
            Item forgeRecipeTypeIndicatorItem = items.get(13).getItem();

            for (ForgeRecipeType recipeType : ForgeRecipeType.RECIPE_TYPES) {
                if (recipeType.menuItemIndicator == forgeRecipeTypeIndicatorItem) {
                    return recipeType;
                }
            }
        }

        return null;
    }

    private void processForgeRecipe(StringBuilder builder, ForgeRecipeType forgeRecipeType, List<ItemStack> items) {
        builder.append("{{Forge Item")
                .append("\n")
                .append("|type=").append(forgeRecipeType.name().toLowerCase())
                .append("\n");

        for (int i = 0; i < FORGE_POSITION_TO_SLOT_MAP.length; i++) {
            int position = FORGE_POSITION_TO_SLOT_MAP[i];
            ItemStack itemStack = items.get(position);
            if (itemStack.getItem() == Items.BLACK_STAINED_GLASS_PANE) {
                // menu glass item, skip!
                continue;
            }

            WikiItem item = new WikiItem(itemStack, Action.COPYING_RECIPE_INVENTORY);
            builder.append("|in").append(i + 1).append("=").append(item.generateText(Action.COPYING_RECIPE_INVENTORY)).append("\n");
        }

        ItemStack product = items.get(FORCE_RECIPE_RESULT_SLOT);
        WikiItem resultItem = product.isEmpty() ? null : new WikiItem(product, Action.COPYING_RECIPE_INVENTORY);
        if (resultItem != null) {
            builder.append("|out=").append(resultItem.generateText(Action.COPYING_RECIPE_INVENTORY)).append("\n");
        }

        builder.append("}}\n<noinclude>[[Category:Recipe Templates]]</noinclude>");

        wikiWriter.sendMessage("Copied top GUI forge recipe to clipboard.");
        wikiWriter.copyToClipboard(builder.toString());

        if (resultItem != null) {
            String itemID = resultItem.getSkyBlockId();
            if (!itemID.isEmpty()) {
                String templatePageName = "Template:Recipe/" + itemID;
                wikiWriter.suggestPageLink(templatePageName);
            }
        }
    }

    private enum ForgeRecipeType {
        CAST(Items.LAVA_BUCKET),
        REFINE(Items.FURNACE);

        private static final ForgeRecipeType[] RECIPE_TYPES = values();

        private final Item menuItemIndicator;

        ForgeRecipeType(Item menuItemIndicator) {
            this.menuItemIndicator = menuItemIndicator;
        }
    }
}
