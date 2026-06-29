package com.forrestguice.suntimes.naturalhour.ui.tiles;

import android.content.Context;

import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.TestMissingSuntimes;
import com.forrestguice.suntimes.naturalhour.TestRequiresSuntimes;
import com.forrestguice.suntimes.naturalhour.ui.widget.WidgetConfigActivityTest;

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
public class TileConfigActivityTest
{
    public static final String TAG = "TileConfigActivity";
    public static final String TAG_MISSING_SUNTIMES = "missing_suntimes";

    @Rule
    public ActivityTestRule<NaturalHourTileConfigActivity> activityRule = new ActivityTestRule<>(NaturalHourTileConfigActivity.class, false, false);
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
    public void test_tileConfigActivity_about()
    {
        NaturalHourTileConfigActivity activity = activityRule.launchActivity(WidgetConfigActivityTest.getLaunchIntent(context, NaturalHourTileConfigActivity.class, -9000, null));
        TileConfigActivityRobot robot = new TileConfigActivityRobot();
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
    public void test_tileConfigActivity_about_missingSuntimes()
    {
        NaturalHourTileConfigActivity activity = activityRule.launchActivity(WidgetConfigActivityTest.getLaunchIntent(context, NaturalHourTileConfigActivity.class, -9000, null));
        TileConfigActivityRobot robot = new TileConfigActivityRobot();
        robot.captureScreenshot(activity, TAG)
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
     * TileConfigActivityRobot
     */
    public static class TileConfigActivityRobot extends WidgetConfigActivityTest.WidgetConfigActivityRobot
    {
        public TileConfigActivityRobot() {
            setRobot(this);
        }

        public TileConfigActivityRobot assertOverflowMenuShown()
        {
            onView(withText(R.string.action_about)).check(assertShown);
            return this;
        }

        public TileConfigActivityRobot assertAboutShown()
        {
            onView(withText(R.string.app_name)).check(assertShown);
            onView(withText(R.string.app_desc)).check(assertShown);
            onView(withId(R.id.txt_about_version)).check(assertShown);
            return this;
        }
    }
}