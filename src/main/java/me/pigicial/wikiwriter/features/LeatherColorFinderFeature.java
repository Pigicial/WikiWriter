package me.pigicial.wikiwriter.features;

import me.pigicial.wikiwriter.WikiWriter;

import java.awt.*;

public enum LeatherColorFinderFeature {
    GRAY(new Color(43, 47, 49)),
    YELLOW(new Color(148, 129, 37)),
    WHITE(new Color(182, 182, 82)),
    GREEN(new Color(67, 90, 16)),
    LIME(new Color(106, 165, 26)),
    RED(new Color(148, 38, 31)),
    MAGENTA(new Color(172, 66, 163)),
    CYAN(new Color(18, 130, 130)),
    ORANGE(new Color(209, 108, 24)),
    PURPLE(new Color(113, 41, 152)),
    BLACK(new Color(24, 24, 27)),
    PINK(new Color(209, 120, 147)),
    LIGHT_BLUE(new Color(48, 148, 180)),
    LIGHT_GRAY(new Color(131, 131, 126)),
    DEFAULT(new Color(6, 6, 7));

    private final Color color;

    LeatherColorFinderFeature(Color color) {
        this.color = color;
    }

    public static LeatherColorFinderFeature findColor(int comparingColorNumber) {

        if (comparingColorNumber == 0) {
            return DEFAULT;
        }

        Color comparingColor = new Color(comparingColorNumber, false);
        WikiWriter.getInstance().sendMessage("Comparing color: " + comparingColor + ", r: " + comparingColor.getRed() + ", g: " + comparingColor.getGreen() + ", b: " + comparingColor.getBlue());

        for (LeatherColorFinderFeature colorFinderFeature : values()) {
            long rDist = Math.abs(colorFinderFeature.color.getRed()) - comparingColor.getRed();
            long gDist = Math.abs(colorFinderFeature.color.getGreen()) - comparingColor.getGreen();
            long bDist = Math.abs(colorFinderFeature.color.getBlue()) - comparingColor.getBlue();

            if (rDist + gDist + bDist < 150) {
                return colorFinderFeature;
            }
        }

        return DEFAULT;
    }

}
