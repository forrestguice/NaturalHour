// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2020 Forrest Guice
    This file is part of RomanTime.

    RomanTime is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    RomanTime is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with RomanTime.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.forrestguice.suntimes.romantime.ui;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.TextView;
import android.widget.Toast;

import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.addon.TimeZoneHelper;
import com.forrestguice.suntimes.romantime.R;
import com.forrestguice.suntimes.romantime.data.RomanTimeCalculator;
import com.forrestguice.suntimes.romantime.data.RomanTimeData;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class RomanTimeFragment extends Fragment
{
    protected RecyclerView cardView;
    protected LinearLayoutManager cardLayout;
    protected RomanTimeCardAdapter cardAdapter;

    protected SuntimesInfo info;
    protected TimeZone timezone = TimeZone.getDefault();
    protected boolean is24 = true;

    public void setSuntimesInfo(SuntimesInfo value, TimeZone tz, boolean is24)
    {
        this.info = value;
        this.timezone = tz;
        this.is24 = is24;
        cardAdapter.setCardOptions(new RomanTimeAdapterOptions(getActivity(), info, timezone, is24));
    }
    public TimeZone getTimeZone() {
        return timezone;
    }
    public boolean is24() {
        return is24;
    }

    public RomanTimeFragment() {
        setHasOptionsMenu(true);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_romantime, container, false);
        initViews(view);

        Context context = getActivity();
        if (info == null && context != null) {
            info = SuntimesInfo.queryInfo(context);
        }

        initData(getActivity());
        cardView.scrollToPosition(RomanTimeCardAdapter.TODAY_POSITION);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startClockRunnable();
    }

    protected void startClockRunnable() {
        Log.d("DEBUG", "clockRunnable: starting..");
        cardView.post(clockUpdateRunnable);
    }
    protected void stopClockRunnable() {
        Log.d("DEBUG", "clockRunnable: stopping..");
        cardView.removeCallbacks(clockUpdateRunnable);
    }
    private Runnable clockUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("DEBUG", "clockRunnable: update clock");
            cardAdapter.notifyDataSetChanged();
            cardView.postDelayed(clockUpdateRunnable, CLOCK_UPDATE_INTERVAL);
        }
    };
    public static final long CLOCK_UPDATE_INTERVAL = 15 * 1000;

    @Override
    public void onStop()
    {
        stopClockRunnable();
        super.onStop();
    }

    public void updateViews() {
        cardAdapter.notifyDataSetChanged();
    }

    protected void initViews(View content)
    {
        cardView = (RecyclerView) content.findViewById(R.id.cardView);
        cardView.setHasFixedSize(true);
        cardView.setLayoutManager(cardLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        cardView.addItemDecoration(cardDecoration);
        //cardView.setOnScrollListener(onCardScrollChanged);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(cardView);
    }

    private RecyclerView.ItemDecoration cardDecoration = new RecyclerView.ItemDecoration()
    {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
        {
            super.getItemOffsets(outRect, view, parent, state);
            Resources r = getResources();
            outRect.top = (int)r.getDimension(R.dimen.card_margin_top);
            outRect.bottom = (int)r.getDimension(R.dimen.card_margin_bottom);
            outRect.left = (int)r.getDimension(R.dimen.card_margin_left);
            outRect.right = (int)r.getDimension(R.dimen.card_margin_right);
        }
    };

    protected void initData(Context context)
    {
        if (context != null)
        {
            cardAdapter = new RomanTimeCardAdapter(getActivity(), new RomanTimeAdapterOptions(getActivity(), info, timezone, is24));
            cardAdapter.setCardAdapterListener(cardListener);
            cardAdapter.initData();
            cardView.setAdapter(cardAdapter);
        }
    }

    private RomanTimeAdapterListener cardListener = new RomanTimeAdapterListener()
    {
        public void onClockClick(int position) {
            announceRomanTime();
        }
        public void onDateClick(int position) {
            showToday();
        }
        public void onCardClick(int position) {
            Toast.makeText(getActivity(), "TODO", Toast.LENGTH_SHORT).show();
            // TODO
        }
        public boolean onCardLongClick(int position) {
            Toast.makeText(getActivity(), "TODO", Toast.LENGTH_SHORT).show();
            return false;
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_roman, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_today:
                showToday();
                return true;

            case R.id.action_date_equinox_spring:
                showSpringEquinox();
                return false;

            case R.id.action_date_solstice_summer:
                showSummerSolstice();
                return false;

            case R.id.action_date_equinox_autumnal:
                showAutumnEquinox();
                return false;

            case R.id.action_date_solstice_winter:
                showWinterSolstice();
                return false;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public void announceRomanTime()
    {
        Context context = getActivity();
        if (context != null)
        {
            int position = cardLayout.findFirstVisibleItemPosition();
            RomanTimeData data = cardAdapter.initData(position);
            int currentHour = RomanTimeData.findRomanHour(Calendar.getInstance(timezone), data);    // [1,24]
            int currentHourOf = ((currentHour - 1) % 12) + 1;            // [1,12]

            String[] phrase = context.getResources().getStringArray(R.array.hour_phrase);
            Snackbar snackbar = Snackbar.make(cardView, DisplayStrings.romanNumeral(context, currentHourOf) + "\n" + phrase[currentHour], Snackbar.LENGTH_LONG);
            snackbar.setText(DisplayStrings.romanNumeral(context, currentHourOf) + ", " + phrase[currentHour]);
            snackbar.show();
        }
    }

    public void showToday() {
        scrollToPosition(RomanTimeCardAdapter.TODAY_POSITION);
    }

    public void showSpringEquinox()
    {
        RomanTimeData data = cardAdapter.initData(RomanTimeCardAdapter.TODAY_POSITION);
        scrollToPosition(cardAdapter.positionForDate(data.getEquinoxSolsticeDates()[0]));
    }

    public void showSummerSolstice()
    {
        RomanTimeData data = cardAdapter.initData(RomanTimeCardAdapter.TODAY_POSITION);
        scrollToPosition(cardAdapter.positionForDate(data.getEquinoxSolsticeDates()[1]));
    }

    public void showAutumnEquinox()
    {
        RomanTimeData data = cardAdapter.initData(RomanTimeCardAdapter.TODAY_POSITION);
        scrollToPosition(cardAdapter.positionForDate(data.getEquinoxSolsticeDates()[2]));
    }

    public void showWinterSolstice()
    {
        RomanTimeData data = cardAdapter.initData(RomanTimeCardAdapter.TODAY_POSITION);
        scrollToPosition(cardAdapter.positionForDate(data.getEquinoxSolsticeDates()[3]));
    }

    public static final int SMOOTHSCROLL_ITEMLIMIT = 28;
    public void scrollToPosition(int position)
    {
        int current = cardLayout.findFirstVisibleItemPosition();

        if (Math.abs(position - current) < SMOOTHSCROLL_ITEMLIMIT) {
            cardView.smoothScrollToPosition(position);
        } else {
            //cardView.scrollToPosition(position < current ? position + SMOOTHSCROLL_ITEMLIMIT : position - SMOOTHSCROLL_ITEMLIMIT);
            //cardView.smoothScrollToPosition(position);
            cardView.scrollToPosition(position);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * RecyclerView.ViewHolder
     */
    public static class RomanTimeViewHolder extends RecyclerView.ViewHolder
    {
        public TextView text_date;
        public TextView text_hour, text_hour_long;
        public TextView text_debug;
        public RomanTimeClockView clockface;

        public RomanTimeViewHolder(@NonNull View itemView, RomanTimeAdapterOptions options)
        {
            super(itemView);
            text_date = (TextView) itemView.findViewById(R.id.text_date);
            text_hour = (TextView) itemView.findViewById(R.id.text_time_romanhour0);
            text_hour_long = (TextView)  itemView.findViewById(R.id.text_time_romanhour1);
            clockface = (RomanTimeClockView) itemView.findViewById(R.id.clockface);
            text_debug = (TextView) itemView.findViewById(R.id.text_time_debug);
        }

        public void onBindViewHolder(@NonNull Context context, int position, RomanTimeData data, RomanTimeAdapterOptions options)
        {
            if (data != null)
            {
                Calendar now = Calendar.getInstance(options.timezone);
                int currentHour = RomanTimeData.findRomanHour(now, data);    // [1,24]
                int currentHourOf = ((currentHour - 1) % 12) + 1;            // [1,12]

                if (text_debug != null && text_debug.getVisibility() == View.VISIBLE)
                {
                    long[] romanHours = data.getRomanHours();
                    CharSequence[] dayHours = new String[12];
                    StringBuilder debugDisplay0 = new StringBuilder("Day");
                    for (int i=0; i<dayHours.length; i++) {
                        dayHours[i] = DisplayStrings.formatTime(context, romanHours[i], options.timezone, options.is24);
                        debugDisplay0.append("\t").append(DisplayStrings.romanNumeral(context, i + 1)).append(": ").append(dayHours[i]);
                    }

                    CharSequence[] nightHours = new String[12];
                    StringBuilder debugDisplay1 = new StringBuilder("Night");
                    for (int i=0; i<nightHours.length; i++) {
                        nightHours[i] = DisplayStrings.formatTime(context, romanHours[12 + i], options.timezone, options.is24);
                        debugDisplay1.append("\t").append(DisplayStrings.romanNumeral(context, i + 1)).append(": ").append(nightHours[i]);
                    }
                    text_debug.setText(debugDisplay0 + "\n" + debugDisplay1);
                }

                CharSequence dateDisplay = DisplayStrings.formatDate(context, data.getDateMillis());
                int dayDelta = position - RomanTimeCardAdapter.TODAY_POSITION;
                text_date.setText(DisplayStrings.formatDateHeader(context, dayDelta, dateDisplay));

                String[] phrase = context.getResources().getStringArray(R.array.hour_phrase);
                text_hour.setText(DisplayStrings.romanNumeral(context, currentHourOf));
                text_hour_long.setText(phrase[currentHour]);

                clockface.setTimeZone(options.timezone);
                clockface.setShowTime(true);
                clockface.set24HourMode(options.is24);
                //clockface.setFlag(RomanTimeClockView.FLAG_SHOW_DATEYEAR, true);
                clockface.setData(data);

            } else {
                text_date.setText("");
                text_hour.setText("");
                text_hour_long.setText("");
            }
        }
    }


    /**
     * AdapterOptions
     */
    public static class RomanTimeAdapterOptions
    {
        public SuntimesInfo suntimes_info;
        public SuntimesInfo.SuntimesOptions suntimes_options;
        public TimeZone timezone;
        public boolean is24;

        public RomanTimeAdapterOptions(Context context, SuntimesInfo info, TimeZone tz, boolean is24) {
            this.suntimes_info = info;
            this.suntimes_options = info.getOptions(context);
            this.timezone = tz;
            this.is24 = is24;
        }
    }

    /**
     * RecyclerView.Adapter
     */
    public class RomanTimeCardAdapter extends RecyclerView.Adapter<RomanTimeViewHolder>
    {
        public static final int MAX_POSITIONS = 2000;
        public static final int TODAY_POSITION = (MAX_POSITIONS / 2);

        protected WeakReference<Context> contextRef;

        public RomanTimeCardAdapter(Context context, RomanTimeAdapterOptions options)
        {
            contextRef = new WeakReference<>(context);
            this.options = options;
        }

        @NonNull
        @Override
        public RomanTimeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            LayoutInflater layout = LayoutInflater.from(viewGroup.getContext());
            View view = layout.inflate(R.layout.card_romantime, viewGroup, false);
            return new RomanTimeViewHolder(view, options);
        }

        @Override
        public void onBindViewHolder(@NonNull RomanTimeViewHolder holder, int position)
        {
            Context context = contextRef.get();
            if (context != null)
            {
                holder.onBindViewHolder(context, position, initData(position), options);
                attachClickListeners(holder, position);
            }
        }

        @Override
        public void onViewRecycled(@NonNull RomanTimeViewHolder holder)
        {
            detachClickListeners(holder);
            holder.clockface.invalidate();
            int position = holder.getAdapterPosition();
            if (position >= 0) {
                data.remove(position);
            }
        }

        private RomanTimeAdapterOptions options;
        public void setCardOptions(RomanTimeAdapterOptions options) {
            Log.d("DEBUG", "setCardOptions");
            this.options = options;
            cardAdapter.invalidateData();
            cardAdapter.initData();
        }
        public RomanTimeAdapterOptions getOptions() {
            return options;
        }

        @Override
        public int getItemCount() {
            return MAX_POSITIONS;
        }

        @SuppressLint("UseSparseArrays")
        protected HashMap<Integer, RomanTimeData> data = new HashMap<>();
        public HashMap<Integer, RomanTimeData> getData() {
            return data;
        }

        public RomanTimeData initData()
        {
            calculator = new RomanTimeCalculator();
            RomanTimeData d;
            data.clear();
            invalidated = false;

            initData(TODAY_POSITION - 1);
            d = initData(TODAY_POSITION);
            initData(TODAY_POSITION + 1);
            initData(TODAY_POSITION + 2);
            notifyDataSetChanged();
            return d;
        }

        public RomanTimeData initData(int position)
        {
            RomanTimeData d = data.get(position);
            if (d == null && !invalidated) {
                data.put(position, d = createData(position));   // data gets removed in onViewRecycled
                //Log.d("DEBUG", "add data " + position);
            }
            return d;
        }

        protected RomanTimeData createData(int position)
        {
            Calendar date = Calendar.getInstance(options.timezone);
            date.add(Calendar.DATE, position - TODAY_POSITION);
            date.set(Calendar.HOUR_OF_DAY, 12);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            return calculateData(new RomanTimeData(date.getTimeInMillis(), info.location[1], info.location[2], info.location[3]));
        }

        private RomanTimeCalculator calculator = new RomanTimeCalculator();
        private RomanTimeData calculateData(RomanTimeData romanTimeData)
        {
            Context context = contextRef.get();
            if (context != null)
            {
                ContentResolver resolver = context.getContentResolver();
                if (resolver != null) {
                    calculator.calculateData(resolver, romanTimeData);
                } else {
                    Log.e(getClass().getSimpleName(), "createData: null contentResolver!");
                }
            } else {
                Log.e(getClass().getSimpleName(), "createData: null context!");
            }
            return romanTimeData;
        }

        public int positionForDate(long dateMillis)
        {
            RomanTimeData dataToday = data.get(TODAY_POSITION);
            long todayMillis = dataToday.getDateMillis();
            return TODAY_POSITION + (int)((dateMillis - todayMillis) / (24 * 60 * 60 * 1000)) + 1;
        }

        private boolean invalidated = false;
        public void invalidateData()
        {
            invalidated = true;
            data.clear();
            notifyDataSetChanged();
        }

        private void attachClickListeners(@NonNull final RomanTimeViewHolder holder, int position)
        {
            holder.text_date.setOnClickListener(onDateClick(position));
            holder.clockface.setOnClickListener(onClockClick(position));
            //holder.layout_front.setOnClickListener(onCardClick(holder));
            //holder.layout_front.setOnLongClickListener(onCardLongClick(holder));
        }

        private void detachClickListeners(@NonNull RomanTimeViewHolder holder)
        {
            holder.text_date.setOnClickListener(null);
            holder.clockface.setOnClickListener(null);
            //holder.layout_front.setOnClickListener(null);
            //holder.layout_front.setOnLongClickListener(null);
        }

        public void setCardAdapterListener( @NonNull RomanTimeAdapterListener listener ) {
            adapterListener = listener;
        }
        private RomanTimeAdapterListener adapterListener = new RomanTimeAdapterListener();

        private View.OnClickListener onClockClick(final int position) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterListener.onClockClick(position);
                }
            };
        }
        private View.OnClickListener onDateClick(final int position) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterListener.onDateClick(position);
                }
            };
        }
        private View.OnClickListener onCardClick(@NonNull final RomanTimeViewHolder holder) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterListener.onCardClick(holder.getAdapterPosition());
                }
            };
        }
        private View.OnLongClickListener onCardLongClick(@NonNull final RomanTimeViewHolder holder) {
            return new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //holder.text_debug.setVisibility( holder.text_debug.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                    return adapterListener.onCardLongClick(holder.getAdapterPosition());
                }
            };
        }
    }

    /**
     * AdapterListener
     */
    public static class RomanTimeAdapterListener
    {
        public void onClockClick(int position) {}
        public void onDateClick(int position) {}
        public void onCardClick(int position) {}
        public boolean onCardLongClick(int position) { return false; }
    }

    /**
     * CardViewScroller
     */
    public static class CardScroller extends LinearSmoothScroller
    {
        private static final float MILLISECONDS_PER_INCH = 125f;

        public CardScroller(Context context) {
            super(context);
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
        }

        @Override protected int getVerticalSnapPreference() {
            return LinearSmoothScroller.SNAP_TO_START;
        }
    }

    public static TimeZone getTimeZone(Context context, SuntimesInfo info)
    {
        if (info.timezoneMode == null || info.timezoneMode.equals("CUSTOM_TIMEZONE")) {
            return TimeZone.getTimeZone(info.timezone);

        } else if (info.timezoneMode.equals("SOLAR_TIME")) {
            if (info.solartimeMode.equals("LOCAL_MEAN_TIME"))
                return getLocalMeanTZ(context, info.location[2]);
            else return getApparantSolarTZ(context, info.location[2]);

        } else {
            return TimeZone.getDefault();
        }
    }

    public static TimeZone getLocalMeanTZ(Context context, String longitude) {
        return new TimeZoneHelper.LocalMeanTime(Double.parseDouble(longitude), context.getString(R.string.solartime_localmean));
    }

    public static TimeZone getApparantSolarTZ(Context context, String longitude) {
        return new TimeZoneHelper.ApparentSolarTime(Double.parseDouble(longitude), context.getString(R.string.solartime_apparent));
    }
}
