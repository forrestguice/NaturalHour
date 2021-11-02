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

import android.graphics.Canvas;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class NaturalHourWallpaper extends WallpaperService
{
    @Override
    public Engine onCreateEngine()
    {
        return new NaturalHourWallpaperEngine();
    }

    /**
     * NaturalHourWallpaperEngine
     */
    private class NaturalHourWallpaperEngine extends Engine
    {
        public static final int UPDATE_INTERVAL_MS = 30000;

        private final Handler handler = new Handler();
        private boolean isVisible = false;
        private int width, height;

        public NaturalHourWallpaperEngine()
        {
            // TODO
            handler.post(drawRunner);
        }

        @Override
        public void onVisibilityChanged(boolean visible)
        {
            isVisible = visible;
            if (visible) {
                handler.post(drawRunner);
            } else {
                handler.removeCallbacks(drawRunner);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder)
        {
            super.onSurfaceDestroyed(holder);
            isVisible = false;
            handler.removeCallbacks(drawRunner);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height)
        {
            this.width = width;
            this.height = height;
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onTouchEvent(MotionEvent event)
        {
            // TODO
            super.onTouchEvent(event);
        }

        private void draw()
        {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null)
                {
                    // TODO
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