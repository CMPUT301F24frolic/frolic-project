package com.example.frolic;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.notNullValue;
import static java.util.regex.Pattern.matches;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;

import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class EntrantSignUpActivityTest {
    @Rule
    public ActivityTestRule<EntrantSignUpActivity> activityRule =
            new ActivityTestRule<>(EntrantSignUpActivity.class, true, false);

    private Uri dummyImageUri;

    @Before
    public void setUp() {
        Intents.init();
        // Create a dummy Uri for image selection
        dummyImageUri = Uri.parse("android.resource://com.example.frolic/drawable/ic_profile");
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testUserInputDataIsSavedCorrectly() {
        // Launch the activity
        activityRule.launchActivity(new Intent());

        // Type in the user details
        onView(withId(R.id.etUserName)).perform(typeText("Test User"));
        onView(withId(R.id.etUserEmail)).perform(typeText("testuser@example.com"));
        onView(withId(R.id.etUserMobile)).perform(typeText("1234567890"));

        // Simulate saving changes
        onView(withId(R.id.btnSaveChanges)).perform(click());

        // Verify the intent to navigate to EntrantProfileActivity with the correct extras
        intended(hasComponent(EntrantProfileActivity.class.getName()));
        intended(hasExtra("name", "Test User"));
        intended(hasExtra("email", "testuser@example.com"));
        intended(hasExtra("phone", "1234567890"));
    }

    @Test
    public void testImageSelectorOpens() {
        // Launch the activity
        activityRule.launchActivity(new Intent());

        // Simulate clicking the "Add Profile Image" button
        onView(withId(R.id.btnAddProfileImage)).perform(click());

        // Verify that an intent to open image selector is triggered
        //intended(Intent.ACTION_GET_CONTENT);
    }

}
