package me.pigicial.wikiwriter.features;

import gg.essential.universal.UScreen;
import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.core.Config;
import me.pigicial.wikiwriter.utils.Action;
import me.pigicial.wikiwriter.utils.WikiItem;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class SingleSlotItemCopyFeature {

    private final KeyBinding keyBinding = new KeyBinding("Copy Item To Single GUI Slot", Keyboard.KEY_K, "Wiki Writer");
    private final WikiWriter wikiWriter;

    public SingleSlotItemCopyFeature(WikiWriter wikiWriter) {
        this.wikiWriter = wikiWriter;
        ClientRegistry.registerKeyBinding(keyBinding);

    }

    @SubscribeEvent
    public void onKey(GuiScreenEvent.KeyboardInputEvent.Post event) {
        try {
            Config config = wikiWriter.getConfig();
            if (!config.modEnabled || !config.copyItems) return;
            if (Keyboard.getEventKey() != keyBinding.getKeyCode()) return;
            if (!(event.gui instanceof GuiContainer)) return;
            if (Keyboard.getEventKeyState()) return; // only activate on key release

            GuiScreen currentScreen = UScreen.getCurrentScreen();
            if (currentScreen instanceof GuiEditSign || currentScreen instanceof GuiContainerCreative && ((GuiContainerCreative) currentScreen).getSelectedTabIndex() == 5) {
                return;
            }

            Slot slotUnderMouse = ((GuiContainer) event.gui).getSlotUnderMouse();
            if (slotUnderMouse == null) return; // if they press H while outside a slot, don't do anything
            ItemStack stack = slotUnderMouse.getStack();
            if (stack == null) return;

            WikiItem item = new WikiItem(null, stack, Action.COPYING_STANDALONE_ITEM_SINGLE_SLOT, false);
            String text = item.isHasSkyblockItemID() && (config.referenceModeForSingleSlotItems == 0 || config.referenceModeForSingleSlotItems == 1 && item.isShopItem())
                    ? item.isShopItem() ? item.convertToReferenceWithPotentialShopLore() : item.convertToReference() : item.convertToWikiItem();

            wikiWriter.copyToClipboard("{{inventory" + (config.watermarkCopiedSingleInventoryItems && !config.watermarkCopiedSingleInventoryItemsText.isEmpty() ? " <!-- " + config.watermarkCopiedSingleInventoryItemsText + " -->" : "") + "\n|rows=1\n|columns=1\n|A1=" + text + "\n}}");
            wikiWriter.sendMessage("Copied hovered item to clipboard as a single-slot menu item.");
        } catch (Exception e) {
            wikiWriter.sendMessage("Something went wrong when trying to copy this item, please report this with your latest.log file!");
            e.printStackTrace();
        }
    }
}
