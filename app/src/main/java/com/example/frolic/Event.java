package com.example.frolic;

import android.graphics.Bitmap;
import android.util.Log;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
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
    private double price;
    private boolean geolocationRequired;
    private boolean receiveNotification;
    private String qrHash;

    // A no-argument constructor is required for Firestore deserialization
    public Event() {}

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
    }

    // Getters and setters for Firestore compatibility

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
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

    public int getWaitlistLimit() {
        return waitlistLimit;
    }

    public void setWaitlistLimit(int waitlistLimit) {
        this.waitlistLimit = waitlistLimit;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isGeolocationRequired() {
        return geolocationRequired;
    }

    public void setGeolocationRequired(boolean geolocationRequired) {
        this.geolocationRequired = geolocationRequired;
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

    public void setQrHash(String qrHash) {
        this.qrHash = qrHash;
    }

    @Exclude
    public Bitmap getEventQRCode() {
        return QRCodeGenerator.generateQRCode(qrHash);
    }

    /**
     * Converts the event's attributes into a map representation suitable for storage in Firebase.
     * Each attribute is stored as a key-value pair, allowing for structured data storage in Firestore.
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
}
