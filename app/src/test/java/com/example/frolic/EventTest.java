package com.example.frolic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

public class EventTest {

    private Event event;
    private String eventId = "event123";
    private String organizerId = "organizer456";
    private String facilityId = "facility789";
    private String eventName = "Sample Event";
    private int maxConfirmed = 100;
    private int waitlistLimit = 50;
    private Date eventDate = new Date();  // Use the current date for eventDate
    private Date enrollDate = new Date(); // Use the current date for enrollDate
    private boolean geolocationRequired = true;
    private boolean receiveNotification = true;
    private String qrHash = "QRHASH123";
    private String eventImageUrl = "https://example.com/event-image.jpg"; // Adding image URL

    private Identity identity;
    private Entrant entrant;
    private Event event1;
    private Event event2;

    @BeforeEach
    public void setUp() {
        // Initialize Event object with the provided constructor
        event = new Event(eventId, organizerId, facilityId, eventName, maxConfirmed, waitlistLimit, eventDate, enrollDate, geolocationRequired, receiveNotification, qrHash, eventImageUrl);

        // Initialize additional required objects
        identity = new Identity("User123");
        entrant = new Entrant(identity);

        // Initialize more Event objects for the Entrant tests
        event1 = new Event("event001", "organizer1", "facility1", "Event 1", 100, 50, new Date(), new Date(), true, true, "QRHASH1", "https://example.com/event1-image.jpg");
        event2 = new Event("event002", "organizer2", "facility2", "Event 2", 100, 50, new Date(), new Date(), true, true, "QRHASH2", "https://example.com/event2-image.jpg");
    }

    @Test
    public void testConstructor() {
        // Verify all fields are initialized correctly
        assertEquals(eventId, event.getEventId());
        assertEquals(organizerId, event.getOrganizerId());
        assertEquals(facilityId, event.getFacilityId());
        assertEquals(eventName, event.getEventName());
        assertEquals(maxConfirmed, event.getMaxConfirmed());
        assertEquals(waitlistLimit, event.getWaitlistLimit());
        assertEquals(eventDate, event.getEventDate());
        assertEquals(enrollDate, event.getEnrollDate());
        assertTrue(event.isGeolocationRequired());
        assertTrue(event.isReceiveNotification());
        assertEquals(qrHash, event.getQrHash());
        assertEquals(eventImageUrl, event.getEventImageUrl()); // Verify eventImageUrl
    }

    @Test
    public void testSetAndGetIdentity() {
        Identity newIdentity = new Identity("New User");

        // Set a new identity and verify
        entrant.setIdentity(newIdentity);
        assertEquals(newIdentity, entrant.getIdentity());
    }

    @Test
    public void testSetAndGetEventsEntered() {
        ArrayList<Event> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);

        // Set eventsEntered list and verify it is correctly set
        entrant.setEventsEntered(events);
        assertEquals(events, entrant.getEventsEntered());
    }

    @Test
    public void testAddEventToEventsEntered() {
        // Add events to eventsEntered list and verify they are added
        entrant.getEventsEntered().add(event1);
        entrant.getEventsEntered().add(event2);

        // Check that events are correctly added
        assertEquals(2, entrant.getEventsEntered().size());
        assertTrue(entrant.getEventsEntered().contains(event1));
        assertTrue(entrant.getEventsEntered().contains(event2));
    }

    @Test
    public void testClearEventsEntered() {
        // Add events to eventsEntered list
        entrant.getEventsEntered().add(event1);
        entrant.getEventsEntered().add(event2);

        // Clear the list and verify it is empty
        entrant.getEventsEntered().clear();
        assertTrue(entrant.getEventsEntered().isEmpty());
    }
}
