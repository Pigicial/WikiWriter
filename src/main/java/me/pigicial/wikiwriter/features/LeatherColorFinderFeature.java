package me.pigicial.wikiwriter.features;

import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

public class LeatherColorFinderFeature {

    private static final LeatherArmorColor[] LEATHER_ARMOR_COLORS = LeatherArmorColor.values();

    public static LeatherArmorColor findColor(long comparingColorNumber) {
        if (comparingColorNumber == -1) {
            return LeatherArmorColor.DEFAULT;
        }

        Color comparingColor = new Color((int) comparingColorNumber, false);

        TreeMap<Double, LeatherArmorColor> distanceMap = new TreeMap<>();

        for (LeatherArmorColor armorColor : LEATHER_ARMOR_COLORS) {
            double rDist = Math.abs(armorColor.color.getRed()) - (double) comparingColor.getRed();
            double gDist = Math.abs(armorColor.color.getGreen()) - (double) comparingColor.getGreen();
            double bDist = Math.abs(armorColor.color.getBlue()) - (double) comparingColor.getBlue();

            distanceMap.put(Math.sqrt(rDist * rDist + gDist * gDist + bDist * bDist), armorColor);
        }

        Map.Entry<Double, LeatherArmorColor> entry = distanceMap.firstEntry();
        LeatherArmorColor value = entry.getValue();
        return value == null ? LeatherArmorColor.DEFAULT : value;
    }

    public enum LeatherArmorColor {
        GRAY(new Color(43, 47, 49)),
        YELLOW(new Color(225, 207, 50)),
        WHITE(new Color(255, 255, 255)),
        GREEN(new Color(74, 101, 16)),
        BLUE(new Color(58, 64, 152)),
        LIME(new Color(111, 174, 27)),
        RED(new Color(185, 41, 31)),
        MAGENTA(new Color(172, 66, 163)),
        CYAN(new Color(18, 130, 130)),
        ORANGE(new Color(206, 107, 24)),
        PURPLE(new Color(113, 41, 152)),
        BLACK(new Color(24, 24, 27)),
        PINK(new Color(202, 116, 142)),
        LIGHT_BLUE(new Color(76, 176, 206)),
        LIGHT_GRAY(new Color(131, 131, 126)),
        DEFAULT(new Color(113, 71, 45));

        private final Color color;

        LeatherArmorColor(Color color) {
            this.color = color;
        }
    }
}
