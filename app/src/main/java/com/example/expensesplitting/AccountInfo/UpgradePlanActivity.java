package com.example.expensesplitting.AccountInfo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.R;

public class UpgradePlanActivity extends AppCompatActivity {

    private TextView monthlyTab, yearlyTab, planPrice;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_plan);

        monthlyTab = findViewById(R.id.monthlyTab);
        yearlyTab = findViewById(R.id.yearlyTab);
        planPrice = findViewById(R.id.planPrice);
        continueButton = findViewById(R.id.continueButton);

        // Default: Monthly Plan
        setupPlan("Monthly", "$4.99 / month");

        monthlyTab.setOnClickListener(v -> setupPlan("Monthly", "$4.99 / month"));
        yearlyTab.setOnClickListener(v -> setupPlan("Yearly", "$49.99 / year"));
    }

    private void setupPlan(String planType, String price) {
        if (planType.equals("Monthly")) {
            monthlyTab.setBackgroundResource(R.drawable.tab_selected_background);
            monthlyTab.setTextColor(getResources().getColor(R.color.white));
            yearlyTab.setBackgroundResource(R.drawable.tab_unselected_background);
            yearlyTab.setTextColor(getResources().getColor(R.color.gray));
        } else {
            yearlyTab.setBackgroundResource(R.drawable.tab_selected_background);
            yearlyTab.setTextColor(getResources().getColor(R.color.white));
            monthlyTab.setBackgroundResource(R.drawable.tab_unselected_background);
            monthlyTab.setTextColor(getResources().getColor(R.color.gray));
        }
        planPrice.setText(price);
        continueButton.setText("Continue - " + price);
    }

    public void backtoUserProfile(View view) {
        finish();
    }
}