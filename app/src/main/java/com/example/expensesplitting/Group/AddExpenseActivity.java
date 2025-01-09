package com.example.expensesplitting.Group;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Database.ExpenseHelper;
import com.example.expensesplitting.Database.GroupHelper;
import com.example.expensesplitting.R;

import java.util.ArrayList;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText titleInput, amountInput, notesInput;
    private Spinner paidBySpinner;
    private TextView categoryText, splitByText;
    private ImageView categoryIcon;
    private String selectedCategory = "General", splitMethod = "Equally";
    private long groupId;
    private ExpenseHelper expenseHelper;
    private GroupHelper groupHelper;
    private static final int SPLIT_BY_REQUEST_CODE = 2;
    private static final int CATEGORY_SELECTION_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Get group details from intent
        groupId = getIntent().getLongExtra("GROUP_ID", -1);

        // Initialize views
        titleInput = findViewById(R.id.titleInput);
        amountInput = findViewById(R.id.amountInput);
        notesInput = findViewById(R.id.notesInput);
        paidBySpinner = findViewById(R.id.paidBySpinner);
        categoryIcon = findViewById(R.id.categoryIcon);
        categoryText = findViewById(R.id.categoryText);
        splitByText = findViewById(R.id.splitByText);

        Button saveButton = findViewById(R.id.saveButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        LinearLayout categoryButtonLayout = findViewById(R.id.categoryButtonLayout);
        LinearLayout splitByButtonLayout = findViewById(R.id.splitByButtonLayout);

        // Initialize helpers
        expenseHelper = new ExpenseHelper(this);
        groupHelper = new GroupHelper(this);

        // Populate Paid By Spinner
        populatePaidBySpinner();

        // Handle category selection
        categoryButtonLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AddExpenseActivity.this, CategorySelectionActivity.class);
            intent.putExtra("SELECTED_CATEGORY", selectedCategory);
            startActivityForResult(intent, CATEGORY_SELECTION_REQUEST_CODE);
        });

        // Handle split by selection
        splitByButtonLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AddExpenseActivity.this, SplitByActivity.class);
            intent.putExtra("GROUP_ID", groupId); // Pass the group ID
            intent.putExtra("TOTAL_AMOUNT", getEnteredAmount());
            intent.putExtra("PARTICIPANTS", getParticipants()); // Use putExtra for Serializable
            startActivityForResult(intent, SPLIT_BY_REQUEST_CODE);
        });

        // Save and cancel actions
        saveButton.setOnClickListener(v -> saveExpense());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void populatePaidBySpinner() {
        ArrayList<String> participants = new ArrayList<>();
        Cursor cursor = groupHelper.getParticipantsForGroup(groupId);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                participants.add(cursor.getString(cursor.getColumnIndexOrThrow("participant_name")));
            }
            cursor.close();
        }

        if (participants.isEmpty()) {
            participants.add("You");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, participants);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paidBySpinner.setAdapter(adapter);
    }

    private double getEnteredAmount() {
        try {
            return Double.parseDouble(amountInput.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount entered", Toast.LENGTH_SHORT).show();
            return 0;
        }
    }

    private ArrayList<Participant> getParticipants() {
        ArrayList<Participant> participants = new ArrayList<>();
        Cursor cursor = groupHelper.getParticipantsForGroup(groupId);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                participants.add(new Participant(cursor.getString(cursor.getColumnIndexOrThrow("participant_name"))));
            }
            cursor.close();
        }

        return participants;
    }

    private void saveExpense() {
        String title = titleInput.getText().toString().trim();
        double amount = getEnteredAmount();
        String notes = notesInput.getText().toString().trim();
        String paidBy = paidBySpinner.getSelectedItem().toString();

        if (title.isEmpty() || amount <= 0) {
            Toast.makeText(this, "Please provide valid title and amount.", Toast.LENGTH_SHORT).show();
            return;
        }

        Expense expense = new Expense(groupId, title, amount, selectedCategory, paidBy, splitMethod, notes);
        boolean success = expenseHelper.addExpense(expense);

        if (success) {
            Toast.makeText(this, "Expense saved successfully!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
        } else {
            Toast.makeText(this, "Failed to save expense!", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CATEGORY_SELECTION_REQUEST_CODE && resultCode == RESULT_OK) {
            selectedCategory = data.getStringExtra("SELECTED_CATEGORY");

            // Update the category text and icon
            categoryText.setText(selectedCategory);
            setCategoryIcon(selectedCategory);
        } else if (requestCode == SPLIT_BY_REQUEST_CODE && resultCode == RESULT_OK) {
            splitMethod = data.getStringExtra("SPLIT_METHOD");
            splitByText.setText(splitMethod);
        }
    }

    private void setCategoryIcon(String category) {
        // Dynamically set the icon based on the selected category
        switch (category.toLowerCase()) {
            case "games":
                categoryIcon.setImageResource(R.drawable.game);
                break;
            case "movies":
                categoryIcon.setImageResource(R.drawable.movie);
                break;
            case "music":
                categoryIcon.setImageResource(R.drawable.music);
                break;
            case "sports":
                categoryIcon.setImageResource(R.drawable.sport);
                break;
            case "groceries":
                categoryIcon.setImageResource(R.drawable.groceries);
                break;
            case "dining out":
                categoryIcon.setImageResource(R.drawable.dining);
                break;
            case "liquor":
                categoryIcon.setImageResource(R.drawable.liquor);
                break;
            case "ticket":
                categoryIcon.setImageResource(R.drawable.airline);
                break;
            case "shopping":
                categoryIcon.setImageResource(R.drawable.shopping);
                break;
            case "car":
                categoryIcon.setImageResource(R.drawable.transport);
                break;
            case "hotel":
                categoryIcon.setImageResource(R.drawable.hotel);
                break;
            default:
                categoryIcon.setImageResource(R.drawable.category);
                break;
        }
    }
}
