/**
    Copyright (C) 2024 Forrest Guice
    This file is part of NaturalHour.

    NaturalHour is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    NaturalHour is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with NaturalHour.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.forrestguice.suntimes.naturalhour.data;

import java.util.TimeZone;

public class EquinoctialHours
{
    public static final String JULIAN_HOURS = "Julian";           // starts at noon
    public static final String BABYLONIAN_HOURS = "Babylonian";   // starts at sunrise
    public static final String ITALIAN_HOURS = "Italian";         // starts at sunset

    public static Boolean is24(String id, Boolean defaultValue)
    {
        switch (id)
        {
            case JULIAN_HOURS:
            case BABYLONIAN_HOURS:
            case ITALIAN_HOURS:
                return true;

            default:
                return defaultValue;
        }
    }

    private static final double TWO_PI = 2d * Math.PI;
    private static final long DAY_MILLIS = 24 * 60 * 60 * 1000;

    public static long getTimeOffset(TimeZone timezone, NaturalHourData data, long defaultValue, double startAngle0)
    {
        double startAngleOffset = getStartAngleOffset(timezone, data, defaultValue, startAngle0);
        return (long)(-DAY_MILLIS * (startAngleOffset / TWO_PI));
    }

    public static double getStartAngleOffset(TimeZone timezone, NaturalHourData data, double defaultValue, double startAngle0)
    {
        switch (timezone.getID())
        {
            case JULIAN_HOURS:
                return startAngle0 + data.getAngle(data.getNaturalHours()[6], timezone);

            case BABYLONIAN_HOURS:
                return startAngle0 + data.getAngle(data.getTwilightTimes()[3], timezone);

            case ITALIAN_HOURS:
                return startAngle0 + data.getAngle(data.getTwilightTimes()[4], timezone);

            default:
                return defaultValue;
        }
    }
}

