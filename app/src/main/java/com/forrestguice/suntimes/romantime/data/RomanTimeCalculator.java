// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2020 Forrest Guice
    This file is part of RomanTime.

    RomanTime is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    RomanTime is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with RomanTime.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.forrestguice.suntimes.romantime.data;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.forrestguice.suntimes.calculator.core.CalculatorProviderContract;

public class RomanTimeCalculator
{
    /**
     * calculateData
     */
    public boolean calculateData(ContentResolver resolver, @NonNull RomanTimeData data)
    {
        if (queryData(resolver, data))
        {
            if (data.sunrise > 0 && data.sunset > 0)
            {
                long dayLength = (data.sunset - data.sunrise);
                data.dayHourLength = dayLength / 12L;
                for (int i=0; i<12; i++) {
                    data.romanHours[i] = data.sunrise + (data.dayHourLength * i);
                }

                long nightLength = (24 * 60 * 60 * 1000) - dayLength;
                data.nightHourLength = nightLength / 12L;
                for (int i=0; i<12; i++) {
                    data.romanHours[12 + i] = data.sunset + (data.nightHourLength * i);
                }

                data.calculated = true;

            } else {
                Log.e(getClass().getSimpleName(), "calculateData: sunrise or sunset does not occur on this day at this location");
                data.calculated = false;
            }
        }
        return data.calculated;
    }

    /**
     * queryData
     */
    public boolean queryData(ContentResolver resolver, @NonNull RomanTimeData data)
    {
        if (resolver != null)
        {
            try {
                querySunriseSunset(resolver, data);

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

    protected void querySunriseSunset(ContentResolver resolver, RomanTimeData data)
    {
        String[] projection = new String[] { CalculatorProviderContract.COLUMN_SUN_ACTUAL_RISE, CalculatorProviderContract.COLUMN_SUN_ACTUAL_SET };
        Uri uri = Uri.parse("content://" + CalculatorProviderContract.AUTHORITY + "/" + CalculatorProviderContract.QUERY_SUN + "/" + data.getDateMillis() );
        Cursor cursor = resolver.query(uri, projection, null, null, null);
        if (cursor != null)
        {
            cursor.moveToFirst();
            data.sunrise = cursor.isNull(0) ? -1 : cursor.getLong(0);
            data.sunset = cursor.isNull(1) ? -1 : cursor.getLong(1);
            cursor.close();
        }
    }




}
