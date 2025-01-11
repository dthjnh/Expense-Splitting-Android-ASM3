package com.example.expensesplitting.User.TopUp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Model.Transaction;
import com.example.expensesplitting.R;

public class TopUpReceiptActivity extends AppCompatActivity {
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_receipt);

        Intent intent = getIntent();
        Transaction transaction = (Transaction) intent.getSerializableExtra("transaction");

        if (transaction != null) {
            ImageView closeButton = findViewById(R.id.close_button);
            closeButton.setOnClickListener(v -> {
                Intent backToHome = new Intent(TopUpReceiptActivity.this, TopUpActivity.class);
                startActivity(backToHome);
            });

            TextView amountTextView = findViewById(R.id.amount_text_view);
            TextView recipientTextView = findViewById(R.id.recipient);
            TextView requestUserEmailTextView = findViewById(R.id.request_user_email);
            TextView amountBillTextView = findViewById(R.id.amount_bill);
            TextView recipientBillTextView = findViewById(R.id.recipient_bill);
            TextView recipientEmailBillTextView = findViewById(R.id.recipient_email_bill);
            TextView dateTextView = findViewById(R.id.date);
            TextView transactionIdBillTextView = findViewById(R.id.transaction_id_bill);
            TextView noteBillTextView = findViewById(R.id.note_bill);

            amountTextView.setText(String.format("$%.2f", transaction.getAmount()));
            recipientTextView.setText("You top up to Splitify Balance");
            requestUserEmailTextView.setText(transaction.getUsername());
            amountBillTextView.setText(String.format("$%.2f", transaction.getAmount()));
            recipientBillTextView.setText("•••• •••• •••• " + transaction.getRecipient().substring(transaction.getRecipient().length() - 4));
            recipientEmailBillTextView.setText(transaction.getRecipientEmail());
            dateTextView.setText(android.text.format.DateFormat.format("MMM dd, yyyy - hh:mm a", transaction.getTimestamp()));
            transactionIdBillTextView.setText(transaction.getDocumentId());
            noteBillTextView.setText(transaction.getNote());
        }
    }
}