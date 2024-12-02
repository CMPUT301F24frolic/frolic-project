package com.example.frolic;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EntrantDashboardActivityTest {
    @Rule
    public ActivityTestRule<EntrantDashboardActivity> activityRule =
            new ActivityTestRule<>(EntrantDashboardActivity.class);

    @Before
    public void setUp() {
        Intents.init();  // Initialize Espresso Intents
    }

    @After
    public void tearDown() {
        Intents.release();  // Clean up after each test
    }

    @Test
    public void testViewEventsNavigation() {
        onView(withId(R.id.tvViewEvents)).perform(click());
        intended(hasComponent(EventsListActivity.class.getName()));
    }

    @Test
    public void testMyEventsNavigation() {
        onView(withId(R.id.tvMyEvents)).perform(click());
        intended(hasComponent(EventDetailsActivity.class.getName()));
    }

    @Test
    public void testProfileNavigation() {
        onView(withId(R.id.tvProfile)).perform(click());
        intended(hasComponent(EntrantEditProfile.class.getName()));
    }

    @Test
    public void testScanQRNavigation() {
        onView(withId(R.id.btnScanQR)).perform(click());
        intended(hasComponent(QRScanActivity.class.getName()));
    }

}

