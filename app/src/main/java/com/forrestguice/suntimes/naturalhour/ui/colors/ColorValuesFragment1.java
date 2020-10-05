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

package com.forrestguice.suntimes.naturalhour.ui.colors;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.util.ArrayList;

public class ColorValuesFragment1 extends ColorValuesFragment
{
    @Override
    protected Intent pickColorIntent(String key, int requestCode)
    {
        int currentColor = colorValues.getColor(key);
        ArrayList<Integer> recentColors = colorValues.getColors();

        Uri data = Uri.parse("color://" + String.format("#%08X", currentColor));
        Bundle extras = new Bundle();
        extras.putInt("color", currentColor);
        extras.putBoolean("showAlpha", true);
        if (recentColors != null) {
            extras.putIntegerArrayList("recentColors", recentColors);
        }

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setClassName("com.forrestguice.suntimeswidget", "com.forrestguice.suntimeswidget.settings.colors.ColorActivity");
        intent.setData(data);
        intent.putExtras(extras);
        return intent;
    }

    @Override
    protected void onPickColorResult(String key, Intent data) {
        setColor(key, data.getIntExtra("color", colorValues.getColor(key)));
    }

}
