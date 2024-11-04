package com.example.frolic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * ChosenEntrantsActivity displays a list of entrants who were selected in the lottery system
 * for a specific event. It retrieves the list of chosen entrant IDs from the intent and displays
 * each entrant's details in a RecyclerView. The count of chosen entrants is also dynamically
 * displayed at the top of the screen.
 */
public class ChosenEntrantsActivity extends AppCompatActivity {

    private TextView tvBack, tvTitle, tvChosenListCount;
    private RecyclerView rvEntrants;
    private EntrantsAdapter adapter;
    private ArrayList<String> chosenEntrantIds;

    /**
     * Initializes the activity, sets up the UI components, retrieves the chosen entrant IDs from
     * the intent, and displays them in a RecyclerView. Displays a message if no entrants are available.
     *
     * @param savedInstanceState the saved instance state for activity recreation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chosen_entrants_screen);

        Intent intent = getIntent();
        chosenEntrantIds = intent.getStringArrayListExtra("entrantList");

        tvBack = findViewById(R.id.tvBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvChosenListCount = findViewById(R.id.llChosenList).findViewById(R.id.tvCount);
        rvEntrants = findViewById(R.id.rvEntrants);

        rvEntrants.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntrantsAdapter(chosenEntrantIds);
        rvEntrants.setAdapter(adapter);

        updateCountDisplay();

        tvBack.setOnClickListener(v -> finish());

        if (chosenEntrantIds == null || chosenEntrantIds.isEmpty()) {
            Toast.makeText(this, "No entrants in the chosen list.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Updates the count display of chosen entrants in the UI. Sets the text to "0" if
     * the chosen entrant list is null.
     */
    private void updateCountDisplay() {
        if (chosenEntrantIds != null) {
            tvChosenListCount.setText(String.valueOf(chosenEntrantIds.size()));
        } else {
            tvChosenListCount.setText("0");
        }
    }
}
