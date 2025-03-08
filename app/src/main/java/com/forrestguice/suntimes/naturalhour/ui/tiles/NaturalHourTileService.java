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

package com.forrestguice.suntimes.naturalhour.ui.tiles;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.text.SpannableStringBuilder;

import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.ui.DisplayStrings;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;

import java.util.TimeZone;

@TargetApi(24)
public class NaturalHourTileService extends SuntimesTileService
{
    @Override
    protected SuntimesTileBase initTileBase() {
        return new NaturalHourTileBase(null);
    }

    @Override
    protected int appWidgetId() {
        return NaturalHourTileBase.TILE_APPWIDGET_ID;
    }

    @Override
    protected void updateTile(Context context)
    {
        Tile tile = getQsTile();
        tile.setLabel(getLabel(context));
        tile.setIcon(getIcon(context));
        updateTileState(context, tile).updateTile();
    }

    protected Icon getIcon(Context context) {
        return Icon.createWithResource(context, R.drawable.ic_time);
    }

    protected CharSequence getLabel(Context context)
    {
        NaturalHourTileBase b = ((NaturalHourTileBase) base);
        TimeZone timezone = b.getTimeZone(context);
        int timeFormat = b.getTimeFormat(context);
        boolean is24 = (timeFormat == NaturalHourClockBitmap.TIMEFORMAT_24);    // TODO: timeformat: 6hr
        String timeString = DisplayStrings.formatTime(context, b.now(context).getTimeInMillis(), timezone, is24).toString();
        String timezoneString = context.getString(R.string.format_announcement_timezone, timezone.getID());
        String clockTimeString = context.getString(R.string.format_announcement_clocktime, timeString, timezoneString);

        SpannableStringBuilder label = new SpannableStringBuilder(base.formatDialogTitle(context));
        label.append(" ");
        label.append(clockTimeString);
        return label;
    }

}
