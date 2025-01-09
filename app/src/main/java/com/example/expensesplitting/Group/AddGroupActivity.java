package com.example.expensesplitting.Group;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Database.GroupHelper;
import com.example.expensesplitting.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddGroupActivity extends AppCompatActivity {

    private EditText titleInput, descriptionInput, currencyInput;
    private ImageView coverImageView, backButton;
    private String selectedCategory;
    private Uri selectedImageUri;
    private GroupHelper dbHelper;

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

        backButton.setOnClickListener(v -> finish());
        coverImageView.setOnClickListener(v -> openImagePicker());

        // Add category buttons to list
        categoryButtons.add(findViewById(R.id.categoryTrip));
        categoryButtons.add(findViewById(R.id.categoryFamily));
        categoryButtons.add(findViewById(R.id.categoryCouple));
        categoryButtons.add(findViewById(R.id.categoryEvent));
        categoryButtons.add(findViewById(R.id.categoryProject));
        categoryButtons.add(findViewById(R.id.categoryOther));

        // Set up category button listeners
        setUpCategoryButtons();

        Button continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(v -> saveGroup());

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> finish());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        imagePickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();

                    // Take persistent permissions for the URI
                    if (selectedImageUri != null) {
                        getContentResolver().takePersistableUriPermission(
                                selectedImageUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            coverImageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

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

    private void saveGroup() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String currency = currencyInput.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || currency.isEmpty() || selectedCategory == null) {
            Toast.makeText(this, "Please fill all fields and select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        long groupId = dbHelper.insertGroup(title, description, currency, selectedCategory, selectedImageUri != null ? selectedImageUri.toString() : null);

        if (groupId == -1) {
            Toast.makeText(this, "Failed to create group", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, SelectParticipantsActivity.class);
        intent.putExtra("GROUP_ID", groupId);
        startActivity(intent);

        finish();
    }
}