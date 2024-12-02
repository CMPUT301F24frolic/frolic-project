package com.example.frolic;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;



import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class QRScanActivityTest {

    @Rule
    public GrantPermissionRule cameraPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testQRCodeGenerationAndDisplay() {
        // Prepare mock event data
        String mockEventData = "mock_event_data";
        Bitmap generatedQRCode = QRCodeGenerator.generateQRCode(mockEventData);

        // Ensure the QR code bitmap is not null
        assert generatedQRCode != null : "QR Code generation failed.";

        // Launch activity with intent containing mock event data
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.putExtra("eventData", mockEventData);
        ActivityScenario<QRScanActivity> scenario = ActivityScenario.launch(intent);

        // Verify that the QR code is displayed
        onView(withId(R.id.ivQRCode)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackButtonNavigatesToDashboard() {
        // Launch activity
        ActivityScenario<QRScanActivity> scenario = ActivityScenario.launch(QRScanActivity.class);

        // Click the back button
        onView(withId(R.id.btnBack)).perform(click());

        // Verify navigation to EntrantDashboardActivity
        intended(hasComponent(EntrantDashboardActivity.class.getName()));
    }

    @Test
    public void testQRCodeScanStartsScanner() {
        // Launch activity
        ActivityScenario<QRScanActivity> scenario = ActivityScenario.launch(QRScanActivity.class);

        // Verify that clicking the scan button initiates QR scanning
        onView(withId(R.id.btnScanQRCode)).perform(click());

        onView(withText("Scan a QR code")).check(matches(isDisplayed()));
    }

}
