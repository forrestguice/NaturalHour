// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2026 Forrest Guice
    This file is part of Natural Hour.

    Natural Hour is free software: you can limeistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Natural Hour is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Natural Hour.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.forrestguice.suntimes.naturalhour.ui.colors;

import com.forrestguice.suntimes.naturalhour.R;

/**
 * MaterialPalette
 */
public class MaterialPalette
{
    protected int[] n1 = new int[Shade.values().length];
    protected int[] n2 = new int[Shade.values().length];
    protected int[] a1 = new int[Shade.values().length];
    protected int[] a2 = new int[Shade.values().length];
    protected int[] a3 = new int[Shade.values().length];

    public MaterialPalette() {
        init();
    }
    public MaterialPalette(Color neutral1, Color neutral2, Color accent1, Color accent2, Color accent3)
    {
        n1 = shadesOf(neutral1);
        n2 = shadesOf(neutral2);
        a1 = shadesOf(accent1);
        a2 = shadesOf(accent2);
        a3 = shadesOf(accent3);
    }

    protected void init() {}

    public int n1(Shade shade) {
        return shadeOf(n1, shade);
    }
    public int n2(Shade shade) {
        return shadeOf(n2, shade);
    }
    public int a1(Shade shade) {
        return shadeOf(a1, shade);
    }
    public int a2(Shade shade) {
        return shadeOf(a2, shade);
    }
    public int a3(Shade shade) {
        return shadeOf(a3, shade);
    }

    /**
     * Shade
     */
    public enum Shade {
        s50, s100, s200, s300, s400, s500, s600, s700, s800, s900, s999,
        a100, a200, a400, a700,
        s0, s1000
    }

    protected int shadeOf(int[] colors, Shade shade)
    {
        switch (shade) {
            case s0: return R.color.white;
            case s1000: return R.color.black;
            default: return colors[shade.ordinal()];
        }
    }

    /**
     * Color
     */
    public enum Color {
        grey, brown, blue_grey, pink, red, deep_orange, orange, amber, yellow, lime, light_green, green, teal, cyan, light_blue, blue, indigo, deep_purple, purple
    }
    public int[] shadesOf(Color color)
    {
        switch (color)
        {
            case brown: return brown;
            case blue_grey: return blue_grey;
            case pink: return pink;
            case red: return red;
            case deep_orange: return deep_orange;
            case orange: return orange;
            case amber: return amber;
            case yellow: return yellow;
            case lime: return lime;
            case light_green: return light_green;
            case green: return green;
            case teal: return teal;
            case cyan: return cyan;
            case light_blue: return light_blue;
            case blue: return blue;
            case indigo: return indigo;
            case purple: return purple;
            case deep_purple: return deep_purple;
            case grey: default: return grey;
        }
    }

    public static int[] grey = new int[] { R.color.grey_50, R.color.grey_100, R.color.grey_200, R.color.grey_300, R.color.grey_400, R.color.grey_500, R.color.grey_600, R.color.grey_700, R.color.grey_800, R.color.grey_900, R.color.grey_999,
            R.color.grey_100, R.color.grey_200, R.color.grey_300, R.color.grey_400 };

    public static int[] brown = new int[] { R.color.brown_50, R.color.brown_100, R.color.brown_200, R.color.brown_300, R.color.brown_400, R.color.brown_500, R.color.brown_600, R.color.brown_700, R.color.brown_800, R.color.brown_900, R.color.brown_999,
            R.color.brown_100, R.color.brown_200, R.color.brown_300, R.color.brown_400 };

    public static int[] blue_grey = new int[] { R.color.blue_grey_50, R.color.blue_grey_100, R.color.blue_grey_200, R.color.blue_grey_300, R.color.blue_grey_400, R.color.blue_grey_500, R.color.blue_grey_600, R.color.blue_grey_700, R.color.blue_grey_800, R.color.blue_grey_900, R.color.blue_grey_999,
            R.color.blue_grey_100, R.color.blue_grey_200, R.color.blue_grey_400, R.color.blue_grey_700 };

    public static int[] pink = new int[] { R.color.pink_50, R.color.pink_100, R.color.pink_200, R.color.pink_300, R.color.pink_400, R.color.pink_500, R.color.pink_600, R.color.pink_700, R.color.pink_800, R.color.pink_900, R.color.pink_900,
            R.color.pink_a100, R.color.pink_a200, R.color.pink_a400, R.color.pink_a700 };

    public static int[] red = new int[] { R.color.red_50, R.color.red_100, R.color.red_200, R.color.red_300, R.color.red_400, R.color.red_500, R.color.red_600, R.color.red_700, R.color.red_800, R.color.red_900, R.color.red_900,
            R.color.red_a100, R.color.red_a200, R.color.red_a400, R.color.red_a700 };

    public static int[] deep_orange = new int[] { R.color.deep_orange_50, R.color.deep_orange_100, R.color.deep_orange_200, R.color.deep_orange_300, R.color.deep_orange_400, R.color.deep_orange_500, R.color.deep_orange_600, R.color.deep_orange_700, R.color.deep_orange_800, R.color.deep_orange_900, R.color.deep_orange_900,
            R.color.deep_orange_a100, R.color.deep_orange_a200, R.color.deep_orange_a400, R.color.deep_orange_a700 };

    public static int[] orange = new int[] { R.color.orange_50, R.color.orange_100, R.color.orange_200, R.color.orange_300, R.color.orange_400, R.color.orange_500, R.color.orange_600, R.color.orange_700, R.color.orange_800, R.color.orange_900, R.color.orange_900,
            R.color.orange_a100, R.color.orange_a200, R.color.orange_a400, R.color.orange_a700 };

    public static int[] amber = new int[] { R.color.amber_50, R.color.amber_100, R.color.amber_200, R.color.amber_300, R.color.amber_400, R.color.amber_500, R.color.amber_600, R.color.amber_700, R.color.amber_800, R.color.amber_900, R.color.amber_900,
            R.color.amber_a100, R.color.amber_a200, R.color.amber_a400, R.color.amber_a700 };

    public static int[] yellow = new int[] { R.color.yellow_50, R.color.yellow_100, R.color.yellow_200, R.color.yellow_300, R.color.yellow_400, R.color.yellow_500, R.color.yellow_600, R.color.yellow_700, R.color.yellow_800, R.color.yellow_900, R.color.yellow_900,
            R.color.yellow_a100, R.color.yellow_a200, R.color.yellow_a400, R.color.yellow_a700 };

    public static int[] lime = new int[] { R.color.lime_50, R.color.lime_100, R.color.lime_200, R.color.lime_300, R.color.lime_400, R.color.lime_500, R.color.lime_600, R.color.lime_700, R.color.lime_800, R.color.lime_900, R.color.lime_900,
            R.color.lime_a100, R.color.lime_a200, R.color.lime_a400, R.color.lime_a700 };

    public static int[] light_green = new int[] { R.color.light_green_50, R.color.light_green_100, R.color.light_green_200, R.color.light_green_300, R.color.light_green_400, R.color.light_green_500, R.color.light_green_600, R.color.light_green_700, R.color.light_green_800, R.color.light_green_900, R.color.light_green_900,
            R.color.light_green_a100, R.color.light_green_a200, R.color.light_green_a400, R.color.light_green_a700 };

    public static int[] green = new int[] { R.color.green_50, R.color.green_100, R.color.green_200, R.color.green_300, R.color.green_400, R.color.green_500, R.color.green_600, R.color.green_700, R.color.green_800, R.color.green_900, R.color.green_900,
            R.color.green_a100, R.color.green_a200, R.color.green_a400, R.color.green_a700 };

    public static int[] teal = new int[] { R.color.teal_50, R.color.teal_100, R.color.teal_200, R.color.teal_300, R.color.teal_400, R.color.teal_500, R.color.teal_600, R.color.teal_700, R.color.teal_800, R.color.teal_900, R.color.teal_900,
            R.color.teal_a100, R.color.teal_a200, R.color.teal_a400, R.color.teal_a700 };

    public static int[] cyan = new int[] { R.color.cyan_50, R.color.cyan_100, R.color.cyan_200, R.color.cyan_300, R.color.cyan_400, R.color.cyan_500, R.color.cyan_600, R.color.cyan_700, R.color.cyan_800, R.color.cyan_900, R.color.cyan_900,
            R.color.cyan_a100, R.color.cyan_a200, R.color.cyan_a400, R.color.cyan_a700 };

    public static int[] light_blue = new int[] { R.color.light_blue_50, R.color.light_blue_100, R.color.light_blue_200, R.color.light_blue_300, R.color.light_blue_400, R.color.light_blue_500, R.color.light_blue_600, R.color.light_blue_700, R.color.light_blue_800, R.color.light_blue_900, R.color.light_blue_900,
            R.color.light_blue_a100, R.color.light_blue_a200, R.color.light_blue_a400, R.color.light_blue_a700 };

    public static int[] blue = new int[] { R.color.blue_50, R.color.blue_100, R.color.blue_200, R.color.blue_300, R.color.blue_400, R.color.blue_500, R.color.blue_600, R.color.blue_700, R.color.blue_800, R.color.blue_900, R.color.blue_900,
            R.color.blue_a100, R.color.blue_a200, R.color.blue_a400, R.color.blue_a700 };

    public static int[] indigo = new int[] { R.color.indigo_50, R.color.indigo_100, R.color.indigo_200, R.color.indigo_300, R.color.indigo_400, R.color.indigo_500, R.color.indigo_600, R.color.indigo_700, R.color.indigo_800, R.color.indigo_900, R.color.indigo_900,
            R.color.indigo_a100, R.color.indigo_a200, R.color.indigo_a400, R.color.indigo_a700 };

    public static int[] deep_purple = new int[] { R.color.deep_purple_50, R.color.deep_purple_100, R.color.deep_purple_200, R.color.deep_purple_300, R.color.deep_purple_400, R.color.deep_purple_500, R.color.deep_purple_600, R.color.deep_purple_700, R.color.deep_purple_800, R.color.deep_purple_900, R.color.deep_purple_900,
            R.color.deep_purple_a100, R.color.deep_purple_a200, R.color.deep_purple_a400, R.color.deep_purple_a700 };

    public static int[] purple = new int[] { R.color.purple_50, R.color.purple_100, R.color.purple_200, R.color.purple_300, R.color.purple_400, R.color.purple_500, R.color.purple_600, R.color.purple_700, R.color.purple_800, R.color.purple_900, R.color.purple_900,
            R.color.purple_a100, R.color.purple_a200, R.color.purple_a400, R.color.purple_a700 };
}
