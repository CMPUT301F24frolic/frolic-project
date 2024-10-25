package com.example.frolic;
import java.util.ArrayList;


public class Identity {
    private String deviceID;
    // Device ID can be found using ANDROID_ID or generated using a UUID
    // More information: https://stackoverflow.com/questions/60503568/best-possible-way-to-get-device-id-in-android
    private String name;
    private String email;
    private int phoneNumber;
    private boolean admin = false;
    private boolean notifications = false;

    /**
     * This constructor takes in a device ID parameter and sets a default Identity.
     * Note that all Identities MUST have a deviceID, as is defined by all constructors.
     * @param deviceID
     * This parameter is a device ID that is defined by the device and specifies which user is accessing the app.
     */
    public Identity(String deviceID) {
        this.deviceID = deviceID;
        name = "";
        email = "";
        phoneNumber = 0;
    }

    /**
     * This constructor takes in three parameters if the identity does not include a phone number.
     * @param deviceID
     * This parameter is a device ID that is defined by the device and specifies which user is accessing the app.
     * @param name
     * The string that will represent the identity's name
     * @param email
     * The string that will represent the identity's email address
     */
    public Identity(String deviceID, String name, String email) {
        this.deviceID = deviceID;
        this.name = name;
        this.email = email;
        phoneNumber = 0;
    }

    /**
     * This constructor takes in all four parameters when making an account.
     * @param deviceID
     * This parameter is a device ID that is defined by the device and specifies which user is accessing the app.
     * @param name
     * The string that will represent the identity's name
     * @param email
     * The string that will represent the identity's email address
     * @param phoneNumber
     * The integer value that will represent the identity's phone number
     */
    public Identity(String deviceID, String name, String email, int phoneNumber) {
        this.deviceID = deviceID;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    // Some of these may be redundant. Feel free to remove later if they seem that way
    // TODO: Add Javadocs

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


    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }


    public boolean getNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }


    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}
