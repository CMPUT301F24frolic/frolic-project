package com.example.frolic;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MapViewActivityTest {

    private static final String MOCK_EVENT_ID = "event123";

    @Test
    public void testBackButtonClosesActivity() {
        // Create the Intent to start MapViewActivity with a mock eventId
        Intent intent = new Intent();
        intent.putExtra("eventId", MOCK_EVENT_ID);

        // Launch the MapViewActivity using ActivityScenario
        try (ActivityScenario<MapViewActivity> scenario = ActivityScenario.launch(intent)) {
            // Simulate a click on the back button
            onView(withId(R.id.tvBack)).perform(click());

            // Verify the activity is finished (i.e., the MapViewActivity should be closed)
            scenario.onActivity(activity -> {
                assert(activity.isFinishing());
            });
        }
    }
}
