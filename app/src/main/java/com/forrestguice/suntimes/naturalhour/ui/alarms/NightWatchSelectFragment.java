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

package com.forrestguice.suntimes.naturalhour.ui.alarms;

import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.data.alarms.NaturalHourAlarm1;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;

public class NightWatchSelectFragment extends AlarmSelectFragmentBase implements AlarmSelectFragment
{
    public static final String ARG_WATCH_I = "watch_i";
    public static final int DEF_WATCH_I = 1;

    public static final String ARG_WATCH_N = "watch_n";
    public static final int DEF_WATCH_N = 4;

    protected NumberPicker watchPicker;

    public NightWatchSelectFragment()
    {
        super();
        Bundle args = initArgs();
        args.putInt(ARG_WATCH_I, DEF_WATCH_I);
        args.putInt(ARG_WATCH_N, DEF_WATCH_N);
        setArguments(args);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_nightwatch_selector;
    }

    @Override
    public String getSelectedEventID() {
        return new NaturalHourAlarm1().toAlarmID(getIntArg(ARG_HOURMODE, DEF_HOURMODE), getSelectedI(), getSelectedN());
    }

    @Override
    public void setSelectedEventID(String eventID)
    {
        int[] params = new NaturalHourAlarm1().fromAlarmID(eventID);
        if (params != null)
        {
            setIntArg(ARG_HOURMODE, params[0]);
            setBoolArg(ARG_MODE24, (params[0] == NaturalHourClockBitmap.HOURMODE_SUNSET));
            setIntArg(ARG_WATCH_I, params[1]);
            setIntArg(ARG_WATCH_N, params[2]);
        }
    }

    @Override
    public void initViews(View content)
    {
        watchPicker = (NumberPicker)content.findViewById(R.id.pick_watch);
        if (watchPicker != null)
        {
            int n = getSelectedN();
            String[] displayValues = new String[n];
            for (int i=0; i<n; i++) {
                displayValues[i] = "Night Watch " + (i + 1) + " of " + n;    // TODO: i18n
            }

            watchPicker.setMinValue(1);
            watchPicker.setMaxValue(getSelectedN());
            watchPicker.setWrapSelectorWheel(false);
            watchPicker.setDisplayedValues(displayValues);
            watchPicker.setOnValueChangedListener(onWatchSelected);
        }
    }

    private final NumberPicker.OnValueChangeListener onWatchSelected = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            onInputChanged();
        }
    };

    protected void onInputChanged()
    {
        int i = getSelectedI();
        setIntArg(ARG_WATCH_I, i);
        if (listener != null) {
            listener.onItemSelected(new int[] { i, getSelectedN() });
        }
        updateViews();
    }

    public int getSelectedI()
    {
        if (watchPicker != null) {
            return watchPicker.getValue();
        } else return 1;
    }

    public int getSelectedN() {
        return getIntArg(ARG_WATCH_N, DEF_WATCH_N);
    }

    @Override
    public void updateViews()
    {
        if (watchPicker != null) {
            watchPicker.setValue(getIntArg(ARG_WATCH_I, DEF_WATCH_I));
        }
    }

}