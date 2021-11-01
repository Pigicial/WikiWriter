package com.antonio32a.examplemod.commands;

import com.antonio32a.examplemod.ExampleMod;
import gg.essential.api.EssentialAPI;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;

public class ExampleCommand extends Command {
    public ExampleCommand() {
        super("example");
    }

    @DefaultHandler
    public void handle() {
        EssentialAPI.getGuiUtil().openScreen(ExampleMod.getInstance().getConfig().gui());
    }
}