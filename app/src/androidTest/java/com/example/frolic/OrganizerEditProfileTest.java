package com.example.frolic;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OrganizerEditProfileTest {

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES);

    private FirebaseFirestore db;

    @Before
    public void setUp() {
        db = FirebaseFirestore.getInstance();
        ActivityScenario.launch(OrganizerEditProfile.class);
    }

    @Test
    public void testUIElementsDisplayed() {
        onView(withId(R.id.ivProfileImage)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.etName)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.etEmail)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.etPhone)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.cbNotifications)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.btnUploadImage)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.btnSaveProfile)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.etFacilityName)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.etFacilityAddress)).check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testProfileDataLoading() {
        onView(withId(R.id.etName)).check(matches(withText("Sample Name")));
        onView(withId(R.id.etEmail)).check(matches(withText("sample@example.com")));
    }

    @Test
    public void testSaveProfileWithValidInput() {
        onView(withId(R.id.etName)).perform(typeText("John Doe"), closeSoftKeyboard());
        onView(withId(R.id.etEmail)).perform(typeText("john@example.com"), closeSoftKeyboard());
        onView(withId(R.id.etPhone)).perform(typeText("1234567890"), closeSoftKeyboard());
        onView(withId(R.id.etFacilityName)).perform(typeText("Facility A"), closeSoftKeyboard());
        onView(withId(R.id.etFacilityAddress)).perform(typeText("123 Main St"), closeSoftKeyboard());

        onView(withId(R.id.btnSaveProfile)).perform(click());


    }

    @Test
    public void testErrorForEmptyRequiredFields() {
        onView(withId(R.id.etName)).perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.etEmail)).perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.etFacilityName)).perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.etFacilityAddress)).perform(clearText(), closeSoftKeyboard());

        onView(withId(R.id.btnSaveProfile)).perform(click());

        // Check that error messages are displayed for required fields
        onView(withId(R.id.etName)).check(matches(hasErrorText("Name is required")));
        onView(withId(R.id.etEmail)).check(matches(hasErrorText("Email is required")));
        onView(withId(R.id.etFacilityName)).check(matches(hasErrorText("Facility name is required")));
        onView(withId(R.id.etFacilityAddress)).check(matches(hasErrorText("Facility address is required")));
    }

    @Test
    public void testImageUploadFunctionality() {
        Intents.init();

        onView(withId(R.id.btnUploadImage)).perform(click());

        // Check that image picker intent is fired
        intended(hasAction(Intent.ACTION_PICK));

        Intents.release();
    }
}
