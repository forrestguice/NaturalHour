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

import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;

import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class NaturalHourClockBitmap
{
    public static final double START_TOP = -Math.PI / 2d;
    public static final double START_BOTTOM = Math.PI / 2d;

    public static final String FLAG_START_AT_TOP = "clockface_startAtTop";
    public static final String FLAG_SHOW_VIGILIA = "clockface_showVigilia";
    public static final String FLAG_SHOW_TIMEZONE = "clockface_showTimeZone";
    public static final String FLAG_SHOW_DATE = "clockface_showDate";
    public static final String FLAG_SHOW_DATEYEAR = "clockface_showDateYear";
    public static final String FLAG_SHOW_HAND_SIMPLE = "clockface_showHandSimple";
    public static final String FLAG_SHOW_BACKGROUND_PLATE = "clockface_showBackgroundPlate";
    public static final String FLAG_SHOW_BACKGROUND_DAY = "clockface_showBackgroundDay";
    public static final String FLAG_SHOW_BACKGROUND_NIGHT = "clockface_showBackgroundNight";
    public static final String FLAG_SHOW_BACKGROUND_AMPM = "clockface_showBackgroundAmPm";
    public static final String FLAG_SHOW_BACKGROUND_TWILIGHTS = "clockface_showBackgroundTwilights";
    public static final String FLAG_SHOW_TICKS_15M = "clockface_showTick15m";
    public static final String FLAG_SHOW_TICKS_5M = "clockface_showTick5m";

    public static final String[] FLAGS = new String[] { FLAG_START_AT_TOP, FLAG_SHOW_VIGILIA, FLAG_SHOW_TIMEZONE,
            FLAG_SHOW_DATE, FLAG_SHOW_DATEYEAR, FLAG_SHOW_HAND_SIMPLE, FLAG_SHOW_BACKGROUND_PLATE, FLAG_SHOW_BACKGROUND_DAY,
            FLAG_SHOW_BACKGROUND_NIGHT, FLAG_SHOW_BACKGROUND_AMPM, FLAG_SHOW_BACKGROUND_TWILIGHTS, FLAG_SHOW_TICKS_15M, FLAG_SHOW_TICKS_5M,
    };

    protected ContentValues flags = new ContentValues();
    private void initFlags(Context context)
    {
        setFlagIfUnset(FLAG_SHOW_TIMEZONE, context.getResources().getBoolean(R.bool.clockface_show_timezone));
        setFlagIfUnset(FLAG_SHOW_DATE, context.getResources().getBoolean(R.bool.clockface_show_date));
        setFlagIfUnset(FLAG_SHOW_DATEYEAR, false);
        setFlagIfUnset(FLAG_SHOW_VIGILIA, context.getResources().getBoolean(R.bool.clockface_show_vigilia));
        setFlagIfUnset(FLAG_SHOW_HAND_SIMPLE, context.getResources().getBoolean(R.bool.clockface_show_hand_simple));
        setFlagIfUnset(FLAG_SHOW_BACKGROUND_NIGHT, context.getResources().getBoolean(R.bool.clockface_show_background_night));
        setFlagIfUnset(FLAG_SHOW_BACKGROUND_PLATE, context.getResources().getBoolean(R.bool.clockface_show_background_plate));
        setFlagIfUnset(FLAG_SHOW_BACKGROUND_DAY, context.getResources().getBoolean(R.bool.clockface_show_background_day));
        setFlagIfUnset(FLAG_SHOW_BACKGROUND_AMPM, context.getResources().getBoolean(R.bool.clockface_show_background_ampm));
        setFlagIfUnset(FLAG_SHOW_BACKGROUND_TWILIGHTS, context.getResources().getBoolean(R.bool.clockface_show_background_twilights));
        setFlagIfUnset(FLAG_SHOW_TICKS_5M, context.getResources().getBoolean(R.bool.clockface_show_ticks_5m));
        setFlagIfUnset(FLAG_SHOW_TICKS_15M, context.getResources().getBoolean(R.bool.clockface_show_ticks_15m));

        setFlagIfUnset(FLAG_START_AT_TOP, context.getResources().getBoolean(R.bool.clockface_start_at_top));
        startAngle = startAngle != null ? startAngle : (flags.getAsBoolean(FLAG_START_AT_TOP) ? START_TOP : START_BOTTOM);
    }

    public void setFlag(String flag, boolean value) {
        flags.put(flag, value);
        onFlagChanged(flag);
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

    public boolean getFlag(String flag) {
        return flags.getAsBoolean(flag);
    }

    public static boolean getDefaultFlag(Context context, String flag) {
        switch (flag) {
            case FLAG_SHOW_TIMEZONE: return context.getResources().getBoolean(R.bool.clockface_show_timezone);
            case FLAG_SHOW_DATE: return context.getResources().getBoolean(R.bool.clockface_show_date);
            case FLAG_SHOW_VIGILIA: return context.getResources().getBoolean(R.bool.clockface_show_vigilia);
            case FLAG_SHOW_HAND_SIMPLE: return context.getResources().getBoolean(R.bool.clockface_show_hand_simple);
            case FLAG_SHOW_BACKGROUND_PLATE: return context.getResources().getBoolean(R.bool.clockface_show_background_plate);
            case FLAG_SHOW_BACKGROUND_NIGHT: return context.getResources().getBoolean(R.bool.clockface_show_background_night);
            case FLAG_SHOW_BACKGROUND_DAY: return context.getResources().getBoolean(R.bool.clockface_show_background_day);
            case FLAG_SHOW_BACKGROUND_AMPM: return context.getResources().getBoolean(R.bool.clockface_show_background_ampm);
            case FLAG_SHOW_BACKGROUND_TWILIGHTS: return context.getResources().getBoolean(R.bool.clockface_show_background_twilights);
            case FLAG_SHOW_TICKS_5M: return context.getResources().getBoolean(R.bool.clockface_show_ticks_5m);
            case FLAG_SHOW_TICKS_15M: return context.getResources().getBoolean(R.bool.clockface_show_ticks_15m);
            case FLAG_SHOW_DATEYEAR:
            default: return false;
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
    protected void setStartAngle() {

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


    public Bitmap makeBitmap(Context context, NaturalHourData data, ClockColorValues appearance)
    {
        if (width <= 0 || height <= 0 || appearance == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(context, canvas, data);
        return bitmap;
    }

    public void draw(Context context, Canvas canvas, NaturalHourData data)
    {
        if (paint == null) {
            initPaint(context);
        }

        drawBackground(context, data, canvas, cX, cY);
        drawTimeArcs(context, data, canvas, cX, cY);
        drawTicks(canvas, cX, cY, is24);
        drawTickLabels(canvas, cX, cY, is24);
        canvas.drawCircle(cX, cY, radiusInner(cX), paintTickLarge);

        if (showTime) {
            drawHourHand(time <= 0 ? System.currentTimeMillis() : time, canvas, cX, cY, radiusInner(cX));
        }
    }

    private ClockColorValues colors = new ClockColorValues();

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

    private Paint paint, paintLabel, paintHand,
            paintTickHuge, paintTickLarge, paintTickMedium, paintTickSmall, paintTickTiny,
            paintArcDayFill, paintArcDayBorder, paintFillDay,
            paintArcNightFill, paintArcNightBorder, paintFillNight,
            paintCenter, paintBackground;

    public static class ClockColorValues
    {
        protected int colorDay = Color.DKGRAY;
        protected int colorDay1 = ColorUtils.setAlphaComponent(colorDay, 128);
        protected int colorDay1AM = Color.LTGRAY;
        protected int colorDay1PM = Color.DKGRAY;
        protected int colorDayLabel = Color.WHITE;
        protected int colorNight = Color.BLUE;
        protected int colorNight1 = ColorUtils.setAlphaComponent(colorNight, 128);
        protected int colorNightLabel = Color.YELLOW;
        protected int colorFace = Color.DKGRAY;
        protected int colorPlate = Color.BLACK;
        protected int colorArcDayBorder = Color.WHITE;
        protected int colorArcNightBorder = Color.DKGRAY;
        protected int colorFrame = Color.WHITE;
        protected int colorCenter = Color.WHITE;
        protected int colorHand = Color.MAGENTA;
        protected int colorLabel = Color.WHITE;
        protected int colorLabel1 = Color.LTGRAY;

        protected int colorCivil = Color.CYAN;
        protected int colorNautical = Color.BLUE;
        protected int colorAstro = Color.BLACK;

        public ClockColorValues() {
        }

        public ClockColorValues(Context context)
        {
            int[] attrs = new int[] {
                    R.attr.clockColorPlate, R.attr.clockColorFace, R.attr.clockColorFrame,
                    R.attr.clockColorCenter, R.attr.clockColorHand,
                    R.attr.clockColorLabel1, R.attr.clockColorLabel2,
                    R.attr.clockColorDayFill, R.attr.clockColorDayText, R.attr.clockColorDayBorder,
                    R.attr.clockColorNightFill, R.attr.clockColorNightText, R.attr.clockColorNightBorder,
                    R.attr.clockColorAM, R.attr.clockColorPM,
                    R.attr.clockColorAstro, R.attr.clockColorNautical, R.attr.clockColorCivil
            };
            TypedArray a = context.obtainStyledAttributes(attrs);

            colorPlate = ContextCompat.getColor(context, a.getResourceId(0, R.color.clockColorPlate_dark));
            colorFace = ContextCompat.getColor(context, a.getResourceId(1, R.color.clockColorFace_dark));
            colorFrame = ContextCompat.getColor(context, a.getResourceId(2, R.color.clockColorFrame_dark));
            colorCenter = ContextCompat.getColor(context, a.getResourceId(3, R.color.clockColorCenter_dark));
            colorHand = ContextCompat.getColor(context, a.getResourceId(4, R.color.clockColorHand_dark));
            colorLabel = ContextCompat.getColor(context, a.getResourceId(5, R.color.clockColorLabel1_dark));
            colorLabel1 = ContextCompat.getColor(context, a.getResourceId(6, R.color.clockColorLabel2_dark));

            colorDay = ContextCompat.getColor(context, a.getResourceId(7, R.color.clockColorDay_dark));
            colorDayLabel = ContextCompat.getColor(context, a.getResourceId(8, R.color.clockColorDayLabel_dark));
            colorArcDayBorder = ContextCompat.getColor(context, a.getResourceId(9, R.color.clockColorDayBorder_dark));
            colorDay1 = colorDay; // TODO

            colorNight = ContextCompat.getColor(context, a.getResourceId(10, R.color.clockColorNight_dark));
            colorNightLabel = ContextCompat.getColor(context, a.getResourceId(11, R.color.clockColorNightLabel_dark));
            colorArcNightBorder = ContextCompat.getColor(context, a.getResourceId(12, R.color.clockColorNightBorder_dark));
            colorNight1 = colorNight;  // TODO

            colorDay1AM = ContextCompat.getColor(context, a.getResourceId(13, R.color.clockColorAM_dark));
            colorDay1PM = ContextCompat.getColor(context, a.getResourceId(14, R.color.clockColorPM_dark));
            colorAstro = ContextCompat.getColor(context, a.getResourceId(15, R.color.clockColorAstro_dark));
            colorNautical = ContextCompat.getColor(context, a.getResourceId(16, R.color.clockColorNautical_dark));
            colorCivil = ContextCompat.getColor(context, a.getResourceId(17, R.color.clockColorCivil_dark));

            a.recycle();
        }
    }

    private SimpleDateFormat dateFormat_short = null, dateFormat_long = null;

    private void initPaint(Context context)
    {
        initFlags(context);
        colors = new ClockColorValues(context);

        dateFormat_short = new SimpleDateFormat(context.getString(R.string.format_date0), Locale.getDefault());
        dateFormat_long = new SimpleDateFormat(context.getString(R.string.format_date0_long), Locale.getDefault());

        handWidth = context.getResources().getDimension(R.dimen.clockface_hand_width);
        centerRadius = (int)(context.getResources().getDimension(R.dimen.clockface_center_width) / 2f);
        arcWidth = (int)context.getResources().getDimension(R.dimen.clockface_arc_width);
        arcStrokeWidth = (int)context.getResources().getDimension(R.dimen.clockface_arc_stroke_width);
        textLarge = (int)context.getResources().getDimension(R.dimen.clockface_text_large);
        textMedium = (int)context.getResources().getDimension(R.dimen.clockface_text_medium);
        textSmall = (int)context.getResources().getDimension(R.dimen.clockface_text_small);

        tickLength_huge = arcWidth;
        tickLength_large = 2 * arcWidth / 3f;
        tickLength_medium = (arcWidth / 2f);
        tickLength_small = (arcWidth / 4f);
        tickLength_tiny = (arcWidth / 8f);

        paint = new Paint();
        paint.setColor(colors.colorLabel);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(context.getResources().getDimension(R.dimen.clockface_stroke_width));
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(textSmall);
        paint.setTypeface(Typeface.create(paint.getTypeface(), Typeface.BOLD));

        paintLabel = new Paint();
        paintLabel.setColor(colors.colorLabel);
        paintLabel.setStyle(Paint.Style.FILL);
        paintLabel.setStrokeWidth(context.getResources().getDimension(R.dimen.clockface_stroke_width));
        paintLabel.setAntiAlias(true);
        paintLabel.setTextAlign(Paint.Align.CENTER);
        paintLabel.setTextSize(textSmall);

        paintHand = new Paint();
        paintHand.setColor(colors.colorHand);
        paintHand.setStyle(Paint.Style.FILL);
        paintHand.setStrokeWidth(handWidth);
        paintHand.setAntiAlias(true);

        paintCenter = new Paint();
        paintCenter.setStyle(Paint.Style.FILL);
        paintCenter.setColor(colors.colorCenter);
        paintCenter.setStrokeWidth(centerRadius);
        paintCenter.setAntiAlias(true);

        paintBackground = new Paint();
        paintBackground.setStyle(Paint.Style.FILL);
        paintBackground.setColor(colors.colorFace);
        paintBackground.setStrokeWidth(centerRadius);
        paintBackground.setAntiAlias(true);

        paintTickHuge = new Paint();
        paintTickHuge.setColor(colors.colorFrame);
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
        paintArcDayBorder.setColor(colors.colorArcDayBorder);
        paintArcDayBorder.setStrokeWidth(arcStrokeWidth);
        paintArcDayBorder.setAntiAlias(true);

        paintArcNightBorder = new Paint();
        paintArcNightBorder.setStyle(Paint.Style.STROKE);
        paintArcNightBorder.setColor(colors.colorArcNightBorder);
        paintArcNightBorder.setStrokeWidth(arcStrokeWidth);
        paintArcNightBorder.setAntiAlias(true);

        paintArcDayFill = new Paint();
        paintArcDayFill.setStyle(Paint.Style.STROKE);
        paintArcDayFill.setColor(colors.colorDay);
        paintArcDayFill.setStrokeWidth(arcWidth);
        paintArcDayFill.setAntiAlias(true);

        paintArcNightFill = new Paint();
        paintArcNightFill.setStyle(Paint.Style.STROKE);
        paintArcNightFill.setColor(colors.colorNight);
        paintArcNightFill.setStrokeWidth(arcWidth);
        paintArcNightFill.setAntiAlias(true);

        paintFillNight = new Paint();
        paintFillNight.setStyle(Paint.Style.FILL);
        paintFillNight.setColor(colors.colorNight1);
        paintFillNight.setAntiAlias(true);

        paintFillDay = new Paint();
        paintFillDay.setStyle(Paint.Style.FILL);
        paintFillDay.setColor(colors.colorDay1);
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
            double sunriseAngle = getAdjustedAngle(startAngle, data.getAngle(naturalHours[0], timezone));
            double sunsetAngle = getAdjustedAngle(startAngle, data.getAngle(naturalHours[12], timezone));

            for (int i=0; i<naturalHours.length; i++)
            {
                boolean isNight = (i >= 12);
                double hourAngle = (isNight ? nightAngle : dayAngle);

                double a = getAdjustedAngle(startAngle, data.getAngle(naturalHours[i], timezone));
                canvas.drawArc(circle_mid, (float) Math.toDegrees(a), (float) Math.toDegrees(hourAngle), false, (isNight ? paintArcNightFill : paintArcDayFill));
                drawRay(canvas, cX, cY, a, r_inner, r_outer, isNight ? paintArcNightBorder : paintArcDayBorder);

                double a1 = a + (hourAngle / 2d);
                double lw = arcWidth * 0.5f;
                double lx = cX + (r_inner + lw) * Math.cos(a1);
                double ly = cY + (r_inner + lw) * Math.sin(a1);

                paint.setColor(isNight ? colors.colorNightLabel : colors.colorDayLabel);
                paint.setTextSize(textSmall);
                CharSequence label = DisplayStrings.romanNumeral(context, ((i % 12) + 1));
                canvas.drawText(label.toString(), (float)(lx), (float)(ly) + (textSmall * 0.5f), paint);
            }
            canvas.drawArc(circle_outer, (float) Math.toDegrees(sunriseAngle), (float) Math.toDegrees(sunsetAngle-sunriseAngle), false, paintArcDayBorder);
            drawRay(canvas, cX, cY, getAdjustedAngle(startAngle, data.getAngle(naturalHours[0], timezone)), r_inner, r_outer, paintArcNightBorder);
            drawRay(canvas, cX, cY, getAdjustedAngle(startAngle, data.getAngle(naturalHours[12], timezone)), r_inner, r_outer, paintArcNightBorder);

            if (flags.getAsBoolean(FLAG_SHOW_VIGILIA))
            {
                int c = 1;
                Path labelPath = new Path();
                double nightSweepAngle = nightAngle * 3;
                for (int i = 12; i<naturalHours.length; i += 3)
                {
                    double a = getAdjustedAngle(startAngle, data.getAngle(naturalHours[i], timezone));
                    canvas.drawArc(circle_mid1, (float) Math.toDegrees(a), (float) Math.toDegrees(nightSweepAngle), false, paintArcNightFill);
                    canvas.drawArc(circle_outer1, (float) Math.toDegrees(a), (float) Math.toDegrees(nightSweepAngle), false, paintArcNightBorder);
                    drawRay(canvas, cX, cY, a, r_outer, r_outer1, paintArcNightBorder);

                    paint.setTextSize(textSmall);
                    CharSequence label = DisplayStrings.formatNightWatchLabel(context, c, true);
                    labelPath.reset();

                    if (startAngle < 0) {
                        labelPath.addArc(circle_mid1, (float) Math.toDegrees(a), (float) Math.toDegrees(nightSweepAngle));
                    } else {
                        labelPath.addArc(circle_mid1, (float) Math.toDegrees(a + nightSweepAngle), (float) Math.toDegrees(-nightSweepAngle));
                    }

                    paint.setColor(colors.colorNightLabel);
                    canvas.drawTextOnPath(label.toString(), labelPath, 0, textSmall / 3f, paint);
                    c++;
                }

                double a0 = getAdjustedAngle(startAngle, data.getAngle(naturalHours[12], timezone) + (nightSweepAngle * 4));
                drawRay(canvas, cX, cY, a0, r_outer, r_outer1, paintArcNightBorder);
            }
            canvas.drawArc(circle_outer, (float) Math.toDegrees(sunsetAngle), (float) Math.toDegrees(2*Math.PI - (sunsetAngle-sunriseAngle)), false, paintArcNightBorder);


        } else {
            canvas.drawCircle(cX, cY, r_outer, paintTickMedium);
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

    protected void drawTicks(Canvas canvas, float cX, float cY, boolean is24)
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

        double a = startAngle;
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

    protected void drawTickLabels(Canvas canvas, float cX, float cY, boolean is24)
    {
        float r0 = radiusInner(cX);
        float rHugeTick = r0 - tickLength_huge;
        float rLargeTick = r0 - tickLength_large;
        float rMediumTick = r0 - tickLength_medium;
        double a = startAngle;
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
            paint.setColor(colors.colorLabel);

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
            paintBackground.setColor(colors.colorPlate);
            canvas.drawCircle(cX, cY, radiusOuter1(cX), paintBackground);
        }

        paintBackground.setColor(colors.colorFace);
        canvas.drawCircle(cX, cY, radiusOuter(cX), paintBackground);

        if (data != null && data.isCalculated())
        {
            long[] twilightHours = data.getTwilightTimes();
            long[] naturalHours = data.getNaturalHours();
            double dayHourAngle = data.getDayHourAngle();
            double dayAngle = getAdjustedAngle(startAngle, data.getAngle(twilightHours[3], timezone));
            double nightAngle = getAdjustedAngle(startAngle, data.getAngle(twilightHours[4], timezone));

            double daySpan = NaturalHourData.simplifyAngle(Math.max(nightAngle, dayAngle) - Math.min(nightAngle, dayAngle));
            double nightSpan = 2 * Math.PI - daySpan;

            if (flags.getAsBoolean(FLAG_SHOW_BACKGROUND_NIGHT)) {
                drawPie(canvas, cX, cY, radiusInner(cX), nightAngle, nightSpan, paintFillNight);
            }

            boolean showTwilights = flags.getAsBoolean(FLAG_SHOW_BACKGROUND_TWILIGHTS);
            boolean showDay = flags.getAsBoolean(FLAG_SHOW_BACKGROUND_DAY);
            if (showTwilights || showDay)
            {
                double a0 = getAdjustedAngle(startAngle, data.getAngle(twilightHours[0], timezone));
                for (int i=1; i<twilightHours.length; i++)
                {
                    double a1 = getAdjustedAngle(startAngle, data.getAngle(twilightHours[i], timezone));
                    if ((i == 4 && showDay) || i != 4 && showTwilights)
                    {
                        double span = a1 - a0;
                        paintFillDay.setColor(getTwilightColor(i-1));
                        drawPie(canvas, cX, cY, radiusInner(cX), a0, span, paintFillDay);
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
                double a1 = getAdjustedAngle(startAngle, data.getAngle(twilightHours[3], timezone));
                paintFillDay.setColor(colors.colorDay1AM);
                drawPie(canvas, cX, cY, radiusInner(cX), a1, middaySpan, paintFillDay);

                double a2 = getAdjustedAngle(startAngle, data.getAngle(naturalHours[6], timezone));
                paintFillDay.setColor(colors.colorDay1PM);
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
            paintLabel.setColor(colors.colorLabel1);
            paintLabel.setTextSize(textSmall);
            canvas.drawTextOnPath(timezone.getID(), path, 0, 0, paintLabel);
        }
    }

    private int getTwilightColor(int i)
    {
        switch (i)
        {
            case 7: return colors.colorNight;
            case 0: case 6: return colors.colorAstro;
            case 1: case 5: return colors.colorNautical;
            case 2: case 4: return colors.colorCivil;
            case 3: default: return colors.colorDay;
        }
    }

    private boolean alongBottom(double radians)
    {
        double angle = NaturalHourData.simplifyAngle(radians);
        return angle >= 0 && angle <= Math.PI;
    }

    protected void drawHourHand(long nowMillis, Canvas canvas, float cX, float cY, float length)
    {
        Calendar now = Calendar.getInstance(timezone);
        now.setTimeInMillis(nowMillis);

        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);

        double a1 = getAdjustedAngle(startAngle, NaturalHourData.getAngle(hour, minute, second));
        double x1 = cX + length * Math.cos(a1);
        double y1 = cY + length * Math.sin(a1);

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

}
