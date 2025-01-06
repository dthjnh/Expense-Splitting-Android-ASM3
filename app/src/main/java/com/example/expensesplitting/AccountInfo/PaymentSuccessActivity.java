package com.example.expensesplitting.AccountInfo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.R;
import com.example.expensesplitting.UserActivity;

public class PaymentSuccessActivity extends AppCompatActivity {

    private TextView congratulationsTitle, subscriptionMessage, benefitsUnlockedTitle, benefitsList, thankYouMessage;
    private Button okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        congratulationsTitle = findViewById(R.id.congratulations_title);
        subscriptionMessage = findViewById(R.id.subscription_message);
        benefitsUnlockedTitle = findViewById(R.id.benefits_unlocked_title);
        benefitsList = findViewById(R.id.benefits_list);
        thankYouMessage = findViewById(R.id.thank_you_message);
        okButton = findViewById(R.id.ok_button);

        String planName = getIntent().getStringExtra("PLAN_NAME");
        String planDescription = getIntent().getStringExtra("PLAN_DESCRIPTION");

        subscriptionMessage.setText("Welcome to Splitify Premium!");
        benefitsList.setText(planDescription);

        okButton.setOnClickListener(v -> {
            startActivity(new Intent(this, UserActivity.class));
            finish();
        });
    }
}