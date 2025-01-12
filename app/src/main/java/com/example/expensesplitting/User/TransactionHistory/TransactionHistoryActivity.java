package com.example.expensesplitting.User.TransactionHistory;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import com.google.android.material.tabs.TabLayout;

public class TransactionHistoryActivity extends AppCompatActivity {
    private TransactionHistoryAdapter adapter;
    private List<Transaction> transactions, filteredTransactions;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ImageView emptyView;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactions = new ArrayList<>();
        filteredTransactions = new ArrayList<>();
        emptyView = findViewById(R.id.empty_state_text);
        tabLayout = findViewById(R.id.tab_layout);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        adapter = new TransactionHistoryAdapter(filteredTransactions);
        recyclerView.setAdapter(adapter);

        setupTabs();
        fetchTransactions();

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchTransactions() {
        String userEmail = Objects.requireNonNull(auth.getCurrentUser()).getEmail();

        Query userEmailQuery = db.collection("transactions")
                .whereEqualTo("userEmail", userEmail)
                .whereIn("status", List.of("paid", "topup", "withdraw", "requested"));

        Query recipientEmailQuery = db.collection("transactions")
                .whereEqualTo("recipientEmail", userEmail)
                .whereEqualTo("status", "requested");

        userEmailQuery.get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                transactions.clear();
                for (var document : task1.getResult().getDocuments()) {
                    Transaction transaction = document.toObject(Transaction.class);
                    if (transaction != null && transaction.getDocumentId() == null) {
                        transaction.setDocumentId(document.getId());
                        transactions.add(transaction);
                    }
                }
                recipientEmailQuery.get().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        for (var document : task2.getResult().getDocuments()) {
                            Transaction transaction = document.toObject(Transaction.class);
                            if (transaction != null && transaction.getDocumentId() == null) {
                                transaction.setDocumentId(document.getId());
                                transactions.add(transaction);
                            }
                        }
                        applyFilter("All");
                    } else {
                        Toast.makeText(this, "Error fetching transactions: " + Objects.requireNonNull(task2.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Error fetching transactions: " + Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Pay"));
        tabLayout.addTab(tabLayout.newTab().setText("Request"));
        tabLayout.addTab(tabLayout.newTab().setText("Top Up"));
        tabLayout.addTab(tabLayout.newTab().setText("Withdraw"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String selectedTab = tab.getText().toString();
                applyFilter(selectedTab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                String selectedTab = tab.getText().toString();
                applyFilter(selectedTab);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void applyFilter(String filter) {
        filteredTransactions.clear();

        if (filter.equals("All")) {
            filteredTransactions.addAll(transactions);
        } else {
            for (Transaction transaction : transactions) {
                switch (filter) {
                    case "Pay":
                        if ("paid".equals(transaction.getStatus())) {
                            filteredTransactions.add(transaction);
                        }
                        break;
                    case "Request":
                        if ("requested".equals(transaction.getStatus())) {
                            filteredTransactions.add(transaction);
                        }
                        break;
                    case "Top Up":
                        if ("topup".equals(transaction.getStatus())) {
                            filteredTransactions.add(transaction);
                        }
                        break;
                    case "Withdraw":
                        if ("withdraw".equals(transaction.getStatus())) {
                            filteredTransactions.add(transaction);
                        }
                        break;
                }
            }
        }

        adapter.notifyDataSetChanged();
        emptyView.setVisibility(filteredTransactions.isEmpty() ? View.VISIBLE : View.GONE);
    }
}