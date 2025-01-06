package com.example.expensesplitting.Group;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Database.GroupHelper;
import com.example.expensesplitting.R;

import java.util.ArrayList;
import java.util.List;

public class SplitByActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnEqual, btnUnequal, btnPercentage, btnCancel, btnOk;
    private List<Participant> participants = new ArrayList<>();
    private SplitAdapter splitAdapter;
    private double totalAmount;
    private long groupId;
    private GroupHelper groupHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_by);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewSplit);
        btnEqual = findViewById(R.id.btnEqual);
        btnUnequal = findViewById(R.id.btnUnequal);
        btnPercentage = findViewById(R.id.btnPercentage);
        btnCancel = findViewById(R.id.btnCancel);
        btnOk = findViewById(R.id.btnOk);

        // Retrieve data from intent
        totalAmount = getIntent().getDoubleExtra("TOTAL_AMOUNT", 0);
        groupId = getIntent().getLongExtra("GROUP_ID", -1);
        participants = (ArrayList<Participant>) getIntent().getSerializableExtra("PARTICIPANTS");

        if (groupId == -1 || participants == null) {
            Toast.makeText(this, "Invalid Group ID or Participants", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        splitAdapter = new SplitAdapter(participants, totalAmount);
        recyclerView.setAdapter(splitAdapter);

        setupButtonListeners();
    }

    private void setupButtonListeners() {
        btnEqual.setOnClickListener(v -> {
            splitAdapter.applySplit(SplitAdapter.SplitType.EQUAL);
            Toast.makeText(this, "Equal Split Selected", Toast.LENGTH_SHORT).show();
        });

        btnUnequal.setOnClickListener(v -> {
            splitAdapter.applySplit(SplitAdapter.SplitType.UNEQUAL);
            Toast.makeText(this, "Unequal Split Selected", Toast.LENGTH_SHORT).show();
        });

        btnPercentage.setOnClickListener(v -> {
            splitAdapter.applySplit(SplitAdapter.SplitType.PERCENTAGE);
            Toast.makeText(this, "Percentage Split Selected", Toast.LENGTH_SHORT).show();
        });

        btnCancel.setOnClickListener(v -> finish());

        btnOk.setOnClickListener(v -> {
            if (splitAdapter.validateSplit()) {
                Toast.makeText(this, "Split saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Invalid split. Please check inputs.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
