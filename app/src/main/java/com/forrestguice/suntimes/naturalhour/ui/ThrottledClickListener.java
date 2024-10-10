// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2023 Forrest Guice
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

import android.view.View;
import com.forrestguice.suntimes.annotation.NonNull;

/**
 * ThrottledClickListener
 */
public class ThrottledClickListener implements View.OnClickListener
{
    protected long delayMs;
    protected Long previousClickAt;
    protected View.OnClickListener listener;

    public ThrottledClickListener(@NonNull View.OnClickListener listener) {
        this(listener, 1000);
    }

    public ThrottledClickListener(@NonNull View.OnClickListener listener, long delayMs)
    {
        this.delayMs = delayMs;
        this.listener = listener;
        if (listener == null) {
            throw new NullPointerException("OnClickListener is null!");
        }
    }

    @Override
    public void onClick(View v)
    {
        long currentClickAt = System.currentTimeMillis();
        if (previousClickAt == null || Math.abs(currentClickAt - previousClickAt) > delayMs) {
            previousClickAt = currentClickAt;
            listener.onClick(v);
        }
    }
}