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
import android.animation.ArgbEvaluator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.service.dreams.DreamService;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;

import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.annotation.Nullable;
import com.forrestguice.suntimes.naturalhour.AppSettings;
import com.forrestguice.suntimes.naturalhour.BuildConfig;
import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourCalculator;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourData;
import com.forrestguice.suntimes.naturalhour.ui.clockview.ClockColorValues;
import com.forrestguice.suntimes.naturalhour.ui.clockview.ClockColorValuesCollection;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockView;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValues;
import com.forrestguice.suntimes.naturalhour.ui.widget.WidgetSettings;

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
    protected DreamAnimationInterface animation;
    protected NaturalHourClockView clockView;
    protected ColorValues clockAppearance;

    protected int getLayoutResID() {
        return R.layout.activity_daydream;
    }

    protected SuntimesInfo info;
    protected void initSuntimesInfo() {
        if (info == null) {
            info = SuntimesInfo.queryInfo(this);
        }
    }

    protected NaturalHourClockBitmap bitmapHelper = null;
    protected NaturalHourClockBitmap getBitmapHelper(Context context)
    {
        if (bitmapHelper == null) {
            bitmapHelper = createBitmapHelper(context);
        }
        return bitmapHelper;
    }
    protected NaturalHourClockBitmap createBitmapHelper(Context context) {
        return new ClockDaydreamBitmap(context, 0);
    }

    protected NaturalHourCalculator calculator;
    protected NaturalHourCalculator initCalculator() {
        return NaturalHourClockBitmap.getCalculator(AppSettings.getClockIntValue(getApplicationContext(), NaturalHourClockBitmap.VALUE_HOURMODE, getBitmapHelper(getApplicationContext())));
    }

    protected String[] getLocation() {
        return info != null && info.location != null && info.location.length >= 4 ? info.location : new String[] {"", "0", "0", "0"};   // TODO: default/fallback value
    }
    protected TimeZone getTimezone(Context context)
    {
        int tzMode = AppSettings.getClockIntValue(context, widgetPrefix() + AppSettings.KEY_MODE_TIMEZONE, context.getResources().getInteger(R.integer.daydream_tzmode));
        return AppSettings.fromTimeZoneMode(context, tzMode, info);
    }
    protected int getTimeFormat(Context context)
    {
        int timeMode = AppSettings.getClockIntValue(context, widgetPrefix() + AppSettings.KEY_MODE_TIMEFORMAT, AppSettings.TIMEMODE_DEFAULT);
        return AppSettings.fromTimeFormatMode(context, timeMode, info);
    }
    protected int getBackgroundMode(Context context) {
        return AppSettings.getClockIntValue(context, widgetPrefix() + DaydreamSettings.KEY_MODE_BACKGROUND, context.getResources().getInteger(R.integer.daydream_bgmode));
    }
    protected String widgetPrefix() {
        return WidgetSettings.widgetKeyPrefix(appWidgetId);
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
            clockView.setTimeFormat(getTimeFormat(context));
            clockView.setShowTime(true);

            ClockColorValuesCollection<ClockColorValues> colors = new ClockColorValuesCollection<>(context);
            clockAppearance = colors.getSelectedColors(context, appWidgetId);
            if (clockAppearance == null) {
                clockAppearance = colors.getDefaultColors(context);
            }
            clockView.setColors(clockAppearance);
            setBackgroundColor(context);

            for (String key : NaturalHourClockBitmap.FLAGS) {
                String widgetKey = widgetPrefix() + key;
                if (AppSettings.containsKey(context, widgetKey)) {
                    clockView.setFlag(key, AppSettings.getClockFlag(context, widgetKey, getBitmapHelper(context)));
                    //Log.d("DEBUG", "setFlag: " + key + " :: " + AppSettings.getClockFlag(context, widgetKey, getBitmapHelper(context)));
                }
            }
            for (String key : NaturalHourClockBitmap.VALUES) {
                String widgetKey = widgetPrefix() + key;
                if (AppSettings.containsKey(context, widgetKey)) {
                    clockView.setValue(key, AppSettings.getClockIntValue(context, widgetKey, getBitmapHelper(context)));
                }
            }
            clockView.setData(initData(context));
        }
    }

    protected void setBackgroundColor(Context context)
    {
        switch (getBackgroundMode(context))
        {
            case AppSettings.BGMODE_BLACK:
                mainLayout.setBackgroundColor(Color.BLACK);
                break;

            case AppSettings.BGMODE_COLOR:
            default:
                mainLayout.setBackgroundColor(clockAppearance.getColor(ClockColorValues.COLOR_BACKGROUND));
                break;
        }
    }

    protected DreamAnimationInterface initAnimation(Context context)
    {
        return new WanderingDreamAnimation(context)
        {
            @Override
            protected TimeInterpolator getFadeInInterpolator() {
                switch (random(0, 3)) {
                    case 0: return new BounceInterpolator();
                    default: return new AccelerateDecelerateInterpolator();
                }
            }

            @Override
            protected TimeInterpolator getFadeOutInterpolator() {
                return new AccelerateDecelerateInterpolator();
            }

            @Override
            protected TimeInterpolator getWanderingInterpolator() {
                switch (random(0, 6)) {
                    case 0: return new BounceInterpolator();
                    case 1: return new AnticipateOvershootInterpolator();
                    default: return new AccelerateDecelerateInterpolator();
                }
            }
        };
    }

    @Override
    public void onAttachedToWindow()
    {
        if (BuildConfig.DEBUG) {
            Log.d("DEBUG", "daydream: onAttachedToWindow");
        }
        super.onAttachedToWindow();

        animation = initAnimation(this);
        setScreenBright(animation.isScreenBright());
        setInteractive(animation.isInteractive());
        setFullscreen(animation.isFullscreen());
        setDisplayCutoutMode();

        initSuntimesInfo();
        setContentView(getLayoutResID());
        initViews(this);
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams attrs)
    {
        if (BuildConfig.DEBUG) {
            Log.d("DEBUG", "daydream: onWindowAttributesChanged");
        }
    }

    protected void setDisplayCutoutMode()
    {
        if (Build.VERSION.SDK_INT >= 28)
        {
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            if (Build.VERSION.SDK_INT >= 30) {
                layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
            } else layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(layoutParams);
        }
    }

    @TargetApi(21)
    @Override
    public void onWakeUp()
    {
        if (BuildConfig.DEBUG) {
            Log.d("DEBUG", "daydream: onWakeUp (0)");
        }
        animation.gracefullyStopAnimation(new Runnable()
        {
            @Override
            public void run() {
                if (BuildConfig.DEBUG) {
                    Log.d("DEBUG", "daydream: onWakeUp (1)");
                }
                finish();
            }
        });
    }

    @Override
    public void onDetachedFromWindow() {
        if (BuildConfig.DEBUG) {
            Log.d("DEBUG", "daydream: onDetachedFromWindow");
        }
        super.onDetachedFromWindow();
    }

    @Override
    public void onDreamingStarted()
    {
        if (BuildConfig.DEBUG) {
            Log.d("DEBUG", "daydream: onDreamingStarted");
        }
        super.onDreamingStarted();
        startUpdateTask();                  // this loop triggers update on hours/minutes
        if (clockView != null) {
            clockView.startUpdateTask();    // this loop updates seconds
        }
        if (animation != null) {
            animation.startAnimation();
        }
    }

    @Override
    public void onDreamingStopped()
    {
        if (BuildConfig.DEBUG) {
            Log.d("DEBUG", "daydream: onDreamingStopped");
        }
        stopUpdateTask();
        if (clockView != null) {
            clockView.stopUpdateTask();
        }
        if (animation != null) {
            animation.stopAnimation();
        }
        super.onDreamingStopped();
    }

    public void startUpdateTask()
    {
        if (BuildConfig.DEBUG) {
            Log.d("DEBUG", "daydream: update starting..");
        }
        if (clockView != null) {
            clockView.post(updateRunnable);
        }
    }
    public void stopUpdateTask()
    {
        if (BuildConfig.DEBUG) {
            Log.d("DEBUG", "daydream: update stopping..");
        }
        if (clockView != null) {
            clockView.removeCallbacks(updateRunnable);
        }
    }
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (BuildConfig.DEBUG) {
                Log.d("DEBUG", "daydream: update tick");
            }
            if (clockView != null) {
                clockView.updateBase();
                clockView.postDelayed(updateRunnable, UPDATE_INTERVAL);
            }
        }
    };
    public static final long UPDATE_INTERVAL = 15 * 1000;

    /**
     * DreamAnimationInterface
     */
    public interface DreamAnimationInterface
    {
        void startAnimation();
        void stopAnimation();
        void gracefullyStopAnimation(@Nullable Runnable onAnimationEnd);
        boolean isAnimated();

        boolean isFullscreen();
        boolean isInteractive();
        boolean isScreenBright();
    }

    /**
     * DreamAnimation
     */
    protected class WanderingDreamAnimation implements DreamAnimationInterface
    {
        public boolean option_interactive;
        public boolean option_fullscreen;
        public boolean option_screenbright;

        protected boolean option_wander;
        protected boolean option_randomPosition;
        protected boolean option_fade_scale, option_fade_wander, option_fade_rotate;
        protected boolean option_background_pulse;

        protected long option_background_pulse_duration;
        protected long hide_duration_min, hide_duration_max;
        protected long wait_duration_min, wait_duration_max;
        protected long fade_duration_in, fade_duration_out;
        protected float alpha_value_min, alpha_value_max;
        protected float scale_value_min, scale_value_max;
        protected float rotate_value_in, rotate_value_out;
        protected float wander_value_x = 200f;
        protected float wander_value_y = 200f;
        protected int option_rotate_chance = 2;

        public WanderingDreamAnimation(Resources r)
        {
            option_fullscreen = r.getBoolean(R.bool.daydream_fullscreen);
            option_interactive = r.getBoolean(R.bool.daydream_interactive);
            option_screenbright = r.getBoolean(R.bool.daydream_screenbright);

            option_randomPosition = r.getBoolean(R.bool.anim_daydream_option_randomPosition);
            option_wander = r.getBoolean(R.bool.anim_daydream_option_wander);
            option_fade_scale = r.getBoolean(R.bool.anim_daydream_option_fade_scale);
            option_fade_wander = r.getBoolean(R.bool.anim_daydream_option_fade_wander);
            option_fade_rotate = r.getBoolean(R.bool.anim_daydream_option_fade_rotate);
            option_background_pulse = r.getBoolean(R.bool.anim_daydream_option_background_pulse);
            option_background_pulse_duration = r.getInteger(R.integer.anim_daydream_background_pulse_duration);

            scale_value_min = Float.parseFloat(r.getString(R.string.anim_daydream_scale_value_min));
            scale_value_max = Float.parseFloat(r.getString(R.string.anim_daydream_scale_value_max));

            rotate_value_in = Float.parseFloat(r.getString(R.string.anim_daydream_rotate_value_in));
            rotate_value_out = Float.parseFloat(r.getString(R.string.anim_daydream_rotate_value_out));

            alpha_value_min = Float.parseFloat(r.getString(R.string.anim_daydream_alpha_value_min));
            alpha_value_max = Float.parseFloat(r.getString(R.string.anim_daydream_alpha_value_max));

            fade_duration_in = r.getInteger(R.integer.anim_daydream_fadein_duration);
            fade_duration_out = r.getInteger(R.integer.anim_daydream_fadeout_duration);

            hide_duration_min = r.getInteger(R.integer.anim_daydream_hide_duration_min);
            hide_duration_max = r.getInteger(R.integer.anim_daydream_hide_duration_max);

            wait_duration_min = r.getInteger(R.integer.anim_daydream_wait_duration_min);
            wait_duration_max = r.getInteger(R.integer.anim_daydream_wait_duration_max);
        }
        public WanderingDreamAnimation(Context context)
        {
            this(context.getResources());
            option_fullscreen = DaydreamSettings.getDaydreamFlag(context, appWidgetId, DaydreamSettings.KEY_MODE_FULLSCREEN, DaydreamSettings.DEF_MODE_FULLSCREEN);
            option_interactive = DaydreamSettings.getDaydreamFlag(context, appWidgetId, DaydreamSettings.KEY_MODE_INTERACTIVE, DaydreamSettings.DEF_MODE_INTERACTIVE);
            option_screenbright = DaydreamSettings.getDaydreamFlag(context, appWidgetId, DaydreamSettings.KEY_MODE_SCREENBRIGHT, DaydreamSettings.DEF_MODE_SCREENBRIGHT);
            option_background_pulse_duration = DaydreamSettings.getDaydreamIntValue(context, appWidgetId, DaydreamSettings.KEY_ANIM_BGPULSE_DURATION, (int) option_background_pulse_duration);
        }

        @Override
        public void startAnimation()
        {
            isAnimated = true;
            if (option_background_pulse
                    && getBackgroundMode(ClockDaydreamService.this) == AppSettings.BGMODE_COLOR)
            {
                int[] bgColors = new int[]{ clockAppearance.getColor(ClockColorValues.COLOR_BACKGROUND),
                                            clockAppearance.getColor(ClockColorValues.COLOR_BACKGROUND_ALT) };
                startAnimateBackground(bgColors, option_background_pulse_duration, new AccelerateInterpolator());
            }
            animateFadeIn(clockLayout);
        }

        @Override
        public void stopAnimation()
        {
            isAnimated = false;
            if (clockLayout != null) {
                clockLayout.clearAnimation();
            }
            stopAnimateBackground();
        }

        @Override
        public void gracefullyStopAnimation(@Nullable Runnable onAnimationEnd)
        {
            isAnimated = false;
            if (clockLayout != null) {
                clockLayout.clearAnimation();

                if (onAnimationEnd != null) {
                    animateFadeOut(clockLayout, 1000, onAnimationEnd, 0);
                }
            }
            stopAnimateBackground();   // TODO: graceful stop
        }

        protected boolean isAnimated = false;
        @Override
        public boolean isAnimated() {
            return isAnimated;
        }

        @Override
        public boolean isFullscreen() {
            return option_fullscreen;
        }

        @Override
        public boolean isInteractive() {
            return option_interactive;
        }

        @Override
        public boolean isScreenBright() {
            return option_screenbright;
        }

        protected void animateFadeIn(final View view)
        {
            if (BuildConfig.DEBUG) {
                Log.d("DEBUG", "animateFadeIn");
            }
            if (view != null)
            {
                ViewPropertyAnimator animation = view.animate();
                if (option_fade_scale)
                {
                    view.setScaleX(scale_value_min);
                    view.setScaleY(scale_value_min);
                    animation.scaleY(scale_value_max).scaleX(scale_value_max);
                }
                if (option_fade_wander)
                {
                    float[] translateBy = getRandomDiagonalTranslation(view, (int) wander_value_x, (int) wander_value_y);
                    animation.translationXBy(translateBy[0]);
                    animation.translationYBy(translateBy[1]);
                }
                if (option_fade_rotate)
                {
                    if (option_rotate_chance <= 1 || random(0, option_rotate_chance) == 0) {
                        view.setRotation(-rotate_value_in);
                        animation.rotation(0);
                    } else {
                        view.setRotation(0);
                    }
                }
                view.setAlpha(alpha_value_min);
                animation.alpha(alpha_value_max)
                        .setInterpolator(getFadeInInterpolator())
                        .setDuration(fade_duration_in)
                        .setListener(wanderWaitOrFade(view));
            }
        }
        protected void animateFadeOut(final View view)
        {
            animateFadeOut(view, fade_duration_out, new Runnable() {
                @Override
                public void run() {
                    if (isAnimated) {
                        if (option_randomPosition)
                            setRandomViewPosition(view);
                        else centerViewPosition(view);
                        animateFadeIn(view);
                    }
                }
            }, random(hide_duration_min, hide_duration_max));
        }
        protected void animateFadeOut(final View view, long duration, @Nullable Runnable onAnimationEnd, long onAnimationEndDelay)
        {
            if (BuildConfig.DEBUG) {
                Log.d("DEBUG", "animateFadeOut");
            }
            if (view != null)
            {
                ViewPropertyAnimator animation = view.animate();
                if (option_fade_scale) {
                    view.setScaleX(scale_value_max);
                    view.setScaleY(scale_value_max);
                    animation.scaleY(scale_value_min).scaleX(scale_value_min);
                }
                if (option_fade_wander)
                {
                    float[] translateBy = getRandomDiagonalTranslation(view, (int) wander_value_x, (int) wander_value_y);
                    animation.translationXBy(translateBy[0]);
                    animation.translationYBy(translateBy[1]);
                }
                if (option_fade_rotate)
                {
                    if (option_rotate_chance <= 1 || random(0, option_rotate_chance) == 0) {
                        view.setRotation(0);
                        animation.rotation(rotate_value_out);
                    }
                }
                view.setAlpha(alpha_value_max);
                animation.alpha(alpha_value_min)
                        .setInterpolator(getFadeOutInterpolator())
                        .setDuration(duration)
                        .setListener(new AnimatorListenerAdapter()
                        {
                            @Override
                            public void onAnimationEnd(Animator animation)
                            {
                                super.onAnimationEnd(animation);
                                if (onAnimationEnd != null) {
                                    view.postDelayed(onAnimationEnd, onAnimationEndDelay);
                                }
                            }
                        });
            }
        }

        protected void animateWanderAway(final View view)
        {
            if (BuildConfig.DEBUG) {
                Log.d("DEBUG", "animateWanderAway");
            }
            if (view != null)
            {
                float[] translateBy = getRandomDiagonalTranslation(view, null, null);
                view.animate()
                        .setInterpolator(getWanderingInterpolator())
                        .translationXBy(translateBy[0])
                        .translationYBy(translateBy[1])
                        .setDuration(fade_duration_in)
                        .setListener(wanderWaitOrFade(view));
            }
        }

        protected TimeInterpolator getFadeInInterpolator() {
            return new AccelerateDecelerateInterpolator();
        }
        protected TimeInterpolator getFadeOutInterpolator() {
            return new AccelerateDecelerateInterpolator();
        }
        protected TimeInterpolator getWanderingInterpolator() {
            return new AccelerateDecelerateInterpolator();
        }

        protected AnimatorListenerAdapter wanderWaitOrFade(final View view)
        {
            return new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    super.onAnimationEnd(animation);
                    if (isAnimated)
                    {
                        view.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                switch (random(0,6))
                                {
                                    case 0: animateFadeOut(view); break;
                                    case 1: view.postDelayed(this, random(wait_duration_min, wait_duration_max)); break;
                                    default:
                                        if (option_wander)
                                            animateWanderAway(view);
                                        else animateFadeOut(view);
                                        break;
                                }
                            }
                        }, random(wait_duration_min, wait_duration_max));
                    }
                }
            };
        }

        protected long random(long min, long max) {
            return (long) ((Math.random() * (max - min)) + min);
        }
        protected int random(int min, int max) {
            return (int) ((Math.random() * (max - min)) + min);
        }

        protected float[] getRandomDiagonalTranslation(View view, @Nullable Integer byX, @Nullable Integer byY)
        {
            if (byX == null) {
                byX = view.getWidth();
            }
            if (byY == null) {
                byY = view.getHeight();
            }
            return getRandomDiagonalTranslation(view, (int) wander_value_x, byX, (int) wander_value_y, byY);
        }

        protected float[] getRandomDiagonalTranslation(View view, int xMin, int xMax, int yMin, int yMax)
        {
            Random random = new Random();
            float[] result = new float[2];
            int byX = (xMin != xMax) ? random(xMin, xMax) : xMin;
            int byY = (yMin != yMax) ? random(yMin, yMax) : yMin;

            float x = view.getX();
            float right_max = mainLayout.getWidth() - (x + view.getWidth());
            result[0] = (random.nextBoolean() ? 1 : -1) * byX;
            result[0] = result[0] > 0 ? Math.min(result[0], right_max) : Math.max(result[0], -x);

            float y = view.getY();
            float bottom_max = mainLayout.getHeight() - (y + view.getHeight());
            result[1] = (random.nextBoolean() ? 1 : -1) * byY;
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

        protected void centerViewPosition(View view)
        {
            if (view != null)
            {
                view.setX((float)((mainLayout.getWidth() / 2 - view.getWidth() / 2)));
                view.setY((float)((mainLayout.getHeight() / 2 - view.getHeight() / 2)));
            }
        }

        protected Object backgroundAnimation;
        protected Object startAnimateBackground(int[] animColors, long duration, TimeInterpolator interpolator)
        {
            stopAnimateBackground();
            return animateColors(animColors, duration, true, interpolator, new ColorableView<>(mainLayout));
        }

        protected void stopAnimateBackground()
        {
            if (backgroundAnimation != null)
            {
                ValueAnimator animator = (ValueAnimator) backgroundAnimation;
                animator.cancel();
                backgroundAnimation = null;
            }
        }
    }

    /**
     * Colorable
     */
    public interface Colorable {
        void setColor(int color);
    }
    public static class ColorableView<T extends View> implements Colorable
    {
        protected final T view;
        public ColorableView(T v) {
            view = v;
        }
        public void setColor(int color) {
            if (view != null) {
                view.setBackgroundColor(color);
            }
        }
        public T getView() {
            return view;
        }
    }

    @Nullable
    private static ValueAnimator animateColors(int[] colors, long duration, boolean repeat, @Nullable TimeInterpolator interpolator, final Colorable... views) {
        return animateColors(colors, duration, repeat, interpolator, null, views);
    }

    @Nullable
    private static ValueAnimator animateColors(int[] colors, long duration, boolean repeat, @Nullable TimeInterpolator interpolator, @Nullable final ValueAnimator.AnimatorUpdateListener listener, final Colorable... views)
    {
        if (views != null && views.length > 0)
        {
            final ValueAnimator animation = getColorValueAnimator(colors);
            animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animator)
                {
                    for (Colorable v : views) {
                        if (v != null) {
                            v.setColor((int) animator.getAnimatedValue());
                        }
                    }
                    if (listener != null) {
                        listener.onAnimationUpdate(animator);
                    }
                }
            });
            if (repeat) {
                animation.setRepeatCount(ValueAnimator.INFINITE);
                animation.setRepeatMode(ValueAnimator.REVERSE);
            }
            if (interpolator != null) {
                animation.setInterpolator(interpolator);
            }
            animation.setDuration(duration);
            animation.start();
            return animation;
        }
        return null;
    }

    private static ValueAnimator getColorValueAnimator(int... colors)
    {
        if (Build.VERSION.SDK_INT >= 21) {
            return ValueAnimator.ofArgb(colors);
        } else {
            ValueAnimator animator = new ValueAnimator();
            animator.setIntValues(colors);
            animator.setEvaluator(new ArgbEvaluator());
            return animator;
        }
    }

}
