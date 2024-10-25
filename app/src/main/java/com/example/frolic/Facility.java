package com.example.frolic;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class Facility {
    private Organizer organizer;
    private String name;
    private ArrayList<Event> events;

    /**
     * This constructor acts as the default constructor for Facility.
     * Note that it requires an organizer, as facilities must have an organizer.
     * @param organizer
     * This Organizer will be set as the organizer of this facility.
     */
    public Facility(Organizer organizer) {
        this.organizer = organizer;
        name = "";
        events = organizer.getEventsOrganizing();
    }

    /**
     * This constructor acts as another constructor for Facility.
     * @param organizer
     * This Organizer will be set as the organizer of this facility.
     * @param name
     * This String will be set as the facility's name.
     */
    public Facility(Organizer organizer, String name) {
        this.organizer = organizer;
        this.name = name;
        events = organizer.getEventsOrganizing();
    }

    // Getters and Setters
    // Some of these may be redundant. Feel free to remove later if they seem that way
    // TODO: add Javadocs

    public Organizer getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }


}
