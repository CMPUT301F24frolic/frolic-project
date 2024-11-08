package com.example.frolic;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.net.Uri;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EntrantProfileActivityTest {
    @Rule
    public ActivityTestRule<EntrantProfileActivity> activityRule =
            new ActivityTestRule<>(EntrantProfileActivity.class, true, false);

    private Intent createTestIntent() {
        // Create a test intent with sample data
        Intent intent = new Intent();
        intent.putExtra("name", "Test User");
        intent.putExtra("email", "testuser@example.com");
        intent.putExtra("phone", "1234567890");
        intent.putExtra("profileImageUri", Uri.parse("android.resource://com.example.frolic/drawable/ic_profile").toString());
        intent.putExtra("deviceId", "testDeviceId");
        return intent;
    }

    @Before
    public void setUp() {
        // Initialize Espresso Intents before each test
        Intents.init();
    }

    @After
    public void tearDown() {
        // Release Espresso Intents after each test
        Intents.release();
    }

    @Test
    public void testProfileDisplay() {
        // Launch the activity with a test intent
        activityRule.launchActivity(createTestIntent());

        // Check if the name, email, and phone are displayed correctly
        onView(withId(R.id.tvUserName)).check(matches(withText("Test User")));
        onView(withId(R.id.tvUserEmail)).check(matches(withText("testuser@example.com")));
        onView(withId(R.id.tvUserMobile)).check(matches(withText("1234567890")));
    }

    @Test
    public void testEditProfileButton() {
        // Launch the activity with a test intent
        activityRule.launchActivity(createTestIntent());

        // Perform a click on the edit profile button
        onView(withId(R.id.btnEditProfile)).perform(click());

        // Verify that the intent to start EntrantEditProfile is triggered
        Intents.intended(IntentMatchers.hasComponent(EntrantEditProfile.class.getName()));
        Intents.intended(IntentMatchers.hasExtra("deviceId", "testDeviceId"));
    }

    @Test
    public void testViewEventsButton() {
        // Launch the activity with a test intent
        activityRule.launchActivity(createTestIntent());

        // Perform a click on the view events button
        onView(withId(R.id.btnViewEvent)).perform(click());

        // Check if it goes to the appropriate page or triggers the correct action
        // Add further checks here based on your view events page logic
    }
}
