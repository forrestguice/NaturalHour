// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2024 Forrest Guice
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

import android.content.Context;
import android.util.Log;

import com.forrestguice.suntimes.naturalhour.AppSettings;
import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourData;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Alarm at night watch i of n.
 */
public class NaturalHourAlarm1 extends NaturalHourAlarm0
{
    public static final String TYPE_PREFIX = "N";

    @Override
    public String getTypeID() {
        return NaturalHourAlarm1.TYPE_PREFIX;
    }

    /**
     * @param hourMode mode
     * @param i [1, n]
     * @param n number of night watches
     * @return natural sounding phrase
     */
    private String nightWatchPhrase(Context context, int hourMode, int i, int n) {
        return context.getString(R.string.nightwatch_phrase2, "" + i, "" + n);
    }

    /**
     * @param alarmID alarmID; N_hourMode_nightWatchNumber_numNightWatches
     * @return null if alarmID is invalid; or int[3] .. [CalculatorMode, nightWatchNumber, numNightWatches]
     */
    @Override
    public int[] fromAlarmID(@Nullable String alarmID)
    {
        String[] parts = alarmID != null ? alarmID.split("_") : new String[0];
        if (parts.length == 3 || parts.length == 4)
        {
            try {
                int n = Integer.parseInt(parts[3]);
                int i = Integer.parseInt(parts[2]);
                int hourMode = Integer.parseInt(parts[1]);
                return new int[] { hourMode, i, n };

            } catch (NumberFormatException e) {
                Log.e("NaturalHourProvider", "alarmToNightWatch: invalid alarmID: " + alarmID + " .. " + e);
            }
        }
        return null;
    }

    @Override
    public String toAlarmID(int[] params) {
        return toAlarmID(params[0], params[1], params[2]);
    }
    public String toAlarmID(int hourMode, int nightWatchNumber, int numNightWatches) {
        return NaturalHourAlarm1.TYPE_PREFIX + "_" + hourMode + "_" + nightWatchNumber + "_" + numNightWatches;
    }

    @Override
    public boolean isOfType(@Nullable String alarmID) {
        return alarmID == null || alarmID.startsWith(NaturalHourAlarm1.TYPE_PREFIX);
    }

    @Override
    public String[] getAlarmList(@NonNull Context context)
    {
        int hourMode = AppSettings.getClockIntValue(context, NaturalHourClockBitmap.VALUE_HOURMODE, NaturalHourClockBitmap.HOURMODE_DEFAULT);
        int n = AppSettings.getClockIntValue(context, NaturalHourClockBitmap.VALUE_NIGHTWATCH_TYPE, NaturalHourClockBitmap.NIGHTWATCH_DEFAULT);

        String[] r = new String[n];
        for (int i=0; i<n; i++) {
            r[i] = toAlarmID(hourMode, i + 1, n);
        }
        return r;
    }

    @Override
    public String getAlarmTitle(Context context, @Nullable String alarmID)
    {
        int[] nightwatch = fromAlarmID(alarmID);
        if (nightwatch != null) {
            return context.getString(R.string.alarm_title_format, nightWatchPhrase(context, nightwatch[0], nightwatch[1], nightwatch[2]));
        } else return null;
    }

    @Override
    public String getAlarmSummary(Context context, @Nullable String alarmID)
    {
        if (fromAlarmID(alarmID) != null) {
            return context.getString(R.string.alarm_summary_format);
        } else return null;
    }

    @Override
    public String getAlarmPhrase(Context context, @Nullable String alarmID)
    {
        int[] nightwatch = fromAlarmID(alarmID);
        if (nightwatch != null) {
            return nightWatchPhrase(context, nightwatch[0], nightwatch[1], nightwatch[2]);
        } else return null;
    }

    @Override
    public String getAlarmPhraseGender(Context context, @Nullable String alarmID) {
        return context.getString(R.string.time_gender);
    }
    @Override
    public int getAlarmPhraseQuantity(Context context, @Nullable String alarmID)
    {
        int[] nightwatch = fromAlarmID(alarmID);
        if (nightwatch != null) {
            return nightwatch[1];
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
    public Calendar getEventTime(int hourMode, NaturalHourData data, int[] params) {
        return data.getNightWatch(params[1], params[2]);
    }

}