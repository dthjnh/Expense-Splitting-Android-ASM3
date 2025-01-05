package com.example.expensesplitting.AccountInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Model.PaymentMethod;
import com.example.expensesplitting.R;

public class ReviewSummaryActivity extends AppCompatActivity {

    private TextView planNameText, planPriceText, planDescriptionText, selectedCardText;
    private ImageView paymentIcon;
    private Button confirmPaymentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_summary);

        // Initialize views
        planNameText = findViewById(R.id.plan_name);
        planPriceText = findViewById(R.id.plan_price);
        planDescriptionText = findViewById(R.id.plan_description);
        selectedCardText = findViewById(R.id.selected_card_text);
        paymentIcon = findViewById(R.id.payment_icon);
        confirmPaymentButton = findViewById(R.id.confirm_payment_button);

        // Retrieve data from Intent
        String selectedPlanName = getIntent().getStringExtra("PLAN_NAME");
        String selectedPlanPrice = getIntent().getStringExtra("PLAN_PRICE");
        String selectedPlanDescription = getIntent().getStringExtra("PLAN_DESCRIPTION");
        String cardNumber = getIntent().getStringExtra("CARD_NUMBER");
        String cardType = getIntent().getStringExtra("CARD_TYPE");

        // Set data to views
        planNameText.setText(selectedPlanName);
        planPriceText.setText(selectedPlanPrice);
        planDescriptionText.setText(selectedPlanDescription);
        selectedCardText.setText("**** **** **** " + cardNumber.substring(cardNumber.length() - 4));

        // Set payment icon based on card type
        setPaymentIcon(cardType);

        // Confirm payment action
        confirmPaymentButton.setOnClickListener(v -> {
            Toast.makeText(this, "Payment confirmed for " + selectedPlanName, Toast.LENGTH_SHORT).show();
            finish(); // Close the activity after confirmation
        });
    }

    private void setPaymentIcon(String cardType) {
        if (cardType.startsWith("4")) {
            paymentIcon.setImageResource(R.drawable.ic_visa);
        } else if (cardType.startsWith("5")) {
            paymentIcon.setImageResource(R.drawable.ic_mastercard);
        } else {
            paymentIcon.setImageResource(R.drawable.ic_default_card);
        }
    }

}