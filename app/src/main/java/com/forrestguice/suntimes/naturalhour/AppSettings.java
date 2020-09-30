// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2020 Forrest Guice
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

package com.forrestguice.suntimes.naturalhour;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.naturalhour.ui.NaturalHourClockBitmap;
import com.forrestguice.suntimes.naturalhour.ui.NaturalHourFragment;

import java.util.TimeZone;

public class AppSettings
{
    public static final String KEY_CLOCK_ORIENTATION = "clockorientation";
    public static final int ORIENTATION_TOP = 0;        // midnight on top
    public static final int ORIENTATION_BOTTOM = 1;     // midnight on bottom
    public static final int ORIENTATION_DEFAULT = ORIENTATION_BOTTOM;

    public static final String KEY_CLOCK_HOURMODE = "clockhourmode";
    public static final int HOURMODE_ACTUAL = 0;     // day is bounded by actual sunrise/sunset
    public static final int HOURMODE_CIVIL = 1;      // day is bounded by civil twilight rise/set
    public static final int HOURMODE_DEFAULT = HOURMODE_ACTUAL;

    public static final String KEY_MODE_TIMEFORMAT = "timeformatmode";
    public static final int TIMEMODE_SYSTEM = 0, TIMEMODE_SUNTIMES = 1, TIMEMODE_12HR = 2, TIMEMODE_24HR = 3;
    public static final int TIMEMODE_DEFAULT = TIMEMODE_24HR;

    public static final String KEY_MODE_TIMEZONE = "timezonemode";
    public static final int TZMODE_SYSTEM = 0, TZMODE_SUNTIMES = 1, TZMODE_LOCALMEAN = 2, TZMODE_APPARENTSOLAR = 3;
    public static final int TZMODE_DEFAULT = TZMODE_APPARENTSOLAR;

    public static void setClockFlag(Context context, String key, boolean flag)
    {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.putBoolean(key, flag);
        prefs.apply();
    }
    public static boolean getClockFlag(Context context, String key) {
        return getClockFlag(context, key, NaturalHourClockBitmap.getDefaultFlag(context, key));
    }
    public static boolean getClockFlag(Context context, String key, boolean defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(key, defaultValue);
    }

    public static void setClockValue(Context context, String key, int value) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.putInt(key, value);
        prefs.apply();
    }
    public static int getClockValue(Context context, String key) {
        return getClockValue(context, key, getClockDefaultValue(context, key));
    }
    public static int getClockValue(Context context, String key, int defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(key, defaultValue);
    }
    public static int getClockDefaultValue(Context context, String key) {
        switch (key)
        {
            case KEY_CLOCK_ORIENTATION: return ORIENTATION_DEFAULT;
            case KEY_CLOCK_HOURMODE: return HOURMODE_DEFAULT;
            case KEY_MODE_TIMEFORMAT: return TIMEMODE_DEFAULT;
            case KEY_MODE_TIMEZONE: return TZMODE_DEFAULT;
            default: return -1;
        }
    }

    public static void setTimeFormatMode(Context context, int mode)
    {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.putInt(KEY_MODE_TIMEFORMAT, mode);
        prefs.apply();
    }
    public static int getTimeFormatMode(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(KEY_MODE_TIMEFORMAT, TIMEMODE_DEFAULT);
    }

    public static void setTimeZoneMode(Context context, int mode) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.putInt(KEY_MODE_TIMEZONE, mode);
        prefs.apply();
    }
    public static int getTimeZoneMode(Context context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(KEY_MODE_TIMEZONE, TZMODE_DEFAULT);
    }

    /**
     * @param mode TIMEMODE_12HR, TIMEMODE_24HR, TIMEMODE_SUNTIMES, TIMEMODE_SYSTEM
     * @return true 24hr format, false 12hr format
     */
    public static boolean fromTimeFormatMode(Context context, int mode, SuntimesInfo suntimesInfo)
    {
        switch (mode)
        {
            case TIMEMODE_12HR: return false;
            case TIMEMODE_24HR: return true;
            case TIMEMODE_SUNTIMES: return suntimesInfo.getOptions(context).time_is24;
            case TIMEMODE_SYSTEM: default: return android.text.format.DateFormat.is24HourFormat(context);
        }
    }

    public static TimeZone fromTimeZoneMode(Context context, int mode, SuntimesInfo suntimesInfo)
    {
        switch (mode)
        {
            case TZMODE_SUNTIMES: return NaturalHourFragment.getTimeZone(context, suntimesInfo);
            case TZMODE_LOCALMEAN: return NaturalHourFragment.getLocalMeanTZ(context, suntimesInfo.location[2]);
            case TZMODE_APPARENTSOLAR: return NaturalHourFragment.getApparantSolarTZ(context, suntimesInfo.location[2]);
            case TZMODE_SYSTEM: default: return TimeZone.getDefault();
        }
    }
}
