// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2022 Forrest Guice
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

import java.util.Date;
import java.util.TimeZone;

public class TimeZoneWrapper extends TimeZone
{
    protected final boolean inDst;
    protected final TimeZone timezone;

    /**
     * @param timezone TimeZone object to wrap
     * @param dst true force DST adjustment on; false force DST adjustment off
     */
    public TimeZoneWrapper(TimeZone timezone, boolean dst)
    {
        this.timezone = timezone;
        this.inDst = dst;
    }

    @Override
    public boolean useDaylightTime() {
        return inDst;
    }

    @Override
    public boolean inDaylightTime(Date date) {
        return inDst;
    }

    @Override
    public int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds) {
        return timezone.getOffset(era, year, month, day, dayOfWeek, milliseconds);
    }

    @Override
    public void setRawOffset(int offsetMillis) {
        timezone.setRawOffset(offsetMillis);
    }

    @Override
    public int getRawOffset() {
        return timezone.getRawOffset();
    }
}