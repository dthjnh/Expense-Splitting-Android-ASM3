package com.example.expensesplitting.User.Withdraw;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WithdrawReceiptActivity extends AppCompatActivity {

    private TextView withdrawAmount, withdrawTo, cardType, receiptWithdrawAmount, receiptCardNumber,
            receiptCardType, receiptDate, receiptTransactionId, receiptNotes;
    private Button downloadReceiptButton, shareReceiptButton;
    private ImageView closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_receipt);

        // Initialize views
        withdrawAmount = findViewById(R.id.withdraw_amount);
        withdrawTo = findViewById(R.id.withdraw_to);
        cardType = findViewById(R.id.card_type);
        receiptWithdrawAmount = findViewById(R.id.receipt_withdraw_amount);
        receiptCardNumber = findViewById(R.id.receipt_card_number);
        receiptCardType = findViewById(R.id.receipt_card_type);
        receiptDate = findViewById(R.id.receipt_date);
        receiptTransactionId = findViewById(R.id.receipt_transaction_id);
        receiptNotes = findViewById(R.id.receipt_notes);
        downloadReceiptButton = findViewById(R.id.download_receipt_button);
        shareReceiptButton = findViewById(R.id.share_receipt_button);
        closeButton = findViewById(R.id.close_button);

        // Get data from Intent
        String amount = getIntent().getStringExtra("AMOUNT");
        String cardNumber = getIntent().getStringExtra("CARD_NUMBER");
        String cardTypeValue = getIntent().getStringExtra("CARD_TYPE");
        String notes = getIntent().getStringExtra("NOTES");

        // Generate a sample transaction ID and current date
        String transactionId = generateTransactionId();
        String currentDate = getCurrentDate();

        // Set data to views
        withdrawAmount.setText("$" + amount);
        withdrawTo.setText("You withdraw to **** " + cardNumber.substring(cardNumber.length() - 4));
        cardType.setText(cardTypeValue);

        receiptWithdrawAmount.setText("$" + amount);
        receiptCardNumber.setText("**** " + cardNumber.substring(cardNumber.length() - 4));
        receiptCardType.setText(cardTypeValue);
        receiptDate.setText(currentDate);
        receiptTransactionId.setText(transactionId);
        receiptNotes.setText(notes != null ? notes : "No additional notes.");

        // Handle close button click
        closeButton.setOnClickListener(v -> finish());

        // Handle download receipt button click
        downloadReceiptButton.setOnClickListener(v -> {
            // Add logic for downloading the receipt (e.g., generating a PDF)
            Toast.makeText(this, "Receipt downloaded successfully!", Toast.LENGTH_SHORT).show();
        });

        // Handle share receipt button click
        shareReceiptButton.setOnClickListener(v -> {
            // Share receipt details as text
            String receiptDetails = "Receipt:\n" +
                    "Amount: $" + amount + "\n" +
                    "Card: " + cardTypeValue + " (**** " + cardNumber.substring(cardNumber.length() - 4) + ")\n" +
                    "Date: " + currentDate + "\n" +
                    "Transaction ID: " + transactionId + "\n" +
                    "Notes: " + (notes != null ? notes : "No additional notes.");
            shareReceipt(receiptDetails);
        });
    }

    private String generateTransactionId() {
        return "T" + System.currentTimeMillis();
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy · HH:mm a", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void shareReceipt(String details) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, details);
        startActivity(Intent.createChooser(shareIntent, "Share Receipt"));
    }
}