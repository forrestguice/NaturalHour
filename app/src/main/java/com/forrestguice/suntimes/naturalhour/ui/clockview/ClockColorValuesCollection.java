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

import android.content.Context;

import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValues;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValuesCollection;

/**
 * ColorValuesCollection
 */
public class ClockColorValuesCollection<ClockColorValues> extends ColorValuesCollection
{
    public static final String PREFS_CLOCKCOLORS = "prefs_clockcolors";

    public ClockColorValuesCollection() {
        super();
    }
    public ClockColorValuesCollection(Context context) {
        super(context);
    }

    @Override
    public String getSharedPrefsName() {
        return PREFS_CLOCKCOLORS;
    }

    @Override
    public ColorValues getDefaultColors(Context context) {
        return new com.forrestguice.suntimes.naturalhour.ui.clockview.ClockColorValues(context);
    }
}
