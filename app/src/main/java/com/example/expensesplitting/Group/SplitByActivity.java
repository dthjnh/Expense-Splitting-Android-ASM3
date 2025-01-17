package com.example.expensesplitting.Group;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Database.SplitHelper;
import com.example.expensesplitting.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplitByActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnEqual, btnUnequal, btnCancel, btnOk;
    private TextView totalAmountText, amountLeftText;
    private List<Participant> participants = new ArrayList<>();
    private SplitAdapter splitAdapter;
    private double totalAmount;
    private double amountLeft;
    private SplitHelper splitHelper;

    private static final String TAG = "SplitByActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_by);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewSplit);
        btnEqual = findViewById(R.id.btnEqual);
        btnUnequal = findViewById(R.id.btnUnequal);
        btnCancel = findViewById(R.id.btnCancel);
        btnOk = findViewById(R.id.btnOk);
        totalAmountText = findViewById(R.id.totalAmountText);
        amountLeftText = findViewById(R.id.amountLeftText);

        // Retrieve data from intent
        totalAmount = getIntent().getDoubleExtra("TOTAL_AMOUNT", 0);
        participants = (ArrayList<Participant>) getIntent().getSerializableExtra("PARTICIPANTS");

        if (participants == null || participants.isEmpty()) {
            Toast.makeText(this, "No participants available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize SplitHelper
        splitHelper = new SplitHelper(participants, totalAmount);

        // Set the initial total amount and amount left
        amountLeft = totalAmount;
        updateAmountTexts();

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        splitAdapter = new SplitAdapter(participants, totalAmount, SplitAdapter.SplitType.EQUAL, this::updateAmountTexts);
        recyclerView.setAdapter(splitAdapter);

        // Pre-select "Equal" button
        setSelectedButton(btnEqual, btnUnequal);

        // Set up button listeners
        setupButtonListeners();
    }

    private void setupButtonListeners() {
        btnEqual.setOnClickListener(v -> {
            splitHelper.divideEqually();
            splitAdapter.setSplitType(SplitAdapter.SplitType.EQUAL);
            setSelectedButton(btnEqual, btnUnequal);
            updateAmountTexts();
            Toast.makeText(this, "Equal Split Selected", Toast.LENGTH_SHORT).show();
        });

        btnUnequal.setOnClickListener(v -> {
            Map<String, Double> amounts = new HashMap<>();
            for (Participant participant : participants) {
                amounts.put(participant.getName(), participant.getAmount());
            }
            splitHelper.divideUnequally(amounts);
            splitAdapter.setSplitType(SplitAdapter.SplitType.UNEQUAL);
            setSelectedButton(btnUnequal, btnEqual);
            updateAmountTexts();
            Toast.makeText(this, "Unequal Split Selected", Toast.LENGTH_SHORT).show();
        });

        btnCancel.setOnClickListener(v -> finish());

        btnOk.setOnClickListener(v -> {
            if (validateAmounts()) {
                Intent resultIntent = new Intent();
                String splitMethod = splitAdapter.getSplitType() == SplitAdapter.SplitType.EQUAL ? "Equal" : "Unequal";

                // Serialize splitDetails to JSON using Gson
                Map<String, Double> splitDetails = splitHelper.getSplitDetails();
                Gson gson = new Gson();
                String splitDetailsJson = gson.toJson(splitDetails);

                Log.d(TAG, "Serialized split_details JSON: " + splitDetailsJson);

                resultIntent.putExtra("SPLIT_METHOD", splitMethod);
                resultIntent.putExtra("SPLIT_DETAILS", splitDetailsJson); // Pass JSON string
                setResult(RESULT_OK, resultIntent);

                Toast.makeText(this, "Split saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private boolean validateAmounts() {
        if (amountLeft != 0) {
            Toast.makeText(this, "Please allocate the entire amount", Toast.LENGTH_SHORT).show();
            return false;
        }

        for (Participant participant : participants) {
            if (participant.getAmount() < 0) {
                Toast.makeText(this, "Invalid amount for participant: " + participant.getName(), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private void setSelectedButton(Button selectedButton, Button unselectedButton) {
        selectedButton.setBackgroundTintList(getResources().getColorStateList(R.color.yellow));
        selectedButton.setTextColor(getResources().getColor(android.R.color.black));

        unselectedButton.setBackgroundTintList(getResources().getColorStateList(R.color.white));
        unselectedButton.setTextColor(getResources().getColor(android.R.color.black));
    }

    private void updateAmountTexts() {
        double allocatedAmount = 0;
        for (Participant participant : participants) {
            allocatedAmount += participant.getAmount();
        }
        amountLeft = totalAmount - allocatedAmount;

        totalAmountText.setText(String.format("Total: $%.2f", totalAmount));
        amountLeftText.setText(String.format("Amount Left: $%.2f", amountLeft));

        if (amountLeft < 0) {
            amountLeftText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            amountLeftText.setTextColor(getResources().getColor(android.R.color.black));
        }
    }
}