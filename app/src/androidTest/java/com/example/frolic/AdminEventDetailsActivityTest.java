package com.example.frolic;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import com.example.frolic.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminEventDetailsActivityTest {
    private ActivityScenario<AdminEventDetailsActivity> scenario;
    @Rule
    public ActivityScenarioRule<AdminEventDetailsActivity> activityRule =
            new ActivityScenarioRule<>(AdminEventDetailsActivity.class);

    @Rule
    public GrantPermissionRule grantPermissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.INTERNET);

    @Before
    public void setUp() {
        Intents.init();
        // Create an Intent with the required eventId
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AdminEventDetailsActivity.class);
        intent.putExtra("eventId", "sampleEventId"); // Replace "sampleEventId" with a valid test ID

        // Launch activity with the intent
        scenario = ActivityScenario.launch(intent);
    }

    @After
    public void tearDown() {
        Intents.release();
        scenario.close();
    }

    @Test
    public void testLoadEventDetails() {
        // Check if title, date, and description TextViews are displayed.
        Espresso.onView(ViewMatchers.withId(R.id.tvTitle))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.tvEventDate))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.tvEventDescription))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testBackButtonNavigation() {
        // Perform click action on the back button and verify the activity is closed.
        Espresso.onView(ViewMatchers.withId(R.id.btnBack))
                .perform(ViewActions.click());

        // Verify the activity has finished by checking if it's no longer displayed.
        Espresso.onView(ViewMatchers.withId(R.id.tvTitle))
                .check(ViewAssertions.doesNotExist());
    }

    @Test
    public void testDeleteButtonShowsConfirmationDialog() {
        // Click the delete button to check if the confirmation dialog appears.
        Espresso.onView(ViewMatchers.withId(R.id.btnDeleteEvent))
                .perform(ViewActions.click());

        // Check for dialog title and buttons to ensure confirmation dialog is shown.
        Espresso.onView(ViewMatchers.withText("Delete Event"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withText("Are you sure you want to delete this event?"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withText("Delete"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withText("Cancel"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testDeleteButtonCancelsAndProceeds() {
        // Click delete button to trigger dialog and then click "Cancel" to close it.
        Espresso.onView(ViewMatchers.withId(R.id.btnDeleteEvent))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withText("Cancel"))
                .perform(ViewActions.click());

        // Verify the dialog is dismissed by ensuring "Delete" is not visible.
        Espresso.onView(ViewMatchers.withText("Delete"))
                .check(ViewAssertions.doesNotExist());
    }

    @Test
    public void testConfirmDeleteAction() {
        // Click delete button and then click "Delete" in the confirmation dialog.
        Espresso.onView(ViewMatchers.withId(R.id.btnDeleteEvent))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withText("Delete"))
                .perform(ViewActions.click());

        // Check if the activity finishes after deletion.
        Espresso.onView(ViewMatchers.withId(R.id.tvTitle))
                .check(ViewAssertions.doesNotExist());
    }
}
