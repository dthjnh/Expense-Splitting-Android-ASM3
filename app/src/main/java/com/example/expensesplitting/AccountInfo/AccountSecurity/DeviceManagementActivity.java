package com.example.expensesplitting.AccountInfo.AccountSecurity;


import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Login.SignIn;
import com.example.expensesplitting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DeviceManagementActivity extends AppCompatActivity {

    private LinearLayout deviceListContainer;
    private FirebaseAuth auth;
    private Button logOutAllDevicesButton;
    private FirebaseFirestore firestore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_management);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        logOutAllDevicesButton = findViewById(R.id.logOutAllDevicesButton);
        logOutAllDevicesButton.setOnClickListener(v -> logOutAllDevices());

        deviceListContainer = findViewById(R.id.deviceListContainer);

        loadDevices();
    }

    private void loadDevices() {
        firestore.collection("devices")
                .whereEqualTo("userId", auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Device> devices = queryDocumentSnapshots.toObjects(Device.class);

                    deviceListContainer.removeAllViews();
                    for (Device device : devices) {
                        addDeviceItem(device);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load devices", Toast.LENGTH_SHORT).show());
    }

    private void addDeviceItem(Device device) {
        View deviceItemView = getLayoutInflater().inflate(R.layout.item_device, deviceListContainer, false);

        TextView deviceName = deviceItemView.findViewById(R.id.deviceName);
        TextView deviceDetails = deviceItemView.findViewById(R.id.deviceDetails);
        Button logOutButton = deviceItemView.findViewById(R.id.logOutDeviceButton);

        deviceName.setText(device.getName());
        deviceDetails.setText("Last login: " + device.getLastLogin());
        logOutButton.setOnClickListener(v -> logOutFromDevice(device));

        deviceListContainer.addView(deviceItemView);
    }

    private void logOutFromDevice(Device device) {
        Toast.makeText(this, "Logged out from " + device.getName(), Toast.LENGTH_SHORT).show();

        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, SignIn.class));
        finish();
    }

    private void logOutAllDevices() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("devices")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> Log.d("DeviceManagement", "Device logged out successfully."))
                                    .addOnFailureListener(e -> Log.e("DeviceManagement", "Failed to log out device: " + e.getMessage()));
                        }

                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(this, "Logged out from all devices", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(DeviceManagementActivity.this, SignIn.class));
                        finish();
                    } else {
                        Toast.makeText(this, "No devices found to log out", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DeviceManagement", "Failed to load devices: " + e.getMessage());
                    Toast.makeText(this, "Failed to log out from devices", Toast.LENGTH_SHORT).show();
                });
    }

}