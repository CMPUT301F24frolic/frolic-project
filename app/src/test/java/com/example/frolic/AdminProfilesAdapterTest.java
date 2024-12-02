package com.example.frolic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


class AdminProfilesAdapterTest {

    private ArrayList<Identity> testProfiles;
    private AdminProfilesAdapter.OnProfileActionListener mockListener;
    private AdminProfilesAdapter adapter;

    @BeforeEach
    void setUp() {
        // Prepare test data
        testProfiles = new ArrayList<>();
        // Using constructor with all parameters
        testProfiles.add(new Identity("device123", "John Doe", "john.doe@example.com", "ADMIN"));
        // Using constructor without phone number
        testProfiles.add(new Identity("device132", "John Allen", "john.allen@example.com", "ADMIN"));
        // Using constructor with only device ID
        testProfiles.add(new Identity("3"));

        // Initialize the adapter
        adapter = new AdminProfilesAdapter(new ArrayList<>(testProfiles), mockListener, null); // Context is null in unit tests
    }

    @Test
    void testAdapterInitialization() {
        // Verify the adapter initializes with the correct number of profiles
        assertNotNull(adapter);
        assertEquals(3, adapter.getItemCount());
    }

    @Test
    void testBindViewHolder() {
        // Simulate binding a view holder
        AdminProfilesAdapter.ViewHolder viewHolder = adapter.onCreateViewHolder(null, 0);
        adapter.onBindViewHolder(viewHolder, 0);

        // Verify data binding for the first profile
        assertEquals("Alice Johnson", viewHolder.tvEntrantName.getText().toString());
        assertEquals("alice@example.com", viewHolder.tvEntrantEmail.getText().toString());
    }


    @Test
    void testHandleEmptyNameAndEmail() {
        // Add a profile with missing name and email
        ArrayList<Identity> profilesWithMissingData = new ArrayList<>();
        profilesWithMissingData.add(new Identity("5")); // Only device ID, others default
        adapter.updateProfiles(new ArrayList<>(profilesWithMissingData));

        // Simulate binding the view holder for the new profile
        AdminProfilesAdapter.ViewHolder viewHolder = adapter.onCreateViewHolder(null, 0);
        adapter.onBindViewHolder(viewHolder, 0);

        // Verify default values for missing name and email
        assertEquals("No Name", viewHolder.tvEntrantName.getText().toString());
        assertEquals("No Email", viewHolder.tvEntrantEmail.getText().toString());
    }

    @Test
    void testMultipleConstructors() {
        // Verify profiles created with different constructors
        Identity profile1 = new Identity("6");
        assertEquals("ENTRANT", profile1.getRole());
        assertEquals(0, profile1.getPhoneNumber());
        assertEquals("", profile1.getName());
        assertEquals("", profile1.getEmail());

        Identity profile2 = new Identity("7", "Diana Prince", "diana@example.com", "Admin");
        assertEquals("Admin", profile2.getRole());
        assertEquals("Diana Prince", profile2.getName());
        assertEquals("diana@example.com", profile2.getEmail());
        assertEquals(0, profile2.getPhoneNumber());

        Identity profile3 = new Identity("8", "Clark Kent", "clark@example.com", "User", 999888777);
        assertEquals("User", profile3.getRole());
        assertEquals("Clark Kent", profile3.getName());
        assertEquals("clark@example.com", profile3.getEmail());
        assertEquals(999888777, profile3.getPhoneNumber());
    }
}