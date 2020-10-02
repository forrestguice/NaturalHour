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

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ActionProvider;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.naturalhour.AppSettings;
import com.forrestguice.suntimes.naturalhour.MainActivity;
import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourCalculator;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourCalculator1;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class NaturalHourWidget extends AppWidgetProvider
{
    public static final String ACTION_WIDGET_UPDATE = "suntimes.naturalhour.WIDGET_UPDATE";
    public static final String ACTION_WIDGET_CLICK_DONOTHING = "suntimes.naturalhour.WIDGET_CLICK_DONOTHING";
    public static final String ACTION_WIDGET_CLICK_LAUNCHAPP = "suntimes.naturalhour.WIDGET_CLICK_LAUNCHAPP";
    public static final String ACTION_WIDGET_CLICK_RECONFIGURE = "suntimes.naturalhour.WIDGET_CLICK_RECONFIGURE";

    public static final String ACTION_SUNTIMES_THEME_UPDATE = "suntimes.SUNTIMES_THEME_UPDATE";
    public static final String ACTION_SUNTIMES_ALARM_UPDATE = "suntimes.SUNTIMES_ALARM_UPDATE";

    public static final String KEY_WIDGETCLASS = "widgetClass";
    public static final String KEY_THEME = "themeName";

    public void initLocale(Context context) {
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);
        initLocale(context);

        String filter = getUpdateIntentFilter();
        String action = intent.getAction();
        Bundle extras = intent.getExtras();

        if (action != null && action.equals(filter))
        {
            String widgetClass = intent.getStringExtra(KEY_WIDGETCLASS);
            if (getClass().toString().equals(widgetClass))
            {
                int appWidgetID = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);  // synonymous
                Log.d(getClass().getSimpleName(), "onReceive: " + filter + "(" + appWidgetID + "): " + getClass().toString());

                if (appWidgetID <= 0) {
                    updateWidgets(context);
                } else {
                    onUpdate(context, AppWidgetManager.getInstance(context), new int[]{appWidgetID});
                }
                setUpdateAlarm(context, appWidgetID);      // schedule next update
            }

        } else if (action != null && action.equals(AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED)) {
            Log.d(getClass().getSimpleName(), "onReceive: ACTION_APPWIDGET_OPTIONS_CHANGED :: " + getClass());

        } else if (action != null && action.equals(ACTION_SUNTIMES_THEME_UPDATE)) {
            String themeName = (intent.hasExtra(KEY_THEME) ? intent.getStringExtra(KEY_THEME) : null);
            Log.d(getClass().getSimpleName(), "onReceive: SUNTIMES_THEME_UPDATE :: " + getClass() + " :: " + themeName);
            updateWidgets(context, themeName);

        } else if (action != null && action.equals(ACTION_SUNTIMES_ALARM_UPDATE)) {
            Log.d(getClass().getSimpleName(), "onReceive: SUNTIMES_ALARM_UPDATE :: " + getClass());
            updateWidgets(context);
            setUpdateAlarms(context);

        } else if (action != null && action.equals("android.intent.action.TIME_SET")) {
            Log.d(getClass().getSimpleName(), "onReceive: android.intent.action.TIME_SET :: " + getClass());
            //updateWidgets(context);
            //setUpdateAlarms(context);
            // TODO: handle TIME_SET better .. when automatic/network time is enabled this thing fires /frequently/ ...

        } else if (action != null && action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            Log.d(getClass().getSimpleName(), "onReceive: ACTION_APPWIDGET_UPDATE :: " + getClass());
            if (extras != null)
            {
                int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if (appWidgetIds != null)
                {
                    for (int appWidgetId : appWidgetIds) {
                        setUpdateAlarm(context, appWidgetId);
                    }
                }
            }

        } else if (isClickAction(action))  {
            Log.d(getClass().getSimpleName(), "onReceive: ClickAction :: " + action + ":: " + getClass());
            handleClickAction(context, intent);

        } else {
            Log.d(getClass().getSimpleName(), "onReceive: unhandled :: " + action + " :: " + getClass());
        }
    }

    @Nullable
    protected Class getConfigClass() {
        return null;
    }

    protected String getUpdateIntentFilter() {
        return ACTION_WIDGET_UPDATE;
    }

    protected PendingIntent getUpdateIntent(Context context, int appWidgetId)
    {
        String updateFilter = getUpdateIntentFilter();
        Intent intent = new Intent(updateFilter);
        intent.setComponent(new ComponentName(context, NaturalHourWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra(KEY_WIDGETCLASS, getClass().toString());
        return PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    protected long getUpdateTimeMillis( Context context, int appWidgetID )
    {
        long suggestedUpdateMillis = getNextSuggestedUpdate(context, appWidgetID);
        if (suggestedUpdateMillis <= 0) {
            return getUpdateTimeMillis(null);

        } else {
            Calendar suggestedUpdate = Calendar.getInstance();
            suggestedUpdate.setTimeInMillis(suggestedUpdateMillis);
            return getUpdateTimeMillis(suggestedUpdate);
        }
    }

    protected long getUpdateTimeMillis(Calendar suggestedUpdateTime)
    {
        Calendar updateTime = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        if (now.before(suggestedUpdateTime)) {
            updateTime.setTimeInMillis(suggestedUpdateTime.getTimeInMillis());
            Log.d(getClass().getSimpleName(), "getUpdateTimeMillis: next update is at: " + updateTime.getTimeInMillis());

        } else {
            updateTime.set(Calendar.MILLISECOND, 0);   // reset seconds, minutes, and hours to 0
            updateTime.set(Calendar.MINUTE, 0);
            updateTime.set(Calendar.SECOND, 0);
            updateTime.set(Calendar.HOUR_OF_DAY, 0);
            updateTime.add(Calendar.DAY_OF_MONTH, 1);  // and increment the date by 1 day
            Log.d(getClass().getSimpleName(), "getUpdateTimeMillis: next update is at midnight: " + updateTime.getTimeInMillis());
        }
        return updateTime.getTimeInMillis();
    }


    protected long getUpdateInterval() {
        return 0L;
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds)
    {
        for (int appWidgetId : appWidgetIds)
        {
            unsetUpdateAlarm(context, appWidgetId);
            deleteNextSuggestedUpdate(context, appWidgetId);
        }
    }

    public void updateWidgets(Context context)
    {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] widgetIds = getWidgetIds(context, widgetManager);
        onUpdate(context, widgetManager, widgetIds);
    }

    public void updateWidgets(Context context, String themeName)
    {
        if (themeName == null)
        {
            Log.w(getClass().getSimpleName(), "updateWidgets: requested to update widgets by theme but no theme was supplied (null)... updating all");
            updateWidgets(context);
            return;
        }

        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] widgetIds = getWidgetIds(context, widgetManager);
        ArrayList<Integer> filteredList = new ArrayList<>();
        for (int id : widgetIds)
        {
            String theme = "dark";    // TODO: WidgetSettings.loadThemeName(context, id);
            if (theme.equals(themeName)) {
                filteredList.add(id);
            }
        }
        if (filteredList.size() > 0)
        {
            int[] filteredIds = new int[filteredList.size()];
            for (int i = 0; i < filteredIds.length; i++) {
                filteredIds[i] = filteredList.get(i);
            }
            onUpdate(context, widgetManager, filteredIds);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        initLocale(context);
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    protected void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
    {
        Calendar now = Calendar.getInstance();
        SuntimesInfo suntimesInfo = SuntimesInfo.queryInfo(context);
        double latitude = Double.parseDouble(suntimesInfo.location[1]);
        double longitude = Double.parseDouble(suntimesInfo.location[2]);
        double altitude = Double.parseDouble(suntimesInfo.location[3]);

        RemoteViews views = getViews(context);
        views.setOnClickPendingIntent(R.id.widgetframe_inner, getClickActionIntent(context, appWidgetId, getClass()));

        //boolean showTitle = false; // TODO: WidgetSettings.loadShowTitlePref(context, appWidgetId);
        //views.setViewVisibility(R.id.text_title, showTitle ? View.VISIBLE : View.GONE);

        ContentResolver resolver = context.getContentResolver();
        NaturalHourCalculator calculator = AppSettings.HOURMODE_CIVIL == AppSettings.getClockIntValue(context, AppSettings.KEY_CLOCK_HOURMODE)
                ? new NaturalHourCalculator1() : new NaturalHourCalculator();

        NaturalHourData data = new NaturalHourData(now.getTimeInMillis(), latitude, longitude, altitude);
        calculator.calculateData(resolver, data);

        prepareForUpdate(context, appWidgetId, data);
        themeViews(context, views, appWidgetId);
        updateViews(context, appWidgetId, views, data, suntimesInfo);
        appWidgetManager.updateAppWidget(appWidgetId, views);

        Calendar nextUpdate = Calendar.getInstance();
        nextUpdate.setTimeInMillis(data.getDateMillis());
        nextUpdate.add(Calendar.MINUTE, 1);   // up to a minute from now
        nextUpdate.set(Calendar.SECOND, 1);
        saveNextSuggestedUpdate(context, appWidgetId, nextUpdate.getTimeInMillis());
    }

    private NaturalHourClockBitmap.ClockColorValues clockAppearance;
    protected void updateViews(Context context, int appWidgetId, RemoteViews views, NaturalHourData data, SuntimesInfo suntimesInfo)
    {
        Log.d(getClass().getSimpleName(), "updateViews: " + appWidgetId);
        boolean is24 = AppSettings.fromTimeFormatMode(context, AppSettings.getTimeFormatMode(context), suntimesInfo);
        TimeZone timezone = AppSettings.fromTimeZoneMode(context, AppSettings.getTimeZoneMode(context), suntimesInfo);

        NaturalHourClockBitmap clockView = new NaturalHourClockBitmap(context, clockSizePx);
        clockView.setTimeZone(timezone);
        clockView.set24HourMode(is24);

        for (String flag : NaturalHourClockBitmap.FLAGS) {
            clockView.setFlag(flag, AppSettings.getClockFlag(context, flag));
        }

        views.setImageViewBitmap(R.id.clockface, clockView.makeBitmap(context, data, clockAppearance));
        if (Build.VERSION.SDK_INT >= 15)
        {
            String timeDescription = "TODO";            // TODO
            views.setContentDescription(R.id.clockface, timeDescription);
        }
        //views.setTextViewText(R.id.text_title, "title");
    }

    protected int clockSizePx = 0;
    protected void prepareForUpdate(Context context, int appWidgetId, NaturalHourData data)
    {
        int maxWidgetBoundDp;
        if (Build.VERSION.SDK_INT >= 16)
        {
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
            Bundle widgetOptions = widgetManager.getAppWidgetOptions(appWidgetId);
            maxWidgetBoundDp = Math.max(widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH), widgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT));
        } else {
            maxWidgetBoundDp = 512;
        }

        int maxWidgetBoundPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, maxWidgetBoundDp, context.getResources().getDisplayMetrics());
        Point screenSizePx = new Point(maxWidgetBoundPx, maxWidgetBoundPx);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);    // prefer screen dimensions over max widget bounds
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getSize(screenSizePx);
        }
        clockSizePx = Math.min(screenSizePx.x, screenSizePx.y);
    }

    protected void themeViews(Context context, RemoteViews views, int appWidgetId)
    {
        clockAppearance = new NaturalHourClockBitmap.ClockColorValues(context);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions)
    {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        initLocale(context);
        updateWidget(context, appWidgetManager, appWidgetId);
    }

    public int layoutID() {
        return R.layout.widget_naturalhour_3x3;
    }

    public RemoteViews getViews(Context context) {
        return new RemoteViews(context.getPackageName(), layoutID());
    }

    public int[] getWidgetIds(Context context)
    {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        return getWidgetIds(context, widgetManager);
    }
    public int[] getWidgetIds(Context context, AppWidgetManager widgetManager) {
        return widgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
    }

    protected int[] minSize = { 0, 0 };
    protected int[] getMinSize(Context context)
    {
        if (minSize[0] <= 0 || minSize[1] <= 0) {
            initMinSize(context);
        }
        return minSize;
    }
    protected void initMinSize(Context context) {
        minSize[0] = context.getResources().getInteger(R.integer.widget_size_min_dp3x3);
        minSize[1] = context.getResources().getInteger(R.integer.widget_size_min_dp3x3);
    }

    public PendingIntent getClickActionIntent(Context context, int appWidgetId, Class widgetClass)
    {
        Intent actionIntent = new Intent(context, widgetClass);
        actionIntent.setAction(ACTION_WIDGET_CLICK_LAUNCHAPP);   // TODO: configurable
        actionIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getBroadcast(context, appWidgetId, actionIntent, 0);
    }

    public boolean isClickAction(String action) {
        return action != null && (action.equals(ACTION_WIDGET_CLICK_LAUNCHAPP) || action.equals(ACTION_WIDGET_CLICK_RECONFIGURE) || action.equals(ACTION_WIDGET_CLICK_DONOTHING));
    }

    protected boolean handleClickAction(Context context, Intent intent)
    {
        String action = intent.getAction();
        Bundle extras = intent.getExtras();
        int appWidgetId = (extras != null ? extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, 0) : 0);

        if (action == null) {
            return false;

        } else if (action.equals(ACTION_WIDGET_CLICK_DONOTHING)) {
            return true;

        } else if (action.equals(ACTION_WIDGET_CLICK_LAUNCHAPP)) {
            Intent launchIntent = new Intent(context, MainActivity.class);
            launchIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(launchIntent);
            return true;

        } else if (action.equals(ACTION_WIDGET_CLICK_RECONFIGURE) && getConfigClass() != null) {
            Intent configIntent = new Intent(context, getConfigClass());
            configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            configIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //configIntent.putExtra(EXTRA_RECONFIGURE, true);
            context.startActivity(configIntent);
            return true;
        }
        return false;
    }

    /**
     * Start widget updates; register an alarm (inexactRepeating) that does not wake the device for each widget.
     * @param context the Context
     */
    protected void setUpdateAlarms( Context context )
    {
        for (int appWidgetID : getWidgetIds(context)) {
            setUpdateAlarm(context, appWidgetID);
        }
    }
    protected void unsetUpdateAlarms( Context context )
    {
        for (int appWidgetID : getWidgetIds(context)) {
            unsetUpdateAlarm(context, appWidgetID);
        }
    }

    /**
     * Start widget updates; register an alarm (inexactRepeating) that does not wake the device.
     * @param context the context
     */
    protected void setUpdateAlarm( Context context, int alarmID )
    {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null)
        {
            PendingIntent alarmIntent = getUpdateIntent(context, alarmID);

            long updateTime = getUpdateTimeMillis(context, alarmID);
            if (updateTime > 0)
            {
                if (Build.VERSION.SDK_INT < 19) {
                    alarmManager.set(AlarmManager.RTC, updateTime, alarmIntent);
                } else {
                    alarmManager.setWindow(AlarmManager.RTC, updateTime, 5 * 1000, alarmIntent);
                }
                Log.d(getClass().getSimpleName(), "setUpdateAlarm: " + DisplayStrings.formatTime(context, updateTime, TimeZone.getDefault(), false).toString() + " --> " + getUpdateIntentFilter() + "(" + alarmID + ") :: " + getUpdateInterval());
            } else Log.d(getClass().getSimpleName(), "setUpdateAlarm: skipping " + alarmID);
        }
    }

    /**
     * Stop widget updates; unregisters the update alarm.
     * @param context the context
     */
    protected void unsetUpdateAlarm( Context context, int alarmID )
    {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null)
        {
            PendingIntent alarmIntent = getUpdateIntent(context, alarmID);
            alarmManager.cancel(alarmIntent);
            Log.d(getClass().getSimpleName(), "unsetUpdateAlarm: unset alarm --> " + getUpdateIntentFilter() + "(" + alarmID + ")");
        }
    }

    public static final String PREFS_WIDGET = "com.forrestguice.suntimes.naturalhour";
    public static final String PREF_PREFIX_KEY = "appwidget_";
    public static final String PREF_KEY_NEXTUPDATE = "nextUpdate";

    public static long getNextSuggestedUpdate(Context context, int appWidgetId)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_WIDGET, 0);
        String prefs_prefix = PREF_PREFIX_KEY + appWidgetId;
        return prefs.getLong(prefs_prefix + PREF_KEY_NEXTUPDATE, -1);
    }
    public static void saveNextSuggestedUpdate(Context context, int appWidgetId, long updateTime)
    {
        Log.d("NaturalHourWidget", "saveNextSuggestedUpdate: " + DisplayStrings.formatTime(context, updateTime, TimeZone.getDefault(), false));
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_WIDGET, 0).edit();
        String prefs_prefix = PREF_PREFIX_KEY + appWidgetId;
        prefs.putLong(prefs_prefix + PREF_KEY_NEXTUPDATE, updateTime);
        prefs.apply();
    }
    public static void deleteNextSuggestedUpdate(Context context, int appWidgetId)
    {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_WIDGET, 0).edit();
        String prefs_prefix = PREF_PREFIX_KEY + appWidgetId;
        prefs.remove(prefs_prefix + PREF_KEY_NEXTUPDATE);
        prefs.apply();
    }
}