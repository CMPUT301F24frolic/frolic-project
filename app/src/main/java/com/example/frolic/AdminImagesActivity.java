package com.example.frolic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class AdminImagesActivity extends AppCompatActivity {
    private static final String TAG = "AdminImagesActivity";
    private FirebaseFirestore db;
    private RecyclerView rvImages;
    private AdminImagesAdapter adapter;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_images_view);

        db = FirebaseFirestore.getInstance();
        initializeViews();
        setupRecyclerView();
        loadImagesFromFirebase();
    }

    private void initializeViews() {
        rvImages = findViewById(R.id.rvImages);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupRecyclerView() {
        rvImages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminImagesAdapter(new ArrayList<>(), this::showDeleteConfirmation);
        rvImages.setAdapter(adapter);
    }

    private void loadImagesFromFirebase() {
        ArrayList<ImageData> images = new ArrayList<>();

        // Load entrant profile images
        db.collection("entrants")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot document : querySnapshot) {
                        String profileImage = document.getString("profileImage");
                        if (profileImage != null) {
                            images.add(new ImageData(profileImage, "Entrant Profile", document.getId(), "entrants"));
                        }
                    }
                    // Load event images
                    db.collection("events")
                            .get()
                            .addOnSuccessListener(eventSnapshot -> {
                                for (DocumentSnapshot document : eventSnapshot) {
                                    String eventImage = document.getString("eventImageUrl");
                                    if (eventImage != null) {
                                        images.add(new ImageData(eventImage, "Event Poster", document.getId(), "events"));
                                    }
                                }
                                adapter.updateImages(images); // Update adapter with all images
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "Error loading event images", e));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading entrant images", e);
                    Toast.makeText(this, "Error loading images", Toast.LENGTH_SHORT).show();
                });
    }

    private void showDeleteConfirmation(ImageData imageData) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Delete", (dialog, which) -> deleteImage(imageData))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteImage(ImageData imageData) {
        db.collection(imageData.getCollection())
                .document(imageData.getDocumentId())
                .update(imageData.getType().equals("Entrant Profile") ? "profileImage" : "eventImageUrl", null)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Image deleted successfully", Toast.LENGTH_SHORT).show();
                    loadImagesFromFirebase(); // Refresh the images
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting image", e);
                    Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show();
                });
    }
}

