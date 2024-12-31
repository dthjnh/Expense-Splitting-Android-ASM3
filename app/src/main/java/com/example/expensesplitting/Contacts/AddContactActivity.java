package com.example.expensesplitting.Contacts;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Database.ContactDatabaseHelper;
import com.example.expensesplitting.R;

public class AddContactActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail;
    private Button buttonSave;
    private ContactDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonSave = findViewById(R.id.buttonSave);

        databaseHelper = new ContactDatabaseHelper(this);

        // Save contact
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty()) {
                    Toast.makeText(AddContactActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    Contact newContact = new Contact(0,name, email, R.drawable.ic_avatar, false);
                    boolean added = databaseHelper.addContact(newContact);

                    if (added) {
                        Toast.makeText(AddContactActivity.this, "Contact added successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    } else {
                        Toast.makeText(AddContactActivity.this, "Contact already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
