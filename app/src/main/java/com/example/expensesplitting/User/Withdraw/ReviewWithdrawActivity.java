package com.example.expensesplitting.User.Withdraw;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Model.Wallet;
import com.example.expensesplitting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReviewWithdrawActivity extends AppCompatActivity {

    private TextView amountToWithdraw, cardNumber, notesText;
    private Button cancelButton, confirmWithdrawButton;
    private ImageView backButton, cardIcon;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_withdraw);

        // Initialize views
        amountToWithdraw = findViewById(R.id.amount_to_withdraw);
        cardNumber = findViewById(R.id.card_number);
        notesText = findViewById(R.id.notes_text);
        cancelButton = findViewById(R.id.cancel_button);
        confirmWithdrawButton = findViewById(R.id.confirm_withdraw_button);
        backButton = findViewById(R.id.back_button);
        cardIcon = findViewById(R.id.card_logo);

        // Retrieve data from Intent
        String amount = getIntent().getStringExtra("AMOUNT");
        String cardNumberText = getIntent().getStringExtra("CARD_NUMBER");
        String cardType = getIntent().getStringExtra("CARD_TYPE");
        String notes = getIntent().getStringExtra("NOTES");

        // Set values to views
        amountToWithdraw.setText("$" + amount);
        assert cardNumberText != null;
        cardNumber.setText("•••• •••• •••• " + cardNumberText.substring(cardNumberText.length() - 4));
        notesText.setText(notes != null ? notes : "No notes provided.");

        // Set card icon based on card type
        if (cardType != null) {
            if (cardType.startsWith("4")) {
                cardIcon.setImageResource(R.drawable.ic_visa);
            } else if (cardType.startsWith("5")) {
                cardIcon.setImageResource(R.drawable.ic_mastercard);
            } else {
                cardIcon.setImageResource(R.drawable.ic_default_card);
            }
        }

        // Handle back button click
        backButton.setOnClickListener(v -> onBackPressed());

        // Handle cancel button click
        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewWithdrawActivity.this, WithdrawActivity.class);
            startActivity(intent);
            finish();
        });

        // Handle confirm button click
        confirmWithdrawButton.setOnClickListener(v -> {
            if (amount != null) {
                double withdrawAmount = Double.parseDouble(amount);

                // Assuming Firebase Firestore is used
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String userId = auth.getCurrentUser().getUid();

                // Fetch the current balance and update it
                db.collection("wallets")
                        .whereEqualTo("userId", userId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                // Get the wallet document
                                String walletId = task.getResult().getDocuments().get(0).getId();
                                Wallet wallet = task.getResult().getDocuments().get(0).toObject(Wallet.class);

                                if (wallet != null && wallet.getBalance() >= withdrawAmount) {
                                    double newBalance = wallet.getBalance() - withdrawAmount;

                                    // Update the balance in the database
                                    db.collection("wallets")
                                            .document(walletId)
                                            .update("balance", newBalance)
                                            .addOnSuccessListener(aVoid -> {
                                                // Success: Redirect to WithdrawReceiptActivity
                                                Intent intent = new Intent(ReviewWithdrawActivity.this, WithdrawReceiptActivity.class);
                                                intent.putExtra("AMOUNT", amount);
                                                intent.putExtra("CARD_NUMBER", cardNumberText);
                                                intent.putExtra("CARD_TYPE", cardType);
                                                intent.putExtra("NOTES", notes != null ? notes : "No additional notes.");
                                                startActivity(intent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle the failure
                                                Toast.makeText(this, "Error updating balance: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "Error fetching wallet data", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Invalid withdrawal amount", Toast.LENGTH_SHORT).show();
            }
        });
    }
}