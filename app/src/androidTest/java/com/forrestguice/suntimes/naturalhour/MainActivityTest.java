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

import static com.forrestguice.suntimes.naturalhour.TestRobot.setAnimationsEnabled;

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
        MainActivityRobot robot = new MainActivityRobot();
                robot.assertActionBar_homeButtonShown(true);
    }

    /**
     * MainActivityRobot
     */
    public static class MainActivityRobot extends TestRobot.ActivityRobot<MainActivityRobot>
    {
        public MainActivityRobot() {
            setRobot(this);
        }
    }
}