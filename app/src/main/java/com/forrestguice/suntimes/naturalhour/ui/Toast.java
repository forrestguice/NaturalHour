/*
    Copyright (C) 2022 Forrest Guice
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

package com.forrestguice.suntimes.naturalhour.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.forrestguice.suntimes.naturalhour.R;

/**
 * Custom toast methods.. applies version specific bug-fixes to Toast messages.
 * bug: [Android 13 dark theme toasts white on white](https://issuetracker.google.com/issues/245108402)
 */
public class Toast
{
    public static final int LENGTH_LONG = android.widget.Toast.LENGTH_LONG;
    public static final int LENGTH_SHORT = android.widget.Toast.LENGTH_SHORT;

    /**
     * Applies `backgroundResource` and `textAppearance` for api33+ (and targetSdk is under than 30).
     */
    @SuppressLint("ShowToast")
    public static android.widget.Toast makeText(Context context, CharSequence text, int duration)
    {
        android.widget.Toast toast = android.widget.Toast.makeText(context, text, duration);
        if (context.getApplicationContext().getApplicationInfo().targetSdkVersion < 30)
        {
            if (Build.VERSION.SDK_INT >= 33)
            {
                View v = toast.getView();    // Toast.getView will return null for targetApi R+
                if (v != null)
                {
                    v.setBackgroundResource(R.drawable.toast_frame);
                    TextView message = (TextView) v.findViewById(android.R.id.message);
                    if (message != null) {
                        message.setTextAppearance(R.style.ToastTextAppearance);
                    }
                }
            }
        }
        return toast;
    }
}