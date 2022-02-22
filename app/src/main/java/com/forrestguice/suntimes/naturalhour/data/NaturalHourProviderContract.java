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

import com.forrestguice.suntimes.alarm.AlarmEventContract;
import com.forrestguice.suntimes.widget.WidgetListHelper;

/**
 * 0: initial version (QUERY_CONFIG, COLUMN_CONFIG_* QUERY_WIDGET, COLUMN_WIDGET_*)
 * 1: adds alarms; QUERY_ALARM_*; COLUMN_ALARM_*; COLUMN_CONFIG_PROVIDER
 */
public interface NaturalHourProviderContract
{
    String AUTHORITY = "suntimes.naturalhour.provider";
    String READ_PERMISSION = "suntimes.permission.READ_CALCULATOR";
    String VERSION_NAME = "v0.1.0";
    int VERSION_CODE = 1;

    /*
     * CONFIG
     */
    String COLUMN_CONFIG_PROVIDER = "provider";                             // String (provider reference)
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

    /*
     * ALARMS
     */
    String COLUMN_EVENT_NAME = AlarmEventContract.COLUMN_EVENT_NAME;              // String (alarm/event ID)
    String COLUMN_EVENT_TITLE = AlarmEventContract.COLUMN_EVENT_TITLE;            // String (display string)
    String COLUMN_EVENT_SUMMARY = AlarmEventContract.COLUMN_EVENT_SUMMARY;        // String (extended display string)
    String COLUMN_EVENT_PHRASE = AlarmEventContract.COLUMN_EVENT_PHRASE;                         // String (noun / natural language phrase)
    String COLUMN_EVENT_PHRASE_GENDER = AlarmEventContract.COLUMN_EVENT_PHRASE_GENDER;           // String (noun gender; SelectFormat param)
    String COLUMN_EVENT_PHRASE_QUANTITY = AlarmEventContract.COLUMN_EVENT_PHRASE_QUANTITY;       // int (noun quantity; SelectFormat param)
    String COLUMN_EVENT_TIMEMILLIS = AlarmEventContract.COLUMN_EVENT_TIMEMILLIS;  // long (timestamp millis)

    String QUERY_EVENT_INFO = AlarmEventContract.QUERY_EVENT_INFO;
    String[] QUERY_EVENT_INFO_PROJECTION = AlarmEventContract.QUERY_EVENT_INFO_PROJECTION;

    String QUERY_EVENT_CALC = AlarmEventContract.QUERY_EVENT_CALC;
    String[] QUERY_EVENT_CALC_PROJECTION = AlarmEventContract.QUERY_EVENT_CALC_PROJECTION;

    String EXTRA_ALARM_NOW = AlarmEventContract.EXTRA_ALARM_NOW;                  // long (millis)
    String EXTRA_ALARM_REPEAT = AlarmEventContract.EXTRA_ALARM_REPEAT;            // boolean
    String EXTRA_ALARM_REPEAT_DAYS = AlarmEventContract.EXTRA_ALARM_REPEAT_DAYS;  // boolean[] .. [m,t,w,t,f,s,s]
    String EXTRA_ALARM_OFFSET = AlarmEventContract.EXTRA_ALARM_OFFSET;            // long (millis)
    String EXTRA_ALARM_EVENT = AlarmEventContract.EXTRA_ALARM_EVENT;              // eventID

    String EXTRA_LOCATION_LABEL = AlarmEventContract.EXTRA_LOCATION_LABEL;        // String
    String EXTRA_LOCATION_LAT = AlarmEventContract.EXTRA_LOCATION_LAT;            // double (DD)
    String EXTRA_LOCATION_LON = AlarmEventContract.EXTRA_LOCATION_LON;            // double (DD)
    String EXTRA_LOCATION_ALT = AlarmEventContract.EXTRA_LOCATION_ALT;            // double (meters)

}
