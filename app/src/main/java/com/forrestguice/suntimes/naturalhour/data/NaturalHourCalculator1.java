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

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.forrestguice.suntimes.calculator.core.CalculatorProviderContract;

/**
 * An alternate definition of "natural hour" where the day begins and ends at civil twilight (6 degrees).
 */
public class NaturalHourCalculator1 extends NaturalHourCalculator
{
    @Override
    public long[] queryStartEndDay(ContentResolver resolver, long dateMillis) {
        return queryCivilTwilight(resolver, dateMillis);
    }

    public long[] queryCivilTwilight(ContentResolver resolver, long date)
    {
        long[] retValue = new long[] {-1, -1};
        String[] projection = new String[] { CalculatorProviderContract.COLUMN_SUN_CIVIL_RISE, CalculatorProviderContract.COLUMN_SUN_CIVIL_SET };
        Uri uri = Uri.parse("content://" + CalculatorProviderContract.AUTHORITY + "/" + CalculatorProviderContract.QUERY_SUN + "/" + date );
        Cursor cursor = resolver.query(uri, projection, null, null, null);
        if (cursor != null)
        {
            cursor.moveToFirst();
            retValue[0] = cursor.isNull(0) ? -1 : cursor.getLong(0);
            retValue[1] = cursor.isNull(1) ? -1 : cursor.getLong(1);
            cursor.close();
        }
        return retValue;
    }
}
