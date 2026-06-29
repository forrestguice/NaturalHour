package com.forrestguice.suntimes.naturalhour.ui.daydream;

import android.content.Context;
import android.content.Intent;

import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.TestMissingSuntimes;
import com.forrestguice.suntimes.naturalhour.TestRequiresSuntimes;
import com.forrestguice.suntimes.naturalhour.TestRobot;
import com.forrestguice.suntimes.naturalhour.ui.tiles.NaturalHourTileConfigActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.forrestguice.suntimes.naturalhour.TestRobot.setAnimationsEnabled;
import static com.forrestguice.suntimes.naturalhour.espresso.ViewAssertionHelper.assertShown;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ClockDaydreamSettingsActivityTest
{
    public static final String TAG = "ClockDaydreamSettingsActivity";
    public static final String TAG_MISSING_SUNTIMES = "missing_suntimes";

    @Rule
    public ActivityTestRule<ClockDaydreamSettingsActivity> activityRule = new ActivityTestRule<>(ClockDaydreamSettingsActivity.class, false, false);
    private Context context;

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
    @TestRequiresSuntimes
    public void test_daydreamSettingsActivity_about()
    {
        ClockDaydreamSettingsActivity activity = activityRule.launchActivity(new Intent(context, ClockDaydreamSettingsActivity.class));
        DaydreamSettingsActivityRobot robot = new DaydreamSettingsActivityRobot();
        robot.captureScreenshot(activity, TAG)
                .assertSuntimesRequiredMessageNotShown(activity);

        robot.showOverflowMenu(activityRule.getActivity())
                .captureScreenshot(activity, TAG + "_menu")
                .assertOverflowMenuShown();

        robot.clickOverflowMenu_about()
                .captureScreenshot(activity, TAG + "_about")
                .assertAboutShown()
                .assertAboutNotShown_missingSuntimes(activity);
    }

    @Test
    @TestMissingSuntimes
    public void test_daydreamSettingsActivity_about_missingSuntimes()
    {
        ClockDaydreamSettingsActivity activity = activityRule.launchActivity(new Intent(context, ClockDaydreamSettingsActivity.class));
        DaydreamSettingsActivityRobot robot = new DaydreamSettingsActivityRobot();
        robot.captureScreenshot(activity, TAG, TAG_MISSING_SUNTIMES)
                .assertSuntimesRequiredMessageShown(activity);

        robot.showOverflowMenu(activityRule.getActivity())
                .captureScreenshot(activity, TAG + "_menu", TAG_MISSING_SUNTIMES)
                .assertOverflowMenuShown();

        robot.clickOverflowMenu_about()
                .captureScreenshot(activity, TAG + "_about", TAG_MISSING_SUNTIMES)
                .assertAboutShown()
                .assertAboutShown_missingSuntimes(activity);
    }

    /**
     * DaydreamSettingsActivityRobot
     */
    public static class DaydreamSettingsActivityRobot extends TestRobot.ActivityRobot<DaydreamSettingsActivityRobot>
    {
        public DaydreamSettingsActivityRobot() {
            setRobot(this);
        }

        public DaydreamSettingsActivityRobot assertOverflowMenuShown()
        {
            onView(withText(R.string.action_about)).check(assertShown);
            return this;
        }

        public DaydreamSettingsActivityRobot assertAboutShown()
        {
            onView(withText(R.string.app_name)).check(assertShown);
            onView(withText(R.string.app_desc)).check(assertShown);
            onView(withId(R.id.txt_about_version)).check(assertShown);
            return this;
        }
    }
}