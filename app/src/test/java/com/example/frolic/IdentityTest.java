package com.example.frolic;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;

public class IdentityTest {
    private Identity identity;

    @BeforeEach
    public void setUp() {
        // Initialize a new Identity object before each test
        identity = new Identity("12345");
    }

    @Test
    public void testConstructorWithDeviceID() {
        // Test the constructor with only the deviceID
        assertNotNull(identity);
        assertEquals("12345", identity.getDeviceID());
        assertEquals("", identity.getName());
        assertEquals("", identity.getEmail());
        assertEquals("ENTRANT", identity.getRole());  // Default role
        assertEquals(0, identity.getPhoneNumber());  // Default phone number
    }

    @Test
    public void testConstructorWithAllDetails() {
        // Test the constructor with all parameters (including phone number)
        identity = new Identity("12345", "John Doe", "john.doe@example.com", "ORGANIZER", 1234567890);

        assertNotNull(identity);
        assertEquals("12345", identity.getDeviceID());
        assertEquals("John Doe", identity.getName());
        assertEquals("john.doe@example.com", identity.getEmail());
        assertEquals("ORGANIZER", identity.getRole());
        assertEquals(1234567890, identity.getPhoneNumber());
    }

    @Test
    public void testSetName() {
        // Test the setter and getter for name
        identity.setName("Jane Doe");
        assertEquals("Jane Doe", identity.getName());
    }

    @Test
    public void testSetEmail() {
        // Test the setter and getter for email
        identity.setEmail("jane.doe@example.com");
        assertEquals("jane.doe@example.com", identity.getEmail());
    }

    @Test
    public void testSetRole() {
        // Test the setter and getter for role
        identity.setRole("ORGANIZER");
        assertEquals("ORGANIZER", identity.getRole());
    }

    @Test
    public void testSetPhoneNumber() {
        // Test the setter and getter for phone number
        identity.setPhoneNumber(987654321);
        assertEquals(987654321, identity.getPhoneNumber());
    }

    @Test
    public void testSetAdmin() {
        // Test the setter and getter for admin status
        identity.setAdmin(true);
        assertTrue(identity.getAdmin());

        identity.setAdmin(false);
        assertFalse(identity.getAdmin());
    }

    @Test
    public void testSetNotifications() {
        // Test the setter and getter for notifications
        identity.setNotifications(true);
        assertTrue(identity.getNotifications());

        identity.setNotifications(false);
        assertFalse(identity.getNotifications());
    }

    @Test
    public void testDefaultRoleIsEntrant() {
        // Test that the default role is "ENTRANT"
        Identity newIdentity = new Identity("67890");
        assertEquals("ENTRANT", newIdentity.getRole());
    }

    @Test
    public void testSetDeviceID() {
        // Test the setter and getter for device ID
        identity.setDeviceID("54321");
        assertEquals("54321", identity.getDeviceID());
    }

    @Test
    public void testAdminStatusDefaultIsFalse() {
        // Test that the default admin status is false
        assertFalse(identity.getAdmin());
    }

    @Test
    public void testNotificationsDefaultIsFalse() {
        // Test that the default notifications setting is false
        assertFalse(identity.getNotifications());
    }
}
