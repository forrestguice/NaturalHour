package com.forrestguice.suntimes.naturalhour.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import static com.forrestguice.suntimes.naturalhour.TestRobot.setAnimationsEnabled;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NaturalHourWidgetTest
{
    protected Context context;

    @Before
    public void beforeTest() throws IOException {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        setAnimationsEnabled(false);
    }
    @After
    public void afterTest() throws IOException {
        setAnimationsEnabled(true);
    }

    @Test
    public void test_getUpdateIntent()
    {
        int appWidgetID0 = -1000;
        NaturalHourWidget widget0 = new NaturalHourWidget();
        Intent intent0 = widget0.getUpdateIntent(context, appWidgetID0);

        assertNotNull(intent0);
        assertEquals(widget0.getUpdateIntentFilter(), intent0.getAction());
        assertTrue(intent0.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID));
        assertEquals(appWidgetID0, intent0.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, Integer.MAX_VALUE));
        assertTrue(intent0.hasExtra(NaturalHourWidget.KEY_WIDGETCLASS));
        assertEquals(intent0.getStringExtra(NaturalHourWidget.KEY_WIDGETCLASS), widget0.getClass().toString());
    }

    @Test
    public void test_getUpdatePendingIntent()
    {
        NaturalHourWidget widget0 = new NaturalHourWidget();
        PendingIntent intent0 = widget0.getUpdatePendingIntent(context, -1000);
        assertNotNull(intent0);
    }

    @Test
    public void test_onReceive_widgetUpdate()
    {
        NaturalHourWidget widget0 = new NaturalHourWidget();
        Intent intent0 = widget0.getUpdateIntent(context, -1000);

        assertFalse(widget0.t_onReceived);
        widget0.onReceive(context, intent0);
        assertTrue(widget0.t_onReceived);
    }

    @Test
    public void test_onReceive_appWidgetUpdate()
    {
        NaturalHourWidget widget0 = new NaturalHourWidget();
        Intent intent0 = widget0.getUpdateIntent(context, -1000);
        intent0.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        assertFalse(widget0.t_onReceived);
        widget0.onReceive(context, intent0);
        assertTrue(widget0.t_onReceived);
    }

    @Test
    public void test_onReceive_themUpdate()
    {
        NaturalHourWidget widget0 = new NaturalHourWidget();
        Intent intent0 = widget0.getUpdateIntent(context, -1000);
        intent0.setAction(NaturalHourWidget.ACTION_SUNTIMES_THEME_UPDATE);

        assertFalse(widget0.t_onReceived);
        widget0.onReceive(context, intent0);
        assertTrue(widget0.t_onReceived);
    }

    @Test
    public void test_onReceive_alarmUpdate()
    {
        NaturalHourWidget widget0 = new NaturalHourWidget();
        Intent intent0 = widget0.getUpdateIntent(context, -1000);
        intent0.setAction(NaturalHourWidget.ACTION_SUNTIMES_ALARM_UPDATE);

        assertFalse(widget0.t_onReceived);
        widget0.onReceive(context, intent0);
        assertTrue(widget0.t_onReceived);
    }

    @Test
    public void test_onReceive_appWidgetOptionsChanged()
    {
        NaturalHourWidget widget0 = new NaturalHourWidget();
        Intent intent0 = widget0.getUpdateIntent(context, -1000);
        intent0.setAction(AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED);

        assertFalse(widget0.t_onReceived);
        widget0.onReceive(context, intent0);
        assertTrue(widget0.t_onReceived);
    }
}