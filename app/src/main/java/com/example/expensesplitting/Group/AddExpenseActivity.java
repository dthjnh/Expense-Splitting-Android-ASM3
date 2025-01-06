package com.example.expensesplitting.Group;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    private TextView categoryButton;
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
        categoryButton = findViewById(R.id.categoryButton);

        Button splitByButton = findViewById(R.id.splitByButton);
        Button saveButton = findViewById(R.id.saveButton);
        Button cancelButton = findViewById(R.id.cancelButton);

        // Initialize helpers
        expenseHelper = new ExpenseHelper(this);
        groupHelper = new GroupHelper(this);

        // Populate Paid By Spinner
        populatePaidBySpinner();

        // Handle category selection
        categoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddExpenseActivity.this, CategorySelectionActivity.class);
            intent.putExtra("SELECTED_CATEGORY", selectedCategory);
            startActivityForResult(intent, CATEGORY_SELECTION_REQUEST_CODE);
        });

        splitByButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddExpenseActivity.this, SplitByActivity.class);
            intent.putExtra("GROUP_ID", groupId); // Pass the group ID
            intent.putExtra("TOTAL_AMOUNT", getEnteredAmount());
            intent.putExtra("PARTICIPANTS", getParticipants()); // Use putExtra for Serializable
            startActivityForResult(intent, SPLIT_BY_REQUEST_CODE);
        });

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
        // Logic to save the expense
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CATEGORY_SELECTION_REQUEST_CODE && resultCode == RESULT_OK) {
            selectedCategory = data.getStringExtra("SELECTED_CATEGORY");
            categoryButton.setText(selectedCategory);
        } else if (requestCode == SPLIT_BY_REQUEST_CODE && resultCode == RESULT_OK) {
            splitMethod = data.getStringExtra("SPLIT_METHOD");
        }
    }
}
