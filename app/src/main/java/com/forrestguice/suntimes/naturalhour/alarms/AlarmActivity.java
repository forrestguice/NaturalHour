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

package com.forrestguice.suntimes.naturalhour.alarms;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.forrestguice.suntimes.addon.LocaleHelper;
import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.addon.ui.Messages;
import com.forrestguice.suntimes.alarm.AlarmHelper;
import com.forrestguice.suntimes.naturalhour.MainActivity;
import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourProvider;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract;
import com.forrestguice.suntimes.naturalhour.ui.AboutDialog;
import com.forrestguice.suntimes.naturalhour.ui.DisplayStrings;
import com.forrestguice.suntimes.naturalhour.ui.HelpDialog;

public class AlarmActivity extends AppCompatActivity
{
    public static final String DIALOG_HELP = "helpDialog";
    public static final String DIALOG_ABOUT = "aboutDialog";

    private SuntimesInfo suntimesInfo = null;

    @Override
    protected void attachBaseContext(Context context)
    {
        suntimesInfo = SuntimesInfo.queryInfo(context);    // obtain Suntimes version info
        super.attachBaseContext( (suntimesInfo != null && suntimesInfo.appLocale != null) ?    // override the locale
                LocaleHelper.loadLocale(context, suntimesInfo.appLocale) : context );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (suntimesInfo.appTheme != null) {    // override the theme
            setTheme(MainActivity.getThemeResID(suntimesInfo.appTheme));
        }
        setContentView(R.layout.activity_alarm);
        setResult(RESULT_CANCELED);

        Intent intent = getIntent();
        if (intent.hasExtra(NaturalHourProviderContract.EXTRA_ALARM_EVENT))
        {
            String alarmUriString = intent.getStringExtra(NaturalHourProviderContract.EXTRA_ALARM_EVENT);
            if (alarmUriString != null)
            {
                Uri alarmUri = Uri.parse(alarmUriString);
                String alarmID = alarmUri.getLastPathSegment();
                int[] naturalHour = NaturalHourProvider.alarmIdToNaturalHour(alarmID);
                if (naturalHour != null) {
                    // TODO: loadUserInput(alarmID, false);
                }
            }
        }

        initToolbar();

        /*View timeformatButton = findViewById(R.id.bottombar_button_layout0);
        if (timeformatButton != null) {
            timeformatButton.setOnClickListener(onTimeFormatClick);
        }*/
        /*View timezoneButton = findViewById(R.id.bottombar_button_layout1);
        if (timezoneButton != null) {
            //timezoneButton.setOnClickListener(onTimeZoneClick);
        }*/

        if (!SuntimesInfo.checkVersion(this, suntimesInfo))
        {
            View view = getWindow().getDecorView().findViewById(android.R.id.content);
            if (!suntimesInfo.hasPermission)
                Messages.showPermissionDeniedMessage(this, view);
            else Messages.showMissingDependencyMessage(this, view);
        }
    }

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
        }
    }

    protected void restoreDialogs()
    {
        final FragmentManager fragments = getSupportFragmentManager();
        /*NaturalHourFragment naturalHour = (NaturalHourFragment) fragments.findFragmentById(R.id.naturalhour_fragment);
        if (sheetDialog != null && naturalHour != null)
        {
            sheetDialog.setColorCollection(naturalHour.getColorCollection());
            sheetDialog.updateViews();
            sheetDialog.setFragmentListener(colorSheetListener);
        }*/
    }

    @Override
    protected void onResumeFragments()
    {
        super.onResumeFragments();
        Log.d("DEBUG", "onResumeFragments");
        updateViews(AlarmActivity.this);
        restoreDialogs();
    }

    protected CharSequence createTitle(SuntimesInfo info) {
        return (suntimesInfo != null && suntimesInfo.location != null && suntimesInfo.location.length >= 4)
                ? suntimesInfo.location[0]
                : getString(R.string.app_name);
    }

    protected CharSequence createSubTitle(SuntimesInfo info) {
        return (suntimesInfo != null) ? DisplayStrings.formatLocation(this, suntimesInfo) : "";
    }

    protected void updateViews(Context context)
    {
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle(createTitle(suntimesInfo));
            toolbar.setSubtitle(DisplayStrings.formatLocation(this, suntimesInfo));
        }

        //FragmentManager fragments = getSupportFragmentManager();
        //NaturalHourFragment fragment = (NaturalHourFragment) fragments.findFragmentById(R.id.naturalhour_fragment);
        //if (fragment != null) {
        //    fragment.setSuntimesInfo(suntimesInfo,
        //            AppSettings.fromTimeZoneMode(context, AppSettings.getTimeZoneMode(context), suntimesInfo),
        //            AppSettings.fromTimeFormatMode(context, AppSettings.getTimeFormatMode(context), suntimesInfo));
        //}

        TextView timeformatText = (TextView) findViewById(R.id.bottombar_button0);   // TODO
        /*if (timeformatText != null && fragment != null) {
            timeformatText.setText( getString( fragment.is24() ? R.string.timeformat_24hr : R.string.timeformat_12hr ) );
        }*/

        TextView timezoneText = (TextView) findViewById(R.id.bottombar_button1);   // TODO
        /*if (timezoneText != null && fragment != null) {
            timezoneText.setText( fragment.getTimeZone().getID() );
        }*/
    }

    /*@Override
    public void onSaveInstanceState( Bundle outState )
    {
        super.onSaveInstanceState(outState);
    }*/

    /*@Override
    public void onRestoreInstanceState(@NonNull Bundle savedState)
    {
        super.onRestoreInstanceState(savedState);
    }*/

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
    }*/

    @SuppressWarnings("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu)
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
        Intent result = new Intent();    // e.g. content://suntimes.naturalhour.provider/alarmInfo/0_6_0    .. hourMode:0, hour:6(noon), moment:0
        result.putExtra(NaturalHourProviderContract.COLUMN_CONFIG_PROVIDER, NaturalHourProviderContract.AUTHORITY);
        result.putExtra(NaturalHourProviderContract.COLUMN_ALARM_NAME, alarmID);
        result.putExtra(NaturalHourProviderContract.COLUMN_ALARM_TITLE, NaturalHourProvider.getAlarmTitle(this, alarmID));
        result.putExtra(NaturalHourProviderContract.COLUMN_ALARM_SUMMARY, NaturalHourProvider.getAlarmSummary(this, alarmID));
        result.setData(Uri.parse(AlarmHelper.getAlarmInfoUri(NaturalHourProviderContract.AUTHORITY, alarmID)));
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    protected void showHelp()
    {
        HelpDialog dialog = MainActivity.createHelpDialog(this,suntimesInfo, R.array.help_topics);
        dialog.show(getSupportFragmentManager(), DIALOG_HELP);
    }

    protected void showAbout() {
        AboutDialog dialog = MainActivity.createAboutDialog(suntimesInfo);
        dialog.show(getSupportFragmentManager(), DIALOG_ABOUT);
    }
}
