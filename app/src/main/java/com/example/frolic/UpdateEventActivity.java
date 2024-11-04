package com.example.frolic;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
// TODO: Needs to be implemented. Need to ask TA about what is allowed to be updated
// in an event. In the user stories it only mentions the event poster.
public class UpdateEventActivity extends AppCompatActivity {

    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_info_screen);

        // Retrieve eventId from intent
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
