package com.example.frolic;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;

public class EntrantTest {
    private Identity testIdentity;
    private Entrant testEntrant;

    @BeforeEach
    public void setUp() {
        // Creating an identity for the Entrant, with a unique device ID
        testIdentity = new Identity("device123", "John Doe", "john.doe@example.com", "ENTRANT");
        testEntrant = new Entrant(testIdentity); // Initializing Entrant with the created identity
    }

    @Test
    public void testGetIdentity() {
        // Test the getIdentity method to ensure it returns the correct Identity
        assertEquals(testIdentity, testEntrant.getIdentity());
    }

    @Test
    public void testSetIdentity() {
        // Set a new identity and verify the setter works correctly
        Identity newIdentity = new Identity("device456", "Jane Doe", "jane.doe@example.com", "ENTRANT");
        testEntrant.setIdentity(newIdentity);
        assertEquals(newIdentity, testEntrant.getIdentity());
    }

    @Test
    public void testGetEventsEntered() {
        // Test the getEventsEntered method to ensure it returns the correct list of events
        assertNotNull(testEntrant.getEventsEntered());
        assertTrue(testEntrant.getEventsEntered().isEmpty());
    }

    @Test
    public void testSetEventsEntered() {
        // Test setting a list of events for the entrant
        Event testEvent = new Event("event123", "organizer123", "facility123", "Sample Event", 10, 5, new Date(), new Date(), true, true, "hash123");
        ArrayList<Event> events = new ArrayList<>();
        events.add(testEvent);
        testEntrant.setEventsEntered(events);

        assertEquals(1, testEntrant.getEventsEntered().size());
        assertEquals(testEvent, testEntrant.getEventsEntered().get(0));
    }

    @Test
    public void testAddEventToEntrant() {
        // Test adding an event to the entrant's event list
        Event testEvent = new Event("event123", "organizer123", "facility123", "Sample Event", 10, 5, new Date(), new Date(), true, true, "hash123");
        testEntrant.getEventsEntered().add(testEvent);

        assertEquals(1, testEntrant.getEventsEntered().size());
        assertEquals(testEvent, testEntrant.getEventsEntered().get(0));
    }

    @Test
    public void testRemoveEventFromEntrant() {
        // Test removing an event from the entrant's event list
        Event testEvent = new Event("event123", "organizer123", "facility123", "Sample Event", 10, 5, new Date(), new Date(), true, true, "hash123");
        testEntrant.getEventsEntered().add(testEvent);
        testEntrant.getEventsEntered().remove(testEvent);

        assertTrue(testEntrant.getEventsEntered().isEmpty());
    }

    @Test
    public void testGetEntrantDetails() {
        // Verifying if identity details are correctly returned.
        assertEquals("John Doe", testEntrant.getIdentity().getName());
        assertEquals("john.doe@example.com", testEntrant.getIdentity().getEmail());
        assertEquals("ENTRANT", testEntrant.getIdentity().getRole());
    }

    @Test
    public void testEntrantRole() {
        // Test role is set to "ENTRANT" by default
        assertEquals("ENTRANT", testEntrant.getIdentity().getRole());
    }
}
