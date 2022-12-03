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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;

import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;
import com.forrestguice.suntimes.naturalhour.ui.NaturalHourFragment;

import java.util.TimeZone;

public class AppSettings
{
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";
    public static final String THEME_SYSTEM = "system";

    public static final String KEY_MODE_TIMEFORMAT = "timeformatmode";
    public static final int TIMEMODE_SYSTEM = 0, TIMEMODE_SUNTIMES = 1, TIMEMODE_12HR = 2, TIMEMODE_24HR = 3;
    public static final int TIMEMODE_DEFAULT = TIMEMODE_24HR;

    public static final String KEY_MODE_TIMEZONE = "timezonemode";
    public static final int TZMODE_SYSTEM = 0, TZMODE_SUNTIMES = 1, TZMODE_LOCALMEAN = 2, TZMODE_APPARENTSOLAR = 3;
    public static final int TZMODE_DEFAULT = TZMODE_APPARENTSOLAR;

    public static final String[] VALUES = new String[] { AppSettings.KEY_MODE_TIMEFORMAT, AppSettings.KEY_MODE_TIMEZONE };
    public static final int[] VALUES_DEF = new int[] { AppSettings.TIMEMODE_DEFAULT, AppSettings.TZMODE_DEFAULT };

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
    public static int getClockIntValue(Context context, String key) {
        return getClockIntValue(context, key, getClockDefaultValue(context, key));
    }
    public static int getClockIntValue(Context context, String key, int defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(key, defaultValue);
    }
    public static int getClockDefaultValue(Context context, String key) {
        switch (key)
        {
            case KEY_MODE_TIMEFORMAT: return TIMEMODE_DEFAULT;
            case KEY_MODE_TIMEZONE: return TZMODE_DEFAULT;
            default: return -1;
        }
    }

    public static boolean containsKey(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).contains(key);
    }
    public static void deleteKey(Context context, String key) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(key).apply();
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
    public static boolean fromTimeFormatMode(@NonNull Context context, int mode, @Nullable SuntimesInfo suntimesInfo)
    {
        if (suntimesInfo == null) {
            return android.text.format.DateFormat.is24HourFormat(context);
        }
        switch (mode)
        {
            case TIMEMODE_12HR: return false;
            case TIMEMODE_24HR: return true;
            case TIMEMODE_SUNTIMES: return suntimesInfo.getOptions(context).time_is24;
            case TIMEMODE_SYSTEM: default: return android.text.format.DateFormat.is24HourFormat(context);
        }
    }

    public static TimeZone fromTimeZoneMode(@NonNull Context context, int mode, @Nullable SuntimesInfo suntimesInfo)
    {
        boolean hasLocation = (suntimesInfo != null && suntimesInfo.location != null && suntimesInfo.location.length >= 4);
        switch (mode) {
            case TZMODE_LOCALMEAN: return NaturalHourFragment.getLocalMeanTZ(context, hasLocation ? suntimesInfo.location[2] : "0");
            case TZMODE_APPARENTSOLAR: return NaturalHourFragment.getApparantSolarTZ(context, hasLocation ? suntimesInfo.location[2] : "0");
            case TZMODE_SUNTIMES: return NaturalHourFragment.getTimeZone(context, suntimesInfo);
            case TZMODE_SYSTEM: default: return TimeZone.getDefault();
        }
    }

    public static int getThemeResID(@NonNull String themeName)
    {
        return themeName.startsWith(AppSettings.THEME_SYSTEM) ? R.style.NaturalHourAppTheme_System
                : themeName.startsWith(AppSettings.THEME_LIGHT) ? R.style.NaturalHourAppTheme_Light
                : themeName.startsWith(AppSettings.THEME_DARK) ? R.style.NaturalHourAppTheme_Dark
                : R.style.NaturalHourAppTheme_Dark;
    }

    public static int setTheme(Activity activity, int themeResID)
    {
        activity.setTheme(themeResID);
        if (themeResID == R.style.NaturalHourAppTheme_System) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (themeResID == R.style.NaturalHourAppTheme_Light) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (themeResID == R.style.NaturalHourAppTheme_Dark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        return themeResID;
    }
}
