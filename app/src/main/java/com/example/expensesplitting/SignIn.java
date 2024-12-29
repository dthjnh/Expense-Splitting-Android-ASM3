package com.example.expensesplitting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText editEmailSignIn, editPasswordSignIn;
    private Button btnSignIn;
    private TextView signUpPrompt, forgotPassword;
    private CheckBox rememberMeCheckBox;
    private ImageView imageViewTogglePassword;
    private boolean isPasswordVisible = false;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();

        editEmailSignIn = findViewById(R.id.editEmailSignIn);
        editPasswordSignIn = findViewById(R.id.editPasswordSignIn);
        btnSignIn = findViewById(R.id.btnSignIn);
        signUpPrompt = findViewById(R.id.signUpPrompt);
        forgotPassword = findViewById(R.id.forgotPassword);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        imageViewTogglePassword = findViewById(R.id.imageViewTogglePassword);


        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Clear input fields when logging out
        clearInputsOnLogout();

        forgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(SignIn.this, ForgotPassword.class));
        });

        // Auto-fill password when email is entered
        editEmailSignIn.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { // When the email field loses focus
                String enteredEmail = editEmailSignIn.getText().toString();
                if (enteredEmail.equals(sharedPreferences.getString("email", ""))) {
                    editPasswordSignIn.setText(sharedPreferences.getString("password", ""));
                } else {
                    editPasswordSignIn.setText("");
                }
            }
        });

        // Toggle password visibility
        imageViewTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                editPasswordSignIn.setTransformationMethod(PasswordTransformationMethod.getInstance());
                imageViewTogglePassword.setImageResource(R.drawable.ic_visibility);
            } else {
                editPasswordSignIn.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                imageViewTogglePassword.setImageResource(R.drawable.ic_eye_off);
            }
            isPasswordVisible = !isPasswordVisible;
            editPasswordSignIn.setSelection(editPasswordSignIn.length());
        });

        // Sign In button functionality
        btnSignIn.setOnClickListener(v -> {
            String email = editEmailSignIn.getText().toString().trim();
            String password = editPasswordSignIn.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignIn.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save login details if Remember Me is checked
            if (rememberMeCheckBox.isChecked()) {
                editor.putString("email", email);
                editor.putString("password", password);
                editor.putBoolean("rememberMe", true);
                editor.apply();
            } else {
                editor.clear(); // Clear saved data if unchecked
                editor.apply();
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        Toast.makeText(SignIn.this, "Sign In Successful", Toast.LENGTH_SHORT).show();
                        navigateBasedOnEmail(email);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(SignIn.this, "Failed to sign in: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Navigate to Sign Up page
        signUpPrompt.setOnClickListener(v -> {
            startActivity(new Intent(SignIn.this, SignUp.class));
        });
    }

    private void navigateBasedOnEmail(String email) {
        if (email.endsWith("@admin.com")) {
            startActivity(new Intent(SignIn.this, Admin.class));
            Toast.makeText(SignIn.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(SignIn.this, UserActivity.class));
            Toast.makeText(SignIn.this, "Welcome User", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            String email = auth.getCurrentUser().getEmail();
            if (email != null) {
                navigateBasedOnEmail(email);
            }
        }
    }

    private void clearInputsOnLogout() {
        // Check if user is logged out and clear inputs
        if (auth.getCurrentUser() == null) {
            editEmailSignIn.setText("");
            editPasswordSignIn.setText("");
            rememberMeCheckBox.setChecked(false);
        }
    }
}