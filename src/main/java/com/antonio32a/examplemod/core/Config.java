package com.antonio32a.examplemod.core;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;
import com.antonio32a.examplemod.ExampleMod;

import java.io.File;

public class Config extends Vigilant {
    @Property(
        type = PropertyType.SWITCH,
        name = "Gaming",
        description = "Are you gaming?",
        category = "Main"
    )
    public boolean gaming = true;

    public Config() {
        super(new File(ExampleMod.configLocation));
        initialize();
    }
}