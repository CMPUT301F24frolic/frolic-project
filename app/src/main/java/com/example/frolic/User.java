package com.example.frolic;
import java.util.ArrayList;

public class User {
    private String name;
    private String email;
    private int phoneNumber;
    private ArrayList<Event> eventsOrganizing;
    private ArrayList<Event> eventsEntered;
    private Facility facility = null;
    private boolean admin = false;
    // Set users to not be admins by default lol

    /*
     * This constructor takes in no parameters and sets a default User.
     */
    public User() {
        name = "";
        email = "";
        phoneNumber = 0;
    }

    /*
     * This constructor takes in two parameters if the user does not include a phone number.
     * @param name
     * The string that will represent the user's name
     * @param email
     * The string that will represent the user's email address
     */
    public User(String name, String email) {
        this.name = name;
        this.email = email;
        phoneNumber = 0;
    }

    /*
     * This constructor takes in all three parameters when making an account.
     * @param name
     * The string that will represent the user's name
     * @param email
     * The string that will represent the user's email address
     * @param phoneNumber
     * The integer value that will represent the user's phone number
     */
    public User(String name, String email, int phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    // Some of these may be redundant. Feel free to remove later if they seem that way
    // TODO: add Javadocs for this (i dont want to do this)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public ArrayList<Event> getEventsOrganizing() {
        return eventsOrganizing;
    }

    public void setEventsOrganizing(ArrayList<Event> eventsOrganizing) {
        this.eventsOrganizing = eventsOrganizing;
    }


    public ArrayList<Event> getEventsEntered() {
        return eventsEntered;
    }

    public void setEventsEntered(ArrayList<Event> eventsEntered) {
        this.eventsEntered = eventsEntered;
    }


    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }


    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }


}
