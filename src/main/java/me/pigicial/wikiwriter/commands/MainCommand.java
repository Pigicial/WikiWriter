package me.pigicial.wikiwriter.commands;

import me.pigicial.wikiwriter.WikiWriter;
import gg.essential.api.EssentialAPI;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MainCommand extends Command {
    public MainCommand() {
        super("wikiwriter", false);
    }

    @Nullable
    @Override
    public Set<Alias> getCommandAliases() {
        return new HashSet<>(Collections.singletonList(new Alias("ww")));
    }

    @DefaultHandler
    public void handle() {
        try {
            EssentialAPI.getGuiUtil().openScreen(WikiWriter.getInstance().getConfig().gui());
        } catch (Exception e) {
            WikiWriter.getInstance().sendMessage("Failed to open this GUI, please report this with your latest.log file!");
            e.printStackTrace();
        }
    }
}