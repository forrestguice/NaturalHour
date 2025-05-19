// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2021-2025 Forrest Guice
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

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forrestguice.suntimes.addon.AddonHelper;
import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.alarm.AlarmHelper;
import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourProvider;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourProviderContract;
import com.forrestguice.suntimes.naturalhour.ui.DisplayStrings;
import com.forrestguice.suntimes.naturalhour.ui.NaturalHourFragment;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TimeZone;

import static com.forrestguice.suntimes.alarm.AlarmEventContract.EXTRA_ALARM_NOW;
import static com.forrestguice.suntimes.alarm.AlarmEventContract.EXTRA_LOCATION_ALT;
import static com.forrestguice.suntimes.alarm.AlarmEventContract.EXTRA_LOCATION_LAT;
import static com.forrestguice.suntimes.alarm.AlarmEventContract.EXTRA_LOCATION_LON;

public class NaturalHourAlarmFragment extends Fragment
{
    public static final String ARG_HOURMODE = "hourmode";
    public static final int DEF_HOURMODE = NaturalHourClockBitmap.HOURMODE_SUNRISE;

    public static final String ARG_TIME24 = "is24";
    public static final boolean DEF_TIME24 = true;

    public static final String ARG_TIMEZONE = "timezone";
    public static final String DEF_TIMEZONE = null;

    protected TextView text_time;
    protected AlarmSelectFragment alarmSelect;

    public NaturalHourAlarmFragment()
    {
        setHasOptionsMenu(false);
        Bundle args = new Bundle();
        setArguments(args);
        setIs24(false);
        setLocation(0, 0, 0);
    }

    public String getAlarmID() {
        return alarmSelect.getSelectedEventID();
    }

    public void setAlarmID(String alarmID)
    {
        int[] params = NaturalHourProvider.getAlarmInfo(alarmID).fromAlarmID(alarmID);
        if (params != null) {
            setHourMode(params[0]);
        }
        alarmSelect.setSelectedEventID(alarmID);

        /*NaturalHourAlarmType alarmType = NaturalHourProvider.getAlarmInfo(alarmID);
        switch (alarmType.getTypeID()) {
            case NaturalHourAlarm1.TYPE_PREFIX:
            case NaturalHourAlarm0.TYPE_PREFIX:
            default: break;
        }*/
    }

    public int getHourMode() {
        Bundle args = getArguments();
        return args != null ? args.getInt(ARG_HOURMODE, DEF_HOURMODE) : DEF_HOURMODE;
    }
    public void setHourMode( int mode )
    {
        Bundle args = getArguments();
        if (args != null) {
            args.putInt(ARG_HOURMODE, mode);
        }
        if (alarmSelect != null) {
            alarmSelect.setIntArg(NaturalHourSelectFragment.ARG_HOURMODE, getHourMode());
            alarmSelect.setBoolArg(NaturalHourSelectFragment.ARG_MODE24, NaturalHourFragment.isMode24(getHourMode()));
            alarmSelect.initViews(alarmSelect.getView());
        }
    }

    public boolean is24() {
        Bundle args = getArguments();
        return args != null && args.getBoolean(ARG_TIME24, DEF_TIME24);
    }
    public void setIs24(boolean value)
    {
        Bundle args = getArguments();
        if (args != null) {
            args.putBoolean(ARG_TIME24, value);
        }
    }

    public TimeZone getTimeZone()
    {
        Bundle args = getArguments();
        if (args != null) {
            String tzID = args.getString(ARG_TIMEZONE, DEF_TIMEZONE);
            return (tzID != null ? TimeZone.getTimeZone(tzID) : TimeZone.getDefault());
        }
        return TimeZone.getDefault();
    }
    public void setTimeZone(String tzID) {
        Bundle args = getArguments();
        if (args != null) {
            args.putString(ARG_TIMEZONE, tzID);
        }
    }

    public double[] getLocation()
    {
        Bundle args = getArguments();
        if (args != null) {
            double lat = args.getDouble(EXTRA_LOCATION_LAT, 0);
            double lon = args.getDouble(EXTRA_LOCATION_LON, 0);
            double alt = args.getDouble(EXTRA_LOCATION_ALT, 0);
            return new double[] {lat, lon, alt};
        } else return new double[] {0,0,0};
    }
    protected void setLocation(@Nullable SuntimesInfo info) {
        if (info != null && info.location != null) {
            setLocation(info.location[1], info.location[2], info.location[3]);
        }
    }
    public void setLocation(String lat, String lon, String alt) {
        setLocation(Double.parseDouble(lat), Double.parseDouble(lon), Double.parseDouble(alt));
    }
    public void setLocation(double lat, double lon, double alt)
    {
        Bundle args = getArguments();
        if (args != null) {
            args.putDouble(EXTRA_LOCATION_LAT, lat);
            args.putDouble(EXTRA_LOCATION_LON, lon);
            args.putDouble(EXTRA_LOCATION_ALT, alt);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_alarmsheet, container, false);
        initViews(view);
        return view;
    }

    protected void initViews(View content)
    {
        text_time = (TextView)content.findViewById(R.id.text_time);
        if (text_time != null) {
            text_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    triggerAlarmSelected();
                }
            });
        }

        FragmentManager fragments = getChildFragmentManager();
        alarmSelect = (NaturalHourSelectFragment) fragments.findFragmentById(R.id.naturalhourselect_fragment);
        if (alarmSelect != null)
        {
            alarmSelect.setIntArg(NaturalHourSelectFragment.ARG_HOURMODE, getHourMode());
            alarmSelect.setBoolArg(NaturalHourSelectFragment.ARG_MODE24, NaturalHourFragment.isMode24(getHourMode()));

            //alarmSelect.setIntArg(NaturalHourSelectFragment.ARG_HOUR, getHour());
            //alarmSelect.setIntArg(NaturalHourSelectFragment.ARG_MOMENT, getMoment());
            alarmSelect.initViews(alarmSelect.getView());    // re-init views
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        FragmentManager fragments = getChildFragmentManager();
        alarmSelect = (NaturalHourSelectFragment) fragments.findFragmentById(R.id.naturalhourselect_fragment);
        if (alarmSelect != null)
        {
            alarmSelect.updateViews();
            alarmSelect.setFragmentListener(onSelectionChanged);
        }
        updateViews(getActivity());
    }

    private final NaturalHourSelectFragment.FragmentListener onSelectionChanged = new NaturalHourSelectFragment.FragmentListener()
    {
        @Override
        public void onItemSelected(int[] values)
        {
            //Bundle args = getArguments();
            //if (args != null) {
            //    args.putInt(ARG_HOUR, values[0]);
            //    args.putInt(ARG_MOMENT, values[1]);
            //}
            updateViews(getActivity());
            triggerAlarmSelected();
        }
    };

    protected void updateViews(Context context) {
        updateTimeView(getAlarmID());
    }

    protected void updateTimeView(String alarmID)
    {
        Context context = getActivity();
        if (text_time != null && context != null)
        {
            double[] location = getLocation();
            HashMap<String, String> selectionMap = new HashMap<>();
            selectionMap.put(EXTRA_ALARM_NOW, Long.toString(System.currentTimeMillis()));
            selectionMap.put(EXTRA_LOCATION_LAT, location[0] + "");
            selectionMap.put(EXTRA_LOCATION_LON, location[1] + "");
            selectionMap.put(EXTRA_LOCATION_ALT, location[2] + "");
            long alarmTimeMillis = NaturalHourProvider.getAlarmInfo(alarmID).calculateAlarmTime(context, alarmID, selectionMap);   // TODO: optimize
            text_time.setText(alarmTimeMillis >= 0 ? DisplayStrings.formatTime(context, alarmTimeMillis, getTimeZone(), is24() ? 24 : 12) : "");
        }
    }

    public static void scheduleAlarm(Context context, String alarmID)
    {
        String alarmUri = AlarmHelper.getEventInfoUri(NaturalHourProviderContract.AUTHORITY, alarmID);
        String label = NaturalHourProvider.getAlarmInfo(alarmID).getAlarmTitle(context, alarmID);
        try {
            context.startActivity(AddonHelper.scheduleAlarm("ALARM", label, -1, -1, TimeZone.getDefault(), alarmUri));
        } catch (ActivityNotFoundException e) {
            Log.e("AlarmFragment", "Failed to schedule alarm: " + e);
        }
    }

    /**
     * Set fragment arguments; supports (passes) args to NaturalHourAlarmFragment
     * @param a Bundle of args
     */
    public void addArguments(@Nullable Bundle a)
    {
        Bundle args = getArguments();
        if (args != null && a != null) {
            args.putAll(a);
        } else if (a != null) {
            setArguments(a);
        }
    }

    /**
     * FragmentListener
     */
    public interface FragmentListener
    {
        void onAlarmSelected(String alarmID);
    }

    protected ArrayList<FragmentListener> listeners = new ArrayList<>();
    public void addFragmentListener(FragmentListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }
    public void removeFragmentListener(FragmentListener l) {
        listeners.remove(l);
    }
    public void clearFragmentListeners() {
        listeners.clear();
    }

    protected void triggerAlarmSelected()
    {
        for (FragmentListener listener : listeners) {
            if (listener != null) {
                listener.onAlarmSelected(getAlarmID());
            }
        }
    }
}
