package com.example.frolic;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

/**
 * UserDBConnector handles saving and updating user data in Firestore.
 * It also provides getters and setters for all user attributes.
 */
public class UserDBConnector {

    private String userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String profileImageUrl;

    private final FirebaseFirestore db;

    /**
     * Constructor to initialize all user fields.
     *
     * @param userId          Unique ID of the user.
     * @param name            User's full name.
     * @param email           User's email address.
     * @param phoneNumber     User's phone number (optional).
     * @param profileImageUrl URL of the user's profile picture (optional).
     */
    public UserDBConnector(String userId, String name, String email, String phoneNumber, String profileImageUrl) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Converts the user attributes into a map for Firestore storage.
     *
     * @return A map containing user attributes.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", userId);
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("phoneNumber", phoneNumber);
        userMap.put("profileImageUrl", profileImageUrl);
        return userMap;
    }

    /**
     * Saves the user data to Firestore.
     */
    public void saveToFirestore() {
        db.collection("Users").document(userId).set(toMap())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i("UserDBConnector", "User saved successfully.");
                    } else {
                        Log.e("UserDBConnector", "Error saving user.", task.getException());
                    }
                });
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
}
