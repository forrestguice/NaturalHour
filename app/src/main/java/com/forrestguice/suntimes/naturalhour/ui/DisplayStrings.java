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

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.naturalhour.R;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DisplayStrings
{
    public static String numeral(@NonNull Context context, int hour, String[] numerals)
    {
        StringBuilder result = new StringBuilder();
        String ten = numerals[10];

        int tens = hour / 10;
        int ones = hour % 10;
        for (int i=0; i<tens; i++) {
            result.append(ten);
        }
        result.append(numerals[ones]);
        return result.toString();
    }

    public static String arabicNumeral(@NonNull Context context, int hour) {
        return Integer.toString(hour);
    }

    public static String localizedNumeral(@NonNull Context context, @NonNull Locale locale, int hour) {
        return String.format( locale, "%d", hour);
    }

    public static String romanNumeral(@NonNull Context context, int hour) {
        return numeral(context, hour, context.getResources().getStringArray(R.array.roman_numeral));
    }

    public static String greekNumeral(@NonNull Context context, int hour, boolean lowerCase) {
        return numeral(context, hour, context.getResources().getStringArray((lowerCase ? R.array.greek_lower_numeral : R.array.greek_upper_numeral)));
    }


    public static String atticNumeral(@NonNull Context context, int hour) {
        return numeral(context, hour, context.getResources().getStringArray(R.array.attic_numeral));
    }

    public static String armenianNumeral(@NonNull Context context, int hour)
    {
        StringBuilder result = new StringBuilder();
        String[] numerals = context.getResources().getStringArray(R.array.armenian_numeral);
        String ten = numerals[10];

        int tens = hour / 10;
        int ones = hour % 10;

        if (tens == 2) {
            result.append(numerals[11]);
        } else if (tens == 1) {
            result.append(ten);
        } else {
            for (int i=0; i<tens; i++) {
                result.append(ten);
            }
        }

        result.append(numerals[ones]);
        return result.toString();
    }

    public static String etruscanNumeral(@NonNull Context context, int hour) {
        return numeral(context, hour, context.getResources().getStringArray(R.array.etruscan_numeral));
    }

    public static String hebrewNumeral(@NonNull Context context, int hour)
    {
        String[] numerals = context.getResources().getStringArray(R.array.hebrew_numeral);
        return numerals[hour];
    }

    /*
     * @param context Context
     * @param num [1-4]
     * @return e.g. "Vigilia Prima"
     */
    public static CharSequence formatNightWatchLabel0(@NonNull Context context, int num)
    {
        String[] phrase = context.getResources().getStringArray(R.array.nightwatch_phrase0);
        if (num >= 1 && num <= 4) {
            return phrase[num];
        } else return "";
    }
    public static CharSequence formatNightWatchLabel1(@NonNull Context context, int num)
    {
        String[] phrase = context.getResources().getStringArray(R.array.nightwatch_phrase1);
        if (num >= 1 && num <= 3) {
            return phrase[num];
        } else return "";
    }

    public static CharSequence formatDate(@NonNull Context context, long date)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return formatDate(context, calendar);
    }

    public static CharSequence formatDate(@NonNull Context context, Calendar date)
    {
        Calendar now = Calendar.getInstance(date.getTimeZone());
        boolean isThisYear = now.get(Calendar.YEAR) == date.get(Calendar.YEAR);

        if (dateFormat_short == null || dateFormat_long == null)
        {
            Locale locale = Locale.getDefault();
            dateFormat_short = new SimpleDateFormat(context.getString( R.string.format_date ), locale);
            dateFormat_long = new SimpleDateFormat(context.getString( R.string.format_date_long), locale);
        }

        SimpleDateFormat dateFormat = isThisYear ? dateFormat_short : dateFormat_long;
        dateFormat.setTimeZone(date.getTimeZone());
        return dateFormat.format(date.getTime());
    }

    private static SimpleDateFormat dateFormat_short = null, dateFormat_long = null;

    public static CharSequence formatDateHeader(Context context, int dayDelta, CharSequence formattedDate)
    {
        if (dayDelta == 0) {
            return context.getString(R.string.format_date_today, formattedDate);
        } else if (dayDelta == -1) {
            return context.getString(R.string.format_date_yesterday, ""+Math.abs(dayDelta), formattedDate);
        } else if (dayDelta == 1) {
            return context.getString(R.string.format_date_tomorrow, ""+Math.abs(dayDelta), formattedDate);
        } else if (dayDelta > 1) {
            return context.getString(R.string.format_date_future, ""+Math.abs(dayDelta), formattedDate);
        } else {
            return context.getString(R.string.format_date_past, ""+Math.abs(dayDelta), formattedDate);
        }
    }

    public static String timeFormatLabel(@NonNull Context context, boolean is24) {
        return context.getString(is24 ? R.string.timeformat_24hr : R.string.timeformat_12hr);
    }
    public static String timeFormatTag(@NonNull Context context, boolean is24) {
        return context.getString(R.string.action_timeformat_system_format, timeFormatLabel(context, is24));
    }
    public static CharSequence formatTimeFormatLabel(Context context, String labelFormat, boolean is24) {
        String tag = DisplayStrings.timeFormatTag(context, is24);
        String label = String.format(labelFormat, tag);
        return DisplayStrings.createRelativeSpan(null, label, tag, 0.65f);
    }

    public static String timeZoneTag(@NonNull Context context, @NonNull String timezone) {
        return context.getString(R.string.action_timezone_system_format, timezone);
    }
    public static CharSequence formatTimeZoneLabel(Context context, String labelFormat, String timezone) {
        String tag = DisplayStrings.timeZoneTag(context, timezone);
        String label = String.format(labelFormat, tag);
        return DisplayStrings.createRelativeSpan(null, label, tag, 0.65f);
    }

    public static CharSequence formatTime(@NonNull Context context, long dateTime, TimeZone timezone, boolean is24Hr)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateTime);
        String format = (is24Hr ? context.getString(R.string.format_time24) : context.getString(R.string.format_time12));
        SimpleDateFormat timeFormat = new SimpleDateFormat(format, Locale.getDefault());
        timeFormat.setTimeZone(timezone);
        return timeFormat.format(calendar.getTime());
    }

    public static SpannableString formatLocation(@NonNull Context context, @NonNull SuntimesInfo info)
    {
        if (info.location == null || info.location.length < 4) {
            return new SpannableString("");
        }

        SuntimesInfo.SuntimesOptions options = info.getOptions(context);
        boolean useAltitude = options.use_altitude;
        if (!useAltitude || info.location[3] == null || info.location[3].equals("0") || info.location[3].isEmpty()) {
            return formatLocation(context, Double.parseDouble(info.location[1]), Double.parseDouble(info.location[2]), null);
        } else {
            try {
                double meters = Double.parseDouble(info.location[3]);
                String altitude = formatHeight(context, meters, options.length_units, 0, true);
                String altitudeTag = context.getString(R.string.format_tag, altitude);
                formatter.setRoundingMode(RoundingMode.FLOOR);
                formatter.setMinimumFractionDigits(0);
                formatter.setMaximumFractionDigits(4);
                String displayString = context.getString(R.string.format_location_long, formatter.format(Double.parseDouble(info.location[1])), formatter.format(Double.parseDouble(info.location[2])), altitudeTag);
                return createRelativeSpan(null, displayString, altitudeTag, 0.5f);

            } catch (NumberFormatException e) {
                Log.e("formatLocation", "invalid altitude! " + e);
                return new SpannableString(context.getString(R.string.format_location, info.location[1], info.location[2]));
            }
        }
    }

    public static SpannableString formatLocation(@NonNull Context context, double latitude, double longitude, @Nullable Integer places)
    {
        formatter.setRoundingMode(RoundingMode.FLOOR);
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(places != null ? places : 4);
        return new SpannableString(context.getString(R.string.format_location, formatter.format(latitude), formatter.format(longitude)));
    }

    public static String formatHeight(Context context, double meters, String units, int places, boolean shortForm)
    {
        double value;
        String unitsString;
        if (units != null && units.equals(SuntimesInfo.SuntimesOptions.UNITS_IMPERIAL)) {
            value = 3.28084d * meters;
            unitsString = (shortForm ? context.getString(R.string.units_feet_short) : context.getString(R.string.units_feet));

        } else {
            value = meters;
            unitsString = (shortForm ? context.getString(R.string.units_meters_short) : context.getString(R.string.units_meters));
        }
        formatter.setRoundingMode(RoundingMode.FLOOR);
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(places);
        return context.getString(R.string.format_location_altitude, formatter.format(value), unitsString);
    }
    private static NumberFormat formatter = NumberFormat.getInstance();

    public static SpannableString createRelativeSpan(@Nullable SpannableString span, @NonNull String text, @NonNull String toRelative, float relativeSize)
    {
        if (span == null) {
            span = new SpannableString(text);
        }
        int start = text.indexOf(toRelative);
        if (start >= 0) {
            int end = start + toRelative.length();
            span.setSpan(new RelativeSizeSpan(relativeSize), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return span;
    }

    public static SpannableString createColorSpan(SpannableString span, String text, String toColorize, int color)
    {
        if (span == null) {
            span = new SpannableString(text);
        }
        int start = text.indexOf(toColorize);
        if (start >= 0)
        {
            int end = start + toColorize.length();
            span.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return span;
    }

    public static Spanned fromHtml(String htmlString )
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY);
        else return Html.fromHtml(htmlString);
    }

}
