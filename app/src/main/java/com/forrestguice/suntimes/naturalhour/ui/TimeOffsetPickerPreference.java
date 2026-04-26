/**
    Copyright (C) 2025 Forrest Guice
    This file is part of SuntimesWidget.

    SuntimesWidget is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    SuntimesWidget is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with SuntimesWidget.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.forrestguice.suntimes.naturalhour.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;

import androidx.annotation.NonNull;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceViewHolder;

import android.util.AttributeSet;

import com.forrestguice.suntimes.addon.ui.SuntimesUtils;
import com.forrestguice.suntimes.naturalhour.R;

/**
 * A version of MillisecondPickerPreference that allows selecting a millisecond value as a
 * combination of hours, minutes, and seconds.
 */
@TargetApi(11)
public class TimeOffsetPickerPreference extends DialogPreference
{
    private int value;

    public static final class TimeOffsetPickerPreferenceParams
    {
        public int param_minMs = 1, param_maxMs = 10000;

        public String param_zeroText = null;
        public String param_resetText = null;
        public Integer param_resetValue = null;

        public boolean param_showDirection = false;
        public boolean param_showSeconds = true;
        public boolean param_showMinutes = true;
        public boolean param_showHours = true;
        public boolean param_showDays = false;
    }
    protected TimeOffsetPickerPreferenceParams params;
    public TimeOffsetPickerPreferenceParams getParams() {
        if (params == null) {
            params = new TimeOffsetPickerPreferenceParams();
        }
        return params;
    }

    @TargetApi(21)
    public TimeOffsetPickerPreference(Context context) {
        super(context);
    }

    public TimeOffsetPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initParams(context, attrs);
    }

    @TargetApi(21)
    public TimeOffsetPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParams(context, attrs);
    }

    @TargetApi(21)
    public TimeOffsetPickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initParams(context, attrs);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int i) {
        return a.getInt(i, getMin());
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(getMin()) : (Integer)defaultValue);
    }

    public int getMin() {
        TimeOffsetPickerPreferenceParams params = getParams();
        return params.param_minMs;
    }
    public int getMax() {
        TimeOffsetPickerPreferenceParams params = getParams();
        return params.param_maxMs;
    }

    public void setValue(int value)
    {
        this.value = value;
        persistInt(this.value);
        updateSummary();
    }
    public int getValue() {
        return this.value;
    }

    public void initParams(Context context, AttributeSet attrs)
    {
        TimeOffsetPickerPreferenceParams params = getParams();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TimeOffsetPickerPreference, 0, 0);
        try {
            params.param_minMs = a.getInt(R.styleable.TimeOffsetPickerPreference_minValue, params.param_minMs);
            params.param_maxMs = a.getInt(R.styleable.TimeOffsetPickerPreference_maxValue, params.param_maxMs);
            params.param_zeroText = a.getString(R.styleable.TimeOffsetPickerPreference_zeroValueText);
            params.param_resetText = a.getString(R.styleable.TimeOffsetPickerPreference_resetDefaultsText);
            params.param_resetValue = a.getInt(R.styleable.TimeOffsetPickerPreference_resetDefaultsValue, params.param_minMs);
            params.param_showSeconds = a.getBoolean(R.styleable.TimeOffsetPickerPreference_allowPickSeconds, params.param_showSeconds);
            params.param_showMinutes = a.getBoolean(R.styleable.TimeOffsetPickerPreference_allowPickMinutes, params.param_showMinutes);
            params.param_showHours = a.getBoolean(R.styleable.TimeOffsetPickerPreference_allowPickHours, params.param_showHours);
            params.param_showDays = a.getBoolean(R.styleable.TimeOffsetPickerPreference_allowPickDays, params.param_showDays);
            params.param_showDirection = a.getBoolean(R.styleable.TimeOffsetPickerPreference_allowPickBeforeAfter, params.param_showDirection);

        } finally {
            a.recycle();
        }
    }
    
    private String createSummaryString(int value)
    {
        TimeOffsetPickerPreferenceParams params = getParams();
        if (value == 0 && params.param_zeroText != null) {
            return params.param_zeroText;
        } else {
            return new SuntimesUtils().timeDeltaLongDisplayString(0, value, true).getValue();
        }
    }

    private void updateSummary() {
        setSummary(createSummaryString(getValue()));
    }

}
