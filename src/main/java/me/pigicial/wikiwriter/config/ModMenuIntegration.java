package me.pigicial.wikiwriter.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.pigicial.wikiwriter.WikiWriter;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> WikiWriter.getInstance().getConfig().createGui(parent);
    }
}
