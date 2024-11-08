// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2020-2024 Forrest Guice
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

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.forrestguice.suntimes.addon.AppThemeInfo;
import com.forrestguice.suntimes.addon.LocaleHelper;
import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.addon.ui.Messages;
import com.forrestguice.suntimes.naturalhour.AppThemes;
import com.forrestguice.suntimes.naturalhour.MainActivity;
import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.ui.AboutDialog;

public abstract class WidgetConfigActivity extends AppCompatActivity
{
    public static final String DIALOG_ABOUT = "aboutDialog";

    public static final String EXTRA_RECONFIGURE = "reconfigure";

    protected SuntimesInfo suntimesInfo = null;
    protected boolean reconfigure = false;

    protected int appWidgetId = getDefaultAppWidgetId();
    private Intent resultValue;

    protected WidgetPreferenceFragment flagFragment;

    public abstract Class getWidgetClass();

    @Override
    protected void attachBaseContext(Context context)
    {
        AppThemeInfo.setFactory(new AppThemes());
        suntimesInfo = SuntimesInfo.queryInfo(context);    // obtain Suntimes version info
        super.attachBaseContext( (suntimesInfo != null && suntimesInfo.appLocale != null) ?    // override the locale
                LocaleHelper.loadLocale(context, suntimesInfo.appLocale) : context );
    }

    public int getDefaultAppWidgetId() {
        return AppWidgetManager.INVALID_APPWIDGET_ID;
    }

    public int getAppWidgetId() {
        return appWidgetId;
    }

    public boolean isReconfiguring() {
        return reconfigure;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (suntimesInfo.appTheme != null) {    // override the theme
            AppThemeInfo.setTheme(this, suntimesInfo);
        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null)
        {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            reconfigure = extras.getBoolean(EXTRA_RECONFIGURE, false);
        }

        resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_CANCELED, resultValue);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
        {
            Log.w("onCreate", "Invalid widget ID! returning early.");
            finish();
            return;
        }

        setContentView(getActivityLayoutResID());
        initViews(this);

        if (!SuntimesInfo.checkVersion(this, suntimesInfo))
        {
            View view = getWindow().getDecorView().findViewById(android.R.id.content);
            if (!suntimesInfo.hasPermission)
                Messages.showPermissionDeniedMessage(this, view);
            else Messages.showMissingDependencyMessage(this, view);
        }
    }

    protected int getActivityLayoutResID() {
        return R.layout.activity_widget;
    }

    protected void initViews(Context context)
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        
        flagFragment = (WidgetPreferenceFragment) getFragmentManager().findFragmentById(R.id.clockFlagsFragment);
        if (flagFragment != null) {
            flagFragment.setSuntimesInfo(suntimesInfo);
            flagFragment.setAppWidgetId(appWidgetId);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        flagFragment = (WidgetPreferenceFragment) getFragmentManager().findFragmentById(R.id.clockFlagsFragment);
        if (flagFragment != null) {
            flagFragment.setSuntimesInfo(suntimesInfo);
        }
    }

    @SuppressWarnings("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu)
    {
        Messages.forceActionBarIcons(menu);
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_widget, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_done:
                return onDone();

            case android.R.id.home:
                return onCanceled();

            case R.id.action_about:
                showAbout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected boolean onDone()
    {
        updateWidgets(this);
        setResult(RESULT_OK, resultValue);
        finish();
        return true;
    }

    protected boolean onCanceled()
    {
        setResult(RESULT_CANCELED, resultValue);
        finish();
        return true;
    }

    protected void updateWidgets(Context context)
    {
        Intent updateIntent = new Intent(context, getWidgetClass());
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] { appWidgetId });
        sendBroadcast(updateIntent);
    }

    protected void showAbout() {
        AboutDialog dialog = MainActivity.createAboutDialog(suntimesInfo);
        dialog.show(getSupportFragmentManager(), DIALOG_ABOUT);
    }
}
