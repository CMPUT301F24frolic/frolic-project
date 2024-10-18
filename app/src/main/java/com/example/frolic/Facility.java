package com.example.frolic;
import java.util.ArrayList;

public class Facility {
    private User organizer;
    private String name;
    private ArrayList<Event> events;
    // I'm not entirely sure why this is in the UML, I sort of assumed each facility would only have one organizer
    // I added it anyway

    /*
     * This constructor acts as the default constructor for Facility.
     * Note that it requires an organizer, as facilities must have an organizer.
     * @param organizer
     * This User will be set as the organizer of this facility.
     */
    public Facility(User organizer) {
        this.organizer = organizer;
        name = "";
        events = organizer.getEventsOrganizing();
    }

    /*
     * This constructor acts as another constructor for Facility.
     * @param organizer
     * This User will be set as the organizer of this facility.
     * @param name
     * This String will be set as the facility's name.
     */
    public Facility(User organizer, String name) {
        this.organizer = organizer;
        this.name = name;
        events = organizer.getEventsOrganizing();
    }

    // Getters and Setters
    // Some of these may be redundant. Feel free to remove later if they seem that way
    // TODO: add Javadocs

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
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
