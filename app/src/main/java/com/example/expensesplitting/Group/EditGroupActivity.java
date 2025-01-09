package com.example.expensesplitting.Group;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Database.GroupHelper;
import com.example.expensesplitting.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditGroupActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput, currencyInput;
    private ImageView coverImageView, backButton;
    private String selectedCategory;
    private Uri selectedImageUri;
    private GroupHelper dbHelper;
    private long groupId;

    private final List<Button> categoryButtons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        // Initialize Views
        titleInput = findViewById(R.id.groupTitleInput);
        descriptionInput = findViewById(R.id.groupDescriptionInput);
        currencyInput = findViewById(R.id.currencyInput);
        coverImageView = findViewById(R.id.coverImageView);
        backButton = findViewById(R.id.backButton);

        dbHelper = new GroupHelper(this);

        // Add category buttons to list
        categoryButtons.add(findViewById(R.id.categoryTrip));
        categoryButtons.add(findViewById(R.id.categoryFamily));
        categoryButtons.add(findViewById(R.id.categoryCouple));
        categoryButtons.add(findViewById(R.id.categoryEvent));
        categoryButtons.add(findViewById(R.id.categoryProject));
        categoryButtons.add(findViewById(R.id.categoryOther));

        // Get group ID from Intent
        groupId = getIntent().getLongExtra("GROUP_ID", -1);

        // Load existing group details
        loadGroupDetails();

        backButton.setOnClickListener(v -> finish());

        coverImageView.setOnClickListener(v -> openImagePicker());

        // Set up category button listeners
        setUpCategoryButtons();

        Button continueButton = findViewById(R.id.continueButton);
        continueButton.setText("Save Changes");
        continueButton.setOnClickListener(v -> saveGroup());

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> finish());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                coverImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUpCategoryButtons() {
        for (Button button : categoryButtons) {
            button.setOnClickListener(v -> {
                selectedCategory = button.getText().toString(); // Set selected category
                setSelectedButton(button); // Highlight selected button
                Toast.makeText(this, "Selected Category: " + selectedCategory, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setSelectedButton(Button selectedButton) {
        for (Button button : categoryButtons) {
            if (button == selectedButton) {
                button.setBackgroundTintList(getResources().getColorStateList(R.color.yellow)); // Highlight selected button
                button.setTextColor(getResources().getColor(android.R.color.black));
            } else {
                button.setBackgroundTintList(getResources().getColorStateList(R.color.white)); // Reset others to white
                button.setTextColor(getResources().getColor(android.R.color.black));
            }
        }
    }

    private void loadGroupDetails() {
        Cursor cursor = dbHelper.getGroupById(groupId);
        if (cursor != null && cursor.moveToFirst()) {
            titleInput.setText(cursor.getString(cursor.getColumnIndexOrThrow(GroupHelper.COLUMN_NAME)));
            descriptionInput.setText(cursor.getString(cursor.getColumnIndexOrThrow(GroupHelper.COLUMN_DESCRIPTION)));
            currencyInput.setText(cursor.getString(cursor.getColumnIndexOrThrow(GroupHelper.COLUMN_CURRENCY)));
            selectedCategory = cursor.getString(cursor.getColumnIndexOrThrow(GroupHelper.COLUMN_CATEGORY));
            String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(GroupHelper.COLUMN_IMAGE));

            if (imageUri != null) {
                selectedImageUri = Uri.parse(imageUri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                    coverImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }

            // Pre-select the category button based on loaded data
            preSelectCategoryButton();

            cursor.close();
        } else {
            Toast.makeText(this, "Failed to load group details", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void preSelectCategoryButton() {
        for (Button button : categoryButtons) {
            if (button.getText().toString().equalsIgnoreCase(selectedCategory)) {
                setSelectedButton(button);
                break;
            }
        }
    }

    private void saveGroup() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String currency = currencyInput.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || currency.isEmpty() || selectedCategory == null) {
            Toast.makeText(this, "Please fill all fields and select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.updateGroup(groupId, title, description, currency, selectedCategory, selectedImageUri != null ? selectedImageUri.toString() : null);

        if (success) {
            Toast.makeText(this, "Group updated successfully", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent); // Notify GroupDetailsActivity of changes
            finish();
        } else {
            Toast.makeText(this, "Failed to update group", Toast.LENGTH_SHORT).show();
        }
    }
}