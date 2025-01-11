package com.example.expensesplitting.User.TopUp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Model.PaymentMethod;
import com.example.expensesplitting.Model.Transaction;
import com.example.expensesplitting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class TopUpSummaryActivity extends AppCompatActivity {
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private WalletBalanceUpdateListener walletBalanceUpdateListener;

    public interface WalletBalanceUpdateListener {
        void onWalletBalanceUpdated(double newBalance);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_summary);

        walletBalanceUpdateListener = (WalletBalanceUpdateListener) getSupportFragmentManager().findFragmentById(R.id.top_section_fragment);

        ImageView backButton = findViewById(R.id.back_button);
        TextView amountToTopUp = findViewById(R.id.amount_value);
        EditText addNotesInput = findViewById(R.id.add_notes_input);
        Button cancelButton = findViewById(R.id.topup_cancel_button);
        Button confirmButton = findViewById(R.id.topup_continue_button2);

        backButton.setOnClickListener(v -> finish());

        AtomicReference<Intent> intent = new AtomicReference<>(getIntent());
        String amount = intent.get().getStringExtra("topUpAmount");
        PaymentMethod paymentMethod = (PaymentMethod) intent.get().getSerializableExtra("paymentMethod");

        String formattedAmount = formatAmount(amount);
        amountToTopUp.setText(formattedAmount);

        assert paymentMethod != null;
        displayPaymentMethod(paymentMethod);

        cancelButton.setOnClickListener(v -> finish());

        confirmButton.setOnClickListener(v -> {
            String topUpAmount = amountToTopUp.getText().toString().replaceAll("[$,]", "");
            double formattedDoubleAmount = Double.parseDouble(topUpAmount);
            String notes = addNotesInput.getText().toString();

            Transaction newTransaction = new Transaction(
                    null,
                    "topup",
                    currentUser.getDisplayName(),
                    currentUser.getEmail(),
                    paymentMethod.getCardNumber(),
                    "Visa",
                    formattedDoubleAmount,
                    new Date(),
                    notes,
                    "topup"
            );

            db.collection("transactions").add(newTransaction).addOnSuccessListener(documentReference -> {
                String transactionId = documentReference.getId();
                newTransaction.setDocumentId(transactionId);
                updateWalletBalance(topUpAmount);

                intent.set(new Intent(this, TopUpReceiptActivity.class));
                intent.get().putExtra("transaction", newTransaction);
                intent.get().putExtra("transactionId", transactionId);
                startActivity(intent.get());
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
                Log.e("TopUpSummaryActivity", "Error adding document", e);
            });
        });
    }

    private String formatAmount(String amount) {
        double amountValue = Double.parseDouble(amount);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormat.format(amountValue);
    }

    private void displayPaymentMethod(PaymentMethod paymentMethod) {
        TextView cardNumber = findViewById(R.id.item_card_number);
        String maskedCardNumber = "•••• •••• •••• " + paymentMethod.getCardNumber().substring(paymentMethod.getCardNumber().length() - 4);
        cardNumber.setText(maskedCardNumber);
    }

    private void updateWalletBalance(String topUpAmount) {
        db.collection("wallets")
                .whereEqualTo("userId", currentUser.getUid())
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        queryDocumentSnapshots.getDocuments().forEach(document -> {
                            double currentBalance = document.getDouble("balance");
                            double newBalance = currentBalance + Double.parseDouble(topUpAmount);
                            db.collection("wallets").document(document.getId())
                                    .update("balance", newBalance)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("TopUpSummaryActivity", "Wallet balance updated");
                                        if (walletBalanceUpdateListener != null) {
                                            walletBalanceUpdateListener.onWalletBalanceUpdated(newBalance);
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e("TopUpSummaryActivity", "Error updating wallet balance", e));
                        });
                    }
                });
    }
}