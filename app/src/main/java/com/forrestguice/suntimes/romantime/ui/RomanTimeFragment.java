package com.forrestguice.suntimes.romantime.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.romantime.R;
import com.forrestguice.suntimes.romantime.data.RomanTimeCalculator;
import com.forrestguice.suntimes.romantime.data.RomanTimeData;

import java.util.TimeZone;

public class RomanTimeFragment extends Fragment
{
    protected RomanTimeData data = null;

    protected TextView text_sunrise, text_sunset;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_romantime, container, false);
        initViews(view);

        data = initData(getActivity());
        updateViews(getActivity(), data);

        return view;
    }

    protected void initViews(View content)
    {
        text_sunrise = (TextView)content.findViewById(R.id.text_sunrise);
        text_sunset = (TextView)content.findViewById(R.id.text_sunset);
    }

    protected void updateViews(Context context, RomanTimeData data)
    {
        if (data != null)
        {
            long[] romanHours = data.getRomanHours();

            CharSequence[] dayHours = new String[12];
            StringBuilder debugDisplay0 = new StringBuilder();
            for (int i=0; i<dayHours.length; i++) {
                dayHours[i] = DisplayStrings.formatTime(context, romanHours[i], TimeZone.getDefault().getID(), true);
                debugDisplay0.append("\n").append(DisplayStrings.romanNumeral(context, i + 1)).append(": ").append(dayHours[i]);
            }

            CharSequence[] nightHours = new String[12];
            StringBuilder debugDisplay1 = new StringBuilder();
            for (int i=0; i<nightHours.length; i++) {
                nightHours[i] = DisplayStrings.formatTime(context, romanHours[12 + i], TimeZone.getDefault().getID(), true);
                debugDisplay1.append("\n").append(DisplayStrings.romanNumeral(context, i + 1)).append(": ").append(nightHours[i]);
            }

            text_sunrise.setText(debugDisplay0.toString());
            text_sunset.setText(debugDisplay1.toString());

        } else {
            text_sunrise.setText("");
            text_sunset.setText("");
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    protected RomanTimeData initData(Context context)
    {
        if (context != null)
        {
            ContentResolver resolver = context.getContentResolver();
            SuntimesInfo config = SuntimesInfo.queryInfo(context);
            double latitude = Double.parseDouble(config.location[1]);
            double longitude = Double.parseDouble(config.location[2]);
            double altitude = Double.parseDouble(config.location[3]);
            long date = System.currentTimeMillis();

            RomanTimeData data = new RomanTimeData(date, latitude, longitude, altitude);
            RomanTimeCalculator calculator = new RomanTimeCalculator();
            calculator.calculateData(resolver, data);
            return data;
        } else return null;
    }
}
