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
import android.support.annotation.Nullable;
import android.view.Menu;

import com.forrestguice.suntimes.addon.ui.Messages;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValues;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValuesSelectFragment;

public class ClockColorValuesSelectFragment extends ColorValuesSelectFragment
{
    @Override
    protected void onPrepareOverflowMenu(Context context, Menu menu) {
        Messages.forceActionBarIcons(menu);
    }

    @Override
    @Nullable
    protected ColorValues createColorValues(String jsonString) {
        return new ClockColorValues(jsonString);
    }
}