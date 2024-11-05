package com.example.frolic;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity to display a map view related to a specific event. Allows users to view the map
 * associated with an event and includes a back button to return to the previous screen.
 */
public class MapViewActivity extends AppCompatActivity {

    private String eventId;

    /**
     * Called when the activity is first created. Initializes the view, retrieves the event ID
     * from the intent, and sets up the back button functionality.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_of_map_screen);

        eventId = getIntent().getStringExtra("eventId");

        TextView tvBack = findViewById(R.id.tvBack);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
