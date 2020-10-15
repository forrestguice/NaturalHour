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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.forrestguice.suntimes.naturalhour.R;

public class ColorValuesCollectionFragment extends Fragment
{
    protected Spinner selector;
    protected ImageButton editButton;

    public ColorValuesCollectionFragment() {
        setHasOptionsMenu(true);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState)
    {
        View content = inflater.inflate(R.layout.fragment_colorcollection, container, false);
        if (savedState != null) {
            onRestoreInstanceState(savedState);
        }

        selector = content.findViewById(R.id.colorvalues_selector);
        if (selector != null) {
            selector.setOnItemSelectedListener(onItemSelected);
        }

        editButton = content.findViewById(R.id.editButton);
        if (editButton != null) {
            editButton.setOnClickListener(onEditButtonClicked);
        }

        updateViews();
        return content;
    }

    private AdapterView.OnItemSelectedListener onItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            onColorValuesSelected(position);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    };
    protected void onColorValuesSelected(int position)
    {
        if (listener != null) {
            listener.onItemSelected((ColorValuesItem) selector.getItemAtPosition(position));
        }
    }

    private View.OnClickListener onEditButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onEditSelectedItem();
        }
    };
    protected void onEditSelectedItem()
    {
        if (listener != null) {
            listener.onEditClicked(((ColorValuesItem) selector.getSelectedItem()).colorsID);
        }
    }

    protected ArrayAdapter<ColorValuesItem> initAdapter(Context context)
    {
        ColorValuesItem[] items = (colorCollection == null ? new ColorValuesItem[0] : ColorValuesItem.createItems(getActivity(), colorCollection.getCollection()));
        return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, items);
    }

    protected void onRestoreInstanceState(@NonNull Bundle savedState) { /* EMPTY */ }

    protected void updateViews()
    {
        if (selector != null)
        {
            selector.setAdapter(initAdapter(getActivity()));

            if (colorCollection != null)
            {
                int selectedIndex = 0;
                String selectedColorsID = colorCollection.getSelectedColorsID();
                for (int i=0; i<selector.getCount(); i++)
                {
                    ColorValuesItem item = (ColorValuesItem) selector.getItemAtPosition(i);
                    if (item.colorsID.equals(selectedColorsID)) {
                        selectedIndex = i;
                        break;
                    }
                }
                selector.setSelection(selectedIndex, false);
            }
        }
    }

    protected ColorValuesCollection colorCollection = null;
    public void setColorCollection(ColorValuesCollection collection) {
        colorCollection = collection;
        updateViews();
    }

    /**
     * ColorValuesItem
     */
    public static class ColorValuesItem
    {
        public String displayString;
        public String colorsID;

        public ColorValuesItem(String displayString, String colorsID) {
            this.displayString = displayString;
            this.colorsID = colorsID;
        }

        public String toString() {
            return displayString;
        }

        public static ColorValuesItem[] createItems(Context context, String[] colorIDs)
        {
            ColorValuesItem[] items = new ColorValuesItem[colorIDs.length];
            for (int i=0; i<colorIDs.length; i++) {
                items[i] = new ColorValuesItem(colorIDs[i], colorIDs[i]);
            }
            return items;
        }
    }

    /**
     * FragmentListener
     */
    public interface FragmentListener
    {
        void onEditClicked(String colorsID);
        void onItemSelected(ColorValuesItem item);
    }

    protected FragmentListener listener = null;
    public void setFragmentListener(FragmentListener l) {
        listener = l;
    }
}