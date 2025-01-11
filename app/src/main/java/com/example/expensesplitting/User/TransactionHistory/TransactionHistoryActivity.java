package com.example.expensesplitting.User.TransactionHistory;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Model.Transaction;
import com.example.expensesplitting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransactionHistoryActivity extends AppCompatActivity {
    private TransactionHistoryAdapter adapter;
    private List<Transaction> transactions;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView emptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactions = new ArrayList<>();
        emptyView = findViewById(R.id.empty_state_text);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        adapter = new TransactionHistoryAdapter(transactions);
        recyclerView.setAdapter(adapter);

        fetchTransactions();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchTransactions() {
        String userEmail = Objects.requireNonNull(auth.getCurrentUser()).getEmail();

        // Query for userEmail with status: paid, topup, withdraw
        Query userEmailQuery = db.collection("transactions")
                .whereEqualTo("userEmail", userEmail)
                .whereIn("status", List.of("paid", "topup", "withdraw"));

        // Query for recipientEmail with status: requested
        Query recipientEmailQuery = db.collection("transactions")
                .whereEqualTo("recipientEmail", userEmail)
                .whereEqualTo("status", "requested");

        // Execute both queries and combine results
        userEmailQuery.get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                transactions.clear();
                for (var document : task1.getResult().getDocuments()) {
                    Transaction transaction = document.toObject(Transaction.class);
                    if (transaction != null) {
                        transactions.add(transaction);
                    }
                }
                recipientEmailQuery.get().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        for (var document : task2.getResult().getDocuments()) {
                            Transaction transaction = document.toObject(Transaction.class);
                            if (transaction != null) {
                                transactions.add(transaction);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        emptyView.setVisibility(transactions.isEmpty() ? View.VISIBLE : View.GONE);
                    } else {
                        Toast.makeText(this, "Error fetching transactions: " + Objects.requireNonNull(task2.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Error fetching transactions: " + Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}