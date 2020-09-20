// SPDX-License-Identifier: GPL-3.0-or-later
/*
    Copyright (C) 2020 Forrest Guice
    This file is part of RomanTime.

    RomanTime is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    RomanTime is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with RomanTime.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.forrestguice.suntimes.romantime.ui;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import com.forrestguice.suntimes.addon.SuntimesInfo;
import com.forrestguice.suntimes.romantime.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DisplayStrings
{
    public static CharSequence romanNumeral(@NonNull Context context, int hour)
    {
        switch (hour) {
            case 1: return "I"; case 2: return "II"; case 3: return "III"; case 4: return "IV";
            case 5: return "V"; case 6: return "VI"; case 7: return "VII"; case 8: return "VIII"; case 9: return "IX";
            case 10: return "X"; case 11: return "XI"; case 12: return "XII"; case 13: return "XIII"; case 14: return "XIV";
            case 15: return "XV"; case 16: return "XVI"; case 17: return "XVII"; case 18: return "XVIII"; case 19: return "XIX";
            case 20: return "XX"; case 21: return "XXI"; case 22: return "XXII"; case 23: return "XXIII"; case 24: return "XXIV";
            default: return "";
        }
    }

    /*
     * @param context Context
     * @param num [1-4]
     * @return e.g. "Vigilia Prima"
     */
    public static CharSequence formatNightWatchLabel(@NonNull Context context, int num, boolean latin)
    {
        String[] phrase = context.getResources().getStringArray(latin ? R.array.vigilia_phrase_latin : R.array.vigilia_phrase);
        if (num >= 1 && num <= 4) {
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
        Calendar now = Calendar.getInstance();
        boolean isThisYear = now.get(Calendar.YEAR) == date.get(Calendar.YEAR);

        Locale locale = Locale.getDefault();
        SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString( isThisYear ? R.string.format_date : R.string.format_date_long), locale);
        dateFormat.setTimeZone(date.getTimeZone());
        return dateFormat.format(date.getTime());
    }

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
        SuntimesInfo.SuntimesOptions options = info.getOptions(context);
        boolean useAltitude = options.use_altitude;
        if (!useAltitude || info.location[2] == null || info.location[2].equals("0") || info.location[2].isEmpty()) {
            return new SpannableString(context.getString(R.string.format_location, info.location[1], info.location[2]));

        } else {
            try {
                double meters = Double.parseDouble(info.location[3]);
                String altitude = formatHeight(context, meters, options.length_units, 0, true);
                String altitudeTag = context.getString(R.string.format_tag, altitude);
                String displayString = context.getString(R.string.format_location_long, info.location[1], info.location[2], altitudeTag);
                return createRelativeSpan(null, displayString, altitudeTag, 0.5f);

            } catch (NumberFormatException e) {
                Log.e("formatLocation", "invalid altitude! " + e);
                return new SpannableString(context.getString(R.string.format_location, info.location[1], info.location[2]));
            }
        }
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

        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(places);
        return context.getString(R.string.format_location_altitude, formatter.format(value), unitsString);
    }

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

    public static Spanned fromHtml(String htmlString )
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY);
        else return Html.fromHtml(htmlString);
    }

}
