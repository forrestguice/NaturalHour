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

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import com.forrestguice.suntimes.addon.AppThemeInfo;
import com.forrestguice.suntimes.addon.LocaleHelper;
import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.naturalhour.ui.DisplayStrings;
import com.forrestguice.suntimes.naturalhour.ui.IntListPreference;
import com.forrestguice.suntimes.naturalhour.ui.NaturalHourFragment;
import com.forrestguice.suntimes.naturalhour.ui.Toast;
import com.forrestguice.suntimes.naturalhour.ui.clockview.ClockColorValuesCollection;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValues;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValuesCollection;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValuesCollectionPreference;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValuesSheetActivity;

import java.util.TimeZone;

public class SettingsActivity extends AppCompatActivity
{
    private SuntimesInfo suntimesInfo = null;

    @Override
    protected void attachBaseContext(Context context)
    {
        AppThemeInfo.setFactory(new AppThemes());
        suntimesInfo = SuntimesInfo.queryInfo(context);    // obtain Suntimes version info
        super.attachBaseContext( (suntimesInfo != null && suntimesInfo.appLocale != null) ?    // override the locale
                LocaleHelper.loadLocale(context, suntimesInfo.appLocale) : context );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (suntimesInfo.appTheme != null) {    // override the theme
            AppThemeInfo.setTheme(this, suntimesInfo);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getFragmentManager().beginTransaction().replace(android.R.id.content, new NaturalHourPreferenceFragment(), NaturalHourPreferenceFragment.TAG).commit();
        AppSettings.sanityCheck(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        FragmentManager fragments = getFragmentManager();
        NaturalHourPreferenceFragment fragment = (NaturalHourPreferenceFragment) fragments.findFragmentByTag(NaturalHourPreferenceFragment.TAG);
        if (fragment != null) {
            fragment.setSuntimesInfo(suntimesInfo);
        }
    }

    /**
     * NaturalHourPreferenceFragment
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NaturalHourPreferenceFragment extends PreferenceFragment
    {
        public static final String TAG = "naturalHourFragment";

        public static final int REQUEST_PICKCOLORS_LIGHT = 100;
        public static final int REQUEST_PICKCOLORS_DARK = 101;

        public static final String KEY_COLORS_LIGHT = "colors_light";
        public static final String KEY_COLORS_DARK = "colors_dark";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            initColors();
        }

        protected void initColors()
        {
            Context context = getActivity();

            if (context != null)
            {
                final ColorValuesCollectionPreference lightColorsPref = (ColorValuesCollectionPreference) findPreference(KEY_COLORS_LIGHT);
                if (lightColorsPref != null) {
                    lightColorsPref.setAppWidgetID(0);
                    lightColorsPref.setCollection(context, new ClockColorValuesCollection<ColorValues>(context));
                    lightColorsPref.initPreferenceOnClickListener(this, REQUEST_PICKCOLORS_LIGHT);
                }

                final ColorValuesCollectionPreference darkColorsPref = (ColorValuesCollectionPreference) findPreference(KEY_COLORS_DARK);
                if (darkColorsPref != null) {
                    darkColorsPref.setAppWidgetID(-1);
                    darkColorsPref.setCollection(context, new ClockColorValuesCollection<ColorValues>(context));
                    darkColorsPref.initPreferenceOnClickListener(this, REQUEST_PICKCOLORS_DARK);
                }
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data)
        {
            Log.d("DEBUG", "onActivityResult: " + requestCode);
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode)
            {
                case REQUEST_PICKCOLORS_LIGHT:
                case REQUEST_PICKCOLORS_DARK:
                    onPickColors(requestCode, resultCode, data);
                    break;
            }
        }

        private void onPickColors(int requestCode, int resultCode, Intent data)
        {
            if (resultCode == RESULT_OK)
            {
                String selection = data.getStringExtra(ColorValuesSheetActivity.EXTRA_SELECTED_COLORS_ID);
                int appWidgetID = data.getIntExtra(ColorValuesSheetActivity.EXTRA_APPWIDGET_ID, 0);
                String colorTag = data.getStringExtra(ColorValuesSheetActivity.EXTRA_COLORTAG);
                ColorValuesCollection<ColorValues> collection = data.getParcelableExtra(ColorValuesSheetActivity.EXTRA_COLLECTION);
                //Log.d("DEBUG", "onPickColors: " + selection);

                if (collection != null) {
                    collection.setSelectedColorsID(getActivity(), selection, appWidgetID, colorTag);
                    Log.d("DEBUG", "onPickColors: colorTag: " + colorTag);
                }

                String prefKey = (requestCode == REQUEST_PICKCOLORS_DARK) ? KEY_COLORS_DARK : KEY_COLORS_LIGHT;
                final ColorValuesCollectionPreference colorsPref = (ColorValuesCollectionPreference) findPreference(prefKey);
                if (colorsPref != null) {
                    colorsPref.setCollection(getActivity(), collection);
                }
                Toast.makeText(getActivity(), getString(R.string.restart_required_message), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onStart()
        {
            super.onStart();
            PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(onChangedNeedsRestart);
        }

        @Override
        public void onStop()
        {
            PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(onChangedNeedsRestart);
            super.onStop();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item)
        {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        protected void updateDynamicPrefs(Context context, @NonNull SuntimesInfo info)
        {
            updateTimeZonePref(context, (IntListPreference) findPreference("timezonemode"), info);
            updateTimeModePref(context, (IntListPreference) findPreference("timeformatmode"), info);
        }
        public static void updateTimeZonePref(Context context, IntListPreference pref, @NonNull SuntimesInfo info)
        {
            if (pref != null)
            {
                CharSequence[] entries = pref.getEntries();
                entries[0] = DisplayStrings.formatTimeZoneLabel(context, entries[0].toString(), TimeZone.getDefault().getID());
                entries[1] = DisplayStrings.formatTimeZoneLabel(context, entries[1].toString(), NaturalHourFragment.getTimeZone(context, info).getID());
            }
        }
        public static void updateTimeModePref(Context context, IntListPreference pref, @NonNull SuntimesInfo info)
        {
            if (pref != null)
            {
                CharSequence[] entries = pref.getEntries();
                entries[0] = DisplayStrings.formatTimeFormatLabel(context, entries[0].toString(), AppSettings.fromTimeFormatMode(context, AppSettings.TIMEMODE_SYSTEM, info));
                entries[1] = DisplayStrings.formatTimeFormatLabel(context, entries[1].toString(), AppSettings.fromTimeFormatMode(context, AppSettings.TIMEMODE_SUNTIMES, info));
            }
        }

        private final SharedPreferences.OnSharedPreferenceChangeListener onChangedNeedsRestart = new SharedPreferences.OnSharedPreferenceChangeListener()
        {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
            {
                if (key.equals(AppSettings.KEY_MODE_BACKGROUND)) {
                    Toast.makeText(getActivity(), getString(R.string.restart_required_message), Toast.LENGTH_SHORT).show();
                }
            }
        };

        protected SuntimesInfo info;
        public void setSuntimesInfo(SuntimesInfo info) {
            this.info = info;
            if (this.info != null) {
                updateDynamicPrefs(getActivity(), this.info);
            }
        }
    }

}
