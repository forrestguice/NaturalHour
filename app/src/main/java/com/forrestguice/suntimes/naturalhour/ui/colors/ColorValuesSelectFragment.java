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
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.forrestguice.suntimes.naturalhour.ui.Toast;
import com.forrestguice.suntimes.naturalhour.R;

public class ColorValuesSelectFragment extends ColorValuesFragment
{
    public static final String ARG_APPWIDGETID = "appWidgetID";
    public static final int DEF_APPWIDGETID = 0;

    public static final String ARG_ALLOW_EDIT = "allowEdit";
    public static final boolean DEF_ALLOW_EDIT = true;

    public static final String ARG_SHOW_LABEL = "showLabel";
    public static final boolean DEF_SHOW_LABEL = false;

    public static final String ARG_SHOW_BACK = "showBack";
    public static final boolean DEF_SHOW_BACK = true;

    public static final String ARG_SHOW_MENU = "showMenu";
    public static final boolean DEF_SHOW_MENU = true;

    protected TextView label;
    protected Spinner selector;
    protected ImageButton addButton, editButton, backButton, menuButton;

    public ColorValuesSelectFragment()
    {
        setHasOptionsMenu(false);

        Bundle args = new Bundle();
        args.putBoolean(ARG_ALLOW_EDIT, DEF_ALLOW_EDIT);
        args.putBoolean(ARG_SHOW_LABEL, DEF_SHOW_LABEL);
        args.putBoolean(ARG_SHOW_BACK, DEF_SHOW_BACK);
        args.putBoolean(ARG_SHOW_MENU, DEF_SHOW_MENU);
        args.putInt(ARG_APPWIDGETID, DEF_APPWIDGETID);
        setArguments(args);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState)
    {
        android.support.v7.view.ContextThemeWrapper contextWrapper = new android.support.v7.view.ContextThemeWrapper(getActivity(), getThemeResID());    // hack: contextWrapper required because base theme is not properly applied
        View content = inflater.cloneInContext(contextWrapper).inflate(R.layout.fragment_colorselector, container, false);
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

        backButton = content.findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(onBackButtonClicked);
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
            return (item != null ? item.colorsID : null);
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
            ColorValuesItem item = (ColorValuesItem) selector.getSelectedItem();
            listener.onEditClicked(item != null ? item.colorsID : null);
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
            ColorValuesItem item = (ColorValuesItem) selector.getSelectedItem();
            listener.onAddClicked(item != null ? item.colorsID : null);
        }
    }

    private View.OnClickListener onBackButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBack();
        }
    };
    protected void onBack() {
        if (listener != null) {
            listener.onBackClicked();
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
                case R.id.action_colors_add:
                    onAddItem();
                    return true;

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
        Context context = getActivity();
        if (colorCollection != null && context != null)
        {
            StringBuilder exportString = new StringBuilder(colorCollection.toString());
            for (String colorsID : colorCollection.getCollection())
            {
                ColorValues colors = colorCollection.getColors(context, colorsID);
                exportString.append("\n");
                exportString.append(colors.toJSON());
            }
            exportString.append("...");

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, exportString.toString());
            startActivity(Intent.createChooser(intent, null));
        }
    }

    protected void onImportColors()
    {
        Context context = getActivity();
        if (colorCollection != null && context != null)
        {
            String importString = "";  // TODO: user input
            importColors(context, importString);
        }
    }
    protected void importColors(@NonNull Context context, String jsonString)
    {
        ColorValues values = createColorValues(jsonString);
        if (values != null)
        {
            String id = values.getID();
            if (!colorCollection.hasColors(id))
            {
                colorCollection.setColors(context, values);
                Toast.makeText(getActivity(), context.getString(R.string.msg_colors_imported, id), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * createColorsValues
     * @param jsonString json colorvalues string
     * @return defaults null; this method should be overridden by concrete implementations to create and return a valid ColorValues obj
     */
    @Nullable
    protected ColorValues createColorValues(String jsonString) {
        return null;
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
        if (backButton != null) {
            backButton.setVisibility(getShowBack() ? View.VISIBLE : View.GONE);
        }
        boolean allowEdit = allowEdit();
        if (addButton != null) {
            addButton.setVisibility(allowEdit ? View.VISIBLE : View.GONE);
        }
        if (editButton != null) {
            editButton.setVisibility(allowEdit ? View.VISIBLE : View.GONE);
        }
        if (menuButton != null)
        {
            boolean showMenu = getShowMenu();
            menuButton.setVisibility(showMenu ? View.VISIBLE : View.GONE);
            if (addButton != null) {    // shown as part of menu
                addButton.setVisibility(showMenu || !allowEdit() ? View.GONE : View.VISIBLE);
            }
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

    public void setShowBack(boolean showBack) {
        setBoolArg(ARG_SHOW_BACK, showBack);
    }
    public boolean getShowBack() {
        return getBoolArg(ARG_SHOW_BACK, DEF_SHOW_BACK);
    }

    public void setShowMenu(boolean showMenu) {
        setBoolArg(ARG_SHOW_MENU, showMenu);
    }
    public boolean getShowMenu() {
        return getBoolArg(ARG_SHOW_MENU, DEF_SHOW_MENU);
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
        void onBackClicked();
        void onAddClicked(@Nullable String colorsID);
        void onEditClicked(@Nullable String colorsID);
        void onItemSelected(ColorValuesItem item);
    }

    protected FragmentListener listener = null;
    public void setFragmentListener(FragmentListener l) {
        listener = l;
    }
}