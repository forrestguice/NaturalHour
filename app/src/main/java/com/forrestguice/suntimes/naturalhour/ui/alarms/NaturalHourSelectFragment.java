// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2021 Forrest Guice
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
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValuesFragment;

public class NaturalHourSelectFragment extends ColorValuesFragment
{
    public static final String ARG_MODE24 = "mode_24";
    public static final Boolean DEF_MODE24 = false;

    public static final String ARG_HOUR = "hour";
    public static final int DEF_HOUR = 0;

    public static final String ARG_MOMENT = "moment";
    public static final int DEF_MOMENT = 0;

    protected NumberPicker hourPicker;
    protected NumberPicker daynightPicker;
    protected NumberPicker momentPicker;

    public NaturalHourSelectFragment()
    {
        setHasOptionsMenu(false);

        Bundle args = new Bundle();
        args.putInt(ARG_HOUR, DEF_HOUR);           // selected hour; [0,23]
        args.putInt(ARG_MOMENT, DEF_MOMENT);       // selected moment; [0,39]
        args.putBoolean(ARG_MODE24, DEF_MODE24);
        setArguments(args);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState)
    {
        View content = inflater.inflate(R.layout.fragment_naturalhour_selector, container, false);
        if (savedState != null) {
            onRestoreInstanceState(savedState);
        }
        initViews(content);
        updateViews();
        return content;
    }

    protected void initViews(View content)
    {
        boolean mode24 = getBoolArg(ARG_MODE24, DEF_MODE24);

        hourPicker = (NumberPicker)content.findViewById(R.id.pick_hour);
        if (hourPicker != null)
        {
            hourPicker.setMinValue(0);
            hourPicker.setMaxValue(mode24 ? (24-1) : (12-1));
            hourPicker.setWrapSelectorWheel(false);
            hourPicker.setDisplayedValues(getResources().getStringArray(mode24 ? R.array.hour_24 : R.array.hour_12));
            hourPicker.setOnValueChangedListener(onHourSelected);
        }

        daynightPicker = (NumberPicker)content.findViewById(R.id.pick_daynight);
        if (daynightPicker != null)
        {
            daynightPicker.setMinValue(0);
            daynightPicker.setMaxValue(mode24 ? 0 : 1);
            daynightPicker.setWrapSelectorWheel(false);
            daynightPicker.setDisplayedValues(getResources().getStringArray(R.array.phrase_of_day));
            daynightPicker.setOnValueChangedListener(onDayNightSelected);
        }

        momentPicker = (NumberPicker)content.findViewById(R.id.pick_moment);
        if (momentPicker != null)
        {
            String[] displayValues = momentDisplayValues(getContext());
            momentPicker.setMinValue(0);
            momentPicker.setMaxValue(displayValues.length-1);
            momentPicker.setDisplayedValues(displayValues);
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

    private NumberPicker.OnValueChangeListener onHourSelected = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            onInputChanged();
        }
    };
    private NumberPicker.OnValueChangeListener onDayNightSelected = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            onInputChanged();
        }
    };
    private NumberPicker.OnValueChangeListener onMomentSelected = new NumberPicker.OnValueChangeListener() {
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
            listener.onItemSelected(hour, moment);
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

    protected void onRestoreInstanceState(@NonNull Bundle savedState) { /* EMPTY */ }

    protected void updateViews()
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

    protected void setIntArg(String key, int value) {
        Bundle args = getArguments();
        if (args != null) {
            args.putInt(key, value);
            updateViews();
        }
    }
    protected int getIntArg(String key, int defValue) {
        Bundle args = getArguments();
        return args != null ? args.getInt(key, defValue) : defValue;
    }

    protected void setBoolArg(String key, boolean value) {
        Bundle args = getArguments();
        if (args != null) {
            args.putBoolean(key, value);
            updateViews();
        }
    }
    protected boolean getBoolArg(String key, boolean defValue) {
        Bundle args = getArguments();
        return args != null ? args.getBoolean(key, defValue) : defValue;
    }

    /**
     * FragmentListener
     */
    public interface FragmentListener
    {
        void onItemSelected(int hour, int moment);
    }

    protected FragmentListener listener = null;
    public void setFragmentListener(FragmentListener l) {
        listener = l;
    }
}