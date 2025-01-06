package com.example.expensesplitting.AccountInfo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class BillingSubscriptionActivity extends AppCompatActivity {

    private TextView planPriceText, planDescriptionText, expiryDateText, currentPlanText, renewText;
    private LinearLayout plan_details;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_subscription);

        planPriceText = findViewById(R.id.plan_price);
        planDescriptionText = findViewById(R.id.plan_description);
        expiryDateText = findViewById(R.id.expiry_date);
        currentPlanText = findViewById(R.id.current_plan_text);
        renewText = findViewById(R.id.renew_text);
        plan_details = findViewById(R.id.plan_details);

        fetchSubscriptionDetails();
        addClickableRenewText();
    }

    private void fetchSubscriptionDetails() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("subscription")) {
                        String price = documentSnapshot.getString("subscription.price");
                        String description = documentSnapshot.getString("subscription.description");
                        String expiryDate = documentSnapshot.getString("subscription.expiry_date");

                        if (price != null && description != null && expiryDate != null) {
                            planPriceText.setText(price);
                            planDescriptionText.setText(description);
                            expiryDateText.setText("Your subscription will expire on " + expiryDate + ".");
                        } else {
                            showNoSubscription();
                        }
                    } else {
                        showNoSubscription();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showNoSubscription() {
        currentPlanText.setVisibility(View.GONE);
        expiryDateText.setText("You currently have no active subscription.\n Upgrade to a premium plan!");
    }

    private void addClickableRenewText() {
        String fullText = "Cancel your subscription here.";
        SpannableString spannableString = new SpannableString(fullText);

        int startIndex = fullText.indexOf("here");
        int endIndex = startIndex + "here".length();

        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#F9A825")), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                showCancelSubscriptionDialog();
            }
        };

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        renewText.setText(spannableString);
        renewText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void showCancelSubscriptionDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Cancel Subscription")
                .setMessage("Are you sure you want to cancel your subscription?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    cancelSubscription();
                    planPriceText.setVisibility(View.GONE);
                    planDescriptionText.setVisibility(View.GONE);
                    renewText.setVisibility(View.GONE);
                    currentPlanText.setVisibility(View.GONE);
                    plan_details.setVisibility(View.GONE);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create();

        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
    }

    private void cancelSubscription() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .update("subscription", null)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Subscription canceled successfully.", Toast.LENGTH_SHORT).show();
                    fetchSubscriptionDetails();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to cancel subscription: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void backToAccountActivity(View view) {
        startActivity(new Intent(this, AccountActivity.class));
    }
}