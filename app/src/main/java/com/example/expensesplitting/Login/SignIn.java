package com.example.expensesplitting.Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.AdminReplyChatActivity;
import com.example.expensesplitting.R;
import com.example.expensesplitting.UserActivity;
import com.example.expensesplitting.WelcomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        boolean rememberMeState = sharedPreferences.getBoolean("rememberMe", false);
        rememberMeCheckBox.setChecked(rememberMeState);

        clearInputsOnLogout();

        forgotPassword.setOnClickListener(v -> startActivity(new Intent(SignIn.this, ForgotPassword.class)));

        editEmailSignIn.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String enteredEmail = editEmailSignIn.getText().toString();
                if (enteredEmail.equals(sharedPreferences.getString("email", ""))) {
                    editPasswordSignIn.setText(sharedPreferences.getString("password", ""));
                } else {
                    editPasswordSignIn.setText("");
                }
            }
        });

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

        btnSignIn.setOnClickListener(v -> {
            String email = editEmailSignIn.getText().toString().trim();
            String password = editPasswordSignIn.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignIn.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rememberMeCheckBox.isChecked()) {
                editor.putString("email", email);
                editor.putString("password", password);
                editor.putBoolean("rememberMe", true);
                editor.apply();
            } else {
                editor.clear();
                editor.apply();
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        Log.d("SignIn", "Sign-in successful for: " + email);
                        addCurrentDeviceToFirestore();
                        navigateBasedOnRole();
                    })
                    .addOnFailureListener(e -> Toast.makeText(SignIn.this, "Failed to sign in: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        signUpPrompt.setOnClickListener(v -> startActivity(new Intent(SignIn.this, SignUp.class)));
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
                .addOnSuccessListener(documentReference -> Log.d("DeviceManagement", "Device added successfully."))
                .addOnFailureListener(e -> Log.e("DeviceManagement", "Failed to add device: " + e.getMessage()));
    }

   private void navigateBasedOnRole() {
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseFirestore.getInstance().collection("users").document(userId).get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String role = documentSnapshot.getString("role");
                    Log.d("SignIn", "User role: " + role);
                    if ("admin".equals(role)) {
                        Log.d("SignIn", "Admin detected");
                        startActivity(new Intent(SignIn.this, AdminReplyChatActivity.class));
                        Toast.makeText(SignIn.this, "Welcome Admin", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("SignIn", "User detected");
                        startActivity(new Intent(SignIn.this, UserActivity.class));
                        Toast.makeText(SignIn.this, "Welcome User", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                } else {
                    Log.e("SignIn", "User document does not exist");
                }
            })
            .addOnFailureListener(e -> Log.e("SignIn", "Error fetching role: " + e.getMessage()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            navigateBasedOnRole();
        }
    }

    private void clearInputsOnLogout() {
        if (auth.getCurrentUser() == null) {
            editEmailSignIn.setText("");
            editPasswordSignIn.setText("");
            rememberMeCheckBox.setChecked(false);
        }
    }

    public void backToWelcome(View view) {
        Intent intent = new Intent(SignIn.this, WelcomeActivity.class);
        startActivity(intent);
    }
}