package com.forrestguice.suntimes.naturalhour.ui.widget;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Widget4x3ConfigActivityTest extends WidgetConfigActivityTest
{
    @Rule
    public ActivityTestRule<NaturalHourWidget_4x3_ConfigActivity> activityRule = new ActivityTestRule<>(NaturalHourWidget_4x3_ConfigActivity.class, false, false);

    @Test
    public void test_widgetConfigActivity_about()
    {
        activityRule.launchActivity(getLaunchIntent(context, NaturalHourWidget_4x3_ConfigActivity.class, -9000, null));
        test_widgetConfigActivity_about(context, new WidgetConfigActivityRobot());
    }

}