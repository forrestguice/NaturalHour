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

    protected TextView text_sunrise, text_sunset;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_romantime, container, false);
        initViews(view);


        return view;
    }

    protected void initViews(View content)
    {
        text_sunrise = (TextView)content.findViewById(R.id.text_sunrise);
        text_sunset = (TextView)content.findViewById(R.id.text_sunset);
    }

    protected void updateViews(Context context, RomanTimeData data)
    {
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
