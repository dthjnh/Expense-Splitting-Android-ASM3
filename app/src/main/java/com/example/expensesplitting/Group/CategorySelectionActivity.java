package com.example.expensesplitting.Group;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.R;

import java.util.ArrayList;

public class CategorySelectionActivity extends AppCompatActivity {

    private ArrayList<String> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        // Initialize views
        ListView categoryListView = findViewById(R.id.categoryListView);
        TextView cancelButton = findViewById(R.id.cancelButton);

        // Initialize categories
        initializeCategories();

        // Set up the adapter
        CategoryAdapter adapter = new CategoryAdapter(this, categories, selectedCategory -> {
            // Return the selected category to the calling activity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SELECTED_CATEGORY", selectedCategory);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
        categoryListView.setAdapter(adapter);

        // Cancel button functionality
        cancelButton.setOnClickListener(v -> finish());
    }

    private void initializeCategories() {
        categories = new ArrayList<>();
        categories.add("Games");
        categories.add("Movies");
        categories.add("Music");
        categories.add("Sports");
        categories.add("Groceries");
        categories.add("Dining Out");
        categories.add("Liquor");
        categories.add("Car");
        categories.add("Ticket");
        categories.add("Hotel");
        categories.add("Shopping");
        categories.add("Other");

    }
}
