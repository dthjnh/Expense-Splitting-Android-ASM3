package com.example.expensesplitting.User.PaymentMethod;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Model.PaymentMethod;
import com.example.expensesplitting.Model.Wallet;
import com.example.expensesplitting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewPaymentActivity extends AppCompatActivity {
    ImageView closeButton;
    EditText expiryDate, cardNumber, accountHolderName, cvv;
    Button saveButton;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_payment_method);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        expiryDate = findViewById(R.id.expiry_date);
        cardNumber = findViewById(R.id.card_number);
        accountHolderName = findViewById(R.id.account_holder_name);
        cvv = findViewById(R.id.cvv);
        saveButton = findViewById(R.id.add_new_payment_method_button);
        closeButton = findViewById(R.id.close_add_payment_button);

        // Set input filter to enforce 12 digits
        cardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 12) {
                    cardNumber.setText(s.subSequence(0, 12));
                    cardNumber.setSelection(12);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        expiryDate.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @SuppressLint("DefaultLocale")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("\\D", "");
                    String cleanC = current.replaceAll("\\D", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 4) {
                        String mmYY = "MMYY";
                        clean = clean + mmYY.substring(clean.length());
                    } else {
                        int mon = Integer.parseInt(clean.substring(0, 2));
                        if (mon > 12) mon = 12;
                        clean = String.format("%02d%02d", mon, Integer.parseInt(clean.substring(2, 4)));
                    }

                    clean = String.format("%s/%s", clean.substring(0, 2), clean.substring(2, 4));

                    sel = Math.max(sel, 0);
                    current = clean;
                    expiryDate.setText(current);
                    expiryDate.setSelection(Math.min(sel, current.length()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        saveButton.setOnClickListener(v -> {
            addPaymentMethod();
            Intent intent = new Intent(NewPaymentActivity.this, PaymentMethodActivity.class);
            setResult(RESULT_OK, intent);
            finish();
        });

        closeButton.setOnClickListener(v -> {
            Intent intent = new Intent(NewPaymentActivity.this, PaymentMethodActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void addPaymentMethod() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("wallets")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Wallet wallet = task.getResult().getDocuments().get(0).toObject(Wallet.class);
                        if (wallet != null) {
                            List<PaymentMethod> paymentMethods = wallet.getPaymentMethods();
                            if (paymentMethods == null) {
                                paymentMethods = new ArrayList<>();
                            }
                            PaymentMethod newPaymentMethod = new PaymentMethod(
                                    userId,
                                    cardNumber.getText().toString(),
                                    accountHolderName.getText().toString(),
                                    expiryDate.getText().toString(),
                                    cvv.getText().toString()
                            );
                            paymentMethods.add(newPaymentMethod);
                            db.collection("wallets").document(task.getResult().getDocuments().get(0).getId())
                                    .update("paymentMethods", paymentMethods)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(NewPaymentActivity.this, "Payment method added successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(NewPaymentActivity.this, PaymentMethodActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(NewPaymentActivity.this, "Error adding payment method: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(NewPaymentActivity.this, "Error fetching wallet: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}