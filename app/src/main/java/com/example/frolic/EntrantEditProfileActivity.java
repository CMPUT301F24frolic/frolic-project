package com.example.frolic;

import android.widget.Button;
import android.widget.EditText;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class EntrantEditProfileActivity extends Activity {

    private EditText editNameEditText, editEmailEditText, editPhoneEditText;
    private ImageView editProfileImageView;
    private Button saveChangesButton, selectNewImageButton;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_edit_profile);

        // Initialize views
        editNameEditText = findViewById(R.id.etUserName);
        editEmailEditText = findViewById(R.id.etUserEmail);
        editPhoneEditText = findViewById(R.id.etUserMobile);
        editProfileImageView = findViewById(R.id.ivProfileImage);
        saveChangesButton = findViewById(R.id.btnSaveChanges);
        selectNewImageButton = findViewById(R.id.btnEditProfileImage);


        Intent intent = getIntent();
        String currentName = intent.getStringExtra("name");
        String currentEmail = intent.getStringExtra("email");
        String currentPhone = intent.getStringExtra("phone");
        String currentProfileImageUri = intent.getStringExtra("profileImageUri");


        if (currentName != null) {
            editNameEditText.setText(currentName);
        }
        if (currentEmail != null) {
            editEmailEditText.setText(currentEmail);
        }
        if (currentPhone != null) {
            editPhoneEditText.setText(currentPhone);
        }
        if (currentProfileImageUri != null) {
            Uri profileImageUri = Uri.parse(currentProfileImageUri);
            editProfileImageView.setImageURI(profileImageUri);
        }

        // Set listener for selecting a new profile image
        selectNewImageButton.setOnClickListener(v -> openImageSelector());

        // Set listener for saving changes
        saveChangesButton.setOnClickListener(v -> saveChanges());
    }

    // Method to open image selector
    private void openImageSelector() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            editProfileImageView.setImageURI(selectedImageUri);
        }
    }
    // Method to save changes and return data to EntrantProfileActivity
    private void saveChanges() {
        // Get updated data
        String updatedName = editNameEditText.getText().toString().trim();
        String updatedEmail = editEmailEditText.getText().toString().trim();
        String updatedPhone = editPhoneEditText.getText().toString().trim();

        // Prepare the result intent to send back to EntrantProfileActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedName", updatedName);
        resultIntent.putExtra("updatedEmail", updatedEmail);
        resultIntent.putExtra("updatedPhone", updatedPhone);

        if (selectedImageUri != null) {
            resultIntent.putExtra("updatedProfileImageUri", selectedImageUri.toString());
        }

        // Set the result and finish the activity
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
