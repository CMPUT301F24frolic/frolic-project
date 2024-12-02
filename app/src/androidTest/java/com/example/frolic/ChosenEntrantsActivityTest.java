package com.example.frolic;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class ChosenEntrantsActivityTest {

    @Rule
    public ActivityTestRule<ChosenEntrantsActivity> activityRule =
            new ActivityTestRule<>(ChosenEntrantsActivity.class, true, false);

    @Test
    public void testRecyclerViewDisplaysEntrants() {
        // Set up intent with a list of chosen entrants
        Intent intent = new Intent();
        ArrayList<String> entrantList = new ArrayList<>();
        entrantList.add("Entrant 1");
        entrantList.add("Entrant 2");
        entrantList.add("Entrant 3");
        intent.putStringArrayListExtra("entrantList", entrantList);

        // Launch the activity with the intent
        activityRule.launchActivity(intent);

        // Check that RecyclerView has at least 3 items
        onView(withId(R.id.rvEntrants))
                .check(matches(hasMinimumChildCount(3)));  // 3 entrants

        // Check if the count display is correct
        onView(withId(R.id.tvCount))
                .check(matches(withText("3")));  // Should show 3 entrants
    }

    @Test
    public void testEmptyEntrantListShowsMessageAndCount() {
        // Set up intent with an empty list of chosen entrants
        Intent intent = new Intent();
        intent.putStringArrayListExtra("entrantList", new ArrayList<>());  // Empty list

        // Launch the activity with the intent
        activityRule.launchActivity(intent);

        // Check that the count display shows 0
        onView(withId(R.id.tvCount))
                .check(matches(withText("0")));

    }
}

