package com.example.frolic;
import java.util.ArrayList;

public class Organizer {
    private Identity identity;
    private ArrayList<String> eventsOrganizing;  // Store event IDs instead of full Event objects for Firebase
    private Facility facility = null;

    /**
     * This constructor takes in an Identity parameter and sets a default Organizer.
     * Note that all Organizers must have an "Identity" to identify them.
     * @param identity
     * This parameter acts as a unique Identity to specify the entrant.
     */
    public Organizer(Identity identity) {
        this.identity = identity;
        this.eventsOrganizing = new ArrayList<>();
    }

    // Getters and Setters
    // Some of these may be redundant. Feel free to remove later if they seem that way
    // TODO: Add Javadocs

    public String getOrganizerId() {
        return identity.getDeviceID();
    }

    public void organizeEvent(String eventId) {
        eventsOrganizing.add(eventId);
    }

    public ArrayList<String> getEventsOrganizing() {
        return eventsOrganizing;
    }

    public void setEventsOrganizing(ArrayList<String> eventsOrganizing) {
        this.eventsOrganizing = eventsOrganizing;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }


    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
