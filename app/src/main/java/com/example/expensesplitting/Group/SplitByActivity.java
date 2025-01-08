package com.example.expensesplitting.Group;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.R;

import java.util.ArrayList;
import java.util.List;

public class SplitByActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnEqual, btnUnequal, btnCancel, btnOk;
    private TextView totalAmountText, amountLeftText;
    private List<Participant> participants = new ArrayList<>();
    private SplitAdapter splitAdapter;
    private double totalAmount;
    private double amountLeft;

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

        if (participants == null) {
            Toast.makeText(this, "No participants available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set the initial total amount and amount left
        amountLeft = totalAmount;
        updateAmountTexts();

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        splitAdapter = new SplitAdapter(participants, totalAmount, SplitAdapter.SplitType.EQUAL, this::updateAmountTexts);
        recyclerView.setAdapter(splitAdapter);

        setupButtonListeners();
    }

    private void setupButtonListeners() {
        btnEqual.setOnClickListener(v -> {
            splitAdapter.setSplitType(SplitAdapter.SplitType.EQUAL);
            updateAmountTexts();
            Toast.makeText(this, "Equal Split Selected", Toast.LENGTH_SHORT).show();
        });

        btnUnequal.setOnClickListener(v -> {
            splitAdapter.setSplitType(SplitAdapter.SplitType.UNEQUAL);
            updateAmountTexts();
            Toast.makeText(this, "Unequal Split Selected", Toast.LENGTH_SHORT).show();
        });

        btnCancel.setOnClickListener(v -> finish());

        btnOk.setOnClickListener(v -> {
            if (amountLeft == 0) {
                Toast.makeText(this, "Split saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Please allocate the entire amount", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAmountTexts() {
        // Update total and remaining amounts
        double allocatedAmount = 0;
        for (Participant participant : participants) {
            allocatedAmount += participant.getAmount();
        }
        amountLeft = totalAmount - allocatedAmount;

        // Update the TextViews
        totalAmountText.setText(String.format("Total: $%.2f", totalAmount));
        amountLeftText.setText(String.format("Amount Left: $%.2f", amountLeft));

        // Show red text if the amount left is negative
        if (amountLeft < 0) {
            amountLeftText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            amountLeftText.setTextColor(getResources().getColor(android.R.color.black));
        }
    }
}
