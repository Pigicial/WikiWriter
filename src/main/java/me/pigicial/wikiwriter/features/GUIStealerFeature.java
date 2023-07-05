package me.pigicial.wikiwriter.features;

import me.pigicial.wikiwriter.WikiItem;
import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.config.Config;
import me.pigicial.wikiwriter.utils.Action;
import me.pigicial.wikiwriter.utils.TextUtils;
import net.minecraft.client.MinecraftClient;
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
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIStealerFeature extends KeyBindFeature {

    private static final int CRAFTING_TABLE_OR_RECIPE_REQUIRED_SLOT = 23;

    private final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private final Map<Integer, Integer> craftingTablePositionToSlotMap = new HashMap<>();
    private final Map<Integer, Integer> forgePositionToSlotMap = new HashMap<>();

    public GUIStealerFeature(WikiWriter wikiWriter) {
        super(wikiWriter, "Copy Top GUI", GLFW.GLFW_KEY_I);

        craftingTablePositionToSlotMap.put(1, 10);
        craftingTablePositionToSlotMap.put(2, 11);
        craftingTablePositionToSlotMap.put(3, 12);
        craftingTablePositionToSlotMap.put(4, 19);
        craftingTablePositionToSlotMap.put(5, 20);
        craftingTablePositionToSlotMap.put(6, 21);
        craftingTablePositionToSlotMap.put(7, 28);
        craftingTablePositionToSlotMap.put(8, 29);
        craftingTablePositionToSlotMap.put(9, 30);

        forgePositionToSlotMap.put(1, 10);
        forgePositionToSlotMap.put(2, 11);
        forgePositionToSlotMap.put(3, 19);
        forgePositionToSlotMap.put(4, 20);
        forgePositionToSlotMap.put(5, 28);
        forgePositionToSlotMap.put(6, 29);
        forgePositionToSlotMap.put(7, 37);
        forgePositionToSlotMap.put(8, 38);
    }

    @Override
    protected void onKeyPress(MinecraftClient client) {
        try {
            Config config = wikiWriter.getConfig();
            ClientPlayerEntity player = client.player;

            if (!config.copyGUI || player == null) {
                return;
            }

            ScreenHandler currentScreenHandler = player.currentScreenHandler;
            if (!(currentScreenHandler instanceof GenericContainerScreenHandler chestHandler)) {
                return;
            }

            List<ItemStack> items = ((SimpleInventory) chestHandler.getInventory()).stacks;
            String inventoryName = player.getInventory().getName().getString();

            process(items, inventoryName);

        } catch (Exception e) {
            wikiWriter.sendMessage("Something went wrong when trying to clone this GUI, please report this with your latest.log file!");
            e.printStackTrace();
        }
    }

    protected void process(List<ItemStack> items, String inventoryName) {
        int size = items.size();
        int rows = size / 9;

        Config config = wikiWriter.getConfig();
        StringBuilder builder = new StringBuilder();

        if (isShopMode(items, size, rows)) {
            processShop(builder, inventoryName, items, rows);
            return;
        }

        if (isRecipeMode(rows, inventoryName, items)) {
            processRecipe(builder, inventoryName, items);
            return;
        }

        ForgeRecipeType forgeRecipeType = getForgeRecipeType(inventoryName, items, rows);
        if (forgeRecipeType != null) {
            processForgeRecipe(builder, forgeRecipeType, items);
            return;
        }

        builder.append("{{Inventory\n")
                .append("|name=").append(inventoryName).append("\n")
                .append("|rows=").append(rows).append("\n");

        for (int i = 0; i < size; i++) {
            ItemStack itemStack = items.get(i);
            int horizontalPosition = 1 + (i % 9);
            int verticalPosition = (i / 9);

            WikiItem item = new WikiItem(itemStack, Action.COPYING_INVENTORY, false);
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

            LoreRemovalFeature.RemovedLore removeData = LoreRemovalFeature.checkAndFilter(lore, Action.COPYING_SHOP_INVENTORY);
            return removeData.hasShopLore() && areTopAndBottomRowsCorrect(items, size);
        }

        return false;
    }

    private boolean areTopAndBottomRowsCorrect(List<ItemStack> items, int size) {
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
        Config config = wikiWriter.getConfig();

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

            WikiItem item = new WikiItem(itemStack, Action.COPYING_INVENTORY, true);
            builder.append("|item").append(i + 1).append("=").append(item.generateText(Action.COPYING_INVENTORY)).append("\n");
        }

        builder.append("}}\n<noinclude>[[Category:NPC UI Templates]]</noinclude>");
        wikiWriter.sendMessage("Copied top GUI shop to clipboard.");
        wikiWriter.copyToClipboard(builder.toString());
    }

    private boolean isRecipeMode(int rows, String inventoryName, List<ItemStack> items) {
        return wikiWriter.getConfig().recipeMode
                && (inventoryName.equalsIgnoreCase("Craft Item") || inventoryName.endsWith("Recipe"))
                && rows == 6
                && !items.get(CRAFTING_TABLE_OR_RECIPE_REQUIRED_SLOT).isEmpty();
    }

    protected void processRecipe(StringBuilder builder, String inventoryName, List<ItemStack> items) {
        boolean craftItem = inventoryName.equalsIgnoreCase("Craft Item");
        ItemStack product = craftItem ? items.get(23) : items.get(25);
        if (craftItem && !product.isEmpty() && product.getItem() == Items.BARRIER) {
            wikiWriter.sendMessage("Cannot copy invalid recipe." + Formatting.GRAY + " (Barrier Detected)");
            return;
        }

        builder.append("{{Craft Item").append("\n");
        for (int i = 1; i <= 9; i++) {
            Integer integer = craftingTablePositionToSlotMap.get(i);
            ItemStack itemStack = items.get(integer);
            if (itemStack.isEmpty()) {
                continue;
            }

            WikiItem item = new WikiItem(itemStack, Action.COPYING_RECIPE_INVENTORY, true);
            builder.append("|in").append(i).append("=").append(item.generateText(Action.COPYING_RECIPE_INVENTORY)).append("\n");
        }

        if (!product.isEmpty()) {
            WikiItem item = new WikiItem(product, Action.COPYING_RECIPE_INVENTORY, true);
            builder.append("|out=").append(item.generateText(Action.COPYING_RECIPE_INVENTORY)).append("\n");
        }

        builder.append("}}\n<noinclude>[[Category:Recipe Templates]]</noinclude>");

        wikiWriter.sendMessage("Copied top GUI recipe to clipboard.");
        wikiWriter.copyToClipboard(builder.toString());
    }

    private ForgeRecipeType getForgeRecipeType(String inventoryName, List<ItemStack> items, int rows) {
        if (!wikiWriter.getConfig().forgeRecipeMode) {
            return null;
        }

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

        for (int i = 1; i <= 8; i++) {
            Integer integer = forgePositionToSlotMap.get(i);
            ItemStack itemStack = items.get(integer);
            if (itemStack.getItem() == Items.BLACK_STAINED_GLASS_PANE) {
                // menu glass item, skip!
                continue;
            }

            WikiItem item = new WikiItem(itemStack, Action.COPYING_RECIPE_INVENTORY, true);
            builder.append("|in").append(i).append("=").append(item.generateText(Action.COPYING_RECIPE_INVENTORY)).append("\n");
        }

        ItemStack product = items.get(16);
        if (!product.isEmpty()) {
            WikiItem item = new WikiItem(product, Action.COPYING_RECIPE_INVENTORY, true);
            builder.append("|out=").append(item.generateText(Action.COPYING_RECIPE_INVENTORY)).append("\n");
        }
        builder.append("}}\n<noinclude>[[Category:Recipe Templates]]</noinclude>");

        wikiWriter.sendMessage("Copied top GUI forge recipe to clipboard.");
        wikiWriter.copyToClipboard(builder.toString());
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
