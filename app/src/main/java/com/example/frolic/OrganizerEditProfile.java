package com.example.frolic;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity for creating and editing organizer profiles.
 * Handles both user information and facility information input.
 * Requires facility creation for new organizers.
 */
public class OrganizerEditProfile extends AppCompatActivity {
    private static final String TAG = "OrganizerEditProfile";
    private static final int PERMISSION_REQUEST_CODE = 123;

    private FirebaseFirestore db;
    private String deviceId;
    private Uri selectedImageUri;

    // Profile UI elements
    private ImageView ivProfileImage;
    private EditText etName, etEmail, etPhone;
    private CheckBox cbNotifications;
    private Button btnUploadImage, btnSaveProfile;

    // Facility UI elements
    private EditText etFacilityName, etFacilityAddress;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivProfileImage.setImageURI(selectedImageUri);
                }
            }
    );

    /**
     * Initializes the activity, sets up UI components and Firebase connection.
     * Loads existing user data if available.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     * this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_edit_profile);

        db = FirebaseFirestore.getInstance();
        deviceId = getIntent().getStringExtra("deviceId");

        initializeViews();
        setupClickListeners();
        loadExistingData();
    }

    /**
     * Initializes view references from the layout.
     * Sets up both profile and facility related views.
     */
    private void initializeViews() {
        // Profile views
        ivProfileImage = findViewById(R.id.ivProfileImage);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        cbNotifications = findViewById(R.id.cbNotifications);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        // Facility views
        etFacilityName = findViewById(R.id.etFacilityName);
        etFacilityAddress = findViewById(R.id.etFacilityAddress);
    }

    /**
     * Sets up click listeners for buttons and interactive elements.
     */
    private void setupClickListeners() {
        btnUploadImage.setOnClickListener(v -> checkPermissionAndPickImage());
        btnSaveProfile.setOnClickListener(v -> validateAndSaveProfile());
    }

    /**
     * Loads existing user and facility data from Firestore if available.
     */
    private void loadExistingData() {
        if (deviceId != null) {
            db.collection("organizers").document(deviceId)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            // Load profile data
                            etName.setText(document.getString("name"));
                            etEmail.setText(document.getString("email"));

                            Long phoneNumber = document.getLong("phoneNumber");
                            if (phoneNumber != null) {
                                etPhone.setText(String.valueOf(phoneNumber));
                            }

                            Boolean notifications = document.getBoolean("notifications");
                            if (notifications != null) {
                                cbNotifications.setChecked(notifications);
                            }

                            String base64Image = document.getString("profileImage");
                            if (base64Image != null) {
                                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(
                                        decodedString, 0, decodedString.length);
                                Glide.with(this)
                                        .load(decodedByte)
                                        .circleCrop()
                                        .into(ivProfileImage);
                            }

                            // Load facility data - first check intent for facilityId
                            String facilityId = getIntent().getStringExtra("facilityId");
                            if (facilityId != null) {
                                loadFacilityData(facilityId);
                            } else {
                                // Check if user has a facility
                                db.collection("facilities")
                                        .whereEqualTo("organizerId", deviceId)
                                        .get()
                                        .addOnSuccessListener(querySnapshot -> {
                                            if (!querySnapshot.isEmpty()) {
                                                loadFacilityData(querySnapshot.getDocuments().get(0).getId());
                                            }
                                        })
                                        .addOnFailureListener(e ->
                                                Log.e(TAG, "Error checking facilities", e));
                            }
                        } else if (getIntent().getBooleanExtra("isNewRole", false)) {
                            // New role, try to copy data from entrant profile
                            copyFromEntrantProfile();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading profile", e);
                        Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void copyFromEntrantProfile() {
        db.collection("entrants").document(deviceId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        etName.setText(document.getString("name"));
                        etEmail.setText(document.getString("email"));
                        Long phoneNumber = document.getLong("phoneNumber");
                        if (phoneNumber != null) {
                            etPhone.setText(String.valueOf(phoneNumber));
                        }
                        String base64Image = document.getString("profileImage");
                        if (base64Image != null) {
                            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(
                                    decodedString, 0, decodedString.length);
                            Glide.with(this)
                                    .load(decodedByte)
                                    .circleCrop()
                                    .into(ivProfileImage);
                        }
                    }
                });
    }

    /**
     * Loads facility data from Firestore using facility ID.
     *
     * @param facilityId The ID of the facility to load
     */
    private void loadFacilityData(String facilityId) {
        if (facilityId != null) {
            db.collection("facilities").document(facilityId)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            etFacilityName.setText(document.getString("name"));
                            etFacilityAddress.setText(document.getString("address"));
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Error loading facility", e));
        }
    }

    /**
     * Validates user input and saves both profile and facility data.
     */
    private void validateAndSaveProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneStr = etPhone.getText().toString().trim();
        String facilityName = etFacilityName.getText().toString().trim();
        String facilityAddress = etFacilityAddress.getText().toString().trim();

        // Validate required fields
        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            return;
        }
        if (facilityName.isEmpty()) {
            etFacilityName.setError("Facility name is required");
            return;
        }
        if (facilityAddress.isEmpty()) {
            etFacilityAddress.setError("Facility address is required");
            return;
        }

        // Create facility first, then save profile
        saveFacility(facilityName, facilityAddress, name, email, phoneStr);
    }

    /**
     * Saves facility data to Firestore, then proceeds to save profile data.
     */
    private void saveFacility(String facilityName, String facilityAddress,
                              String name, String email, String phoneStr) {
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", facilityName);
        facilityData.put("address", facilityAddress);
        facilityData.put("organizerId", deviceId);

        // Get existing facilityId from intent
        String existingFacilityId = getIntent().getStringExtra("facilityId");

        if (existingFacilityId != null) {
            // Update existing facility
            db.collection("facilities")
                    .document(existingFacilityId)
                    .update(facilityData)
                    .addOnSuccessListener(aVoid -> {
                        saveProfileData(name, email, phoneStr, existingFacilityId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error updating facility", e);
                        Toast.makeText(this, "Error updating facility", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Create new facility
            db.collection("facilities")
                    .add(facilityData)
                    .addOnSuccessListener(documentReference -> {
                        String facilityId = documentReference.getId();
                        saveProfileData(name, email, phoneStr, facilityId);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error saving facility", e);
                        Toast.makeText(this, "Error saving facility", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    /**
     * Saves profile data to Firestore after facility is created.
     */
    private void saveProfileData(String name, String email, String phoneStr, String facilityId) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("deviceId", deviceId);
        userData.put("name", name);
        userData.put("email", email);
        userData.put("phoneNumber", phoneStr.isEmpty() ? 0 : Integer.parseInt(phoneStr));
        userData.put("notifications", cbNotifications.isChecked());
        userData.put("role", "ORGANIZER");
        userData.put("facilityId", facilityId);

        db.collection("organizers").document(deviceId)
                .get()
                .addOnSuccessListener(document -> {
                    if (!document.exists() || !document.contains("eventsOrganizing")) {
                        // Initialize `eventsOrganizing` as an empty list only if it doesn’t exist
                        userData.put("eventsOrganizing", new ArrayList<>());
                    }

                    // Add image processing if a new image is selected
                    if (selectedImageUri != null) {
                        handleImageInFirestore(userData);
                    } else {
                        saveToFirestore(userData);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking if eventsOrganizing exists", e);
                    Toast.makeText(this, "Error saving profile", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Processes and adds image data to user data before saving.
     */
    private void handleImageInFirestore(Map<String, Object> userData) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            Bitmap resized = getResizedBitmap(bitmap, 500);
            String base64Image = bitmapToBase64(resized);
            userData.put("profileImage", base64Image);
            saveToFirestore(userData);
        } catch (IOException e) {
            Log.e(TAG, "Error processing image", e);
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves the complete user data to Firestore.
     */
    private void saveToFirestore(Map<String, Object> userData) {
        db.collection("organizers")
                .document(deviceId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
                    navigateToOrganizerDashboard();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving profile", e);
                    Toast.makeText(this, "Error saving profile", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Navigates to the organizer dashboard after successful profile creation.
     */
    private void navigateToOrganizerDashboard() {
        Intent intent = new Intent(this, OrganizerDashboardActivity.class);
        intent.putExtra("deviceId", deviceId);
        startActivity(intent);
        finish();
    }

    // Image handling utility methods (same as EntrantEditProfile)
    private void checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.READ_MEDIA_IMAGES},
                    PERMISSION_REQUEST_CODE);
        } else {
            launchImagePicker();
        }
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}