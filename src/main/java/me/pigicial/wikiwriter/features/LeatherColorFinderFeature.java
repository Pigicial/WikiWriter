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
        GRAY(43, 47, 49),
        YELLOW(225, 207, 50),
        WHITE(255, 255, 255),
        GREEN(74, 101, 16),
        BLUE(58, 64, 152),
        LIME(111, 174, 27),
        RED(185, 41, 31),
        MAGENTA(172, 66, 163),
        CYAN(18, 130, 130),
        ORANGE(206, 107, 24),
        PURPLE(113, 41, 152),
        BLACK(24, 24, 27),
        PINK(202, 116, 142),
        LIGHT_BLUE(76, 176, 206),
        LIGHT_GRAY(131, 131, 126),
        DEFAULT(113, 71, 45);

        private final Color color;

        LeatherArmorColor(int red, int green, int blue) {
            this.color = new Color(red, green, blue);
        }
    }
}
