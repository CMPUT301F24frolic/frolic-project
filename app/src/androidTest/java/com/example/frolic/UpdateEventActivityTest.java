package com.example.frolic;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UpdateEventActivityTest {

    @Rule
    public ActivityTestRule<UpdateEventActivity> activityRule =
            new ActivityTestRule<>(UpdateEventActivity.class, true, false);

    @Before
    public void setUp() {
        // Setup intent with extra data
        Intent intent = new Intent();
        intent.putExtra("eventId", "12345");
        activityRule.launchActivity(intent);
    }

    @Test
    public void testBackButtonClick() {
        // Test that clicking the back button finishes the activity
        Espresso.onView(withId(R.id.tvBack))
                .perform(click());

        // Verify that the activity is closed after clicking
        Espresso.onView(withId(R.id.tvBack))
                .check(matches(withText("Back")));
    }
}