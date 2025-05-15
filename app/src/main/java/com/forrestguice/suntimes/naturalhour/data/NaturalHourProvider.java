// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2020-2025 Forrest Guice
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

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.forrestguice.suntimes.ContextCompat;
import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.alarm.AlarmHelper;
import com.forrestguice.suntimes.naturalhour.BuildConfig;
import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.data.alarms.NaturalHourAlarm0;
import com.forrestguice.suntimes.naturalhour.data.alarms.NaturalHourAlarm1;
import com.forrestguice.suntimes.naturalhour.data.alarms.NaturalHourAlarmType;
import com.forrestguice.suntimes.naturalhour.ui.clockview.ClockColorValuesCollection;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValues;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValuesCollection;
import com.forrestguice.suntimes.naturalhour.ui.widget.NaturalHourWidget_3x2;
import com.forrestguice.suntimes.naturalhour.ui.widget.NaturalHourWidget_4x3;
import com.forrestguice.suntimes.naturalhour.ui.widget.NaturalHourWidget_5x3;
import com.forrestguice.suntimes.widget.WidgetListHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.forrestguice.suntimes.alarm.AlarmEventContract.COLUMN_EVENT_REQUIRES_LOCATION;
import static com.forrestguice.suntimes.alarm.AlarmEventContract.COLUMN_EVENT_SUPPORTS_REPEATING;

import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.AUTHORITY;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_EVENT_NAME;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_EVENT_PHRASE;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_EVENT_PHRASE_GENDER;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_EVENT_PHRASE_QUANTITY;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_EVENT_SUMMARY;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_EVENT_TIMEMILLIS;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_EVENT_TITLE;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_CONFIG_APP_VERSION;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_CONFIG_APP_VERSION_CODE;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_CONFIG_PROVIDER_VERSION;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_CONFIG_PROVIDER_VERSION_CODE;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.QUERY_EVENT_CALC;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.QUERY_EVENT_CALC_PROJECTION;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.QUERY_EVENT_INFO;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.QUERY_EVENT_INFO_PROJECTION;
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
        uriMatcher.addURI(AUTHORITY, QUERY_EVENT_INFO, URIMATCH_ALARM_INFO);
        uriMatcher.addURI(AUTHORITY, QUERY_EVENT_INFO + "/*", URIMATCH_ALARM_INFO_FOR_NAME);
        uriMatcher.addURI(AUTHORITY, QUERY_EVENT_CALC + "/*", URIMATCH_ALARM_CALC_FOR_NAME);
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
        return createWidgetListCursor(getContext(), columns, widgetClass, summary, icons);
    }

    public static MatrixCursor createWidgetListCursor(Context context, String[] columns, Class[] widgetClass, String[] summary, int[] iconResID)
    {
        MatrixCursor cursor = new MatrixCursor(columns);
        if (context != null)
        {
            ColorValuesCollection<ColorValues> colorCollection = ClockColorValuesCollection.initClockColors(context);
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
            for (int i=0; i<widgetClass.length; i++)
            {
                int[] widgetIDs = widgetManager.getAppWidgetIds(new ComponentName(context, widgetClass[i]));
                for (int appWidgetID : widgetIDs)
                {
                    ColorValues colors = colorCollection.getSelectedColors(context, appWidgetID);
                    String label = (colors != null ? colors.getID() : null);
                    String formatString = summary[i] + " (%s)";
                    String summary0 = (label != null ? String.format(formatString, label) : summary[i]);

                    Drawable icon = ContextCompat.getDrawable(context, iconResID[i]);
                    cursor.addRow(WidgetListHelper.createWidgetListRow(context, widgetManager, columns, appWidgetID, widgetClass[i].getName(), summary0, icon));
                }
            }
        }
        return cursor;
    }

    /**
     * queryAlarmInfo
     */
    public Cursor queryAlarmInfo(@Nullable String alarmId, @NonNull Uri uri, @Nullable String[] projection, HashMap<String, String> selectionMap, @Nullable String sortOrder)
    {
        NaturalHourAlarmType alarmInfo = getAlarmInfo(alarmId);
        //Log.d("DEBUG", "queryAlarmInfo: " + alarmId);
        String[] columns = (projection != null ? projection : QUERY_EVENT_INFO_PROJECTION);
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
                        case COLUMN_EVENT_NAME:
                            row[i] = alarms[j];
                            break;

                        case COLUMN_EVENT_TITLE:
                            row[i] = alarmInfo.getAlarmTitle(context, alarms[j]);
                            break;

                        case COLUMN_EVENT_SUMMARY:
                            row[i] = alarmInfo.getAlarmSummary(context, alarms[j]);
                            break;

                        case COLUMN_EVENT_REQUIRES_LOCATION:
                            row[i] = Boolean.toString(alarmInfo.getRequiresLocation(alarms[j]));
                            break;

                        case COLUMN_EVENT_SUPPORTS_REPEATING:
                            row[i] = Boolean.toString(alarmInfo.getSupportsRepeating(alarms[j]));
                            break;

                        case COLUMN_EVENT_PHRASE:
                            row[i] = alarmInfo.getAlarmPhrase(context, alarms[j]);
                            break;

                        case COLUMN_EVENT_PHRASE_GENDER:
                            row[i] = alarmInfo.getAlarmPhraseGender(context, alarms[j]);
                            break;

                        case COLUMN_EVENT_PHRASE_QUANTITY:
                            row[i] = alarmInfo.getAlarmPhraseQuantity(context, alarms[j]);
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
        NaturalHourAlarmType alarmInfo = getAlarmInfo(alarmName);
        //Log.d("DEBUG", "queryAlarmTime: " + alarmName);
        String[] columns = (projection != null ? projection : QUERY_EVENT_CALC_PROJECTION);
        MatrixCursor cursor = new MatrixCursor(columns);

        Context context = getContext();
        if (context != null)
        {
            Object[] row = new Object[columns.length];
            for (int i=0; i<columns.length; i++)
            {
                switch (columns[i])
                {
                    case COLUMN_EVENT_NAME:
                        row[i] = alarmName;
                        break;

                    case COLUMN_EVENT_TIMEMILLIS:
                        row[i] = alarmInfo.calculateAlarmTime(context, alarmName, selectionMap);
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
     * getAlarmInfo
     * @param alarmId id of some type
     * @return NaturalHourAlarmType implementation / typeInfo
     */
    public static NaturalHourAlarmType getAlarmInfo(@Nullable String alarmId)
    {
        if (alarmId != null) {
            for (NaturalHourAlarmType type : getAlarmTypes()) {
                if (type.isOfType(alarmId)) {
                    return type;
                }
            }
        }
        return new NaturalHourAlarm0();
    }

    public static NaturalHourAlarmType[] getAlarmTypes() {
        return new NaturalHourAlarmType[] { new NaturalHourAlarm0(), new NaturalHourAlarm1() };
    }

    public static String[] getAlarmList(Context context)
    {
        ArrayList<String> retValue = new ArrayList<>();
        for (NaturalHourAlarmType type : getAlarmTypes()) {
            retValue.addAll(Arrays.asList(type.getAlarmList(context)));
        }
        return retValue.toArray(new String[0]);
    }

}