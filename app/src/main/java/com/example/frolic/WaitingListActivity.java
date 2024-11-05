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
 * Activity to display the waiting list of entrants for an event.
 * Shows the list of entrants who are on the waiting list, with a count
 * of total entrants. Allows navigation back to the previous screen.
 */
public class WaitingListActivity extends AppCompatActivity {

    private TextView tvBack, tvTitle, tvWaitingListCount;
    private RecyclerView rvEntrants;
    private EntrantsAdapter adapter;
    private ArrayList<String> waitingListEntrantIds;

    /**
     * Initializes the activity, retrieves the list of waiting entrants from the intent,
     * sets up the views, and configures the RecyclerView to display the list of waiting entrants.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.waiting_list_screen);

        Intent intent = getIntent();
        waitingListEntrantIds = intent.getStringArrayListExtra("entrantList");

        tvBack = findViewById(R.id.tvBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvWaitingListCount = findViewById(R.id.llWaitingList).findViewById(R.id.tvCount);
        rvEntrants = findViewById(R.id.rvEntrants);

        rvEntrants.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntrantsAdapter(waitingListEntrantIds);
        rvEntrants.setAdapter(adapter);

        updateCountDisplay();

        tvBack.setOnClickListener(v -> finish());

        if (waitingListEntrantIds == null || waitingListEntrantIds.isEmpty()) {
            Toast.makeText(this, "No entrants in the waiting list.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Updates the display of the waiting list count based on the size of waitingListEntrantIds.
     * Sets the count to 0 if the list is null or empty.
     */
    private void updateCountDisplay() {
        if (waitingListEntrantIds != null) {
            tvWaitingListCount.setText(String.valueOf(waitingListEntrantIds.size()));
        } else {
            tvWaitingListCount.setText("0");
        }
    }
}
