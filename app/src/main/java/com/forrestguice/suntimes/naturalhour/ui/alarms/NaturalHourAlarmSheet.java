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

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.forrestguice.suntimes.naturalhour.R;

import static com.forrestguice.suntimes.alarm.AlarmEventContract.EXTRA_LOCATION_ALT;
import static com.forrestguice.suntimes.alarm.AlarmEventContract.EXTRA_LOCATION_LAT;
import static com.forrestguice.suntimes.alarm.AlarmEventContract.EXTRA_LOCATION_LON;

public class NaturalHourAlarmSheet extends BottomSheetDialogFragment
{
    protected NaturalHourAlarmFragment fragment;

    public NaturalHourAlarmSheet() {
        Bundle args = new Bundle();
        setArguments(args);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedState)
    {
        android.support.v7.view.ContextThemeWrapper contextWrapper = new android.support.v7.view.ContextThemeWrapper(getActivity(), getThemeResID());    // hack: contextWrapper required because base theme is not properly applied
        return inflater.cloneInContext(contextWrapper).inflate(R.layout.dialog_alarms, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        ImageButton okButton = (ImageButton) view.findViewById(R.id.btn_done);
        if (okButton != null) {
            okButton.setOnClickListener(onOkClicked);
        }

        FragmentManager fragments = getChildFragmentManager();
        fragment = (NaturalHourAlarmFragment) fragments.findFragmentByTag("alarmFragment");
        if (fragment == null) {
            fragment = new NaturalHourAlarmFragment();
        }
        if (listener != null) {
            fragment.addFragmentListener(listener);
            fragment.addArguments(getArguments());
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.alarmdialog_fragments, fragment, "alarmFragment").commit();
        fragments.executePendingTransactions();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        expandSheet(getDialog());
    }

    private final View.OnClickListener onOkClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onAddAlarmClicked(fragment.getAlarmID());
            }
            dismiss();
        }
    };

    public void setTheme(int themeResID) {
        Bundle args = getArguments();
        if (args != null) {
            args.putInt("themeResID", themeResID);
        }
    }
    public int getThemeResID() {
        Bundle args = getArguments();
        return args != null ? args.getInt("themeResID", R.style.NaturalHourAppTheme_Dark) : R.style.NaturalHourAppTheme_Dark;
    }

    public void setLocation(String... location) {
        if (location != null && location.length >= 4) {
            setLocation(Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3]));
        }
    }
    public void setLocation(double lat, double lon, double alt)
    {
        Bundle args = getArguments();
        if (args == null) {
            args = new Bundle();
            setArguments(args);
        }
        args.putDouble(EXTRA_LOCATION_LAT, lat);
        args.putDouble(EXTRA_LOCATION_LON, lon);
        args.putDouble(EXTRA_LOCATION_ALT, alt);
    }

    public interface FragmentListener extends NaturalHourAlarmFragment.FragmentListener {
        void onAddAlarmClicked(String alarmID);
    }

    protected NaturalHourAlarmSheet.FragmentListener listener = null;
    public void setFragmentListener(NaturalHourAlarmSheet.FragmentListener l)
    {
        listener = l;
        if (fragment != null) {
            fragment.addFragmentListener(listener);
        }
    }

    private static void expandSheet(DialogInterface dialog)
    {
        if (dialog != null) {
            BottomSheetDialog bottomSheet = (BottomSheetDialog) dialog;
            FrameLayout layout = (FrameLayout) bottomSheet.findViewById(android.support.design.R.id.design_bottom_sheet);  // for AndroidX, resource is renamed to com.google.android.material.R.id.design_bottom_sheet
            if (layout != null) {
                BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(layout);
                behavior.setHideable(false);
                behavior.setSkipCollapsed(true);
                behavior.setPeekHeight(200);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }
}
