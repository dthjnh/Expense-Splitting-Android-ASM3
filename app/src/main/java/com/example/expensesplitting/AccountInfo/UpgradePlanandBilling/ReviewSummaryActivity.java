package com.example.expensesplitting.AccountInfo.UpgradePlanandBilling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReviewSummaryActivity extends AppCompatActivity {

    private TextView planNameText, planPriceText, planDescriptionText, selectedCardText;
    private ImageView paymentIcon;
    private Button confirmPaymentButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_summary);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        planNameText = findViewById(R.id.plan_name);
        planPriceText = findViewById(R.id.plan_price);
        planDescriptionText = findViewById(R.id.plan_description);
        selectedCardText = findViewById(R.id.selected_card_text);
        paymentIcon = findViewById(R.id.payment_icon);
        confirmPaymentButton = findViewById(R.id.confirm_payment_button);

        String selectedPlanName = getIntent().getStringExtra("PLAN_NAME");
        String selectedPlanPrice = getIntent().getStringExtra("PLAN_PRICE");
        String selectedPlanDescription = getIntent().getStringExtra("PLAN_DESCRIPTION");
        String cardNumber = getIntent().getStringExtra("CARD_NUMBER");
        String cardType = getIntent().getStringExtra("CARD_TYPE");

        planNameText.setText(selectedPlanName);
        planPriceText.setText(selectedPlanPrice);
        planDescriptionText.setText(selectedPlanDescription);
        selectedCardText.setText("**** **** **** " + cardNumber.substring(cardNumber.length() - 4));

        setPaymentIcon(cardType);

        confirmPaymentButton.setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ReviewSummaryActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_processing_payment, null);
            builder.setView(dialogView);
            builder.setCancelable(false);
            final android.app.AlertDialog processingDialog = builder.create();
            processingDialog.show();

            new android.os.Handler().postDelayed(() -> {
                processingDialog.dismiss();
                saveSubscriptionToFirestore(selectedPlanName, selectedPlanPrice, selectedPlanDescription);

                Intent intent = new Intent(ReviewSummaryActivity.this, PaymentSuccessActivity.class);
                intent.putExtra("PLAN_NAME", selectedPlanName);
                intent.putExtra("PLAN_DESCRIPTION", selectedPlanDescription);
                intent.putExtra("PLAN_PRICE", selectedPlanPrice);
                startActivity(intent);
                finish();
            }, 3000);
        });
    }

    private void saveSubscriptionToFirestore(String planName, String price, String description) {
        String userId = auth.getCurrentUser().getUid();
        Map<String, Object> subscriptionData = new HashMap<>();
        subscriptionData.put("plan_name", planName);
        subscriptionData.put("price", price);
        subscriptionData.put("expiry_date", "Feb 13, 2025");
        subscriptionData.put("description", description);

        db.collection("users").document(userId)
                .update("subscription", subscriptionData)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Subscription saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save subscription: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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