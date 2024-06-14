package me.pigicial.wikiwriter.features.replacements;

import me.pigicial.wikiwriter.features.items.TextReplacementPipeline;
import me.pigicial.wikiwriter.features.items.WikiItem;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface MenuModification {

    BiConsumer<WikiItem, Integer> getItemConsumer();

    Consumer<TextReplacementPipeline> getTextPipelineConsumer();
}
