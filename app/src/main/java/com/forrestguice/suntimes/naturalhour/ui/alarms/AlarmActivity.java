// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2021-2023 Forrest Guice
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

package com.forrestguice.suntimes.naturalhour.ui.alarms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.forrestguice.suntimes.addon.AppThemeInfo;
import com.forrestguice.suntimes.addon.LocaleHelper;
import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.addon.ui.Messages;
import com.forrestguice.suntimes.alarm.AlarmHelper;
import com.forrestguice.suntimes.naturalhour.AppSettings;
import com.forrestguice.suntimes.naturalhour.AppThemes;
import com.forrestguice.suntimes.naturalhour.MainActivity;
import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourProvider;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract;
import com.forrestguice.suntimes.naturalhour.data.alarms.NaturalHourAlarmType;
import com.forrestguice.suntimes.naturalhour.ui.AboutDialog;
import com.forrestguice.suntimes.naturalhour.ui.DisplayStrings;
import com.forrestguice.suntimes.naturalhour.ui.HelpDialog;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;

import java.util.TimeZone;

import static com.forrestguice.suntimes.alarm.AlarmEventContract.EXTRA_LOCATION_ALT;
import static com.forrestguice.suntimes.alarm.AlarmEventContract.EXTRA_LOCATION_LAT;
import static com.forrestguice.suntimes.alarm.AlarmEventContract.EXTRA_LOCATION_LON;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.EXTRA_LOCATION_LABEL;

public class AlarmActivity extends AppCompatActivity
{
    public static final String DIALOG_HELP = "helpDialog";
    public static final String DIALOG_ABOUT = "aboutDialog";

    protected SuntimesInfo suntimesInfo = null;
    protected String param_location;
    protected double param_latitude = 0, param_longitude = 0, param_altitude = 0;

    protected ActionMode actionMode = null;
    protected AlarmActionsCompat alarmActions;
    protected NaturalHourAlarmFragment fragment;

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
        setContentView(R.layout.activity_alarm);
        setResult(RESULT_CANCELED);

        Intent intent = getIntent();
        String param_alarmID = null;
        if (intent.hasExtra(NaturalHourProviderContract.EXTRA_ALARM_EVENT))
        {
            String alarmUriString = intent.getStringExtra(NaturalHourProviderContract.EXTRA_ALARM_EVENT);
            if (alarmUriString != null) {
                param_alarmID = Uri.parse(alarmUriString).getLastPathSegment();
            }
            intent.removeExtra(NaturalHourProviderContract.EXTRA_ALARM_EVENT);
        }

        initToolbar();
        alarmActions = new AlarmActionsCompat();

        FragmentManager fragments = getSupportFragmentManager();
        fragment = (NaturalHourAlarmFragment) fragments.findFragmentById(R.id.naturalhouralarm_fragment);
        if (fragment != null)
        {
            fragment.setHourMode(AppSettings.getClockIntValue(AlarmActivity.this, NaturalHourClockBitmap.VALUE_HOURMODE));
            if (param_alarmID != null) {
                fragment.setAlarmID(param_alarmID);    // overrides hourMode
            }
            fragment.setIs24(AppSettings.fromTimeFormatMode(AlarmActivity.this, AppSettings.getTimeFormatMode(AlarmActivity.this), suntimesInfo) == 24);   // TODO: timeformat
            fragment.setLocation(param_latitude, param_longitude, param_altitude);
            fragment.addFragmentListener(onAlarmSelectionChanged);
            triggerActionMode(fragment.getView(), fragment.getAlarmID());
        }

        if (!SuntimesInfo.checkVersion(this, suntimesInfo))
        {
            View view = getWindow().getDecorView().findViewById(android.R.id.content);
            if (!suntimesInfo.hasPermission)
                Messages.showPermissionDeniedMessage(this, view);
            else Messages.showMissingDependencyMessage(this, view);
        }
    }

    protected void initLocation(Intent intent)
    {
        intent.setExtrasClassLoader(getClassLoader());
        if (intent.hasExtra(EXTRA_LOCATION_LAT) && intent.hasExtra(EXTRA_LOCATION_LON))
        {
            double latitude = intent.getDoubleExtra(EXTRA_LOCATION_LAT, -1000);
            double longitude = intent.getDoubleExtra(EXTRA_LOCATION_LON, -1000);
            if ((latitude >= -90 && latitude <= 90) && (longitude >= -180 && longitude <= 180))
            {
                String labelString = intent.getStringExtra(EXTRA_LOCATION_LABEL);
                param_location = labelString != null ? labelString : "";
                param_latitude = latitude;
                param_longitude = longitude;
                param_altitude = intent.getDoubleExtra(EXTRA_LOCATION_ALT, 0);

                if (fragment != null) {
                    fragment.setLocation(param_latitude, param_longitude, param_altitude);
                }

            } else {
                initLocation(suntimesInfo);
            }
        } else {
            initLocation(suntimesInfo);
        }
    }
    protected void initLocation(@Nullable SuntimesInfo info)
    {
        if (info != null && info.location != null)
        {
            param_location = info.location[0];
            param_latitude = Double.parseDouble(info.location[1]);
            param_longitude = Double.parseDouble(info.location[2]);
            param_altitude = Double.parseDouble(info.location[3]);

            if (fragment != null) {
                fragment.setLocation(param_latitude, param_longitude, param_altitude);
            }
        }
    }

    private final NaturalHourAlarmFragment.FragmentListener onAlarmSelectionChanged = new NaturalHourAlarmFragment.FragmentListener()
    {
        @Override
        public void onAlarmSelected(String alarmID)
        {
            Log.d("DEBUG", "on item selected: " + alarmID);
            updateViews(AlarmActivity.this);
            triggerActionMode(null, alarmID);
        }
    };

    protected void initToolbar()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("DEBUG", "onResume");
        String appTheme = SuntimesInfo.queryAppTheme(getContentResolver());
        if (appTheme != null && suntimesInfo != null && suntimesInfo.appTheme != null && !appTheme.equals(suntimesInfo.appTheme)) {
            recreate();
        } else {
            suntimesInfo = SuntimesInfo.queryInfo(AlarmActivity.this);    // refresh suntimesInfo
            initLocation(getIntent());
        }
    }

    protected void restoreDialogs()
    {
        final FragmentManager fragments = getSupportFragmentManager();
        NaturalHourAlarmFragment fragment = (NaturalHourAlarmFragment) fragments.findFragmentById(R.id.naturalhouralarm_fragment);
        if (fragment != null) {
            fragment.updateViews(AlarmActivity.this);
            fragment.addFragmentListener(onAlarmSelectionChanged);
        }

        HelpDialog helpDialog = (HelpDialog) fragments.findFragmentByTag(DIALOG_HELP);
        if (helpDialog != null) {
            helpDialog.setNeutralButtonListener(HelpDialog.getOnlineHelpClickListener(this, HELP_PATH_ID), DIALOG_HELP);
        }
    }

    @Override
    protected void onResumeFragments()
    {
        super.onResumeFragments();
        Log.d("DEBUG", "onResumeFragments");
        updateViews(AlarmActivity.this);
        restoreDialogs();
    }

    protected static CharSequence createTitle(Context context, SuntimesInfo info) {
        return (info != null && info.location != null && info.location.length >= 4)
                ? info.location[0]
                : context.getString(R.string.app_name);
    }

    protected static CharSequence createSubTitle(Context context, SuntimesInfo info) {
        return (info != null) ? DisplayStrings.formatLocation(context, info) : "";
    }

    protected void updateViews(Context context)
    {
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null)
        {
            toolbar.setTitle(param_location != null ? param_location : createTitle(context, suntimesInfo));
            toolbar.setSubtitle(DisplayStrings.formatLocation(this, param_latitude, param_longitude, param_altitude, 4, suntimesInfo.getOptions(this).length_units));
        }

        boolean is24 = AppSettings.fromTimeFormatMode(context, AppSettings.getTimeFormatMode(context), suntimesInfo) == NaturalHourClockBitmap.TIMEFORMAT_24;   // TODO: timeformat
        TextView timeformatText = (TextView) findViewById(R.id.bottombar_button0);
        if (timeformatText != null) {
            timeformatText.setText( is24 ? R.string.timeformat_24hr : R.string.timeformat_12hr );
            timeformatText.setVisibility(View.GONE);
        }

        TimeZone timezone = TimeZone.getDefault();
        TextView timezoneText = (TextView) findViewById(R.id.bottombar_button1);
        if (timezoneText != null) {
            timezoneText.setText( timezone.getID() );
        }
    }

    @SuppressWarnings("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, @NonNull Menu menu)
    {
        Messages.forceActionBarIcons(menu);
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_help:
                showHelp();
                return true;

            case R.id.action_about:
                showAbout();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onDone(String alarmID)
    {
        NaturalHourAlarmType alarmInfo = NaturalHourProvider.getAlarmInfo(alarmID);
        Intent result = new Intent();    // e.g. content://suntimes.naturalhour.provider/alarmInfo/0_6_0    .. hourMode:0, hour:6(noon), moment:0
        result.putExtra(NaturalHourProviderContract.COLUMN_CONFIG_PROVIDER, NaturalHourProvider.getAuthority());
        result.putExtra(NaturalHourProviderContract.COLUMN_EVENT_NAME, alarmID);
        result.putExtra(NaturalHourProviderContract.COLUMN_EVENT_TITLE, alarmInfo.getAlarmTitle(this, alarmID));
        result.putExtra(NaturalHourProviderContract.COLUMN_EVENT_SUMMARY, alarmInfo.getAlarmSummary(this, alarmID));
        result.setData(Uri.parse(AlarmHelper.getEventInfoUri(NaturalHourProvider.getAuthority(), alarmID)));
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    protected boolean triggerActionMode(View view, String alarmID)
    {
        if (actionMode == null)
        {
            if (alarmID != null) {
                onTriggerActionMode(alarmID);
            }
            return true;

        } else {
            actionMode.finish();
            triggerActionMode(view, alarmID);
            return false;
        }
    }

    protected void onTriggerActionMode(@NonNull String alarmID)
    {
        alarmActions.setSelection(alarmID);
        actionMode = startSupportActionMode(alarmActions);
        if (actionMode != null) {
            actionMode.setTitle(NaturalHourProvider.getAlarmInfo(alarmID).getAlarmTitle(this, alarmID));
        }
    }

    /**
     * AlarmActionsCompat
     */
    private class AlarmActionsCompat implements androidx.appcompat.view.ActionMode.Callback
    {
        protected String alarmID = null;
        public void setSelection(String alarmID ) {
            this.alarmID = alarmID;
        }

        @Override
        public boolean onCreateActionMode(androidx.appcompat.view.ActionMode mode, Menu menu)
        {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_alarm1, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(androidx.appcompat.view.ActionMode mode, Menu menu)
        {
            Messages.forceActionBarIcons(menu);
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }

        @Override
        public boolean onActionItemClicked(androidx.appcompat.view.ActionMode mode, MenuItem item)
        {
            if (alarmID != null)
            {
                switch (item.getItemId())
                {
                    case R.id.action_select:
                        onDone(alarmID);
                        mode.finish();
                        return true;
                }
            }
            mode.finish();
            return false;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private static final int HELP_PATH_ID = R.string.help_alarms_path;

    protected void showHelp()
    {
        HelpDialog dialog = MainActivity.createHelpDialog(this, suntimesInfo, R.array.help_topics, MainActivity.DIALOG_HELP, HELP_PATH_ID);
        dialog.show(getSupportFragmentManager(), DIALOG_HELP);
    }

    protected void showAbout() {
        AboutDialog dialog = MainActivity.createAboutDialog(suntimesInfo);
        dialog.show(getSupportFragmentManager(), DIALOG_ABOUT);
    }
}
