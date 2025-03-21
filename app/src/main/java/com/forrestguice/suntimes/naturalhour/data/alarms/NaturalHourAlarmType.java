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

import com.forrestguice.suntimes.naturalhour.data.NaturalHourData;

import java.util.Calendar;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface NaturalHourAlarmType
{
    String getTypeID();
    boolean isOfType(@Nullable String alarmID);

    /**
     * @return an array of [alarmID, ...] (that should be listed by the UI)
     */
    String[] getAlarmList(@NonNull Context context);

    String getAlarmTitle(Context context, @Nullable String alarmID);
    String getAlarmSummary(Context context, @Nullable String alarmID);
    String getAlarmPhrase(Context context, @Nullable String alarmID);
    String getAlarmPhraseGender(Context context, @Nullable String alarmID);
    int getAlarmPhraseQuantity(Context context, @Nullable String alarmID);

    boolean getRequiresLocation(@Nullable String alarmID);
    boolean getSupportsRepeating(@Nullable String alarmID);

    long calculateAlarmTime(@NonNull Context context, @Nullable String alarmID, HashMap<String, String> selectionMap);

    /**
     * @param alarmID alarmID
     * @return int[] params
     */
    int[] fromAlarmID(String alarmID);
    String toAlarmID(int[] params);

    Calendar getEventTime(int hourMode, NaturalHourData data, int[] params);

}