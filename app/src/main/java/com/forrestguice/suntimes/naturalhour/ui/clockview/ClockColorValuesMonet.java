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

import com.forrestguice.suntimes.naturalhour.R;

/**
 * ColorValues
 */
public class ClockColorValuesMonet extends ClockColorValues
{
    public static final String COLOR_ID_MONET = "monet";

    protected static final int[] COLORS_RES_DARK = new int[] {
            R.color.monet_clockColorBackground_dark, R.color.monet_clockColorBackgroundAlt_dark, R.color.monet_clockColorPlate_dark, R.color.monet_clockColorFace_dark, R.color.monet_clockColorFrame_dark,
            R.color.monet_clockColorCenter_dark, R.color.monet_clockColorHand_dark, R.color.monet_clockColorHand1_dark, R.color.monet_clockColorLabel1_dark, R.color.monet_clockColorLabel2_dark,
            R.color.monet_clockColorFrame_dark, R.color.monet_clockColorFrame_dark, R.color.monet_clockColorFrame_dark,
            R.color.monet_clockColorDay_dark, R.color.monet_clockColorDayLabel_dark, R.color.monet_clockColorDayBorder_dark, R.color.monet_clockColorDayFace_dark,
            R.color.monet_clockColorNight_dark, R.color.monet_clockColorNightLabel_dark, R.color.monet_clockColorNightBorder_dark, R.color.monet_clockColorNightFace_dark,
            R.color.monet_clockColorAM_dark, R.color.monet_clockColorPM_dark, R.color.monet_clockColorAstro_dark, R.color.monet_clockColorNautical_dark, R.color.monet_clockColorCivil_dark
    };

    protected static final int[] COLORS_RES_LIGHT = new int[] {
            R.color.monet_clockColorBackground_light, R.color.monet_clockColorBackgroundAlt_light, R.color.monet_clockColorPlate_light, R.color.monet_clockColorFace_light, R.color.monet_clockColorFrame_light,
            R.color.monet_clockColorCenter_light, R.color.monet_clockColorHand_light, R.color.monet_clockColorHand1_light, R.color.monet_clockColorLabel1_light, R.color.monet_clockColorLabel2_light,
            R.color.monet_clockColorFrame_light, R.color.monet_clockColorFrame_light, R.color.monet_clockColorFrame_light,
            R.color.monet_clockColorDay_light, R.color.monet_clockColorDayLabel_light, R.color.monet_clockColorDayBorder_light, R.color.monet_clockColorDayFace_light,
            R.color.monet_clockColorNight_light, R.color.monet_clockColorNightLabel_light, R.color.monet_clockColorNightBorder_light, R.color.monet_clockColorNightFace_light,
            R.color.monet_clockColorAM_light, R.color.monet_clockColorPM_light, R.color.monet_clockColorAstro_light, R.color.monet_clockColorNautical_light, R.color.monet_clockColorCivil_light
    };

    public ClockColorValuesMonet(Context context, boolean fallbackDarkTheme) {
        super(context, fallbackDarkTheme);
    }

    @Override
    public int[] getColorAttrs() {
        return new int[ getColorKeys().length ];    // 0 ... skip attrs
    }
    @Override
    public int[] getColorsResDark() {
        return COLORS_RES_DARK;
    }
    @Override
    public int[] getColorsResLight() {
        return COLORS_RES_LIGHT;
    }
}
