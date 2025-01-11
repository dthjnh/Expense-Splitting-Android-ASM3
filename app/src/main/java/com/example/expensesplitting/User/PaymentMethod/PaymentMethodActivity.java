package com.example.expensesplitting.User.PaymentMethod;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Model.PaymentMethod;
import com.example.expensesplitting.Model.Wallet;
import com.example.expensesplitting.R;
import com.example.expensesplitting.UserActivity;
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
        emptyView = findViewById(R.id.empty_state_text);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        adapter = new PaymentMethodAdapter(paymentMethods, this::showDeleteConfirmationDialog);
        recyclerView.setAdapter(adapter);

        fetchPaymentMethods();

        Button newPaymentMethodButton = findViewById(R.id.topup_cancel_button);
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
                            emptyView.setVisibility(paymentMethods.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                    } else if (task.isSuccessful() && task.getResult().isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(this, "Error fetching payment methods: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDeleteConfirmationDialog(PaymentMethod paymentMethod) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this payment method?")
                .setPositiveButton("Delete", (dialog, which) -> deletePaymentMethod(paymentMethod))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.gray));
    }

    private void deletePaymentMethod(PaymentMethod paymentMethod) {
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        db.collection("wallets")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String documentId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        Wallet wallet = queryDocumentSnapshots.getDocuments().get(0).toObject(Wallet.class);

                        if (wallet != null && wallet.getPaymentMethods() != null) {
                            List<PaymentMethod> updatedMethods = wallet.getPaymentMethods();
                            boolean removed = updatedMethods.removeIf(pm -> pm.getCardNumber().equals(paymentMethod.getCardNumber()));

                            if (removed) {
                                db.collection("wallets").document(documentId)
                                        .update("paymentMethods", updatedMethods)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Payment method deleted successfully", Toast.LENGTH_SHORT).show();
                                            fetchPaymentMethods();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error deleting payment method: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(this, "Payment method not found in list", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "No payment methods found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "No wallet found for the user", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching wallet: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void backtoUserProfile(View view) {
        startActivity(new Intent(PaymentMethodActivity.this, UserActivity.class));
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