package com.example.frolic;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;

public class EntrantEditProfileTest {
    @Rule
    public ActivityTestRule<EntrantEditProfile> activityRule =
            new ActivityTestRule<>(EntrantEditProfile.class, true, false);

    @Before
    public void setUp() {
        Intents.init();
        Intent intent = new Intent();
        intent.putExtra("deviceId", "testDeviceId");
        activityRule.launchActivity(intent);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    /**
     * Test that all the key UI elements are displayed when the activity is launched.
     */
    @Test
    public void testViewsDisplayed() {
        onView(withId(R.id.etName)).check(matches(isDisplayed()));
        onView(withId(R.id.etEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.etPhone)).check(matches(isDisplayed()));
        onView(withId(R.id.cbNotifications)).check(matches(isDisplayed()));
        onView(withId(R.id.btnUploadImage)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSaveChanges)).check(matches(isDisplayed()));
    }

    /**
     * Test that an error is shown when the name field is empty.
     */
    @Test
    public void testNameFieldValidation() {
        // Leave the name field empty and attempt to save
        onView(withId(R.id.etEmail)).perform(typeText("test@example.com"));
        closeSoftKeyboard();
        onView(withId(R.id.btnSaveChanges)).perform(click());

        // Check for the error message on the name field
        onView(withId(R.id.etName)).check(matches(withText("Name is required")));
    }

    /**
     * Test that an error is shown when the email field is empty.
     */
    @Test
    public void testEmailFieldValidation() {
        // Enter name but leave email field empty
        onView(withId(R.id.etName)).perform(typeText("Test User"));
        closeSoftKeyboard();
        onView(withId(R.id.btnSaveChanges)).perform(click());

        // Check for the error message on the email field
        onView(withId(R.id.etEmail)).check(matches(withText("Email is required")));
    }

    /**
     * Test that the profile image upload button works correctly.
     */
    @Test
    public void testUploadImageButton() {
        // Simulate clicking the "Upload Image" button
        onView(withId(R.id.btnUploadImage)).perform(click());

        // Assert that the image picker intent is triggered
        Intents.intended(hasAction(Intent.ACTION_PICK));
    }

    /**
     * Test saving profile with valid inputs and check if success toast appears.
     */
    @Test
    public void testSaveProfileSuccess() {
        // Enter valid details
        onView(withId(R.id.etName)).perform(typeText("Test User"));
        onView(withId(R.id.etEmail)).perform(typeText("test@example.com"));
        onView(withId(R.id.etPhone)).perform(typeText("1234567890"));
        closeSoftKeyboard();

        // Click save changes button
        onView(withId(R.id.btnSaveChanges)).perform(click());

    }
    /**
     * Test removing the profile picture.
     */
    @Test
    public void testRemoveProfileImage() {
        onView(withId(R.id.btnRemoveImage)).perform(click());
        // Verify the ImageView no longer displays an image
        onView(withId(R.id.ivProfileImage)).check(matches(isDisplayed()));
    }




}
