package com.example.expensesplitting.Group;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Database.ExpenseHelper;
import com.example.expensesplitting.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class GroupDetailsActivity extends AppCompatActivity {

    private ExpenseHelper expenseHelper;
    private ArrayList<Expense> expenseList;
    private ExpenseAdapter expenseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        // Get data from intent
        Intent intent = getIntent();
        long groupId = intent.getLongExtra("GROUP_ID", -1); // Ensure groupId is retrieved as long
        String groupName = intent.getStringExtra("GROUP_NAME");
        String groupImage = intent.getStringExtra("GROUP_IMAGE");

        // Bind views
        TextView groupNameText = findViewById(R.id.groupName);
        ImageView groupImageView = findViewById(R.id.groupImage);
        ImageView backArrow = findViewById(R.id.backButton);
        RecyclerView expenseRecyclerView = findViewById(R.id.expenseList);
        FloatingActionButton addExpenseButton = findViewById(R.id.addExpenseButton);

        // Set data to views
        groupNameText.setText(groupName);
        if (groupImage != null && !groupImage.isEmpty()) {
            // Load image using Glide or Picasso
        } else {
            groupImageView.setImageResource(R.drawable.ic_placeholder); // Placeholder
        }

        // Initialize database helper and load expenses
        expenseHelper = new ExpenseHelper(this);
        expenseList = expenseHelper.getExpensesForGroup(groupName);

        // Setup RecyclerView
        expenseAdapter = new ExpenseAdapter(this, expenseList);
        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        expenseRecyclerView.setAdapter(expenseAdapter);

        // Back button click listener
        backArrow.setOnClickListener(v -> finish());

        // Floating action button for adding expense
        addExpenseButton.setOnClickListener(v -> {
            Intent addExpenseIntent = new Intent(GroupDetailsActivity.this, AddExpenseActivity.class);
            addExpenseIntent.putExtra("GROUP_ID", groupId); // Pass the group ID
            addExpenseIntent.putExtra("GROUP_NAME", groupName);
            startActivity(addExpenseIntent);
        });
    }
}
