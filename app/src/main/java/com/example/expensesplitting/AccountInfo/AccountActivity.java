package com.example.expensesplitting.AccountInfo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Contacts.ContactsActivity;
import com.example.expensesplitting.Login.SignIn;
import com.example.expensesplitting.R;
import com.example.expensesplitting.User.PaymentMethod.PaymentMethodActivity;
import com.example.expensesplitting.UserActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountActivity extends AppCompatActivity {

    private static final String TAG = "AccountActivity";

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private TextView userName, userEmail;
    private ImageView userAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userAvatar = findViewById(R.id.userAvatar);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        fetchUserDetails();
    }

    private void fetchUserDetails() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            firestore.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("FirstName");
                            String lastName = documentSnapshot.getString("LastName");
                            String email = documentSnapshot.getString("EmailAddress");

                            userName.setText(firstName + " " + lastName);
                            userEmail.setText(email);
                        } else {
                            Toast.makeText(this, "User details not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching user details: ", e);
                        Toast.makeText(this, "Error fetching user details", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SignIn.class));
            finish();
        }
    }

    public void logOutUser(android.view.View view) {
        FirebaseAuth.getInstance().signOut();

        googleSignInClient.signOut().addOnCompleteListener(task -> {
            startActivity(new Intent(this, SignIn.class));
            finish();
        });
    }

    public void personalInfo(View view) {
        startActivity(new Intent(this, PersonalInfoActivity.class));
    }

    public void accountSecurity(View view) {
        startActivity(new Intent(this, AccountSecurityActivity.class));
    }

    public void upgradePlan(View view) {
        startActivity(new Intent(this, UpgradePlanActivity.class));
    }

    public void paymentMethod(View view) {
        startActivity(new Intent(this, PaymentMethodActivity.class));
    }
}