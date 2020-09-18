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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.view.View;

import com.forrestguice.suntimes.romantime.R;
import com.forrestguice.suntimes.romantime.data.RomanTimeData;

import java.util.Calendar;
import java.util.TimeZone;

public class RomanTimeClockView extends View
{
    public static final double START_TOP = -Math.PI / 2d;
    public static final double START_BOTTOM = Math.PI / 2d;

    protected RomanTimeData data;
    public void setData(RomanTimeData data) {
        this.data = data;
    }

    protected TimeZone timezone = TimeZone.getDefault();
    public void setTimeZone( TimeZone timezone) {
        this.timezone = timezone;
    }

    protected boolean is24 = true;
    public void set24HourMode(boolean value) {
        is24 = value;
    }

    protected double startAngle = START_BOTTOM;
    public void setStartAngle(double radianValue) {
        startAngle = radianValue;
    }

    protected boolean showMinorTickLabels = true;
    public void setShowMinorTickLabels(boolean value) {
        showMinorTickLabels = value;
    }

    protected boolean showNightBackground = true;
    public void setShowNightBackground(boolean value) {
        showNightBackground = value;
    }

    protected boolean showVigilia = true;
    public void setShowVigilia(boolean value) {
        showVigilia = value;
    }

    protected boolean showTime = true;
    public void setShowTime(boolean value) {
        showTime = value;
    }

    public RomanTimeClockView(Context context) {
        super(context);
    }

    public RomanTimeClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RomanTimeClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        cX = w / 2f;
        cY = h / 2f;
    }
    private float cX, cY;

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (paint == null) {
            initPaint(getContext());
        }

        drawBackground(canvas, cX, cY);
        drawTimeArcs(canvas, cX, cY);
        drawTicks(canvas, cX, cY, is24);
        drawTickLabels(canvas, cX, cY, is24);
        drawFrame(canvas, cX, cY);

        if (showTime) {
            drawHourHand(Calendar.getInstance(timezone), canvas, cX, cY, radiusInner(cX));
        }
    }

    private int colorDay = Color.DKGRAY;
    private int colorDayLabel = Color.WHITE;

    private int colorNight = Color.BLUE;
    private int colorNight1 = ColorUtils.setAlphaComponent(colorNight, 128);
    private int colorNightLabel = Color.YELLOW;

    private int colorBackground = Color.DKGRAY;
    private int colorArcBorder = Color.WHITE;
    private int colorFrame = Color.WHITE;
    private int colorCenter = Color.WHITE;
    private int colorHand = Color.MAGENTA;
    private int colorLabel = Color.WHITE;

    private int arcStrokeWidth;
    private int arcWidth;
    private int centerRadius;

    private int tickLength;

    private int textLarge;
    private int textMedium;
    private int textSmall;

    private Paint paint, paintHand, paintTickMajor, paintTickMinor, paintArcFillDay, paintArcFillNight, paintArcBorder, paintFillNight, paintCenter, paintBackground;

    private void initPaint(Context context)
    {
        centerRadius = (int)(context.getResources().getDimension(R.dimen.clockface_center_width) / 2f);
        arcWidth = (int)context.getResources().getDimension(R.dimen.clockface_arc_width);
        arcStrokeWidth = (int)context.getResources().getDimension(R.dimen.clockface_arc_stroke_width);
        textLarge = (int)context.getResources().getDimension(R.dimen.clockface_text_large);
        textMedium = (int)context.getResources().getDimension(R.dimen.clockface_text_medium);
        textSmall = (int)context.getResources().getDimension(R.dimen.clockface_text_small);

        tickLength = arcWidth;

        paint = new Paint();
        paint.setColor(colorLabel);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(context.getResources().getDimension(R.dimen.clockface_stroke_width));
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(textSmall);
        paint.setTypeface(Typeface.create(paint.getTypeface(), Typeface.BOLD));

        paintHand = new Paint();
        paintHand.setColor(colorHand);
        paintHand.setStyle(Paint.Style.FILL);
        paintHand.setStrokeWidth(context.getResources().getDimension(R.dimen.clockface_hand_width));
        paintHand.setAntiAlias(true);

        paintCenter = new Paint();
        paintCenter.setStyle(Paint.Style.FILL);
        paintCenter.setColor(colorCenter);
        paintCenter.setStrokeWidth(centerRadius);
        paintCenter.setAntiAlias(true);

        paintBackground = new Paint();
        paintBackground.setStyle(Paint.Style.FILL);
        paintBackground.setColor(colorBackground);
        paintBackground.setStrokeWidth(centerRadius);
        paintBackground.setAntiAlias(true);

        paintTickMajor = new Paint();
        paintTickMajor.setColor(colorFrame);
        paintTickMajor.setStyle(Paint.Style.STROKE);
        paintTickMajor.setStrokeWidth(context.getResources().getDimension(R.dimen.clockface_tick_major_width));
        paintTickMajor.setAntiAlias(true);
        paintTickMajor.setTextAlign(Paint.Align.CENTER);

        paintTickMinor = new Paint();
        paintTickMinor.setColor(colorFrame);
        paintTickMinor.setStyle(Paint.Style.STROKE);
        paintTickMinor.setStrokeWidth(context.getResources().getDimension(R.dimen.clockface_tick_minor_width));
        paintTickMinor.setAntiAlias(true);
        paintTickMinor.setTextAlign(Paint.Align.CENTER);

        paintArcBorder = new Paint();
        paintArcBorder.setStyle(Paint.Style.STROKE);
        paintArcBorder.setColor(colorArcBorder);
        paintArcBorder.setStrokeWidth(arcWidth);
        paintArcBorder.setAntiAlias(true);

        paintArcFillDay = new Paint();
        paintArcFillDay.setStyle(Paint.Style.STROKE);
        paintArcFillDay.setColor(colorDay);
        paintArcFillDay.setStrokeWidth(arcWidth - arcStrokeWidth);
        paintArcFillDay.setAntiAlias(true);

        paintArcFillNight = new Paint();
        paintArcFillNight.setStyle(Paint.Style.STROKE);
        paintArcFillNight.setColor(colorNight);
        paintArcFillNight.setStrokeWidth(arcWidth - arcStrokeWidth);
        paintArcFillNight.setAntiAlias(true);

        paintFillNight = new Paint();
        paintFillNight.setStyle(Paint.Style.FILL);
        paintFillNight.setColor(colorNight1);
        paintFillNight.setAntiAlias(true);
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

    protected void drawTimeArcs(Canvas canvas, float cX, float cY)
    {
        float r_inner = radiusInner(cX);
        float r_mid = r_inner + (arcWidth / 2f);
        final RectF circle_mid = new RectF(cX - r_mid, cY - r_mid, cX + r_mid, cY + r_mid);

        float r_outer = radiusOuter(cX);
        float r_mid1 = r_outer + (arcWidth / 2f);
        final RectF circle_mid1 = new RectF(cX - r_mid1, cY - r_mid1, cX + r_mid1, cY + r_mid1);

        if (data != null)
        {
            double dayAngle = data.getDayHourAngle();
            double nightAngle = data.getNightHourAngle();
            long[] romanHours = data.getRomanHours();

            float r_outer1 = radiusOuter1(cX);
            final RectF circle_outer1 = new RectF(cX - r_outer1, cY - r_outer1, cX + r_outer1, cY + r_outer1);

            for (int i=0; i<romanHours.length; i++)
            {
                boolean isNight = (i >= 12);
                double hourAngle = (isNight ? nightAngle : dayAngle);

                double a = getAdjustedAngle(startAngle, RomanTimeData.getAngle(romanHours[i], timezone));
                canvas.drawArc(circle_mid, (float) Math.toDegrees(a), (float) Math.toDegrees(hourAngle), false, (isNight ? paintArcFillNight : paintArcFillDay));
                drawRay(canvas, cX, cY, a, r_inner, r_outer, paintTickMinor);

                double a1 = a + (hourAngle / 2d);
                double lw = arcWidth * 0.5f;
                double lx = cX + (r_inner + lw) * Math.cos(a1);
                double ly = cY + (r_inner + lw) * Math.sin(a1);

                paint.setColor(isNight ? colorNightLabel : colorDayLabel);
                paint.setTextSize(textSmall);
                CharSequence label = DisplayStrings.romanNumeral(getContext(), ((i % 12) + 1));
                canvas.drawText(label.toString(), (float)(lx), (float)(ly) + (textSmall * 0.5f), paint);
            }
            drawRay(canvas, cX, cY, getAdjustedAngle(startAngle, RomanTimeData.getAngle(romanHours[0], timezone)), r_inner, r_outer, paintTickMinor);

            if (showVigilia)
            {
                int c = 1;
                Path labelPath = new Path();
                double nightSweepAngle = nightAngle * 3;
                for (int i = 12; i<romanHours.length; i += 3)
                {
                    double a = getAdjustedAngle(startAngle, RomanTimeData.getAngle(romanHours[i], timezone));
                    canvas.drawArc(circle_mid1, (float) Math.toDegrees(a), (float) Math.toDegrees(nightSweepAngle), false, paintArcFillNight);
                    canvas.drawArc(circle_outer1, (float) Math.toDegrees(a), (float) Math.toDegrees(nightSweepAngle), false, paintTickMinor);
                    drawRay(canvas, cX, cY, a, r_outer, r_outer1, paintTickMinor);

                    paint.setTextSize(textSmall);
                    CharSequence label = DisplayStrings.formatNightWatchLabel(getContext(), c);
                    labelPath.reset();


                    if (startAngle < 0) {
                        labelPath.addArc(circle_mid1, (float) Math.toDegrees(a), (float) Math.toDegrees(nightSweepAngle));
                    } else {
                        labelPath.addArc(circle_mid1, (float) Math.toDegrees(a + nightSweepAngle), (float) Math.toDegrees(-nightSweepAngle));
                    }

                    paint.setColor(colorNightLabel);
                    canvas.drawTextOnPath(label.toString(), labelPath, 0, textSmall / 3f, paint);
                    c++;
                }

                double a0 = getAdjustedAngle(startAngle, RomanTimeData.getAngle(romanHours[12], timezone) + (nightSweepAngle * 4));
                drawRay(canvas, cX, cY, a0, r_outer, r_outer1, paintTickMinor);
            }
        }
        canvas.drawCircle(cX, cY, r_outer, paintTickMinor);
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

    protected void drawPie(Canvas canvas, double cX, double cY, double radius, double radians, Paint paint)
    {
        final RectF circle_inner = new RectF((float)(cX - radius), (float)(cY - radius), (float)(cX + radius), (float)(cY + radius));
        Path path = new Path();
        path.moveTo((float) cX, (float) cY);
        path.lineTo((float) (cX + (radius * Math.cos(radians))), (float) (cY + (radius * Math.sin(radians))));
        path.addArc(circle_inner, (float) Math.toDegrees(radians), (float) Math.toDegrees(data.getNightHourAngle() * 12));
        path.lineTo((float) cX, (float) cY);
        path.close();
        canvas.drawPath(path, paint);
    }

    protected void drawTicks(Canvas canvas, float cX, float cY, boolean is24)
    {
        float r0 = radiusInner(cX);
        float rMajorTick = r0 - tickLength;
        float rMediumTick = r0 - tickLength / 2f;
        float rMinorTick = r0 - (tickLength / 3f);
        double a = startAngle;
        for (int i=1; i<=24; i++)
        {
            a += ((2 * Math.PI) / 24f);
            float r = (i % 3 == 0) ? (i % 6 == 0) ? rMajorTick : rMediumTick : rMinorTick;
            double cosA = Math.cos(a);
            double sinA = Math.sin(a);

            double x0 = cX + r * cosA;
            double y0 = cY + r * sinA;

            double x1 = cX + r0 * cosA;
            double y1 = cY + r0 * sinA;

            canvas.drawLine((float)x0, (float)y0, (float)x1, (float)y1, (i % 6 == 0 ? paintTickMajor : paintTickMinor));
        }
    }


    protected void drawTickLabels(Canvas canvas, float cX, float cY, boolean is24)
    {
        float r0 = radiusInner(cX);
        float rMajorTick = r0 - tickLength;
        float rMediumTick = r0 - tickLength / 2f;
        float rMinorTick = r0 - (tickLength / 3f);
        double a = startAngle;
        for (int i=1; i<=24; i++)
        {
            a += ((2 * Math.PI) / 24f);
            float r = (i % 3 == 0) ? (i % 6 == 0) ? rMajorTick : rMediumTick : rMinorTick;
            double cosA = Math.cos(a);
            double sinA = Math.sin(a);

            double lw = i % 3 == 0 ? arcWidth : arcWidth * 0.5f;
            double lx = cX + (r - lw) * cosA;
            double ly = cY + (r - lw) * sinA;

            boolean isMajorTick = (i % 6 == 0);
            boolean isMinorTick = (i % 3 != 0);

            int textSize = isMajorTick ? textLarge :
                    i % 3 == 0 ? textMedium : textSmall;
            paint.setTextSize(textSize);
            paint.setColor(colorLabel);

            int j = is24 ? i : (i == 24 || i == 12 ? 12 : (i % 12));
            String label = is24 ? (i < 10 ? "0" + j : "" + j) : "" + j;

            if (!isMinorTick || showMinorTickLabels) {
                canvas.drawText(label, (float)(lx), (float)(ly) + (textSize * 0.25f), paint);
            }
        }
    }

    protected void drawBackground(Canvas canvas, float cX, float cY)
    {
        canvas.drawCircle(cX, cY, cX - arcWidth, paintBackground);
        //canvas.drawCircle(cX, cY, cX, paintBackground);

        if (data != null && showNightBackground)
        {
            long[] romanHours = data.getRomanHours();
            double a = getAdjustedAngle(startAngle, RomanTimeData.getAngle(romanHours[12], timezone));
            drawPie(canvas, cX, cY, radiusInner(cX), a, paintFillNight);
        }
    }

    protected void drawFrame(Canvas canvas, float cX, float cY)
    {
        canvas.drawCircle(cX, cY, radiusInner(cX), paintTickMinor);
    }

    protected void drawHourHand(Calendar now, Canvas canvas, float cX, float cY, float length)
    {
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);

        double angle = getAdjustedAngle(startAngle, RomanTimeData.getAngle (hour, minute, second));
        double x1 = cX + length * Math.cos(angle);
        double y1 = cY + length * Math.sin(angle);
        canvas.drawLine(cX, cY, (float)x1, (float)y1, paintHand);
        canvas.drawCircle(cX, cY, centerRadius, paintCenter);
    }

}
