// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2025 Forrest Guice
    This file is part of Natural Hour.

    Natural Hour is free software: you can redistribute it and/or modify
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

/**
 * ColorValues
 */
public class ClockColorValuesSun extends ClockColorValues
{
    public static final String COLOR_ID_SUN = "sun";

    public static String getJSON() {
        return "{\"colorValuesID\":\"sun_dark\",\"colorValuesLabel\":\"Sun\",\"color_plate\":\"#ff212121\",\"color_face\":\"#ff263238\",\"color_frame\":\"#ffdd2c00\",\"color_center\":\"#ffdd2c00\",\"color_hand\":\"#ffff3d00\",\"color_hand1\":\"#ffff3d00\",\"color_label0\":\"#ffdd2c00\",\"color_label1\":\"#ffdd2c00\",\"color_ring_day\":\"#ff263238\",\"color_ring_day_label\":\"#ffffcc00\",\"color_ring_day_stroke\":\"#ffffd740\",\"color_face_day\":\"#ffffcc00\",\"color_ring_night\":\"#ff3e2723\",\"color_ring_night_label\":\"#ffffffff\",\"color_ring_night_stroke\":\"#ffff9100\",\"color_face_night\":\"#82212121\",\"color_face_am\":\"#ffffd500\",\"color_face_pm\":\"#ffffc400\",\"color_face_astro\":\"#ff000066\",\"color_face_nautical\":\"#ff003366\",\"color_face_civil\":\"#ff7ba3ff\",\"color_background\":\"#ff000000\",\"color_backgroundalt\":\"#ff424242\",\"color_start\":\"#ffdd2c00\",\"color_seconds_major\":\"#ffdd2c00\",\"color_seconds_minor\":\"#ffdd2c00\"}";
    }

    public ClockColorValuesSun(Context context, boolean fallbackDarkTheme)
    {
        super(context, fallbackDarkTheme);
        loadColorValues(getJSON());
    }

    @Override
    public int[] getColorAttrs() {
        return new int[ getColorKeys().length ];    // 0 ... skip attrs
    }
    @Override
    public int[] getColorsResDark() {
        return new int[ getColorKeys().length ];
    }
    @Override
    public int[] getColorsResLight() {
        return new int[ getColorKeys().length ];
    }
    @Override
    public int[] getColorsFallback() {
        return new int[ getColorKeys().length ];
    }
}
