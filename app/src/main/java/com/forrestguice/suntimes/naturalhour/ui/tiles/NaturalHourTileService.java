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

import android.annotation.TargetApi;
import android.content.Context;
import android.service.quicksettings.Tile;

@TargetApi(24)
public class NaturalHourTileService extends SuntimesTileService
{
    @Override
    protected SuntimesTileBase initTileBase() {
        return new NaturalHourTileBase(null);
    }

    @Override
    protected int appWidgetId() {
        return NaturalHourTileBase.TILE_APPWIDGET_ID;
    }

    @Override
    protected void updateTile(Context context)
    {
        Tile tile = getQsTile();
        tile.setLabel("TODO");
        //tile.setIcon(Icon.createWithResource(this, R.drawable.ic_action_time);
        updateTileState(context, tile).updateTile();
    }

}
