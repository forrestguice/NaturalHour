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

package com.forrestguice.suntimes.naturalhour.ui.tiles;

import android.appwidget.AppWidgetManager;

/**
 * TileLockScreenActivity
 */
public class TileLockScreenActivity extends SuntimesTileActivity
{
    public static final String EXTRA_APPWIDGETID = AppWidgetManager.EXTRA_APPWIDGET_ID;

    public TileLockScreenActivity() {
        super();
    }

    @Override
    protected SuntimesTileBase initTileBase()
    {
        final int appWidgetID = getIntent().getIntExtra(EXTRA_APPWIDGETID, 0);
        switch (appWidgetID)
        {
            case NaturalHourTileBase.TILE_APPWIDGET_ID: return new NaturalHourTileBase(this);
            default: return null;
        }
    }
}
