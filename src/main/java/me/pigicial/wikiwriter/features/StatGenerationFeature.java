package me.pigicial.wikiwriter.features;

import gg.essential.universal.UScreen;
import me.pigicial.wikiwriter.WikiWriter;
import me.pigicial.wikiwriter.core.Config;
import me.pigicial.wikiwriter.utils.StatType;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class StatGenerationFeature {

    private final KeyBinding keybind = new KeyBinding("Generate Item Stats", Keyboard.KEY_L, "Wiki Writer");
    private final WikiWriter wikiWriter;

    public StatGenerationFeature(WikiWriter wikiWriter) {
        this.wikiWriter = wikiWriter;
        ClientRegistry.registerKeyBinding(keybind);
    }

    @SubscribeEvent
    public void onKey(GuiScreenEvent.KeyboardInputEvent.Post event) {
        try {
            Config config = wikiWriter.getConfig();
            if (!config.modEnabled || !config.copyingItemStatsEnabled) return;
            if (Keyboard.getEventKey() != keybind.getKeyCode()) return;
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

            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt == null) nbt = new NBTTagCompound();

            NBTTagCompound display = nbt.getCompoundTag("display");
            NBTTagList loreTag = display.getTagList("Lore", 8);

            List<String> lore = new ArrayList<>();
            for (int i = 0; i < loreTag.tagCount(); i++)
                lore.add(loreTag.getStringTagAt(i));

            String s = StatType.generateStats(lore);
            if (s.equals("")) {
                wikiWriter.sendMessage("No stats detected.");
                return;
            }
            wikiWriter.copyToClipboard(s);
            wikiWriter.sendMessage("Copied hovered item stats to clipboard.");
        } catch (Exception e) {
            wikiWriter.sendMessage("Something went wrong when trying to copy this item's stats, please report this with your latest.log file!");
            e.printStackTrace();
        }
    }


}
