package com.example.frolic;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Entrant {
    private Identity identity;
    private ArrayList<Event> eventsEntered;
    private String deviceId;  // Unique identifier for the entrant's device

    /**
     * This constructor takes in an identity parameter and generates a default Entrant.
     * A unique device ID is generated automatically to identify the entrant by their device.
     * @param identity This parameter acts as a unique Identity to specify the entrant.
     */
    public Entrant(Identity identity) {
        this.identity = identity;
        this.eventsEntered = new ArrayList<>();
        this.deviceId = generateDeviceId();  // Generate a unique device ID
    }

    /**
     * This constructor allows setting a predefined device ID.
     * Useful if the device ID is retrieved from persistent storage.
     * @param identity The unique Identity for the entrant.
     * @param deviceId The pre-assigned device ID.
     */
    public Entrant(Identity identity, String deviceId) {
        this.identity = identity;
        this.eventsEntered = new ArrayList<>();
        this.deviceId = deviceId;
    }

    /**
     * Generates a unique device ID using UUID.
     * In a real application, this could be replaced with a device-specific identifier.
     * @return A unique UUID string representing the device ID.
     */
    private String generateDeviceId() {
        return UUID.randomUUID().toString();
    }

    // Getters and Setters

    public ArrayList<Event> getEventsEntered() {
        return eventsEntered;
    }

    public void setEventsEntered(ArrayList<Event> eventsEntered) {
        this.eventsEntered = eventsEntered;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    /**
     * Gets the device ID associated with the entrant.
     * @return The device ID.
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets a new device ID for the entrant.
     * @param deviceId The new device ID to assign.
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entrant entrant = (Entrant) o;
        return Objects.equals(deviceId, entrant.deviceId) &&
                Objects.equals(identity, entrant.identity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, identity);
    }

    @Override
    public String toString() {
        return "Entrant{" +
                "identity=" + identity +
                ", deviceId='" + deviceId + '\'' +
                ", eventsEntered=" + eventsEntered +
                '}';
    }
}

