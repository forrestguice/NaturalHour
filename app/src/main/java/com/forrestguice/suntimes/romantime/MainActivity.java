// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2020 Forrest Guice
    This file is part of RomanTime.

    RomanTime is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    RomanTime is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with RomanTime.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.forrestguice.suntimes.romantime;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.forrestguice.suntimes.addon.AddonHelper;
import com.forrestguice.suntimes.addon.LocaleHelper;
import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.addon.ui.Messages;
import com.forrestguice.suntimes.romantime.ui.AboutDialog;
import com.forrestguice.suntimes.romantime.ui.DisplayStrings;
import com.forrestguice.suntimes.romantime.ui.HelpDialog;
import com.forrestguice.suntimes.romantime.ui.RomanTimeFragment;

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

        View bottomBarTitleLayout = findViewById(R.id.bottombar_title_layout);
        if (bottomBarTitleLayout != null) {
            bottomBarTitleLayout.setOnClickListener(onBottomBarTitleClick);
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
        RomanTimeFragment fragment = (RomanTimeFragment) fragments.findFragmentById(R.id.romantime_fragment);
        if (fragment != null) {
            fragment.setSuntimesInfo(suntimesInfo, fromTimeZoneMode(getTimeZoneMode()));
        }

        TextView bottomBarTitle = (TextView) findViewById(R.id.bottombar_title);
        if (bottomBarTitle != null && fragment != null) {
            bottomBarTitle.setText( fragment.getTimeZone().getID() );
        }
    }

    private View.OnClickListener onBottomBarTitleClick = new View.OnClickListener() {
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
            String tzID = getString(R.string.action_timezone_system_format, RomanTimeFragment.getTimeZone(MainActivity.this, suntimesInfo).getID());
            String tzString = getString(R.string.action_timezone_suntimes, tzID);
            itemSuntimes.setTitle(DisplayStrings.createRelativeSpan(null, tzString, tzID, 0.65f));
            itemSuntimes.setChecked(true);
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
    public static final String KEY_MODE_TIMEZONE = "timezoneMode";
    public static final int TZMODE_SYSTEM = 0, TZMODE_SUNTIMES = 1, TZMODE_LOCALMEAN = 2, TZMODE_APPARENTSOLAR = 3;

    public static int menuItemToTimeZoneMode(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_timezone_suntimes: return TZMODE_SUNTIMES;
            case R.id.action_timezone_localmean: return TZMODE_LOCALMEAN;
            case R.id.action_timezone_apparentsolar: return TZMODE_APPARENTSOLAR;
            case R.id.action_timezone_system: default: return TZMODE_SYSTEM;
        }
    }

    public TimeZone fromTimeZoneMode(int mode)
    {
        switch (mode)
        {
            case TZMODE_SUNTIMES: return RomanTimeFragment.getTimeZone(MainActivity.this, suntimesInfo);
            case TZMODE_LOCALMEAN: return RomanTimeFragment.getLocalMeanTZ(MainActivity.this, suntimesInfo.location[2]);
            case TZMODE_APPARENTSOLAR: return RomanTimeFragment.getApparantSolarTZ(MainActivity.this, suntimesInfo.location[2]);
            case TZMODE_SYSTEM: default: return TimeZone.getDefault();
        }
    }

    public int getTimeZoneMode()
    {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        return prefs.getInt(KEY_MODE_TIMEZONE, TZMODE_SUNTIMES);
    }

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
