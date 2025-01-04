package com.example.expensesplitting.User.Pay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Model.Transaction;
import com.example.expensesplitting.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentReceiptActivity extends AppCompatActivity {

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_receipt);

        String formattedDate = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.US).format(new Date());

        Transaction transaction = (Transaction) getIntent().getSerializableExtra("transaction");
        String transactionId = getIntent().getStringExtra("transactionId");

        TextView amountText = findViewById(R.id.amount_text_view);
        TextView recipient = findViewById(R.id.recipient);
        TextView recipientEmail = findViewById(R.id.request_user_email);
        TextView amountBill = findViewById(R.id.amount_bill);
        TextView recipientNameBill = findViewById(R.id.recipient_bill);
        TextView recipientEmailBill = findViewById(R.id.recipient_email_bill);
        TextView dateText = findViewById(R.id.date);
        TextView transactionIdText = findViewById(R.id.transaction_id_bill);
        TextView noteText = findViewById(R.id.note_bill);

        if (transaction != null) {
            amountText.setText(String.format("$%.2f", transaction.getAmount()));
            recipient.setText("Paid to " + transaction.getRecipient());
            recipientEmail.setText(transaction.getRecipientEmail());
            amountBill.setText(String.format("$%.2f", transaction.getAmount()));
            recipientNameBill.setText(transaction.getRecipient());
            recipientEmailBill.setText(transaction.getRecipientEmail());
            dateText.setText(formattedDate);
            transactionIdText.setText(transactionId);
            noteText.setText(transaction.getNote());

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            assert transactionId != null;
            db.collection("transactions").document(transactionId)
                    .update("status", "paid")
                    .addOnSuccessListener(aVoid -> Log.d("PaymentReceiptActivity", "Transaction status updated"))
                    .addOnFailureListener(e -> Log.e("PaymentReceiptActivity", "Error updating transaction status", e));
        }

        ImageView closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}