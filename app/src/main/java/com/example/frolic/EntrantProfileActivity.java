package com.example.frolic;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;

public class EntrantProfileActivity extends Activity {
    private TextView nameViewText, emailViewText, phoneViewText;
    private ImageView profileImageView;
    private ImageView profileImageEdit;
    private Button editProfileButton, viewEventsButton;

    // Request code for Edit Profile
    private static final int EDIT_PROFILE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_profile);

        nameViewText = findViewById(R.id.tvUserName);
        emailViewText = findViewById(R.id.tvUserEmail);
        phoneViewText = findViewById(R.id.tvUserMobile);
        profileImageView = findViewById(R.id.ivProfileImage);
        editProfileButton = findViewById(R.id.btnEditProfile);
        viewEventsButton = findViewById(R.id.btnViewEvent);

        // Get user data from intent
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");
        String profileImageUriString = intent.getStringExtra("profileImageUri");
        String deviceId = intent.getStringExtra("deviceId");

        // Set user data in views
        if (name != null) {
            nameViewText.setText(name);
        }
        if (email != null) {
            emailViewText.setText(email);
        }
        if (phone != null) {
            phoneViewText.setText(phone);
        }
        if (profileImageUriString != null) {
            Uri profileImageUri = Uri.parse(profileImageUriString);
            profileImageView.setImageURI(profileImageUri);
        }

        // Set listener for "Edit Profile" button
        editProfileButton.setOnClickListener(v -> {
            // Create an intent to start the EntrantEditProfile activity
            Intent editIntent = new Intent(EntrantProfileActivity.this, EntrantEditProfile.class);

            // Pass the current profile data and deviceId to the edit activity
            editIntent.putExtra("deviceId", deviceId);
            startActivity(editIntent);
            // No need for startActivityForResult as EntrantEditProfile handles Firebase directly
        });

        // Set listener for "View events" button
        viewEventsButton.setOnClickListener(v -> {
            // view event page logic
        });
    }
}