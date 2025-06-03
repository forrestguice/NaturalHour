/**
    Copyright (C) 2024-2025 Forrest Guice
    This file is part of NaturalHour.

    NaturalHour is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    NaturalHour is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with NaturalHour.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.forrestguice.suntimes.naturalhour.ui.daydream;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;
import com.forrestguice.suntimes.naturalhour.ui.widget.WidgetPreferenceFragment;
import com.forrestguice.suntimes.naturalhour.ui.widget.WidgetSettings;

public class DaydreamPreferenceFragment extends WidgetPreferenceFragment
{
    @Override
    protected NaturalHourClockBitmap createBitmapHelper(Context context) {
        return new ClockDaydreamBitmap(context, 0);
    }

    @Override
    public int getPreferenceResources() {
        return R.xml.pref_daydream;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(onDaydreamPrefChanged);
    }

    @Override
    public void onPause()
    {
        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(onDaydreamPrefChanged);
        super.onPause();
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener onDaydreamPrefChanged = new SharedPreferences.OnSharedPreferenceChangeListener()
    {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String prefKey)
        {
            int appWidgetId = getAppWidgetId();
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID)
            {
                String widgetPrefix0 = WidgetSettings.widgetKeyPrefix(0);
                if (prefKey.startsWith(widgetPrefix0))
                {
                    String key = prefKey.replace(widgetPrefix0, "");
                    String widgetKey = WidgetSettings.widgetKeyPrefix(appWidgetId) + key;

                    SharedPreferences.Editor editor = prefs.edit();
                    for (int i=0; i<DaydreamSettings.VALUES.length; i++) {
                        if (DaydreamSettings.VALUES[i].equals(key)) {
                            editor.putInt(widgetKey, prefs.getInt(prefKey, DaydreamSettings.VALUES_DEF[i]));
                            editor.apply();
                            return;
                        }
                    }

                    for (int i=0; i<DaydreamSettings.FLAGS.length; i++) {
                        if (DaydreamSettings.FLAGS[i].equals(key)) {
                            editor.putBoolean(widgetKey, prefs.getBoolean(prefKey, DaydreamSettings.FLAGS_DEF[i]));
                            editor.apply();
                            return;
                        }
                    }
                }

            } else Log.e("onDaydreamPrefChanged", "AppWidgetID is unset! ignoring change to " + prefKey);
        }
    };

    @Override
    protected void initWidgetDefaults()
    {
        Context context = getActivity();
        int appWidgetId = getAppWidgetId();
        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID && context != null)
        {
            String widgetPrefix0 = WidgetSettings.widgetKeyPrefix(0);
            String widgetPrefix = WidgetSettings.widgetKeyPrefix(appWidgetId);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = prefs.edit();

            for (int i = 0; i<DaydreamSettings.VALUES.length; i++) {
                String prefKey = widgetPrefix0 + DaydreamSettings.VALUES[i];
                String widgetKey = widgetPrefix + DaydreamSettings.VALUES[i];
                editor.putInt(widgetKey, prefs.getInt(prefKey, DaydreamSettings.VALUES_DEF[i]));
            }

            for (int i = 0; i<DaydreamSettings.FLAGS.length; i++) {
                String prefKey = widgetPrefix0 + DaydreamSettings.FLAGS[i];
                String widgetKey = widgetPrefix + DaydreamSettings.FLAGS[i];
                editor.putBoolean(widgetKey, prefs.getBoolean(prefKey, DaydreamSettings.FLAGS_DEF[i]));
            }
            editor.apply();
        }

        super.initWidgetDefaults();
    }

    @Override
    protected void onPrepareReconfigure(int appWidgetId)
    {
        Context context = getActivity();
        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID && context != null)
        {
            String widgetPrefix0 = WidgetSettings.widgetKeyPrefix(0);
            String widgetPrefix = WidgetSettings.widgetKeyPrefix(appWidgetId);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = prefs.edit();

            for (int i = 0; i<DaydreamSettings.VALUES.length; i++) {
                String prefKey = widgetPrefix + DaydreamSettings.VALUES[i];
                String widgetKey = widgetPrefix0 + DaydreamSettings.VALUES[i];
                editor.putInt(widgetKey, prefs.getInt(prefKey, DaydreamSettings.VALUES_DEF[i]));
            }

            for (int i = 0; i<DaydreamSettings.FLAGS.length; i++) {
                String prefKey = widgetPrefix + DaydreamSettings.FLAGS[i];
                String widgetKey = widgetPrefix0 + DaydreamSettings.FLAGS[i];
                editor.putBoolean(widgetKey, prefs.getBoolean(prefKey, DaydreamSettings.FLAGS_DEF[i]));
            }
            editor.apply();
        }

        super.onPrepareReconfigure(appWidgetId);
    }

}
