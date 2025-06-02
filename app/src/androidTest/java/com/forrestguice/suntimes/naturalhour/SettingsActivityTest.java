package com.forrestguice.suntimes.naturalhour;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.forrestguice.suntimes.naturalhour.TestRobot.setAnimationsEnabled;
import static com.forrestguice.suntimes.naturalhour.espresso.ViewAssertionHelper.assertShown;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest
{
    @Rule
    public ActivityTestRule<SettingsActivity> activityRule = new ActivityTestRule<>(SettingsActivity.class);

    @Before
    public void beforeTest() throws IOException {
        setAnimationsEnabled(false);
    }
    @After
    public void afterTest() throws IOException {
        setAnimationsEnabled(true);
    }

    @Test
    public void test_settingsActivity() {
        new SettingsActivityRobot()
                .assertActivityShown();
    }

    /**
     * SettingsActivityRobot
     */
    public static class SettingsActivityRobot extends TestRobot.ActivityRobot<SettingsActivityRobot>
    {
        public SettingsActivityRobot() {
            setRobot(this);
        }

        public SettingsActivityRobot assertActivityShown()
        {
            onView(withText(R.string.pref_category_general)).check(assertShown);
            onView(withText(R.string.pref_title_hourdef)).check(assertShown);
            onView(withText(R.string.pref_title_timeformat)).check(assertShown);
            return this;
        }
    }
}