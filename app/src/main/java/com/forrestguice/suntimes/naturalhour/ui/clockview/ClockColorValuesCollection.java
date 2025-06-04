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

package com.forrestguice.suntimes.naturalhour.ui.clockview;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValues;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValuesCollection;

/**
 * ColorValuesCollection
 */
public class ClockColorValuesCollection<T> extends ColorValuesCollection<ColorValues>
{
    public static final String PREFS_CLOCKCOLORS = "prefs_clockcolors";

    public ClockColorValuesCollection() {
        super();
    }
    public ClockColorValuesCollection(Context context) {
        super(context);
    }
    protected ClockColorValuesCollection(Parcel in) {
        super(in);
    }

    public static final Creator<ClockColorValuesCollection> CREATOR = new Creator<ClockColorValuesCollection>()
    {
        public ClockColorValuesCollection createFromParcel(Parcel in) {
            return new ClockColorValuesCollection<ColorValues>(in);
        }
        public ClockColorValuesCollection<ColorValues>[] newArray(int size) {
            return new ClockColorValuesCollection[size];
        }
    };

    @Override
    @NonNull
    protected String getSharedPrefsPrefix() {
        return "";
    }

    @NonNull
    protected String getCollectionSharedPrefsPrefix() {
        return "";
    }

    @Override
    public String getSharedPrefsName() {
        return PREFS_CLOCKCOLORS;
    }

    @Nullable
    @Override
    public String getCollectionSharedPrefsName() {
        return PREFS_CLOCKCOLORS;
    }

    @Override
    protected String[] getDefaultColorIDs() {
        return new String[] { ClockColorValues.COLOR_ID_DARK, ClockColorValues.COLOR_ID_LIGHT, ClockColorValuesSun.COLOR_ID_SUN};
    }

    @Override
    public ColorValues getDefaultColors(Context context) {
        return new com.forrestguice.suntimes.naturalhour.ui.clockview.ClockColorValues(context);
    }

    @Override
    protected ColorValues getDefaultColors(Context context, @Nullable String colorsID)
    {
        if (colorsID == null) {
            return getDefaultColors(context);
        }

        ColorValues v;
        switch (colorsID)
        {
            case ClockColorValues.COLOR_ID_DARK:
                v = new com.forrestguice.suntimes.naturalhour.ui.clockview.ClockColorValues(context, true);
                break;

            case ClockColorValues.COLOR_ID_LIGHT:
                v = new com.forrestguice.suntimes.naturalhour.ui.clockview.ClockColorValues(context, false);
                break;

            case ClockColorValuesSun.COLOR_ID_SUN:
                v = new ClockColorValuesSun(context, true);
                break;

            default:
                v = getDefaultColors(context);
                break;
        }
        v.setID(colorsID);
        v.setLabel(getDefaultLabel(context, colorsID));
        return v;
    }

    public String getDefaultLabel(Context context, @Nullable String colorsID)
    {
        if (colorsID == null) {
            return context.getString(R.string.defaultColors_name);
        }
        switch(colorsID) {
            case ClockColorValues.COLOR_ID_DARK: return context.getString(R.string.defaultColors_name_dark);
            case ClockColorValues.COLOR_ID_LIGHT: return context.getString(R.string.defaultColors_name_light);
            case ClockColorValuesSun.COLOR_ID_SUN: return context.getString(R.string.defaultColors_name_az);
            default: return colorsID;
        }
    }

    public static ColorValuesCollection<ColorValues> initClockColors(Context context)
    {
        ColorValuesCollection<ColorValues> colorCollection = new ClockColorValuesCollection<ClockColorValues>(context);
        colorCollection.setColors(context, ClockColorValues.getColorDefaults(context, true));
        colorCollection.setColors(context, ClockColorValues.getColorDefaults(context, false));

        String[] defaults = context.getResources().getStringArray(R.array.clockface_collection);
        for (String json : defaults) {
            colorCollection.setColors(context, new ClockColorValues(json));
        }
        return colorCollection;
    }

    @Override
    @Nullable
    public String getSelectedColorsID(Context context, int appWidgetID, @Nullable String tag)
    {
        String defaultId = ((appWidgetID == 0) ? ClockColorValues.COLOR_ID_LIGHT : ClockColorValues.COLOR_ID_DARK);
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getString(getSelectedColorsKey(appWidgetID, tag), defaultId);
    }

}
