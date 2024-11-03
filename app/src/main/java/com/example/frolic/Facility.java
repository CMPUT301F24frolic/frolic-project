package com.example.frolic;

/**
 * Class representing a facility where events can be hosted.
 * Each facility must have an organizer and can host multiple events.
 * Contains basic information about the facility such as name, address,
 * and a reference to its organizer.
 */
public class Facility {
    private Organizer organizer;
    private String name;
    private String address;
    private String id;

    /**
     * This constructor acts as the default constructor for Facility.
     * Note that it requires an organizer, as facilities must have an organizer.
     * Sets empty strings for name, address, and ID.
     *
     * @param organizer The Organizer who will be set as the organizer of this facility.
     */
    public Facility(Organizer organizer) {
        this.organizer = organizer;
        this.name = "";
        this.address = "";
        this.id = "";
    }

    /**
     * This constructor creates a facility with specified details.
     * The ID will be empty until the facility is saved to Firestore.
     *
     * @param organizer The Organizer who will be set as the organizer of this facility.
     * @param name The String that will be set as the facility's name.
     * @param address The String that will be set as the facility's address.
     */
    public Facility(Organizer organizer, String name, String address) {
        this.organizer = organizer;
        this.name = name;
        this.address = address;
        this.id = "";
    }

    /**
     * Gets the organizer associated with this facility.
     *
     * @return The Organizer object representing the facility's organizer.
     */
    public Organizer getOrganizer() {
        return organizer;
    }

    /**
     * Sets a new organizer for this facility.
     *
     * @param organizer The new Organizer to be associated with this facility.
     */
    public void setOrganizer(Organizer organizer) {
        this.organizer = organizer;
    }

    /**
     * Gets the name of the facility.
     *
     * @return The String representing the facility's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for this facility.
     *
     * @param name The String to set as the facility's new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the address of the facility.
     *
     * @return The String representing the facility's address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets a new address for this facility.
     *
     * @param address The String to set as the facility's new address.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the Firestore document ID of this facility.
     * Will be empty string if facility hasn't been saved to Firestore.
     *
     * @return The String representing the facility's Firestore document ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the Firestore document ID for this facility.
     * Typically called after saving the facility to Firestore.
     *
     * @param id The String representing the facility's Firestore document ID.
     */
    public void setId(String id) {
        this.id = id;
    }
}