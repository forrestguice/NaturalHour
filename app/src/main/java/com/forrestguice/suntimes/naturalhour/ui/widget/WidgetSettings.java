// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2022 Forrest Guice
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

package com.forrestguice.suntimes.naturalhour.ui.widget;

import android.content.Context;
import androidx.preference.PreferenceManager;

import com.forrestguice.suntimes.naturalhour.AppSettings;

public class WidgetSettings
{
    public static final String KEY_PREFIX = "widget";
    public static String widgetKeyPrefix(int appWidgetId) {
        return KEY_PREFIX + "_" + appWidgetId + "_";
    }

    public static final String KEY_MODE_ACTION = "actionmode";
    public static final int ACTIONMODE_NOTHING = 0, ACTIONMODE_UPDATE = 1, ACTIONMODE_RECONFIGURE = 2, ACTIONMODE_LAUNCHAPP = 3;
    public static final int ACTIONMODE_DEFAULT = ACTIONMODE_RECONFIGURE;

    public static final String[] VALUES = new String[] { KEY_MODE_ACTION };
    public static final int[] VALUES_DEF = new int[] { ACTIONMODE_DEFAULT };

    public static String fromActionMode(int mode)
    {
        switch (mode) {
            case ACTIONMODE_NOTHING: return NaturalHourWidget.ACTION_WIDGET_CLICK_DONOTHING;
            case ACTIONMODE_UPDATE: return NaturalHourWidget.ACTION_WIDGET_UPDATE;
            case ACTIONMODE_RECONFIGURE: return NaturalHourWidget.ACTION_WIDGET_CLICK_RECONFIGURE;
            case ACTIONMODE_LAUNCHAPP: default: return NaturalHourWidget.ACTION_WIDGET_CLICK_LAUNCHAPP;    // TODO: configurable
        }
    }

    public static int getWidgetIntValue(Context context, int appWidgetId, String key) {
        return getWidgetIntValue(context, appWidgetId, key, getWidgetDefaultValue(context, key));
    }
    public static int getWidgetIntValue(Context context, int appWidgetId, String key, int defaultValue) {
        return AppSettings.getClockIntValue(context, widgetKeyPrefix(appWidgetId) + key, defaultValue);
    }

    public static boolean containsKey(Context context, int appWidgetId, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).contains(widgetKeyPrefix(appWidgetId) + key);
    }
    public static void deleteKey(Context context, int appWidgetId, String key) {
        AppSettings.deleteKey(context, widgetKeyPrefix(appWidgetId) + key);
    }

    public static int getWidgetDefaultValue(Context context, String key) {
        switch (key) {
            case KEY_MODE_ACTION: return ACTIONMODE_DEFAULT;
            default: return -1;
        }
    }

}