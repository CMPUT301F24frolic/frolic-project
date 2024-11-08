package com.example.frolic;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerDashboardActivityTest {

    private static final String DEVICE_ID = "testDeviceId";
    private static final String FACILITY_ID = "testFacilityId";

    @Before
    public void setup() {
        Bundle bundle = new Bundle();
        bundle.putString("deviceId", DEVICE_ID);
        bundle.putString("facilityId", FACILITY_ID);

        Intent intent = new Intent();
        intent.putExtras(bundle);

        ActivityScenario.launch(OrganizerDashboardActivity.class);
    }

    @Test
    public void testCreateEventNavigation() {
        Espresso.onView(ViewMatchers.withId(R.id.tvCreateEvents)).perform(click());
        intended(hasComponent(ListEventActivity.class.getName()));
    }

    @Test
    public void testManageEventNavigation() {
        Espresso.onView(ViewMatchers.withId(R.id.tvManageEvents)).perform(click());
        intended(hasComponent(ManageEventsActivity.class.getName()));
    }

    @Test
    public void testProfileNavigation() {
        Espresso.onView(ViewMatchers.withId(R.id.tvMyProfile)).perform(click());
        intended(hasComponent(OrganizerEditProfile.class.getName()));
    }

    @Test
    public void testSwitchRoleDialogDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.action_switch_role)).perform(click());
        Espresso.onView(ViewMatchers.withText("Switch Role"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testDashboardTitleDisplayed() {
        Espresso.onView(ViewMatchers.withText("Organizer Dashboard"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
