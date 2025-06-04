// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2025 Forrest Guice
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

import com.forrestguice.suntimes.naturalhour.AppSettings;
import com.forrestguice.suntimes.naturalhour.ui.widget.WidgetSettings;

public class DaydreamSettings
{
    public static final String KEY_MODE_FULLSCREEN = "fullscreen";
    public static final boolean DEF_MODE_FULLSCREEN = true;

    public static final String KEY_MODE_INTERACTIVE = "interactive";
    public static final boolean DEF_MODE_INTERACTIVE = false;

    public static final String KEY_MODE_SCREENBRIGHT = "screenbright";
    public static final boolean DEF_MODE_SCREENBRIGHT = false;

    public static final String KEY_MODE_BACKGROUND = "daydreamBackgroundMode";
    public static final int DEF_MODE_BACKGROUND = AppSettings.BGMODE_BLACK;

    public static final String KEY_ANIM_BGPULSE_DURATION = "anim_bgpulse_duration";
    public static final int DEF_ANIM_BGPULSE_DURATION = 15000;

    public static final String[] FLAGS = new String[] { KEY_MODE_FULLSCREEN, KEY_MODE_INTERACTIVE, KEY_MODE_SCREENBRIGHT };
    public static final boolean[] FLAGS_DEF = new boolean[] { DEF_MODE_FULLSCREEN, DEF_MODE_INTERACTIVE, DEF_MODE_SCREENBRIGHT };

    public static final String[] VALUES = new String[] { KEY_MODE_BACKGROUND, KEY_ANIM_BGPULSE_DURATION };
    public static final int[] VALUES_DEF = new int[] { DEF_MODE_BACKGROUND, DEF_ANIM_BGPULSE_DURATION };

    public static boolean getDaydreamFlag(Context context, int appWidgetId, String key, boolean defaultValue) {
        return AppSettings.getClockFlag(context, WidgetSettings.widgetKeyPrefix(appWidgetId) + key, defaultValue);
    }
    public static int getDaydreamIntValue(Context context, int appWidgetId, String key, int defaultValue) {
        return AppSettings.getClockIntValue(context, WidgetSettings.widgetKeyPrefix(appWidgetId) + key, defaultValue);
    }
}