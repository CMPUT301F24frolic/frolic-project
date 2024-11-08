package com.example.frolic;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class EntrantsAdapterTest {
    private EntrantsAdapter adapter;
    private ArrayList<String> mockEntrantIds;
    private Context context;

    @Before
    public void setUp() {
        // Mock entrant IDs
        mockEntrantIds = new ArrayList<>();
        mockEntrantIds.add("entrant1");
        mockEntrantIds.add("entrant2");

        Context context = ApplicationProvider.getApplicationContext();
        adapter = new EntrantsAdapter(mockEntrantIds);
    }

    @Test
    public void testGetItemCount() {
        // Verify that the adapter returns the correct item count
        assertEquals(2, adapter.getItemCount());
    }

    @Test
    public void testViewHolderBinding() {
        // Simulate onBindViewHolder and verify the name and email views
        RecyclerView.ViewHolder viewHolder = adapter.onCreateViewHolder(new RecyclerView(ApplicationProvider.getApplicationContext()), 0);
        adapter.onBindViewHolder((EntrantsAdapter.EntrantViewHolder) viewHolder, 0);

        View itemView = viewHolder.itemView;
        TextView nameView = itemView.findViewById(R.id.tvEntrantName);
        TextView emailView = itemView.findViewById(R.id.tvEntrantEmail);

        // Ensure the TextViews are properly updated
        assertEquals("Unknown", nameView.getText().toString());
        assertEquals("No email available", emailView.getText().toString());
    }
}
