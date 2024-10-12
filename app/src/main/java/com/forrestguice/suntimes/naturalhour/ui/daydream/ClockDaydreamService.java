/**
    Copyright (C) 2024 Forrest Guice
    This file is part of NaturalHour.

    NaturalHour is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    NaturalHour is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with NaturalHour.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.forrestguice.suntimes.naturalhour.ui.daydream;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.service.dreams.DreamService;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.naturalhour.AppSettings;
import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourCalculator;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourData;
import com.forrestguice.suntimes.naturalhour.ui.clockview.ClockColorValues;
import com.forrestguice.suntimes.naturalhour.ui.clockview.ClockColorValuesCollection;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockView;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValues;
import com.forrestguice.suntimes.naturalhour.ui.widget.WidgetPreferenceFragment;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

@TargetApi(17)
public class ClockDaydreamService extends DreamService
{
    public static final int APPWIDGET_ID = -100;

    protected int appWidgetId = APPWIDGET_ID;
    protected View mainLayout;
    protected View clockLayout;
    protected DreamAnimation animation;
    protected NaturalHourClockView clockView;
    protected ColorValues clockAppearance;

    protected int getLayoutResID() {
        return R.layout.layout_daydream_clock;
    }

    protected SuntimesInfo info;
    protected void initSuntimesInfo() {
        if (info == null) {
            info = SuntimesInfo.queryInfo(this);
        }
    }

    protected NaturalHourCalculator calculator;
    protected NaturalHourCalculator initCalculator() {
        return NaturalHourClockBitmap.getCalculator(AppSettings.getClockIntValue(getApplicationContext(), NaturalHourClockBitmap.VALUE_HOURMODE));
    }

    protected String[] getLocation() {
        return info != null && info.location != null && info.location.length >= 4 ? info.location : new String[] {"", "0", "0", "0"};   // TODO: default/fallback value
    }
    protected TimeZone getTimezone(Context context)
    {
        int tzMode = AppSettings.getClockIntValue(context, widgetPrefix() + AppSettings.KEY_MODE_TIMEZONE, AppSettings.TZMODE_DEFAULT );
        return AppSettings.fromTimeZoneMode(context, tzMode, info);
    }
    protected boolean is24(Context context)
    {
        int timeMode = AppSettings.getClockIntValue(context, widgetPrefix() + AppSettings.KEY_MODE_TIMEFORMAT, AppSettings.TIMEMODE_DEFAULT);
        return AppSettings.fromTimeFormatMode(context, timeMode, info);
    }
    protected String widgetPrefix() {
        return WidgetPreferenceFragment.widgetKeyPrefix(appWidgetId);
    }

    protected NaturalHourData initData(Context context)
    {
        String[] location = getLocation();
        Calendar date = Calendar.getInstance(getTimezone(context));
        return calculateData(new NaturalHourData(date.getTimeInMillis(), location[1], location[2], location[3]));
    }
    protected NaturalHourData calculateData(NaturalHourData naturalHourData)
    {
        ContentResolver resolver = getContentResolver();
        if (resolver != null) {
            calculator = initCalculator();
            calculator.calculateData(resolver, naturalHourData);
        } else Log.e(getClass().getSimpleName(), "createData: null contentResolver!");
        return naturalHourData;
    }

    protected void initViews(Context context)
    {
        mainLayout = findViewById(R.id.layout_main);
        clockLayout = findViewById(R.id.layout_clock);
        if (clockLayout != null)
        {
            clockView = findViewById(R.id.clock);
            clockView.setTimeZone(getTimezone(context));
            clockView.set24HourMode(is24(context));
            clockView.setShowTime(true);

            ClockColorValuesCollection<ClockColorValues> colors = new ClockColorValuesCollection<>(context);
            clockAppearance = colors.getSelectedColors(context, appWidgetId);
            clockView.setColors(clockAppearance);

            for (String key : NaturalHourClockBitmap.FLAGS) {
                String widgetKey = widgetPrefix() + key;
                if (AppSettings.containsKey(context, widgetKey)) {
                    clockView.setFlag(key, AppSettings.getClockFlag(context, widgetKey));
                }
            }
            for (String key : NaturalHourClockBitmap.VALUES) {
                String widgetKey = widgetPrefix() + key;
                if (AppSettings.containsKey(context, widgetKey)) {
                    clockView.setValue(key, AppSettings.getClockIntValue(context, widgetKey));
                }
            }
            clockView.setFlag(NaturalHourClockBitmap.FLAG_SHOW_SECONDS, true);
            clockView.setData(initData(context));
        }
        animation = new DreamAnimation(context);
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        setScreenBright(false);
        setInteractive(false);
        setFullscreen(true);
        initSuntimesInfo();
        setContentView(getLayoutResID());
        initViews(this);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onDreamingStarted()
    {
        super.onDreamingStarted();
        if (clockView != null) {
            clockView.startUpdateTask();
        }
        if (animation != null) {
            animation.startAnimation();
        }
    }

    @Override
    public void onDreamingStopped()
    {
        if (clockView != null) {
            clockView.stopUpdateTask();
        }
        if (animation != null) {
            animation.stopAnimation();
        }
        super.onDreamingStopped();
    }

    /**
     * DreamAnimation
     */
    protected class DreamAnimation
    {
        protected long alpha_duration_in = 7500;
        protected long alpha_duration_in_pause = 1000;
        protected long alpha_duration_out = 7500;
        protected long alpha_duration_out_pause = 14000;
        protected float alpha_value_min = 0.02f;
        protected float alpha_value_max = 1.0f;
        protected float scale_value_min = 0.60f;
        protected float scale_value_max = 1.0f;
        protected float wander_value_x = 100f;
        protected float wander_value_y = 200f;

        protected float rotate_value_in = 15;
        protected float rotate_value_out = 15;

        public DreamAnimation() {
        }

        public DreamAnimation(Context context)
        {
            Resources r = context.getResources();
            alpha_duration_in = r.getInteger(R.integer.anim_daydream_alpha_duration_in);
            alpha_duration_in_pause = r.getInteger(R.integer.anim_daydream_alpha_duration_in_pause);
            alpha_duration_out = r.getInteger(R.integer.anim_daydream_alpha_duration_out);
            alpha_duration_out_pause = r.getInteger(R.integer.anim_daydream_alpha_duration_out_pause);
        }

        protected boolean option_scale = true;
        public void setOption_scale(boolean value) {
            option_scale = value;
        }

        protected boolean option_wander = true;
        public void setOption_wander(boolean value) {
            option_wander = value;
        }

        protected boolean option_flip = true;
        public void setOption_flip(boolean value) {
            option_flip = value;
        }

        public void startAnimation()
        {
            isAnimated = true;
            animateFadeIn(clockLayout);
        }

        public void stopAnimation()
        {
            isAnimated = false;
            if (clockLayout != null) {
                clockLayout.clearAnimation();
            }
        }

        protected boolean isAnimated = false;
        public boolean isAnimated() {
            return isAnimated;
        }

        protected void animateFadeIn(final View view)
        {
            if (view != null)
            {
                ViewPropertyAnimator animation = view.animate();
                if (option_scale)
                {
                    view.setScaleX(scale_value_min);
                    view.setScaleY(scale_value_min);
                    animation.scaleY(scale_value_max).scaleX(scale_value_max);
                }
                if (option_wander)
                {
                    float[] translateBy = getRandomDiagonalTranslation(view);
                    animation.translationXBy(translateBy[0]);
                    animation.translationYBy(translateBy[1]);
                }
                if (option_flip) {
                    view.setRotation(-rotate_value_in);
                    animation.rotation(0);
                }
                view.setAlpha(alpha_value_min);
                animation.alpha(alpha_value_max)

                        .setDuration(alpha_duration_in)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .setStartDelay(alpha_duration_in_pause)
                        .setListener(new AnimatorListenerAdapter()
                        {
                            @Override
                            public void onAnimationEnd(Animator animation)
                            {
                                super.onAnimationEnd(animation);
                                if (isAnimated) {
                                    animateFadeOut(view);
                                }
                            }
                        });
            }
        }
        protected void animateFadeOut(final View view)
        {
            if (view != null)
            {
                ViewPropertyAnimator animation = view.animate();
                if (option_scale) {
                    view.setScaleX(scale_value_max);
                    view.setScaleY(scale_value_max);
                    animation.scaleY(scale_value_min).scaleX(scale_value_min);
                }
                if (option_wander)
                {
                    float[] translateBy = getRandomDiagonalTranslation(view);
                    animation.translationXBy(translateBy[0]);
                    animation.translationYBy(translateBy[1]);
                }
                if (option_flip) {
                    view.setRotation(0);
                    animation.rotation(rotate_value_out);
                }
                view.setAlpha(alpha_value_max);
                animation.alpha(alpha_value_min)
                        .setDuration(alpha_duration_out)
                        .setStartDelay(alpha_duration_out_pause)
                        .setListener(new AnimatorListenerAdapter()
                        {
                            @Override
                            public void onAnimationEnd(Animator animation)
                            {
                                super.onAnimationEnd(animation);
                                if (isAnimated) {
                                    setRandomViewPosition(view);
                                    animateFadeIn(view);
                                }
                            }
                        });
            }
        }

        protected float[] getRandomDiagonalTranslation(View view)
        {
            Random random = new Random();
            float[] result = new float[2];

            float x = view.getX();
            float right_max = mainLayout.getWidth() - (x + view.getWidth());
            result[0] = (random.nextBoolean() ? 1 : -1) * wander_value_x;
            result[0] = result[0] > 0 ? Math.min(result[0], right_max) : Math.max(result[0], -x);

            float y = view.getY();
            float bottom_max = mainLayout.getHeight() - (y + view.getHeight());
            result[1] = (random.nextBoolean() ? 1 : -1) * wander_value_y;
            result[1] = result[1] > 0 ? Math.min(result[1], bottom_max) : Math.max(result[1], -y);
            return result;
        }

        protected void setRandomViewPosition(View view)
        {
            if (view != null)
            {
                view.setX((float)(Math.random() * (mainLayout.getWidth() - view.getWidth())));
                view.setY((float)(Math.random() * (mainLayout.getHeight() - view.getHeight())));
            }
        }
    }

}
