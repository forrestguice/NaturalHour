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

package com.forrestguice.suntimes.naturalhour.ui.clockview;

import android.content.Context;

import com.forrestguice.suntimes.naturalhour.ui.colors.MaterialPalette;
import static com.forrestguice.suntimes.naturalhour.ui.colors.MaterialPalette.Shade.*;

public class ClockColorValuesMaterialOrange extends ClockColorValues
{
    public static final String COLOR_ID_MATERIAL_ORANGE_DARK = "material_orange_dark";
    public static final String COLOR_ID_MATERIAL_ORANGE_LIGHT = "material_orange_light";

    public ClockColorValuesMaterialOrange(Context context, boolean fallbackDarkTheme) {
        super(context, fallbackDarkTheme);
    }

    @Override
    public int[] getColorAttrs() {
        return new int[ getColorKeys().length ];    // 0 ... skip attrs
    }

    protected static MaterialPalette darkPalette = new MaterialPalette(
            MaterialPalette.Color.grey, MaterialPalette.Color.brown,
            MaterialPalette.Color.deep_orange, MaterialPalette.Color.deep_orange, MaterialPalette.Color.deep_orange
    );

    @Override
    public int[] getColorsResDark()
    {
        MaterialPalette p = darkPalette;
        return new int[] {
                p.n2(s999), p.n2(s900), p.n1(s800), p.n1(s800),                // background, background (alt), plate, face
                p.a1(s200), p.a1(s200), p.a2(a100), p.a2(a200),                // frame, center, minutes_hand, seconds_hand
                p.a2(a200), p.a2(a100), p.a2(a100), p.a2(a200), p.a2(a400),    // label0, label1, label2, label3, label4
                p.a1(a200), p.a1(a200), p.a1(s200),                            // seconds_start, seconds_major, seconds_minor
                p.n1(s700), p.a2(a100), p.a2(s200), p.n2(s900),                // day-hour, day-hour label, day-hour border, day (face)
                p.n1(s900), p.a2(s300), p.a2(s500), p.n2(s900),                // night-hour, night-hour label, night-hour border, night (face)
                p.n2(s400), p.n2(s500), p.a3(s900), p.a3(s800), p.a3(s700)     // am, pm, astro, nautical, civil
        };
    }

    protected static MaterialPalette lightPalette = new MaterialPalette(
            MaterialPalette.Color.blue_grey, MaterialPalette.Color.brown,
            MaterialPalette.Color.deep_orange, MaterialPalette.Color.deep_orange, MaterialPalette.Color.deep_orange
    );

    @Override
    public int[] getColorsResLight() {
        MaterialPalette p = lightPalette;
        return new int[] {
                p.n2(s100), p.n1(s200), p.n2(s50),  p.n1(s50),                 // background, background (alt), plate, face
                p.a1(s600), p.a2(s200), p.a1(a400), p.a1(a400),                // frame, center, minutes_hand, seconds_hand
                p.a2(a400), p.a2(a400), p.a2(a400), p.a2(a400), p.a2(a400),    // label0, label1, label2, label3, label4
                p.a1(a200), p.a1(a400), p.a1(s600),                            // seconds_start, seconds_major, seconds_minor
                p.n1(s50),  p.a2(a200), p.a2(s500), p.n2(s900),                // day-hour, day-hour label, day-hour border, day (face)
                p.n2(s200), p.a2(a400), p.a2(s600), p.n2(s100),                // night-hour, night-hour label, night-hour border, night (face)
                p.n1(s50),  p.n2(s50),  p.a3(s300), p.a3(s200), p.a3(s100)     // am, pm, astro, nautical, civil
        };
    }
}
