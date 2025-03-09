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

import android.view.View;

public interface AlarmSelectFragment
{
    String getSelectedEventID();
    void setSelectedEventID(String eventID);

    View getView();
    void initViews(View content);
    void updateViews();

    void setIntArg(String key, int value);
    void setBoolArg(String key, boolean value);

    int getIntArg(String key, int defValue);
    boolean getBoolArg(String key, boolean defValue);

    void setFragmentListener(FragmentListener l);
    interface FragmentListener
    {
        void onItemSelected(int[] values);
    }
}