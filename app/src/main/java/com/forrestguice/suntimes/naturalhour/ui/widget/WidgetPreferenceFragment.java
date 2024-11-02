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

package com.forrestguice.suntimes.naturalhour.ui.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;

import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.naturalhour.AppSettings;
import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.SettingsActivity;
import com.forrestguice.suntimes.naturalhour.ui.IntListPreference;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class WidgetPreferenceFragment extends PreferenceFragment
{
    public static final String KEY_PREFIX = "widget";
    public static String widgetKeyPrefix(int appWidgetId) {
        return KEY_PREFIX + "_" + appWidgetId + "_";
    }

    /**
     * @return appWidgetId (apply as widget settings), or 0 if unset (apply as global settings)
     */
    public int getAppWidgetId() {
        return getArguments().getInt("appWidgetId");
    }
    public void setAppWidgetId(int appWidgetId) {
        getArguments().putInt("appWidgetId", appWidgetId);
        initWidgetDefaults();
    }

    public WidgetPreferenceFragment()
    {
        super();
        setArguments(new Bundle());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(getPreferenceResources());
        initWidgetDefaults();
        setHasOptionsMenu(false);
    }

    protected int getPreferenceResources() {
        return R.xml.pref_widget;
    }

    protected void initWidgetDefaults()
    {
        Context context = getActivity();
        int appWidgetId = getAppWidgetId();
        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID && context != null)
        {
            String widgetPrefix0 = widgetKeyPrefix(0);
            String widgetPrefix = widgetKeyPrefix(appWidgetId);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = prefs.edit();
            for (String key : NaturalHourClockBitmap.FLAGS) {                     // copy flags from widget_0 to widget_i
                String prefKey = widgetPrefix0 + key;
                String widgetKey = widgetPrefix + key;
                editor.putBoolean(widgetKey, prefs.getBoolean(prefKey, NaturalHourClockBitmap.getDefaultFlag(context, key)));
            }
            for (String key : NaturalHourClockBitmap.VALUES) {                    // copy values from widget_0 to widget_i
                String prefKey = widgetPrefix0 + key;
                String widgetKey = widgetPrefix + key;
                editor.putInt(widgetKey, prefs.getInt(prefKey, NaturalHourClockBitmap.getDefaultValue(context, key)));
            }
            for (int i = 0; i<AppSettings.VALUES.length; i++) {
                String prefKey = widgetPrefix0 + AppSettings.VALUES[i];
                String widgetKey = widgetPrefix + AppSettings.VALUES[i];
                editor.putInt(widgetKey, prefs.getInt(prefKey, AppSettings.VALUES_DEF[i]));
            }

            editor.apply();
        }
    }

    protected void updateDynamicPrefs(Context context, @NonNull SuntimesInfo info)
    {
        SettingsActivity.NaturalHourPreferenceFragment.updateTimeZonePref(context, (IntListPreference) findPreference("widget_0_timezonemode"), info);
        SettingsActivity.NaturalHourPreferenceFragment.updateTimeModePref(context, (IntListPreference) findPreference("widget_0_timeformatmode"), info);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(onWidgetPrefChanged);
    }

    @Override
    public void onPause()
    {
        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(onWidgetPrefChanged);
        super.onPause();
    }

    private SharedPreferences.OnSharedPreferenceChangeListener onWidgetPrefChanged = new SharedPreferences.OnSharedPreferenceChangeListener()
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

                    Context context = getActivity();
                    SharedPreferences.Editor editor = prefs.edit();

                    for (String boolPref : NaturalHourClockBitmap.FLAGS)
                    {
                        if (boolPref.equals(key)) {
                            editor.putBoolean(widgetKey, prefs.getBoolean(prefKey, NaturalHourClockBitmap.getDefaultFlag(context, key)));
                            editor.apply();
                            return;
                        }
                    }
                    for (String intPref : NaturalHourClockBitmap.VALUES)
                    {
                        if (intPref.equals(key)) {
                            editor.putInt(widgetKey, prefs.getInt(prefKey, NaturalHourClockBitmap.getDefaultValue(context, key)));
                            editor.apply();
                            return;
                        }
                    }
                    for (int i = 0; i<AppSettings.VALUES.length; i++)
                    {
                        if (AppSettings.VALUES[i].equals(key)) {
                            editor.putInt(widgetKey, prefs.getInt(prefKey, AppSettings.VALUES_DEF[i]));
                            editor.apply();
                            return;
                        }
                    }
                }

            } else Log.e("onWidgetPrefChanged", "AppWidgetID is unset! ignoring change to " + prefKey);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    protected SuntimesInfo info;
    public void setSuntimesInfo(SuntimesInfo info) {
        this.info = info;
        if (this.info != null) {
            updateDynamicPrefs(getActivity(), this.info);
        }
    }
}
