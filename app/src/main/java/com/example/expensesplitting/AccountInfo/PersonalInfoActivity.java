package com.example.expensesplitting.AccountInfo;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PersonalInfoActivity extends AppCompatActivity {

    private ImageView profilePicture, editPictureIcon;
    private EditText fullName, email, phoneNumber, dateOfBirth;
    private Button saveButton;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI components
        profilePicture = findViewById(R.id.profilePicture);
        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);
        dateOfBirth = findViewById(R.id.dateOfBirth);
        saveButton = findViewById(R.id.saveButton);

        // Load user data from Firebase
        loadUserData();

        // Date Picker for Date of Birth
        dateOfBirth.setOnClickListener(v -> showDatePicker());

        // Save Button
        saveButton.setOnClickListener(v -> updateUserInfo());
    }

    @SuppressLint("SetTextI18n")
    private void loadUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            firestore.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            fullName.setText(documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("LastName"));
                            email.setText(documentSnapshot.getString("EmailAddress"));
                            phoneNumber.setText(documentSnapshot.getString("Phone"));
                            dateOfBirth.setText(documentSnapshot.getString("DateOfBirth"));
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show());
        }
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            dateOfBirth.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void updateUserInfo() {
        String updatedFullName = fullName.getText().toString().trim();
        String updatedEmail = email.getText().toString().trim();
        String updatedPhone = phoneNumber.getText().toString().trim();
        String updatedDOB = dateOfBirth.getText().toString().trim();

        if (TextUtils.isEmpty(updatedFullName) || TextUtils.isEmpty(updatedEmail) ||
                TextUtils.isEmpty(updatedPhone) || TextUtils.isEmpty(updatedDOB)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            Map<String, Object> updatedUserData = new HashMap<>();
            updatedUserData.put("FirstName", updatedFullName.split(" ")[0]); // Extract first name
            if (updatedFullName.split(" ").length > 1) {
                updatedUserData.put("LastName", updatedFullName.split(" ")[1]); // Extract last name
            }
            updatedUserData.put("EmailAddress", updatedEmail);
            updatedUserData.put("Phone", updatedPhone);
            updatedUserData.put("DateOfBirth", updatedDOB);

            firestore.collection("users").document(userId)
                    .update(updatedUserData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "User info updated successfully", Toast.LENGTH_SHORT).show();
                        // Navigate back to AccountActivity
                        Intent intent = new Intent(PersonalInfoActivity.this, AccountActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update user info", Toast.LENGTH_SHORT).show());
        }
    }

    public void backtoUserProfile(View view) {
        finish();
    }
}