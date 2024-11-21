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

package com.forrestguice.suntimes.naturalhour.data;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.forrestguice.suntimes.calculator.core.CalculatorProviderContract;

import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.Callable;

public class NaturalHourCalculator
{
    protected boolean useDefaultLocation = true;
    public void setUseDefaultLocation(boolean value) {
        useDefaultLocation = value;
    }

    /**
     * calculateData
     */
    public boolean calculateData(ContentResolver resolver, @NonNull NaturalHourData data) {
        return calculateData(resolver, data, true, true);
    }
    public boolean calculateData(ContentResolver resolver, @NonNull NaturalHourData data, boolean includeTwilights, boolean includeSolsticeEquinox)
    {
        if (queryData(resolver, data, includeTwilights, includeSolsticeEquinox)) {
            if (data.dayStart > 0 && data.dayEnd > 0) {
                long dayLength = (data.dayEnd - data.dayStart);
                data.dayHourLength = dayLength / 12L;
                for (int i = 0; i < 12; i++) {
                    data.naturalHours[i] = data.dayStart + (data.dayHourLength * i);
                }

                long nightLength = (24 * 60 * 60 * 1000) - dayLength;
                data.nightHourLength = nightLength / 12L;
                for (int i = 0; i < 12; i++) {
                    data.naturalHours[12 + i] = data.dayEnd + (data.nightHourLength * i);
                }

                data.calculated = true;

            } else {
                Log.e(getClass().getSimpleName(), "calculateData: sunrise or sunset does not occur on this day at this location");
                data.calculated = false;
            }
        } else {
            Log.e(getClass().getSimpleName(), "calculateData: failed to query data");
            data.calculated = false;
        }
        return data.calculated;
    }

    /**
     * queryData
     */
    public boolean queryData(ContentResolver resolver, @NonNull NaturalHourData data, boolean queryTwilights, boolean querySolsticeEquinox) {
        if (resolver != null) {
            try {
                long date = data.getDateMillis();

                if (queryTwilights) {
                    data.twilightHours = queryTwilights(resolver, date, data);
                }

                long[] daylight = queryStartEndDay(resolver, date, data);
                data.dayStart = daylight[0];
                data.dayEnd = daylight[1];

                if (querySolsticeEquinox) {
                    solsticeEquinox = queryEquinoxSolsticeDatesWithTimeout(resolver, date, MAX_WAIT_MS);
                    data.solsticeEquinox = solsticeEquinox;
                }

            } catch (SecurityException e) {
                Log.e(getClass().getSimpleName(), "calculateData: Unable to access " + CalculatorProviderContract.AUTHORITY + "! " + e);
                data.calculated = false;
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public long[] queryTwilights(ContentResolver resolver, long date, NaturalHourData data)
    {
        Double latitude = (useDefaultLocation ? null : data.latitude);
        Double longitude = (useDefaultLocation ? null : data.longitude);
        Double altitude = (useDefaultLocation ? null : data.altitude);

        return queryTwilightWithTimeout(resolver, date, latitude, longitude, altitude, new String[] {
                CalculatorProviderContract.COLUMN_SUN_ASTRO_RISE,     // 0
                CalculatorProviderContract.COLUMN_SUN_NAUTICAL_RISE,  // 1
                CalculatorProviderContract.COLUMN_SUN_CIVIL_RISE,     // 2
                CalculatorProviderContract.COLUMN_SUN_ACTUAL_RISE,    // 3
                CalculatorProviderContract.COLUMN_SUN_ACTUAL_SET,     // 4
                CalculatorProviderContract.COLUMN_SUN_CIVIL_SET,      // 5
                CalculatorProviderContract.COLUMN_SUN_NAUTICAL_SET,   // 6
                CalculatorProviderContract.COLUMN_SUN_ASTRO_SET }     // 7
        , MAX_WAIT_MS);
    }

    public long[] queryStartEndDay(ContentResolver resolver, long dateMillis, NaturalHourData data)
    {
        if (data.twilightHours != null && data.twilightHours.length == 8
                && data.twilightHours[3] > 0 && data.twilightHours[4] > 0)
        {
            return new long[] {data.twilightHours[3], data.twilightHours[4]};    // actual sunrise, actual sunset

        } else {
            Double latitude = (useDefaultLocation ? null : data.latitude);
            Double longitude = (useDefaultLocation ? null : data.longitude);
            Double altitude = (useDefaultLocation ? null : data.altitude);
            return querySunriseSunset(resolver, dateMillis, latitude, longitude, altitude);
        }
    }

    public long[] querySunriseSunset(ContentResolver resolver, long dateMillis, Double latitude, Double longitude, Double altitude) {
        return queryTwilightWithTimeout(resolver, dateMillis, latitude, longitude, altitude, new String[] { CalculatorProviderContract.COLUMN_SUN_ACTUAL_RISE, CalculatorProviderContract.COLUMN_SUN_ACTUAL_SET }, MAX_WAIT_MS);
    }

    public long[] queryCivilTwilight(ContentResolver resolver, long date, Double latitude, Double longitude, Double altitude) {
        return queryTwilightWithTimeout(resolver, date, latitude, longitude, altitude, new String[] { CalculatorProviderContract.COLUMN_SUN_CIVIL_RISE, CalculatorProviderContract.COLUMN_SUN_CIVIL_SET }, MAX_WAIT_MS);
    }

    public long[] queryNauticalTwilight(ContentResolver resolver, long date, Double latitude, Double longitude, Double altitude) {
        return queryTwilightWithTimeout(resolver, date, latitude, longitude, altitude, new String[] { CalculatorProviderContract.COLUMN_SUN_NAUTICAL_RISE, CalculatorProviderContract.COLUMN_SUN_NAUTICAL_SET }, MAX_WAIT_MS);
    }

    public long[] queryAstroTwilight(ContentResolver resolver, long date, Double latitude, Double longitude, Double altitude) {
        return queryTwilightWithTimeout(resolver, date, latitude, longitude, altitude, new String[] { CalculatorProviderContract.COLUMN_SUN_ASTRO_RISE, CalculatorProviderContract.COLUMN_SUN_ASTRO_SET }, MAX_WAIT_MS);
    }

    public static final long MAX_WAIT_MS = 1000;
    public long[] queryTwilightWithTimeout(final ContentResolver resolver, final long date, final Double latitude, final Double longitude, final Double altitude, final String[] projection, long timeoutAfter)
    {
        long[] result = ExecutorUtils.getResult("queryTwilight", new Callable<long[]>() {
            public long[] call() throws Exception {
                return queryTwilight(resolver, date, latitude, longitude, altitude, projection);
            }
        }, timeoutAfter);

        if (result == null) {
            result = new long[projection.length];
            Arrays.fill(result, -1);
        }
        return result;
    }

    public long[] queryTwilight(ContentResolver resolver, long date, Double latitude, Double longitude, Double altitude, String[] projection)
    {
        long[] retValue = new long[projection.length];
        Arrays.fill(retValue, -1);

        Uri uri = Uri.parse("content://" + CalculatorProviderContract.AUTHORITY + "/" + CalculatorProviderContract.QUERY_SUN + "/" + date );
        String selection = null;
        String[] selectionArgs = null;
        if (latitude != null && longitude != null && altitude != null)
        {
            selection = CalculatorProviderContract.COLUMN_CONFIG_LATITUDE + "=? AND "
                    + CalculatorProviderContract.COLUMN_CONFIG_LONGITUDE + "=? AND "
                    + CalculatorProviderContract.COLUMN_CONFIG_ALTITUDE + "=?";
            selectionArgs = new String[] { Double.toString(latitude), Double.toString(longitude), Double.toString(altitude) };
            Log.d("DEBUG", "queryTwilight: selectionArgs: " + selection + " .. " + selectionArgs[0] + "," + selectionArgs[1] + " " + selectionArgs[2] );
        }

        Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, null);
        if (cursor != null)
        {
            cursor.moveToFirst();
            for (int i=0; i<projection.length; i++) {
                retValue[i] = cursor.isNull(i) ? -1 : cursor.getLong(i);
            }
            cursor.close();
        }
        return retValue;
    }

    public long[] queryEquinoxSolsticeDatesWithTimeout(final ContentResolver resolver, final long dateMillis, long timeoutAfter)
    {
        long[] result = ExecutorUtils.getResult("queryEquinox", new Callable<long[]>() {
            public long[] call() throws Exception {
                return queryEquinoxSolsticeDates(resolver, dateMillis);
            }
        }, timeoutAfter);

        if (result == null) {
            result = new long[] {-1, -1, -1, -1};
        }
        return result;
    }

    protected long[] solsticeEquinox = new long[] {-1, -1, -1, -1};
    public long[] queryEquinoxSolsticeDates(ContentResolver resolver, long dateMillis)
    {
        if (solsticeEquinox[0] <= 0)
        {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(dateMillis);
            int year = date.get(Calendar.YEAR);

            String[] projection = new String[] { CalculatorProviderContract.COLUMN_SEASON_VERNAL, CalculatorProviderContract.COLUMN_SEASON_SUMMER,
                                                 CalculatorProviderContract.COLUMN_SEASON_AUTUMN, CalculatorProviderContract.COLUMN_SEASON_WINTER };
            Uri uri = Uri.parse("content://" + CalculatorProviderContract.AUTHORITY + "/" + CalculatorProviderContract.QUERY_SEASONS + "/" + (year - 1) + "-" + (year + 1));
            Cursor cursor = resolver.query(uri, projection, null, null, null);
            if (cursor != null)
            {
                cursor.moveToFirst();
                while (!cursor.isAfterLast())
                {
                    for (int i=0; i<solsticeEquinox.length; i++) {
                        long value = cursor.isNull(i) ? -1 : cursor.getLong(i);
                        long dayDiff = (value - dateMillis) / (24L * 60L * 60L * 1000L);
                        solsticeEquinox[i] = value > solsticeEquinox[i] && dayDiff < 365 ? value : solsticeEquinox[i];
                    }
                    cursor.moveToNext();
                }
                cursor.close();
            }
        }
        return solsticeEquinox;
    }

}