package com.example.frolic;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * DisplayEventQrCodeActivity retrieves a QR hash associated with an event from Firestore
 * and generates a QR code based on this hash. The QR code is displayed on the screen and can
 * be downloaded if needed. The activity includes a back button to return to the previous screen.
 */
public class DisplayEventQrCodeActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String eventId;

    /**
     * Initializes the activity, sets up UI elements, and retrieves the QR code hash associated
     * with the event ID provided in the intent. If the event ID is missing, the activity finishes
     * with an error message.
     *
     * @param savedInstanceState the saved instance state for activity recreation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_event_qr_code);

        db = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("eventId");

        // Retrieve QR Hash and generate the QR code
        if (eventId != null) {
            retrieveQrHashAndGenerateCode();
        } else {
            Toast.makeText(this, "Event ID is missing", Toast.LENGTH_SHORT).show();
            finish();
        }

        Button btnDownloadQrCode = findViewById(R.id.btnDownloadQrCode);
        btnDownloadQrCode.setOnClickListener(v -> downloadQrCode());

        TextView tvBack = findViewById(R.id.tvBack);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Queries Firestore to retrieve the `qrHash` for the event based on `eventId`,
     * then generates and displays the QR code on the screen. If the event or QR hash is
     * missing, an error message is shown.
     */
    private void retrieveQrHashAndGenerateCode() {
        db.collection("events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String qrHash = documentSnapshot.getString("qrHash");
                        if (qrHash != null) {
                            displayQrCode(qrHash);
                        } else {
                            Toast.makeText(this, "QR hash not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to retrieve QR hash", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    /**
     * Generates and displays the QR code image based on the provided QR hash. The QR code is
     * displayed in the ImageView component on the screen. Shows an error message if QR code
     * generation fails.
     *
     * @param qrHash the hash string to encode in the QR code
     */
    private void displayQrCode(String qrHash) {
        Bitmap qrBitmap = QRCodeGenerator.generateQRCode(qrHash);
        if (qrBitmap != null) {
            ImageView ivQrCode = findViewById(R.id.ivQrCode);
            ivQrCode.setImageBitmap(qrBitmap);
        } else {
            Toast.makeText(this, "Error generating QR code", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Downloads the displayed QR code to the device's storage.
     */
    private void downloadQrCode() {
        Toast.makeText(this, "Download functionality not yet implemented", Toast.LENGTH_SHORT).show();
    }
}