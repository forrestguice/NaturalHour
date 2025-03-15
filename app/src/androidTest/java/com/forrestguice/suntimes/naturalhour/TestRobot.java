package com.forrestguice.suntimes.naturalhour;

import android.app.Activity;
import android.app.UiAutomation;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.SystemClock;

import java.io.IOException;

import androidx.test.platform.app.InstrumentationRegistry;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.forrestguice.suntimes.naturalhour.espresso.ViewAssertionHelper.assertShown;
import static com.forrestguice.suntimes.naturalhour.espresso.matcher.ViewMatchersContrib.hasDrawable;
import static com.forrestguice.suntimes.naturalhour.espresso.matcher.ViewMatchersContrib.navigationButton;
import static org.hamcrest.CoreMatchers.allOf;

public abstract class TestRobot<T>
{
    protected T robot;
    public void setRobot(T robot) {
        this.robot = robot;
    }

    public T sleep(long ms) {
        SystemClock.sleep(ms);
        return robot;
    }

    public T doubleRotateDevice(Activity activity)
    {
        rotateDevice(activity, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        sleep(1000);
        rotateDevice(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        return robot;
    }
    public T rotateDevice(Activity activity, int orientation) {
        activity.setRequestedOrientation(orientation);
        return robot;
    }

    public T captureScreenshot(Activity activity, String name) {
        captureScreenshot(activity, "", name);
        return robot;
    }
    public T captureScreenshot(Activity activity, String subdir, String name) {
        //captureScreenshot(activity, subdir, name);   // TODO
        return robot;
    }

    /**
     * ActivityRobot
     * @param <T>
     */
    public static abstract class ActivityRobot<T> extends TestRobot<T>
    {
        public ActivityRobot() {}
        public ActivityRobot(T robot) {
            this.robot = robot;
        }

        public T recreateActivity(final Activity activity)
        {
            InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
                public void run() {
                    activity.recreate();
                }
            });
            return robot;
        }

        public T finishActivity(final Activity activity)
        {
            InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
                public void run() {
                    activity.finish();
                }
            });
            return robot;
        }

        public T clickHomeButton(Context context) {
            onView(navigationButton()).perform(click());
            return robot;
        }
        public T showOverflowMenu(Context context) {
            openActionBarOverflowOrOptionsMenu(context);
            return robot;
        }
        public T clickOverflowMenu_help() {
            onView(withText(R.string.action_help)).inRoot(isPlatformPopup()).perform(click());
            return robot;
        }
        public T clickOverflowMenu_settings() {
            onView(withText(R.string.action_settings)).inRoot(isPlatformPopup()).perform(click());
            return robot;
        }
        public T clickOverflowMenu_about() {
            onView(withText(R.string.action_about)).inRoot(isPlatformPopup()).perform(click());
            return robot;
        }
        public T assertActionBar_homeButtonShown(boolean shown) {
            onView(allOf(navigationButton(), hasDrawable(R.drawable.ic_action_suntimes))).check(shown ? assertShown : doesNotExist());
            return robot;
        }
        public T assertActionBar_navButtonShown(boolean shown) {
            onView(navigationButton()).check(shown ? assertShown : doesNotExist());
            return robot;
        }
    }

    public static void setAnimationsEnabled(boolean enabled) throws IOException
    {
        UiAutomation automation = androidx.test.InstrumentationRegistry.getInstrumentation().getUiAutomation();
        automation.executeShellCommand("settings put global transition_animation_scale " + (enabled ? "1" : "0")).close();
        automation.executeShellCommand("settings put global window_animation_scale " + (enabled ? "1" : "0")).close();
        automation.executeShellCommand("settings put global animator_duration_scale " + (enabled ? "1" : "0")).close();
    }

    public static Context getContext() {
        return androidx.test.InstrumentationRegistry.getInstrumentation().getTargetContext();
    }
}