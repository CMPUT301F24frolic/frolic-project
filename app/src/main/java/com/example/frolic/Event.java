package com.example.frolic;

import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Event {
    private String eventId;
    private String organizerId;
    private String facilityId;
    private String eventName;
    private ArrayList<String> entrantIds = new ArrayList<>();
    private int waitlistLimit;
    private int maxConfirmed;
    private Date eventDate;
    private Date enrollDate;
    private String lotterySystemId;
    private boolean geolocationRequired;
    private boolean receiveNotification;
    private String qrHash;

    // Constructor for Firebase deserialization
    public Event() {
        // Leave this empty for Firebase
    }

    /**
     * Main constructor for creating an event.
     *
     * @param eventId            The unique ID of the event
     * @param organizerId        The ID of the organizer
     * @param facilityId         The ID of the facility where the event is held
     * @param eventName          The name of the event
     * @param maxConfirmed       Maximum number of confirmed entrants
     * @param waitlistLimit      The limit for the waitlist
     * @param eventDate          The date of the event
     * @param enrollDate         The last date for enrollment
     * @param geolocationRequired Whether geolocation is required for the event
     * @param receiveNotification Whether notifications are enabled for the event
     * @param qrHash             The hash for the event's QR code
     */
    public Event(String eventId, String organizerId, String facilityId, String eventName, int maxConfirmed, int waitlistLimit, Date eventDate, Date enrollDate, boolean geolocationRequired, boolean receiveNotification, String qrHash) {
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.facilityId = facilityId;
        this.eventName = eventName;
        this.maxConfirmed = maxConfirmed;
        this.waitlistLimit = waitlistLimit;
        this.eventDate = eventDate;
        this.enrollDate = enrollDate;
        this.geolocationRequired = geolocationRequired;
        this.receiveNotification = receiveNotification;
        this.qrHash = qrHash;
        this.lotterySystemId = eventId + "_lottery"; // Unique ID for the lottery system
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
        eventMap.put("facilityId", facilityId);
        eventMap.put("eventName", eventName);
        eventMap.put("entrantIds", entrantIds);
        eventMap.put("waitlistLimit", waitlistLimit);
        eventMap.put("maxConfirmed", maxConfirmed);
        eventMap.put("eventDate", eventDate);
        eventMap.put("enrollDate", enrollDate);
        eventMap.put("geolocationRequired", geolocationRequired);
        eventMap.put("receiveNotification", receiveNotification);
        eventMap.put("qrHash", qrHash);
        eventMap.put("lotterySystemId", lotterySystemId);
        return eventMap;
    }

    // Getters and Setters
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

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public ArrayList<String> getEntrantIds() {
        return entrantIds;
    }

    public void setEntrantIds(ArrayList<String> entrantIds) {
        this.entrantIds = entrantIds;
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

    public String getLotterySystemId() {
        return lotterySystemId;
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

    /**
     * Generates a QR code bitmap based on the event hash.
     * @return A Bitmap containing the QR code, or null if generation fails.
     */
    public Bitmap getEventQRCode() {
        return QRCodeGenerator.generateQRCode(qrHash);
    }
}

