// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2020-2025 Forrest Guice
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

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.forrestguice.suntimes.naturalhour.AppSettings;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.appcompat.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.forrestguice.suntimes.naturalhour.R;

public class HelpDialog extends BottomSheetDialogFragment
{
    public static final String KEY_HELPTEXT = "helpText";
    public static final String KEY_DIALOGTHEME = "themeResID";
    public static final String KEY_NEUTRALTEXT = "neutralText";
    public static final String KEY_NEUTRALTAG = "neutralTag";

    private int themeResID = R.style.NaturalHourAppTheme_Dark;
    public void setTheme(int themeResID) {
        this.themeResID = themeResID;
    }

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

        initViews(dialogContent);
        if (savedState != null) {
            rawContent = savedState.getCharSequence(KEY_HELPTEXT);
            neutralButtonMsg = savedState.getString(KEY_NEUTRALTEXT);
            listenerTag = savedState.getString(KEY_NEUTRALTAG);
        }
        return dialogContent;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateViews();
        expandSheet(getDialog());
    }

    private View buttonFrame;
    private TextView txtView;
    private Button neutralButton;
    public void initViews(View dialogView)
    {
        txtView = (TextView) dialogView.findViewById(R.id.txt_help_content);
        buttonFrame = dialogView.findViewById(R.id.dialog_buttons);
        neutralButton = (Button) dialogView.findViewById(R.id.dialog_button_neutral);
        if (neutralButton != null) {
            if (AppSettings.isTelevision(getActivity())) {
                neutralButton.setFocusableInTouchMode(true);
            }
        }
    }

    private static void expandSheet(DialogInterface dialog)
    {
        if (dialog != null) {
            BottomSheetDialog bottomSheet = (BottomSheetDialog) dialog;
            FrameLayout layout = (FrameLayout) bottomSheet.findViewById(com.google.android.material.R.id.design_bottom_sheet);  // android.support.design.R.id.design_bottom_sheet
            if (layout != null) {
                BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(layout);
                behavior.setHideable(false);
                behavior.setSkipCollapsed(false);
                behavior.setPeekHeight(200);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    public void updateViews()
    {
        txtView.setText(getContent());
        if (buttonFrame != null) {
            buttonFrame.setVisibility(neutralButtonMsg != null ? View.VISIBLE : View.GONE);
        }
        if (neutralButton != null && neutralButtonMsg != null) {
            neutralButton.setText(neutralButtonMsg);
            neutralButton.setOnClickListener(onNeutralButtonClick);
        }
    }

    @Override
    public void onSaveInstanceState( @NonNull Bundle out ) {
        out.putCharSequence(KEY_HELPTEXT, rawContent);
        out.putInt(KEY_DIALOGTHEME, themeResID);
        out.putString(KEY_NEUTRALTEXT, neutralButtonMsg);
        out.putString(KEY_NEUTRALTAG, listenerTag);
        super.onSaveInstanceState(out);
    }

    /**
     * Show/hide the neutral button.
     * @param msg neutral button text (null hides button, default is null)
     */
    public void setShowNeutralButton( String msg ) {
        neutralButtonMsg = msg;
    }
    private String neutralButtonMsg = null;

    private View.OnClickListener onNeutralButtonClick = null;
    public void setNeutralButtonListener( View.OnClickListener listener, String tag )
    {
        onNeutralButtonClick = listener;
        listenerTag = tag;
    }

    private String listenerTag = "";
    public String getListenerTag() {
        return listenerTag;
    }

    /**
     * getOnlineHelp
     */
    public static View.OnClickListener getOnlineHelpClickListener(final Context context, final int helpPathID)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                context.startActivity(getOnlineHelpIntent(context, context.getString(helpPathID)));
            }
        };
    }
    public static Intent getOnlineHelpIntent(Context context, String helpPath) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.help_url) + Uri.parse(helpPath)));
    }
}
