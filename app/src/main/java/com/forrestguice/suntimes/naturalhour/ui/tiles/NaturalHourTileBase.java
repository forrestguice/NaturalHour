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
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;

import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.naturalhour.AppSettings;
import com.forrestguice.suntimes.naturalhour.MainActivity;
import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourCalculator;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourData;
import com.forrestguice.suntimes.naturalhour.ui.NaturalHourFragment;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;
import com.forrestguice.suntimes.naturalhour.ui.widget.WidgetPreferenceFragment;

import java.util.Calendar;
import java.util.TimeZone;

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
        Intent intent = new Intent(context, NaturalHourTileConfigActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected boolean getLaunchIntentNeedsUnlock() {
        return false;
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
    protected void initDefaults(Context context) {
        super.initDefaults(context);
    }

    @Override
    public SpannableStringBuilder formatDialogTitle(Context context)
    {
        Calendar now = now(context);
        NaturalHourData data = initData(context);

        int hourMode = AppSettings.getClockIntValue(context, NaturalHourClockBitmap.VALUE_HOURMODE);
        boolean mode24 = (hourMode == NaturalHourClockBitmap.HOURMODE_SUNSET);

        int currentHour = NaturalHourData.findNaturalHour(now, data);    // [1,24]
        int currentHourOf = ((currentHour - 1) % 12) + 1;    // [1,12]
        if (mode24) {
            currentHourOf = (currentHour > 12 ? currentHour - 12 : currentHour + 12);
        }

        String numeralString = NaturalHourClockBitmap.getNumeral(context, numeralType(context), currentHourOf);
        SpannableStringBuilder title = new SpannableStringBuilder(numeralString);
        //title.append(timeDisplay);
        return title;
    }

    @Override
    public SpannableStringBuilder formatDialogMessage(Context context)
    {
        Calendar now = now(context);
        NaturalHourData data = initData(context);
        int currentHour = NaturalHourData.findNaturalHour(now, data);    // [1,24]
        SpannableString announcement = NaturalHourFragment.announceTime(context, now, hourMode(context), currentHour, is24(context), numeralType(context));
        return new SpannableStringBuilder(announcement);
    }

    @Override
    public Drawable getDialogIcon(Context context)
    {
        return null;
        //ContextThemeWrapper contextWrapper = new ContextThemeWrapper(context, R.style.NaturalHourAppTheme_System);
        //return ContextCompat.getDrawable(contextWrapper, R.drawable.ic_time);
    }

    //////////////////////////////////////////////////

    protected SuntimesInfo info;
    protected NaturalHourData data;
    protected NaturalHourCalculator calculator;

    protected SuntimesInfo initSuntimesInfo(Context context)
    {
        if (info == null) {
            info = SuntimesInfo.queryInfo(context);
        }
        return info;
    }
    protected NaturalHourData initData(Context context)
    {
        initSuntimesInfo(context);
        if (data == null) {
            data = createData(context);
        }
        return data;
    }
    public NaturalHourCalculator initCalculator(Context context)
    {
        if (calculator == null) {
            calculator = createCalculator(context);
        }
        return calculator;
    }

    protected NaturalHourData createData(Context context)
    {
        Calendar date = Calendar.getInstance(getTimeZone(context));
        date.set(Calendar.HOUR_OF_DAY, 12);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);

        String[] location = getLocation(context);
        return calculateData(context, initCalculator(context), new NaturalHourData(date.getTimeInMillis(), location[1], location[2], location[3]));
    }

    protected Calendar now(Context context) {
        return Calendar.getInstance(getTimeZone(context));
    }

    protected TimeZone getTimeZone(Context context)
    {
        String widgetPrefix = WidgetPreferenceFragment.widgetKeyPrefix(appWidgetId());
        int tzMode = AppSettings.getClockIntValue(context, widgetPrefix + AppSettings.KEY_MODE_TIMEZONE, AppSettings.TZMODE_DEFAULT);
        return AppSettings.fromTimeZoneMode(context, tzMode, initSuntimesInfo(context));
    }

    protected boolean is24(Context context) {

        String widgetPrefix = WidgetPreferenceFragment.widgetKeyPrefix(appWidgetId());
        int timeMode = AppSettings.getClockIntValue(context, widgetPrefix + AppSettings.KEY_MODE_TIMEFORMAT, AppSettings.TIMEMODE_DEFAULT);
        return AppSettings.fromTimeFormatMode(context, timeMode, initSuntimesInfo(context));
    }

    protected String[] getLocation(Context context)
    {
        SuntimesInfo info = initSuntimesInfo(context);
        return info != null && info.location != null && info.location.length >= 4 ? info.location : new String[] {"", "0", "0", "0"};
    }

    public NaturalHourCalculator createCalculator(Context context) {
        return NaturalHourClockBitmap.getCalculator(hourMode(context));
    }

    private NaturalHourData calculateData(@NonNull Context context, @NonNull NaturalHourCalculator calculator, NaturalHourData naturalHourData)
    {
        ContentResolver resolver;
        if (context != null && calculator != null && (resolver = context.getContentResolver()) != null) {
            calculator.calculateData(resolver, naturalHourData);

        } else {
            Log.e(getClass().getSimpleName(), "createData: null context, calculator, or contextResolver!");
        }
        return naturalHourData;
    }

    protected int hourMode(Context context) {
        return AppSettings.getClockIntValue(context, WidgetPreferenceFragment.widgetKeyPrefix(appWidgetId()) + NaturalHourClockBitmap.VALUE_HOURMODE, NaturalHourClockBitmap.HOURMODE_DEFAULT);
    }

    protected int numeralType(Context context) {
        return AppSettings.getClockIntValue(context, WidgetPreferenceFragment.widgetKeyPrefix(appWidgetId()) + NaturalHourClockBitmap.VALUE_NUMERALS);
    }

}
