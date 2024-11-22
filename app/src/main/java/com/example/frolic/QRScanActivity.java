package com.example.frolic;

import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * QRScanActivity displays a QR code and provides a button for scanning QR codes.
 */
public class QRScanActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CAMERA = 1;

    private ImageView qrCodeImageView;
    private Button scanQRCodeButton;
    private Button btnBack;  // Back button declaration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scan);  // Use the provided layout file

        qrCodeImageView = findViewById(R.id.ivQRCode);
        scanQRCodeButton = findViewById(R.id.btnScanQRCode);
        btnBack = findViewById(R.id.btnBack);  // Initialize the back button

        // Set up the back button click listener
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(QRScanActivity.this, EntrantDashboardActivity.class);
            startActivity(intent);
            finish();
        });

        // Retrieve event data from the Intent
        String eventData = getIntent().getStringExtra("eventData"); // "eventData is qrHash

        if (eventData != null && !eventData.isEmpty()) {
            // Generate the QR code from the event data
            Bitmap qrCodeBitmap = QRCodeGenerator.generateQRCode(eventData);
            if (qrCodeBitmap != null) {
                qrCodeImageView.setImageBitmap(qrCodeBitmap);
            } else {
                Toast.makeText(this, "Failed to generate QR code.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No data provided for QR code generation.", Toast.LENGTH_SHORT).show();
        }

        // Set up button click listener for QR code scanning
        scanQRCodeButton.setOnClickListener(v -> {
            // Implement your QR code scanning logic here
            Toast.makeText(this, "QR code scanning feature not implemented yet.", Toast.LENGTH_SHORT).show();
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                initQRCodeScanner();
            }
        } else {
            initQRCodeScanner();
        }
    }
    private void initQRCodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Scan a QR code");
        integrator.initiateScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initQRCodeScanner();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            String scannedEventId = result.getContents(); // Extract eventId from scanned QR code
            fetchEventDetails(scannedEventId);
        } else {
            Toast.makeText(this, "QR code scan cancelled or invalid.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fetches event details using the scanned eventId and transitions to EventDetailsActivity.
     */
    private void fetchEventDetails(String qrHash) {
        if (qrHash == null || qrHash.isEmpty()) {
            Toast.makeText(this, "Invalid QR code scanned.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .whereEqualTo("qrHash", qrHash)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0); // Get the first matching document
                        Event event = document.toObject(Event.class);
                        if (event != null) {
                            // Navigate to EventDetailsActivity with event data
                            Intent intent = new Intent(QRScanActivity.this, EventDetailsActivity.class);
                            intent.putExtra("eventId", event.getEventId());
                            intent.putExtra("eventName", event.getEventName());
                            intent.putExtra("organizerId", event.getOrganizerId());
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Failed to parse event details.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w("QRScanActivity", "No event found for qrHash: " + qrHash);
                        Toast.makeText(this, "No event found for the scanned QR code.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("QRScanActivity", "Error fetching event details", e);
                    Toast.makeText(this, "Error fetching event details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}


