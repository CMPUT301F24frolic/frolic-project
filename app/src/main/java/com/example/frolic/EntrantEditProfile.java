package com.example.frolic;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    private Button btnUploadImage, btnRemoveImage, btnSaveChanges, btnBack;
    private Boolean isAdmin;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivProfileImage.setImageURI(selectedImageUri); // Show the selected image in the ImageView
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_edit_profile);

        db = FirebaseFirestore.getInstance();
        deviceId = getIntent().getStringExtra("deviceId");

        boolean fromRoleSelection = getIntent().getBooleanExtra("fromRoleSelection", false);

        initializeViews();
        setupClickListeners(fromRoleSelection);
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
        btnRemoveImage = findViewById(R.id.btnRemoveImage);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnBack = findViewById(R.id.btnBack);

        tvDeviceId.setText("Device ID: " + deviceId);
    }

    private void setupClickListeners(boolean fromRoleSelection) {
        btnUploadImage.setOnClickListener(v -> showUploadImageDialog());
        btnRemoveImage.setOnClickListener(v -> removeProfileImage());
        btnSaveChanges.setOnClickListener(v -> validateAndSaveProfile());
        btnBack.setOnClickListener(v -> {
            if (fromRoleSelection) {
                // Navigate back to RoleSelectionActivity
                Intent intent = new Intent(this, RoleSelectionActivity.class);
                intent.putExtra("deviceId", deviceId);
                startActivity(intent);
            } else {
                // Navigate back to EntrantDashboardActivity
                Intent intent = new Intent(this, EntrantDashboardActivity.class);
                intent.putExtra("deviceId", deviceId);
                startActivity(intent);
            }
            finish();
        });
    }

    private void loadExistingData() {
        if (deviceId != null) {
            db.collection("entrants").document(deviceId)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            populateEntrantData(document.getData());
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
    private void populateEntrantData(Map<String, Object> data) {
        if (data == null) return;

        etName.setText((String) data.get("name"));
        etEmail.setText((String) data.get("email"));
        Long phoneNumber = (Long) data.get("phoneNumber");
        if (phoneNumber != null) {
            if (phoneNumber == 0) {
                etPhone.setText("");
            } else {
                etPhone.setText(String.valueOf(phoneNumber));
            }
        }

        Boolean notifications = (Boolean) data.get("notifications");
        if (notifications != null) {
            cbNotifications.setChecked(notifications);
        }

        Boolean isAdmin = (Boolean) data.get("admin");
        if (isAdmin == null) { // Default to false if null
            isAdmin = false;
        }
        if (isAdmin) {
            tvAdminStatus.setText("Admin Status: Yes");
        } else {
            tvAdminStatus.setText("Admin Status: Regular User");
        }

        String base64Image = (String) data.get("profileImage");
        if (base64Image != null) {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Glide.with(this)
                    .load(decodedByte)
                    .circleCrop()
                    .into(ivProfileImage);
        }
    }

    private void removeProfileImage() {
        selectedImageUri = null;
        ivProfileImage.setImageResource(R.drawable.default_placeholder); // Use placeholder image
        Toast.makeText(this, "Profile image removed", Toast.LENGTH_SHORT).show();
    }

    private void showUploadImageDialog() {
        String[] options = {"Upload from Device", "Enter Image URL"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload Profile Picture");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                checkPermissionAndPickImage();
            } else if (which == 1) {
                promptForImageUrl();
            }
        });
        builder.show();
    }

    private void promptForImageUrl() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Image URL");

        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String url = input.getText().toString();
            if (!url.isEmpty()) {
                Glide.with(this).load(url).circleCrop().into(ivProfileImage);
                selectedImageUri = Uri.parse(url); // Store the URI for saving later
            } else {
                Toast.makeText(this, "URL cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void checkPermissionAndPickImage() {
        // For Android 13 and above (API 33+)
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
            } else {
                launchImagePicker();
            }
        }
        // For Android 12 and below
        else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                launchImagePicker();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker();
            } else {
                Toast.makeText(this, "Permission denied - cannot select image", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void copyFromOrganizerProfile() {
        db.collection("organizers").document(deviceId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        etName.setText(document.getString("name"));
                        etEmail.setText(document.getString("email"));
                        Long phoneNumber = document.getLong("phoneNumber");
                        if (phoneNumber != null) {
                            if(phoneNumber == 0) {
                                etPhone.setText("");
                            }
                            else{
                                etPhone.setText(String.valueOf(phoneNumber));
                                }
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
                        Boolean notifications = document.getBoolean("notifications");
                        if (notifications != null) {
                            cbNotifications.setChecked(notifications);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error copying organizer data", e));
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
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            return;
        }

        // Validate Phone Number (only if provided)
        if (!phoneStr.isEmpty()) {
            if (!phoneStr.matches("\\d{9,}")) { // Ensures at least 9 digits
                etPhone.setError("Phone number must be at least 9 digits");
                return;
            }
        }

        // First, get the current admin status from Firestore
        db.collection("entrants").document(deviceId).get()
                .addOnSuccessListener(document -> {
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("deviceId", deviceId);
                    userData.put("name", name);
                    userData.put("email", email);
                    userData.put("phoneNumber", phoneStr.isEmpty() ? 0 : Long.parseLong(phoneStr));
                    userData.put("notifications", cbNotifications.isChecked());

                    // Preserve existing admin status if document exists, otherwise use intent extra
                    if (document.exists() && document.contains("admin")) {
                        userData.put("admin", document.getBoolean("admin"));
                    } else {
                        userData.put("admin", getIntent().getBooleanExtra("isAdmin", false));
                    }

                    if (selectedImageUri != null) {
                        handleImageInFirestore(userData);
                    } else {
                        userData.put("profileImage", generateDefaultProfilePicture(name));
                        saveProfileData(userData);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking admin status", e);
                    Toast.makeText(this, "Error saving profile", Toast.LENGTH_SHORT).show();
                });
    }

    private void handleImageInFirestore(Map<String, Object> userData) {
        if (selectedImageUri == null) {
            Log.e(TAG, "No image URI selected");
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri.toString().startsWith("http")) {
            // Handle URL image
            fetchBitmapFromUrl(selectedImageUri.toString(), bitmap -> {
                if (bitmap != null) {
                    saveBitmapToFirestore(userData, bitmap);
                } else {
                    Toast.makeText(this, "Error fetching image from URL", Toast.LENGTH_SHORT).show();
                    // Fall back to default profile picture
                    userData.put("profileImage", generateDefaultProfilePicture(etName.getText().toString().trim()));
                    saveProfileData(userData);
                }
            });
        } else {
            // Handle device URI
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                if (bitmap == null) {
                    throw new IOException("Failed to create bitmap from URI");
                }
                saveBitmapToFirestore(userData, bitmap);
            } catch (IOException | SecurityException e) {
                Log.e(TAG, "Error processing image from device: " + e.getMessage());
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
                // Fall back to default profile picture
                userData.put("profileImage", generateDefaultProfilePicture(etName.getText().toString().trim()));
                saveProfileData(userData);
            }
        }
    }

    // Helper method to fetch Bitmap from a URL
    private void fetchBitmapFromUrl(String url, BitmapCallback callback) {
        new Thread(() -> {
            try {
                java.net.URL imageUrl = new java.net.URL(url);
                Bitmap bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                runOnUiThread(() -> callback.onBitmapLoaded(bitmap));
            } catch (IOException e) {
                Log.e(TAG, "Error fetching image from URL", e);
                runOnUiThread(() -> callback.onBitmapLoaded(null));
            }
        }).start();
    }

    // Helper method to save a Bitmap to Firestore
    private void saveBitmapToFirestore(Map<String, Object> userData, Bitmap bitmap) {
        try {
            if (bitmap == null) {
                throw new IllegalArgumentException("Bitmap cannot be null");
            }

            // Resize bitmap for efficiency
            Bitmap resized = getResizedBitmap(bitmap, 500);
            if (resized == null) {
                throw new IllegalStateException("Failed to resize bitmap");
            }

            // Convert to base64
            String base64Image = bitmapToBase64(resized);
            if (base64Image == null || base64Image.isEmpty()) {
                throw new IllegalStateException("Failed to convert bitmap to base64");
            }

            // Add to Firestore document
            userData.put("profileImage", base64Image);
            saveProfileData(userData);

            // Clean up
            if (resized != bitmap) {
                resized.recycle();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in saveBitmapToFirestore: " + e.getMessage());
            Toast.makeText(this, "Error saving profile image", Toast.LENGTH_SHORT).show();
            // Fall back to default profile picture
            userData.put("profileImage", generateDefaultProfilePicture(etName.getText().toString().trim()));
            saveProfileData(userData);
        }
    }


    // Callback interface for asynchronous Bitmap fetching
    interface BitmapCallback {
        void onBitmapLoaded(Bitmap bitmap);
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

    private String generateDefaultProfilePicture(String name) {
        Bitmap bitmap = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        canvas.drawColor(Color.GREEN);
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        paint.setTextAlign(Paint.Align.CENTER);

        char initial = name.isEmpty() ? '?' : name.charAt(0);
        canvas.drawText(String.valueOf(initial).toUpperCase(), 60, 70, paint);

        return bitmapToBase64(bitmap);
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
}
