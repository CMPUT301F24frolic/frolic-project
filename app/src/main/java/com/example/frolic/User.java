package com.example.frolic;

/**
 * The User class represents the structure of a user profile stored in Firestore.
 * It includes attributes like user ID, name, email, phone number, and profile image URL.
 */
public class User {

    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String profileImageUrl;  // New field for profile picture URL

    /**
     * Default constructor required for Firestore serialization.
     */
    public User() {
        // Required for Firestore deserialization
    }

    /**
     * Constructor to initialize all user attributes.
     *
     * @param userId          Unique ID of the user.
     * @param name            Full name of the user.
     * @param email           Email address of the user.
     * @param phoneNumber     Phone number of the user (optional).
     * @param profileImageUrl URL of the user's profile picture (optional).
     */
    public User(String userId, String name, String email, String phoneNumber, String profileImageUrl) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
    }

    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    /**
     * Converts the user attributes into a string representation.
     *
     * @return String representation of the user object.
     */
    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                '}';
    }
}

