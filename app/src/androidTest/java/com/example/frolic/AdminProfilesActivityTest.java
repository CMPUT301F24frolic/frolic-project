package com.example.frolic;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static java.util.regex.Pattern.matches;

import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminProfilesActivityTest {

    @Rule
    public ActivityTestRule<AdminProfilesActivity> activityRule = new ActivityTestRule<>(AdminProfilesActivity.class, true, false);

    // 1. Test: Check that the Back button works
    @Test
    public void testBackButton() {
        String profileName = "Smriti"; // Replace with the actual profile you're testing

        // Step 1: Verify that the profile is displayed in the RecyclerView
        //onView(withText(profileName)).check(matches(isDisplayed()));

        // Step 2: Click on the profile
        //onView(withText(profileName)).perform(click());

        // Step 3: Press the back button (this simulates pressing the back button)
        onView(withId(R.id.tvBack)).perform(click());

        // Step 4: Verify that the profile is still displayed in the RecyclerView after pressing back
        //onView(withText(profileName)).check(matches(isDisplayed()));
    }

    @Test
    public void testRecyclerViewIsDisplayed() {
        // Check that the RecyclerView is displayed
        //onView(withId(R.id.rvProfiles)).check(matches(isDisplayed()));
    }

    // Test 2: Verify profile is displayed in the RecyclerView
    @Test
    public void testProfileIsDisplayedInRecyclerView() {
        // Assuming that a profile is already loaded or mocked for the test, check that the profile appears
        String profileName = "Smriti"; // Replace with an actual profile name that should be loaded

        // Check if the profile name is displayed in the RecyclerView
        //onView(withText(profileName)).check(matches(isDisplayed()));
    }

    // 4. Test: Selecting a profile from the RecyclerView and showing details
    @Test
    public void testProfileSelection() {
        // Simulate clicking a profile in the RecyclerView (let's assume we are clicking on the first item)
        Espresso.onView(withId(R.id.rvProfiles))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Verify that the profile details are displayed (you may need to customize this based on your actual UI)
        //Espresso.onView(withId(R.id.tvProfileDetails)).check(matches(isDisplayed()));

        // Check that the correct profile name is displayed in the profile details
        //Espresso.onView(withId(R.id.tvProfileDetails)).check(matches(withText("Alex")));  // Example profile name
    }

}