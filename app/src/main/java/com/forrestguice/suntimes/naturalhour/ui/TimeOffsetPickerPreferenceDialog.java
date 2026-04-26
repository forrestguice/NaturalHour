/**
    Copyright (C) 2026 Forrest Guice
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
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.forrestguice.suntimes.addon.ui.SuntimesUtils;
import com.forrestguice.suntimes.naturalhour.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceDialogFragmentCompat;

@TargetApi(11)
public class TimeOffsetPickerPreferenceDialog extends PreferenceDialogFragmentCompat
{
    private final TimeOffsetPickerPreference.TimeOffsetPickerPreferenceParams params = new TimeOffsetPickerPreference.TimeOffsetPickerPreferenceParams();

    private TextView label;
    private TimeOffsetPicker pickMillis;

    public static TimeOffsetPickerPreferenceDialog newInstance(String key)
    {
        TimeOffsetPickerPreferenceDialog f = new TimeOffsetPickerPreferenceDialog();
        Bundle b = new Bundle();
        b.putString(ARG_KEY, key);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateDialogView(Context context)
    {
        SuntimesUtils.initDisplayStrings(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.layout_dialog_timeoffset, null, false);

        label = (TextView) dialogView.findViewById(R.id.text_label);
        pickMillis = (TimeOffsetPicker) dialogView.findViewById(R.id.pick_offset_millis);

        TimeOffsetPickerPreference preference = (TimeOffsetPickerPreference) getPreference();
        TimeOffsetPickerPreference.TimeOffsetPickerPreferenceParams params = preference.getParams();

        pickMillis.setParams(getContext(), params.param_minMs, params.param_maxMs, params.param_showSeconds, params.param_showMinutes, params.param_showHours, params.param_showDays, params.param_showDirection);
        pickMillis.addViewListener(onValueChanged);

        return dialogView;
    }

    @Override
    public void onBindDialogView(@NonNull View view)
    {
        super.onBindDialogView(view);

        TimeOffsetPickerPreference preference = (TimeOffsetPickerPreference) getPreference();
        if (label != null) {
            label.setText(createSummaryString(preference.getValue()));
        }
        if (pickMillis != null) {
            pickMillis.setSelectedValue(preference.getValue());
        }
    }

    @Override
    public void onPrepareDialogBuilder(AlertDialog.Builder builder)
    {
        super.onPrepareDialogBuilder(builder);
        if (params.param_zeroText != null)
        {
            builder.setNeutralButton(params.param_zeroText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    TimeOffsetPickerPreference preference = (TimeOffsetPickerPreference) getPreference();
                    preference.setValue(0);
                }
            });

        } else if (params.param_resetText != null) {
            builder.setNeutralButton(params.param_resetText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    TimeOffsetPickerPreference preference = (TimeOffsetPickerPreference) getPreference();
                    preference.setValue(params.param_resetValue);
                }
            });
        }
    }

    @Override
    public void onDialogClosed(boolean result)
    {
        if (result)
        {
            int changedValue = (int) pickMillis.getSelectedValue();
            TimeOffsetPickerPreference preference = (TimeOffsetPickerPreference) getPreference();
            if (preference.callChangeListener(changedValue)) {
                preference.setValue(changedValue);
            }
        }
    }

    private final TimeOffsetPicker.MillisecondPickerViewListener onValueChanged = new TimeOffsetPicker.MillisecondPickerViewListener()
    {
        @Override
        public void onValueChanged() {
            if (label != null && pickMillis != null) {
                label.setText(createSummaryString((int) pickMillis.getSelectedValue()));
            }
        }
    };

    private String createSummaryString(int value)
    {
        if (value == 0 && params.param_zeroText != null) {
            return params.param_zeroText;
        } else {
            return new SuntimesUtils().timeDeltaLongDisplayString(0, value, true).getValue();
        }
    }

}
