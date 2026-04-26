// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2023 Forrest Guice
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

package com.forrestguice.suntimes.naturalhour;

import android.content.Context;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.forrestguice.suntimes.addon.AppThemeInfo;
import com.forrestguice.suntimes.addon.SuntimesInfo;

public class AppThemes extends AppThemeInfo.AppThemeInfoFactory
{
    private static final AppThemeInfo info_darkTheme = new DarkThemeInfo();
    private static final AppThemeInfo info_lightTheme = new LightThemeInfo();
    private static final AppThemeInfo info_systemTheme = new SystemThemeInfo();
    private static final AppThemeInfo info_systemTheme_contrast = new ContrastSystemThemeInfo();
    private static final AppThemeInfo info_darkTheme_contrast = new ContrastDarkThemeInfo();
    private static final AppThemeInfo info_lightTheme_contrast = new ContrastLightThemeInfo();
    private static final AppThemeInfo info_systemTheme_monet = new MonetSystemThemeInfo();
    private static final AppThemeInfo info_darkTheme_monet = new MonetDarkThemeInfo();
    private static final AppThemeInfo info_lightTheme_monet = new MonetLightThemeInfo();

    private static final AppThemeInfo info_defaultTheme;
    static {
        if (Build.VERSION.SDK_INT >= 31) {
            info_defaultTheme = info_systemTheme_monet;
        } else {
            info_defaultTheme = info_systemTheme;
        }
    }
    @Override
    public int getDefaultThemeID() {
        if (Build.VERSION.SDK_INT >= 31) {
            return R.style.NaturalHourAppTheme_MonetSystem;
        } else return R.style.NaturalHourAppTheme_System;
    }

    @Override
    public AppThemeInfo loadThemeInfo(@Nullable String extendedThemeName)
    {
        if (extendedThemeName == null) {
            return info_defaultTheme;

        } else if (extendedThemeName.startsWith(SuntimesInfo.THEME_LIGHT)) {
            return info_lightTheme;
        } else if (extendedThemeName.startsWith(SuntimesInfo.THEME_DARK)) {
            return info_darkTheme;
        } else if (extendedThemeName.startsWith(SuntimesInfo.THEME_SYSTEM)) {
            return info_systemTheme;

        } else if (extendedThemeName.startsWith(SuntimesInfo.THEME_CONTRAST_LIGHT)) {
            return info_lightTheme_contrast;
        } else if (extendedThemeName.startsWith(SuntimesInfo.THEME_CONTRAST_DARK)) {
            return info_darkTheme_contrast;
        } else if (extendedThemeName.startsWith(SuntimesInfo.THEME_CONTRAST_SYSTEM)) {
            return info_systemTheme_contrast;

        } else if (extendedThemeName.startsWith(SuntimesInfo.THEME_MONET_LIGHT)) {
            return info_lightTheme_monet;
        } else if (extendedThemeName.startsWith(SuntimesInfo.THEME_MONET_DARK)) {
            return info_darkTheme_monet;
        } else if (extendedThemeName.startsWith(SuntimesInfo.THEME_MONET_SYSTEM)) {
            return info_systemTheme_monet;

        } else {
            return info_defaultTheme;
        }
    }

    @Override
    public void setDefaultNightMode(int mode) {
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    /* SystemThemeInfo */
    public static class SystemThemeInfo extends AppThemeInfo
    {
        @Override
        public String getThemeName() {
            return SuntimesInfo.THEME_SYSTEM;
        }
        @Override
        public int getDefaultNightMode() {
            return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
        @Override
        public int getStyleId(Context context, TextSize size) {
            switch (size) {
                case SMALL: return R.style.NaturalHourAppTheme_System_Small;
                case LARGE: return R.style.NaturalHourAppTheme_System_Large;
                case XLARGE: return R.style.NaturalHourAppTheme_System_XLarge;
                case NORMAL: default: return R.style.NaturalHourAppTheme_System;
            }
        }
    }

    /* LightThemeInfo */
    public static class LightThemeInfo extends AppThemeInfo
    {
        @Override
        public String getThemeName() {
            return SuntimesInfo.THEME_LIGHT;
        }
        @Override
        public int getDefaultNightMode() {
            return AppCompatDelegate.MODE_NIGHT_NO;
        }
        @Override
        public int getStyleId(Context context, TextSize size) {
            switch (size) {
                case SMALL: return R.style.NaturalHourAppTheme_Light_Small;
                case LARGE: return R.style.NaturalHourAppTheme_Light_Large;
                case XLARGE: return R.style.NaturalHourAppTheme_Light_XLarge;
                case NORMAL: default: return R.style.NaturalHourAppTheme_Light;
            }
        }
    }

    /* DarkThemeInfo */
    public static class DarkThemeInfo extends AppThemeInfo
    {
        @Override
        public String getThemeName() {
            return SuntimesInfo.THEME_DARK;
        }
        @Override
        public int getDefaultNightMode() {
            return AppCompatDelegate.MODE_NIGHT_YES;
        }
        @Override
        public int getStyleId(Context context, TextSize size) {
            switch (size) {
                case SMALL: return R.style.NaturalHourAppTheme_Dark_Small;
                case LARGE: return R.style.NaturalHourAppTheme_Dark_Large;
                case XLARGE: return R.style.NaturalHourAppTheme_Dark_XLarge;
                case NORMAL: default: return R.style.NaturalHourAppTheme_Dark;
            }
        }
    }

    /* ContrastSystemThemeInfo */
    public static class ContrastSystemThemeInfo extends AppThemeInfo
    {
        @Override
        public String getThemeName() {
            return SuntimesInfo.THEME_CONTRAST_SYSTEM;
        }
        @Override
        public int getDefaultNightMode() {
            return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
        @Override
        public int getStyleId(Context context, TextSize size) {
            switch (size) {
                case SMALL: return R.style.NaturalHourAppTheme_System1_Small;
                case LARGE: return R.style.NaturalHourAppTheme_System1_Large;
                case XLARGE: return R.style.NaturalHourAppTheme_System1_XLarge;
                case NORMAL: default: return R.style.NaturalHourAppTheme_System1;
            }
        }
    }

    /* ContrastLightThemeInfo */
    public static class ContrastLightThemeInfo extends AppThemeInfo
    {
        @Override
        public String getThemeName() {
            return SuntimesInfo.THEME_CONTRAST_LIGHT;
        }
        @Override
        public int getDefaultNightMode() {
            return AppCompatDelegate.MODE_NIGHT_NO;
        }
        @Override
        public int getStyleId(Context context, TextSize size) {
            switch (size) {
                case SMALL: return R.style.NaturalHourAppTheme_Light1_Small;
                case LARGE: return R.style.NaturalHourAppTheme_Light1_Large;
                case XLARGE: return R.style.NaturalHourAppTheme_Light1_XLarge;
                case NORMAL: default: return R.style.NaturalHourAppTheme_Light1;
            }
        }
    }

    /* ConstrastDarkThemeInfo */
    public static class ContrastDarkThemeInfo extends AppThemeInfo
    {
        @Override
        public String getThemeName() {
            return SuntimesInfo.THEME_CONTRAST_DARK;
        }
        @Override
        public int getDefaultNightMode() {
            return AppCompatDelegate.MODE_NIGHT_YES;
        }
        @Override
        public int getStyleId(Context context, TextSize size) {
            switch (size) {
                case SMALL: return R.style.NaturalHourAppTheme_Dark1_Small;
                case LARGE: return R.style.NaturalHourAppTheme_Dark1_Large;
                case XLARGE: return R.style.NaturalHourAppTheme_Dark1_XLarge;
                case NORMAL: default: return R.style.NaturalHourAppTheme_Dark1;
            }
        }
    }

    /* MonetSystemThemeInfo */
    public static class MonetSystemThemeInfo extends AppThemeInfo
    {
        @Override
        public String getThemeName() {
            return SuntimesInfo.THEME_MONET_SYSTEM;
        }
        @Override
        public int getDefaultNightMode() {
            return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
        @Override
        public int getStyleId(Context context, TextSize size) {
            switch (size) {
                case SMALL: return R.style.NaturalHourAppTheme_MonetSystem_Small;
                case LARGE: return R.style.NaturalHourAppTheme_MonetSystem_Large;
                case XLARGE: return R.style.NaturalHourAppTheme_MonetSystem_XLarge;
                case NORMAL: default: return R.style.NaturalHourAppTheme_MonetSystem;
            }
        }
    }

    /* MonetLightThemeInfo */
    public static class MonetLightThemeInfo extends AppThemeInfo
    {
        @Override
        public String getThemeName() {
            return SuntimesInfo.THEME_MONET_LIGHT;
        }
        @Override
        public int getDefaultNightMode() {
            return AppCompatDelegate.MODE_NIGHT_NO;
        }
        @Override
        public int getStyleId(Context context, TextSize size) {
            switch (size) {
                case SMALL: return R.style.NaturalHourAppTheme_MonetLight_Small;
                case LARGE: return R.style.NaturalHourAppTheme_MonetLight_Large;
                case XLARGE: return R.style.NaturalHourAppTheme_MonetLight_XLarge;
                case NORMAL: default: return R.style.NaturalHourAppTheme_MonetLight;
            }
        }
    }

    /* MonetDarkThemeInfo */
    public static class MonetDarkThemeInfo extends AppThemeInfo
    {
        @Override
        public String getThemeName() {
            return SuntimesInfo.THEME_MONET_DARK;
        }
        @Override
        public int getDefaultNightMode() {
            return AppCompatDelegate.MODE_NIGHT_YES;
        }
        @Override
        public int getStyleId(Context context, TextSize size) {
            switch (size) {
                case SMALL: return R.style.NaturalHourAppTheme_MonetDark_Small;
                case LARGE: return R.style.NaturalHourAppTheme_MonetDark_Large;
                case XLARGE: return R.style.NaturalHourAppTheme_MonetDark_XLarge;
                case NORMAL: default: return R.style.NaturalHourAppTheme_MonetDark;
            }
        }
    }

}
