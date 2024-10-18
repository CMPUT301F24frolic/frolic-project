package com.example.frolic;
import android.util.Log;

import java.util.ArrayList;

public class Event {
    private User organizer;
    private String eventName;
    private String eventDesc;
    private ArrayList<User> entrants;
    private Facility facility;
    private int maxAttendees;


    /*
     * This constructor takes in no parameters and sets a default Event.
     * Note that facilities need to have an owner, so one is provided.
     */
    public Event() {
        organizer = new User();
        eventName = "";
        eventDesc = "";
        facility = new Facility(organizer);
        entrants = new ArrayList<User>();
        maxAttendees = 0;
    }

    /*
     * This constructor acts as the main method of creating an event.
     * Note that Facility is not provided as it should be a prerequisite to create one before starting an event.
     * I am assuming that the UI will prompt the user to create a facility when trying to create an event if they do not already have one.
     */
    public Event(User organizer, String eventName, String eventDesc, int maxAttendees) {
        try { assert organizer.getFacility() != null; }
        catch (AssertionError e) { Log.e("Event.java", "Tried to create an event without a facility", e); }

        this.organizer = organizer;
        this.eventName = eventName;
        this.eventDesc = eventDesc;
        this.maxAttendees = maxAttendees;
        this.facility = organizer.getFacility();
    }

    /*
     * This method adds an entrant to the waiting list.
     * @param entrant
     * This User will be added to the wait list in the event.
     */
    public void addToWaitingList(User entrant) {
        entrants.add(entrant);
    }

    /*
     * This method removes an entrant from the waiting list, if they exist
     * @param entrant
     * This user will be removed from the waiting list if they are in it
     */
    public User removeFromWaitingList(User entrant) {
        if (entrants.contains(entrant)) {
            return entrant;
        }
        Log.e("Event.java", "Tried to remove an entrant that doesn't exist in the list");
        return entrant;
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


    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }


    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }
    

    public ArrayList<User> getEntrants() {
        return entrants;
    }

    public void setEntrants(ArrayList<User> entrants) {
        this.entrants = entrants;
    }


    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }


    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }


}