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

package com.forrestguice.suntimes.naturalhour;

import android.annotation.SuppressLint;
import android.app.UiModeManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.naturalhour.ui.DisplayStrings;
import com.forrestguice.suntimes.naturalhour.ui.clockview.ClockColorValues;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;
import com.forrestguice.suntimes.naturalhour.ui.NaturalHourFragment;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValues;

import java.util.TimeZone;

public class AppSettings
{
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";
    public static final String THEME_SYSTEM = "system";

    public static final String KEY_MODE_TIMEFORMAT = "timeformatmode";
    public static final int TIMEMODE_SYSTEM = 0, TIMEMODE_SUNTIMES = 1, TIMEMODE_12HR = 2, TIMEMODE_24HR = 3, TIMEMODE_6HR = 4;
    public static final int TIMEMODE_DEFAULT = TIMEMODE_24HR;

    public static final String KEY_MODE_TIMEZONE = "timezonemode";
    public static final int TZMODE_SYSTEM = 0, TZMODE_SUNTIMES = 1,
                            TZMODE_LOCALMEAN = 2, TZMODE_APPARENTSOLAR = 3, TZMODE_UTC = 4,
                            TZMODE_ITALIAN = 5, TZMODE_ITALIAN_CIVIL = 6, TZMODE_BABYLONIAN = 7, TZMODE_JULIAN = 8;
    public static final int TZMODE_DEFAULT = TZMODE_LOCALMEAN;

    @Deprecated    // replaced by "backgroundMode"
    public static final String KEY_USE_WALLPAPER = "useWallpaper";
    @Deprecated
    public static final boolean DEF_USE_WALLPAPER = false;

    public static final String KEY_MODE_BACKGROUND = "backgroundMode";
    public static final int BGMODE_APPTHEME = 0, BGMODE_WALLPAPER = 1, BGMODE_COLOR = 2, BGMODE_BLACK = 3;
    public static final int BGMODE_DEFAULT = BGMODE_APPTHEME;

    public static final String PREF_KEY_DIALOG = "dialog";
    public static final String PREF_KEY_DIALOG_DONOTSHOWAGAIN = "donotshowagain";

    public static final String[] VALUES = new String[] { AppSettings.KEY_MODE_TIMEFORMAT, AppSettings.KEY_MODE_TIMEZONE, AppSettings.KEY_MODE_BACKGROUND };
    public static final int[] VALUES_DEF = new int[] { AppSettings.TIMEMODE_DEFAULT, AppSettings.TZMODE_DEFAULT, AppSettings.BGMODE_DEFAULT };

    public static void setClockFlag(Context context, String key, boolean flag)
    {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.putBoolean(key, flag);
        prefs.apply();
    }
    public static boolean getClockFlag(Context context, String key) {
        return getClockFlag(context, key, getBitmapHelper(context));
    }
    public static boolean getClockFlag(Context context, String key, NaturalHourClockBitmap helper) {
        return getClockFlag(context, key, helper.getDefaultFlag(context, key));
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
        return getClockIntValue(context, key, getBitmapHelper(context));
    }
    public static int getClockIntValue(Context context, String key, NaturalHourClockBitmap helper) {
        return getClockIntValue(context, key, helper.getDefaultValue(context, key));
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

    protected static NaturalHourClockBitmap getBitmapHelper(Context context)
    {
        if (bitmapHelper == null) {
            bitmapHelper = new NaturalHourClockBitmap(context, 0);
        }
        return bitmapHelper;
    }
    private static NaturalHourClockBitmap bitmapHelper = null;

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

    @Deprecated    // replaced by "backgroundMode"
    public static boolean useWallpaper(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(KEY_USE_WALLPAPER, DEF_USE_WALLPAPER);
    }

    public static int getBackgroundMode(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(KEY_MODE_BACKGROUND, useWallpaper(context) ? BGMODE_WALLPAPER : BGMODE_DEFAULT);
    }
    public static void setBackgroundMode(Context context, int value) {
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        prefs.putInt(KEY_MODE_BACKGROUND, value);
        prefs.apply();
    }

    public static Integer getBackgroundModeColor(Context context, @Nullable ColorValues colors)
    {
        switch (AppSettings.getBackgroundMode(context))
        {
            case AppSettings.BGMODE_COLOR:
                return (colors != null
                    ? colors.getColor(ClockColorValues.COLOR_BACKGROUND)
                    : Color.BLACK);

            case AppSettings.BGMODE_BLACK:
                return Color.BLACK;

            case AppSettings.BGMODE_WALLPAPER:
                return Color.TRANSPARENT;

            default: return null;
        }
    }

    /**
     * @return true; dialog should not be shown (user has checked 'do not show again')
     */
    public static boolean checkDialogDoNotShowAgain( Context context, String dialogKey ) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(PREF_KEY_DIALOG + "_" + dialogKey + "_" + PREF_KEY_DIALOG_DONOTSHOWAGAIN, false);
    }
    public static void setDialogDoNotShowAgain(Context context, String dialogKey, boolean value)
    {
        SharedPreferences.Editor pref = PreferenceManager.getDefaultSharedPreferences(context).edit();
        pref.putBoolean(PREF_KEY_DIALOG + "_" + dialogKey + "_" + PREF_KEY_DIALOG_DONOTSHOWAGAIN, value);
        pref.apply();
    }
    public static AlertDialog.Builder buildAlertDialog(final String key, @NonNull LayoutInflater inflater,
                                                       int iconResId, @Nullable CharSequence title, @NonNull CharSequence message, @Nullable final DialogInterface.OnClickListener onOkClicked)
    {
        final Context context = inflater.getContext();
        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.layout_dialog_alert, null);
        final CheckBox check_notagain = (CheckBox) dialogView.findViewById(R.id.check_donotshowagain);

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        if (title != null) {
            dialog.setTitle(title);
        }
        dialog.setMessage(message)
                .setView(dialogView)
                .setIcon(iconResId)
                .setCancelable(false)
                .setPositiveButton(context.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (check_notagain != null) {
                            AppSettings.setDialogDoNotShowAgain(context, key, check_notagain.isChecked());
                        }
                        if (onOkClicked != null) {
                            onOkClicked.onClick(dialog, which);
                        }
                    }
                });
        return dialog;
    }

    public static final String DIALOG_GPL_NOTICE = "gpl_notice";
    public static void showLicenseNotice(Context context, LayoutInflater layoutInflater)
    {
        AlertDialog dialog = AppSettings.buildAlertDialog(DIALOG_GPL_NOTICE, layoutInflater,
                R.drawable.ic_about_ref, context.getString(android.R.string.dialog_alert_title),
                DisplayStrings.fromHtml(context.getString(R.string.gpl_notice)), null).show();

        TextView message = dialog.findViewById(android.R.id.message);
        if (message != null) {
            message.setLinksClickable(true);
            message.setClickable(true);
            message.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public static void sanityCheck(Context context)
    {
        if (!AppSettings.checkDialogDoNotShowAgain(context, DIALOG_GPL_NOTICE)
                && sanityCheck1(context)) {
            showLicenseNotice(context, (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        } else {
            setDialogDoNotShowAgain(context, DIALOG_GPL_NOTICE, true);
        }
    }
    public static void sanityCheck0(Context context) {
        if (BuildConfig.DEBUG) {
            setDialogDoNotShowAgain(context, DIALOG_GPL_NOTICE, true);
        }
    }
    public static boolean sanityCheck1(Context context)
    {
        if (!BuildConfig.DEBUG)
        {
            String publishedID = context.getString(R.string.published_package_id);
            String packageID = "com" +                       // Thinking about removing or changing these lines?
                    "." + "forrest" + "guice" +              // They were placed here intentionally to deter GPLv3 license violations.
                    "." + "sun" + "times" +                  // Please ensure compliance with the LICENSE before distributing modified versions of this software.
                    "." + "natural" + "hour";

            boolean showWarning = (!packageID.equals(publishedID));
            showWarning |= (!packageID.equals(context.getPackageName()));

            int publishedTargetSdkVersion = context.getResources().getInteger(R.integer.published_targetSdkVersion);
            int targetSdkVersion = context.getApplicationContext().getApplicationInfo().targetSdkVersion;
            showWarning |= (targetSdkVersion != publishedTargetSdkVersion);

            if (Build.VERSION.SDK_INT >= 24)
            {
                int publishedMinSdkVersion = context.getResources().getInteger(R.integer.published_minSdkVersion);
                int minSdkVersion = context.getApplicationContext().getApplicationInfo().minSdkVersion;
                showWarning |= (minSdkVersion != publishedMinSdkVersion);
            }

            return showWarning;
        }
        return false;
    }

    /**
     * @param mode TIMEMODE_12HR, TIMEMODE_24HR, TIMEMODE_SUNTIMES, TIMEMODE_SYSTEM
     * @return NaturalHourClockBitmap.TIMEFORMAT values; e.g. 6, 12, 24
     */
    public static int fromTimeFormatMode(@NonNull Context context, int mode, @Nullable SuntimesInfo suntimesInfo)
    {
        if (suntimesInfo == null) {
            return (android.text.format.DateFormat.is24HourFormat(context) ? NaturalHourClockBitmap.TIMEFORMAT_24 : NaturalHourClockBitmap.TIMEFORMAT_12);
        }
        switch (mode)
        {
            case TIMEMODE_6HR: return NaturalHourClockBitmap.TIMEFORMAT_6;
            case TIMEMODE_12HR: return NaturalHourClockBitmap.TIMEFORMAT_12;
            case TIMEMODE_24HR: return NaturalHourClockBitmap.TIMEFORMAT_24;
            case TIMEMODE_SUNTIMES: return suntimesInfo.getOptions(context).time_is24 ? NaturalHourClockBitmap.TIMEFORMAT_24 : NaturalHourClockBitmap.TIMEFORMAT_12;
            case TIMEMODE_SYSTEM: default: return android.text.format.DateFormat.is24HourFormat(context) ? NaturalHourClockBitmap.TIMEFORMAT_24 : NaturalHourClockBitmap.TIMEFORMAT_12;
        }
    }

    public static TimeZone fromTimeZoneMode(@NonNull Context context, int mode, @Nullable SuntimesInfo suntimesInfo)
    {
        boolean hasLocation = (suntimesInfo != null && suntimesInfo.location != null && suntimesInfo.location.length >= 4);
        switch (mode)
        {
            case TZMODE_ITALIAN: return NaturalHourFragment.getItalianHoursTZ(context, hasLocation ? suntimesInfo.location[2] : "0");
            case TZMODE_ITALIAN_CIVIL: return NaturalHourFragment.getItalianCivilHoursTZ(context, hasLocation ? suntimesInfo.location[2] : "0");
            case TZMODE_BABYLONIAN: return NaturalHourFragment.getBabylonianHoursTZ(context, hasLocation ? suntimesInfo.location[2] : "0");
            case TZMODE_JULIAN: return NaturalHourFragment.getJulianHoursTZ(context, hasLocation ? suntimesInfo.location[2] : "0");
            case TZMODE_UTC: return NaturalHourFragment.getUtcTZ();
            case TZMODE_LOCALMEAN: return NaturalHourFragment.getLocalMeanTZ(context, hasLocation ? suntimesInfo.location[2] : "0");
            case TZMODE_APPARENTSOLAR: return NaturalHourFragment.getApparentSolarTZ(context, hasLocation ? suntimesInfo.location[2] : "0");
            case TZMODE_SUNTIMES: return NaturalHourFragment.getTimeZone(context, suntimesInfo);
            case TZMODE_SYSTEM: default: return TimeZone.getDefault();
        }
    }

    /**
     * Is the current device a television? This implies limited features.
     */
    public static boolean isTelevision(@NonNull Context context)
    {
        if (context != null)
        {
            if (Build.VERSION.SDK_INT >= 21) {
                return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK);

            } else if (Build.VERSION.SDK_INT >= 13) {
                UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
                return (uiModeManager != null && (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION));

            } else return false;
        } else return false;
    }

}
