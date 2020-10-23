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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.forrestguice.suntimes.naturalhour.R;

public class ColorValuesSelectFragment extends Fragment
{
    public static final String ARG_APPWIDGETID = "appWidgetID";
    public static final int DEF_APPWIDGETID = 0;

    public static final String ARG_ALLOW_EDIT = "allowEdit";
    public static final boolean DEF_ALLOW_EDIT = true;

    public static final String ARG_SHOW_LABEL = "showLabel";
    public static final boolean DEF_SHOW_LABEL = false;

    protected TextView label;
    protected Spinner selector;
    protected ImageButton addButton, editButton, menuButton;

    public ColorValuesSelectFragment()
    {
        setHasOptionsMenu(false);

        Bundle args = new Bundle();
        args.putBoolean(ARG_ALLOW_EDIT, DEF_ALLOW_EDIT);
        args.putBoolean(ARG_SHOW_LABEL, DEF_SHOW_LABEL);
        args.putInt(ARG_APPWIDGETID, DEF_APPWIDGETID);
        setArguments(args);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState)
    {
        View content = inflater.inflate(R.layout.fragment_colorselector, container, false);
        if (savedState != null) {
            onRestoreInstanceState(savedState);
        }

        label = (TextView) content.findViewById(R.id.color_values_selector_label);

        selector = content.findViewById(R.id.colorvalues_selector);
        if (selector != null) {
            selector.setOnItemSelectedListener(onItemSelected);
        }

        editButton = content.findViewById(R.id.editButton);
        if (editButton != null) {
            editButton.setOnClickListener(onEditButtonClicked);
        }

        addButton = content.findViewById(R.id.addButton);
        if (addButton != null) {
            addButton.setOnClickListener(onAddButtonClicked);
        }

        menuButton = content.findViewById(R.id.menuButton);
        if (menuButton != null) {
            menuButton.setOnClickListener(onMenuButtonClicked);
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
    public String getSelectedID() {
        if (selector != null) {
            ColorValuesItem item = (ColorValuesItem) selector.getSelectedItem();
            return item.colorsID;
        } else return null;
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

    private View.OnClickListener onAddButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onAddItem();
        }
    };
    protected void onAddItem()
    {
        if (listener != null) {
            listener.onAddClicked(((ColorValuesItem) selector.getSelectedItem()).colorsID);
        }
    }

    private View.OnClickListener onMenuButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showOverflowMenu(getActivity(), v);
        }
    };

    protected void showOverflowMenu(Context context, View v)
    {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_colorlist, popup.getMenu());
        onPrepareOverflowMenu(context, popup.getMenu());
        popup.setOnMenuItemClickListener(onOverflowMenuItemSelected);
        popup.show();
    }
    protected void onPrepareOverflowMenu(Context context, Menu menu) { /* EMPTY */ }
    private PopupMenu.OnMenuItemClickListener onOverflowMenuItemSelected = new PopupMenu.OnMenuItemClickListener()
    {
        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.action_colors_export:
                    onExportColors();
                    return true;

                case R.id.action_colors_import:
                    onImportColors();
                    return true;
            }
            return false;
        }
    };

    protected void onExportColors()
    {
        // TODO
        Toast.makeText(getActivity(), "export (TODO)", Toast.LENGTH_SHORT).show();
    }
    protected void onImportColors()
    {
        // TODO
        Toast.makeText(getActivity(), "import (TODO)", Toast.LENGTH_SHORT).show();
    }

    protected ArrayAdapter<ColorValuesItem> initAdapter(Context context)
    {
        ColorValuesItem[] items = (colorCollection == null ? new ColorValuesItem[0] : ColorValuesItem.createItems(getActivity(), colorCollection.getCollection()));
        return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, items);
    }

    protected void onRestoreInstanceState(@NonNull Bundle savedState) { /* EMPTY */ }

    protected void updateViews()
    {
        if (label != null) {
            label.setVisibility(getShowLabel() ? View.VISIBLE : View.GONE);
        }

        if (selector != null)
        {
            selector.setAdapter(initAdapter(getActivity()));

            if (colorCollection != null)
            {
                int selectedIndex = 0;
                String selectedColorsID = colorCollection.getSelectedColorsID(getActivity(), getAppWidgetID());
                if (selectedColorsID == null) {
                    selectedColorsID = colorCollection.getSelectedColorsID(getActivity(), 0);
                }

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

        boolean allowEdit = allowEdit();
        if (addButton != null) {
            addButton.setVisibility(allowEdit ? View.VISIBLE : View.GONE);
        }
        if (editButton != null) {
            editButton.setVisibility(allowEdit ? View.VISIBLE : View.GONE);
        }
    }

    protected ColorValuesCollection colorCollection = null;
    public void setColorCollection(ColorValuesCollection collection) {
        colorCollection = collection;
        updateViews();
    }

    public void setAllowEdit(boolean allowEdit) {
        setBoolArg(ARG_ALLOW_EDIT, allowEdit);
    }
    public boolean allowEdit() {
        return getBoolArg(ARG_ALLOW_EDIT, DEF_ALLOW_EDIT);
    }

    public void setShowLabel(boolean showLabel) {
        setBoolArg(ARG_SHOW_LABEL, showLabel);
    }
    public boolean getShowLabel() {
        return getBoolArg(ARG_SHOW_LABEL, DEF_SHOW_LABEL);
    }

    protected void setBoolArg(String key, boolean value) {
        Bundle args = getArguments();
        if (args != null) {
            args.putBoolean(key, value);
            updateViews();
        }
    }
    protected boolean getBoolArg(String key, boolean defValue) {
        Bundle args = getArguments();
        return args != null ? args.getBoolean(key, defValue) : defValue;
    }

    public void setAppWidgetID(int appWidgetID)
    {
        Bundle args = getArguments();
        if (args != null) {
            args.putInt(ARG_APPWIDGETID, appWidgetID);
            updateViews();
        }
    }
    public int getAppWidgetID() {
        Bundle args = getArguments();
        return args != null ? args.getInt(ARG_APPWIDGETID, DEF_APPWIDGETID) : DEF_APPWIDGETID;
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
        void onAddClicked(String colorsID);
        void onEditClicked(String colorsID);
        void onItemSelected(ColorValuesItem item);
    }

    protected FragmentListener listener = null;
    public void setFragmentListener(FragmentListener l) {
        listener = l;
    }
}