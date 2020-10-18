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

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.ContentProvider;
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
import com.forrestguice.suntimes.naturalhour.BuildConfig;
import com.forrestguice.suntimes.naturalhour.ui.widget.NaturalHourWidget_3x2;
import com.forrestguice.suntimes.naturalhour.ui.widget.NaturalHourWidget_3x2_ConfigActivity;
import com.forrestguice.suntimes.naturalhour.ui.widget.NaturalHourWidget_4x3;
import com.forrestguice.suntimes.naturalhour.ui.widget.NaturalHourWidget_4x3_ConfigActivity;
import com.forrestguice.suntimes.naturalhour.ui.widget.NaturalHourWidget_5x3;
import com.forrestguice.suntimes.naturalhour.ui.widget.NaturalHourWidget_5x3_ConfigActivity;

import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.AUTHORITY;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_CONFIG_APP_VERSION;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_CONFIG_APP_VERSION_CODE;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_CONFIG_PROVIDER_VERSION;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_CONFIG_PROVIDER_VERSION_CODE;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_WIDGET_APPWIDGETID;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_WIDGET_CLASS;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_WIDGET_CONFIGCLASS;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_WIDGET_LABEL;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_WIDGET_PACKAGENAME;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.COLUMN_WIDGET_SUMMARY;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.QUERY_CONFIG;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.QUERY_CONFIG_PROJECTION;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.QUERY_WIDGET;
import static com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract.QUERY_WIDGET_PROJECTION;

public class NaturalHourProvider extends ContentProvider
{
    private static final int URIMATCH_CONFIG = 0;
    private static final int URIMATCH_WIDGET = 10;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static
    {
        uriMatcher.addURI(AUTHORITY, QUERY_CONFIG, URIMATCH_CONFIG);
        uriMatcher.addURI(AUTHORITY, QUERY_WIDGET, URIMATCH_WIDGET);
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

        } else Log.d("DEBUG", "context is null!");
        return cursor;
    }

    /**
     * queryWidget
     */
    public Cursor queryWidgets(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder)
    {
        String[] columns = (projection != null ? projection : QUERY_WIDGET_PROJECTION);
        MatrixCursor cursor = new MatrixCursor(columns);

        Context context = getContext();
        if (context != null)
        {
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
            Class[] widgetClass = new Class[] { NaturalHourWidget_3x2.class, NaturalHourWidget_4x3.class, NaturalHourWidget_5x3.class };
            Class[] configClass = new Class[] { NaturalHourWidget_3x2_ConfigActivity.class, NaturalHourWidget_4x3_ConfigActivity.class, NaturalHourWidget_5x3_ConfigActivity.class };
            String[] summary = new String[] {"Clock Widget (3x2)", "Clock Widget (4x3)", "Clock Widget (5x3)"};
            for (int j=0; j<widgetClass.length; j++)
            {
                int[] widgetIDs = widgetManager.getAppWidgetIds(new ComponentName(context, widgetClass[j]));
                for (int appWidgetID : widgetIDs)
                {
                    AppWidgetProviderInfo info = widgetManager.getAppWidgetInfo(appWidgetID);
                    Object[] row = new Object[columns.length];
                    for (int i=0; i<columns.length; i++)
                    {
                        switch (columns[i])
                        {
                            case COLUMN_WIDGET_PACKAGENAME:
                                row[i] = context.getPackageName();
                                break;

                            case COLUMN_WIDGET_APPWIDGETID:
                                row[i] = appWidgetID;
                                break;

                            case COLUMN_WIDGET_CLASS:
                                row[i] = widgetClass[j].getName();
                                break;

                            case COLUMN_WIDGET_CONFIGCLASS:
                                row[i] = configClass[j].getName();
                                break;

                            case COLUMN_WIDGET_LABEL:
                                row[i] = info.label;
                                break;

                            case COLUMN_WIDGET_SUMMARY:
                                row[i] = summary[j];
                                break;

                            default:
                                row[i] = null;
                                break;
                        }
                    }
                    cursor.addRow(row);
                }
            }

        } else Log.d("DEBUG", "context is null!");
        return cursor;
    }
}