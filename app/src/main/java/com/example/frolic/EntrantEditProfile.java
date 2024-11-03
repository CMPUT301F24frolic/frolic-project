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
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Activity for creating and editing user profiles.
 * Handles user information input and profile picture upload.
 */
public class EntrantEditProfile extends AppCompatActivity {
    private static final String TAG = "EntrantEditProfile";
    private static final int PERMISSION_REQUEST_CODE = 123;

    private FirebaseFirestore db;
    private String deviceId;
    private Uri selectedImageUri;

    private ImageView ivProfileImage;
    private EditText etName, etEmail, etPhone;
    private CheckBox cbNotifications;
    private TextView tvAdminStatus, tvDeviceId;
    private Button btnUploadImage, btnSaveChanges;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivProfileImage.setImageURI(selectedImageUri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_edit_profile);

        db = FirebaseFirestore.getInstance();
        deviceId = getIntent().getStringExtra("deviceId");

        initializeViews();
        setupClickListeners();
        loadExistingData();
    }

    private void initializeViews() {
        ivProfileImage = findViewById(R.id.ivProfileImage);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        cbNotifications = findViewById(R.id.cbNotifications);
        tvAdminStatus = findViewById(R.id.tvAdminStatus);
        tvDeviceId = findViewById(R.id.tvDeviceId);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        tvDeviceId.setText("Device ID: " + deviceId);
    }

    private void setupClickListeners() {
        btnUploadImage.setOnClickListener(v -> checkPermissionAndPickImage());
        btnSaveChanges.setOnClickListener(v -> validateAndSaveProfile());
    }

    private void loadExistingData() {
        if (deviceId != null) {
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

                            Boolean notifications = document.getBoolean("notifications");
                            if (notifications != null) {
                                cbNotifications.setChecked(notifications);
                            }

                            String base64Image = document.getString("profileImage");
                            if (base64Image != null) {
                                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                Glide.with(this)
                                        .load(decodedByte)
                                        .circleCrop()
                                        .into(ivProfileImage);
                            }
                        } else if (getIntent().getBooleanExtra("isNewRole", false)) {
                            // New role, try to copy data from organizer profile
                            copyFromOrganizerProfile();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading user data", e);
                        Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    /**
     * Copies existing profile data from organizer profile when creating new entrant profile
     */
    private void copyFromOrganizerProfile() {
        db.collection("organizers").document(deviceId)
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
                        if (document.getBoolean("notifications") != null) {
                            cbNotifications.setChecked(document.getBoolean("notifications"));
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error copying organizer data", e));
    }

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

    private void validateAndSaveProfile() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneStr = etPhone.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            return;
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("deviceId", deviceId);
        userData.put("name", name);
        userData.put("email", email);
        userData.put("phoneNumber", phoneStr.isEmpty() ? 0 : Integer.parseInt(phoneStr));
        userData.put("notifications", cbNotifications.isChecked());
        userData.put("admin", false);

        if (selectedImageUri != null) {
            handleImageInFirestore(userData);
        } else {
            saveProfileData(userData);
        }
    }

    private void handleImageInFirestore(Map<String, Object> userData) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            Bitmap resized = getResizedBitmap(bitmap, 500);
            String base64Image = bitmapToBase64(resized);
            userData.put("profileImage", base64Image);
            saveProfileData(userData);
        } catch (IOException e) {
            Log.e(TAG, "Error processing image", e);
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
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

    private void saveProfileData(Map<String, Object> userData) {
        db.collection("entrants")
                .document(deviceId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, EntrantDashboardActivity.class);
                    intent.putExtra("deviceId", deviceId);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving profile", e);
                    Toast.makeText(this, "Error saving profile", Toast.LENGTH_SHORT).show();
                });
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