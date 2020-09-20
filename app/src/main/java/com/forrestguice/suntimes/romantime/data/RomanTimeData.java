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

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.Calendar;
import java.util.TimeZone;

public class RomanTimeData implements Parcelable
{
    public static final String KEY_DATE = "date";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_ALTITUDE = "altitude";

    public static final String KEY_SUNRISE = "sunrise";
    public static final String KEY_SUNSET = "sunset";
    public static final String KEY_ROMAN_HOURS = "romanhours";
    public static final String KEY_DAY_HOUR_LENGTH = "dayhourlength";
    public static final String KEY_NIGHT_HOUR_LENGTH = "nighthourlength";

    public static final String KEY_CALCULATED = "iscalculated";

    public static final long DAY_MILLIS = 24 * 60 * 60 * 1000;

    protected long date;
    protected double latitude, longitude, altitude;
    protected long sunrise, sunset;
    protected long dayHourLength, nightHourLength;
    protected long[] romanHours = new long[24];    // 24 roman hours; 1 sunrise; 13 sunset
    protected boolean calculated = false;

    public RomanTimeData(long date, double latitude, double longitude, double altitude)
    {
        this.calculated = false;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public RomanTimeData(long date, String latitude, String longitude, String altitude) {
        this.calculated = false;
        this.date = date;
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
        this.altitude = Double.parseDouble(altitude);
    }

    public RomanTimeData(ContentValues values) {
        initFromContentValues(values);
    }

    private RomanTimeData(Parcel in) {
        initFromParcel(in);
    }

    public void initFromContentValues(ContentValues values)
    {
        this.calculated = values.getAsBoolean(KEY_CALCULATED);
        this.date = values.getAsLong(KEY_DATE);
        this.latitude = values.getAsDouble(KEY_LATITUDE);
        this.longitude = values.getAsDouble(KEY_LONGITUDE);
        this.altitude = values.getAsDouble(KEY_ALTITUDE);
        this.sunrise = values.getAsLong(KEY_SUNRISE);
        this.sunset = values.getAsLong(KEY_SUNSET);
        this.dayHourLength = values.getAsLong(KEY_DAY_HOUR_LENGTH);
        this.nightHourLength = values.getAsLong(KEY_NIGHT_HOUR_LENGTH);
        for (int i=0; i<romanHours.length; i++) {
            romanHours[i] = values.getAsLong(KEY_ROMAN_HOURS + i);
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
        values.put(KEY_SUNRISE, sunrise);
        values.put(KEY_SUNSET, sunset);
        values.put(KEY_DAY_HOUR_LENGTH, dayHourLength);
        values.put(KEY_NIGHT_HOUR_LENGTH, nightHourLength);
        for (int i=0; i<romanHours.length; i++) {
            values.put(KEY_ROMAN_HOURS + i, romanHours[i]);
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
        sunrise = in.readLong();
        sunset = in.readLong();
        dayHourLength = in.readLong();
        nightHourLength = in.readLong();
        in.readLongArray(romanHours);
    }
    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        out.writeByte((byte)(calculated ? 1 : 0));
        out.writeLong(date);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeDouble(altitude);
        out.writeLong(sunrise);
        out.writeLong(sunset);
        out.writeLong(dayHourLength);
        out.writeLong(nightHourLength);
        out.writeLongArray(romanHours);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<RomanTimeData> CREATOR = new Parcelable.Creator<RomanTimeData>()
    {
        public RomanTimeData createFromParcel(Parcel in) {
            return new RomanTimeData(in);
        }
        public RomanTimeData[] newArray(int size) {
            return new RomanTimeData[size];
        }
    };

    public boolean isCalculated() {
        return calculated;
    }

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
            case KEY_SUNRISE: return sunrise;
            case KEY_SUNSET: return sunset;

            case KEY_DATE:
            default: return date;
        }
    }

    /**
     * @return millis
     */
    public double getDayHourLength() {
        return dayHourLength;
    }
    public double getDayHourAngle() {
        return (dayHourLength / (double) DAY_MILLIS) * (2 * Math.PI);
    }

    /**
     * @return millis
     */
    public double getNightHourLength() {
        return nightHourLength;
    }

    /**
     * @return radians
     */
    public double getNightHourAngle() {
        return (nightHourLength / (double) DAY_MILLIS) * (2 * Math.PI);
    }

    /**
     * @param timeMillis
     * @param timezone
     * @return radians
     */
    public static double getAngle( long timeMillis, TimeZone timezone )
    {
        Calendar t0 = Calendar.getInstance(timezone);
        t0.setTimeInMillis(timeMillis);
        return RomanTimeData.getAngle( t0.get(Calendar.HOUR_OF_DAY), t0.get(Calendar.MINUTE), t0.get(Calendar.SECOND));
    }

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

    public static int findRomanHour(Calendar now, RomanTimeData data)
    {
        double dayAngle =  data.getDayHourAngle();
        double nightAngle = data.getNightHourAngle();
        double timeAngle = RomanTimeData.getAngle(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND));
        TimeZone timezone = now.getTimeZone();

        for (int i=0; i<data.romanHours.length; i++)
        {
            double a0 = RomanTimeData.getAngle(data.romanHours[i], timezone);
            double a1 = a0 + (i < 12 ? dayAngle : nightAngle);
            if (timeAngle >= a0 && timeAngle < a1) {
                return (i + 1);
            }
        }
        return 0;
    }

    public long[] getRomanHours() {
        return romanHours;
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
