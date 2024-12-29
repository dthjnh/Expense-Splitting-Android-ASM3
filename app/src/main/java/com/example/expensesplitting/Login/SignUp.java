package com.example.expensesplitting.Login;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Admin;
import com.example.expensesplitting.R;
import com.example.expensesplitting.UserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private EditText firstName, lastName, birthDate, address, emailAddress, phoneNumber, password, confirmPassword;
    private Button btnSignUp;
    private TextView tvAlreadyUser;
    private ImageView passwordToggle, confirmPasswordToggle;
    private ImageButton btnDatePicker;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        address = findViewById(R.id.address);
        birthDate = findViewById(R.id.birthDate);
        emailAddress = findViewById(R.id.emailAddress);
        phoneNumber = findViewById(R.id.phoneNumber);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        btnSignUp = findViewById(R.id.signUpButton);
        tvAlreadyUser = findViewById(R.id.tvAlreadyUser);
        passwordToggle = findViewById(R.id.passwordToggle);
        confirmPasswordToggle = findViewById(R.id.confirmPasswordToggle);
        btnDatePicker = findViewById(R.id.btnDatePicker);

        // Toggle password visibility
        passwordToggle.setOnClickListener(v -> togglePasswordVisibility(password, passwordToggle));
        confirmPasswordToggle.setOnClickListener(v -> togglePasswordVisibility(confirmPassword, confirmPasswordToggle));

        // Date Picker Functionality
        btnDatePicker.setOnClickListener(v -> showDatePicker());

        // Sign Up Button Click Listener
        btnSignUp.setOnClickListener(v -> {
            String fName = firstName.getText().toString();
            String lName = lastName.getText().toString();
            String dob = birthDate.getText().toString();
            String addr = address.getText().toString();
            String email = emailAddress.getText().toString();
            String phone = phoneNumber.getText().toString();
            String pwd = password.getText().toString();
            String confirmPwd = confirmPassword.getText().toString();

            if (fName.isEmpty() || lName.isEmpty() || dob.isEmpty() || email.isEmpty() || addr.isEmpty() || phone.isEmpty() || pwd.isEmpty() || confirmPwd.isEmpty()) {
                Toast.makeText(SignUp.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pwd.equals(confirmPwd)) {
                Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create user in Firebase Authentication
            auth.createUserWithEmailAndPassword(email, pwd)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            saveUserDetails(userId, fName, lName, phone, dob, addr, email, pwd, confirmPwd);

                            // Navigate based on email domain
                            if (email.endsWith("@admin.com")) {
                                startActivity(new Intent(SignUp.this, Admin.class));
                                Toast.makeText(SignUp.this, "Logged in as Admin", Toast.LENGTH_SHORT).show();
                            } else {
                                startActivity(new Intent(SignUp.this, UserActivity.class));
                                Toast.makeText(SignUp.this, "Logged in as User", Toast.LENGTH_SHORT).show();
                            }

                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SignUp.this, "Failed to sign up: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Navigate to Sign In Screen
        tvAlreadyUser.setOnClickListener(v -> finish());
    }

    // Method to Show Date Picker Dialog
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(SignUp.this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            birthDate.setText(selectedDate); // Set the selected date in EditText
        }, year, month, day);

        datePickerDialog.show();
    }

    // Toggle Password Visibility
    private void togglePasswordVisibility(EditText editText, ImageView toggleIcon) {
        if (editText.getTransformationMethod() instanceof PasswordTransformationMethod) {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            toggleIcon.setImageResource(R.drawable.ic_eye_off);
        } else {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            toggleIcon.setImageResource(R.drawable.ic_visibility);
        }
        editText.setSelection(editText.length());
    }

    // Save user details to Firestore
    private void saveUserDetails(String userId, String firstName, String lastName, String phone, String dateOfBirth, String address, String emaiAddress, String password, String confirmPassword) {
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("FirstName", firstName);
        userDetails.put("LastName", lastName);
        userDetails.put("Phone", phone);
        userDetails.put("Address", address);
        userDetails.put("EmailAddress", emaiAddress);
        userDetails.put("DateOfBirth", dateOfBirth);
        userDetails.put("Password", password);
        userDetails.put("ConfirmPassword", confirmPassword);

        firestore.collection("users").document(userId)
                .set(userDetails)
                .addOnSuccessListener(aVoid -> Log.d("SignUp", "User details saved successfully"))
                .addOnFailureListener(e -> Log.e("SignUp", "Error saving user details", e));
    }
}
