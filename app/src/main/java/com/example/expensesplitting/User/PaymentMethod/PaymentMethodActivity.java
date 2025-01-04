package com.example.expensesplitting.User.PaymentMethod;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Model.PaymentMethod;
import com.example.expensesplitting.Model.Wallet;
import com.example.expensesplitting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PaymentMethodActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_NEW_PAYMENT = 1;
    private RecyclerView recyclerView;
    private PaymentMethodAdapter adapter;
    private List<PaymentMethod> paymentMethods;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView emptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        paymentMethods = new ArrayList<>();
        adapter = new PaymentMethodAdapter(paymentMethods);
        recyclerView.setAdapter(adapter);
        emptyView = findViewById(R.id.empty_state_text);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        fetchPaymentMethods();

        Button newPaymentMethodButton = findViewById(R.id.add_new_payment_method_button);
        newPaymentMethodButton.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentMethodActivity.this, NewPaymentActivity.class);
            startActivityForResult(intent, REQUEST_CODE_NEW_PAYMENT);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
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
                            if (paymentMethods.isEmpty()) {
                                emptyView.setVisibility(View.VISIBLE);
                            } else {
                                emptyView.setVisibility(View.GONE);
                            }
                        }
                    } else if (task.isSuccessful() && task.getResult().isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(this, "Error fetching payment methods: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void backtoUserProfile(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_NEW_PAYMENT && resultCode == RESULT_OK) {
            fetchPaymentMethods();
        }
    }
}