package com.example.frolic;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EntrantDashboardActivityTest {
    @Rule
    public ActivityScenarioRule<EntrantDashboardActivity> activityRule =
            new ActivityScenarioRule<>(EntrantDashboardActivity.class);


    @Test
    public void testDashboardTitleIsDisplayed() {
        // Check if the title of the dashboard is correctly displayed
        onView(withId(R.id.tvTitle)).check(matches(withText("Entrant Dashboard")));
        onView(withId(R.id.tvTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void testViewComponentsDisplayed() {
        // Check that all main components on the dashboard are displayed
        onView(withId(R.id.tvTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.tvViewEvents)).check(matches(isDisplayed()));
        onView(withId(R.id.tvMyEvents)).check(matches(isDisplayed()));
        onView(withId(R.id.tvNotifications)).check(matches(isDisplayed()));
        onView(withId(R.id.tvProfile)).check(matches(isDisplayed()));
        onView(withId(R.id.btnScanQR)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigateToProfile() {
        // Test navigating to the profile screen by clicking "My Profile"
        onView(withId(R.id.tvProfile)).perform(click());
        onView(withId(R.id.entrant_details_screen)).check(matches(isDisplayed()));
    }

    @Test
    public void testRoleSwitchToOrganizer() {
        // Open the options menu, click "Switch Role," then confirm switch
        onView(withId(R.id.action_switch_role)).perform(click());
        onView(withText("Switch")).perform(click());

        // Check Organizer Dashboard displayed after switch (mock data needed)
        onView(withId(R.layout.organizer_dashboard_screen)).check(matches(isDisplayed()));
    }
}

