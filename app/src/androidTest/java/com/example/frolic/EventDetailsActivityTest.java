package com.example.frolic;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EventDetailsActivityTest {

    // Grant location permissions for the activity
    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
    );

    private Intent intent;

    @Before
    public void setup() {
        // Create a mock intent with extras
        intent = new Intent();
        intent.putExtra("eventId", "mockEventId");
        intent.putExtra("eventName", "Mock Event");
        intent.putExtra("organizerId", "mockOrganizerId");
        intent.putExtra("deviceId", "mockDeviceId");
    }


    @Test
    public void testWaitButtonFunctionality() {
        // Launch the activity with the intent
        try (ActivityScenario<EventDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            // Check if the wait button is displayed and clickable
            onView(withId(R.id.btnWait))
                    .check(matches(isDisplayed()));

            // Simulate a click action (no actual logic validation here)
            onView(withId(R.id.btnWait)).perform(click());
        }
    }

    @Test
    public void testImageContainerVisibility() {
        // Launch the activity with the intent
        try (ActivityScenario<EventDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            // Check if the image container is not visible initially
            onView(withId(R.id.flEventImageContainer))
                    .check(matches(isDisplayed()));
        }
    }

    @Test
    public void testBackButtonFunctionality() {
        // Launch the activity with the intent
        try (ActivityScenario<EventDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            // Check if the back button is displayed
            onView(withId(R.id.btnBack))
                    .check(matches(isDisplayed()));

            // Simulate a click action on the back button
            onView(withId(R.id.btnBack)).perform(click());

        }
    }
}

