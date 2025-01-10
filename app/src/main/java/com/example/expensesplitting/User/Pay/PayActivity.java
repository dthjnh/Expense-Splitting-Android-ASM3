package com.example.expensesplitting.User.Pay;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PayActivity extends AppCompatActivity implements TransactionAdapter.OnItemClickListener, NewPaymentBottomSheetFragment.OnNewPaymentListener {
    private static final int REQUEST_CODE_PAYMENT_RECEIPT = 1;
    RecyclerView recyclerView;
    TransactionAdapter adapter;
    List<Transaction> transactionList;
    ImageView addPayButton;
    TextView emptyStateText;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyStateText = findViewById(R.id.empty_state_text);

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        transactionList = new ArrayList<>();

        fetchTransactions();

        adapter = new TransactionAdapter(this, transactionList, this);
        recyclerView.setAdapter(adapter);

        addPayButton = findViewById(R.id.add_request_button);
        addPayButton.setOnClickListener(v -> {
            NewPaymentBottomSheetFragment bottomSheetFragment = new NewPaymentBottomSheetFragment();
            bottomSheetFragment.setPaymentAddedListener(this);
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });
    }

    private void fetchTransactions() {
        db.collection("transactions")
                .whereEqualTo("type", "pay")
                .whereEqualTo("userEmail", currentUser.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        transactionList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            String type = document.getString("type");
                            String username = document.getString("username");
                            String userEmailAddress = document.getString("userEmail");
                            String recipient = document.getString("recipient");
                            String recipientEmail = document.getString("recipientEmail");
                            double amount = document.getDouble("amount");
                            Timestamp timestamp = document.getTimestamp("timestamp");
                            String note = document.getString("note");
                            Date date = timestamp != null ? timestamp.toDate() : null;
                            String status = document.getString("status");

                            Transaction transaction = new Transaction(documentId, type, username, userEmailAddress, recipient, recipientEmail, amount, date, note, status);
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
        PaymentBottomSheetFragment fragment = PaymentBottomSheetFragment.newInstance(transaction);
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    @Override
    public void onNewPayment(Transaction transaction, String transactionId) {
        transactionList.add(transaction);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYMENT_RECEIPT && resultCode == RESULT_OK) {
            fetchTransactions();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}