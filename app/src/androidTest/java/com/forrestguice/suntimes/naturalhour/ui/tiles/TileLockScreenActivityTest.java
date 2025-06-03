package com.forrestguice.suntimes.naturalhour.ui.tiles;

import android.content.Context;
import android.content.Intent;

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

import static com.forrestguice.suntimes.naturalhour.TestRobot.setAnimationsEnabled;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TileLockScreenActivityTest
{
    @Rule
    public ActivityTestRule<TileLockScreenActivity> activityRule = new ActivityTestRule<>(TileLockScreenActivity.class, false, false);
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
    public void test_tileLockScreenActivity()
    {
        Intent intent = new Intent(context, TileLockScreenActivity.class);
        intent.putExtra(TileLockScreenActivity.EXTRA_APPWIDGETID, NaturalHourTileBase.TILE_APPWIDGET_ID);
        activityRule.launchActivity(intent);
        assertFalse(activityRule.getActivity().isFinishing());
    }

    @Test
    public void test_tileLockScreenActivity_missingID()
    {
        Intent intent = new Intent(context, TileLockScreenActivity.class);
        activityRule.launchActivity(intent);
        assertTrue(activityRule.getActivity().isFinishing());
    }

    /**
     * TileLockScreenActivityRobot
     */
    public static class TileLockScreenActivityRobot extends TestRobot.ActivityRobot<TileLockScreenActivityRobot>
    {
        public TileLockScreenActivityRobot() {
            setRobot(this);
        }

        public TileLockScreenActivityRobot assertActivityShown()
        {
            // TODO
            return this;
        }
    }
}