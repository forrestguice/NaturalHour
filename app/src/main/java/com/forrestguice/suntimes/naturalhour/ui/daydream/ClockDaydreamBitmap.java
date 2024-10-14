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

package com.forrestguice.suntimes.naturalhour.ui.daydream;

import android.content.Context;
import android.util.Log;

import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;

public class ClockDaydreamBitmap extends NaturalHourClockBitmap
{
    public ClockDaydreamBitmap(Context context, int size) {
        super(context, size);
    }

    @Override
    protected void initFlags(Context context)
    {
        setFlagIfUnset(FLAG_SHOW_SECONDS, context.getResources().getBoolean(R.bool.daydream_show_seconds));
        super.initFlags(context);
    }

    @Override
    public boolean getDefaultFlag(Context context, String flag)
    {
        switch (flag) {
            case FLAG_SHOW_SECONDS: return context.getResources().getBoolean(R.bool.daydream_show_seconds);
            default: return super.getDefaultFlag(context, flag);
        }
    }

    @Override
    public int getDefaultValue(Context context, String key)
    {
        switch (key) {
            default: return super.getDefaultValue(context, key);
        }
    }
}
