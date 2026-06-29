package com.forrestguice.suntimes.naturalhour.ui.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.TestRobot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.forrestguice.suntimes.naturalhour.TestRobot.setAnimationsEnabled;
import static com.forrestguice.suntimes.naturalhour.espresso.ViewAssertionHelper.assertShown;

@LargeTest
@RunWith(AndroidJUnit4.class)
public abstract class WidgetConfigActivityTest
{
    public static final String TAG_MISSING_SUNTIMES = "missing_suntimes";

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

    public static void test_widgetConfigActivity_about(Activity context, WidgetConfigActivityRobot robot, String tag)
    {
        robot.captureScreenshot(context, tag)
                .assertSuntimesRequiredMessageNotShown(context);

        robot.showOverflowMenu(context)
                .captureScreenshot(context, tag + "_menu")
                .assertOverflowMenuShown();

        robot.clickOverflowMenu_about()
                .captureScreenshot(context, tag + "_about")
                .assertAboutShown()
                .assertAboutNotShown_missingSuntimes(context);
    }

    public static void test_widgetConfigActivity_about_missingSuntimes(Activity context, WidgetConfigActivityRobot robot, String tag)
    {
        robot.captureScreenshot(context, tag, TAG_MISSING_SUNTIMES)
                .assertSuntimesRequiredMessageShown(context);

        robot.showOverflowMenu(context)
                .captureScreenshot(context, tag + "_menu", TAG_MISSING_SUNTIMES)
                .assertOverflowMenuShown();

        robot.clickOverflowMenu_about()
                .captureScreenshot(context, tag + "_about", TAG_MISSING_SUNTIMES)
                .assertAboutShown()
                .assertAboutShown_missingSuntimes(context);
    }

    public static Intent getLaunchIntent(Context context, Class<?> widgetClass, int appWidgetID, Boolean reconfigure)
    {
        Intent intent = new Intent(context, widgetClass);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);
        if (reconfigure != null) {
            intent.putExtra(WidgetConfigActivity.EXTRA_RECONFIGURE, reconfigure);
        }
        return intent;
    }

    /**
     * WidgetConfigActivityRobot
     */
    public static class WidgetConfigActivityRobot extends TestRobot.ActivityRobot<WidgetConfigActivityRobot>
    {
        public WidgetConfigActivityRobot() {
            setRobot(this);
        }

        public WidgetConfigActivityRobot assertOverflowMenuShown()
        {
            onView(withText(R.string.action_about)).check(assertShown);
            return this;
        }

        public WidgetConfigActivityRobot assertAboutShown()
        {
            onView(withText(R.string.app_name)).check(assertShown);
            onView(withText(R.string.app_desc)).check(assertShown);
            onView(withId(R.id.txt_about_version)).check(assertShown);
            return this;
        }
    }
}