package com.example.frolic;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Activity to list a new event. Provides fields for event details,
 * options to add an image, geolocation, notifications, and QR code generation.
 * This activity allows organizers to create and customize events.
 */
public class ListEventActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private EditText etEventName, etEventDate, etLastDateRegistration, etVacancy, etWaitlistLimit;
    private CheckBox cbGeolocationRequired, cbNotification;
    private Button btnListEvent, btnAddImage;
    private String organizerId, facilityId;

    private Date eventDate;
    private Date enrollDate;

    private ImageView ivEventImage;
    private Uri selectedImageUri;

    /**
     * Initializes the activity and binds XML layout elements to variables for user interaction.
     * Sets up view references and prepares the UI for event listing.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     * this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}.
     * Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_event_screen);

        // Get organizerId from intent
        organizerId = getIntent().getStringExtra("deviceId");
        if (organizerId == null) {
            Toast.makeText(this, "Organizer ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        facilityId = getIntent().getStringExtra("facilityId");
        if (facilityId == null) {
            Toast.makeText(this, "Facility ID is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        etEventName = findViewById(R.id.etEventName);
        etEventDate = findViewById(R.id.etEventDate);
        etLastDateRegistration = findViewById(R.id.etLastDateRegistration);
        etVacancy = findViewById(R.id.etVacancy);
        etWaitlistLimit = findViewById(R.id.etWaitlistLimit);
        cbGeolocationRequired = findViewById(R.id.cbGeolocationRequired);
        cbNotification = findViewById(R.id.cbNotification);
        btnListEvent = findViewById(R.id.btnListEvent);

        // Disable the button initially
        btnListEvent.setEnabled(false);

        // Add TextWatcher to required fields
        etEventName.addTextChangedListener(textWatcher);
        etEventDate.addTextChangedListener(textWatcher);
        etLastDateRegistration.addTextChangedListener(textWatcher);
        etVacancy.addTextChangedListener(textWatcher);

        // Initially disable the Enroll Date field, user has to first select an event date
        etLastDateRegistration.setEnabled(false);

        initializeDatePickers();

        // Find views
        btnAddImage = findViewById(R.id.btnAddImage);
        ivEventImage = findViewById(R.id.ivEventImage);

        // Set click listener to add image
        btnAddImage.setOnClickListener(v -> checkPermissionAndPickImage());

        // Set up button click listener to capture input data
        btnListEvent.setOnClickListener(v -> {
            if (!btnListEvent.isEnabled()) {
                // Show toast message if required fields are not filled
                Toast.makeText(ListEventActivity.this, "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed to create and save the event
                createAndSaveEvent();
            }
        });

        TextView tvBack = findViewById(R.id.tvBack);
        tvBack.setOnClickListener(v -> {
            // Navigate back to the Organizer Dashboard
            Intent intent = new Intent(ListEventActivity.this, OrganizerDashboardActivity.class);
            intent.putExtra("deviceId", organizerId);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Checks for the necessary permissions to access media files based on the API level.
     * If permissions are not granted, it requests them from the user.
     * If permissions are granted, it opens the image picker.
     */
    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 100);
            } else {
                pickImage();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            } else {
                pickImage();
            }
        }
    }

    /**
     * Launches the image picker to allow the user to select an image from their gallery.
     */
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    /**
     * Handles the result of the image picker activity. If the user selects an image,
     * it retrieves the URI of the selected image and displays it in the ImageView.
     */
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivEventImage.setImageURI(selectedImageUri);
                }
            }
    );

    /**
     * Sets up the DatePicker for EventDate and EnrollDate fields. EventDate must be selected first,
     * and EnrollDate is restricted to the range between the current date and the selected EventDate.
     */
    private void initializeDatePickers() {
        // Make EditText for EventDate non-editable so that we use DatePickerDialog instead
        etEventDate.setFocusable(false);
        etEventDate.setOnClickListener(v -> showDatePickerDialog(
                (date, formattedDate) -> {
                    eventDate = date;
                    etEventDate.setText(formattedDate);

                    // Enable Enroll Date field and set its max date to the Event Date
                    etLastDateRegistration.setEnabled(true);
                    etLastDateRegistration.setOnClickListener(v1 -> showDatePickerDialog(
                            (selectedDate, formattedEnrollDate) -> {
                                enrollDate = selectedDate;
                                etLastDateRegistration.setText(formattedEnrollDate);
                            },
                            eventDate // Set max date to the selected Event Date
                    ));
                },
                null // No max date for Event Date
        ));
    }

    /**
     * Creates an Event object with data from input fields, generates a QR code hash,
     * and saves the event to Firestore. Also initializes a LotterySystem for the event.
     */
    private void createAndSaveEvent() {
        // Capture data from input fields
        String eventName = etEventName.getText().toString();
        String vacancyText = etVacancy.getText().toString();
        int maxConfirmed = vacancyText.isEmpty() ? 0 : Integer.parseInt(vacancyText);
        String waitlistLimitText = etWaitlistLimit.getText().toString();
        int waitlistLimit = waitlistLimitText.isEmpty() ? -1 : Integer.parseInt(waitlistLimitText);
        boolean geolocationRequired = cbGeolocationRequired.isChecked();
        boolean receiveNotification = cbNotification.isChecked();

        // Generate a unique event ID using Firestore
        DocumentReference newEventRef = db.collection("events").document();
        String eventId = newEventRef.getId();  // Unique ID generated by Firestore

        // Create a hash of the event id which will be what the QR code is made from
        String QRCodeHash = String.valueOf(eventId.hashCode());

        String imageBase64 = null;
        if (selectedImageUri != null) {
            imageBase64 = convertImageToBase64();
        }

        Event event = new Event(
                eventId,
                organizerId,
                facilityId,
                eventName,
                maxConfirmed,
                waitlistLimit,
                eventDate,
                enrollDate,
                geolocationRequired,
                receiveNotification,
                QRCodeHash,
                imageBase64
        );


        // Save the event to Firestore
        saveEventToFirestore(event, newEventRef, eventId);
    }

    // Helper method to save event to Firestore
    private void saveEventToFirestore(Event event, DocumentReference eventRef, String eventId) {
        eventRef.set(event.toMap())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event created successfully!", Toast.LENGTH_SHORT).show();

                    // Add the event ID to the organizer's eventsOrganizing list
                    db.collection("organizers").document(organizerId)
                            .update("eventsOrganizing", FieldValue.arrayUnion(eventId))
                            .addOnSuccessListener(aVoid2 -> Log.d("OrganizerUpdate", "Event ID added to organizer's eventsOrganizing list"))
                            .addOnFailureListener(e -> Log.e("OrganizerUpdate", "Error adding event ID to organizer", e));

                    // Initialize lottery system
                    initializeLotterySystem(event, eventId);

                    // Navigate back to the Organizer Dashboard
                    Intent intent = new Intent(ListEventActivity.this, OrganizerDashboardActivity.class);
                    intent.putExtra("deviceId", organizerId);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving event.", Toast.LENGTH_SHORT).show();
                    Log.e("EventSave", "Error saving event", e);
                });
    }

    private String convertImageToBase64() {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (IOException e) {
            Log.e("ImageConversion", "Error converting image to Base64", e);
            return null;
        }
    }

    /**
     * Initializes a LotterySystem for the event in Firestore using the event's ID and settings.
     *
     * @param event the Event object for which the LotterySystem is created
     */
    private void initializeLotterySystem(Event event, String eventId) {
        DocumentReference lotteryRef = db.collection("lotteries").document(eventId);
        LotterySystem lotterySystem = new LotterySystem(
                event.getLotterySystemId(),
                event.getEventId(),
                event.getMaxConfirmed(),
                event.getWaitlistLimit()
        );
        lotteryRef.set(lotterySystem.toMap())
                .addOnSuccessListener(aVoid -> Log.d("LotterySystem", "Lottery system initialized for event " + eventId))
                .addOnFailureListener(e -> Log.e("LotterySystem", "Error initializing lottery system for event " + eventId, e));
    }


    /**
     * Opens a DatePickerDialog to select a date. Sets restrictions to current or future dates.
     * For the enroll date, it restricts selection to a range between the current date and the event date.
     *
     * @param listener DateSetListener that handles the selected date.
     * @param maxDate  Optional maximum date for the DatePicker. If null, no max date is set.
     */
    private void showDatePickerDialog(DateSetListener listener, Date maxDate) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    Date date = calendar.getTime();

                    // Format date to display in EditText
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String formattedDate = dateFormat.format(date);
                    listener.onDateSet(date, formattedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        // Set maximum date if provided (for Enroll Date picker)
        if (maxDate != null) {
            datePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());
        }

        datePickerDialog.show();
    }

    /**
     * Interface for handling date selection in DatePickerDialog.
     */
    private interface DateSetListener {
        void onDateSet(Date date, String formattedDate);
    }

    /**
     * TextWatcher to monitor required fields and enable the "List Event" button
     * only when all required fields are filled.
     */
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Check if all required fields are filled
            boolean isReady = !etEventName.getText().toString().isEmpty() &&
                    !etEventDate.getText().toString().isEmpty() &&
                    !etLastDateRegistration.getText().toString().isEmpty() &&
                    !etVacancy.getText().toString().isEmpty();

            // Enable or disable the button based on the above condition
            btnListEvent.setEnabled(isReady);
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    @Override
    public void onBackPressed() {
        // Handle back button to return to organizer dashboard
        super.onBackPressed();
        Intent intent = new Intent(this, OrganizerDashboardActivity.class);
        intent.putExtra("deviceId", organizerId);
        startActivity(intent);
        finish();
    }
}