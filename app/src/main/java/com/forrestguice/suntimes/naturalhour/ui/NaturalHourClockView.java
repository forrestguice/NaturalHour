// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2020 Forrest Guice
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

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.forrestguice.suntimes.naturalhour.data.NaturalHourData;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValues;

import java.util.TimeZone;

public class NaturalHourClockView extends View
{
    protected NaturalHourData data;
    protected NaturalHourClockBitmap bitmap;

    public NaturalHourClockView(Context context) {
        super(context);
        initView(context);
    }

    public NaturalHourClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public NaturalHourClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    protected void initView(Context context) {
        bitmap = new NaturalHourClockBitmap(getContext(), getWidth());
    }

    public void setData(NaturalHourData data) {
        this.data = data;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            height = Math.min(width, height);
        }
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
            width = Math.min(width, height);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    public void onSizeChanged(int w, int h, int w0, int h0)
    {
        super.onSizeChanged(w, h, w0, h0);
        bitmap.setSize(Math.min(w, h));
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (bitmap == null) {
            initView(getContext());
        }
        bitmap.draw(getContext(), canvas, data);
    }

    public void setTime(long millis) {
        bitmap.setTime(millis);
        invalidate();
    }

    public void setTimeZone( TimeZone timezone) {
        bitmap.setTimeZone(timezone);
        invalidate();
    }

    public void set24HourMode(boolean value) {
        bitmap.set24HourMode(value);
        invalidate();
    }

    public void setStartAngle(double radianValue) {
        bitmap.setStartAngle(radianValue);
        invalidate();
    }

    public void setShowMinorTickLabels(boolean value) {
        bitmap.setShowMinorTickLabels(value);
        invalidate();
    }

    public void setShowTime(boolean value) {
        bitmap.setShowTime(value);
        invalidate();
    }

    public void setFlag(String flag, boolean value) {
        bitmap.setFlag(flag, value);
    }

    public boolean getFlag(String flag) {
        return bitmap.getFlag(flag);
    }

    public boolean getDefaultFlag(Context context, String flag) {
        return NaturalHourClockBitmap.getDefaultFlag(context, flag);
    }

    public ColorValues getColors() {
        return bitmap.getColors();
    }
    public void setColors( ColorValues values ) {
        bitmap.setColors(values);
    }
}