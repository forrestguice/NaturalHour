package com.forrestguice.suntimes.naturalhour;

import android.content.Context;

import com.forrestguice.suntimes.naturalhour.ui.DisplayStrings;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DisplayStringsTest
{
    public static final String TAG = "DisplayStrings";

    public Context getContext() {
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void test_numerals_roman()
    {
        Context context = getContext();
        assertEquals("IV", DisplayStrings.numeral(context, 4, context.getResources().getStringArray(R.array.roman_numeral)));
        assertEquals("VI", DisplayStrings.numeral(context, 6, context.getResources().getStringArray(R.array.roman_numeral)));
        assertEquals("IX", DisplayStrings.numeral(context, 9, context.getResources().getStringArray(R.array.roman_numeral)));
        assertEquals("XII", DisplayStrings.numeral(context, 12, context.getResources().getStringArray(R.array.roman_numeral)));
        assertEquals("XXI", DisplayStrings.numeral(context, 21, context.getResources().getStringArray(R.array.roman_numeral)));
    }

    @Test
    public void test_numerals_attic()
    {
        Context context = getContext();
        assertEquals("IIII", DisplayStrings.numeral(context, 4, context.getResources().getStringArray(R.array.attic_numeral)));
        assertEquals("ΠI", DisplayStrings.numeral(context, 6, context.getResources().getStringArray(R.array.attic_numeral)));
        assertEquals("ΠIIII", DisplayStrings.numeral(context, 9, context.getResources().getStringArray(R.array.attic_numeral)));
        assertEquals("ΔII", DisplayStrings.numeral(context, 12, context.getResources().getStringArray(R.array.attic_numeral)));
        assertEquals("ΔΔI", DisplayStrings.numeral(context, 21, context.getResources().getStringArray(R.array.attic_numeral)));
    }

    @Test
    public void test_numerals_etruscan()
    {
        Context context = getContext();
        assertEquals("IIII", DisplayStrings.numeral(context, 4, context.getResources().getStringArray(R.array.etruscan_numeral)));
        assertEquals("ΛI", DisplayStrings.numeral(context, 6, context.getResources().getStringArray(R.array.etruscan_numeral)));
        assertEquals("ΛIIII", DisplayStrings.numeral(context, 9, context.getResources().getStringArray(R.array.etruscan_numeral)));
        assertEquals("XII", DisplayStrings.numeral(context, 12, context.getResources().getStringArray(R.array.etruscan_numeral)));
        assertEquals("XXI", DisplayStrings.numeral(context, 21, context.getResources().getStringArray(R.array.etruscan_numeral)));
    }

    @Test
    public void test_numerals_greek_upper()
    {
        Context context = getContext();
        assertEquals("Δ", DisplayStrings.numeral(context, 4, context.getResources().getStringArray(R.array.greek_upper_numeral)));
        assertEquals("F", DisplayStrings.numeral(context, 6, context.getResources().getStringArray(R.array.greek_upper_numeral)));
        assertEquals("Θ", DisplayStrings.numeral(context, 9, context.getResources().getStringArray(R.array.greek_upper_numeral)));
        assertEquals("ΙB", DisplayStrings.numeral(context, 12, context.getResources().getStringArray(R.array.greek_upper_numeral)));
        assertEquals("ΚA", DisplayStrings.numeral(context, 21, context.getResources().getStringArray(R.array.greek_upper_numeral)));
    }

    @Test
    public void test_numerals_greek_lower()
    {
        Context context = getContext();
        assertEquals("δ", DisplayStrings.numeral(context, 4, context.getResources().getStringArray(R.array.greek_lower_numeral)));
        assertEquals("ς", DisplayStrings.numeral(context, 6, context.getResources().getStringArray(R.array.greek_lower_numeral)));
        assertEquals("θ", DisplayStrings.numeral(context, 9, context.getResources().getStringArray(R.array.greek_lower_numeral)));
        assertEquals("ιβ", DisplayStrings.numeral(context, 12, context.getResources().getStringArray(R.array.greek_lower_numeral)));
        assertEquals("κα", DisplayStrings.numeral(context, 21, context.getResources().getStringArray(R.array.greek_lower_numeral)));
    }

    @Test
    public void test_numerals_armenian()
    {
        Context context = getContext();
        for (int hour=0; hour<30; hour++) {
            assertEquals(DisplayStrings.armenianNumeral0(context, hour),
                    DisplayStrings.numeral(context, hour, context.getResources().getStringArray(R.array.armenian_numeral)));
        }
    }

}