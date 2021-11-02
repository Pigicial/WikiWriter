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
import java.util.List;

public class GUIStealerFeature {

    private final KeyBinding keybind = new KeyBinding("Copy Top GUI", Keyboard.KEY_I, "Wiki Writer");
    private final WikiWriter wikiWriter;

    private final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public GUIStealerFeature(WikiWriter wikiWriter) {
        this.wikiWriter = wikiWriter;
        ClientRegistry.registerKeyBinding(keybind);
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

        int rows = inventory.size() / 9;

        StringBuilder builder = new StringBuilder();
        if (config.copiedInventoriesAreTemplates) {
            builder.append("<noinclude>[[Category:Inventory_Templates]]</noinclude>\n");
        }

        NumberFormat numberFormat = NumberFormat.getInstance();
        builder.append("{{inventory\n"
                        + "|name=").append(EnumChatFormatting.getTextWithoutFormattingCodes(chest.getLowerChestInventory().getDisplayName().getFormattedText())).append("\n")
                .append("|rows=").append(numberFormat.format(rows))
                .append("\n");

        for (int i = 0, inventorySize = inventory.size(); i < inventorySize; i++) {
            ItemStack itemStack = inventory.get(i);
            int horizontalPosition = 1 + (i % 9);
            int verticalPosition = 1 + (i / 9);
            builder.append("|").append(alphabet[verticalPosition - 1]).append(numberFormat.format(horizontalPosition)).append("=").append(WikiItem.fromItemStack(itemStack).convertToWikiItem()).append("\n");
        }

        builder.append("}}");
        wikiWriter.copyToClipboard(builder.toString());
        wikiWriter.sendMessage("Copied top GUI to clipboard.");
    }
}
