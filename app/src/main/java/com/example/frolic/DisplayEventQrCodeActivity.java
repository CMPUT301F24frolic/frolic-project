package com.example.frolic;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

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
        // Get the ImageView containing the QR code
        ImageView ivQrCode = findViewById(R.id.ivQrCode);
        ivQrCode.setDrawingCacheEnabled(true);
        Bitmap bitmap = ivQrCode.getDrawingCache();

        if (bitmap != null) {
            try {
                // Generate a unique filename
                String fileName = "EventQRCode_" + eventId + ".png";

                // Save the image to the device's Pictures directory
                String savedPath = saveImageToGallery(bitmap, fileName);

                if (savedPath != null) {
                    Toast.makeText(this, "QR code saved to: " + savedPath, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Failed to save QR code", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error saving QR code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "QR code image not available", Toast.LENGTH_SHORT).show();
        }
        ivQrCode.setDrawingCacheEnabled(false);
    }

    /**
     * Saves the given bitmap to the device's Pictures directory as a PNG file.
     *
     * @param bitmap   the bitmap to save
     * @param fileName the name of the file
     * @return the path to the saved file, or null if saving failed
     */
    private String saveImageToGallery(Bitmap bitmap, String fileName) {
        String savedPath = null;

        // For Android 10 and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            try {
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/FrolicEventQRs");

                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                if (imageUri != null) {
                    OutputStream outputStream = resolver.openOutputStream(imageUri);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.close();
                    savedPath = imageUri.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // For Android versions below Q
            File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File qrDir = new File(picturesDir, "FrolicEventQRs");
            if (!qrDir.exists()) {
                qrDir.mkdirs();
            }
            File imageFile = new File(qrDir, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
                savedPath = imageFile.getAbsolutePath();

                // Make the file visible in the gallery
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(imageFile));
                sendBroadcast(mediaScanIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return savedPath;
    }
}
