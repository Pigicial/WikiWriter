package me.pigicial.wikiwriter.features;

import gg.essential.universal.UScreen;
import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.core.Config;
import me.pigicial.wikiwriter.utils.Action;
import me.pigicial.wikiwriter.utils.WikiItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIStealerFeature {

    private final KeyBinding keybind = new KeyBinding("Copy Top GUI", Keyboard.KEY_I, "Wiki Writer");
    private final WikiWriter wikiWriter;

    private final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private final Map<Integer, Integer> craftingTablePositionToSlotMap = new HashMap<>();
    private final Map<Integer, Integer> forgePositionToSlotMap = new HashMap<>();

    public GUIStealerFeature(WikiWriter wikiWriter) {
        this.wikiWriter = wikiWriter;
        ClientRegistry.registerKeyBinding(keybind);

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

    @SubscribeEvent
    public void onKey(GuiScreenEvent.KeyboardInputEvent.Post event) {
        try {
            Config config = wikiWriter.getConfig();
            if (!config.modEnabled || !config.copyGUI) return;
            if (Keyboard.getEventKey() != keybind.getKeyCode()) return;
            if (Keyboard.getEventKeyState()) return; // only activate on key release

            GuiScreen currentScreen = UScreen.getCurrentScreen();
            if (currentScreen instanceof GuiEditSign || currentScreen instanceof GuiContainerCreative && ((GuiContainerCreative) currentScreen).getSelectedTabIndex() == 5) {
                return;
            }

            EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
            if (thePlayer == null) return;
            Container openContainer = thePlayer.openContainer;
            if (!(openContainer instanceof ContainerChest)) return;

            ContainerChest chest = (ContainerChest) openContainer;
            List<ItemStack> baseInventory = openContainer.getInventory();
            List<ItemStack> inventory = baseInventory.subList(0, baseInventory.size() - 9 * 4);

            String inventoryName = EnumChatFormatting.getTextWithoutFormattingCodes(chest.getLowerChestInventory().getDisplayName().getFormattedText());

            int size = inventory.size();
            int rows = size / 9;
            String registryName;
            boolean recipeMode = config.recipeMode && (inventoryName.equalsIgnoreCase("Craft Item") || inventoryName.endsWith("Recipe"))
                    && rows == 6 && inventory.get(23) != null;

            ItemStack forgeItem = null;
            boolean forgeRecipeMode = config.forgeRecipeMode && inventoryName.equalsIgnoreCase("Confirm Process") && rows == 6
                    && (forgeItem = inventory.get(13)) != null && ((registryName = forgeItem.getItem().getRegistryName()).equalsIgnoreCase("minecraft:lava_bucket") || registryName.equalsIgnoreCase("minecraft:furnace"));

            StringBuilder builder = new StringBuilder();
            NumberFormat numberFormat = NumberFormat.getInstance();

            ItemStack firstPotentialShopItem = null;
            boolean shopMode = rows >= 3 && config.shopMenuMode && (firstPotentialShopItem = inventory.get(10)) != null;

            if (shopMode) {
                NBTTagList loreTag = firstPotentialShopItem.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
                List<String> lore = new ArrayList<>();
                for (int i = 0; i < loreTag.tagCount(); i++)
                    lore.add(loreTag.getStringTagAt(i));

                LoreRemovalFeature.RemoveData removeData = LoreRemovalFeature.checkAndFilter(Action.COPYING_INVENTORY, lore, LoreRemovalFeature.BOTTOM_SHOP_FILTERS, false);
                shopMode = !removeData.removedLore.isEmpty();
            }

            if (shopMode) {
                for (int i = 0; i <= 8; i++) {
                    ItemStack itemStack = inventory.get(i);
                    if (itemStack != null && !(itemStack.getItem().getRegistryName().equals("minecraft:stained_glass_pane") && itemStack.getMetadata() == 15)) {
                        shopMode = false;
                        break;
                    }
                }
            }

            if (shopMode) {
                int positionInThisArray = 0;
                for (int i = size - 9; i <= size - 1; i++) {
                    positionInThisArray++;
                    ItemStack itemStack = inventory.get(i);
                    if (itemStack != null && (positionInThisArray == 5 ? !itemStack.getItem().getRegistryName().equals("minecraft:hopper") : (!(itemStack.getItem().getRegistryName().equals("minecraft:stained_glass_pane") && itemStack.getMetadata() == 15)))) {
                        shopMode = false;
                        break;
                    }
                }
            }

            if (shopMode) {
                builder.append("{{Merchant\n|name=").append(inventoryName).append("\n");
                for (int i = 0, max = (rows - 2) * 7; i < max; i++) {
                    int row = 2 + (i / 7);
                    int across = 2 + (i % 7);
                    int slot = row * 9 - 9 + across - 1;
                    ItemStack itemStack = inventory.get(slot);
                    if (itemStack == null) break;

                    WikiItem item = new WikiItem(inventoryName, itemStack, Action.COPYING_INVENTORY, true);
                    builder.append("|item").append(i + 1).append("=").append((config.modifiedShopItemFormat == 0 || config.modifiedShopItemFormat == 1 && item.isShopItem())
                            ? item.isShopItem() ? item.convertToReferenceWithPotentialShopLore() : item.convertToReference() : item.convertToWikiItem()).append("\n");
                }

                builder.append("}}\n<noinclude>[[Category:NPC UI Templates]]</noinclude>");
                wikiWriter.sendMessage("Copied top GUI shop to clipboard.");
            } else if (recipeMode) {
                boolean craftItem = inventoryName.equalsIgnoreCase("Craft Item");
                ItemStack product = craftItem ? inventory.get(23) : inventory.get(25);
                if (craftItem && product != null && product.getItem().getRegistryName().equals("minecraft:barrier")) {
                    wikiWriter.sendMessage("Cannot copy invalid recipe." + EnumChatFormatting.GRAY + " (Barrier Detected)");
                    return;
                }
                builder.append("{{Craft Item\n");
                for (int i = 1; i <= 9; i++) {
                    Integer integer = craftingTablePositionToSlotMap.get(i);
                    ItemStack itemStack = inventory.get(integer);
                    if (itemStack == null) continue;
                    WikiItem item = new WikiItem("Craft Item", itemStack, Action.COPYING_INVENTORY, true);
                    builder.append("|in").append(numberFormat.format(i)).append("=").append(item.convertToReference())
                            .append(item.getStackSize() > 1 ? "," + numberFormat.format(item.getStackSize()) : "").append("\n");
                }

                if (product != null) {
                    WikiItem item = new WikiItem("Craft Item", product, Action.COPYING_INVENTORY, true);
                    builder.append("|out=").append(item.convertToReference())
                            .append(item.getStackSize() > 1 ? "," + numberFormat.format(item.getStackSize()) : "").append("\n");
                }
                builder.append("}}\n<noinclude>[[Category:Recipe Templates]]</noinclude>");

                wikiWriter.sendMessage("Copied top GUI recipe to clipboard.");
            } else if (forgeRecipeMode) {
                builder.append("{{Forge_Item\n");
                builder.append("|type=").append(forgeItem.getDisplayName().toLowerCase().contains("cast") ? "cast" : "refine").append("\n");
                for (int i = 1; i <= 8; i++) {
                    Integer integer = forgePositionToSlotMap.get(i);
                    ItemStack itemStack = inventory.get(integer);
                    if (itemStack == null) continue;
                    if (itemStack.getItem().getRegistryName().equals("minecraft:stained_glass_pane") && itemStack.getMetadata() == 15) continue;


                    WikiItem item = new WikiItem("Confirm Process", itemStack, Action.COPYING_INVENTORY, true);
                    builder.append("|in").append(numberFormat.format(i)).append("=").append(item.convertToReference())
                            .append(item.getStackSize() > 1 ? "," + numberFormat.format(item.getStackSize()) : "").append("\n");
                }

                ItemStack product = inventory.get(16);
                if (product != null) {
                    WikiItem item = new WikiItem("Confirm Process", product, Action.COPYING_INVENTORY, true);
                    builder.append("|out=").append(item.convertToReference())
                            .append(item.getStackSize() > 1 ? "," + numberFormat.format(item.getStackSize()) : "").append("\n");
                }
                builder.append("}}\n<noinclude>[[Category:Recipe Templates]]</noinclude>");

                wikiWriter.sendMessage("Copied top GUI forge recipe to clipboard.");
            } else {
                if (config.copiedInventoriesAreTemplates) {
                    builder.append("<noinclude>[[Category:Inventory_Templates]]</noinclude>\n");
                }

                builder.append("{{inventory\n"
                                + "|name=").append(inventoryName).append("\n")
                        .append("|rows=").append(numberFormat.format(rows))
                        .append("\n");

                for (int i = 0; i < size; i++) {
                    ItemStack itemStack = inventory.get(i);
                    int horizontalPosition = 1 + (i % 9);
                    int verticalPosition = 1 + (i / 9);

                    WikiItem item = new WikiItem(inventoryName, itemStack, Action.COPYING_INVENTORY, false);
                    builder.append("|").append(alphabet[verticalPosition - 1]).append(numberFormat.format(horizontalPosition)).append("=")
                            .append(item.isHasSkyblockItemID() && (config.modifiedShopItemFormat == 0 || config.modifiedShopItemFormat == 1 && item.isShopItem())
                                    ? item.isShopItem() ? item.convertToReferenceWithPotentialShopLore() : item.convertToReference() : item.convertToWikiItem()).append("\n");
                }
                builder.append("}}");
                wikiWriter.sendMessage("Copied top GUI to clipboard.");
            }

            wikiWriter.copyToClipboard(builder.toString());
        } catch (Exception e) {
            wikiWriter.sendMessage("Something went wrong when trying to clone this GUI, please report this with your latest.log file!");
            e.printStackTrace();
        }
    }
}
