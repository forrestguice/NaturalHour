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
 * An alternate definition of "natural hour" where the day begins and ends at civil twilight (6 degrees).
 */
public class NaturalHourCalculator1 extends NaturalHourCalculator
{
    @Override
    public long[] queryStartEndDay(ContentResolver resolver, long dateMillis, NaturalHourData data) {

        if (data.twilightHours != null && data.twilightHours.length == 8
                && data.twilightHours[2] > 0 && data.twilightHours[5] > 0)
        {
            return new long[] {data.twilightHours[2], data.twilightHours[5]};   // civil sunrise, civil sunset

        } else {
            return queryCivilTwilight(resolver, dateMillis, data.latitude, data.longitude, data.altitude);
        }
    }

}
