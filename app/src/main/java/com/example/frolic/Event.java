package com.example.frolic;
import android.util.Log;
import java.util.Date;

import java.util.ArrayList;

public class Event {
    private User organizer;
    private String eventName;
    private String eventDesc;
    private ArrayList<User> confirmed;
    private Facility facility;
    private int maxEntrants;
    private int maxConfirmed;
    private Date eventDate;
    private Date enrollDate;
    private LotterySystem lottery;

    /*
     * This constructor takes in no parameters and sets a default Event.
     * Note that facilities need to have an owner, so one is provided.
     */
    public Event() {
        organizer = new User();
        eventName = "";
        eventDesc = "";
        facility = new Facility(organizer);
        confirmed = new ArrayList<User>();
        maxEntrants = 0;
        maxConfirmed = 0;
        eventDate = new Date();
        enrollDate = new Date();
        lottery = new LotterySystem(this, maxEntrants, maxConfirmed);
    }

    /*
     * This constructor acts as the main method of creating an event.
     * Note that Facility is not provided as it should be a prerequisite to create one before starting an event.
     * I am assuming that the UI will prompt the user to create a facility when trying to create an event if they do not already have one.
     */
    public Event(User organizer, String eventName, String eventDesc, int maxConfirmed, int maxEntrants, Date eventDate, Date enrollDate) {
        try { assert organizer.getFacility() != null; }
        catch (AssertionError e) { Log.e("Event.java", "Tried to create an event without a facility", e); }

        this.organizer = organizer;
        this.eventName = eventName;
        this.eventDesc = eventDesc;
        this.maxConfirmed = maxConfirmed;
        this.maxEntrants = maxEntrants;
        this.facility = organizer.getFacility();

        this.eventDate = eventDate;
        this.enrollDate = enrollDate;
        lottery = new LotterySystem(this, maxEntrants, maxConfirmed);
    }

    /**
     * This method adds an entrant to the entrant list.
     * @param user
     * This User will be added to the confirmed list in the event.
     */
    public void addEntrant(User user) {
        confirmed.add(user);
    }

    /**
     * This method removes an entrant from the confirmed list, if they exist
     * @param user
     * This user will be removed from the confirmed list if they are in it
     */
    public boolean removeEntrant(User user) {
        if (confirmed.contains(user)) {
            return confirmed.remove(user);
        }
        Log.e("Event.java", "Tried to remove an entrant that doesn't exist in the list");
        return false;
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
    

    public ArrayList<User> getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(ArrayList<User> confirmed) {
        this.confirmed = confirmed;
    }


    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }


    public int getMaxConfirmed() {
        return maxConfirmed;
    }

    public void setMaxConfirmed(int maxConfirmed) {
        this.maxConfirmed = maxConfirmed;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }


    public Date getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(Date enrollDate) {
        this.enrollDate = enrollDate;
    }

    public LotterySystem getLottery() {
        return lottery;
    }

    // There is no setter for Lottery, as it is a composition of Event.

    public int getMaxEntrants() {
        return maxEntrants;
    }

    public void setMaxEntrants(int maxEntrants) {
        this.maxEntrants = maxEntrants;
    }
}