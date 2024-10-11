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

package com.forrestguice.suntimes.naturalhour.ui.clockview;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import android.support.annotation.Nullable;
import android.util.Log;

import com.forrestguice.suntimes.addon.TimeZoneHelper;
import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourCalculator;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourCalculator1;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourCalculator2;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourData;
import com.forrestguice.suntimes.naturalhour.data.TimeZoneWrapper;
import com.forrestguice.suntimes.naturalhour.ui.DisplayStrings;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValues;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NaturalHourClockBitmap
{
    public static final double START_TOP = -Math.PI / 2d;
    public static final double START_BOTTOM = Math.PI / 2d;

    public static final String VALUE_NIGHTWATCH_TYPE = "clockface_nightwatchType";
    public static final int NIGHTWATCH_4 = 4;
    public static final int NIGHTWATCH_3 = 3;
    public static final int NIGHTWATCH_DEFAULT = NIGHTWATCH_4;

    public static final String VALUE_HOURMODE = "clockface_hourmode";
    public static final int HOURMODE_SUNRISE = 0;
    public static final int HOURMODE_CIVILRISE = 1;
    public static final int HOURMODE_SUNSET = 2;
    public static final int HOURMODE_DEFAULT = HOURMODE_SUNRISE;

    public static final String VALUE_NUMERALS = "clockface_numerals";
    public static final int NUMERALS_ROMAN = 0;
    public static final int NUMERALS_ARABIC = 1;
    public static final int NUMERALS_ATTIC = 2;
    public static final int NUMERALS_HEBREW = 3;
    public static final int NUMERALS_ETRUSCAN = 4;
    public static final int NUMERALS_GREEK_UPPER = 5;
    public static final int NUMERALS_GREEK_LOWER = 6;
    public static final int NUMERALS_ARMENIAN = 7;
    public static final int NUMERALS_LOCALE = 100;
    public static final int NUMERALS_DEFAULT = NUMERALS_ROMAN;

    public static final String FLAG_START_AT_TOP = "clockface_startAtTop";
    public static final String FLAG_CENTER_NOON = "clockface_centerNoon";
    public static final String FLAG_SHOW_NIGHTWATCH = "clockface_showVigilia";
    public static final String FLAG_SHOW_TIMEZONE = "clockface_showTimeZone";
    public static final String FLAG_SHOW_LOCATION = "clockface_showLocation";
    public static final String FLAG_SHOW_DATE = "clockface_showDate";
    public static final String FLAG_SHOW_DATEYEAR = "clockface_showDateYear";
    public static final String FLAG_SHOW_HAND_SIMPLE = "clockface_showHandSimple";
    public static final String FLAG_SHOW_BACKGROUND_PLATE = "clockface_showBackgroundPlate";
    public static final String FLAG_SHOW_BACKGROUND_DAY = "clockface_showBackgroundDay";
    public static final String FLAG_SHOW_BACKGROUND_NIGHT = "clockface_showBackgroundNight";
    public static final String FLAG_SHOW_BACKGROUND_AMPM = "clockface_showBackgroundAmPm";
    public static final String FLAG_SHOW_BACKGROUND_TWILIGHTS = "clockface_showBackgroundTwilights";
    public static final String FLAG_SHOW_BACKGROUND_MIDNIGHT = "clockface_showBackgroundMidnight";
    public static final String FLAG_SHOW_BACKGROUND_NOON = "clockface_showBackgroundNoon";
    public static final String FLAG_SHOW_TICKS_15M = "clockface_showTick15m";
    public static final String FLAG_SHOW_TICKS_5M = "clockface_showTick5m";

    public static final String[] FLAGS = new String[] { FLAG_START_AT_TOP, FLAG_CENTER_NOON, FLAG_SHOW_NIGHTWATCH, FLAG_SHOW_TIMEZONE, FLAG_SHOW_LOCATION,
            FLAG_SHOW_DATE, FLAG_SHOW_DATEYEAR, FLAG_SHOW_HAND_SIMPLE, FLAG_SHOW_BACKGROUND_PLATE, FLAG_SHOW_BACKGROUND_DAY,
            FLAG_SHOW_BACKGROUND_NIGHT, FLAG_SHOW_BACKGROUND_AMPM, FLAG_SHOW_BACKGROUND_TWILIGHTS, FLAG_SHOW_TICKS_15M, FLAG_SHOW_TICKS_5M,
            FLAG_SHOW_BACKGROUND_MIDNIGHT, FLAG_SHOW_BACKGROUND_NOON
    };
    public static final String[] VALUES = new String[] { VALUE_HOURMODE, VALUE_NUMERALS, VALUE_NIGHTWATCH_TYPE };

    protected ContentValues flags = new ContentValues();
    private void initFlags(Context context)
    {
        setValueIfUnset(VALUE_HOURMODE, context.getResources().getInteger(R.integer.clockface_hourmode));
        setValueIfUnset(VALUE_NUMERALS, context.getResources().getInteger(R.integer.clockface_numerals));
        setValueIfUnset(VALUE_NIGHTWATCH_TYPE, context.getResources().getInteger(R.integer.clockface_nightwatch_type));

        setFlagIfUnset(FLAG_SHOW_TIMEZONE, context.getResources().getBoolean(R.bool.clockface_show_timezone));
        setFlagIfUnset(FLAG_SHOW_LOCATION, context.getResources().getBoolean(R.bool.clockface_show_location));
        setFlagIfUnset(FLAG_SHOW_DATE, context.getResources().getBoolean(R.bool.clockface_show_date));
        setFlagIfUnset(FLAG_SHOW_DATEYEAR, false);
        setFlagIfUnset(FLAG_SHOW_NIGHTWATCH, context.getResources().getBoolean(R.bool.clockface_show_vigilia));
        setFlagIfUnset(FLAG_SHOW_HAND_SIMPLE, context.getResources().getBoolean(R.bool.clockface_show_hand_simple));
        setFlagIfUnset(FLAG_SHOW_BACKGROUND_NIGHT, context.getResources().getBoolean(R.bool.clockface_show_background_night));
        setFlagIfUnset(FLAG_SHOW_BACKGROUND_PLATE, context.getResources().getBoolean(R.bool.clockface_show_background_plate));
        setFlagIfUnset(FLAG_SHOW_BACKGROUND_DAY, context.getResources().getBoolean(R.bool.clockface_show_background_day));
        setFlagIfUnset(FLAG_SHOW_BACKGROUND_AMPM, context.getResources().getBoolean(R.bool.clockface_show_background_ampm));
        setFlagIfUnset(FLAG_SHOW_BACKGROUND_TWILIGHTS, context.getResources().getBoolean(R.bool.clockface_show_background_twilights));
        setFlagIfUnset(FLAG_SHOW_BACKGROUND_MIDNIGHT, context.getResources().getBoolean(R.bool.clockface_show_background_midnight));
        setFlagIfUnset(FLAG_SHOW_BACKGROUND_NOON, context.getResources().getBoolean(R.bool.clockface_show_background_noon));
        setFlagIfUnset(FLAG_SHOW_TICKS_5M, context.getResources().getBoolean(R.bool.clockface_show_ticks_5m));
        setFlagIfUnset(FLAG_SHOW_TICKS_15M, context.getResources().getBoolean(R.bool.clockface_show_ticks_15m));

        setFlagIfUnset(FLAG_CENTER_NOON, context.getResources().getBoolean(R.bool.clockface_center_noon));
        setFlagIfUnset(FLAG_START_AT_TOP, context.getResources().getBoolean(R.bool.clockface_start_at_top));
        startAngle = startAngle != null ? startAngle : (flags.getAsBoolean(FLAG_START_AT_TOP) ? START_TOP : START_BOTTOM);
    }

    public void setFlag(String flag, boolean value) {
        flags.put(flag, value);
        onFlagChanged(flag);
    }
    public void setValue(String key, int value) {
        flags.put(key, value);
        onFlagChanged(key);
    }

    protected void onFlagChanged(String flag) {
        if (flag.equals(FLAG_START_AT_TOP)) {
            startAngle = (flags.getAsBoolean(FLAG_START_AT_TOP) ? START_TOP : START_BOTTOM);
        }
    }

    protected void setFlagIfUnset(String flag, boolean value) {
        if (!flags.containsKey(flag)) {
            flags.put(flag, value);
        }
    }
    protected void setValueIfUnset(String key, int value) {
        if (!flags.containsKey(key)) {
            flags.put(key, value);
        }
    }

    public boolean getFlag(String flag) {
        return flags.getAsBoolean(flag);
    }
    public int getValue(String key) {
        return flags.getAsInteger(key);
    }

    public static boolean getDefaultFlag(Context context, String flag) {
        switch (flag) {
            case FLAG_CENTER_NOON: return context.getResources().getBoolean(R.bool.clockface_center_noon);
            case FLAG_START_AT_TOP: return context.getResources().getBoolean(R.bool.clockface_start_at_top);
            case FLAG_SHOW_TIMEZONE: return context.getResources().getBoolean(R.bool.clockface_show_timezone);
            case FLAG_SHOW_LOCATION: return context.getResources().getBoolean(R.bool.clockface_show_location);
            case FLAG_SHOW_DATE: return context.getResources().getBoolean(R.bool.clockface_show_date);
            case FLAG_SHOW_NIGHTWATCH: return context.getResources().getBoolean(R.bool.clockface_show_vigilia);
            case FLAG_SHOW_HAND_SIMPLE: return context.getResources().getBoolean(R.bool.clockface_show_hand_simple);
            case FLAG_SHOW_BACKGROUND_PLATE: return context.getResources().getBoolean(R.bool.clockface_show_background_plate);
            case FLAG_SHOW_BACKGROUND_NIGHT: return context.getResources().getBoolean(R.bool.clockface_show_background_night);
            case FLAG_SHOW_BACKGROUND_DAY: return context.getResources().getBoolean(R.bool.clockface_show_background_day);
            case FLAG_SHOW_BACKGROUND_AMPM: return context.getResources().getBoolean(R.bool.clockface_show_background_ampm);
            case FLAG_SHOW_BACKGROUND_TWILIGHTS: return context.getResources().getBoolean(R.bool.clockface_show_background_twilights);
            case FLAG_SHOW_BACKGROUND_MIDNIGHT: return context.getResources().getBoolean(R.bool.clockface_show_background_midnight);
            case FLAG_SHOW_BACKGROUND_NOON: return context.getResources().getBoolean(R.bool.clockface_show_background_noon);
            case FLAG_SHOW_TICKS_5M: return context.getResources().getBoolean(R.bool.clockface_show_ticks_5m);
            case FLAG_SHOW_TICKS_15M: return context.getResources().getBoolean(R.bool.clockface_show_ticks_15m);
            case FLAG_SHOW_DATEYEAR:
            default: return false;
        }
    }
    public static int getDefaultValue(Context context, String key) {
        switch (key)
        {
            case VALUE_HOURMODE: return HOURMODE_DEFAULT;
            case VALUE_NUMERALS: return NUMERALS_DEFAULT;
            case VALUE_NIGHTWATCH_TYPE: return NIGHTWATCH_DEFAULT;
            default: return -1;
        }
    }

    protected long time = -1;
    public void setTime(long millis) {
        this.time = millis;
    }

    protected TimeZone timezone = TimeZone.getDefault();
    public void setTimeZone( TimeZone timezone) {
        this.timezone = timezone;
    }

    protected boolean is24 = true;
    public void set24HourMode(boolean value) {
        is24 = value;
    }

    protected Double startAngle = null;
    public void setStartAngle(double radianValue) {
        startAngle = radianValue;
    }

    protected boolean showMinorTickLabels = true;
    public void setShowMinorTickLabels(boolean value) {
        showMinorTickLabels = value;
    }

    protected boolean showTime = true;
    public void setShowTime(boolean value) {
        showTime = value;
    }

    protected int width, height;
    public void setSize(int s) {
        width = height = s;
        this.cX = width / 2f;
        this.cY = height / 2f;
        Log.d("DEBUG", "size: " + s);
    }
    private float cX, cY;

    public NaturalHourClockBitmap(Context context, int size) {
        initFlags(context);
        setSize(size);
    }

    public Bitmap makeBitmap(Context context, NaturalHourData data)
    {
        if (width <= 0 || height <= 0) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(context, canvas, data);
        return bitmap;
    }

    public void draw(Context context, Canvas canvas, NaturalHourData data)
    {
        if (paint == null || colors == null) {
            initPaint(context);
        }

        drawBackground(context, data, canvas, cX, cY);
        drawTimeArcs(context, data, canvas, cX, cY);
        drawTicks(data, canvas, cX, cY, is24);
        drawTickLabels(data, canvas, cX, cY, is24);

        paintTickLarge.setColor(colors.getColor(ClockColorValues.COLOR_FRAME));
        canvas.drawCircle(cX, cY, radiusInner(cX), paintTickLarge);

        if (showTime) {
            drawHourHand(data, time <= 0 ? System.currentTimeMillis() : time, canvas, cX, cY, radiusInner(cX));
        }
    }

    private ColorValues colors = null;
    public void setColors(ColorValues values) {
        colors = values; //new ClockColorValues(values);
    }
    public ColorValues getColors() {
        return colors;
    }

    private float arcStrokeWidth;
    private float arcWidth;
    private float centerRadius;
    protected float handWidth;

    private float tickLength_huge;
    private float tickLength_large;
    private float tickLength_medium;
    private float tickLength_small;
    private float tickLength_tiny;

    private int textLarge;
    private int textMedium;
    private int textSmall;
    private int textTiny;

    private Paint paint, paintLabel, paintHand,
            paintTickHuge, paintTickLarge, paintTickMedium, paintTickSmall, paintTickTiny,
            paintArcDayFill, paintArcDayBorder, paintFillDay,
            paintArcNightFill, paintArcNightBorder, paintFillNight,
            paintCenter, paintBackground;

    private SimpleDateFormat dateFormat_short = null, dateFormat_long = null;

    private void initPaint(Context context)
    {
        initFlags(context);
        if (colors == null) {
            colors = new ClockColorValues(context);
        }

        dateFormat_short = new SimpleDateFormat(context.getString(R.string.format_date0), Locale.getDefault());
        dateFormat_long = new SimpleDateFormat(context.getString(R.string.format_date0_long), Locale.getDefault());

        handWidth = context.getResources().getDimension(R.dimen.clockface_hand_width);
        centerRadius = (int)(context.getResources().getDimension(R.dimen.clockface_center_width) / 2f);
        arcWidth = (int)context.getResources().getDimension(R.dimen.clockface_arc_width);
        arcStrokeWidth = (int)context.getResources().getDimension(R.dimen.clockface_arc_stroke_width);
        textLarge = (int)context.getResources().getDimension(R.dimen.clockface_text_large);
        textMedium = (int)context.getResources().getDimension(R.dimen.clockface_text_medium);
        textSmall = (int)context.getResources().getDimension(R.dimen.clockface_text_small);
        textTiny = (int)context.getResources().getDimension(R.dimen.clockface_text_tiny);

        tickLength_huge = arcWidth;
        tickLength_large = 2 * arcWidth / 3f;
        tickLength_medium = (arcWidth / 2f);
        tickLength_small = (arcWidth / 4f);
        tickLength_tiny = (arcWidth / 8f);

        paint = new Paint();
        paint.setColor(colors.getColor(ClockColorValues.COLOR_LABEL));
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(context.getResources().getDimension(R.dimen.clockface_stroke_width));
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(textSmall);
        paint.setTypeface(Typeface.create(paint.getTypeface(), Typeface.BOLD));

        paintLabel = new Paint();
        paintLabel.setColor(colors.getColor(ClockColorValues.COLOR_LABEL));
        paintLabel.setStyle(Paint.Style.FILL);
        paintLabel.setStrokeWidth(context.getResources().getDimension(R.dimen.clockface_stroke_width));
        paintLabel.setAntiAlias(true);
        paintLabel.setTextAlign(Paint.Align.CENTER);
        paintLabel.setTextSize(textSmall);

        paintHand = new Paint();
        paintHand.setColor(colors.getColor(ClockColorValues.COLOR_HAND));
        paintHand.setStyle(Paint.Style.FILL);
        paintHand.setStrokeWidth(handWidth);
        paintHand.setAntiAlias(true);

        paintCenter = new Paint();
        paintCenter.setStyle(Paint.Style.FILL);
        paintCenter.setColor(colors.getColor(ClockColorValues.COLOR_LABEL));
        paintCenter.setStrokeWidth(centerRadius);
        paintCenter.setAntiAlias(true);

        paintBackground = new Paint();
        paintBackground.setStyle(Paint.Style.FILL);
        paintBackground.setColor(colors.getColor(ClockColorValues.COLOR_FACE));
        paintBackground.setStrokeWidth(centerRadius);
        paintBackground.setAntiAlias(true);

        paintTickHuge = new Paint();
        paintTickHuge.setColor(colors.getColor(ClockColorValues.COLOR_FRAME));
        paintTickHuge.setStyle(Paint.Style.STROKE);
        paintTickHuge.setStrokeWidth(context.getResources().getDimension(R.dimen.clockface_tick_huge_width));
        paintTickHuge.setAntiAlias(true);
        paintTickHuge.setTextAlign(Paint.Align.CENTER);

        paintTickLarge = new Paint(paintTickHuge);
        paintTickLarge.setStrokeWidth(context.getResources().getDimension(R.dimen.clockface_tick_large_width));

        paintTickMedium = new Paint(paintTickHuge);
        paintTickMedium.setStrokeWidth(context.getResources().getDimension(R.dimen.clockface_tick_medium_width));

        paintTickSmall = new Paint(paintTickHuge);
        paintTickSmall.setStrokeWidth(context.getResources().getDimension(R.dimen.clockface_tick_small_width));

        paintTickTiny = new Paint(paintTickHuge);
        paintTickTiny.setStrokeWidth(context.getResources().getDimension(R.dimen.clockface_tick_tiny_width));

        paintArcDayBorder = new Paint();
        paintArcDayBorder.setStyle(Paint.Style.STROKE);
        paintArcDayBorder.setColor(colors.getColor(ClockColorValues.COLOR_RING_DAY_STROKE));
        paintArcDayBorder.setStrokeWidth(arcStrokeWidth);
        paintArcDayBorder.setAntiAlias(true);

        paintArcNightBorder = new Paint();
        paintArcNightBorder.setStyle(Paint.Style.STROKE);
        paintArcNightBorder.setColor(colors.getColor(ClockColorValues.COLOR_RING_NIGHT_STROKE));
        paintArcNightBorder.setStrokeWidth(arcStrokeWidth);
        paintArcNightBorder.setAntiAlias(true);

        paintArcDayFill = new Paint();
        paintArcDayFill.setStyle(Paint.Style.STROKE);
        paintArcDayFill.setColor(colors.getColor(ClockColorValues.COLOR_RING_DAY));
        paintArcDayFill.setStrokeWidth(arcWidth);
        paintArcDayFill.setAntiAlias(true);

        paintArcNightFill = new Paint();
        paintArcNightFill.setStyle(Paint.Style.STROKE);
        paintArcNightFill.setColor(colors.getColor(ClockColorValues.COLOR_RING_NIGHT));
        paintArcNightFill.setStrokeWidth(arcWidth);
        paintArcNightFill.setAntiAlias(true);

        paintFillNight = new Paint();
        paintFillNight.setStyle(Paint.Style.FILL_AND_STROKE);
        paintFillNight.setColor(colors.getColor(ClockColorValues.COLOR_FACE_NIGHT));
        paintFillNight.setAntiAlias(true);

        paintFillDay = new Paint();
        paintFillDay.setStyle(Paint.Style.FILL_AND_STROKE);
        paintFillDay.setColor(colors.getColor(ClockColorValues.COLOR_FACE_DAY));
        paintFillDay.setStrokeWidth(2);
        paintFillDay.setAntiAlias(true);
    }

    private float radiusInner(float r) {
        return r - (2.05f * arcWidth);
    }

    private float radiusOuter(float r) {
        return radiusInner(r) + arcWidth;
    }

    private float radiusOuter1(float r) {
        return radiusInner(r) + (2 * arcWidth);
    }

    public static double getAdjustedAngle( double startAngle, double angle) {
        return startAngle + (Math.PI / 2d) + angle;
    }
    public double getAdjustedAngle( double startAngle, double angle, @Nullable NaturalHourData data ) {
        if (flags.getAsBoolean(FLAG_CENTER_NOON) && data != null && data.isCalculated())
        {
            double noonAngle = data.getAngle(data.getNaturalHours()[6], timezone);
            double offset = Math.PI/2d - noonAngle;
            return getAdjustedAngle(startAngle, angle) + offset;
        }
        return getAdjustedAngle(startAngle, angle);
    }

    protected void drawTimeArcs(Context context, NaturalHourData data, Canvas canvas, float cX, float cY)
    {
        float r_inner = radiusInner(cX);
        float r_mid = r_inner + (arcWidth / 2f);
        final RectF circle_mid = new RectF(cX - r_mid, cY - r_mid, cX + r_mid, cY + r_mid);

        float r_outer = radiusOuter(cX);
        final RectF circle_outer = new RectF(cX - r_outer, cY - r_outer, cX + r_outer, cY + r_outer);

        float r_mid1 = r_outer + (arcWidth / 2f);
        final RectF circle_mid1 = new RectF(cX - r_mid1, cY - r_mid1, cX + r_mid1, cY + r_mid1);

        float r_outer1 = radiusOuter1(cX);
        final RectF circle_outer1 = new RectF(cX - r_outer1, cY - r_outer1, cX + r_outer1, cY + r_outer1);

        if (data != null && data.isCalculated())
        {
            double dayAngle = data.getDayHourAngle();
            double nightAngle = data.getNightHourAngle();
            long[] naturalHours = data.getNaturalHours();
            double sunriseAngle = getAdjustedAngle(startAngle, data.getAngle(naturalHours[0], timezone), data);
            double sunsetAngle = getAdjustedAngle(startAngle, data.getAngle(naturalHours[12], timezone), data);

            int color_day = colors.getColor(ClockColorValues.COLOR_RING_DAY_LABEL);
            paintArcDayFill.setColor(colors.getColor(ClockColorValues.COLOR_RING_DAY));
            paintArcDayBorder.setColor(colors.getColor(ClockColorValues.COLOR_RING_DAY_STROKE));

            int color_night = colors.getColor(ClockColorValues.COLOR_RING_NIGHT_LABEL);
            paintArcNightFill.setColor(colors.getColor(ClockColorValues.COLOR_RING_NIGHT));
            paintArcNightBorder.setColor(colors.getColor(ClockColorValues.COLOR_RING_NIGHT_STROKE));

            TimeZone tz = TimeZoneHelper.ApparentSolarTime.TIMEZONEID.equals(timezone.getID()) ? timezone          // don't wrap ApparentSolarTime
                    : new TimeZoneWrapper(timezone, timezone.inDaylightTime(new Date(naturalHours[0])));

            int hourmode = getValue(VALUE_HOURMODE);
            for (int i=0; i<naturalHours.length; i++)
            {
                boolean isNight = (i >= 12);
                double hourAngle = (isNight ? nightAngle : dayAngle);

                double a = getAdjustedAngle(startAngle, data.getAngle(naturalHours[i], tz), data);
                canvas.drawArc(circle_mid, (float) Math.toDegrees(a), (float) Math.toDegrees(hourAngle), false, (isNight ? paintArcNightFill : paintArcDayFill));
                drawRay(canvas, cX, cY, a, r_inner, r_outer, isNight ? paintArcNightBorder : paintArcDayBorder);

                double a1 = a + (hourAngle / 2d);
                double lw = arcWidth * 0.5f;
                double lx = cX + (r_inner + lw) * Math.cos(a1);
                double ly = cY + (r_inner + lw) * Math.sin(a1);

                paint.setColor(isNight ? color_night : color_day);
                paint.setTextSize(textSmall);


                int j = (hourmode == HOURMODE_SUNSET) ? (i >= 12) ? i - 12 + 1 : i + 12 + 1
                                                      : ((i % 12) + 1);  //i + 1;

                canvas.drawText(getNumeral(context, j), (float)(lx), (float)(ly) + (textSmall * 0.5f), paint);
            }
            canvas.drawArc(circle_outer, (float) Math.toDegrees(sunriseAngle), (float) Math.toDegrees(sunsetAngle-sunriseAngle), false, paintArcDayBorder);
            drawRay(canvas, cX, cY, getAdjustedAngle(startAngle, data.getAngle(naturalHours[0], timezone), data), r_inner, r_outer, paintArcNightBorder);
            drawRay(canvas, cX, cY, getAdjustedAngle(startAngle, data.getAngle(naturalHours[12], timezone), data), r_inner, r_outer, paintArcNightBorder);

            if (flags.getAsBoolean(FLAG_SHOW_NIGHTWATCH))
            {
                int type = getValue(VALUE_NIGHTWATCH_TYPE);
                int numWatches = getNumWatchesForNightWatchType(type);
                double watchSweepAngle = (nightAngle * 12d) / numWatches;

                int c = 1;
                Path labelPath = new Path();
                for (int i = 0; i<numWatches; i++)
                {
                    double a = sunsetAngle + (i * watchSweepAngle);
                    canvas.drawArc(circle_mid1, (float) Math.toDegrees(a), (float) Math.toDegrees(watchSweepAngle), false, paintArcNightFill);
                    canvas.drawArc(circle_outer1, (float) Math.toDegrees(a), (float) Math.toDegrees(watchSweepAngle), false, paintArcNightBorder);
                    drawRay(canvas, cX, cY, a, r_outer, r_outer1, paintArcNightBorder);

                    paint.setTextSize(textSmall);
                    CharSequence label = formatNightWatchLabel(context, type, c);
                    labelPath.reset();

                    if (startAngle < 0) {
                        labelPath.addArc(circle_mid1, (float) Math.toDegrees(a), (float) Math.toDegrees(watchSweepAngle));
                    } else {
                        labelPath.addArc(circle_mid1, (float) Math.toDegrees(a + watchSweepAngle), (float) Math.toDegrees(-watchSweepAngle));
                    }

                    paint.setColor(color_night);
                    canvas.drawTextOnPath(label.toString(), labelPath, 0, textSmall / 3f, paint);
                    c++;
                }

                double a0 = getAdjustedAngle(startAngle, data.getAngle(naturalHours[12], timezone) + (watchSweepAngle * numWatches), data);
                drawRay(canvas, cX, cY, a0, r_outer, r_outer1, paintArcNightBorder);
            }
            canvas.drawArc(circle_outer, (float) Math.toDegrees(sunsetAngle), (float) Math.toDegrees(2*Math.PI - (sunsetAngle-sunriseAngle)), false, paintArcNightBorder);


        } else {
            canvas.drawCircle(cX, cY, r_outer, paintTickMedium);
        }
    }

    protected int getNumWatchesForNightWatchType(int type)
    {
        int n;
        switch (type)
        {
            case 0: n = 4; break;    // legacy mapping
            case 1: n = 3; break;    // legacy mapping
            default: n = type; break;
        }
        return (n <= 1 ? NIGHTWATCH_DEFAULT : (Math.min(type, 11)));
    }

    protected CharSequence formatNightWatchLabel(@NonNull Context context, int type, int num)
    {
        switch (type)
        {
            case NIGHTWATCH_3: return DisplayStrings.formatNightWatchLabel1(context, num);
            default: return DisplayStrings.formatNightWatchLabel0(context, num);
        }
    }

    protected void drawRay(Canvas canvas, double cX, double cY, double angle, double radius_inner, double radius_outer, Paint paint)
    {
        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);
        double x0 = cX + radius_inner * cosA;
        double y0 = cY + radius_inner * sinA;
        double x1 = cX + radius_outer * cosA;
        double y1 = cY + radius_outer * sinA;
        canvas.drawLine((float) x0, (float) y0, (float) x1, (float) y1, paint);
    }

    protected void drawPie(Canvas canvas, double cX, double cY, double radius, double startRadians, double angleRadians, Paint paint)
    {
        final RectF circle_inner = new RectF((float)(cX - radius), (float)(cY - radius), (float)(cX + radius), (float)(cY + radius));
        Path path = new Path();
        path.moveTo((float) cX, (float) cY);
        path.lineTo((float) (cX + (radius * Math.cos(startRadians))), (float) (cY + (radius * Math.sin(startRadians))));
        path.addArc(circle_inner, (float) Math.toDegrees(startRadians), (float) Math.toDegrees(angleRadians));
        path.lineTo((float) cX, (float) cY);
        path.close();
        canvas.drawPath(path, paint);
    }

    protected void drawTicks(NaturalHourData data, Canvas canvas, float cX, float cY, boolean is24)
    {
        float r0 = radiusInner(cX);
        float rHugeTick = r0 - tickLength_huge;
        float rLargeTick = r0 - tickLength_large;
        float rMediumTick = r0 - tickLength_medium;
        float rSmallTick = r0 - tickLength_small;
        float rTinyTick = r0 - tickLength_tiny;
        double oneHourRad = Math.PI / 12d;

        boolean showTick5m = flags.getAsBoolean(FLAG_SHOW_TICKS_5M);
        boolean showTick15m = flags.getAsBoolean(FLAG_SHOW_TICKS_15M);

        int frameColor = colors.getColor(ClockColorValues.COLOR_FRAME);
        paintTickTiny.setColor(frameColor);
        paintTickSmall.setColor(frameColor);
        paintTickMedium.setColor(frameColor);
        paintTickLarge.setColor(frameColor);
        paintTickHuge.setColor(frameColor);

        double a = getAdjustedAngle(startAngle, -Math.PI/2d, data);
        for (int i=1; i<=24; i++)
        {
            a += ((2 * Math.PI) / 24f);

            if (showTick15m || showTick5m)
            {
                for (int j=0; j<4; j++)
                {
                    double a1 = a + ((j * oneHourRad) / 4d);
                    if (showTick5m)
                    {
                        for (int k=0; k<4; k++)
                        {
                            double a2 = a1 + (k * (oneHourRad / 4d) / 3d);       // +5m
                            drawRay(canvas, cX, cY, a2, r0, rTinyTick, paintTickTiny);
                        }
                    }
                    if (showTick15m) {
                        drawRay(canvas, cX, cY, a1, r0, rSmallTick, paintTickSmall);
                    }
                }
            }
            float r = (i % 3 == 0) ? (i % 6 == 0) ? rHugeTick : rLargeTick : rMediumTick;
            drawRay(canvas, cX, cY, a, r0, r, (i % 6 == 0 ? paintTickHuge : paintTickLarge));
        }
    }

    protected void drawTickLabels(NaturalHourData data, Canvas canvas, float cX, float cY, boolean is24)
    {
        paint.setColor(colors.getColor(ClockColorValues.COLOR_LABEL));

        float r0 = radiusInner(cX);
        float rHugeTick = r0 - tickLength_huge;
        float rLargeTick = r0 - tickLength_large;
        float rMediumTick = r0 - tickLength_medium;
        double a = getAdjustedAngle(startAngle, -Math.PI/2d, data);
        for (int i=1; i<=24; i++)
        {
            a += ((2 * Math.PI) / 24f);
            float r = (i % 3 == 0) ? (i % 6 == 0) ? rHugeTick : rLargeTick : rMediumTick;
            double cosA = Math.cos(a);
            double sinA = Math.sin(a);

            double lw = i % 3 == 0 ? tickLength_large : tickLength_medium;
            double lx = cX + (r - lw) * cosA;
            double ly = cY + (r - lw) * sinA;

            boolean isMajorTick = (i % 6 == 0);
            boolean isMinorTick = (i % 3 != 0);

            int textSize = isMajorTick ? textLarge :
                    i % 3 == 0 ? textMedium : textSmall;
            paint.setTextSize(textSize);

            int j = is24 ? i : (i == 24 || i == 12 ? 12 : (i % 12));
            String label = is24 ? (i < 10 ? "0" + j : "" + j) : "" + j;

            if (!isMinorTick || showMinorTickLabels) {
                canvas.drawText(label, (float)(lx), (float)(ly) + (textSize * 0.25f), paint);
            }
        }
    }

    protected void drawBackground(Context context, NaturalHourData data, Canvas canvas, float cX, float cY)
    {
        if (flags.getAsBoolean(FLAG_SHOW_BACKGROUND_PLATE)) {
            paintBackground.setColor(colors.getColor(ClockColorValues.COLOR_PLATE));
            canvas.drawCircle(cX, cY, radiusOuter1(cX), paintBackground);
        }

        paintBackground.setColor(colors.getColor(ClockColorValues.COLOR_FACE));
        canvas.drawCircle(cX, cY, radiusOuter(cX), paintBackground);

        if (data != null && data.isCalculated())
        {
            long[] twilightHours = data.getTwilightTimes();
            long[] naturalHours = data.getNaturalHours();
            double dayHourAngle = data.getDayHourAngle();
            double dayAngle = getAdjustedAngle(startAngle, data.getAngle(twilightHours[3], timezone), data);
            double nightAngle = getAdjustedAngle(startAngle, data.getAngle(twilightHours[4], timezone), data);

            double daySpan, nightSpan;
            if (nightAngle > dayAngle)
            {
                daySpan = NaturalHourData.simplifyAngle(nightAngle - dayAngle);
                nightSpan = 2 * Math.PI - daySpan;
            } else {
                nightSpan = NaturalHourData.simplifyAngle(dayAngle - nightAngle);
                daySpan = 2 * Math.PI - nightSpan;
            }

            paintFillNight.setColor(colors.getColor(ClockColorValues.COLOR_FACE_NIGHT));
            paintFillDay.setColor(colors.getColor(ClockColorValues.COLOR_FACE_DAY));

            if (flags.getAsBoolean(FLAG_SHOW_BACKGROUND_NIGHT)) {
                drawPie(canvas, cX, cY, radiusInner(cX), nightAngle, nightSpan, paintFillNight);
            }

            boolean showTwilights = flags.getAsBoolean(FLAG_SHOW_BACKGROUND_TWILIGHTS);
            boolean showDay = flags.getAsBoolean(FLAG_SHOW_BACKGROUND_DAY);
            if (showTwilights || showDay)
            {
                long time0 = (twilightHours[0] != -1) ? twilightHours[0] : naturalHours[18];
                double a0 = getAdjustedAngle(startAngle, data.getAngle(time0, timezone), data);

                for (int i=1; i<twilightHours.length; i++)
                {
                    long time = (twilightHours[i] != -1) ? twilightHours[i] : naturalHours[18];
                    double a1 = getAdjustedAngle(startAngle, data.getAngle(time, timezone), data);

                    if ((i == 4 && showDay) || i != 4 && showTwilights)
                    {
                        double span = NaturalHourData.simplifyAngle(a1 - a0);
                        if (span != 0) {
                            paintFillDay.setColor(getTwilightColor(i - 1));
                            drawPie(canvas, cX, cY, radiusInner(cX), a0, span, paintFillDay);
                        }
                    }
                    a0 = a1;
                }
            }

            //if (showDay) {
                //paintFillDay.setColor(colors.colorDay1);
                //drawPie(canvas, cX, cY, radiusInner(cX), dayAngle, daySpan, paintFillDay);
            //}

            if (flags.getAsBoolean(FLAG_SHOW_BACKGROUND_AMPM))
            {
                double middaySpan = daySpan / 2d;
                double a1 = getAdjustedAngle(startAngle, data.getAngle(twilightHours[3], timezone), data);
                paintFillDay.setColor(colors.getColor(ClockColorValues.COLOR_FACE_AM));
                drawPie(canvas, cX, cY, radiusInner(cX), a1, middaySpan, paintFillDay);

                double a2 = getAdjustedAngle(startAngle, data.getAngle(naturalHours[6], timezone), data);
                paintFillDay.setColor(colors.getColor(ClockColorValues.COLOR_FACE_PM));
                drawPie(canvas, cX, cY, radiusInner(cX), a2, middaySpan, paintFillDay);
            }

            if (flags.getAsBoolean(FLAG_SHOW_DATE))
            {
                float r = radiusOuter1(cX) - arcWidth/2f;
                final RectF circle = new RectF(cX - r, cY - r, cX + r, cY + r);
                boolean alongBottom = alongBottom(dayAngle + dayHourAngle * 6);

                Path path = new Path();
                double arcAngle = (alongBottom ? nightAngle : dayAngle);
                double sweepAngle = (alongBottom ? -1 : 1) * dayHourAngle * 12;
                path.addArc(circle, (float) Math.toDegrees(arcAngle), (float) Math.toDegrees(sweepAngle));
                paint.setTextSize(textMedium);

                //Calendar date = Calendar.getInstance(timezone);
                //date.setTimeInMillis(data.getDateMillis());
                //String[] weekSymbols = getContext().getResources().getStringArray(R.array.week_symbols);
                //String weekSymbol = weekSymbols[date.get(Calendar.DAY_OF_WEEK)-1];
                CharSequence dateString = formatDate(context, data.getDateMillis());
                canvas.drawTextOnPath(dateString.toString(),  path, 0, textMedium/3f, paint);
            }

            if (flags.getAsBoolean(FLAG_SHOW_LOCATION))
            {
                float r = radiusOuter1(cX) - arcWidth/2f;
                final RectF circle = new RectF(cX - r, cY - r, cX + r, cY + r);
                Path path = new Path();
                path.addArc(circle, (float) Math.toDegrees(-Math.PI/2d + Math.PI/4d), (float) Math.toDegrees(Math.PI/8));
                paintLabel.setColor(colors.getColor(ClockColorValues.COLOR_LABEL1));
                paintLabel.setTextSize(textTiny);
                CharSequence location = DisplayStrings.formatLocation(context, data.getLatitude(), data.getLongitude(), 2);
                canvas.drawTextOnPath(location.toString(), path, 0, textSmall/3f, paintLabel);
            }

            if (flags.getAsBoolean(FLAG_SHOW_BACKGROUND_MIDNIGHT)) {
                double a = getAdjustedAngle(startAngle, data.getAngle(naturalHours[18], timezone), data);
                drawRay(canvas, cX, cY, a, 0, radiusInner(cX), paintArcNightBorder);
            }

            if (flags.getAsBoolean(FLAG_SHOW_BACKGROUND_NOON)) {
                double a = getAdjustedAngle(startAngle, data.getAngle(naturalHours[6], timezone), data);
                drawRay(canvas, cX, cY, a, 0, radiusInner(cX), paintArcDayBorder);
            }
        }

        if (flags.getAsBoolean(FLAG_SHOW_TIMEZONE))
        {
            float r = (radiusInner(cX) - (2.65f * arcWidth));
            final RectF circle = new RectF(cX - r, cY - r, cX + r, cY + r);

            Path path = new Path();
            boolean alongBottom = alongBottom(startAngle);
            double arcAngle = NaturalHourData.simplifyAngle(startAngle + (alongBottom ? Math.PI/2d : -Math.PI/2));
            double sweepAngle = alongBottom ? -Math.PI : Math.PI;
            path.addArc(circle, (float) Math.toDegrees(arcAngle), (float) Math.toDegrees(sweepAngle));
            paintLabel.setColor(colors.getColor(ClockColorValues.COLOR_LABEL1));
            paintLabel.setTextSize(textSmall);
            canvas.drawTextOnPath(timezone.getID(), path, 0, 0, paintLabel);
        }

    }

    private int getTwilightColor(int i)
    {
        switch (i)
        {
            case 7: return colors.getColor(ClockColorValues.COLOR_FACE_NIGHT);
            case 0: case 6: return colors.getColor(ClockColorValues.COLOR_FACE_ASTRO);
            case 1: case 5: return colors.getColor(ClockColorValues.COLOR_FACE_NAUTICAL);
            case 2: case 4: return colors.getColor(ClockColorValues.COLOR_FACE_CIVIL);
            case 3: default: return colors.getColor(ClockColorValues.COLOR_FACE_DAY);
        }
    }

    private boolean alongBottom(double radians)
    {
        double angle = NaturalHourData.simplifyAngle(radians);
        return angle >= 0 && angle <= Math.PI;
    }

    protected void drawHourHand(NaturalHourData data, long nowMillis, Canvas canvas, float cX, float cY, float length)
    {
        Calendar now = Calendar.getInstance(timezone);
        now.setTimeInMillis(nowMillis);

        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);

        double a1 = getAdjustedAngle(startAngle, NaturalHourData.getAngle(hour, minute, second), data);
        double x1 = cX + length * Math.cos(a1);
        double y1 = cY + length * Math.sin(a1);

        paintHand.setColor(colors.getColor(ClockColorValues.COLOR_HAND));

        if (flags.getAsBoolean(FLAG_SHOW_HAND_SIMPLE)) {
            canvas.drawLine(cX, cY, (float)x1, (float)y1, paintHand);

        } else {

            double handRadius = handWidth / 2d;
            double a0 = a1 - Math.PI/2;
            double x0 = cX + handRadius * Math.cos(a0);
            double y0 = cY + handRadius * Math.sin(a0);

            double a2 = a1 + Math.PI/2;
            double x2 = cX + handRadius * Math.cos(a2);
            double y2 = cY + handRadius * Math.sin(a2);

            Path path = new Path();
            path.moveTo((float) x0, (float) y0);
            path.lineTo((float) x1, (float) y1);
            path.lineTo((float) x2, (float) y2);
            path.close();
            canvas.drawPath(path, paintHand);
        }
        if (centerRadius > 0) {
            paintCenter.setColor(colors.getColor(ClockColorValues.COLOR_LABEL));
            canvas.drawCircle(cX, cY, centerRadius, paintCenter);
        }
    }

    private CharSequence formatDate(@NonNull Context context, long dateMillis)
    {
        Calendar now = Calendar.getInstance(timezone);
        Calendar then = Calendar.getInstance(timezone);
        then.setTimeInMillis(dateMillis);
        boolean isThisYear = now.get(Calendar.YEAR) == then.get(Calendar.YEAR);

        SimpleDateFormat dateFormat = !isThisYear || flags.getAsBoolean(FLAG_SHOW_DATEYEAR) ? dateFormat_long : dateFormat_short;
        dateFormat.setTimeZone(timezone);
        return dateFormat.format(dateMillis);
    }

    public String getNumeral(Context context, int i) {
        return getNumeral(context, getValue(VALUE_NUMERALS), i);
    }

    public static String getNumeral(Context context, int type, int i)
    {
        switch (type)
        {
            case NUMERALS_ARABIC: return DisplayStrings.arabicNumeral(context, i);
            case NUMERALS_ATTIC: return DisplayStrings.atticNumeral(context, i);
            case NUMERALS_ARMENIAN: return DisplayStrings.armenianNumeral(context, i);
            case NUMERALS_ETRUSCAN: return DisplayStrings.etruscanNumeral(context, i);
            case NUMERALS_HEBREW: return DisplayStrings.hebrewNumeral(context, i);
            case NUMERALS_GREEK_UPPER: return DisplayStrings.greekNumeral(context, i, false);
            case NUMERALS_GREEK_LOWER: return DisplayStrings.greekNumeral(context, i, true);
            case NUMERALS_LOCALE: return DisplayStrings.localizedNumeral(context, Locale.getDefault(), i);
            case NUMERALS_ROMAN: default: return DisplayStrings.romanNumeral(context, i);
        }
    }

    /**
     * @param hourmode HOURMODE_SUNRISE ...
     * @return an implementation of NaturalHourCalculator
     */
    public static NaturalHourCalculator getCalculator(int hourmode)
    {
        switch (hourmode) {
            case HOURMODE_CIVILRISE: return new NaturalHourCalculator1();
            case HOURMODE_SUNSET: return new NaturalHourCalculator2();
            case HOURMODE_SUNRISE: default: return new NaturalHourCalculator();
        }
    }

    public NaturalHourCalculator getCalculator() {
        return getCalculator(getValue(VALUE_HOURMODE));
    }

}
