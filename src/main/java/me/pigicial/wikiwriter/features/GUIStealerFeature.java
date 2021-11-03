package me.pigicial.wikiwriter.features;

import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.core.Config;
import me.pigicial.wikiwriter.utils.WikiItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIStealerFeature {

    private final KeyBinding keybind = new KeyBinding("Copy Top GUI", Keyboard.KEY_I, "Wiki Writer");
    private final WikiWriter wikiWriter;

    private final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private final Map<Integer, Integer> positionToSlotMap = new HashMap<>();

    public GUIStealerFeature(WikiWriter wikiWriter) {
        this.wikiWriter = wikiWriter;
        ClientRegistry.registerKeyBinding(keybind);

        positionToSlotMap.put(1, 10);
        positionToSlotMap.put(2, 11);
        positionToSlotMap.put(3, 12);
        positionToSlotMap.put(4, 19);
        positionToSlotMap.put(5, 20);
        positionToSlotMap.put(6, 21);
        positionToSlotMap.put(7, 28);
        positionToSlotMap.put(8, 29);
        positionToSlotMap.put(9, 30);
    }

    @SubscribeEvent
    public void onKey(GuiScreenEvent.KeyboardInputEvent.Post event) {
        Config config = wikiWriter.getConfig();
        if (!config.modEnabled || !config.copyGUI) return;
        if (Keyboard.getEventKey() != keybind.getKeyCode()) return;
        if (Keyboard.getEventKeyState()) return; // only activate on key release

        Container openContainer = Minecraft.getMinecraft().thePlayer.openContainer;
        if (!(openContainer instanceof ContainerChest)) return;

        ContainerChest chest = (ContainerChest) openContainer;
        List<ItemStack> baseInventory = openContainer.getInventory();
        List<ItemStack> inventory = baseInventory.subList(0, baseInventory.size() - 9 * 4);

        String inventoryName = EnumChatFormatting.getTextWithoutFormattingCodes(chest.getLowerChestInventory().getDisplayName().getFormattedText());

        int rows = inventory.size() / 9;
        ItemStack craftingTableItem;
        boolean recipeMode = config.recipeMode && (inventoryName.equalsIgnoreCase("Craft Item") || inventoryName.endsWith("Recipe"))
                && rows == 6 && (craftingTableItem = inventory.get(23)) != null && craftingTableItem.getItem().getRegistryName().equals("minecraft:crafting_table");

        StringBuilder builder = new StringBuilder();
        NumberFormat numberFormat = NumberFormat.getInstance();

        if (recipeMode) {
            builder.append("{{Craft Item\n");
            for (int i = 1; i <= 9; i++) {
                Integer integer = positionToSlotMap.get(i);
                ItemStack itemStack = inventory.get(integer);
                if (itemStack == null) continue;
                WikiItem item = new WikiItem("Craft Item", itemStack);
                builder.append("|in").append(numberFormat.format(i)).append("=").append(item.convertToRecipeReference())
                        .append(item.getStackSize() > 1 ? "," + numberFormat.format(item.getStackSize()) : "").append("\n");
            }

            ItemStack product = inventory.get(25);
            if (product != null) {
                WikiItem item = new WikiItem("Craft Item", product);
                builder.append("|out=").append(item.convertToRecipeReference())
                        .append(item.getStackSize() > 1 ? "," + numberFormat.format(item.getStackSize()) : "").append("\n");
            }
            builder.append("}}\n<noinclude>[[Category:Recipe Templates]]</noinclude>");

            wikiWriter.sendMessage("Copied top GUI recipe to clipboard.");
        } else {
            if (config.copiedInventoriesAreTemplates) {
                builder.append("<noinclude>[[Category:Inventory_Templates]]</noinclude>\n");
            }

            builder.append("{{inventory\n"
                            + "|name=").append(inventoryName).append("\n")
                    .append("|rows=").append(numberFormat.format(rows))
                    .append("\n");

            for (int i = 0, inventorySize = inventory.size(); i < inventorySize; i++) {
                ItemStack itemStack = inventory.get(i);
                int horizontalPosition = 1 + (i % 9);
                int verticalPosition = 1 + (i / 9);

                builder.append("|").append(alphabet[verticalPosition - 1]).append(numberFormat.format(horizontalPosition)).append("=")
                        .append(new WikiItem(inventoryName, itemStack).convertToWikiItem()).append("\n");
            }
            builder.append("}}");
            wikiWriter.sendMessage("Copied top GUI to clipboard.");
        }

        wikiWriter.copyToClipboard(builder.toString());
    }
}
