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
    String COLUMN_WIDGET_PACKAGENAME = "packagename";
    String COLUMN_WIDGET_APPWIDGETID = "appwidgetid";
    String COLUMN_WIDGET_CLASS = "widgetclass";
    String COLUMN_WIDGET_CONFIGCLASS = "configclass";
    String COLUMN_WIDGET_LABEL = "label";
    String COLUMN_WIDGET_SUMMARY = "summary";
    String COLUMN_WIDGET_ICON = "icon";

    String QUERY_WIDGET = "widgets";
    String[] QUERY_WIDGET_PROJECTION = new String[] {
            COLUMN_WIDGET_APPWIDGETID, COLUMN_WIDGET_CLASS, COLUMN_WIDGET_CONFIGCLASS, COLUMN_WIDGET_PACKAGENAME,
            COLUMN_WIDGET_LABEL, COLUMN_WIDGET_SUMMARY, COLUMN_WIDGET_ICON
    };

}
