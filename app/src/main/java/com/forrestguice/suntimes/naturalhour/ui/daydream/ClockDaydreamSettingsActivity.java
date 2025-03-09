/**
    Copyright (C) 2024 Forrest Guice
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.addon.ui.Messages;
import com.forrestguice.suntimes.naturalhour.MainActivity;
import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.ui.AboutDialog;
import com.forrestguice.suntimes.naturalhour.ui.widget.WidgetPreferenceFragment;

public class ClockDaydreamSettingsActivity extends AppCompatActivity
{
    public static final String DIALOG_ABOUT = "aboutDialog";

    protected SuntimesInfo info;
    protected int appWidgetId = ClockDaydreamService.APPWIDGET_ID;
    protected DaydreamPreferenceFragment flagFragment;

    public ClockDaydreamSettingsActivity() {
        super();
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(newBase );
        info = SuntimesInfo.queryInfo(newBase);
    }

    @Override
    public void onCreate(Bundle savedState)
    {
        super.onCreate(savedState);
        setContentView(R.layout.activity_daydream_config);
        initViews();
        handleIntent(getIntent());

        if (!SuntimesInfo.checkVersion(this, info))
        {
            View view = getWindow().getDecorView().findViewById(android.R.id.content);
            if (!info.hasPermission)
                Messages.showPermissionDeniedMessage(this, view);
            else Messages.showMissingDependencyMessage(this, view);
        }
    }

    protected void initViews()
    {
        Toolbar menuBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(menuBar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        flagFragment = (DaydreamPreferenceFragment) getFragmentManager().findFragmentById(R.id.clockFlagsFragment);
        if (flagFragment != null) {
            flagFragment.setSuntimesInfo(info);
            flagFragment.setAppWidgetId(appWidgetId, false);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        WidgetPreferenceFragment flagFragment = (WidgetPreferenceFragment) getFragmentManager().findFragmentById(R.id.clockFlagsFragment);
        if (flagFragment != null) {
            flagFragment.setSuntimesInfo(info);
        }
        updateViews(this);
    }

    @Override
    public void onNewIntent( Intent intent )
    {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    protected void handleIntent(Intent intent) {}

    protected void updateViews(Context context) {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_daydream, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_about:
                showAbout(this);
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu)
    {
        Messages.forceActionBarIcons(menu);
        return super.onPrepareOptionsPanel(view, menu);
    }

    public void showAbout(@NonNull Activity activity)
    {
        AboutDialog dialog = MainActivity.createAboutDialog(info);
        dialog.show(getSupportFragmentManager(), DIALOG_ABOUT);
    }
}
