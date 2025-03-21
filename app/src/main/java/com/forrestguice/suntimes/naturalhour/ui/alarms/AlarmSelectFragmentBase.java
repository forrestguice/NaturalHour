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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public abstract class AlarmSelectFragmentBase extends Fragment implements AlarmSelectFragment
{
    protected abstract int getLayoutResourceID();
    public abstract void initViews(View content);
    public abstract void updateViews();

    public static final String ARG_HOURMODE = "mode";
    public static final int DEF_HOURMODE = NaturalHourClockBitmap.HOURMODE_SUNRISE;

    public static final String ARG_MODE24 = "mode_24";
    public static final Boolean DEF_MODE24 = false;

    public static final String KEY_DIALOGTHEME = "themeResID";
    protected static final int DEF_DIALOGTHEME = R.style.NaturalHourAppTheme_Dark;

    public void setTheme(int themeResID)
    {
        Bundle args = initArgs();
        args.putInt(KEY_DIALOGTHEME, themeResID);
        setArguments(args);
    }
    public int getThemeResID() {
        return (getArguments() != null) ? getArguments().getInt(KEY_DIALOGTHEME, DEF_DIALOGTHEME) : DEF_DIALOGTHEME;
    }

    public AlarmSelectFragmentBase()
    {
        setHasOptionsMenu(false);
        Bundle args = initArgs();
        args.putInt(ARG_HOURMODE, DEF_HOURMODE);
        args.putBoolean(ARG_MODE24, DEF_MODE24);
        setArguments(args);
    }

    protected Bundle initArgs() {
        return (getArguments() != null ? getArguments() : new Bundle());
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState)
    {
        View content = inflater.inflate(getLayoutResourceID(), container, false);
        if (savedState != null) {
            onRestoreInstanceState(savedState);
        }
        initViews(content);
        updateViews();
        return content;
    }

    protected void onRestoreInstanceState(@NonNull Bundle savedState) { /* EMPTY */ }

    @Override
    public void setIntArg(String key, int value) {
        Bundle args = getArguments();
        if (args != null) {
            args.putInt(key, value);
            updateViews();
        }
    }
    @Override
    public int getIntArg(String key, int defValue) {
        Bundle args = getArguments();
        return args != null ? args.getInt(key, defValue) : defValue;
    }

    @Override
    public void setBoolArg(String key, boolean value) {
        Bundle args = getArguments();
        if (args != null) {
            args.putBoolean(key, value);
            updateViews();
        }
    }
    @Override
    public boolean getBoolArg(String key, boolean defValue) {
        Bundle args = getArguments();
        return args != null ? args.getBoolean(key, defValue) : defValue;
    }

    protected FragmentListener listener = null;
    @Override
    public void setFragmentListener(FragmentListener l) {
        listener = l;
    }
}