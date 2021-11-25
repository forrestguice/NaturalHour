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

package com.forrestguice.suntimes.naturalhour.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.alarm.AlarmHelper;
import com.forrestguice.suntimes.naturalhour.AppSettings;
import com.forrestguice.suntimes.naturalhour.BuildConfig;
import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.ui.NaturalHourFragment;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;
import com.forrestguice.suntimes.naturalhour.ui.widget.NaturalHourWidget_3x2;
import com.forrestguice.suntimes.naturalhour.ui.widget.NaturalHourWidget_4x3;
import com.forrestguice.suntimes.naturalhour.ui.widget.NaturalHourWidget_5x3;
import com.forrestguice.suntimes.widget.WidgetListHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.forrestguice.suntimes.alarm.AlarmHelper.EXTRA_ALARM_NOW;
import static com.forrestguice.suntimes.alarm.AlarmHelper.EXTRA_ALARM_OFFSET;
import static com.forrestguice.suntimes.alarm.AlarmHelper.EXTRA_ALARM_REPEAT;
import static com.forrestguice.suntimes.alarm.AlarmHelper.EXTRA_ALARM_REPEAT_DAYS;
import static com.forrestguice.suntimes.alarm.AlarmHelper.EXTRA_LOCATION_ALT;
import static com.forrestguice.suntimes.alarm.AlarmHelper.EXTRA_LOCATION_LAT;
import static com.forrestguice.suntimes.alarm.AlarmHelper.EXTRA_LOCATION_LON;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.AUTHORITY;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_ALARM_NAME;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_ALARM_SUMMARY;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_ALARM_TIMEMILLIS;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_ALARM_TITLE;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_CONFIG_APP_VERSION;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_CONFIG_APP_VERSION_CODE;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_CONFIG_PROVIDER_VERSION;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_CONFIG_PROVIDER_VERSION_CODE;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.QUERY_ALARM_CALC;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.QUERY_ALARM_CALC_PROJECTION;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.QUERY_ALARM_INFO;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.QUERY_ALARM_INFO_PROJECTION;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.QUERY_CONFIG;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.QUERY_CONFIG_PROJECTION;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.QUERY_WIDGET;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.QUERY_WIDGET_PROJECTION;

public class NaturalHourProvider extends ContentProvider
{
    private static final int URIMATCH_CONFIG = 0;
    private static final int URIMATCH_WIDGET = 10;
    private static final int URIMATCH_ALARM_INFO = 40;
    private static final int URIMATCH_ALARM_INFO_FOR_NAME = 50;
    private static final int URIMATCH_ALARM_CALC_FOR_NAME = 60;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static
    {
        uriMatcher.addURI(AUTHORITY, QUERY_CONFIG, URIMATCH_CONFIG);
        uriMatcher.addURI(AUTHORITY, QUERY_WIDGET, URIMATCH_WIDGET);
        uriMatcher.addURI(AUTHORITY, QUERY_ALARM_INFO, URIMATCH_ALARM_INFO);
        uriMatcher.addURI(AUTHORITY, QUERY_ALARM_INFO + "/*", URIMATCH_ALARM_INFO_FOR_NAME);
        uriMatcher.addURI(AUTHORITY, QUERY_ALARM_CALC + "/*", URIMATCH_ALARM_CALC_FOR_NAME);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder)
    {
        HashMap<String, String> selectionMap = AlarmHelper.processSelection(AlarmHelper.processSelectionArgs(selection, selectionArgs));
        long[] range;
        Cursor cursor = null;
        int uriMatch = uriMatcher.match(uri);
        switch (uriMatch)
        {
            case URIMATCH_CONFIG:
                Log.i(getClass().getSimpleName(), "URIMATCH_CONFIG");
                cursor = queryConfig(uri, projection, selection, selectionArgs, sortOrder);
                break;

            case URIMATCH_WIDGET:
                Log.i(getClass().getSimpleName(), "URIMATCH_WIDGET");
                cursor = queryWidgets(uri, projection, selection, selectionArgs, sortOrder);
                break;

            case URIMATCH_ALARM_INFO:
                Log.i(getClass().getSimpleName(), "URIMATCH_ALARM_INFO");
                cursor = queryAlarmInfo(null, uri, projection, selectionMap, sortOrder);
                break;

            case URIMATCH_ALARM_INFO_FOR_NAME:
                Log.i(getClass().getSimpleName(), "URIMATCH_ALARM_INFO_FOR_NAME");
                cursor = queryAlarmInfo(uri.getLastPathSegment(), uri, projection, selectionMap, sortOrder);
                break;

            case URIMATCH_ALARM_CALC_FOR_NAME:
                Log.i(getClass().getSimpleName(), "URIMATCH_ALARM_CALC_FOR_NAME");
                cursor = queryAlarmTime(uri.getLastPathSegment(), uri, projection, selectionMap, sortOrder);
                break;

            default:
                Log.e(getClass().getSimpleName(), "Unrecognized URI! " + uri);
                break;
        }
        return cursor;
    }

    /**
     * queryConfig
     */
    public Cursor queryConfig(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder)
    {
        String[] columns = (projection != null ? projection : QUERY_CONFIG_PROJECTION);
        MatrixCursor cursor = new MatrixCursor(columns);

        Context context = getContext();
        if (context != null)
        {
            SuntimesInfo config = SuntimesInfo.queryInfo(context);
            Object[] row = new Object[columns.length];
            for (int i=0; i<columns.length; i++)
            {
                switch (columns[i])
                {
                    case COLUMN_CONFIG_PROVIDER_VERSION:
                        row[i] = NaturalHourProviderContract.VERSION_NAME;
                        break;

                    case COLUMN_CONFIG_PROVIDER_VERSION_CODE:
                        row[i] = NaturalHourProviderContract.VERSION_CODE;
                        break;

                    case COLUMN_CONFIG_APP_VERSION:
                        row[i] = BuildConfig.VERSION_NAME + (BuildConfig.DEBUG ? " [" + BuildConfig.BUILD_TYPE + "]" : "");
                        break;

                    case COLUMN_CONFIG_APP_VERSION_CODE:
                        row[i] = BuildConfig.VERSION_CODE;
                        break;

                    default:
                        row[i] = null;
                        break;
                }
            }
            cursor.addRow(row);

        } else Log.w(getClass().getSimpleName(), "queryConfig: context is null!");
        return cursor;
    }

    /**
     * queryWidget
     */
    public Cursor queryWidgets(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder)
    {
        Context context = getContext();
        String[] columns = (projection != null ? projection : QUERY_WIDGET_PROJECTION);
        Class[] widgetClass = new Class[] { NaturalHourWidget_3x2.class, NaturalHourWidget_4x3.class, NaturalHourWidget_5x3.class };
        String[] summary = (context == null) ? new String[] {"Clock Widget (3x2)", "Clock Widget (4x3)", "Clock Widget (5x3)"}
                : new String[] { context.getString(R.string.widget_summary, "3x2"), context.getString(R.string.widget_summary, "4x3"), context.getString(R.string.widget_summary, "5x3") };
        int[] icons = new int[] { R.mipmap.ic_launcher_round, R.mipmap.ic_launcher_round, R.mipmap.ic_launcher_round };
        return WidgetListHelper.createWidgetListCursor(getContext(), columns, widgetClass, summary, icons);
    }

    /**
     * queryAlarmInfo
     */
    public Cursor queryAlarmInfo(@Nullable String alarmId, @NonNull Uri uri, @Nullable String[] projection, HashMap<String, String> selectionMap, @Nullable String sortOrder)
    {
        //Log.d("DEBUG", "queryAlarmInfo: " + alarmId);
        String[] columns = (projection != null ? projection : QUERY_ALARM_INFO_PROJECTION);
        MatrixCursor cursor = new MatrixCursor(columns);

        Context context = getContext();
        if (context != null)
        {
            String[] alarms = (alarmId != null)
                    ? new String[] { alarmId }
                    : getAlarmList(context);

            for (int j=0; j<alarms.length; j++)
            {
                Object[] row = new Object[columns.length];
                for (int i=0; i<columns.length; i++)
                {
                    switch (columns[i])
                    {
                        case COLUMN_ALARM_NAME:
                            row[i] = alarms[j];
                            break;

                        case COLUMN_ALARM_TITLE:
                            row[i] = getAlarmTitle(context, alarms[j]);
                            break;

                        case COLUMN_ALARM_SUMMARY:
                            row[i] = getAlarmSummary(context, alarms[j]);
                            break;

                        default:
                            row[i] = null;
                            break;
                    }
                }
                cursor.addRow(row);
            }

        } else Log.w(getClass().getSimpleName(), "queryAlarmInfo: context is null!");
        return cursor;
    }

    public Cursor queryAlarmTime(@Nullable String alarmName, @NonNull Uri uri, @Nullable String[] projection, HashMap<String, String> selectionMap, @Nullable String sortOrder)
    {
        //Log.d("DEBUG", "queryAlarmTime: " + alarmName);
        String[] columns = (projection != null ? projection : QUERY_ALARM_CALC_PROJECTION);
        MatrixCursor cursor = new MatrixCursor(columns);

        Context context = getContext();
        if (context != null)
        {
            Object[] row = new Object[columns.length];
            for (int i=0; i<columns.length; i++)
            {
                switch (columns[i])
                {
                    case COLUMN_ALARM_NAME:
                        row[i] = alarmName;
                        break;

                    case COLUMN_ALARM_TIMEMILLIS:
                        row[i] = calculateAlarmTime(context, alarmName, selectionMap);
                        break;

                    default:
                        row[i] = null;
                        break;
                }
            }
            cursor.addRow(row);

        } else Log.w(getClass().getSimpleName(), "queryAlarmTime: context is null!");
        return cursor;
    }

    /**
     * @param alarmID alarmID; hourMode_hourNum or hourMode_hourNum_momentNum
     * @return null if alarmID is invalid; or int[3] .. [CalculatorMode, hourNum, momentNum], where hourNum [0,24) and momentNum [0,40)
     */
    public static int[] alarmIdToNaturalHour(@Nullable String alarmID)
    {
        String[] parts = alarmID != null ? alarmID.split("_") : new String[0];
        if (parts.length == 2 || parts.length == 3)
        {
            try {
                int momentNum = (parts.length == 3) ? Integer.parseInt(parts[2]) : 0;
                int hourNum = Integer.parseInt(parts[1]);
                int hourMode = Integer.parseInt(parts[0]);

                if (hourNum >= 0 && hourNum < 24) {
                    return new int[] { hourMode, hourNum, momentNum };

                } else {
                    Log.e("NaturalHourProvider", "alarmToNaturalHour: invalid alarmID: " + alarmID + "; hour out-of-range: " + hourNum);
                }
            } catch (NumberFormatException e) {
                Log.e("NaturalHourProvider", "alarmToNaturalHour: invalid alarmID: " + alarmID + " .. " + e);
            }
        }
        return null;
    }

    public static String naturalHourToAlarmID(int hourMode, int hourNum, int momentNum)
    {
        if (hourNum < 0 || hourNum >= 24) {
            throw new IndexOutOfBoundsException("hourNum must be [0,24); " + hourNum);
        }
        if (momentNum < 0 || momentNum >= 40) {
            throw new IndexOutOfBoundsException("momentNum [0,40); " + momentNum);
        }
        return hourMode + "_" + hourNum + "_" + momentNum;
    }

    public static String[] getAlarmList(@NonNull Context context) {
        return getAlarmList(AppSettings.getClockIntValue(context, NaturalHourClockBitmap.VALUE_HOURMODE, NaturalHourClockBitmap.HOURMODE_DEFAULT));
    }

    public static String[] getAlarmList(int hourMode)
    {
        //String[] retValue = new String[24];
        //for (int i=0; i<retValue.length; i++) {
        //    retValue[i] = naturalHourToAlarmID(hourMode, i, 0);
        //}
        //return retValue;
        return new String[0];
    }

    public static String getAlarmTitle(Context context, @Nullable String alarmID)
    {
        int[] hour = alarmIdToNaturalHour(alarmID);
        if (hour != null) {
            return context.getString(R.string.alarm_title_format, NaturalHourFragment.naturalHourPhrase(context, hour[0], hour[1], hour[2]));
        } else return null;
    }

    public static String getAlarmSummary(Context context, @Nullable String alarmID) {
        if (alarmIdToNaturalHour(alarmID) != null) {
            return context.getString(R.string.alarm_summary_format);
        } else return null;
    }

    public static long calculateAlarmTime(@NonNull Context context, @Nullable String alarmID, HashMap<String, String> selectionMap)
    {
        int[] hour = alarmIdToNaturalHour(alarmID);
        ContentResolver resolver = context.getContentResolver();
        if (hour != null && resolver != null)
        {
            Calendar now = AlarmHelper.getNowCalendar(selectionMap.get(EXTRA_ALARM_NOW));
            long nowMillis = now.getTimeInMillis();
            float momentRatio = hour[2] / 39f;

            String offsetString = selectionMap.get(EXTRA_ALARM_OFFSET);
            long offset = offsetString != null ? Long.parseLong(offsetString) : 0L;

            boolean repeating = Boolean.parseBoolean(selectionMap.get(EXTRA_ALARM_REPEAT));
            ArrayList<Integer> repeatingDays = AlarmHelper.getRepeatDays(selectionMap.get(EXTRA_ALARM_REPEAT_DAYS));

            String latitudeString = selectionMap.get(EXTRA_LOCATION_LAT);
            String longitudeString = selectionMap.get(EXTRA_LOCATION_LON);
            String altitudeString = selectionMap.get(EXTRA_LOCATION_ALT);
            Double latitude = latitudeString != null ? Double.parseDouble(latitudeString) : null;
            Double longitude = longitudeString != null ? Double.parseDouble(longitudeString) : null;
            double altitude = altitudeString != null ? Double.parseDouble(altitudeString) : 0;
            if (latitude == null || longitude == null)
            {
                SuntimesInfo info = SuntimesInfo.queryInfo(context);
                latitude = Double.parseDouble(info.location[1]);
                longitude = Double.parseDouble(info.location[2]);
                altitude = Double.parseDouble(info.location[3]);
            }

            //Log.d("DEBUG", "calculateAlarmTime: now: " + nowMillis + ", offset: " + offset + ", repeat: " + repeating + ", repeatDays: " + selectionMap.get(EXTRA_ALARM_REPEAT_DAYS)
            //        + ", latitude: " + latitude + ", longitude: " + longitude + ", altitude: " + altitude);

            NaturalHourCalculator calculator = NaturalHourClockBitmap.getCalculator(hour[0]);
            calculator.setUseDefaultLocation(false);

            Calendar alarmTime = Calendar.getInstance();
            Calendar eventTime;

            Calendar day = Calendar.getInstance();
            NaturalHourData data = new NaturalHourData(day.getTimeInMillis(), latitude, longitude, altitude);
            calculator.calculateData(resolver, data, false, false);
            eventTime = data.getNaturalHour(hour[1], momentRatio);
            if (eventTime != null)
            {
                eventTime.set(Calendar.SECOND, 0);
                alarmTime.setTimeInMillis(eventTime.getTimeInMillis() + offset);
            }

            int c = 0;
            Set<Long> timestamps = new HashSet<>();
            while (now.after(alarmTime)
                    || eventTime == null
                    || (repeating && !repeatingDays.contains(eventTime.get(Calendar.DAY_OF_WEEK))))
            {
                if (!timestamps.add(alarmTime.getTimeInMillis()) && c > 365) {
                    Log.e("NaturalHourProvider", "updateAlarmTime: encountered same timestamp twice! (breaking loop)");
                    return -1L;
                }

                Log.w("NaturalHourProvider", "updateAlarmTime: advancing by 1 day..");
                day.add(Calendar.DAY_OF_YEAR, 1);
                data = new NaturalHourData(day.getTimeInMillis(), latitude, longitude, altitude);
                calculator.calculateData(resolver, data, false, false);
                eventTime = data.getNaturalHour(hour[1], momentRatio);
                if (eventTime != null)
                {
                    eventTime.set(Calendar.SECOND, 0);
                    alarmTime.setTimeInMillis(eventTime.getTimeInMillis() + offset);
                }
                c++;
            }

            return eventTime.getTimeInMillis();

        } else {
            return -1L;
        }
    }

}