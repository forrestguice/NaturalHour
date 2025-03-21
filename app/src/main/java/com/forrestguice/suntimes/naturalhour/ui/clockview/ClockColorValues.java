// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2020-2025 Forrest Guice
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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;

import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValues;
import com.forrestguice.suntimes.naturalhour.ui.colors.ResourceColorValues;

/**
 * ColorValues
 */
public class ClockColorValues extends ResourceColorValues implements Parcelable
{
    public static final String COLOR_ID_DARK = "dark";
    public static final String COLOR_ID_LIGHT = "light";

    public static final String COLOR_BACKGROUND = "color_background";
    public static final String COLOR_BACKGROUND_ALT = "color_backgroundalt";
    public static final String COLOR_PLATE = "color_plate";
    public static final String COLOR_FRAME = "color_frame";
    public static final String COLOR_HAND = "color_hand";
    public static final String COLOR_HAND1 = "color_hand1";
    public static final String COLOR_SECONDS_MAJOR = "color_seconds_major";
    public static final String COLOR_SECONDS_MINOR = "color_seconds_minor";
    public static final String COLOR_START = "color_start";
    public static final String COLOR_CENTER = "color_center";
    public static final String COLOR_LABEL = "color_label0";
    public static final String COLOR_LABEL1 = "color_label1";

    public static final String COLOR_FACE = "color_face";
    public static final String COLOR_FACE_AM = "color_face_am";
    public static final String COLOR_FACE_PM = "color_face_pm";
    public static final String COLOR_FACE_DAY = "color_face_day";
    public static final String COLOR_FACE_NIGHT = "color_face_night";
    public static final String COLOR_FACE_CIVIL = "color_face_civil";
    public static final String COLOR_FACE_NAUTICAL = "color_face_nautical";
    public static final String COLOR_FACE_ASTRO = "color_face_astro";

    public static final String COLOR_RING_DAY = "color_ring_day";
    public static final String COLOR_RING_DAY_STROKE = "color_ring_day_stroke";
    public static final String COLOR_RING_DAY_LABEL = "color_ring_day_label";
    public static final String COLOR_RING_NIGHT = "color_ring_night";
    public static final String COLOR_RING_NIGHT_STROKE = "color_ring_night_stroke";
    public static final String COLOR_RING_NIGHT_LABEL = "color_ring_night_label";

    public static final String[] COLORS = new String[] {
            COLOR_BACKGROUND, COLOR_BACKGROUND_ALT, COLOR_PLATE, COLOR_FACE, COLOR_FRAME,
            COLOR_CENTER, COLOR_HAND, COLOR_HAND1, COLOR_LABEL, COLOR_LABEL1,
            COLOR_START, COLOR_SECONDS_MAJOR, COLOR_SECONDS_MINOR,
            COLOR_RING_DAY, COLOR_RING_DAY_LABEL, COLOR_RING_DAY_STROKE, COLOR_FACE_DAY,
            COLOR_RING_NIGHT, COLOR_RING_NIGHT_LABEL, COLOR_RING_NIGHT_STROKE, COLOR_FACE_NIGHT,
            COLOR_FACE_AM, COLOR_FACE_PM, COLOR_FACE_ASTRO, COLOR_FACE_NAUTICAL, COLOR_FACE_CIVIL
    };
    protected static final int[] COLORS_ATTR = new int[] {
            R.attr.clockColorBackground, R.attr.clockColorBackgroundAlt, R.attr.clockColorPlate, R.attr.clockColorFace, R.attr.clockColorFrame,
            R.attr.clockColorCenter, R.attr.clockColorHand, R.attr.clockColorHand1, R.attr.clockColorLabel1, R.attr.clockColorLabel2,
            R.attr.clockColorFrame, R.attr.clockColorFrame, R.attr.clockColorFrame,
            R.attr.clockColorDayFill, R.attr.clockColorDayText, R.attr.clockColorDayBorder, R.attr.clockColorDay,
            R.attr.clockColorNightFill, R.attr.clockColorNightText, R.attr.clockColorNightBorder, R.attr.clockColorNight,
            R.attr.clockColorAM, R.attr.clockColorPM, R.attr.clockColorAstro, R.attr.clockColorNautical, R.attr.clockColorCivil
    };
    protected static final int[] COLORS_RES_DARK = new int[] {
            R.color.clockColorBackground_dark, R.color.clockColorBackgroundAlt_dark, R.color.clockColorPlate_dark, R.color.clockColorFace_dark, R.color.clockColorFrame_dark,
            R.color.clockColorCenter_dark, R.color.clockColorHand_dark, R.color.clockColorHand1_dark, R.color.clockColorLabel1_dark, R.color.clockColorLabel2_dark,
            R.color.clockColorFrame_dark, R.color.clockColorFrame_dark, R.color.clockColorFrame_dark,
            R.color.clockColorDay_dark, R.color.clockColorDayLabel_dark, R.color.clockColorDayBorder_dark, R.color.clockColorDayFace_dark,
            R.color.clockColorNight_dark, R.color.clockColorNightLabel_dark, R.color.clockColorNightBorder_dark, R.color.clockColorNightFace_dark,
            R.color.clockColorAM_dark, R.color.clockColorPM_dark, R.color.clockColorAstro_dark, R.color.clockColorNautical_dark, R.color.clockColorCivil_dark
    };
    protected static final int[] COLORS_RES_LIGHT = new int[] {
            R.color.clockColorBackground_light, R.color.clockColorBackgroundAlt_light, R.color.clockColorPlate_light, R.color.clockColorFace_light, R.color.clockColorFrame_light,
            R.color.clockColorCenter_light, R.color.clockColorHand_light, R.color.clockColorHand1_light, R.color.clockColorLabel1_light, R.color.clockColorLabel2_light,
            R.color.clockColorFrame_light, R.color.clockColorFrame_light, R.color.clockColorFrame_light,
            R.color.clockColorDay_light, R.color.clockColorDayLabel_light, R.color.clockColorDayBorder_light, R.color.clockColorDayFace_light,
            R.color.clockColorNight_light, R.color.clockColorNightLabel_light, R.color.clockColorNightBorder_light, R.color.clockColorNightFace_light,
            R.color.clockColorAM_light, R.color.clockColorPM_light, R.color.clockColorAstro_light, R.color.clockColorNautical_light, R.color.clockColorCivil_light
    };
    public static final int[] LABELS_RESID = new int[] {
            R.string.clockface_background, R.string.clockface_backgroundAlt, R.string.clockface_plate, R.string.clockface_face, R.string.clockface_frame,
            R.string.clockface_center, R.string.clockface_hand, R.string.clockface_hand1, R.string.clockface_label, R.string.clockface_label1,
            R.string.clockface_start, R.string.clockface_seconds_major, R.string.clockface_seconds_minor,
            R.string.clockface_ring_day, R.string.clockface_ring_day_label, R.string.clockface_ring_day_stroke, R.string.clockface_face_day,
            R.string.clockface_ring_night, R.string.clockface_ring_night_label, R.string.clockface_ring_night_stroke, R.string.clockface_face_night,
            R.string.clockface_face_am, R.string.clockface_face_pm, R.string.clockface_face_astro, R.string.clockface_face_nautical, R.string.clockface_face_civil
    };

    public static final int[] COLOR_ROLES = new int[] {
            ROLE_BACKGROUND, ROLE_BACKGROUND, ROLE_BACKGROUND, ROLE_BACKGROUND_PRIMARY, ROLE_FOREGROUND,
            ROLE_FOREGROUND, ROLE_FOREGROUND, ROLE_FOREGROUND, ROLE_TEXT, ROLE_TEXT,
            ROLE_FOREGROUND, ROLE_FOREGROUND, ROLE_FOREGROUND,
            ROLE_BACKGROUND, ROLE_TEXT, ROLE_FOREGROUND, ROLE_BACKGROUND,
            ROLE_BACKGROUND, ROLE_TEXT, ROLE_FOREGROUND, ROLE_BACKGROUND,
            ROLE_BACKGROUND, ROLE_BACKGROUND, ROLE_BACKGROUND, ROLE_BACKGROUND, ROLE_BACKGROUND
    };

    protected static final int[] COLORS_FALLBACK = new int[] {
            Color.BLACK, Color.DKGRAY, Color.BLACK, Color.DKGRAY, Color.WHITE,
            Color.WHITE, Color.MAGENTA, Color.GREEN, Color.WHITE, Color.LTGRAY,
            Color.WHITE, Color.WHITE, Color.WHITE,
            Color.DKGRAY, Color.WHITE, Color.WHITE, ColorUtils.setAlphaComponent(Color.WHITE, 128),
            Color.BLUE, Color.YELLOW, Color.DKGRAY, ColorUtils.setAlphaComponent(Color.BLUE, 128),
            Color.LTGRAY, Color.DKGRAY, Color.BLACK, Color.BLUE, Color.CYAN
    };

    @Override
    public String[] getColorKeys() {
        return COLORS;
    }

    @Override
    public int[] getColorAttrs() {
        return COLORS_ATTR;
    }

    @Override
    public int[] getColorLabelsRes() {
        return LABELS_RESID;
    }

    @Override
    public int[] getColorRoles() {
        return COLOR_ROLES;
    }

    @Override
    public int[] getColorsResDark() {
        return COLORS_RES_DARK;
    }

    @Override
    public int[] getColorsResLight() {
        return COLORS_RES_LIGHT;
    }

    @Override
    public int[] getColorsFallback() {
        return COLORS_FALLBACK;
    }

    public ClockColorValues(ColorValues other) {
        super(other);
    }
    public ClockColorValues(SharedPreferences prefs, String prefix) {
        super(prefs, prefix);
    }
    private ClockColorValues(Parcel in) {
        super(in);
    }
    public ClockColorValues() {
        super();
    }
    public ClockColorValues(Context context) {
        this(context, true);
    }
    public ClockColorValues(Context context, boolean darkTheme) {
        super(context, darkTheme);
    }
    public ClockColorValues(String jsonString) {
        super(jsonString);
    }

    public static final Creator<ClockColorValues> CREATOR = new Creator<ClockColorValues>()
    {
        public ClockColorValues createFromParcel(Parcel in) {
            return new ClockColorValues(in);
        }
        public ClockColorValues[] newArray(int size) {
            return new ClockColorValues[size];
        }
    };

    public static ClockColorValues getColorDefaults(Context context, boolean darkTheme)
    {
        ClockColorValues values = new ClockColorValues();
        int[] defaultResID = darkTheme ? COLORS_RES_DARK : COLORS_RES_LIGHT;
        for (int i=0; i<COLORS.length; i++) {
            values.setColor(COLORS[i], ContextCompat.getColor(context, defaultResID[i]));
            values.setLabel(COLORS[i], (LABELS_RESID[i] != 0 ? context.getString(LABELS_RESID[i]) : COLORS[i]));
            values.setRole(COLORS[i], COLOR_ROLES[i]);
        }
        values.setID(darkTheme ? COLOR_ID_DARK : COLOR_ID_LIGHT);
        values.setLabel(darkTheme ? context.getString(R.string.defaultColors_name_dark) : context.getString(R.string.defaultColors_name_light));
        return values;
    }
}
