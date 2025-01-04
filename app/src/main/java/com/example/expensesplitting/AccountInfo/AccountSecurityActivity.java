package com.example.expensesplitting.AccountInfo;

import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Login.SignIn;
import com.example.expensesplitting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountSecurityActivity extends AppCompatActivity {

    private Switch rememberMeSwitch, biometricIdSwitch, faceIdSwitch, smsAuthenticatorSwitch, googleAuthenticatorSwitch;
    private LinearLayout changePasswordLayout, deviceManagementLayout, deleteAccountLayout;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);

        firestore= FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        // Initialize Switches
        rememberMeSwitch = findViewById(R.id.rememberMeSwitch);
        biometricIdSwitch = findViewById(R.id.biometricIdSwitch);
        faceIdSwitch = findViewById(R.id.faceIdSwitch);
        smsAuthenticatorSwitch = findViewById(R.id.smsAuthenticatorSwitch);
        googleAuthenticatorSwitch = findViewById(R.id.googleAuthenticatorSwitch);

        // Initialize Clickable Rows
        changePasswordLayout = findViewById(R.id.changePasswordLayout);
        deviceManagementLayout = findViewById(R.id.deviceManagementLayout);
        deleteAccountLayout = findViewById(R.id.deleteAccountLayout);

        // Restore "Remember Me" switch state
        boolean isRememberMeOn = sharedPreferences.getBoolean("rememberMe", false);
        rememberMeSwitch.setChecked(isRememberMeOn);

        // Set Switch Listeners
        rememberMeSwitch.setOnCheckedChangeListener(this::onSwitchChanged);
        biometricIdSwitch.setOnCheckedChangeListener(this::onSwitchChanged);
        faceIdSwitch.setOnCheckedChangeListener(this::onSwitchChanged);
        smsAuthenticatorSwitch.setOnCheckedChangeListener(this::onSwitchChanged);
        googleAuthenticatorSwitch.setOnCheckedChangeListener(this::onSwitchChanged);

        // Set Click Listeners
        changePasswordLayout.setOnClickListener(v -> navigateToChangePassword());
        deviceManagementLayout.setOnClickListener(v -> navigateToDeviceManagement());
        deleteAccountLayout.setOnClickListener(v -> confirmDeleteAccount());
    }

    private void onSwitchChanged(CompoundButton buttonView, boolean isChecked) {
        String featureName = "";

        if (buttonView.getId() == R.id.rememberMeSwitch) {
            featureName = "Remember Me";
            sharedPreferences.edit().putBoolean("rememberMe", isChecked).apply(); // Save "Remember Me" state
        } else if (buttonView.getId() == R.id.biometricIdSwitch) {
            featureName = "Biometric ID";
        } else if (buttonView.getId() == R.id.faceIdSwitch) {
            featureName = "Face ID";
        } else if (buttonView.getId() == R.id.smsAuthenticatorSwitch) {
            featureName = "SMS Authenticator";
        } else if (buttonView.getId() == R.id.googleAuthenticatorSwitch) {
            featureName = "Google Authenticator";
        }

        String status = isChecked ? "enabled" : "disabled";
        Toast.makeText(this, featureName + " " + status, Toast.LENGTH_SHORT).show();
    }

    private void navigateToChangePassword() {
        Toast.makeText(this, "Navigate to Change Password", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ChangePasswordActivity.class));
    }

    private void navigateToDeviceManagement() {
        Toast.makeText(this, "Navigate to Device Management", Toast.LENGTH_SHORT).show();
         startActivity(new Intent(this, DeviceManagementActivity.class));
    }

    private void confirmDeleteAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Account");
        builder.setMessage("Are you sure you want to delete your account? This action cannot be undone.");

        builder.setPositiveButton("Delete", (dialog, which) -> deleteAccount());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        // Show the dialog first to customize the button colors
        dialog.show();

        // Customize button colors
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.gray));
    }

    private void deleteAccount() {
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            // Delete user data from Firestore
            firestore.collection("users").document(userId).delete()
                    .addOnSuccessListener(aVoid -> {
                        // Delete user from Firebase Authentication
                        auth.getCurrentUser().delete()
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, SignIn.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete account: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No user is currently logged in", Toast.LENGTH_SHORT).show();
        }
    }

    public void backtoUserProfile(android.view.View view) {
        startActivity(new Intent(this, AccountActivity.class));
    }
}