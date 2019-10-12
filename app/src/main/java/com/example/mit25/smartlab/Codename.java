package com.example.mit25.smartlab;

/**
 * Created by mit25 on 5/6/18.
 */

import java.util.Random;

/** Utility class to generate random Android names */
public final class Codename {
    private static final String[] COLORS =
            new String[] {
                    "Red",
                    "Orange",
                    "Yellow",
                    "Green",
                    "Blue",
                    "Indigo",
                    "Violet",
                    "Purple",
                    "Lavender",
                    "Fuchsia",
                    "Plum",
                    "Orchid",
                    "Magenta",
            };

    private static final String[] TREATS =
            new String[] {
                    "Alpha",
                    "Beta",
                    "Cupcake",
                    "Donut",
                    "Eclair",
                    "Froyo",
                    "Gingerbread",
                    "Honeycomb",
                    "Ice Cream Sandwich",
                    "Jellybean",
                    "Kit Kat",
                    "Lollipop",
                    "Marshmallow",
                    "Nougat",
            };

    private static final Random generator = new Random();

    private Codename() {}

    /** Generate a random Android agent codename */
    public static String generate() {
        String color = COLORS[generator.nextInt(COLORS.length)];
        String treat = TREATS[generator.nextInt(TREATS.length)];
        return color + " " + treat;
    }
}

