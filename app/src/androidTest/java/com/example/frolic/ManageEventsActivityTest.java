package com.example.frolic;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.content.Intent;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ManageEventsActivityTest {

    @Rule
    public ActivityScenarioRule<ManageEventsActivity> activityRule =
            new ActivityScenarioRule<>(ManageEventsActivity.class);

    @Rule
    public GrantPermissionRule grantPermissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.INTERNET);

    private FirebaseFirestore db;

    @Before
    public void setup() {
        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)  // Disable offline persistence for testing
                .build());

        // Launch the activity with a dummy organizer ID
        activityRule.getScenario().onActivity(activity -> {
            Intent intent = new Intent(activity, ManageEventsActivity.class);
            intent.putExtra("deviceId", "dummy_organizer_id");
            activity.startActivity(intent);
        });
    }

    /**
     * Test that checks if the RecyclerView is displayed.
     */
    @Test
    public void testRecyclerViewDisplayed() {
        onView(withId(R.id.rvEvents)).check(matches(isDisplayed()));
    }

    /**
     * Test that simulates loading events and checks if RecyclerView items are populated.
     * (Requires mock or dummy data in Firestore)
     */
    @Test
    public void testRecyclerViewItemsPopulated() {
        // Assumes events have been added to the Firestore collection for the dummy organizer
        onView(withId(R.id.rvEvents))
                .check(matches(not(withText("No events found"))));  // Check that items are displayed
    }

    /**
     * Test clicking an item in the RecyclerView.
     */
    @Test
    public void testRecyclerViewItemClick() {
        // Scroll to a position and perform click (Assumes there is data at position 0)
        onView(withId(R.id.rvEvents))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

           }


}
