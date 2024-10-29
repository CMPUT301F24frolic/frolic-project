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
            // Create an intent to start the EditProfileActivity
            Intent editIntent = new Intent(EntrantProfileActivity.this, EntrantEditProfileActivity.class);

            // Pass the current profile data to the edit activity
            editIntent.putExtra("name", nameViewText.getText().toString());
            editIntent.putExtra("email", emailViewText.getText().toString());
            editIntent.putExtra("phone", phoneViewText.getText().toString());
            editIntent.putExtra("profileImageUri", profileImageUriString);

            // Start the EditProfileActivity and wait for the result
            startActivityForResult(editIntent, EDIT_PROFILE_REQUEST);
        });

    }

    // Handle the result from EditProfileActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Retrieve the updated profile data
            String updatedName = data.getStringExtra("updatedName");
            String updatedEmail = data.getStringExtra("updatedEmail");
            String updatedPhone = data.getStringExtra("updatedPhone");
            String updatedProfileImageUri = data.getStringExtra("updatedProfileImageUri");

            // Update the UI with the new data
            if (updatedName != null) {
                nameViewText.setText(updatedName);
            }
            if (updatedEmail != null) {
                emailViewText.setText(updatedEmail);
            }
            if (updatedPhone != null) {
                phoneViewText.setText(updatedPhone);
            }
            if (updatedProfileImageUri != null) {
                Uri profileImageUri = Uri.parse(updatedProfileImageUri);
                profileImageView.setImageURI(profileImageUri);
            }
        }

        // Set listener for "View events" button
        viewEventsButton.setOnClickListener(v -> {
            // view event page logic
        });
    }
}
