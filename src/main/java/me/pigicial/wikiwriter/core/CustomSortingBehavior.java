package me.pigicial.wikiwriter.core;

import gg.essential.vigilance.data.Category;
import gg.essential.vigilance.data.PropertyData;
import gg.essential.vigilance.data.SortingBehavior;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CustomSortingBehavior extends SortingBehavior {

    @NotNull
    @Override
    public Comparator<? super Category> getCategoryComparator() {
        return Comparator.comparing(a -> 0);
    }

    @NotNull
    @Override
    public Comparator<? super PropertyData> getPropertyComparator() {
        return Comparator.comparing(a -> 0);
    }

    @NotNull
    @Override
    public Comparator<? super Map.Entry<String, ? extends List<PropertyData>>> getSubcategoryComparator() {
        return Comparator.comparing(a -> 0);
    }
}
