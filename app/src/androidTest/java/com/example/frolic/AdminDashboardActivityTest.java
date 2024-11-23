package com.example.frolic;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminDashboardActivityTest {
    // Define the deviceId to pass into the intent
    private static final String TEST_DEVICE_ID = "02b7f87772d89350";

    // Rule to launch AdminDashboardActivity using an Intent
    @Rule
    public ActivityScenarioRule<AdminDashboardActivity> activityScenarioRule =
            new ActivityScenarioRule<>(AdminDashboardActivity.class);
    @Test
    public void testAdminDashboardUI() {
        // Ensure Admin Dashboard Activity is launched
        // Launch the AdminDashboardActivity with a test deviceId
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.frolic", "com.example.frolic.AdminDashboardActivity");
        intent.putExtra("deviceId", TEST_DEVICE_ID);

        // Launch the activity with the Intent
        ActivityScenario<AdminDashboardActivity> scenario = ActivityScenario.launch(intent);

        onView(withId(R.id.tvManageEvents)).check(matches(isDisplayed()));
        onView(withId(R.id.tvManageProfiles)).check(matches(isDisplayed()));
        onView(withId(R.id.tvManageQRCodes)).check(matches(isDisplayed()));
        onView(withId(R.id.tvManageFacilities)).check(matches(isDisplayed()));
    }

    @Test
    public void testManageEventsNavigation() {
        // Launch AdminDashboardActivity with a test deviceId
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.frolic", "com.example.frolic.AdminDashboardActivity");
        intent.putExtra("deviceId", TEST_DEVICE_ID);

        // Launch the activity with the Intent
        ActivityScenario<AdminDashboardActivity> scenario = ActivityScenario.launch(intent);

        // Perform click on the "Manage Events" TextView
        onView(withId(R.id.tvManageEvents)).perform(click());

        // After clicking, verify that AdminEventsActivity is launched by checking for the RecyclerView
        onView(withId(R.id.recyclerViewEvents)).check(matches(isDisplayed()));

    }

    @Test
    public void testManageProfilesNavigation() {
        // Launch AdminDashboardActivity with a test deviceId
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.frolic", "com.example.frolic.AdminDashboardActivity");
        intent.putExtra("deviceId", TEST_DEVICE_ID);

        // Launch the activity with the Intent
        ActivityScenario<AdminDashboardActivity> scenario = ActivityScenario.launch(intent);

        // Perform click on the "Manage Profiles" TextView
        onView(withId(R.id.tvManageProfiles)).perform(click());

        // After clicking, verify that AdminProfilesActivity is launched by checking for the RecyclerView
        onView(withId(R.id.rvProfiles)).check(matches(isDisplayed()));
    }

}
