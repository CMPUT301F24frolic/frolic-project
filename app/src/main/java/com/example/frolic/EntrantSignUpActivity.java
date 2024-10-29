package com.example.frolic;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class EntrantSignUpActivity extends Activity{

    private EditText nameEditText, emailEditText, phoneEditText;
    private ImageView profileImageView;
    private Button saveChangesButton, addProfileImageButton;

    // Request code for selecting an image
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_sign_up);

        // Initialize views
        nameEditText = findViewById(R.id.etUserName);
        emailEditText = findViewById(R.id.etUserEmail);
        phoneEditText = findViewById(R.id.etUserMobile);
        profileImageView = findViewById(R.id.ivProfileImage);
        saveChangesButton = findViewById(R.id.btnSaveChanges);
        addProfileImageButton = findViewById(R.id.btnAddProfileImage);

        addProfileImageButton.setOnClickListener(v -> openImageSelector());
        saveChangesButton.setOnClickListener(v -> saveUserData());

    }

    // Method to open image selector for profile picture
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
            profileImageView.setImageURI(selectedImageUri);
        }
    }

    // Method to save user data and navigate to EntrantProfileActivity
    private void saveUserData() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        // Create an intent to pass data to the EntrantProfileActivity
        Intent intent = new Intent(EntrantSignUpActivity.this, EntrantProfileActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("email", email);
        intent.putExtra("phone", phone);
        if (selectedImageUri != null) {
            intent.putExtra("profileImageUri", selectedImageUri.toString()); // Pass the image URI as a string
        }

        // Navigate to EntrantProfileActivity
        startActivity(intent);
        finish();
    }
}

