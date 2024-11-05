package com.example.frolic;

import android.graphics.Bitmap;
import android.util.Log;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an event in the system with various attributes including organizer, name, description,
 * confirmed entrants, facility, and other event details.
 */
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
    private final LotterySystem lottery;
    private double price;
    private boolean geolocationRequired;
    private boolean receiveNotification;
    private String qrHash;

    // No-argument constructor required for Firestore deserialization
    public Event() {
        this.lottery = new LotterySystem(this);
    }

    /**
     * Creates an Event instance with the specified parameters.
     *
     * @param eventId               Unique identifier for the event.
     * @param organizer             Organizer of the event.
     * @param eventName             Name of the event.
     * @param eventDesc             Description of the event.
     * @param maxConfirmed          Maximum number of confirmed entrants.
     * @param waitlistLimit         Limit for the waitlist.
     * @param eventDate             Date of the event.
     * @param enrollDate            Enrollment date for the event.
     * @param price                 Price of attending the event.
     * @param geolocationRequired   Whether geolocation is required.
     * @param receiveNotification   Whether notifications should be sent for the event.
     * @param qrHash                QR hash associated with the event.
     */
    public Event(String eventId, Organizer organizer, String eventName, String eventDesc, int maxConfirmed, int waitlistLimit, Date eventDate, Date enrollDate, double price, boolean geolocationRequired, boolean receiveNotification, String qrHash) {
        if (organizer.getFacility() == null) {
            Log.e("Event.java", "Tried to create an event without a facility");
        }
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
        this.lottery = new LotterySystem(this);
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
     * Converts the event's attributes into a map representation suitable for storage in Firestore.
     * Each attribute is stored as a key-value pair, allowing for structured data storage in Firestore.
     *
     * @return a map containing the event's attributes, with attribute names as keys and their respective values.
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
