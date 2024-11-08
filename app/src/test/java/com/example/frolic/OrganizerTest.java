package com.example.frolic;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

public class OrganizerTest {
    private Identity identity;
    private Organizer organizer;

    @BeforeEach
    public void setUp() {
        // Create an Identity object for testing purposes
        identity = new Identity("12345", "John Doe", "john.doe@example.com", "ORGANIZER", 1234567890);
        // Initialize Organizer object with Identity
        organizer = new Organizer(identity);
    }

    @Test
    public void testOrganizerConstructor() {
        // Verify that the Organizer constructor sets the Identity and initializes an empty events list
        assertNotNull(organizer);
        assertEquals("12345", organizer.getOrganizerId());  // Check if organizer ID is set from identity's device ID
        assertNotNull(organizer.getEventsOrganizing());
        assertTrue(organizer.getEventsOrganizing().isEmpty());  // Verify the eventsOrganizing list is empty initially
    }

    @Test
    public void testOrganizeEvent() {
        // Test the method that adds event IDs to the eventsOrganizing list
        organizer.organizeEvent("event1");
        organizer.organizeEvent("event2");

        ArrayList<String> events = organizer.getEventsOrganizing();
        assertEquals(2, events.size());  // Verify the list has two events
        assertTrue(events.contains("event1"));  // Verify that event1 was added
        assertTrue(events.contains("event2"));  // Verify that event2 was added
    }

    @Test
    public void testGetOrganizerId() {
        // Test the getter for organizer ID (device ID from identity)
        assertEquals("12345", organizer.getOrganizerId());
    }

    @Test
    public void testSetEventsOrganizing() {
        // Test setting a custom list of events
        ArrayList<String> newEvents = new ArrayList<>();
        newEvents.add("event3");
        newEvents.add("event4");
        organizer.setEventsOrganizing(newEvents);

        ArrayList<String> events = organizer.getEventsOrganizing();
        assertEquals(2, events.size());
        assertTrue(events.contains("event3"));
        assertTrue(events.contains("event4"));
    }

    @Test
    public void testSetAndGetFacility() {
        // Create an Organizer for the Facility
        Identity identity = new Identity("deviceID", "Organizer Name", "email@example.com", "ORGANIZER");
        Organizer organizer = new Organizer(identity);

        // Create a Facility associated with the organizer
        Facility facility = new Facility(organizer, "Facility1", "Location1");

        // Set the facility for the organizer
        organizer.setFacility(facility);

        // Test that the facility is correctly set
        assertNotNull(organizer.getFacility());  // Ensure the facility is not null
        assertEquals("Facility1", organizer.getFacility().getName());  // Check the facility's name
        assertEquals("Location1", organizer.getFacility().getAddress());  // Check the facility's address
    }

    @Test
    public void testGetIdentity() {
        // Test getting the identity associated with the organizer
        assertEquals(identity, organizer.getIdentity());
    }

    @Test
    public void testSetIdentity() {
        // Test setting a new identity for the organizer
        Identity newIdentity = new Identity("67890", "Jane Doe", "jane.doe@example.com", "ORGANIZER", 987654321);
        organizer.setIdentity(newIdentity);

        assertEquals(newIdentity, organizer.getIdentity());
        assertEquals("67890", organizer.getOrganizerId());
    }

    @Test
    public void testEmptyFacilityByDefault() {
        // Verify that the facility is null by default
        assertNull(organizer.getFacility());
    }
}
