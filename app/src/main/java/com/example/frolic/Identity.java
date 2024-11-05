package com.example.frolic;

/**
 * Class representing a user's identity in the application.
 * Contains basic user information and preferences.
 * Every user must have a device ID for unique identification.
 */
public class Identity {
    private String deviceID;
    // Device ID can be found using ANDROID_ID or generated using a UUID
    // More information: https://stackoverflow.com/questions/60503568/best-possible-way-to-get-device-id-in-android
    private String name;
    private String email;
    private String role;
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
        role = "ENTRANT";  // Default role
        phoneNumber = 0;
    }

    /**
     * This constructor takes in parameters for a basic identity setup without phone number.
     * @param deviceID The device ID that identifies this user.
     * @param name The string that will represent the identity's name.
     * @param email The string that will represent the identity's email address.
     * @param role The string that will represent the user's role ("ENTRANT", "ORGANIZER").
     */
    public Identity(String deviceID, String name, String email, String role) {
        this.deviceID = deviceID;
        this.name = name;
        this.email = email;
        this.role = role;
        phoneNumber = 0;
    }

    /**
     * This constructor takes in all parameters when making an account.
     * @param deviceID The device ID that identifies this user.
     * @param name The string that will represent the identity's name.
     * @param email The string that will represent the identity's email address.
     * @param role The string that will represent the user's role ("ENTRANT", "ORGANIZER").
     * @param phoneNumber The integer value that will represent the identity's phone number.
     */
    public Identity(String deviceID, String name, String email, String role, int phoneNumber) {
        this.deviceID = deviceID;
        this.name = name;
        this.email = email;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters with JavaDocs

    /**
     * Gets the name associated with this identity.
     * @return The string representing the identity's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name for this identity.
     * @param name The string to set as the identity's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the email associated with this identity.
     * @return The string representing the identity's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email for this identity.
     * @param email The string to set as the identity's email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the role associated with this identity.
     * @return The string representing the identity's role ("ENTRANT", "ORGANIZER").
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role for this identity.
     * @param role The string to set as the identity's role ("ENTRANT", "ORGANIZER").
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Gets the phone number associated with this identity.
     * @return The integer representing the identity's phone number.
     */
    public int getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number for this identity.
     * @param phoneNumber The integer to set as the identity's phone number.
     */
    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the admin status of this identity.
     * @return Boolean indicating if this identity has admin privileges.
     */
    public boolean getAdmin() {
        return admin;
    }

    /**
     * Sets the admin status for this identity.
     * @param admin The boolean to set as the identity's admin status.
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    /**
     * Gets the notification preferences of this identity.
     * @return Boolean indicating if this identity has notifications enabled.
     */
    public boolean getNotifications() {
        return notifications;
    }

    /**
     * Sets the notification preferences for this identity.
     * @param notifications The boolean to set as the identity's notification preference.
     */
    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    /**
     * Gets the device ID associated with this identity.
     * @return The string representing the identity's device ID.
     */
    public String getDeviceID() {
        return deviceID;
    }

    /**
     * Sets the device ID for this identity.
     * @param deviceID The string to set as the identity's device ID.
     */
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}