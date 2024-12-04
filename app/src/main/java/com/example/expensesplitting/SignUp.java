package com.example.expensesplitting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText firstName, lastName, emailAddress, birthDate, password, confirmPassword;
    private Button signUpButton, googleSignUpButton;
    private TextView tvAlreadyUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        signUpButton = findViewById(R.id.signUpButton);
        googleSignUpButton = findViewById(R.id.googleSignUpButton);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        emailAddress = findViewById(R.id.emailAddress);
        birthDate = findViewById(R.id.birthDate);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        tvAlreadyUser = findViewById(R.id.tvAlreadyUser);

        signUpButton.setOnClickListener(v -> {
            String firstNameText = firstName.getText().toString();
            String lastNameText = lastName.getText().toString();
            String emailAddressText = emailAddress.getText().toString();
            String birthDateText = birthDate.getText().toString();
            String passwordText = password.getText().toString();
            String confirmPasswordText = confirmPassword.getText().toString();

            if (firstNameText.isEmpty() || lastNameText.isEmpty() || emailAddressText.isEmpty() || birthDateText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty()) {
                Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (!passwordText.equals(confirmPasswordText)) {
                Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                auth.createUserWithEmailAndPassword(emailAddressText, passwordText).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser().getUid();
                        User newUser = new User(firstNameText, lastNameText, emailAddressText, birthDateText);

                        FirebaseFirestore.getInstance().collection("users").document(userId).set(newUser)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(SignUp.this, "User created successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUp.this, SignIn.class));
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(SignUp.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                });
                    } else {
                        Toast.makeText(SignUp.this, "User creation failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        tvAlreadyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, SignIn.class));
            }
        });

    }
}