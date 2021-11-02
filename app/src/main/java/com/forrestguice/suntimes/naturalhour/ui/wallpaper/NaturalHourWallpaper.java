// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2021 Forrest Guice
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

package com.forrestguice.suntimes.naturalhour.ui.wallpaper;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.naturalhour.AppSettings;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourCalculator;
import com.forrestguice.suntimes.naturalhour.data.NaturalHourData;
import com.forrestguice.suntimes.naturalhour.ui.clockview.ClockColorValues;
import com.forrestguice.suntimes.naturalhour.ui.clockview.ClockColorValuesCollection;
import com.forrestguice.suntimes.naturalhour.ui.clockview.NaturalHourClockBitmap;
import com.forrestguice.suntimes.naturalhour.ui.colors.ColorValues;
import com.forrestguice.suntimes.naturalhour.ui.widget.WidgetPreferenceFragment;

import java.util.Calendar;
import java.util.TimeZone;

public class NaturalHourWallpaper extends WallpaperService
{
    @Override
    public Engine onCreateEngine() {
        return new NaturalHourWallpaperEngine(-1);
    }

    /**
     * NaturalHourWallpaperEngine
     */
    private class NaturalHourWallpaperEngine extends Engine
    {
        public static final int UPDATE_INTERVAL_MS = 30000;

        private int appWidgetId;
        private final Handler handler = new Handler();
        private boolean isVisible = false;
        private int width, height;
        private Paint paint;

        public NaturalHourWallpaperEngine( int appWidgetId )
        {
            this.appWidgetId = appWidgetId;
            paint = new Paint();
            // TODO

            handler.post(drawRunner);
        }

        @Override
        public void onVisibilityChanged(boolean visible)
        {
            Log.d(getClass().getSimpleName(), "onVisibilityChanged: " + visible);
            isVisible = visible;
            handler.removeCallbacks(drawRunner);
            if (visible) {
                handler.post(drawRunner);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder)
        {
            super.onSurfaceDestroyed(holder);
            isVisible = false;
            handler.removeCallbacks(drawRunner);
            Log.d(getClass().getSimpleName(), "onSurfaceDestroyed");
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height)
        {
            Log.d(getClass().getSimpleName(), "onSurfaceChanged");
            this.width = width;
            this.height = height;
            handler.removeCallbacks(drawRunner);
            if (isVisible) {
                handler.post(drawRunner);
            }
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onTouchEvent(MotionEvent event)
        {
            // TODO
            super.onTouchEvent(event);
        }

        private void draw(@NonNull Context context, @NonNull Canvas canvas)
        {
            double latitude = 0, longitude = 0, altitude = 0;
            SuntimesInfo suntimesInfo = SuntimesInfo.queryInfo(context);
            if (suntimesInfo != null && suntimesInfo.location != null && suntimesInfo.location.length >= 4)
            {
                latitude = Double.parseDouble(suntimesInfo.location[1]);
                longitude = Double.parseDouble(suntimesInfo.location[2]);
                altitude = Double.parseDouble(suntimesInfo.location[3]);
            }

            ContentResolver resolver = context.getContentResolver();
            NaturalHourCalculator calculator = NaturalHourClockBitmap.getCalculator(AppSettings.getClockIntValue(context, WidgetPreferenceFragment.widgetKeyPrefix(appWidgetId) + NaturalHourClockBitmap.VALUE_HOURMODE, NaturalHourClockBitmap.HOURMODE_DEFAULT));

            Calendar now = Calendar.getInstance();
            NaturalHourData data = new NaturalHourData(now.getTimeInMillis(), latitude, longitude, altitude);
            calculator.calculateData(resolver, data);
            draw(context, canvas, paint, suntimesInfo, data);
        }
        private void draw(@NonNull Context context, @NonNull Canvas canvas, @NonNull Paint paint, @Nullable SuntimesInfo suntimesInfo, @NonNull NaturalHourData data)
        {
            Log.d(getClass().getSimpleName(), "draw: " + width + "," + height);
            String widgetPrefix = WidgetPreferenceFragment.widgetKeyPrefix(appWidgetId);
            int timeMode = AppSettings.getClockIntValue(context, widgetPrefix + AppSettings.KEY_MODE_TIMEFORMAT, AppSettings.TIMEMODE_DEFAULT);
            int tzMode = AppSettings.getClockIntValue(context, widgetPrefix + AppSettings.KEY_MODE_TIMEZONE, AppSettings.TZMODE_DEFAULT );
            boolean is24 = AppSettings.fromTimeFormatMode(context, timeMode, suntimesInfo);
            TimeZone timezone = AppSettings.fromTimeZoneMode(context, tzMode, suntimesInfo);

            int clockSizePx = Math.min(width, height);

            int left = (width - clockSizePx) / 2;      // TODO: configurable?
            int top = 0;                               // top
            //int top = (height - clockSizePx) / 2;    // center
            //int top = (height - clockSizePx);        // bottom

            NaturalHourClockBitmap clockView = new NaturalHourClockBitmap(context, clockSizePx);
            clockView.setTimeZone(timezone);
            clockView.set24HourMode(is24);

            for (String key : NaturalHourClockBitmap.FLAGS) {
                String widgetKey = widgetPrefix + key;
                if (AppSettings.containsKey(context, widgetKey)) {
                    clockView.setFlag(key, AppSettings.getClockFlag(context, widgetKey));
                }
            }
            for (String key : NaturalHourClockBitmap.VALUES) {
                String widgetKey = widgetPrefix + key;
                if (AppSettings.containsKey(context, widgetKey)) {
                    clockView.setValue(key, AppSettings.getClockIntValue(context, widgetKey));
                }
            }

            ClockColorValuesCollection<ClockColorValues> colors = new ClockColorValuesCollection<>(context);
            ColorValues clockAppearance = colors.getSelectedColors(context, appWidgetId);
            clockView.setColors(clockAppearance);

            Bitmap bitmap = clockView.makeBitmap(context, data);
            canvas.drawColor(clockAppearance.getColor(ClockColorValues.COLOR_PLATE));
            canvas.drawBitmap(bitmap, left, top, paint);
        }

        private void draw()
        {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    draw(getApplicationContext(), canvas);
                }

            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }

            handler.removeCallbacks(drawRunner);
            if (isVisible) {
                handler.postDelayed(drawRunner, UPDATE_INTERVAL_MS);
            }
        }
        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
            }
        };
    }
}