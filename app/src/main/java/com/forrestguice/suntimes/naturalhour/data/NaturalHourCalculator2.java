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

/**
 * An alternate definition of "natural hour" where the day is counted sunset to sunset.
 */
public class NaturalHourCalculator2 extends NaturalHourCalculator
{
    @Override
    public long[] queryStartEndDay(ContentResolver resolver, long dateMillis, NaturalHourData data)
    {
        long[] riseset0 = querySunriseSunset(resolver, dateMillis - (24 * 60 * 60 * 1000));
        long[] riseset1 = querySunriseSunset(resolver, dateMillis);
        return new long[] {riseset1[0], riseset0[1] + 24 * 60 * 60 * 1000};
    }
}
