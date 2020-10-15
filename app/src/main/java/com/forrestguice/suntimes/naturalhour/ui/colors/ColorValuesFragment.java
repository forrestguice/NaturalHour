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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.forrestguice.suntimes.naturalhour.R;

import java.lang.reflect.Method;

public class ColorValuesFragment extends Fragment
{
    protected EditText editID;
    protected GridLayout panel;

    public ColorValuesFragment() {
        setHasOptionsMenu(true);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState)
    {
        View content = inflater.inflate(R.layout.fragment_colorvalues, container, false);
        if (savedState != null) {
            onRestoreInstanceState(savedState);
        }

        ImageButton overflow = (ImageButton) content.findViewById(R.id.overflow);
        if (overflow != null) {
            overflow.setOnClickListener(onOverflowButtonClicked);
        }

        ImageButton saveButton = (ImageButton) content.findViewById(R.id.saveButton);
        if (saveButton != null) {
            saveButton.setOnClickListener(onSaveButtonClicked);
        }

        panel = (GridLayout) content.findViewById(R.id.colorPanel);
        editID = (EditText) content.findViewById(R.id.editTextID);


        updateViews();
        return content;
    }

    private View.OnClickListener onSaveButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onSaveColorValues();
        }
    };

    protected void onSaveColorValues()
    {
        String colorsID = editID.getText().toString();
        colorValues.setID(colorsID);

        if (listener != null) {
            listener.onSaveClicked(colorsID, colorValues);
        }
    }

    private View.OnClickListener onOverflowButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showOverflowMenu(getActivity(), v);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle out)
    {
        super.onSaveInstanceState(out);
        out.putParcelable("colorValues", colorValues);
    }
    protected void onRestoreInstanceState(@NonNull Bundle savedState) {
        colorValues = savedState.getParcelable("colorValues");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, android.content.Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode >= 0) {
            onPickColorResult(requestCode, resultCode, data);
        }
    }

    protected TextView[] colorEdits;
    protected void updateViews()
    {
        if (editID != null && colorValues != null)
        {
            String colorsID = colorValues.getID();
            editID.setText(colorsID != null ? colorsID : "");
        }

        if (panel != null && colorValues != null)
        {
            String[] keys = colorValues.getColorKeys();
            colorEdits = new TextView[keys.length];

            panel.removeAllViews();
            for (int i=0; i<keys.length; i++)
            {
                colorEdits[i] = new TextView(getActivity());
                colorEdits[i].setText(colorValues.getLabel(keys[i]));
                colorEdits[i].setTextColor(colorValues.getColor(keys[i]));
                colorEdits[i].setOnClickListener(onColorEditClick(keys[i]));
                panel.addView(colorEdits[i],
                        new GridLayout.LayoutParams(GridLayout.spec(i/2, GridLayout.CENTER),
                                GridLayout.spec(i%2, GridLayout.CENTER))
                );
            }

        } else if (panel != null) {
            TextView emptyMsg = new TextView(getActivity());
            emptyMsg.setText(" ");
            panel.removeAllViews();
            panel.addView(emptyMsg);
        }
    }

    public View.OnClickListener onColorEditClick(final String colorKey) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickColor(colorKey);
            }
        };
    }

    protected ColorValues colorValues = null;
    public void setColorValues(ColorValues v) {
        colorValues = v;
        updateViews();
    }

    protected void setColor(String key, int color) {
        colorValues.setColor(key, color);
        updateViews();
    }

    public void pickColor(String key)
    {
        int requestCode = colorValues.colorKeyIndex(key);
        if (requestCode >= 0) {
            startActivityForResult(pickColorIntent(key, requestCode), requestCode);
        }
    }

    protected Intent pickColorIntent(String key, int requestCode) {
        return null;
    }

    protected void onPickColorResult(int requestCode, int resultCode, Intent data)
    {
        String[] keys = colorValues.getColorKeys();
        if (resultCode == Activity.RESULT_OK && requestCode >= 0 && requestCode <keys.length) {
            onPickColorResult(keys[requestCode],data);
        }
    }

    protected void onPickColorResult(String key, Intent data) { /* EMPTY */ }

    protected void shareColors(Context context)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, colorValues.toString());
        startActivity(Intent.createChooser(intent, null));
    }

    public void showOverflowMenu(Context context, View v)
    {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_colorfragment, popup.getMenu());
        forceActionBarIcons(popup.getMenu());
        popup.setOnMenuItemClickListener(onOverflowMenuItemSelected);
        popup.show();
    }

    private PopupMenu.OnMenuItemClickListener onOverflowMenuItemSelected = new PopupMenu.OnMenuItemClickListener()
    {
        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.action_colors_share:
                    shareColors(getActivity());
                    return true;
            }
            return false;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_colors_share:
                shareColors(getActivity());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * from http://stackoverflow.com/questions/18374183/how-to-show-icons-in-overflow-menu-in-actionbar
     */
    private static void forceActionBarIcons(Menu menu)
    {
        if (menu != null)
        {
            if (menu.getClass().getSimpleName().equals("MenuBuilder"))
            {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);

                } catch (Exception e) {
                    Log.e("ColorValuesFragment", "failed to set show overflow icons", e);
                }
            }
        }
    }


    /**
     * FragmentListener
     */
    public interface FragmentListener
    {
        void onSaveClicked(String colorsID, ColorValues values);
    }

    protected FragmentListener listener = null;
    public void setFragmentListener(FragmentListener l) {
        listener = l;
    }

}
