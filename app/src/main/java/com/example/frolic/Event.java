package com.example.frolic;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.Date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Event {
    private String eventId;
    private String organizerId;
    private String eventName;
    private String eventDesc;
    private ArrayList<Entrant> confirmed = new ArrayList<>();
    private Facility facility;
    private int waitlistLimit;
    private int maxConfirmed;
    private Date eventDate;
    private Date enrollDate;
    private final LotterySystem lottery;
    private double price;
    private boolean geolocationRequired;
    private boolean receiveNotification;
    private String qrHash;

    /**
     * This constructor acts as the main method of creating an event.
     * Note that Facility is not provided as it should be a prerequisite to create one before starting an event.
     * I am assuming that the UI will prompt the user to create a facility when trying to create an event if they do not already have one.
     */
    public Event(String eventId, Organizer organizer, String eventName, String eventDesc, int maxConfirmed, int waitlistLimit, Date eventDate, Date enrollDate, double price, boolean geolocationRequired, boolean receiveNotification, String qrHash) {
        try { assert organizer.getFacility() != null; }
        catch (AssertionError e) { Log.e("Event.java", "Tried to create an event without a facility", e); }

        this.eventId = eventId;
        this.organizerId = organizer.getOrganizerId();
        this.eventName = eventName;
        this.eventDesc = eventDesc;
        this.maxConfirmed = maxConfirmed;
        this.waitlistLimit = waitlistLimit;
        this.facility = organizer.getFacility();
        this.eventDate = eventDate;
        this.enrollDate = enrollDate;
        this.price = price;
        this.geolocationRequired = geolocationRequired;
        this.receiveNotification = receiveNotification;
        this.qrHash = qrHash;
        lottery = new LotterySystem(this);
    }

    /**
     * Converts the event's attributes into a map representation suitable for storage in Firebase.
     * Each attribute is stored as a key-value pair, allowing for structured data storage in Firestore.
     *
     * @return a map containing the event's attributes, with attribute names as keys
     *         and their respective values, formatted for Firebase storage
     */
    public Map<String, Object> toMap() {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("eventId", eventId);
        eventMap.put("organizerId", organizerId);
        eventMap.put("eventName", eventName);
        eventMap.put("eventDesc", eventDesc);
        eventMap.put("waitlistLimit", waitlistLimit);
        eventMap.put("maxConfirmed", maxConfirmed);
        eventMap.put("eventDate", eventDate);
        eventMap.put("enrollDate", enrollDate);
        eventMap.put("price", price);
        eventMap.put("geolocationRequired", geolocationRequired);
        eventMap.put("qrHash", qrHash);
        return eventMap;
    }

    /**
     * This method adds an entrant to the entrant list.
     * @param entrant
     * This Entrant will be added to the confirmed list in the event.
     */
    public void addEntrant(Entrant entrant) {
        confirmed.add(entrant);
    }

    /**
     * This method removes an entrant from the confirmed list, if they exist
     * @param entrant
     * This user will be removed from the confirmed list if they are in it
     */
    public boolean removeEntrant(Entrant entrant) {
        if (confirmed.contains(entrant)) {
            return confirmed.remove(entrant);
        }
        Log.e("Event.java", "Tried to remove an entrant that doesn't exist in the list");
        return false;
    }

    /**
     * Generates a QR code bitmap based on the event hash.
     * @return A Bitmap containing the QR code, or null if generation fails.
     */
    public Bitmap getEventQRCode() { return QRCodeGenerator.generateQRCode(qrHash);}


    // Getters and Setters
    // Some of these may be redundant. Feel free to remove later if they seem that way
    // TODO: add Javadocs

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
    

    public ArrayList<Entrant> getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(ArrayList<Entrant> confirmed) {
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

    public int getWaitlistLimit() {
        return waitlistLimit;
    }

    public void setWaitlistLimit(int waitlistLimit) {
        this.waitlistLimit = waitlistLimit;
    }

    public double getPrice() { return price; }

    public void setPrice(double price) { this.price = price; }

    public boolean isGeolocationRequired() { return geolocationRequired;}

    public void setGeolocationRequired(boolean geolocationRequired) {this.geolocationRequired = geolocationRequired; }

    public String getEventId() {
        return eventId;
    }

    public boolean isReceiveNotification() {
        return receiveNotification;
    }

    public void setReceiveNotification(boolean receiveNotification) {
        this.receiveNotification = receiveNotification;
    }

    public String getQrHash() {
        return qrHash;
    }
}