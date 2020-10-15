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

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;

public abstract class ColorValues implements Parcelable
{
    public abstract String[] getColorKeys();

    public ColorValues() {}
    public ColorValues(ColorValues other) {
        loadColorValues(other);
    }
    protected ColorValues(Parcel in) {
        loadColorValues(in);
    }
    public ColorValues(SharedPreferences prefs, String prefix) {
        loadColorValues(prefs, prefix);
    }

    public void loadColorValues(@NonNull Parcel in) {
        for (String key : getColorKeys()) {
            setColor(key, in.readInt());
        }
    }
    public void loadColorValues(@NonNull ColorValues other)
    {
        for (String key : other.getColorKeys()) {
            setColor(key, other.getColor(key));
        }
    }
    public void loadColorValues(SharedPreferences prefs, String prefix)
    {
        for (String key : getColorKeys()) {
            setColor(key, prefs.getInt(prefix + key, Color.WHITE));
        }
    }

    protected ContentValues values = new ContentValues();
    public ContentValues getContentValues() {
        return new ContentValues(values);
    }
    public void setColor(String key, int color) {
        values.put(key, color);
    }

    public int getColor(String key)
    {
        if (values.containsKey(key)) {
            return values.getAsInteger(key);
        } else return Color.WHITE;
    }

    public ArrayList<Integer> getColors()
    {
        ArrayList<Integer> list = new ArrayList<>();
        for (String key : getColorKeys()) {
            list.add(getColor(key));
        }
        return list;
    }

    public void putColors(SharedPreferences.Editor prefs, String prefix)
    {
        for (String key : getColorKeys()) {
            prefs.putInt(prefix + key, values.getAsInteger(key));
        }
        prefs.apply();
    }

    public void putColors(ContentValues other) {
        other.putAll(values);
    }

    public int colorKeyIndex(@NonNull String key)
    {
        String[] keys = getColorKeys();
        if (keys != null) {
            for (int i=0; i < keys.length; i++) {
                if (key.equals(keys[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        for (String key : getColorKeys()) {
            dest.writeInt(values.getAsInteger(key));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * @return yaml
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder("--- # ColorValues\n");
        for (String key : getColorKeys())
        {
            result.append("- ");
            result.append(key);
            result.append(": \"#");
            result.append( Integer.toHexString(getColor(key)) );
            result.append("\"\n");
        }
        return result.toString();
    }
}