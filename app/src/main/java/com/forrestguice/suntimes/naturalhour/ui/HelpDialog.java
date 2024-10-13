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

package com.forrestguice.suntimes.naturalhour.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.appcompat.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.forrestguice.suntimes.naturalhour.R;

public class HelpDialog extends BottomSheetDialogFragment
{
    public static final String KEY_HELPTEXT = "helpText";
    public static final String KEY_DIALOGTHEME = "themeResID";

    private int themeResID = R.style.NaturalHourAppTheme_Dark;
    public void setTheme(int themeResID) {
        this.themeResID = themeResID;
    }

    private TextView txtView;
    private CharSequence rawContent = "";
    public CharSequence getContent() {
        return rawContent;
    }
    public void setContent( String content ) {
        setContent((CharSequence)DisplayStrings.fromHtml(content));
    }

    public void setContent( CharSequence content )
    {
        rawContent = content;
        if (txtView != null) {
            txtView.setText(content);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedState)
    {
        themeResID = ((savedState != null) ? savedState.getInt(KEY_DIALOGTHEME) : themeResID);
        ContextThemeWrapper contextWrapper = new ContextThemeWrapper(getActivity(), themeResID);    // hack: contextWrapper required because base theme is not properly applied
        View dialogContent = inflater.cloneInContext(contextWrapper).inflate(R.layout.dialog_help, parent, false);
        txtView = (TextView) dialogContent.findViewById(R.id.help_content);
        if (savedState != null) {
            rawContent = savedState.getCharSequence(KEY_HELPTEXT);
        }
        return dialogContent;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        txtView.setText(getContent());
        expandSheet(getDialog());
    }

    private static void expandSheet(DialogInterface dialog)
    {
        if (dialog != null) {
            BottomSheetDialog bottomSheet = (BottomSheetDialog) dialog;
            FrameLayout layout = (FrameLayout) bottomSheet.findViewById(android.support.design.R.id.design_bottom_sheet);  // for AndroidX, resource is renamed to com.google.android.material.R.id.design_bottom_sheet
            if (layout != null) {
                BottomSheetBehavior behavior = BottomSheetBehavior.from(layout);
                behavior.setHideable(false);
                behavior.setSkipCollapsed(false);
                behavior.setPeekHeight(200);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    @Override
    public void onSaveInstanceState( @NonNull Bundle out ) {
        out.putCharSequence(KEY_HELPTEXT, rawContent);
        out.putInt(KEY_DIALOGTHEME, themeResID);
        super.onSaveInstanceState(out);
    }
}
