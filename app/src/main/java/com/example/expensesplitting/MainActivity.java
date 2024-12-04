package com.example.expensesplitting;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private TextView userNameTextView, userEmailTextView, userBirthdateTextView;
    private Button deleteAccountButton, logOutButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userNameTextView = findViewById(R.id.userNameTextView);
        userEmailTextView = findViewById(R.id.userEmailTextView);
        userBirthdateTextView = findViewById(R.id.userBirthdateTextView);
        deleteAccountButton = findViewById(R.id.deleteAccountButton);
        logOutButton = findViewById(R.id.logOutButton);

        // Get current user
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            loadUserData(userId);

            deleteAccountButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteUserAccount(userId, currentUser);
                }
            });

            logOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    auth.signOut();
                    startActivity(new Intent(MainActivity.this, SignIn.class));
                    finish();
                }
            });

        } else {
            userNameTextView.setText("No user logged in");
            userEmailTextView.setText("");
            userBirthdateTextView.setText("");
        }
    }

    private void loadUserData(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String email = documentSnapshot.getString("email");
                        String birthDate = documentSnapshot.getString("birthDate");

                        // Display data
                        userNameTextView.setText(firstName + " " + lastName);
                        userEmailTextView.setText(email != null ? email : "No Email Available");
                        userBirthdateTextView.setText(birthDate != null ? birthDate : "No Birthdate Available");
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    private void deleteUserAccount(String userId, FirebaseUser currentUser) {
        // Delete Firestore document
        db.collection("users").document(userId).delete()
                .addOnSuccessListener(aVoid -> {
                    currentUser.delete()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                    // Redirect to SignIn screen
                                    startActivity(new Intent(MainActivity.this, SignIn.class));
                                    finish();
                                } else {
                                    Toast.makeText(this, "Failed to delete account from authentication", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete user data", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }
}

class User {
    private String firstName;
    private String lastName;
    private String email;
    private String birthDate;

    public User() {
    }

    public User(String firstName, String lastName, String email, String birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDate = birthDate;
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
}


