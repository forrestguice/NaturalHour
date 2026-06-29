package com.forrestguice.suntimes.naturalhour;

import android.content.Context;
import android.text.Html;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import java.io.IOException;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.forrestguice.suntimes.naturalhour.TestRobot.setAnimationsEnabled;
import static com.forrestguice.suntimes.naturalhour.espresso.ViewAssertionHelper.assertShown;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest
{
    public static final String TAG = "MainActivity";
    public static final String TAG_MISSING_SUNTIMES = "missing_suntimes";

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void beforeTest() throws IOException {
        setAnimationsEnabled(false);
    }
    @After
    public void afterTest() throws IOException {
        setAnimationsEnabled(true);
    }

    /**
     * test_mainActivity
     */
    @Test
    @TestMissingSuntimes
    public void test_mainActivity_missingSuntimes()
    {
        MainActivity activity = activityRule.getActivity();
        MainActivityRobot robot = new MainActivityRobot()
                .captureScreenshot(activity, TAG, TAG_MISSING_SUNTIMES);
        assertFalse(activity.suntimesInfo.isInstalled);
        robot.assertSuntimesRequiredMessageShown(activity);
    }

    @Test
    @TestRequiresSuntimes
    public void test_mainActivity()
    {
        MainActivity activity = activityRule.getActivity();
        MainActivityRobot robot = new MainActivityRobot()
                .captureScreenshot(activity, TAG);

        assertNotNull(activity.suntimesInfo);
        assertTrue(activity.suntimesInfo.isInstalled);
        assertTrue(activity.suntimesInfo.hasPermission);

        robot.assertActionBar_homeButtonShown(true)
                .assertSuntimesRequiredMessageNotShown(activity);
    }

    @Test
    @TestRequiresSuntimes
    public void test_mainActivity_setAlarm() {
        MainActivity activity = activityRule.getActivity();
        new MainActivityRobot()
                .showOverflowMenu(activity)
                .captureScreenshot(activity, TAG + "_setAlarm_menu")
                .assertOverflowMenuShown_alarms()
                .clickOverflowMenu_setAlarm()
                .captureScreenshot(activity, TAG + "_setAlarm")
                .assertSetAlarmShown();
    }

    @Test
    public void test_mainActivity_help() {
        MainActivity activity = activityRule.getActivity();
        new MainActivityRobot()
                .showOverflowMenu(activity)
                .assertOverflowMenuShown()
                .clickOverflowMenu_help()
                .captureScreenshot(activity, TAG + "_help")
                .assertHelpShown();
    }

    @Test
    public void test_mainActivity_about() {
        MainActivity activity = activityRule.getActivity();
        new MainActivityRobot()
                .showOverflowMenu(activity)
                .captureScreenshot(activity, TAG + "_menu")
                .assertOverflowMenuShown()
                .clickOverflowMenu_about()
                .captureScreenshot(activity, TAG + "_about")
                .assertAboutShown();
    }

    @Test
    @TestMissingSuntimes
    public void test_mainActivity_about_missingSuntimes() {
        MainActivity activity = activityRule.getActivity();
        new MainActivityRobot()
                .showOverflowMenu(activity)
                .assertOverflowMenuShown()
                .clickOverflowMenu_about()
                .captureScreenshot(activity, TAG + "_about", TAG_MISSING_SUNTIMES)
                .assertAboutShown()
                .assertAboutShown_missingSuntimes(activity);
    }

    /**
     * MainActivityRobot
     */
    public static class MainActivityRobot extends TestRobot.ActivityRobot<MainActivityRobot>
    {
        public MainActivityRobot() {
            setRobot(this);
        }

        public MainActivityRobot clickOverflowMenu_setAlarm() {
            onView(withText(R.string.action_alarms)).perform(click());
            return this;
        }

        public MainActivityRobot assertOverflowMenuShown() {
            onView(withText(R.string.action_help)).check(assertShown);
            onView(withText(R.string.action_about)).check(assertShown);
            return this;
        }
        public MainActivityRobot assertOverflowMenuShown_alarms() {
            onView(withText(R.string.action_alarms)).check(assertShown);
            return this;
        }

        public MainActivityRobot assertSetAlarmShown() {
            onView(withId(R.id.alarmdialog_title)).check(assertShown);
            return this;
        }

        public MainActivityRobot assertHelpShown() {
            onView(withId(R.id.txt_help_content)).check(assertShown);
            return this;
        }

        public MainActivityRobot assertAboutShown()
        {
            onView(withText(R.string.app_name)).check(assertShown);
            onView(withText(R.string.app_desc)).check(assertShown);
            onView(withId(R.id.txt_about_version)).check(assertShown);
            return this;
        }

    }
}