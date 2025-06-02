package com.forrestguice.suntimes.naturalhour.ui.alarms;

import android.content.Context;
import android.content.Intent;

import com.forrestguice.suntimes.naturalhour.MainActivityTest;
import com.forrestguice.suntimes.naturalhour.R;
import com.forrestguice.suntimes.naturalhour.TestRobot;

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
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.forrestguice.suntimes.naturalhour.TestRobot.setAnimationsEnabled;
import static com.forrestguice.suntimes.naturalhour.espresso.ViewAssertionHelper.assertShown;
import static com.forrestguice.suntimes.naturalhour.espresso.matcher.ViewMatchersContrib.navigationButton;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AlarmActivityTest
{
    @Rule
    public ActivityTestRule<AlarmActivity> activityRule = new ActivityTestRule<>(AlarmActivity.class, false, false);
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
    public void test_alarmActivity_help()
    {
        activityRule.launchActivity(new Intent(context, AlarmActivity.class));
        new AlarmActivityRobot()
                .cancelActionMode().sleep(500)
                .showOverflowMenu(activityRule.getActivity())
                .clickOverflowMenu_help()
                .assertHelpShown();
    }

    @Test
    public void test_alarmActivity_about()
    {
        activityRule.launchActivity(new Intent(context, AlarmActivity.class));
        new AlarmActivityRobot()
                .cancelActionMode().sleep(500)
                .showOverflowMenu(activityRule.getActivity())
                .assertOverflowMenuShown()
                .clickOverflowMenu_about()
                .assertAboutShown();
    }

    /**
     * AlarmActivityRobot
     */
    public static class AlarmActivityRobot extends TestRobot.ActivityRobot<AlarmActivityRobot>
    {
        public AlarmActivityRobot() {
            setRobot(this);
        }

        public AlarmActivityRobot cancelActionMode() {
            onView(navigationButton()).perform(click());
            return this;
        }

        public AlarmActivityRobot assertOverflowMenuShown()
        {
            onView(withText(R.string.action_help)).check(assertShown);
            onView(withText(R.string.action_about)).check(assertShown);
            return this;
        }

        public AlarmActivityRobot assertHelpShown() {
            onView(withId(R.id.txt_help_content)).check(assertShown);
            return this;
        }

        public AlarmActivityRobot assertAboutShown()
        {
            onView(withText(R.string.app_name)).check(assertShown);
            onView(withText(R.string.app_desc)).check(assertShown);
            onView(withId(R.id.txt_about_version)).check(assertShown);
            return this;
        }
    }
}