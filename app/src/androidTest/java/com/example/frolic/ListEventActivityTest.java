package com.example.frolic;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ListEventActivityTest {

    @Rule
    public ActivityScenarioRule<ListEventActivity> activityRule =
            new ActivityScenarioRule<>(ListEventActivity.class);

    @Before
    public void setUp() {
        ActivityScenario<ListEventActivity> scenario = activityRule.getScenario();
        scenario.onActivity(activity -> {
            Intent intent = new Intent();
            intent.putExtra("deviceId", "testOrganizerId");
            intent.putExtra("facilityId", "testFacilityId");
            activity.setIntent(intent);
        });
    }

    @Test
    public void testInitialViewElements() {
        // Verify all required fields and buttons are displayed
        onView(withId(R.id.etEventName)).check(matches(isDisplayed()));
        onView(withId(R.id.etEventDate)).check(matches(isDisplayed()));
        onView(withId(R.id.etLastDateRegistration)).check(matches(isDisplayed()));
        onView(withId(R.id.etVacancy)).check(matches(isDisplayed()));
        onView(withId(R.id.cbGeolocationRequired)).check(matches(isDisplayed()));
        onView(withId(R.id.cbNotification)).check(matches(isDisplayed()));
        onView(withId(R.id.btnListEvent)).check(matches(isDisplayed()));
        onView(withId(R.id.btnListEvent)).check(matches(isEnabled()));
    }


    @Test
    public void testEnableButtonWhenFieldsAreFilled() {
        // Fill out all required fields
        onView(withId(R.id.etEventName)).perform(replaceText("Sample Event"), closeSoftKeyboard());
        onView(withId(R.id.etEventDate)).perform(click());
        onView(withText("OK")).perform(click()); // Assume a date is selected
        onView(withId(R.id.etLastDateRegistration)).perform(click());
        onView(withText("OK")).perform(click()); // Assume a date is selected
        onView(withId(R.id.etVacancy)).perform(replaceText("10"), closeSoftKeyboard());

        // Verify that the List Event button is enabled
        onView(withId(R.id.btnListEvent)).check(matches(isEnabled()));
    }

    @Test
    public void testEventCreation() {
        // Fill out all fields to simulate event creation
        onView(withId(R.id.etEventName)).perform(replaceText("Sample Event"), closeSoftKeyboard());
        onView(withId(R.id.etEventDate)).perform(click());
        onView(withText("OK")).perform(click()); // Select a date for the event
        onView(withId(R.id.etLastDateRegistration)).perform(click());
        onView(withText("OK")).perform(click()); // Select a date for registration
        onView(withId(R.id.etVacancy)).perform(replaceText("10"), closeSoftKeyboard());
        onView(withId(R.id.etWaitlistLimit)).perform(replaceText("5"), closeSoftKeyboard());
        onView(withId(R.id.cbGeolocationRequired)).perform(click()); // Check geolocation
        onView(withId(R.id.cbNotification)).perform(click()); // Enable notification

        // Click the List Event button
        onView(withId(R.id.btnListEvent)).perform(click());

            }

    @Test
    public void testBackButtonNavigation() {
        // Simulate clicking the close button
        onView(withId(R.id.ivClose)).perform(click());

        // Verify that the Organizer Dashboard Activity is displayed
        onView(withText("Organizer Dashboard")).check(matches(isDisplayed()));
    }
}

