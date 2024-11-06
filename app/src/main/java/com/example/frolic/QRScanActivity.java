package com.example.frolic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * QRScanActivity displays a QR code and provides a button for scanning QR codes.
 */
public class QRScanActivity extends AppCompatActivity {

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
    }
}


