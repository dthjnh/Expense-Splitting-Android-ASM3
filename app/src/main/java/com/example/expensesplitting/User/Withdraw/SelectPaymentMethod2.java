package com.example.expensesplitting.User.Withdraw;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.AccountInfo.UpgradePlanandBilling.ReviewSummaryActivity;
import com.example.expensesplitting.AccountInfo.UpgradePlanandBilling.UpgradePlanActivity;
import com.example.expensesplitting.Model.PaymentMethod;
import com.example.expensesplitting.Model.Wallet;
import com.example.expensesplitting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SelectPaymentMethod2 extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SelectablePaymentMethodAdapter2 adapter;
    private List<PaymentMethod> paymentMethods;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView emptyView;
    private Button continueButton;
    private PaymentMethod selectedPaymentMethod;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payment_method2);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyView = findViewById(R.id.empty_state_text);
        continueButton = findViewById(R.id.continueButton);

        paymentMethods = new ArrayList<>();
        adapter = new SelectablePaymentMethodAdapter2(paymentMethods, paymentMethod -> {
            selectedPaymentMethod = paymentMethod;
            Toast.makeText(this, "Selected: **** " + paymentMethod.getCardNumber().substring(paymentMethod.getCardNumber().length() - 4), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        fetchPaymentMethods();

        continueButton.setOnClickListener(v -> {
            if (selectedPaymentMethod != null) {
                Intent intent = new Intent(this, ReviewWithdrawActivity.class);
                intent.putExtra("AMOUNT", getIntent().getStringExtra("AMOUNT")); // Pass withdrawal amount
                intent.putExtra("CARD_NUMBER", selectedPaymentMethod.getCardNumber()); // Pass card number
                intent.putExtra("CARD_TYPE", selectedPaymentMethod.getCardNumber()); // Pass card type
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPaymentMethods() {
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        db.collection("wallets")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Wallet wallet = task.getResult().getDocuments().get(0).toObject(Wallet.class);
                        if (wallet != null && wallet.getPaymentMethods() != null) {
                            paymentMethods.clear();
                            paymentMethods.addAll(wallet.getPaymentMethods());
                            adapter.notifyDataSetChanged();
                            emptyView.setVisibility(paymentMethods.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                    } else {
                        emptyView.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "No payment methods found", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void backToUpgradePlan(View view) {
        startActivity(new Intent(this, WithdrawActivity.class));
    }
}