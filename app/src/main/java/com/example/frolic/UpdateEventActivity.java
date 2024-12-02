package com.example.frolic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity for updating the image of an event. Allows users to view the current event image,
 * select a new image, and save the updated image to Firestore.
 */
public class UpdateEventActivity extends AppCompatActivity {

    private static final String TAG = "UpdateEventActivity";

    private String eventId;
    private ImageView ivCurrentImage;
    private Uri selectedImageUri;
    private FirebaseFirestore db;

    /**
     * Launches the image picker for selecting a new event image.
     */
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivCurrentImage.setImageURI(selectedImageUri); // Display selected image
                }
            }
    );

    /**
     * Initializes the activity, sets up UI components, and retrieves the current event image.
     *
     * @param savedInstanceState The saved instance state for restoring the activity state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_info_screen);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve eventId from intent
        eventId = getIntent().getStringExtra("eventId");

        // Set up UI elements
        ivCurrentImage = findViewById(R.id.ivCurrentImage);
        TextView btnUpdateImage = findViewById(R.id.btnUpdateImage);
        Button btnSave = findViewById(R.id.btnSave);
        TextView tvBack = findViewById(R.id.tvBack);

        // Set up Back button
        tvBack.setOnClickListener(v -> finish());

        // Load current event image
        loadCurrentEventImage();

        // Set up button listeners
        btnUpdateImage.setOnClickListener(v -> pickNewImage());
        btnSave.setOnClickListener(v -> saveUpdatedImage());
    }

    /**
     * Loads the current event image from Firestore and displays it. If no image exists,
     * an error message is shown.
     */
    private void loadCurrentEventImage() {
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("eventImageUrl")) {
                        String base64Image = documentSnapshot.getString("eventImageUrl");
                        if (base64Image != null) {
                            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                            Glide.with(this)
                                    .load(decodedString)
                                    .placeholder(R.drawable.default_placeholder)
                                    .into(ivCurrentImage);
                        }
                    } else {
                        Log.e(TAG, "No image found for event");
                        Toast.makeText(this, "No image found for this event", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading current event image", e);
                    Toast.makeText(this, "Failed to load event image", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Opens the image picker to allow the user to select a new image for the event.
     */
    private void pickNewImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    /**
     * Saves the selected image to Firestore by converting it to a Base64-encoded string.
     * If no image is selected, an error message is displayed.
     */
    private void saveUpdatedImage() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Convert the selected image to Base64
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            String base64Image = bitmapToBase64(bitmap);

            // Update the Firestore document
            Map<String, Object> updates = new HashMap<>();
            updates.put("eventImageUrl", base64Image);

            db.collection("events").document(eventId)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Event image updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error updating event image", e);
                        Toast.makeText(this, "Failed to update event image", Toast.LENGTH_SHORT).show();
                    });

        } catch (IOException e) {
            Log.e(TAG, "Error processing image", e);
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Converts a Bitmap to a Base64-encoded string.
     *
     * @param bitmap The Bitmap to encode.
     * @return The Base64 string.
     */
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
