package com.example.frolic;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;

public class FacilityTest {
    private Facility facility;
    private Organizer organizer;

    @BeforeEach
    public void setUp() {
        // Create a mock Organizer with a simple Identity
        Identity identity = new Identity("Organizer Name");
        organizer = new Organizer(identity);
        // Create Facility with the organizer
        facility = new Facility(organizer);
    }
    @Test
    public void testConstructorWithOrganizer() {
        // Verify that the Facility constructor sets the organizer correctly
        assertEquals(organizer, facility.getOrganizer(), "Organizer should be set correctly");
    }

    @Test
    public void testConstructorWithOrganizerAndDetails() {
        // Create a new facility with all details
        facility = new Facility(organizer, "Facility Name", "123 Address St.");

        // Verify that the name and address are correctly set
        assertEquals("Facility Name", facility.getName(), "Facility name should be set correctly");
        assertEquals("123 Address St.", facility.getAddress(), "Facility address should be set correctly");
    }

    @Test
    public void testSetName() {
        // Change the name of the facility and verify the change
        facility.setName("New Facility Name");
        assertEquals("New Facility Name", facility.getName(), "Facility name should be updated");
    }

    @Test
    public void testSetAddress() {
        // Change the address of the facility and verify the change
        facility.setAddress("456 New Address Ave.");
        assertEquals("456 New Address Ave.", facility.getAddress(), "Facility address should be updated");
    }

    @Test
    public void testSetOrganizer() {
        // Create a new organizer
        Identity newIdentity = new Identity("New Organizer");
        Organizer newOrganizer = new Organizer(newIdentity);

        // Set the new organizer and verify the change
        facility.setOrganizer(newOrganizer);
        assertEquals(newOrganizer, facility.getOrganizer(), "Facility organizer should be updated");
    }

    @Test
    public void testSetId() {
        // Set the Firestore ID and verify
        facility.setId("12345");
        assertEquals("12345", facility.getId(), "Facility ID should be updated");
    }

    @Test
    public void testGetIdWhenNotSet() {
        // Verify that the default ID is an empty string
        assertEquals("", facility.getId(), "Facility ID should be an empty string if not set");
    }
}
