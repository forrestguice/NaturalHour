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
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
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
import android.widget.Toast;

import com.forrestguice.suntimes.addon.AddonHelper;
import com.forrestguice.suntimes.addon.LocaleHelper;
import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.addon.ui.Messages;
import com.forrestguice.suntimes.naturalhour.ui.AboutDialog;
import com.forrestguice.suntimes.naturalhour.ui.DisplayStrings;
import com.forrestguice.suntimes.naturalhour.ui.HelpDialog;
import com.forrestguice.suntimes.naturalhour.ui.NaturalHourFragment;
import com.forrestguice.suntimes.naturalhour.ui.alarms.NaturalHourAlarmFragment;
import com.forrestguice.suntimes.naturalhour.ui.alarms.NaturalHourAlarmSheet;
import com.forrestguice.suntimes.naturalhour.ui.alarms.NaturalHourSelectFragment;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValues;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValuesSheetFragment;

import java.util.TimeZone;

public class MainActivity extends AppCompatActivity
{
    public static final String DIALOG_ALARM = "alarmDialog";
    public static final String DIALOG_HELP = "helpDialog";
    public static final String DIALOG_ABOUT = "aboutDialog";

    private SuntimesInfo suntimesInfo = null;
    private BottomSheetBehavior<View> bottomSheet;
    private ColorValuesSheetFragment sheetDialog;

    @Override
    protected void attachBaseContext(Context context)
    {
        suntimesInfo = SuntimesInfo.queryInfo(context);    // obtain Suntimes version info
        super.attachBaseContext( (suntimesInfo != null && suntimesInfo.appLocale != null) ?    // override the locale
                LocaleHelper.loadLocale(context, suntimesInfo.appLocale) : context );
    }

    public static int getThemeResID(@NonNull String themeName) {
        return themeName.equals(SuntimesInfo.THEME_LIGHT) ? R.style.NaturalHourAppTheme_Light : R.style.NaturalHourAppTheme_Dark;
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

        View bottomSheetView = findViewById(R.id.app_bottomsheet);
        bottomSheet = BottomSheetBehavior.from(bottomSheetView);
        bottomSheet.setHideable(true);
        bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheet.setBottomSheetCallback(bottomSheetCallback);

        sheetDialog = new ColorValuesSheetFragment();
        if (suntimesInfo.appTheme != null) {    // override the theme
            sheetDialog.setTheme(getThemeResID(suntimesInfo.appTheme));
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.app_bottomsheet, sheetDialog).commit();

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
        if (appTheme != null && suntimesInfo != null && suntimesInfo.appTheme != null && !appTheme.equals(suntimesInfo.appTheme)) {
            recreate();
        } else {
            suntimesInfo = SuntimesInfo.queryInfo(MainActivity.this);    // refresh suntimesInfo
        }
    }

    protected void restoreDialogs()
    {
        final FragmentManager fragments = getSupportFragmentManager();
        NaturalHourFragment naturalHour = (NaturalHourFragment) fragments.findFragmentById(R.id.naturalhour_fragment);
        if (sheetDialog != null && naturalHour != null)
        {
            sheetDialog.setColorCollection(naturalHour.getColorCollection());
            sheetDialog.updateViews();
            sheetDialog.setFragmentListener(colorSheetListener);
        }

        NaturalHourAlarmSheet alarmSheet = (NaturalHourAlarmSheet) fragments.findFragmentByTag(DIALOG_ALARM);
        if (alarmSheet != null) {
            alarmSheet.setFragmentListener(onAlarmDialog);
        }
    }

    @Override
    protected void onResumeFragments()
    {
        super.onResumeFragments();
        Log.d("DEBUG", "onResumeFragments");
        updateViews(MainActivity.this);
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

    protected String[] getLocation() {
        return suntimesInfo != null && suntimesInfo.location != null && suntimesInfo.location.length >= 4 ? suntimesInfo.location : new String[] {"", "0", "0", "0"};   // TODO: default/fallback value
    }

    protected void updateViews(Context context)
    {
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle(createTitle(suntimesInfo));
            toolbar.setSubtitle(DisplayStrings.formatLocation(this, suntimesInfo));
        }

        FragmentManager fragments = getSupportFragmentManager();
        NaturalHourFragment fragment = (NaturalHourFragment) fragments.findFragmentById(R.id.naturalhour_fragment);
        if (fragment != null) {
            fragment.setSuntimesInfo(suntimesInfo,
                    AppSettings.fromTimeZoneMode(context, AppSettings.getTimeZoneMode(context), suntimesInfo),
                    AppSettings.fromTimeFormatMode(context, AppSettings.getTimeFormatMode(context), suntimesInfo));
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

    @Override
    public void onSaveInstanceState( Bundle outState )
    {
        super.onSaveInstanceState(outState);
        outState.putInt("bottomSheet", bottomSheet.getState());
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedState)
    {
        super.onRestoreInstanceState(savedState);
        int sheetState = savedState.getInt("bottomSheet", BottomSheetBehavior.STATE_HIDDEN);
        bottomSheet.setState(sheetState);
    }

    protected void showBottomSheet()
    {
        final FragmentManager fragments = getSupportFragmentManager();
        final NaturalHourFragment naturalHour = (NaturalHourFragment) fragments.findFragmentById(R.id.naturalhour_fragment);
        if (sheetDialog != null && naturalHour != null)
        {
            sheetDialog.setMode(ColorValuesSheetFragment.MODE_SELECT);
            sheetDialog.setColorCollection(naturalHour.getColorCollection());
            sheetDialog.updateViews(naturalHour.getClockColors());
            sheetDialog.setFragmentListener(colorSheetListener);
        }
        bottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private ColorValuesSheetFragment.FragmentListener colorSheetListener = new ColorValuesSheetFragment.FragmentListener()
    {
        @Override
        public void requestPeekHeight(int height) {
            bottomSheet.setPeekHeight(height);
        }

        @Override
        public void requestHideSheet() {
            hideBottomSheet();
        }

        @Override
        public void requestExpandSheet() {
            bottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        @Override
        public void onColorValuesSelected(ColorValues values) {
            FragmentManager fragments = getSupportFragmentManager();
            NaturalHourFragment naturalHour = (NaturalHourFragment) fragments.findFragmentById(R.id.naturalhour_fragment);
            if (naturalHour != null) {
                naturalHour.setClockColors(values);
            }
        }

        @Override
        public void onModeChanged(int mode) {
            switch (mode)
            {
                case ColorValuesSheetFragment.MODE_EDIT:
                    bottomSheet.setHideable(false);
                    break;

                case ColorValuesSheetFragment.MODE_SELECT:
                default:
                    bottomSheet.setHideable(true);
                    break;
            }
        }
    };

    protected void hideBottomSheet() {
        bottomSheet.setHideable(true);
        bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    protected boolean isBottomSheetShowing() {
        return bottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED || bottomSheet.getState() == BottomSheetBehavior.STATE_COLLAPSED;
    }

    protected BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback()
    {
        @Override
        public void onStateChanged(@NonNull View view, int newState) {}
        @Override
        public void onSlide(@NonNull View view, float v) {}
    };

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
        Messages.forceActionBarIcons(menu);

        MenuItem alarmItem = menu.findItem(R.id.action_alarms);
        if (alarmItem != null)
        {
            int suntimesAlarms_minVersion = getResources().getInteger(R.integer.min_suntimes_alarms_version_code);
            boolean itemEnabled = (suntimesInfo.appCode >= suntimesAlarms_minVersion);
            alarmItem.setVisible(itemEnabled);
            alarmItem.setEnabled(itemEnabled);
        }

        return super.onPrepareOptionsPanel(view, menu);
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
            case R.id.action_alarms:
                showAlarmDialog();
                return true;

            case R.id.action_colors:
                showBottomSheet();
                return true;

            case R.id.action_settings:
                showSettings();
                return true;

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

    @Override
    public void onBackPressed()
    {
        if (isBottomSheetShowing())
        {
            if (sheetDialog != null) {
                if (sheetDialog.getMode() == ColorValuesSheetFragment.MODE_EDIT) {
                    sheetDialog.cancelEdit(MainActivity.this);
                } else hideBottomSheet();
            } else hideBottomSheet();
        } else super.onBackPressed();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

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
            boolean is24 = AppSettings.fromTimeFormatMode(MainActivity.this, AppSettings.TIMEMODE_SYSTEM, suntimesInfo);
            CharSequence timeFormat = DisplayStrings.timeFormatLabel(MainActivity.this, is24);
            String displayTag = getString(R.string.action_timeformat_system_format, timeFormat);
            String displayString = getString(R.string.action_timeformat_system, displayTag);
            itemSystem.setTitle(DisplayStrings.createRelativeSpan(null, displayString, displayTag, 0.65f));
        }

        if (itemSuntimes != null)
        {
            boolean is24 = AppSettings.fromTimeFormatMode(MainActivity.this, AppSettings.TIMEMODE_SUNTIMES, suntimesInfo);
            CharSequence timeFormat = DisplayStrings.timeFormatLabel(MainActivity.this, is24);
            String displayTag = getString(R.string.action_timeformat_system_format, timeFormat);
            String displayString = getString(R.string.action_timeformat_suntimes, displayTag);
            itemSuntimes.setTitle(DisplayStrings.createRelativeSpan(null, displayString, displayTag, 0.65f));
        }

        items[AppSettings.getTimeFormatMode(MainActivity.this)].setChecked(true);
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
                    AppSettings.setTimeFormatMode(MainActivity.this, menuItemToTimeFormatMode(item));
                    updateViews(MainActivity.this);
                    return true;
            }
            return false;
        }
    };

    public static int menuItemToTimeFormatMode(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_timeformat_suntimes: return AppSettings.TIMEMODE_SUNTIMES;
            case R.id.action_timeformat_12hr: return AppSettings.TIMEMODE_12HR;
            case R.id.action_timeformat_system: return AppSettings.TIMEMODE_SYSTEM;
            case R.id.action_timeformat_24hr: default: return AppSettings.TIMEMODE_24HR;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

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

        items[AppSettings.getTimeZoneMode(MainActivity.this)].setChecked(true);
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
                    AppSettings.setTimeZoneMode(MainActivity.this, menuItemToTimeZoneMode(item));
                    updateViews(MainActivity.this);
                    return true;
            }
            return false;
        }
    };
    public static int menuItemToTimeZoneMode(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_timezone_suntimes: return AppSettings.TZMODE_SUNTIMES;
            case R.id.action_timezone_localmean: return AppSettings.TZMODE_LOCALMEAN;
            case R.id.action_timezone_system: return AppSettings.TZMODE_SYSTEM;
            case R.id.action_timezone_apparentsolar: default: return AppSettings.TZMODE_APPARENTSOLAR;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    protected void showSettings()
    {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    protected void showAlarmDialog()
    {
        int suntimesAlarms_minVersion = getResources().getInteger(R.integer.min_suntimes_alarms_version_code);
        if (suntimesInfo.appCode >= suntimesAlarms_minVersion)
        {
            NaturalHourAlarmSheet dialog = new NaturalHourAlarmSheet();
            if (suntimesInfo != null && suntimesInfo.appTheme != null) {
                dialog.setTheme(getThemeResID(suntimesInfo.appTheme));
            }

            Bundle args = dialog.getArguments() != null ? dialog.getArguments() : new Bundle();
            args.putBoolean(NaturalHourAlarmFragment.ARG_TIME24, AppSettings.fromTimeFormatMode(MainActivity.this, AppSettings.getTimeFormatMode(MainActivity.this), suntimesInfo));
            args.putInt(NaturalHourAlarmFragment.ARG_HOURMODE, AppSettings.getClockIntValue(MainActivity.this, NaturalHourClockBitmap.VALUE_HOURMODE));
            args.putInt(NaturalHourAlarmFragment.ARG_HOUR, 0);    // TODO: save/restore last selection
            dialog.setArguments(args);

            dialog.setLocation(getLocation());
            dialog.setFragmentListener(onAlarmDialog);
            dialog.show(getSupportFragmentManager(), DIALOG_ALARM);

        } else {
            String notSupportedMessage = DisplayStrings.fromHtml(getString(R.string.missing_dependency, getString(R.string.min_suntimes_alarms_version))).toString();
            Toast.makeText(MainActivity.this, notSupportedMessage, Toast.LENGTH_LONG).show();
        }
    }
    private NaturalHourAlarmSheet.FragmentListener onAlarmDialog = new NaturalHourAlarmSheet.FragmentListener() {
        @Override
        public void onAlarmSelected(String alarmID) {}

        @Override
        public void onAddAlarmClicked(String alarmID) {
            NaturalHourAlarmFragment.scheduleAlarm(MainActivity.this, alarmID);
        }
    };

    protected void showHelp()
    {
        HelpDialog dialog = createHelpDialog(this,suntimesInfo, R.array.help_topics);
        dialog.show(getSupportFragmentManager(), DIALOG_HELP);
    }
    public static HelpDialog createHelpDialog(Context context, @Nullable SuntimesInfo suntimesInfo, int helpTopicsArrayRes)
    {
        HelpDialog dialog = new HelpDialog();
        if (suntimesInfo != null && suntimesInfo.appTheme != null) {
            dialog.setTheme(getThemeResID(suntimesInfo.appTheme));
        }

        String[] help = context.getResources().getStringArray(helpTopicsArrayRes);
        String helpContent = help[0];
        for (int i=1; i<help.length; i++) {
            helpContent = context.getString(R.string.format_help, helpContent, help[i]);
        }
        dialog.setContent(helpContent + "<br/>");
        return dialog;
    }

    protected void showAbout() {
        AboutDialog dialog = MainActivity.createAboutDialog(suntimesInfo);
        dialog.show(getSupportFragmentManager(), DIALOG_ABOUT);
    }
    public static AboutDialog createAboutDialog(@Nullable SuntimesInfo suntimesInfo)
    {
        AboutDialog dialog = new AboutDialog();
        if (suntimesInfo != null) {
            dialog.setVersion(suntimesInfo);
            if (suntimesInfo.appTheme != null) {
                dialog.setTheme(getThemeResID(suntimesInfo.appTheme));
            }
        }
        return dialog;
    }
}
