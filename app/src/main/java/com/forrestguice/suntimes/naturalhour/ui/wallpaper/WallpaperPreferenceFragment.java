// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2021 Forrest Guice
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

package com.forrestguice.suntimes.naturalhour.ui.wallpaper;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.ui.widget.WidgetPreferenceFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WallpaperPreferenceFragment extends WidgetPreferenceFragment
{
    public WallpaperPreferenceFragment() {
        super();
    }

    @Override
    public int getPrefResId() {
        return R.xml.pref_wallpaper;
    }

    @Override
    protected void initWidgetDefaults()
    {
        super.initWidgetDefaults();

        Context context = getActivity();
        int appWidgetId = getAppWidgetId();
        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID && context != null)
        {
            String widgetPrefix0 = widgetKeyPrefix(0);
            String widgetPrefix = widgetKeyPrefix(appWidgetId);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = prefs.edit();

            for (int i = 0; i<NaturalHourWallpaper.VALUES.length; i++) {
                String prefKey = widgetPrefix0 + NaturalHourWallpaper.VALUES[i];
                String widgetKey = widgetPrefix + NaturalHourWallpaper.VALUES[i];
                editor.putInt(widgetKey, prefs.getInt(prefKey, NaturalHourWallpaper.VALUES_DEF[i]));
            }

            editor.apply();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(onWallpaperPrefChanged);
    }

    @Override
    public void onPause()
    {
        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(onWallpaperPrefChanged);
        super.onPause();
    }

    private SharedPreferences.OnSharedPreferenceChangeListener onWallpaperPrefChanged = new SharedPreferences.OnSharedPreferenceChangeListener()
    {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String prefKey)
        {
            int appWidgetId = getAppWidgetId();
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID)
            {
                String widgetPrefix0 = widgetKeyPrefix(0);
                if (prefKey.startsWith(widgetPrefix0))
                {
                    String key = prefKey.replace(widgetPrefix0, "");
                    String widgetKey = widgetKeyPrefix(appWidgetId) + key;
                    SharedPreferences.Editor editor = prefs.edit();
                    for (int i = 0; i<NaturalHourWallpaper.VALUES.length; i++)
                    {
                        if (NaturalHourWallpaper.VALUES[i].equals(key)) {
                            editor.putInt(widgetKey, prefs.getInt(prefKey, NaturalHourWallpaper.VALUES_DEF[i]));
                            editor.apply();
                            return;
                        }
                    }
                }
            } else Log.e("onWallpaperPrefChanged", "AppWidgetID is unset! ignoring change to " + prefKey);
        }
    };

}