package com.example.expensesplitting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Login.SignIn;
import com.example.expensesplitting.Login.SignUp;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "WelcomeActivity";

    private Button btnGoogleSignIn, btnSignUp, btnLogIn;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Initialize Firebase Auth and Firestore
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Add your web client ID from Firebase Console
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize buttons
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogIn = findViewById(R.id.btnLogIn);

        // Google Sign-In Button
        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());

        // Sign Up Button
        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, SignUp.class);
            startActivity(intent);
        });

        // Log In Button
        btnLogIn.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, SignIn.class);
            startActivity(intent);
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                // Get the Google Sign-In account
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken(), account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign-in failed", e);
                Toast.makeText(this, "Google Sign-In Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken, GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-in success
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            addCurrentDeviceToFirestore();

                            saveUserToFirestore(user, account);
                            Toast.makeText(this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            // Navigate to your app's main activity
                            startActivity(new Intent(WelcomeActivity.this, UserActivity.class));
                            finish();
                        }
                    } else {
                        // Sign-in failed
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addCurrentDeviceToFirestore() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String deviceName = android.os.Build.MODEL;
        String lastLogin = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());

        Map<String, Object> deviceData = new HashMap<>();
        deviceData.put("name", deviceName);
        deviceData.put("lastLogin", lastLogin);
        deviceData.put("userId", userId);

        FirebaseFirestore.getInstance().collection("devices")
                .add(deviceData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("DeviceManagement", "Device added successfully.");
                })
                .addOnFailureListener(e -> {
                    Log.e("DeviceManagement", "Failed to add device: " + e.getMessage());
                });
    }

    private void saveUserToFirestore(FirebaseUser firebaseUser, GoogleSignInAccount account) {
        String userId = firebaseUser.getUid();

        Map<String, Object> userData = new HashMap<>();
        userData.put("FirstName", account.getGivenName());
        userData.put("LastName", account.getFamilyName());
        userData.put("EmailAddress", firebaseUser.getEmail());
        userData.put("ProfilePicture", account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "default");

        firestore.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User saved successfully in Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving user in Firestore", e));
    }
}