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

public class CanceledEntrantsActivity extends AppCompatActivity {

    private TextView tvBack, tvTitle, tvCanceledListCount;
    private RecyclerView rvEntrants;
    private EntrantsAdapter adapter;
    private ArrayList<String> canceledEntrantIds;

    /**
     * Called when the activity is first created. Sets up the RecyclerView, populates the
     * list of canceled entrants, and configures the UI.
     *
     * @param savedInstanceState the state of the activity as previously saved
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.canceled_entrants_screen);

        Intent intent = getIntent();
        canceledEntrantIds = intent.getStringArrayListExtra("entrantList");

        tvBack = findViewById(R.id.tvBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvCanceledListCount = findViewById(R.id.llCanceled).findViewById(R.id.tvCount);
        rvEntrants = findViewById(R.id.rvEntrants);

        // Set up RecyclerView with EntrantsAdapter
        rvEntrants.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntrantsAdapter(canceledEntrantIds);
        rvEntrants.setAdapter(adapter);

        // Update count
        updateCountDisplay();

        tvBack.setOnClickListener(v -> finish());

        // Display a message if there are no entrants in the canceled list
        if (canceledEntrantIds == null || canceledEntrantIds.isEmpty()) {
            Toast.makeText(this, "No entrants in the canceled list.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Updates the count display with the number of entrants in the canceled list.
     */
    private void updateCountDisplay() {
        if (canceledEntrantIds != null) {
            tvCanceledListCount.setText(String.valueOf(canceledEntrantIds.size()));
        } else {
            tvCanceledListCount.setText("0");
        }
    }
}
