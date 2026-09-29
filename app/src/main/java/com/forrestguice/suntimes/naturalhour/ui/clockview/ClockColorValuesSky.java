// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2026 Forrest Guice
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
public class ClockColorValuesSky extends ClockColorValues
{
    public static final String COLOR_ID_SKY = "sky";

    public static String getJSON() {
        return "{\"colorValuesID\":\"sky\",\"colorValuesLabel\":\"Sky\",\"color_background\":\"#ff0d47a1\",\"color_backgroundalt\":\"#ff1565c0\",\"color_plate\":\"#7f180704\",\"color_face\":\"#ff263238\",\"color_frame\":\"#ffb2ebf2\",\"color_center\":\"#ffdd2c00\",\"color_hand\":\"#ffb2ebf2\",\"color_hand1\":\"#ffe0f7fa\",\"color_label0\":\"#ffe0f7fa\",\"color_label_tz\":\"#ffb2ebf2\",\"color_label_date\":\"#ffe0f7fa\",\"color_label_loc\":\"#ffb2ebf2\",\"color_start\":\"#ffe0f7fa\",\"color_seconds_major\":\"#ffe0f7fa\",\"color_seconds_minor\":\"#ffe0f7fa\",\"color_ring_day\":\"#ff0277bd\",\"color_ring_day_label\":\"#ffffc400\",\"color_ring_day_stroke\":\"#ff80d8ff\",\"color_face_day\":\"#ffffcc00\",\"color_ring_night\":\"#ff263238\",\"color_ring_night_label\":\"#ff80d8ff\",\"color_ring_night_stroke\":\"#ff80d8ff\",\"color_face_night\":\"#82212121\",\"color_face_am\":\"#ff4fc3f7\",\"color_face_pm\":\"#ff03a9f4\",\"color_face_astro\":\"#ff1a237e\",\"color_face_nautical\":\"#ff01579b\",\"color_face_civil\":\"#ff0288d1\"}";
    }

    public ClockColorValuesSky(Context context, boolean fallbackDarkTheme)
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
