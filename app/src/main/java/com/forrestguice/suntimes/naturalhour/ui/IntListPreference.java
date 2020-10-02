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

package com.forrestguice.suntimes.naturalhour.ui;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;

/**
 * IntListPreference: an extended version of ListPreference that attempts to save/load values as int.
 */
public class IntListPreference extends ListPreference
{
    public IntListPreference(Context context) {
        super(context);
    }

    public IntListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean persistString(String value) {
        return persistInt(Integer.parseInt(value));
    }

    @Override
    protected String getPersistedString(String defaultValue)
    {
        if (!shouldPersist()) {
            return defaultValue;
        }

        int intDefault;
        try {
            intDefault = defaultValue != null ? Integer.parseInt(defaultValue) : 0;
        } catch (NumberFormatException e) {
            Log.e(getClass().getSimpleName(), "getPersistedString: not numeric! " + e);
            intDefault = 0;
        }
        return Integer.toString(getPersistedInt(intDefault));
    }
}