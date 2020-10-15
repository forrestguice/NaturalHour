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

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public abstract class ColorValuesCollection<T extends ColorValues>
{
    public static final String KEY_COLLECTION = "colorValuesCollection";
    public static final String KEY_SELECTED = "selectedValues";

    public ColorValuesCollection() {}
    public ColorValuesCollection(Context context) {
        loadCollection(getSharedPreferences(context));
    }

    protected Set<String> collection = new TreeSet<String>();
    public String[] getCollection() {
        return collection.toArray(new String[0]);
    }
    protected void loadCollection(SharedPreferences prefs) {
        collection.clear();
        collection.addAll(prefs.getStringSet(KEY_COLLECTION, null));
        selected = prefs.getString(KEY_SELECTED, null);
    }
    protected void saveCollection(SharedPreferences prefs) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(KEY_COLLECTION, collection);
        editor.apply();
    }

    protected T loadColors(SharedPreferences prefs, String colorsID)
    {
        T values = getInstanceOfT();
        values.loadColorValues(prefs, colorsID);
        return values;
    }
    protected void saveColors(SharedPreferences prefs, String colorsID, ColorValues values) {
        SharedPreferences.Editor editor = prefs.edit();
        values.putColors(editor, colorsID);
        editor.apply();
    }

    private T getInstanceOfT()
    {
        try {
            ParameterizedType superClass = (ParameterizedType) getClass().getGenericSuperclass();
            @SuppressWarnings("unchecked")
            Class<T> type = (Class<T>) superClass.getActualTypeArguments()[0];
            return type.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public abstract T getDefaultColors(Context context);
    protected HashMap<String, ColorValues> colorValues = new HashMap<>();
    public ColorValues getColors( Context context, @NonNull String colorsID )
    {
        if (!colorValues.containsKey(colorsID))
        {
            T values = loadColors(getSharedPreferences(context), colorsID);
            if (values != null) {
                colorValues.put(colorsID, values);
            }
        }
        return (colorValues.containsKey(colorsID) ? colorValues.get(colorsID) : null);
    }
    public void setColors(Context context, String colorsID, ColorValues values)
    {
        colorValues.put(colorsID, values);
        saveColors(getSharedPreferences(context), colorsID, values);
        if (collection.add(colorsID)) {
            saveCollection(getSharedPreferences(context));
        }
    }

    protected String selected = null;
    public ColorValues getSelectedColors(Context context)
    {
        if (selected != null && colorValues.containsKey(selected)) {
            return getColors(context, selected);
        } else return getDefaultColors(context);
    }
    public String getSelectedColorsID() {
        return selected;
    }
    public void setSelectedColorsID(Context context, String colorsID)
    {
        selected = colorsID;
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_SELECTED, colorsID);
        editor.apply();
    }

    public abstract String getSharedPrefsName();
    protected SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(getSharedPrefsName(), 0);
    }
}