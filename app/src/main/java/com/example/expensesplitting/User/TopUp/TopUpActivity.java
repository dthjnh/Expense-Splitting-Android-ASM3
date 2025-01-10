package com.example.expensesplitting.User.TopUp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Model.Wallet;
import com.example.expensesplitting.R;
import com.example.expensesplitting.UserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class TopUpActivity extends AppCompatActivity {
    EditText amount;
    TextView balance;
    ImageView backButton;
    Button continueButton;

    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up);

        amount = findViewById(R.id.amount_text);
        balance = findViewById(R.id.balance);
        backButton = findViewById(R.id.back_button);

        continueButton = findViewById(R.id.continue_button);
        continueButton.setOnClickListener(view -> {
            Intent intent = new Intent(TopUpActivity.this, SelectPaymentMethodActivity.class);
            intent.putExtra("topUpAmount", amount.getText().toString());
            startActivity(intent);
            finish();
        });

        String topUpAmount = amount.getText().toString();


        fetchWalletBalance();

        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(TopUpActivity.this, UserActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    public void fetchWalletBalance() {
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("wallets")
                .whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(TopUpActivity.this, "Wallet not found", Toast.LENGTH_SHORT).show();
                        } else {
                            Wallet wallet = task.getResult().getDocuments().get(0).toObject(Wallet.class);
                            if (wallet != null) {
                                balance.setText("Available Balance: " + String.format("$%,.2f", wallet.getBalance()));
                            }
                        }
                    } else {
                        Toast.makeText(this, "Error checking wallet: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}