package com.example.frolic;
import android.content.Intent;
import android.view.View;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import com.google.firebase.firestore.FirebaseFirestore;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static java.util.regex.Pattern.matches;

@RunWith(AndroidJUnit4.class)
public class RoleSelectionActivityTest {
    @Rule
    public ActivityTestRule<RoleSelectionActivity> activityRule = new ActivityTestRule<>(RoleSelectionActivity.class, true, false);

    @Before
    public void setUp() {
        // Initialize Intents for checking navigation
        Intents.init();
    }

    /**
     * Test that clicking the Entrant button opens the EntrantEditProfile activity.
     */
    @Test
    public void testEntrantButtonOpensEntrantEditProfile() {
        // Launch RoleSelectionActivity with test deviceId
        Intent intent = new Intent();
        intent.putExtra("deviceId", "testDeviceId");
        activityRule.launchActivity(intent);

        // Click the Entrant button
        onView(withId(R.id.btnEntrant)).perform(click());

        // Verify that EntrantEditProfile activity was launched
        intended(IntentMatchers.hasComponent(EntrantEditProfile.class.getName()));
    }

    /**
     * Test that clicking the Organizer button opens the OrganizerEditProfile activity.
     */
    @Test
    public void testOrganizerButtonOpensOrganizerEditProfile() {
        // Launch RoleSelectionActivity with test deviceId
        Intent intent = new Intent();
        intent.putExtra("deviceId", "testDeviceId");
        activityRule.launchActivity(intent);

        // Click the Organizer button
        onView(withId(R.id.btnOrganizer)).perform(click());

        // Verify that OrganizerEditProfile activity was launched
        intended(IntentMatchers.hasComponent(OrganizerEditProfile.class.getName()));
    }

    /**
     * Test that Admin button is hidden initially (for non-admin users).
     */
    @Test
    public void testAdminButtonIsHiddenForNonAdmin() {
        // Launch RoleSelectionActivity with test deviceId
        Intent intent = new Intent();
        intent.putExtra("deviceId", "testDeviceId");
        activityRule.launchActivity(intent);

        // Verify that Admin button is hidden by default
        onView(withId(R.id.btnAdmin)).check(matches(not(isDisplayed())));
    }

    /**
     * Test clicking Admin button, assuming it's visible.
     * Note: In production, this should only work if the user is an admin.
     * Make btnAdmin visible in XML or manually show it if needed for this test.
     */
    @Test
    public void testAdminButtonOpensAdminDashboardActivity() {
        // Launch RoleSelectionActivity with test deviceId
        Intent intent = new Intent();
        intent.putExtra("deviceId", "adminDeviceId"); // Pretend this is an admin user
        activityRule.launchActivity(intent);

        // For testing purposes, assume the Admin button is visible
        onView(withId(R.id.btnAdmin)).perform(click());

        // Verify that AdminDashboardActivity was launched
        intended(IntentMatchers.hasComponent(AdminDashboardActivity.class.getName()));
    }
}
