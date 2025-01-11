package com.example.expensesplitting.User.Request;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Model.Transaction;
import com.example.expensesplitting.R;
import com.example.expensesplitting.User.Transaction.TransactionAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RequestActivity extends AppCompatActivity implements TransactionAdapter.OnItemClickListener, NewRequestBottomSheetFragment.OnNewRequestListener, RequestBottomSheetFragment.OnTransactionsUpdatedListener {
    private static final int REQUEST_CODE_REQUEST_RECEIPT = 1;
    RecyclerView recyclerView;
    TransactionAdapter adapter;
    List<Transaction> transactionList;
    ImageView addRequest;
    TextView emptyStateText;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_request);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyStateText = findViewById(R.id.empty_state_text);

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        transactionList = new ArrayList<>();

        fetchTransactions();

        adapter = new TransactionAdapter(this, transactionList, this);
        adapter.setTransactionsUpdatedListener(this);
        recyclerView.setAdapter(adapter);

        addRequest = findViewById(R.id.add_request_button);
        addRequest.setOnClickListener(v -> {
            String balance = getIntent().getStringExtra("balance");
            NewRequestBottomSheetFragment bottomSheetFragment = NewRequestBottomSheetFragment.newInstance(balance);
            bottomSheetFragment.setRequestAddedListener(this);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });
    }

    private void fetchTransactions() {
        db.collection("transactions")
                .whereEqualTo("type", "request")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        transactionList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            String type = document.getString("type");
                            String username = document.getString("username");
                            String userEmail= document.getString("userEmail");
                            String recipient = document.getString("recipient");
                            String recipientEmail = document.getString("recipientEmail");
                            double amount = document.getDouble("amount");
                            Timestamp timestamp = document.getTimestamp("timestamp");
                            String note = document.getString("note");
                            Date date = timestamp != null ? timestamp.toDate() : null;
                            String status = document.getString("status");

                            Transaction transaction = new Transaction(documentId, type, username, userEmail, recipient, recipientEmail, amount, date, note, status);
                            transactionList.add(transaction);
                        }
                        adapter.notifyDataSetChanged();
                        updateEmptyState();
                    } else {
                        Toast.makeText(this, "Error fetching transactions", Toast.LENGTH_SHORT).show();
                        Log.e("PayActivity", "Error fetching transactions", task.getException());
                        finish();
                    }
                });
    }

    private void updateEmptyState() {
        if (transactionList.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
        } else {
            emptyStateText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(Transaction transaction) {
        RequestBottomSheetFragment fragment = RequestBottomSheetFragment.newInstance(transaction);
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    @Override
    public void onNewRequest(Transaction transaction, String transactionId) {
        transactionList.add(transaction);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REQUEST_RECEIPT && resultCode == RESULT_OK) {
            fetchTransactions();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onTransactionsUpdated(List<Transaction> updatedTransactions) {
        transactionList.clear();
        transactionList.addAll(updatedTransactions);
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }
}