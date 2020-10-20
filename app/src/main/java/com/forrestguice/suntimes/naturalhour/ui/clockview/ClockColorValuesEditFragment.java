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

package com.forrestguice.suntimes.naturalhour.ui.clockview;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.forrestguice.suntimes.addon.AddonHelper;
import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValuesEditFragment;
import com.forrestguice.suntimes.themes.SuntimesThemeContract;
import com.forrestguice.suntimes.themes.ThemeHelper;

public class ClockColorValuesEditFragment extends ColorValuesEditFragment
{
    private SuntimesInfo suntimesInfo = null;
    private SuntimesInfo initSuntimesInfo(Context context) {
        if (suntimesInfo == null) {
            suntimesInfo = SuntimesInfo.queryInfo(context);
        }
        return suntimesInfo;
    }

    @Override
    protected Intent pickColorIntent(String key, int requestCode) {
        return AddonHelper.intentForColorActivity(colorValues.getColor(key), true, colorValues.getColors());
    }

    @Override
    protected void onPickColorResult(String key, Intent data) {
        setColor(key, AddonHelper.resultForColorActivity(data, colorValues.getColor(key)));
    }

    @Override
    protected void onPrepareOverflowMenu(Context context, Menu menu)
    {
        MenuItem copyFromTheme = menu.findItem(R.id.action_colors_copytheme);
        if (copyFromTheme != null) {
            copyFromTheme.setVisible(AddonHelper.supportForThemesActivity(initSuntimesInfo(context)));
        }
    }

    @Override
    protected void importFromTheme(Context context)
    {
        if (AddonHelper.supportForThemesActivity(initSuntimesInfo(context))) {
            super.importFromTheme(context);
        } else {
            Log.w("importFromTheme", "This feature requires Suntimes v0.12.8 (60) or greater.");
        }
    }

    @Override
    protected Intent pickThemeIntent() {
        return AddonHelper.intentForThemesActivity(null);
    }

    @Override
    protected void onPickThemeResult(Intent data)
    {
        Context context = getActivity();
        if (context != null)
        {
            String themeName = AddonHelper.resultForThemesActivity(data);
            if (themeName != null) {
                mapThemeToClockColors(ThemeHelper.loadTheme(getActivity(), themeName));
            }
        }
    }

    protected void mapThemeToClockColors(ContentValues themeValues)
    {
        if (themeValues != null)
        {
            setColor(ClockColorValues.COLOR_PLATE, themeValues.getAsInteger(SuntimesThemeContract.THEME_BACKGROUND_COLOR));
            setColor(ClockColorValues.COLOR_HAND, themeValues.getAsInteger(SuntimesThemeContract.THEME_ACCENTCOLOR));
            setColor(ClockColorValues.COLOR_CENTER, themeValues.getAsInteger(SuntimesThemeContract.THEME_ACCENTCOLOR));
            setColor(ClockColorValues.COLOR_FRAME, themeValues.getAsInteger(SuntimesThemeContract.THEME_TITLECOLOR));
            setColor(ClockColorValues.COLOR_LABEL, themeValues.getAsInteger(SuntimesThemeContract.THEME_TIMECOLOR));
            setColor(ClockColorValues.COLOR_LABEL1, themeValues.getAsInteger(SuntimesThemeContract.THEME_TEXTCOLOR));

            setColor(ClockColorValues.COLOR_FACE, themeValues.getAsInteger(SuntimesThemeContract.THEME_BACKGROUND_COLOR));
            setColor(ClockColorValues.COLOR_FACE_NIGHT, themeValues.getAsInteger(SuntimesThemeContract.THEME_BACKGROUND_COLOR));
            setColor(ClockColorValues.COLOR_FACE_ASTRO, themeValues.getAsInteger(SuntimesThemeContract.THEME_ASTROCOLOR));
            setColor(ClockColorValues.COLOR_FACE_NAUTICAL, themeValues.getAsInteger(SuntimesThemeContract.THEME_NAUTICALCOLOR));
            setColor(ClockColorValues.COLOR_FACE_CIVIL, themeValues.getAsInteger(SuntimesThemeContract.THEME_CIVILCOLOR));
            setColor(ClockColorValues.COLOR_FACE_DAY, themeValues.getAsInteger(SuntimesThemeContract.THEME_DAYCOLOR));
            setColor(ClockColorValues.COLOR_FACE_AM, themeValues.getAsInteger(SuntimesThemeContract.THEME_SUNRISECOLOR));
            setColor(ClockColorValues.COLOR_FACE_PM, themeValues.getAsInteger(SuntimesThemeContract.THEME_SUNSETCOLOR));

            setColor(ClockColorValues.COLOR_RING_DAY, themeValues.getAsInteger(SuntimesThemeContract.THEME_DAYCOLOR));
            setColor(ClockColorValues.COLOR_RING_DAY_LABEL, themeValues.getAsInteger(SuntimesThemeContract.THEME_TITLECOLOR));
            setColor(ClockColorValues.COLOR_RING_DAY_STROKE, themeValues.getAsInteger(SuntimesThemeContract.THEME_TITLECOLOR));
            setColor(ClockColorValues.COLOR_RING_NIGHT, themeValues.getAsInteger(SuntimesThemeContract.THEME_BACKGROUND_COLOR));
            setColor(ClockColorValues.COLOR_RING_NIGHT_LABEL, themeValues.getAsInteger(SuntimesThemeContract.THEME_TITLECOLOR));
            setColor(ClockColorValues.COLOR_RING_NIGHT_STROKE, themeValues.getAsInteger(SuntimesThemeContract.THEME_TITLECOLOR));
        }
    }

}
