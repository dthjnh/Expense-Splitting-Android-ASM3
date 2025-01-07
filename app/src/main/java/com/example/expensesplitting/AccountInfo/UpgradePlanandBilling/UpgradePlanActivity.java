package com.example.expensesplitting.AccountInfo.UpgradePlanandBilling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.AccountInfo.AccountActivity;
import com.example.expensesplitting.AccountInfo.Payment.SelectPaymentMethodActivity;
import com.example.expensesplitting.R;

public class UpgradePlanActivity extends AppCompatActivity {

    private TextView monthlyTab, yearlyTab, planPrice, planDescription;
    private Button continueButton;
    private String selectedPlanDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_plan);

        monthlyTab = findViewById(R.id.monthlyTab);
        yearlyTab = findViewById(R.id.yearlyTab);
        planPrice = findViewById(R.id.planPrice);
        planDescription = findViewById(R.id.planDescription);
        continueButton = findViewById(R.id.continueButton);

        setupPlan("Monthly", "$4.99 / month", getMonthlyDescription());

        monthlyTab.setOnClickListener(v -> setupPlan("Monthly", "$4.99 / month", getMonthlyDescription()));
        yearlyTab.setOnClickListener(v -> setupPlan("Yearly", "$49.99 / year", getYearlyDescription()));

        continueButton.setOnClickListener(v -> {
            String selectedPlanPrice = planPrice.getText().toString();
            String selectedPlanType = monthlyTab.getCurrentTextColor() == getResources().getColor(R.color.white) ? "Monthly" : "Yearly";
            String selectedPlanDescription = planDescription.getText().toString();

            Intent intent = new Intent(UpgradePlanActivity.this, SelectPaymentMethodActivity.class);
            intent.putExtra("SELECTED_PLAN_NAME", selectedPlanType);
            intent.putExtra("SELECTED_PLAN_PRICE", selectedPlanPrice);
            intent.putExtra("SELECTED_PLAN_DESCRIPTION", selectedPlanDescription);
            startActivity(intent);
        });
    }

    private void setupPlan(String planType, String price, String description) {
        if (planType.equals("Monthly")) {
            monthlyTab.setBackgroundResource(R.drawable.tab_selected_background);
            monthlyTab.setTextColor(getResources().getColor(R.color.black));
            yearlyTab.setBackgroundResource(R.drawable.tab_unselected_background);
            yearlyTab.setTextColor(getResources().getColor(R.color.gray));
        } else {
            yearlyTab.setBackgroundResource(R.drawable.tab_selected_background);
            yearlyTab.setTextColor(getResources().getColor(R.color.black));
            monthlyTab.setBackgroundResource(R.drawable.tab_unselected_background);
            monthlyTab.setTextColor(getResources().getColor(R.color.gray));
        }
        planPrice.setText(price);
        planDescription.setText(description);
        selectedPlanDescription = description;
        continueButton.setText("Continue - " + price);
    }

    private String getMonthlyDescription() {
        return "✔ All basic benefits, plus:\n" +
                "✔ Ad-free experience.\n" +
                "✔ Priority customer support.\n" +
                "✔ Access to detailed spending insights.\n" +
                "✔ Unlimited bill-sharing groups.\n" +
                "✔ Early access to new features.";
    }

    private String getYearlyDescription() {
        return "✔ All monthly subscription benefits, plus:\n" +
                "✔ Exclusive access to beta features.\n" +
                "✔ Premium customization options (themes, icons).\n" +
                "✔ Special offers and discounts from partner brands.";
    }

    public void backToUserProfile(View view) {
        startActivity(new Intent(UpgradePlanActivity.this, AccountActivity.class));
    }
}