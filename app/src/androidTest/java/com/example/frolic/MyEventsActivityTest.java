package com.example.frolic;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MyEventsActivityTest {

    @Rule
    public ActivityTestRule<MyEventsActivity> activityRule =
            new ActivityTestRule<>(MyEventsActivity.class, true, false);

    @Test
    public void testRecyclerViewDisplaysEventData() {
        // Set up intent with entrantId to launch the activity
        Intent intent = new Intent();
        intent.putExtra("entrantId", "12345");  // Example entrant ID
        activityRule.launchActivity(intent);

        // Check that RecyclerView has at least one item after loading events
        Espresso.onView(withId(R.id.recyclerViewMyEvents))
                .check(matches(hasMinimumChildCount(1)));  // Expect at least one event to be displayed
    }

    @Test
    public void testBackButtonClick() {
        // Launch the activity with any required data
        activityRule.launchActivity(new Intent());

        // Test if clicking the back button finishes the activity
        Espresso.onView(withId(R.id.btnBack))
                .perform(click());
    }

    @Test
    public void testRecyclerViewItemClickOpensEventDetails() {
        // Set up intent with entrantId
        Intent intent = new Intent();
        intent.putExtra("entrantId", "12345");
        activityRule.launchActivity(intent);

        // Check if the RecyclerView has items and click on the first item
        Espresso.onView(withId(R.id.recyclerViewMyEvents))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        Espresso.onView(withId(R.id.tvEventName)).check(matches(withText("Sample Event Name"))); // Example check
    }
}

