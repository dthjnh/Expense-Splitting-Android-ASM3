package com.example.expensesplitting.User.TransactionHistory;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Model.Transaction;
import com.example.expensesplitting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TransactionHistoryDetailActivity extends AppCompatActivity {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
    String displayName;

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history_detail);

        Transaction transaction = (Transaction) getIntent().getSerializableExtra("transaction");

        if (transaction == null) {
            Toast.makeText(this, "Transaction data is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView headerTextView = findViewById(R.id.context);
        ImageView imageView = findViewById(R.id.context_image_view);

        TextView amountTextView = findViewById(R.id.amount_text_view);
        TextView recipientTextView = findViewById(R.id.recipient);
        TextView emailTextView = findViewById(R.id.recipient_detail);

        TextView contextLine1TextView = findViewById(R.id.context_line_1);
        TextView contextLine2TextView = findViewById(R.id.context_line_2);
        TextView contextLine3TextView = findViewById(R.id.context_line_3);

        TextView amountBillTextView = findViewById(R.id.amount_bill);
        TextView recipientBillTextView = findViewById(R.id.recipient_bill);
        TextView recipientEmailBillTextView = findViewById(R.id.recipient_email_bill);
        TextView transactionIdTextView = findViewById(R.id.transaction_id_bill);
        TextView dateTextView = findViewById(R.id.date);
        TextView statusTextView = findViewById(R.id.status);
        TextView noteTextView = findViewById(R.id.note_bill);

        LinearLayout statusLayout = findViewById(R.id.status_layout);
        statusLayout.setVisibility(View.GONE);

        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.ENGLISH);

        checkRequestPaymentStatus(() -> {
            runOnUiThread(() -> {
                switch (Objects.requireNonNull(transaction).getType()) {
                    case "topup":
                        headerTextView.setText("Top Up");
                        imageView.setImageResource(R.drawable.transaction_history_detail_topup);
                        contextLine1TextView.setText("You top up");
                        contextLine2TextView.setText("From");
                        contextLine3TextView.setText("Payment Method");

                        amountTextView.setText(String.format("+ $%.2f", transaction.getAmount()));
                        displayName = auth.getDisplayName();
                        recipientTextView.setText(displayName != null ? displayName : "No display name available");

                        emailTextView.setText(auth.getEmail());

                        amountBillTextView.setText(String.format("$%.2f", transaction.getAmount()));
                        recipientBillTextView.setText(transaction.getRecipient());
                        recipientEmailBillTextView.setText(transaction.getRecipientEmail());

                        try {
                            Date date = inputFormat.parse(transaction.getTimestamp().toString());
                            assert date != null;
                            String formattedDate = outputFormat.format(date);
                            dateTextView.setText(formattedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            dateTextView.setText("Invalid date");
                        }

                        transactionIdTextView.setText(transaction.getDocumentId());
                        noteTextView.setText(transaction.getNote());

                        break;

                    case "pay":
                        headerTextView.setText("Pay");
                        imageView.setImageResource(R.drawable.user);
                        contextLine1TextView.setText("You have paid");
                        contextLine2TextView.setText("To");
                        contextLine3TextView.setText("Email");

                        amountTextView.setText(String.format("- $%.2f", transaction.getAmount()));
                        displayName = auth.getDisplayName();
                        recipientTextView.setText(displayName != null ? displayName : "No display name available");

                        emailTextView.setText(auth.getEmail());

                        amountBillTextView.setText(String.format("$%.2f", transaction.getAmount()));
                        recipientBillTextView.setText(transaction.getRecipient());
                        recipientEmailBillTextView.setText(transaction.getRecipientEmail());

                        try {
                            Date date = inputFormat.parse(transaction.getTimestamp().toString());
                            assert date != null;
                            String formattedDate = outputFormat.format(date);
                            dateTextView.setText(formattedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            dateTextView.setText("Invalid date");
                        }

                        transactionIdTextView.setText(transaction.getDocumentId());
                        noteTextView.setText(transaction.getNote());

                        break;

                    case "withdraw":
                        headerTextView.setText("Withdraw");
                        imageView.setImageResource(R.drawable.transaction_withdraw);
                        contextLine1TextView.setText("You withdrawn");
                        contextLine2TextView.setText("To");
                        contextLine3TextView.setText("Account");

                        amountTextView.setText(String.format("- $%.2f", transaction.getAmount()));
                        displayName = auth.getDisplayName();
                        recipientTextView.setText(displayName != null ? displayName : "No display name available");

                        emailTextView.setText(auth.getEmail());

                        amountBillTextView.setText(String.format("$%.2f", transaction.getAmount()));
                        recipientBillTextView.setText(transaction.getRecipient());
                        recipientEmailBillTextView.setText(transaction.getRecipientEmail());

                        try {
                            Date date = inputFormat.parse(transaction.getTimestamp().toString());
                            assert date != null;
                            String formattedDate = outputFormat.format(date);
                            dateTextView.setText(formattedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            dateTextView.setText("Invalid date");
                        }

                        transactionIdTextView.setText(transaction.getDocumentId());
                        noteTextView.setText(transaction.getNote());

                        break;

                    case "request":
                        headerTextView.setText("Request");
                        imageView.setImageResource(R.drawable.user);
                        contextLine1TextView.setText("You received");
                        contextLine2TextView.setText("From");

                        amountTextView.setText(String.format("$%.2f", transaction.getAmount()));
                        displayName = auth.getDisplayName();
                        recipientTextView.setText(displayName != null ? displayName : "No display name available");

                        emailTextView.setText(auth.getEmail());

                        amountBillTextView.setText(String.format("$%.2f", transaction.getAmount()));
                        recipientBillTextView.setText(transaction.getRecipient());
                        recipientEmailBillTextView.setText(transaction.getRecipientEmail());

                        try {
                            Date date = inputFormat.parse(transaction.getTimestamp().toString());
                            assert date != null;
                            String formattedDate = outputFormat.format(date);
                            dateTextView.setText(formattedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            dateTextView.setText("Invalid date");
                        }

                        transactionIdTextView.setText(transaction.getDocumentId());
                        noteTextView.setText(transaction.getNote());

                        statusLayout.setVisibility(View.VISIBLE);

                        break;
                }
            });
        });

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }

    private void checkRequestPaymentStatus(Runnable callback) {
        db.collection("transactions")
                .whereIn("type", List.of("pay", "request"))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Transaction> payTransactions = new ArrayList<>();
                        List<Transaction> requestTransactions = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Transaction transaction = document.toObject(Transaction.class);
                            if ("pay".equals(transaction.getType())) {
                                payTransactions.add(transaction);
                            } else if ("request".equals(transaction.getType())) {
                                requestTransactions.add(transaction);
                            }
                        }

                        for (Transaction requestTransaction : requestTransactions) {
                            String status = getString(payTransactions, requestTransaction);
                            updateStatusTextView(status);
                        }
                    }
                    callback.run();
                })
                .addOnFailureListener(e -> {
                    Log.e("FetchTransactions", "Failed to fetch transactions", e);
                    runOnUiThread(() -> Toast.makeText(this, "Failed to fetch transactions", Toast.LENGTH_SHORT).show());
                    callback.run();
                });
    }

    @NonNull
    private static String getString(List<Transaction> payTransactions, Transaction requestTransaction) {
        boolean isPaid = false;
        for (Transaction payTransaction : payTransactions) {
            if (payTransaction.getDocumentId() != null && requestTransaction.getDocumentId().equals(payTransaction.getDocumentId()) && "paid".equals(payTransaction.getStatus())) {
                isPaid = true;
                break;
            }
        }
        return isPaid ? "Paid" : "Unpaid";
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceType"})
    private void updateStatusTextView(String status) {
        runOnUiThread(() -> {
            TextView statusTextView = findViewById(R.id.status);
            statusTextView.setText(status);
            Log.d("TransactionHistoryDetailActivity", "Status: " + status);

            if ("paid".equals(status)) {
                statusTextView.setBackground(getResources().getDrawable(R.drawable.rounded_status_background));
            } else {
                statusTextView.setBackground(getResources().getDrawable(R.drawable.rounded_red_status_background));
            }
        });
    }
}