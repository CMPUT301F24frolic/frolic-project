package com.example.frolic;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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
    private EditText etEventName, etEventDate, etLastDateRegistration, etVacancy, etPrice, etWaitlistLimit;
    private CheckBox cbGeolocationRequired, cbNotification;
    private Button btnListEvent;

    private Date eventDate;
    private Date enrollDate;

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
    }

    /**
     * Creates an Event object with data from input fields, generates a hash for the QR code,
     * and saves the event to Firestore. Also provides feedback with Toast messages.
     */
    private void createAndSaveEvent() {
        // Capture data from input fields
        String eventName = etEventName.getText().toString();
        String eventDesc = "Description for the event"; // Placeholder, need textbox?
        String vacancyText = etVacancy.getText().toString();
        int maxConfirmed = vacancyText.isEmpty() ? 0 : Integer.parseInt(vacancyText);
        String waitlistLimitText = etWaitlistLimit.getText().toString();
        int waitlistLimit = waitlistLimitText.isEmpty() ? -1 : Integer.parseInt(waitlistLimitText);
        boolean geolocationRequired = cbGeolocationRequired.isChecked();
        boolean receiveNotification = cbNotification.isChecked();

        //Replace organizer with actual organizer, this is for TESTING PURPOSES ONLY
        Identity mockIdentity = new Identity("MockDeviceID");
        Organizer organizer = new Organizer(mockIdentity);

        // Generate a unique event ID using Firestore
        DocumentReference newEventRef = db.collection("events").document();
        String eventId = newEventRef.getId();  // Unique ID generated by Firestore

        // Create a hash of the event id which will be what the QR code is made from
        String QRCodeHash = String.valueOf(eventId.hashCode());

        // Create the Event object
        // Create the Event object with the available parameters
        // Example setup for variables in listactivity.java
        String organizerId = "someOrganizerId"; // Replace with actual logic to get organizer ID
        String facilityId = "someFacilityId";   // Replace with actual logic to get facility ID
        String qrHash = "someQrHash";           // Replace with actual logic to get QR hash

// Ensure these values are available before instantiating Event

        Event event = new Event(
                eventId,
                organizerId,          // Assuming you have the organizer's ID as a String
                facilityId,           // Provide the facility ID if available
                eventName,
                maxConfirmed,
                waitlistLimit,
                eventDate,
                enrollDate,
                geolocationRequired,
                receiveNotification,
                qrHash                 // Assuming QRCodeHash is intended to be qrHash
        );


        // Save the event to Firestore
        newEventRef.set(event.toMap())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event created successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving event.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
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
                    !etVacancy.getText().toString().isEmpty() &&
                    !etPrice.getText().toString().isEmpty();

            // Enable or disable the button based on the above condition
            btnListEvent.setEnabled(isReady);
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };
}
