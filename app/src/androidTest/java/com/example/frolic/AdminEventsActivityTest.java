package com.example.frolic;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminEventsActivityTest {
    @Rule
    public ActivityScenarioRule<AdminEventsActivity> activityScenarioRule =
            new ActivityScenarioRule<>(AdminEventsActivity.class);


    @Test
    public void testEventItemClick() {
        String eventName = "Test Event Alex"; // Replace with an actual event name if available

        // Launch AdminEventsActivity with intent and the required deviceId
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("eventId", "AhftcUiNoLK7jOSYHDIR"); // Set the device ID for testing
        ActivityScenario<AdminEventsActivity> scenario = ActivityScenario.launch(intent);

        // Assuming an event item is displayed, simulate clicking on an event
        onView(withText(eventName)).perform(click());

        // Verify that AdminEventDetailsActivity is launched
        onView(withId(R.id.tvTitle)) // Assuming this is a title in the details activity
                .check(matches(isDisplayed()));
    }
    @Test
    public void testBackButtonFunctionality() {
        // Launch AdminEventsActivity with the required deviceId
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("eventId", "02b7f87772d89350"); // Set the device ID for testing
        ActivityScenario<AdminEventsActivity> scenario = ActivityScenario.launch(intent);

        // Click on the back button in the AdminEventsActivity
        onView(withId(R.id.btnBack)).perform(click());

        // Ensure AdminDashboardActivity is displayed again
        onView(withId(R.id.tvManageEvents)).check(matches(isDisplayed()));
    }

}
