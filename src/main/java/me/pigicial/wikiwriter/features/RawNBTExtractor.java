package me.pigicial.wikiwriter.features;

public class RawNBTExtractor {

    /*
    private final KeyBinding keybind = new KeyBinding("Copy Raw NBT", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_J, "WikiWriter");
    private final WikiWriter wikiWriter;

    public RawNBTExtractor(WikiWriter wikiWriter) {
        this.wikiWriter = wikiWriter;
        ClientRegistry.registerKeyBinding(keybind);
    }

    @SubscribeEvent
    public void onKey(GuiScreenEvent.KeyboardInputEvent.Post event) {
        try {
            Config config = wikiWriter.getConfig();
            if (!config.modEnabled || !config.rawNbtExtractionEnabled) return;
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

            NBTTagCompound tag = new NBTTagCompound();
            stack.writeToNBT(tag);

            wikiWriter.copyToClipboard(createNbt(tag));
            wikiWriter.sendMessage("Copied hovered item raw NBT to clipboard.");
        } catch (Exception e) {
            wikiWriter.sendMessage("Something went wrong when trying to copy this item's NBT, please report this with your latest.log file!");
            e.printStackTrace();
        }
    }

    private String createNbt(NBTBase nbt) {
        // thank u complexorigin for this code
        final String INDENT = "    ";

        int tagID = nbt.getId();
        StringBuilder stringBuilder = new StringBuilder();

        // Determine which type of tag it is.
        if (tagID == Constants.NBT.TAG_END) {
            stringBuilder.append('}');

        } else if (tagID == Constants.NBT.TAG_BYTE_ARRAY || tagID == Constants.NBT.TAG_INT_ARRAY) {
            stringBuilder.append('[');
            if (tagID == Constants.NBT.TAG_BYTE_ARRAY) {
                NBTTagByteArray nbtByteArray = (NBTTagByteArray) nbt;
                byte[] bytes = nbtByteArray.getByteArray();

                for (int i = 0; i < bytes.length; i++) {
                    stringBuilder.append(bytes[i]);

                    // Don't add a comma after the last element.
                    if (i < (bytes.length - 1)) {
                        stringBuilder.append(", ");
                    }
                }
            } else {
                NBTTagIntArray nbtIntArray = (NBTTagIntArray) nbt;
                int[] ints = nbtIntArray.getIntArray();

                for (int i = 0; i < ints.length; i++) {
                    stringBuilder.append(ints[i]);

                    // Don't add a comma after the last element.
                    if (i < (ints.length - 1)) {
                        stringBuilder.append(", ");
                    }
                }
            }
            stringBuilder.append(']');

        } else if (tagID == Constants.NBT.TAG_LIST) {
            NBTTagList nbtTagList = (NBTTagList) nbt;

            stringBuilder.append('[');
            for (int i = 0; i < nbtTagList.tagCount(); i++) {
                NBTBase currentListElement = nbtTagList.get(i);

                stringBuilder.append(createNbt(currentListElement));

                // Don't add a comma after the last element.
                if (i < (nbtTagList.tagCount() - 1)) {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append(']');

        } else if (tagID == Constants.NBT.TAG_COMPOUND) {
            NBTTagCompound nbtTagCompound = (NBTTagCompound) nbt;

            stringBuilder.append('{');
            if (!nbtTagCompound.hasNoTags()) {
                Iterator<String> iterator = nbtTagCompound.getKeySet().iterator();

                stringBuilder.append(System.lineSeparator());

                while (iterator.hasNext()) {
                    String key = iterator.next();
                    NBTBase currentCompoundTagElement = nbtTagCompound.getTag(key);

                    stringBuilder.append(key).append(": ").append(
                            createNbt(currentCompoundTagElement));

                    if (key.contains("backpack_data") && currentCompoundTagElement instanceof NBTTagByteArray) {
                        try {
                            NBTTagCompound backpackData = CompressedStreamTools.readCompressed(new ByteArrayInputStream(((NBTTagByteArray)currentCompoundTagElement).getByteArray()));

                            stringBuilder.append(",").append(System.lineSeparator());
                            stringBuilder.append(key).append("(decoded): ").append(
                                    createNbt(backpackData));
                        } catch (IOException ignored) {

                        }
                    }

                    // Don't add a comma after the last element.
                    if (iterator.hasNext()) {
                        stringBuilder.append(",").append(System.lineSeparator());
                    }
                }

                // Indent all lines
                String indentedString = stringBuilder.toString().replaceAll(System.lineSeparator(), System.lineSeparator() + INDENT);
                stringBuilder = new StringBuilder(indentedString);
            }

            stringBuilder.append(System.lineSeparator()).append('}');
        }
        // This includes the tags: byte, short, int, long, float, double, and string
        else {
            stringBuilder.append(nbt.toString());
        }

        return stringBuilder.toString();
    }
     */

}
