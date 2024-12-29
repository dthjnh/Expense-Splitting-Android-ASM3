package com.example.expensesplitting.Login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText editEmailForgotPassword;
    private Button btnSendResetLink;
    private TextView tvBackToSignIn;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        editEmailForgotPassword = findViewById(R.id.editEmailForgotPassword);
        btnSendResetLink = findViewById(R.id.btnSendResetLink);
        tvBackToSignIn = findViewById(R.id.tvBackToSignIn);

        // Send Reset Link Button
        btnSendResetLink.setOnClickListener(v -> {
            String email = editEmailForgotPassword.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(ForgotPassword.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firebase password reset function
            auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(aVoid -> Toast.makeText(ForgotPassword.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(ForgotPassword.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        // Back to Sign In TextView
        tvBackToSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPassword.this, SignIn.class);
            startActivity(intent);
            finish();
        });
    }
}