package com.example.expensesplitting.User.Withdraw;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Model.Wallet;
import com.example.expensesplitting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReviewWithdrawActivity extends AppCompatActivity {

    private TextView amountToWithdraw, cardNumber;
    private EditText notesInput;
    private Button cancelButton, confirmWithdrawButton;
    private ImageView backButton, cardIcon;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_withdraw);

        amountToWithdraw = findViewById(R.id.amount_to_withdraw);
        cardNumber = findViewById(R.id.card_number);
        notesInput = findViewById(R.id.add_notes_input);
        cancelButton = findViewById(R.id.cancel_button);
        confirmWithdrawButton = findViewById(R.id.confirm_withdraw_button);
        backButton = findViewById(R.id.back_button);
        cardIcon = findViewById(R.id.card_logo);

        String amount = getIntent().getStringExtra("AMOUNT");
        String cardNumberText = getIntent().getStringExtra("CARD_NUMBER");
        String cardType = getIntent().getStringExtra("CARD_TYPE");

        amountToWithdraw.setText("$" + amount);
        assert cardNumberText != null;
        cardNumber.setText("•••• •••• •••• " + cardNumberText.substring(cardNumberText.length() - 4));

        if (cardType != null) {
            if (cardType.startsWith("4")) {
                cardIcon.setImageResource(R.drawable.ic_visa);
            } else if (cardType.startsWith("5")) {
                cardIcon.setImageResource(R.drawable.ic_mastercard);
            } else {
                cardIcon.setImageResource(R.drawable.ic_default_card);
            }
        }

        backButton.setOnClickListener(v -> onBackPressed());

        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReviewWithdrawActivity.this, WithdrawActivity.class);
            startActivity(intent);
            finish();
        });

        confirmWithdrawButton.setOnClickListener(v -> {
            if (amount != null) {
                double withdrawAmount = Double.parseDouble(amount);
                String notes = notesInput.getText().toString().trim();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String userId = auth.getCurrentUser().getUid();

                db.collection("wallets")
                        .whereEqualTo("userId", userId)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                String walletId = task.getResult().getDocuments().get(0).getId();
                                Wallet wallet = task.getResult().getDocuments().get(0).toObject(Wallet.class);

                                if (wallet != null && wallet.getBalance() >= withdrawAmount) {
                                    double newBalance = wallet.getBalance() - withdrawAmount;

                                    db.collection("wallets")
                                            .document(walletId)
                                            .update("balance", newBalance)
                                            .addOnSuccessListener(aVoid -> {
                                                saveTransaction(db, auth.getCurrentUser().getEmail(), withdrawAmount, cardNumberText, cardType, notes);

                                                Intent intent = new Intent(ReviewWithdrawActivity.this, WithdrawReceiptActivity.class);
                                                intent.putExtra("AMOUNT", amount);
                                                intent.putExtra("CARD_NUMBER", cardNumberText);
                                                intent.putExtra("CARD_TYPE", cardType);
                                                intent.putExtra("NOTES", notes.isEmpty() ? "No additional notes." : notes);
                                                startActivity(intent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
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

    private void saveTransaction(FirebaseFirestore db, String userEmail, double amount, String cardNumber, String cardType, String notes) {
        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("amount", amount);
        transactionData.put("cardNumber", "•••• •••• •••• " + cardNumber.substring(cardNumber.length() - 4));
        transactionData.put("cardType", cardType);
        transactionData.put("userEmail", userEmail);
        transactionData.put("timestamp", getCurrentTimestamp());
        transactionData.put("status", "withdraw");
        transactionData.put("type", "withdraw");
        transactionData.put("notes", notes.isEmpty() ? "No additional notes." : notes);

        db.collection("transactions")
                .add(transactionData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Transaction recorded successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error storing transaction: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy · HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}