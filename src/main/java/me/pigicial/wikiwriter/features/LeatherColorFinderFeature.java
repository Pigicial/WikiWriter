package me.pigicial.wikiwriter.features;

import me.pigicial.wikiwriter.WikiWriter;

import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

public enum LeatherColorFinderFeature {
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

    LeatherColorFinderFeature(Color color) {
        this.color = color;
    }

    public static LeatherColorFinderFeature findColor(int comparingColorNumber) {

        if (comparingColorNumber == -1) {
            return DEFAULT;
        }

        Color comparingColor = new Color(comparingColorNumber, false);
       // WikiWriter.getInstance().sendMessage("Comparing color: " + comparingColor + ", r: " + comparingColor.getRed() + ", g: " + comparingColor.getGreen() + ", b: " + comparingColor.getBlue());

        TreeMap<Double, LeatherColorFinderFeature> distanceMap = new TreeMap<>();

        for (LeatherColorFinderFeature colorFinderFeature : values()) {
            long rDist = Math.abs(colorFinderFeature.color.getRed()) - comparingColor.getRed();
            long gDist = Math.abs(colorFinderFeature.color.getGreen()) - comparingColor.getGreen();
            long bDist = Math.abs(colorFinderFeature.color.getBlue()) - comparingColor.getBlue();

            distanceMap.put(Math.sqrt(rDist * rDist + gDist * gDist + bDist * bDist), colorFinderFeature);
        }

        Map.Entry<Double, LeatherColorFinderFeature> entry = distanceMap.firstEntry();
        LeatherColorFinderFeature value = entry.getValue();
        if (value == null) value = DEFAULT;
        return value;
    }

}
