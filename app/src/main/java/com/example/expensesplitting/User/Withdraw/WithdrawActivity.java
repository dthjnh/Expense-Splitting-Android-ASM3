package com.example.expensesplitting.User.Withdraw;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class WithdrawActivity extends AppCompatActivity {

    private TextView currentBalance;
    private EditText withdrawAmount;
    private Button selectPaymentMethodButton;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private double availableBalance = 0.0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        currentBalance = findViewById(R.id.current_balance);
        withdrawAmount = findViewById(R.id.withdraw_amount);
        selectPaymentMethodButton = findViewById(R.id.select_payment_method_button);

        fetchWalletBalance();

        selectPaymentMethodButton.setOnClickListener(v -> {
            String amountStr = withdrawAmount.getText().toString();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(WithdrawActivity.this, SelectPaymentMethod2.class);
                intent.putExtra("AMOUNT", amountStr);
                startActivity(intent);
            }
        });

    }

    @SuppressLint("DefaultLocale")
    private void fetchWalletBalance() {
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
                            Toast.makeText(WithdrawActivity.this, "Wallet not found", Toast.LENGTH_SHORT).show();
                        } else {
                            Wallet wallet = task.getResult().getDocuments().get(0).toObject(Wallet.class);
                            if (wallet != null) {
                                availableBalance = wallet.getBalance();
                                currentBalance.setText(String.format("Available Balance: $%,.2f", availableBalance));
                            }
                        }
                    } else {
                        Toast.makeText(this, "Error checking wallet: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void backToUserActivity(View view) {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
        finish();
    }
}