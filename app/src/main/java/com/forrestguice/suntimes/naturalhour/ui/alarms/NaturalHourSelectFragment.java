// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2021-2024 Forrest Guice
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

package com.forrestguice.suntimes.naturalhour.ui.alarms;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourProvider;
import com.forrestguice.suntimes.naturalhour.data.alarms.NaturalHourAlarm0;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;

public class NaturalHourSelectFragment extends AlarmSelectFragmentBase implements AlarmSelectFragment
{
    public static final String ARG_HOUR = "hour";
    public static final int DEF_HOUR = 0;

    public static final String ARG_MOMENT = "moment";
    public static final int DEF_MOMENT = 0;

    protected NumberPicker hourPicker;
    protected NumberPicker daynightPicker;
    protected NumberPicker momentPicker;

    public NaturalHourSelectFragment()
    {
        super();
        Bundle args = initArgs();
        args.putInt(ARG_HOUR, DEF_HOUR);           // selected hour; [0,23]
        args.putInt(ARG_MOMENT, DEF_MOMENT);       // selected moment; [0,39]
        setArguments(args);
    }

    @Override
    public String getSelectedEventID() {
        return NaturalHourAlarm0.naturalHourToAlarmID(getIntArg(ARG_HOURMODE, DEF_HOURMODE), getSelectedHour(), getSelectedMoment());
    }

    @Override
    public void setSelectedEventID(String eventID)
    {
        int[] params = NaturalHourProvider.getAlarmInfo(eventID).fromAlarmID(eventID);
        if (params != null)
        {
            setIntArg(ARG_HOURMODE, params[0]);
            setBoolArg(ARG_MODE24, (params[0] == NaturalHourClockBitmap.HOURMODE_SUNSET_24));
            setIntArg(ARG_HOUR, params[1]);
            setIntArg(ARG_MOMENT, params[2]);
        }
    }

    @Override
    public void initViews(View content)
    {
        if (content == null) {
            return;
        }

        boolean mode24 = getBoolArg(ARG_MODE24, DEF_MODE24);

        hourPicker = (NumberPicker)content.findViewById(R.id.pick_hour);
        if (hourPicker != null)
        {
            hourPicker.setDisplayedValues(getResources().getStringArray(mode24 ? R.array.hour_24 : R.array.hour_12));
            hourPicker.setMinValue(0);
            hourPicker.setMaxValue(mode24 ? (24-1) : (12-1));
            hourPicker.setWrapSelectorWheel(false);
            hourPicker.setOnValueChangedListener(onHourSelected);
        }

        daynightPicker = (NumberPicker)content.findViewById(R.id.pick_daynight);
        if (daynightPicker != null)
        {
            daynightPicker.setDisplayedValues(getResources().getStringArray(R.array.phrase_of_day));
            daynightPicker.setMinValue(0);
            daynightPicker.setMaxValue(mode24 ? 0 : 1);
            daynightPicker.setWrapSelectorWheel(false);
            daynightPicker.setOnValueChangedListener(onDayNightSelected);
        }

        momentPicker = (NumberPicker)content.findViewById(R.id.pick_moment);
        if (momentPicker != null)
        {
            String[] displayValues = momentDisplayValues(getContext());
            momentPicker.setDisplayedValues(displayValues);
            momentPicker.setMinValue(0);
            momentPicker.setMaxValue(displayValues.length-1);
            momentPicker.setWrapSelectorWheel(false);
            momentPicker.setOnValueChangedListener(onMomentSelected);
        }
    }

    private String[] momentDisplayValues(Context context)
    {
        String[] values = new String[40];
        values[0] = " ";
        for (int i=1; i<values.length; i++) {    // TODO: i18n?
            values[i] = i + "/" + values.length;
        }
        return values;
    }

    private final NumberPicker.OnValueChangeListener onHourSelected = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            onInputChanged();
        }
    };
    private final NumberPicker.OnValueChangeListener onDayNightSelected = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            onInputChanged();
        }
    };
    private final NumberPicker.OnValueChangeListener onMomentSelected = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            onInputChanged();
        }
    };

    protected void onInputChanged()
    {
        int hour = getSelectedHour();
        int moment = getSelectedMoment();
        setIntArg(ARG_HOUR, hour);
        setIntArg(ARG_MOMENT, moment);
        if (listener != null) {
            listener.onItemSelected(new int[] {hour, moment});
        }
        updateViews();
    }

    public int getSelectedHour()
    {
        if (hourPicker != null) {
            return getBoolArg(ARG_MODE24, DEF_MODE24) ? hourPicker.getValue() : (hourPicker.getValue() + 12 * daynightPicker.getValue());
        } else return 0;
    }

    public int getSelectedMoment()
    {
        if (momentPicker != null) {
            return momentPicker.getValue();
        } else return 0;
    }

    @Override
    public void updateViews()
    {
        boolean mode24 = getBoolArg(ARG_MODE24, DEF_MODE24);
        int hour = getIntArg(ARG_HOUR, DEF_HOUR);
        int moment = getIntArg(ARG_MOMENT, DEF_MOMENT);

        if (hourPicker != null) {
            hourPicker.setValue(mode24 ? hour
                    : (hour >= 12 ? hour-12 : hour));
        }

        if (daynightPicker != null) {
            daynightPicker.setValue(mode24 ? 0
                    : (hour >= 12 ? 1 : 0));
        }

        if (momentPicker != null) {
            momentPicker.setValue(moment);
        }
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_naturalhour_selector;
    }
}