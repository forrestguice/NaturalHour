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
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.naturalhour.BuildConfig;
import com.forrestguice.suntimes.naturalhour.R;

public class AboutDialog extends BottomSheetDialogFragment
{
    public static final String KEY_DIALOGTHEME = "themeResID";
    public static final String KEY_APPVERSION = "paramAppVersion";
    public static final String KEY_PROVIDERVERSION = "paramProviderVersion";
    public static final String KEY_PROVIDER_PERMISSIONDENIED = "paramProviderDenied";

    private int themeResID = R.style.NaturalHourAppTheme_Dark;
    public void setTheme(int themeResID) {
        this.themeResID = themeResID;
    }

    private String appVersion = null;
    private Integer providerVersion = null;
    private boolean providerPermissionsDenied = false;
    public void setVersion(@NonNull SuntimesInfo info)
    {
        this.appVersion = info.appName;
        this.providerVersion = info.providerCode;
        this.providerPermissionsDenied = !info.hasPermission;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedState)
    {
        restoreInstanceState(savedState);

        ContextThemeWrapper contextWrapper = new ContextThemeWrapper(getActivity(), themeResID);    // hack: contextWrapper required because base theme is not properly applied
        View dialogContent = inflater.cloneInContext(contextWrapper).inflate(R.layout.dialog_about, parent, false);

        TextView version = (TextView)dialogContent.findViewById(R.id.txt_about_version);
        version.setMovementMethod(LinkMovementMethod.getInstance());
        version.setText(DisplayStrings.fromHtml(htmlVersionString()));

        TextView providerView = (TextView) dialogContent.findViewById(R.id.txt_about_provider);
        providerView.setText(DisplayStrings.fromHtml(providerVersionString()));

        TextView support = (TextView)dialogContent.findViewById(R.id.txt_about_support);
        support.setMovementMethod(LinkMovementMethod.getInstance());
        support.setText(DisplayStrings.fromHtml(getString(R.string.app_support_url)));

        TextView legalView1 = (TextView) dialogContent.findViewById(R.id.txt_about_legal1);
        legalView1.setMovementMethod(LinkMovementMethod.getInstance());
        legalView1.setText(DisplayStrings.fromHtml(getString(R.string.app_legal1)));

        TextView url = (TextView)dialogContent.findViewById(R.id.txt_about_url);
        url.setMovementMethod(LinkMovementMethod.getInstance());
        url.setText(DisplayStrings.fromHtml(getString(R.string.app_url)));

        return dialogContent;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        DialogInterface dialog = getDialog();
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

    protected void restoreInstanceState( Bundle savedState )
    {
        if (savedState != null)
        {
            if (savedState.containsKey(KEY_DIALOGTHEME)) {
                themeResID = savedState.getInt(KEY_DIALOGTHEME);
            }
            if (savedState.containsKey(KEY_PROVIDER_PERMISSIONDENIED)) {
                providerPermissionsDenied = savedState.getBoolean(KEY_PROVIDER_PERMISSIONDENIED);
            }
            if (savedState.containsKey(KEY_APPVERSION)) {
                appVersion = savedState.getString(KEY_APPVERSION);
            }
            if (savedState.containsKey(KEY_PROVIDERVERSION)) {
                providerVersion = savedState.getInt(KEY_PROVIDERVERSION);
            }
        }
    }

    @Override
    public void onSaveInstanceState( @NonNull Bundle out )
    {
        out.putInt(KEY_DIALOGTHEME, themeResID);
        out.putBoolean(KEY_PROVIDER_PERMISSIONDENIED, providerPermissionsDenied);
        if (appVersion != null) {
            out.putString(KEY_APPVERSION, appVersion);
        }
        if (providerVersion != null) {
            out.putInt(KEY_PROVIDERVERSION, providerVersion);
        }
        super.onSaveInstanceState(out);
    }



    public String htmlVersionString()
    {
        String buildString = anchor(getString(R.string.app_commit_url) + BuildConfig.GIT_HASH, BuildConfig.GIT_HASH);
        String versionString = anchor(getString(R.string.app_changelog_url), BuildConfig.VERSION_NAME) + " " + smallText("(" + buildString + ")");
        if (BuildConfig.DEBUG) {
            versionString += " " + smallText("[" + BuildConfig.BUILD_TYPE + "]");
        }
        return getString(R.string.app_version, versionString);
    }

    protected String providerVersionString()
    {
        String denied = getString(R.string.app_provider_version_denied);
        String missingVersion = getString(R.string.app_provider_version_missing);
        String versionString = (appVersion == null) ? missingVersion
                : (appVersion + " (" + ((providerVersion != null) ? providerVersion
                : (providerPermissionsDenied ? denied : missingVersion)) + ")");
        return getString(R.string.app_provider_version, versionString);
    }

    public static String anchor(String url, String text) {
        return "<a href=\"" + url + "\">" + text + "</a>";
    }

    protected static String smallText(String text)
    {
        return "<small>" + text + "</small>";
    }


}
