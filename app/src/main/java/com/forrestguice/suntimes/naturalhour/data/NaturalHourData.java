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

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.TimeZone;

public class NaturalHourData implements Parcelable
{
    public static final String KEY_DATE = "date";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_ALTITUDE = "altitude";

    public static final String KEY_TWILIGHT = "twilights";
    public static final String KEY_DAY_START = "daystart";
    public static final String KEY_DAY_END = "dayend";
    public static final String KEY_NATURAL_HOURS = "naturalhours";

    public static final String KEY_DAY_HOUR_LENGTH = "dayhourlength";
    public static final String KEY_NIGHT_HOUR_LENGTH = "nighthourlength";

    public static final String KEY_SOLSTICE_EQUINOX = "solsticeequinox";

    public static final String KEY_CALCULATED = "iscalculated";

    public static final long DAY_MILLIS = 24 * 60 * 60 * 1000;

    protected long date;
    protected double latitude, longitude, altitude;
    protected long dayStart, dayEnd;
    protected long dayHourLength, nightHourLength;
    protected long[] twilightHours = new long[8];    // rising [0-3] (astro, nautical, civil, actual), setting [4-7] (actual, civil, nautical, astro)
    protected long[] naturalHours = new long[24];    // 24 natural hours; 0 sunrise; 12 sunset
    protected long[] solsticeEquinox = new long[4];  // spring, summer, autumn, winter
    protected boolean calculated = false;

    public NaturalHourData(long date, double latitude, double longitude, double altitude)
    {
        this.calculated = false;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public NaturalHourData(long date, String latitude, String longitude, String altitude) {
        this.calculated = false;
        this.date = date;
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
        this.altitude = Double.parseDouble(altitude);
    }

    public NaturalHourData(ContentValues values) {
        initFromContentValues(values);
    }

    private NaturalHourData(Parcel in) {
        initFromParcel(in);
    }

    public void initFromContentValues(ContentValues values)
    {
        this.calculated = values.getAsBoolean(KEY_CALCULATED);
        this.date = values.getAsLong(KEY_DATE);
        this.latitude = values.getAsDouble(KEY_LATITUDE);
        this.longitude = values.getAsDouble(KEY_LONGITUDE);
        this.altitude = values.getAsDouble(KEY_ALTITUDE);
        this.dayStart = values.getAsLong(KEY_DAY_START);
        this.dayEnd = values.getAsLong(KEY_DAY_END);
        this.dayHourLength = values.getAsLong(KEY_DAY_HOUR_LENGTH);
        this.nightHourLength = values.getAsLong(KEY_NIGHT_HOUR_LENGTH);
        for (int i = 0; i< twilightHours.length; i++) {
            twilightHours[i] = values.getAsLong(KEY_TWILIGHT + i);
        }
        for (int i = 0; i< naturalHours.length; i++) {
            naturalHours[i] = values.getAsLong(KEY_NATURAL_HOURS + i);
        }
        for (int i=0; i<solsticeEquinox.length; i++) {
            solsticeEquinox[i] = values.getAsLong(KEY_SOLSTICE_EQUINOX + i);
        }
    }
    public ContentValues asContentValues()
    {
        ContentValues values = new ContentValues();
        values.put(KEY_CALCULATED, calculated);
        values.put(KEY_DATE, date);
        values.put(KEY_LATITUDE, latitude);
        values.put(KEY_LONGITUDE, longitude);
        values.put(KEY_ALTITUDE, altitude);
        values.put(KEY_DAY_START, dayStart);
        values.put(KEY_DAY_END, dayEnd);
        values.put(KEY_DAY_HOUR_LENGTH, dayHourLength);
        values.put(KEY_NIGHT_HOUR_LENGTH, nightHourLength);
        for (int i = 0; i< twilightHours.length; i++) {
            values.put(KEY_TWILIGHT + i, twilightHours[i]);
        }
        for (int i = 0; i< naturalHours.length; i++) {
            values.put(KEY_NATURAL_HOURS + i, naturalHours[i]);
        }
        for (int i=0; i<solsticeEquinox.length; i++) {
            values.put(KEY_SOLSTICE_EQUINOX + i, solsticeEquinox[i]);
        }
        return values;
    }

    public void initFromParcel(Parcel in)
    {
        calculated = (in.readByte() != 0);
        date = in.readLong();
        latitude = in.readDouble();
        longitude = in.readDouble();
        altitude = in.readDouble();
        dayStart = in.readLong();
        dayEnd = in.readLong();
        dayHourLength = in.readLong();
        nightHourLength = in.readLong();
        in.readLongArray(twilightHours);
        in.readLongArray(naturalHours);
        in.readLongArray(solsticeEquinox);
    }
    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        out.writeByte((byte)(calculated ? 1 : 0));
        out.writeLong(date);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeDouble(altitude);
        out.writeLong(dayStart);
        out.writeLong(dayEnd);
        out.writeLong(dayHourLength);
        out.writeLong(nightHourLength);
        out.writeLongArray(twilightHours);
        out.writeLongArray(naturalHours);
        out.writeLongArray(solsticeEquinox);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<NaturalHourData> CREATOR = new Parcelable.Creator<NaturalHourData>()
    {
        public NaturalHourData createFromParcel(Parcel in) {
            return new NaturalHourData(in);
        }
        public NaturalHourData[] newArray(int size) {
            return new NaturalHourData[size];
        }
    };

    public boolean isCalculated() {
        return calculated;
    }

    /**
     * @return the date millis used to initialize data
     */
    public long getDateMillis() {
        return date;
    }
    public long getDateMillis(@Nullable String key)
    {
        if (key == null) {
            return date;
        }
        switch (key)
        {
            case KEY_DAY_START: return dayStart;
            case KEY_DAY_END: return dayEnd;
            case KEY_DATE: default: return date;
        }
    }

    /**
     * @return length of an hour (day) in millis
     */
    public double getDayHourLength() {
        return dayHourLength;
    }

    /**
     * @return length of an hour (day) in radians
     */
    public double getDayHourAngle() {
        return (dayHourLength / (double) DAY_MILLIS) * (2 * Math.PI);
    }

    /**
     * @return length of an hour (night) in millis
     */
    public double getNightHourLength() {
        return nightHourLength;
    }

    /**
     * @return length of an hour (night) in radians
     */
    public double getNightHourAngle() {
        return (nightHourLength / (double) DAY_MILLIS) * (2 * Math.PI);
    }

    /**
     * @return millis
     */
    public double getDayMomentLength() {
        return getDayHourLength() / 40d;
    }
    public double getNightMomentLength() {
        return getNightHourLength() / 40d;
    }

    /**
     * @param timeMillis
     * @param timezone
     * @return radians
     */
    public double getAngle( long timeMillis, TimeZone timezone )
    {
        calendar.setTimeZone(timezone);
        calendar.setTimeInMillis(timeMillis);
        return NaturalHourData.getAngle( calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }
    private Calendar calendar = Calendar.getInstance();

    /**
     * @param hour HOUR_OF_DAY
     * @param minute MINUTE
     * @param second SECOND
     * @return radians
     */
    public static double getAngle(int hour, int minute, int second)
    {
        double twoPI = 2 * Math.PI;
        return (-Math.PI / 2d) +
                ((hour / 24d) * twoPI) +
                ((minute / (60d * 24d)) * twoPI) +
                ((second / (60d * 60d * 24d)) * twoPI);
    }

    public static double simplifyAngle(double radians)
    {
        double fullCircle = 2 * Math.PI;
        double retValue = radians;
        while (retValue < 0) {
            retValue += fullCircle;
        }
        while (retValue > fullCircle) {
            retValue -= fullCircle;
        }
        return retValue;
    }


    public static int findNaturalHour(Calendar now, NaturalHourData data)
    {
        double dayAngle =  data.getDayHourAngle();
        double nightAngle = data.getNightHourAngle();
        double timeAngle = simplifyAngle(NaturalHourData.getAngle(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND)));
        TimeZone timezone = now.getTimeZone();

        for (int i = 0; i<data.naturalHours.length; i++)
        {
            double hourAngle = i < 12 ? dayAngle : nightAngle;
            double a0 = simplifyAngle(data.getAngle(data.naturalHours[i], timezone));
            double a1 = a0 + hourAngle;
            if (timeAngle >= a0 && timeAngle < a1) {
                return (i + 1);
            }
        }
        return 0;
    }

    /**
     * @return long[8] containing twilight times in order of appearance; [0-3] rising (astro, nautical, civil, actual); [4-7] setting (actual, civil, nautical, astro)
     */
    public long[] getTwilightTimes() {
        return twilightHours;
    }

    /**
     * @return long[24] containing natural hours; daytime: [0-11], nighttime: [12-23]; dayStart: 0, dayEnd: 12, midday: 6, midnight: 23
     */
    public long[] getNaturalHours() {
        return naturalHours;
    }

    /**
     * @param i index into array returned by getNaturalHours()
     * @return Calendar for natural hour or null if dne
     */
    public Calendar getNaturalHour(int i)
    {
        if (i < 0 || i >= naturalHours.length) {
            throw new IndexOutOfBoundsException("i must be [0," + naturalHours.length + "); " + i);
        }

        if (calculated && naturalHours[i] > 0)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(naturalHours[i]);
            return calendar;
        }
        return null;
    }

    /**
     * @return long[4]; spring, summer, fall, winter
     */
    public long[] getEquinoxSolsticeDates() {
        return solsticeEquinox;
    }

    /**
     * @return decimal degrees
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return decimal degrees
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @return meters
     */
    public double getAltitude() {
        return altitude;
    }
}
