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
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.forrestguice.suntimes.naturalhour.R;

public class ColorValuesSheetFragment extends Fragment
{
    public ColorValuesSheetFragment() {
        setHasOptionsMenu(false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState)
    {
        View content = inflater.inflate(R.layout.fragment_colorsheet, container, false);
        FragmentManager fragments = getChildFragmentManager();
        listDialog = (ColorValuesSelectFragment) fragments.findFragmentById(R.id.colorsCollectionFragment);
        editDialog = (ColorValuesEditFragment1) fragments.findFragmentById(R.id.colorsFragment);
        if (savedState != null) {
            onRestoreInstanceState(savedState);
        }
        return content;
    }

    protected ColorValuesSelectFragment listDialog;
    protected ColorValuesEditFragment1 editDialog;

    protected void onRestoreInstanceState(@NonNull Bundle savedState) { /* EMPTY */ }

    public void updateViews(ColorValues values)
    {
        final Context context = getActivity();
        if (listDialog != null && editDialog != null)
        {
            listDialog.setColorCollection(colorCollection);
            if (listDialog.getView() != null) {
                listDialog.getView().setVisibility(View.VISIBLE);
                requestPeekHeight(listDialog.getView().getHeight());
            }
            listDialog.setFragmentListener(new ColorValuesSelectFragment.FragmentListener()
            {
                @Override
                public void onEditClicked(String colorsID) {
                    editDialog.setColorValues(colorCollection.getColors(context, colorsID));
                    if (listDialog.getView() != null) {
                        listDialog.getView().setVisibility(View.GONE);
                    }
                    if (editDialog.getView() != null) {
                        editDialog.getView().setVisibility(View.VISIBLE);
                    }
                    requestExpandSheet();
                }

                @Override
                public void onItemSelected(ColorValuesSelectFragment.ColorValuesItem item)
                {
                    colorCollection.setSelectedColorsID(context, item.colorsID);
                    ColorValues selectedColors = colorCollection.getColors(context, item.colorsID);
                    onSelectColors(selectedColors);
                }
            });

            if (editDialog.getView() != null) {
                editDialog.getView().setVisibility(View.GONE);
            }
            editDialog.setColorValues(values);
            editDialog.setFragmentListener(new ColorValuesEditFragment.FragmentListener()
            {
                @Override
                public void onCancelClicked() {
                    if (editDialog.getView() != null) {
                        editDialog.getView().setVisibility(View.GONE);
                    }
                    if (listDialog.getView() != null) {
                        listDialog.getView().setVisibility(View.VISIBLE);
                    }
                    colorCollection.clearCache();    // cached instance may have been modified
                    onSelectColors(colorCollection.getSelectedColors(context));
                }

                @Override
                public void onSaveClicked(String colorsID, ColorValues values)
                {
                    colorCollection.clearCache();
                    colorCollection.setColors(context, colorsID, values);
                    colorCollection.setSelectedColorsID(context, colorsID);
                    if (listener != null) {
                        listener.requestHideSheet();
                    }
                    Toast.makeText(context, getString(R.string.msg_colors_saved, colorsID), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDeleteClicked(String colorsID) {
                    colorCollection.removeColors(context, colorsID);
                    Toast.makeText(context, getString(R.string.msg_colors_deleted, colorsID), Toast.LENGTH_SHORT).show();
                    requestHideSheet();
                }
            });
        }
        requestExpandSheet();
    }

    protected ColorValuesCollection colorCollection = null;
    public void setColorCollection(ColorValuesCollection collection) {
        colorCollection = collection;
    }

    protected void requestPeekHeight(int height) {
        if (listener != null) {
            listener.requestPeekHeight(height);
        }
    }
    protected void requestHideSheet() {
        if (listener != null) {
            listener.requestHideSheet();
        }
    }
    protected void requestExpandSheet() {
        if (listener != null) {
            listener.requestExpandSheet();
        }
    }
    protected void onSelectColors(ColorValues values) {
        if (listener != null) {
            listener.onColorValuesSelected(values);
        }
    }

    /**
     * FragmentListener
     */
    public interface FragmentListener
    {
        void requestPeekHeight(int height);
        void requestHideSheet();
        void requestExpandSheet();
        void onColorValuesSelected(ColorValues values);
    }

    protected FragmentListener listener = null;
    public void setFragmentListener(FragmentListener l) {
        listener = l;
    }
}