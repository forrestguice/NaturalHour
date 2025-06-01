package com.forrestguice.suntimes.naturalhour;

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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.forrestguice.suntimes.naturalhour.TestRobot.setAnimationsEnabled;
import static com.forrestguice.suntimes.naturalhour.espresso.ViewAssertionHelper.assertShown;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest
{
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
    public void test_mainActivity() {
        new MainActivityRobot()
                .assertActionBar_homeButtonShown(true);
    }

    @Test
    public void test_mainActivity_setAlarm() {
        MainActivity activity = activityRule.getActivity();
        new MainActivityRobot()
                .showOverflowMenu(activity)
                .assertOverflowMenuShown()
                .clickOverflowMenu_setAlarm()
                .assertSetAlarmShown();
    }

    @Test
    public void test_mainActivity_help() {
        MainActivity activity = activityRule.getActivity();
        new MainActivityRobot()
                .showOverflowMenu(activity)
                .assertOverflowMenuShown()
                .clickOverflowMenu_help()
                .assertHelpShown();
    }

    @Test
    public void test_mainActivity_about() {
        MainActivity activity = activityRule.getActivity();
        new MainActivityRobot()
                .showOverflowMenu(activity)
                .assertOverflowMenuShown()
                .clickOverflowMenu_about()
                .assertAboutShown();
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
            onView(withText(R.string.action_alarms)).check(assertShown);
            return this;
        }

        public MainActivityRobot assertOverflowMenuShown()
        {
            onView(withText(R.string.action_alarms)).check(assertShown);
            onView(withText(R.string.action_help)).check(assertShown);
            onView(withText(R.string.action_about)).check(assertShown);
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