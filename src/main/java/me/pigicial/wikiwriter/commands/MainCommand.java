package me.pigicial.wikiwriter.commands;

import me.pigicial.wikiwriter.WikiWriter;
import gg.essential.api.EssentialAPI;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;

public class MainCommand extends Command {
    public MainCommand() {
        super("wikiwriter");
    }

    @DefaultHandler
    public void handle() {
        EssentialAPI.getGuiUtil().openScreen(WikiWriter.getInstance().getConfig().gui());
    }
}