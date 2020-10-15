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
    protected void saveColors(SharedPreferences prefs, String colorsID, T values) {
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
    protected HashMap<String, T> colorValues = new HashMap<>();
    protected T getColors( Context context, @NonNull String colorsID )
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
    protected void setColors(Context context, String colorsID, T values)
    {
        colorValues.put(colorsID, values);
        saveColors(getSharedPreferences(context), colorsID, values);
        if (collection.add(colorsID)) {
            saveCollection(getSharedPreferences(context));
        }
    }

    protected String selected = null;
    public T getSelectedColors(Context context)
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