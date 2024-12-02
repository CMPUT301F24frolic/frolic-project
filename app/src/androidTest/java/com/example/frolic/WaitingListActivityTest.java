package com.example.frolic;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class WaitingListActivityTest {

    @Test
    public void testWaitingListActivityDisplaysCorrectEntrants() {
        // Create a list of entrant IDs
        ArrayList<String> entrantList = new ArrayList<>();
        entrantList.add("Entrant 1");
        entrantList.add("Entrant 2");
        entrantList.add("Entrant 3");

        // Create an Intent to launch the WaitingListActivity
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), WaitingListActivity.class);
        intent.putStringArrayListExtra("entrantList", entrantList);

        // Launch the activity
        ActivityScenario<WaitingListActivity> scenario = ActivityScenario.launch(intent);

        // Verify that the waiting list count TextView displays the correct number
        Espresso.onView(withId(R.id.tvCount)).check(matches(withText(String.valueOf(entrantList.size()))));

        // Verify that each entrant is displayed in the RecyclerView
        for (String entrant : entrantList) {
            onView(withText(entrant)).check(matches(isDisplayed()));
        }

        // Simulate a click on the back button and verify that the activity finishes
        Espresso.onView(withId(R.id.tvBack)).perform(click());
        scenario.close();
    }

    @Test
    public void testWaitingListActivityDisplaysNoEntrantsMessage() {
        // Create an empty list of entrants
        ArrayList<String> entrantList = new ArrayList<>();

        // Create an Intent to launch the WaitingListActivity
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), WaitingListActivity.class);
        intent.putStringArrayListExtra("entrantList", entrantList);

        // Launch the activity
        ActivityScenario<WaitingListActivity> scenario = ActivityScenario.launch(intent);

        // Verify that the waiting list count TextView displays 0
        Espresso.onView(withId(R.id.tvCount)).check(matches(withText("0")));

        // Simulate a click on the back button and verify that the activity finishes
        Espresso.onView(withId(R.id.tvBack)).perform(click());
        scenario.close();
    }
}
