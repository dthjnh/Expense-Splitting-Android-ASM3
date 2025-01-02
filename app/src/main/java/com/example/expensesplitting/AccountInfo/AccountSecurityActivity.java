package com.example.expensesplitting.AccountInfo;

import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.R;

public class AccountSecurityActivity extends AppCompatActivity {

    private Switch rememberMeSwitch, biometricIdSwitch, faceIdSwitch, smsAuthenticatorSwitch, googleAuthenticatorSwitch;
    private LinearLayout changePasswordLayout, deviceManagementLayout, deactivateAccountLayout, deleteAccountLayout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);

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
        deactivateAccountLayout = findViewById(R.id.deactivateAccountLayout);
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
        deactivateAccountLayout.setOnClickListener(v -> navigateToDeactivateAccount());
        deleteAccountLayout.setOnClickListener(v -> navigateToDeleteAccount());
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

    private void navigateToDeactivateAccount() {
        // Navigate to Deactivate Account Screen
        Toast.makeText(this, "Navigate to Deactivate Account", Toast.LENGTH_SHORT).show();
        // Example: startActivity(new Intent(this, DeactivateAccountActivity.class));
    }

    private void navigateToDeleteAccount() {
        // Navigate to Delete Account Screen
        Toast.makeText(this, "Navigate to Delete Account", Toast.LENGTH_SHORT).show();
        // Example: startActivity(new Intent(this, DeleteAccountActivity.class));
    }

    public void backtoUserProfile(android.view.View view) {
        startActivity(new Intent(this, AccountActivity.class));
    }
}