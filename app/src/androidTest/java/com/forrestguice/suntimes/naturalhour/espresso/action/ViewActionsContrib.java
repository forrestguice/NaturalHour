package com.forrestguice.suntimes.naturalhour.espresso.action;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.CoordinatesProvider;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import org.hamcrest.Matcher;

import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.core.AllOf.allOf;

public class ViewActionsContrib
{
    public static ViewAction swipeRightTo(final int x) {
        return swipeHorizontalTo(x);
    }
    public static ViewAction swipeLeftTo(final int x) {
        return swipeHorizontalTo(x);
    }
    public static ViewAction swipeHorizontalTo(final int x)
    {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER, new CoordinatesProvider()
        {
            @Override
            public float[] calculateCoordinates(View view)
            {
                int[] position = new int[2];
                view.getLocationOnScreen(position);
                return new float[] { x, position[1] };
            }
        }, Press.FINGER);
    }

    /**
     * from https://stackoverflow.com/a/51262525
     */
    public static ViewAction selectTabAtPosition(final int position)
    {
        return new ViewAction()
        {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isDisplayed(), isAssignableFrom(TabLayout.class));
            }

            @Override
            public String getDescription() {
                return "with tab at index " + position;
            }

            @Override
            public void perform(UiController uiController, View view)
            {
                if (view instanceof TabLayout)
                {
                    TabLayout tabLayout = (TabLayout) view;
                    TabLayout.Tab tab = tabLayout.getTabAt(position);
                    if (tab != null) {
                        tab.select();
                    }
                }
            }
        };
    }

}