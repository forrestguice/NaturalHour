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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.view.ContextThemeWrapper;

import com.forrestguice.suntimes.naturalhour.MainActivity;
import com.forrestguice.suntimes.naturalhour.R;

public class NaturalHourTileBase extends SuntimesTileBase
{
    public static final int TILE_APPWIDGET_ID = -1000;

    public NaturalHourTileBase(@Nullable Activity activity) {
        super(activity);
    }

    @Override
    public int appWidgetId() {
        return TILE_APPWIDGET_ID;
    }

    @Override
    public Intent getConfigIntent(Context context) {
        return null;   // TODO: configurable
    }

    @NonNull
    protected String getLaunchIntentTitle(Context context) {
        return context.getString(R.string.app_name);
    }

    @Override
    public Intent getLaunchIntent(Context context)
    {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    @Nullable
    protected Intent getLockScreenIntent(Context context) {
        return new Intent(context, TileLockScreenActivity.class);
    }

    @Override
    protected void initDefaults(Context context)
    {
        super.initDefaults(context);
        // TODO: configurable
    }

    @Override
    public SpannableStringBuilder formatDialogTitle(Context context)
    {
        //Calendar now = now(context);
        //WidgetSettings.TimeFormatMode formatMode = WidgetSettings.loadTimeFormatModePref(context, appWidgetId());
        //String timeString = utils.calendarTimeShortDisplayString(context, now, false, formatMode).toString();
        //SpannableString timeDisplay = SuntimesUtils.createBoldSpan(null, timeString, timeString);
        //timeDisplay = SuntimesUtils.createRelativeSpan(timeDisplay, timeString, timeString, 1.25f);

        SpannableStringBuilder title = new SpannableStringBuilder("TODO");
        //title.append(timeDisplay);
        return title;
    }

    @Override
    public SpannableStringBuilder formatDialogMessage(Context context)
    {
        //TimeZone timezone = timezone(context);
        //Location location = location(context);
        //String tzDisplay = WidgetTimezones.getTimeZoneDisplay(context, timezone);
        //boolean isLocalTime = SuntimesTileBase.isLocalTime(timezone.getID());

        //String dateString = utils.calendarDateDisplayString(context, now(context), true).toString();
        //SpannableString dateDisplay = SuntimesUtils.createBoldSpan(null, dateString, dateString);
        //dateDisplay = SuntimesUtils.createRelativeSpan(dateDisplay, dateString, dateString, 1.25f);

        //SpannableStringBuilder message = new SpannableStringBuilder(tzDisplay);
        SpannableStringBuilder message = new SpannableStringBuilder("TODO");
        //message.append((isLocalTime ? "\n" + location.getLabel() : ""));
        message.append("\n\n");
        //message.append(dateDisplay);
        return message;
    }

    @Override
    public Drawable getDialogIcon(Context context)
    {
        ContextThemeWrapper contextWrapper = new ContextThemeWrapper(context, R.style.NaturalHourAppTheme_System);
        //TimeZone timezone = timezone(context);
        //boolean isLocalTime = SuntimesTileBase.isLocalTime(timezone.getID());

        //int[] attrs = { R.attr.ic };
        //TypedArray a = contextWrapper.obtainStyledAttributes(attrs);
        //int icon = a.getResourceId(isLocalTime ? 1 : 0, R.drawable.ic_action_time);
        //a.recycle();
        return ContextCompat.getDrawable(context, R.drawable.ic_alarm);    // TODO
    }
}
