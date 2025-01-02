package com.example.expensesplitting.AccountInfo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText currentPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button changePasswordButton;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        progressBar = findViewById(R.id.progressBar);

        // Change Password Button Click
        changePasswordButton.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(currentPassword)) {
            currentPasswordEditText.setError("Enter your current password");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            newPasswordEditText.setError("Enter a new password");
            return;
        }

        if (newPassword.length() < 6) {
            newPasswordEditText.setError("Password must be at least 6 characters");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            // Re-authenticate the user
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            user.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        // Update the password in Firebase
                        user.updatePassword(newPassword)
                                .addOnCompleteListener(task -> {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ChangePasswordActivity.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                                        finish(); // Close the activity
                                    } else {
                                        Toast.makeText(ChangePasswordActivity.this, "Failed to change password: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ChangePasswordActivity.this, "The password you entered does not match your current password. Please try again", Toast.LENGTH_SHORT).show();
                    });
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}