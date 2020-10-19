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

package com.forrestguice.suntimes.naturalhour.data;

import com.forrestguice.suntimes.widget.WidgetListHelper;

public interface NaturalHourProviderContract
{
    String AUTHORITY = "suntimes.naturalhour.provider";
    String READ_PERMISSION = "suntimes.permission.READ_CALCULATOR";
    String VERSION_NAME = "v0.0.0";
    int VERSION_CODE = 0;

    /*
     * CONFIG
     */
    String COLUMN_CONFIG_PROVIDER_VERSION = "provider_version";             // String (provider version string)
    String COLUMN_CONFIG_PROVIDER_VERSION_CODE = "provider_version_code";   // int (provider version code)
    String COLUMN_CONFIG_APP_VERSION = "app_version";                       // String (app version string)
    String COLUMN_CONFIG_APP_VERSION_CODE = "app_version_code";             // int (app version code)

    String QUERY_CONFIG = "config";
    String[] QUERY_CONFIG_PROJECTION = new String[] {
            COLUMN_CONFIG_PROVIDER_VERSION, COLUMN_CONFIG_PROVIDER_VERSION_CODE,
            COLUMN_CONFIG_APP_VERSION, COLUMN_CONFIG_APP_VERSION_CODE
    };

    /*
     * WIDGET
     */
    String COLUMN_WIDGET_PACKAGENAME = WidgetListHelper.COLUMN_WIDGET_PACKAGENAME;
    String COLUMN_WIDGET_APPWIDGETID = WidgetListHelper.COLUMN_WIDGET_APPWIDGETID;
    String COLUMN_WIDGET_CLASS = WidgetListHelper.COLUMN_WIDGET_CLASS;
    String COLUMN_WIDGET_CONFIGCLASS = WidgetListHelper.COLUMN_WIDGET_CONFIGCLASS;
    String COLUMN_WIDGET_LABEL = WidgetListHelper.COLUMN_WIDGET_LABEL;
    String COLUMN_WIDGET_SUMMARY = WidgetListHelper.COLUMN_WIDGET_SUMMARY;
    String COLUMN_WIDGET_ICON = WidgetListHelper.COLUMN_WIDGET_ICON;

    String QUERY_WIDGET = WidgetListHelper.QUERY_WIDGET;
    String[] QUERY_WIDGET_PROJECTION = WidgetListHelper.QUERY_WIDGET_PROJECTION;

}
