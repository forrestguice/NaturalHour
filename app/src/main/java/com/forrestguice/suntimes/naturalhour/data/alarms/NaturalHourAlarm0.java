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

package com.forrestguice.suntimes.naturalhour.data.alarms;

import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.alarm.AlarmHelper;
import com.forrestguice.suntimes.naturalhour.AppSettings;
import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourCalculator;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourData;
import com.forrestguice.suntimes.naturalhour.ui.NaturalHourFragment;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.forrestguice.suntimes.alarm.AlarmEventContract.EXTRA_ALARM_NOW;
import static com.forrestguice.suntimes.alarm.AlarmEventContract.EXTRA_ALARM_OFFSET;
import static com.forrestguice.suntimes.alarm.AlarmEventContract.EXTRA_ALARM_REPEAT;
import static com.forrestguice.suntimes.alarm.AlarmEventContract.EXTRA_ALARM_REPEAT_DAYS;
import static com.forrestguice.suntimes.alarm.AlarmEventContract.EXTRA_LOCATION_ALT;
import static com.forrestguice.suntimes.alarm.AlarmEventContract.EXTRA_LOCATION_LAT;
import static com.forrestguice.suntimes.alarm.AlarmEventContract.EXTRA_LOCATION_LON;

/**
 * Alarm at Hour and Moment.
 */
public class NaturalHourAlarm0 implements NaturalHourAlarmType
{
    /**
     * @param alarmID alarmID; hourMode_hourNum or hourMode_hourNum_momentNum
     * @return null if alarmID is invalid; or int[3] .. [CalculatorMode, hourNum, momentNum], where hourNum [0,24) and momentNum [0,40)
     */
    public static int[] alarmIdToNaturalHour(@Nullable String alarmID)
    {
        String[] parts = alarmID != null ? alarmID.split("_") : new String[0];
        if (parts.length == 2 || parts.length == 3)
        {
            try {
                int momentNum = (parts.length == 3) ? Integer.parseInt(parts[2]) : 0;
                int hourNum = Integer.parseInt(parts[1]);
                int hourMode = Integer.parseInt(parts[0]);

                if (hourNum >= 0 && hourNum < 24) {
                    return new int[] { hourMode, hourNum, momentNum };

                } else {
                    Log.e("NaturalHourProvider", "alarmToNaturalHour: invalid alarmID: " + alarmID + "; hour out-of-range: " + hourNum);
                }
            } catch (NumberFormatException e) {
                Log.e("NaturalHourProvider", "alarmToNaturalHour: invalid alarmID: " + alarmID + " .. " + e);
            }
        }
        return null;
    }

    public static String naturalHourToAlarmID(int hourMode, int hourNum, int momentNum)
    {
        if (hourNum < 0 || hourNum >= 24) {
            throw new IndexOutOfBoundsException("hourNum must be [0,24); " + hourNum);
        }
        if (momentNum < 0 || momentNum >= 40) {
            throw new IndexOutOfBoundsException("momentNum [0,40); " + momentNum);
        }
        return hourMode + "_" + hourNum + "_" + momentNum;
    }

    @Override
    public String[] getAlarmList(@NonNull Context context) {
        return getAlarmList(AppSettings.getClockIntValue(context, NaturalHourClockBitmap.VALUE_HOURMODE, NaturalHourClockBitmap.HOURMODE_DEFAULT));
    }
    public static String[] getAlarmList(int hourMode)
    {
        //String[] retValue = new String[24];
        //for (int i=0; i<retValue.length; i++) {
        //    retValue[i] = naturalHourToAlarmID(hourMode, i, 0);
        //}
        //return retValue;
        return new String[0];
    }

    @Override
    public String getAlarmTitle(Context context, @Nullable String alarmID)
    {
        int[] hour = alarmIdToNaturalHour(alarmID);
        if (hour != null) {
            return context.getString(R.string.alarm_title_format, NaturalHourFragment.naturalHourPhrase(context, hour[0], hour[1], hour[2]));
        } else return null;
    }

    @Override
    public String getAlarmSummary(Context context, @Nullable String alarmID) {
        if (alarmIdToNaturalHour(alarmID) != null) {
            return context.getString(R.string.alarm_summary_format);
        } else return null;
    }

    @Override
    public String getAlarmPhrase(Context context, @Nullable String alarmID) {
        int[] hour = alarmIdToNaturalHour(alarmID);
        if (hour != null) {
            return NaturalHourFragment.naturalHourPhrase(context, hour[0], hour[1], hour[2]);
        } else return null;
    }
    @Override
    public String getAlarmPhraseGender(Context context, @Nullable String alarmID) {
        return context.getString(R.string.time_gender);
    }
    @Override
    public int getAlarmPhraseQuantity(Context context, @Nullable String alarmID) {
        int[] hour = alarmIdToNaturalHour(alarmID);
        if (hour != null) {
            return 1 + (hour[1] % 12);
        } else return 1;
    }

    @Override
    public boolean getRequiresLocation(@Nullable String alarmID) {
        return true;
    }

    @Override
    public boolean getSupportsRepeating(@Nullable String alarmID) {
        return true;
    }

    @Override
    public long calculateAlarmTime(@NonNull Context context, @Nullable String alarmID, HashMap<String, String> selectionMap)
    {
        int[] hour = alarmIdToNaturalHour(alarmID);
        ContentResolver resolver = context.getContentResolver();
        if (hour != null && resolver != null)
        {
            Calendar now = AlarmHelper.getNowCalendar(selectionMap.get(EXTRA_ALARM_NOW));
            long nowMillis = now.getTimeInMillis();
            float momentRatio = (float)hour[2] / 39f;

            String offsetString = selectionMap.get(EXTRA_ALARM_OFFSET);
            long offset = offsetString != null ? Long.parseLong(offsetString) : 0L;

            boolean repeating = Boolean.parseBoolean(selectionMap.get(EXTRA_ALARM_REPEAT));
            ArrayList<Integer> repeatingDays = AlarmHelper.getRepeatDays(selectionMap.get(EXTRA_ALARM_REPEAT_DAYS));

            String latitudeString = selectionMap.get(EXTRA_LOCATION_LAT);
            String longitudeString = selectionMap.get(EXTRA_LOCATION_LON);
            String altitudeString = selectionMap.get(EXTRA_LOCATION_ALT);
            Double latitude = latitudeString != null ? Double.parseDouble(latitudeString) : null;
            Double longitude = longitudeString != null ? Double.parseDouble(longitudeString) : null;
            double altitude = altitudeString != null ? Double.parseDouble(altitudeString) : 0;
            if (latitude == null || longitude == null)
            {
                SuntimesInfo info = SuntimesInfo.queryInfo(context);
                latitude = Double.parseDouble(info.location[1]);
                longitude = Double.parseDouble(info.location[2]);
                altitude = Double.parseDouble(info.location[3]);
            }

            Log.d("DEBUG", "calculateAlarmTime: now: " + nowMillis + ", offset: " + offset + ", repeat: " + repeating + ", repeatDays: " + selectionMap.get(EXTRA_ALARM_REPEAT_DAYS)
                    + ", latitude: " + latitude + ", longitude: " + longitude + ", altitude: " + altitude + " .. moment: " + momentRatio + " .. " + hour[2] + " .. " + alarmID);

            NaturalHourCalculator calculator = NaturalHourClockBitmap.getCalculator(hour[0]);
            calculator.setUseDefaultLocation(false);

            Calendar alarmTime = Calendar.getInstance();
            Calendar eventTime;

            Calendar day = Calendar.getInstance();
            NaturalHourData data = new NaturalHourData(day.getTimeInMillis(), latitude, longitude, altitude);
            calculator.calculateData(resolver, data, false, false);
            eventTime = data.getNaturalHour(hour[1], momentRatio);
            if (eventTime != null)
            {
                eventTime.set(Calendar.SECOND, 0);
                alarmTime.setTimeInMillis(eventTime.getTimeInMillis() + offset);
            }

            int c = 0;
            Set<Long> timestamps = new HashSet<>();
            while (now.after(alarmTime)
                    || eventTime == null
                    || (repeating && !repeatingDays.contains(eventTime.get(Calendar.DAY_OF_WEEK))))
            {
                if (!timestamps.add(alarmTime.getTimeInMillis()) && c > 365) {
                    Log.e("NaturalHourProvider", "updateAlarmTime: encountered same timestamp twice! (breaking loop)");
                    return -1L;
                }

                Log.w("NaturalHourProvider", "updateAlarmTime: advancing by 1 day..");
                day.add(Calendar.DAY_OF_YEAR, 1);
                data = new NaturalHourData(day.getTimeInMillis(), latitude, longitude, altitude);
                calculator.calculateData(resolver, data, false, false);
                eventTime = data.getNaturalHour(hour[1], momentRatio);
                if (eventTime != null)
                {
                    eventTime.set(Calendar.SECOND, 0);
                    alarmTime.setTimeInMillis(eventTime.getTimeInMillis() + offset);
                }
                c++;
            }

            return eventTime.getTimeInMillis();

        } else {
            return -1L;
        }
    }

}