package com.example.expensesplitting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class SignIn extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText editEmailSignIn, editPasswordSignIn;
    private Button btnSignIn;
    private TextView signUpPrompt;
    private ImageView imageViewTogglePassword;
    private boolean isPasswordVisible = false;

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
        imageViewTogglePassword = findViewById(R.id.imageViewTogglePassword);

        // Toggle password visibility
        imageViewTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                editPasswordSignIn.setTransformationMethod(PasswordTransformationMethod.getInstance());
                imageViewTogglePassword.setImageResource(R.drawable.ic_eye);
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
}