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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.forrestguice.suntimes.addon.AddonHelper;
import com.forrestguice.suntimes.addon.LocaleHelper;
import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.addon.ui.Messages;
import com.forrestguice.suntimes.naturalhour.ui.AboutDialog;
import com.forrestguice.suntimes.naturalhour.ui.DisplayStrings;
import com.forrestguice.suntimes.naturalhour.ui.HelpDialog;
import com.forrestguice.suntimes.naturalhour.ui.NaturalHourFragment;

import java.lang.reflect.Method;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity
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

    private int getThemeResID(@NonNull String themeName) {
        return themeName.equals(SuntimesInfo.THEME_LIGHT) ? R.style.RomanTimeAppTheme_Light : R.style.RomanTimeAppTheme_Dark;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (suntimesInfo.appTheme != null) {    // override the theme
            setTheme(getThemeResID(suntimesInfo.appTheme));
        }
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_suntimes);
        }

        View timeformatButton = findViewById(R.id.bottombar_button_layout0);
        if (timeformatButton != null) {
            timeformatButton.setOnClickListener(onTimeFormatClick);
        }

        View timezoneButton = findViewById(R.id.bottombar_button_layout1);
        if (timezoneButton != null) {
            timezoneButton.setOnClickListener(onTimeZoneClick);
        }

        if (!SuntimesInfo.checkVersion(this, suntimesInfo))
        {
            View view = getWindow().getDecorView().findViewById(android.R.id.content);
            if (!suntimesInfo.hasPermission)
                Messages.showPermissionDeniedMessage(this, view);
            else Messages.showMissingDependencyMessage(this, view);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d("DEBUG", "onResume");
        String appTheme = SuntimesInfo.queryAppTheme(getContentResolver());
        if (appTheme != null && !appTheme.equals(suntimesInfo.appTheme)) {
            recreate();
        } else {
            suntimesInfo = SuntimesInfo.queryInfo(MainActivity.this);    // refresh suntimesInfo
        }
    }

    @Override
    protected void onResumeFragments()
    {
        super.onResumeFragments();
        Log.d("DEBUG", "onResumeFragments");
        updateViews();
    }

    protected void updateViews()
    {
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle(suntimesInfo.location[0]);
            toolbar.setSubtitle(DisplayStrings.formatLocation(this, suntimesInfo));
        }

        FragmentManager fragments = getSupportFragmentManager();
        NaturalHourFragment fragment = (NaturalHourFragment) fragments.findFragmentById(R.id.romantime_fragment);
        if (fragment != null) {
            fragment.setSuntimesInfo(suntimesInfo, fromTimeZoneMode(getTimeZoneMode()), fromTimeFormatMode(getTimeFormatMode()));
        }

        TextView timeformatText = (TextView) findViewById(R.id.bottombar_button0);
        if (timeformatText != null && fragment != null) {
            timeformatText.setText( getString( fragment.is24() ? R.string.timeformat_24hr : R.string.timeformat_12hr ) );
        }

        TextView timezoneText = (TextView) findViewById(R.id.bottombar_button1);
        if (timezoneText != null && fragment != null) {
            timezoneText.setText( fragment.getTimeZone().getID() );
        }
    }

    private View.OnClickListener onTimeFormatClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showTimeFormatPopup(v);
        }
    };

    private View.OnClickListener onTimeZoneClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showTimeZonePopup(v);
        }
    };

    @SuppressWarnings("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu)
    {
        forceActionBarIcons(menu);
        return super.onPrepareOptionsPanel(view, menu);
    }

    /**
     * from http://stackoverflow.com/questions/18374183/how-to-show-icons-in-overflow-menu-in-actionbar
     */
    public static void forceActionBarIcons(Menu menu)
    {
        if (menu != null)
        {
            if (menu.getClass().getSimpleName().equals("MenuBuilder"))
            {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);

                } catch (Exception e) {
                    Log.e(MainActivity.class.getSimpleName(), "failed to set show overflow icons", e);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                onHomePressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onHomePressed() {
        AddonHelper.startSuntimesActivity(this);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public static final String KEY_MODE_TIMEFORMAT = "timeformatMode";
    public static final int TIMEMODE_SYSTEM = 0, TIMEMODE_SUNTIMES = 1, TIMEMODE_12HR = 2, TIMEMODE_24HR = 3;
    public static final int TIMEMODE_DEFAULT = TIMEMODE_24HR;

    public void showTimeFormatPopup(View v)
    {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_timeformat, popup.getMenu());
        updateTimeFormatPopupMenu(popup.getMenu());
        popup.setOnMenuItemClickListener(onTimeFormatPopupMenuItemSelected);
        popup.show();
    }
    private void updateTimeFormatPopupMenu(Menu menu)
    {
        MenuItem itemSystem = menu.findItem(R.id.action_timeformat_system);
        MenuItem itemSuntimes = menu.findItem(R.id.action_timeformat_suntimes);
        MenuItem[] items = new MenuItem[] {itemSystem, itemSuntimes, menu.findItem(R.id.action_timeformat_12hr), menu.findItem(R.id.action_timeformat_24hr)};

        if (itemSystem != null)
        {
            boolean is24 = fromTimeFormatMode(TIMEMODE_SYSTEM);
            String timeFormat = getString(is24 ? R.string.timeformat_24hr : R.string.timeformat_12hr);
            String displayTag = getString(R.string.action_timeformat_system_format, timeFormat);
            String displayString = getString(R.string.action_timeformat_system, displayTag);
            itemSystem.setTitle(DisplayStrings.createRelativeSpan(null, displayString, displayTag, 0.65f));
        }

        if (itemSuntimes != null)
        {
            boolean is24 = fromTimeFormatMode(TIMEMODE_SUNTIMES);
            String timeFormat = getString(is24 ? R.string.timeformat_24hr : R.string.timeformat_12hr);
            String displayTag = getString(R.string.action_timeformat_system_format, timeFormat);
            String displayString = getString(R.string.action_timeformat_suntimes, displayTag);
            itemSuntimes.setTitle(DisplayStrings.createRelativeSpan(null, displayString, displayTag, 0.65f));
        }

        items[getTimeFormatMode()].setChecked(true);
    }
    private PopupMenu.OnMenuItemClickListener onTimeFormatPopupMenuItemSelected = new PopupMenu.OnMenuItemClickListener()
    {
        @Override
        public boolean onMenuItemClick(MenuItem item)
        {

            switch (item.getItemId())
            {
                case R.id.action_timeformat_system:
                case R.id.action_timeformat_suntimes:
                case R.id.action_timeformat_12hr:
                case R.id.action_timeformat_24hr:
                    item.setChecked(true);
                    SharedPreferences.Editor prefs = getPreferences(Context.MODE_PRIVATE).edit();
                    prefs.putInt(KEY_MODE_TIMEFORMAT, menuItemToTimeFormatMode(item));
                    prefs.apply();
                    updateViews();
                    return true;
            }
            return false;
        }
    };

    public static int menuItemToTimeFormatMode(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_timeformat_suntimes: return TIMEMODE_SUNTIMES;
            case R.id.action_timeformat_12hr: return TIMEMODE_12HR;
            case R.id.action_timeformat_system: return TIMEMODE_SYSTEM;
            case R.id.action_timeformat_24hr: default: return TIMEMODE_24HR;
        }
    }

    /**
     * @param mode TIMEMODE_12HR, TIMEMODE_24HR, TIMEMODE_SUNTIMES, TIMEMODE_SYSTEM
     * @return true 24hr format, false 12hr format
     */
    public boolean fromTimeFormatMode(int mode)
    {
        switch (mode)
        {
            case TIMEMODE_12HR: return false;
            case TIMEMODE_24HR: return true;
            case TIMEMODE_SUNTIMES: return suntimesInfo.getOptions(MainActivity.this).time_is24;
            case TIMEMODE_SYSTEM: default: return android.text.format.DateFormat.is24HourFormat(MainActivity.this);
        }
    }

    public int getTimeFormatMode()
    {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        return prefs.getInt(KEY_MODE_TIMEFORMAT, TIMEMODE_DEFAULT);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public static final String KEY_MODE_TIMEZONE = "timezoneMode";
    public static final int TZMODE_SYSTEM = 0, TZMODE_SUNTIMES = 1, TZMODE_LOCALMEAN = 2, TZMODE_APPARENTSOLAR = 3;
    public static final int TZMODE_DEFAULT = TZMODE_APPARENTSOLAR;

    public void showTimeZonePopup(View v)
    {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_timezone, popup.getMenu());
        updateTimeZonePopupMenu(popup.getMenu());
        popup.setOnMenuItemClickListener(onTimeZonePopupMenuItemSelected);
        popup.show();
    }
    private void updateTimeZonePopupMenu(Menu menu)
    {
        MenuItem itemSystem = menu.findItem(R.id.action_timezone_system);
        MenuItem itemSuntimes = menu.findItem(R.id.action_timezone_suntimes);
        MenuItem[] items = new MenuItem[] {itemSystem, itemSuntimes, menu.findItem(R.id.action_timezone_localmean), menu.findItem(R.id.action_timezone_apparentsolar)};

        if (itemSystem != null) {
            String tzID = getString(R.string.action_timezone_system_format, TimeZone.getDefault().getID());
            String tzString = getString(R.string.action_timezone_system, tzID);
            itemSystem.setTitle(DisplayStrings.createRelativeSpan(null, tzString, tzID, 0.65f));
        }

        if (itemSuntimes != null) {
            String tzID = getString(R.string.action_timezone_system_format, NaturalHourFragment.getTimeZone(MainActivity.this, suntimesInfo).getID());
            String tzString = getString(R.string.action_timezone_suntimes, tzID);
            itemSuntimes.setTitle(DisplayStrings.createRelativeSpan(null, tzString, tzID, 0.65f));
        }

        items[getTimeZoneMode()].setChecked(true);
    }
    private PopupMenu.OnMenuItemClickListener onTimeZonePopupMenuItemSelected = new PopupMenu.OnMenuItemClickListener()
    {
        @Override
        public boolean onMenuItemClick(MenuItem item)
        {

            switch (item.getItemId())
            {
                case R.id.action_timezone_system:
                case R.id.action_timezone_suntimes:
                case R.id.action_timezone_localmean:
                case R.id.action_timezone_apparentsolar:
                    item.setChecked(true);
                    SharedPreferences.Editor prefs = getPreferences(Context.MODE_PRIVATE).edit();
                    prefs.putInt(KEY_MODE_TIMEZONE, menuItemToTimeZoneMode(item));
                    prefs.apply();
                    updateViews();
                    return true;
            }
            return false;
        }
    };
    public static int menuItemToTimeZoneMode(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_timezone_suntimes: return TZMODE_SUNTIMES;
            case R.id.action_timezone_localmean: return TZMODE_LOCALMEAN;
            case R.id.action_timezone_system: return TZMODE_SYSTEM;
            case R.id.action_timezone_apparentsolar: default: return TZMODE_APPARENTSOLAR;
        }
    }

    public TimeZone fromTimeZoneMode(int mode)
    {
        switch (mode)
        {
            case TZMODE_SUNTIMES: return NaturalHourFragment.getTimeZone(MainActivity.this, suntimesInfo);
            case TZMODE_LOCALMEAN: return NaturalHourFragment.getLocalMeanTZ(MainActivity.this, suntimesInfo.location[2]);
            case TZMODE_APPARENTSOLAR: return NaturalHourFragment.getApparantSolarTZ(MainActivity.this, suntimesInfo.location[2]);
            case TZMODE_SYSTEM: default: return TimeZone.getDefault();
        }
    }

    public int getTimeZoneMode()
    {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        return prefs.getInt(KEY_MODE_TIMEZONE, TZMODE_DEFAULT);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    protected void showHelp()
    {
        HelpDialog dialog = new HelpDialog();
        dialog.setTheme(getThemeResID(suntimesInfo.appTheme));

        String[] help = getResources().getStringArray(R.array.help_topics);
        String helpContent = help[0];
        for (int i=1; i<help.length; i++) {
            helpContent = getString(R.string.format_help, helpContent, help[i]);
        }
        dialog.setContent(helpContent + "<br/>");
        dialog.show(getSupportFragmentManager(), DIALOG_HELP);
    }

    protected void showAbout()
    {
        AboutDialog dialog = new AboutDialog();
        dialog.setTheme(getThemeResID(suntimesInfo.appTheme));
        dialog.setVersion(suntimesInfo);
        dialog.show(getSupportFragmentManager(), DIALOG_ABOUT);
    }
}