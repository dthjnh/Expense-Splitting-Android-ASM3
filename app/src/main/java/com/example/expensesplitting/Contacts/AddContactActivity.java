package com.example.expensesplitting.Contacts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Database.ContactDatabaseHelper;
import com.example.expensesplitting.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddContactActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private Button buttonCancel, buttonSearch;
    private ContactDatabaseHelper databaseHelper;
    private FirebaseFirestore firestore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        editTextEmail = findViewById(R.id.editTextEmail);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonCancel = findViewById(R.id.buttonCancel);

        databaseHelper = new ContactDatabaseHelper(this);
        firestore = FirebaseFirestore.getInstance();

        buttonSearch.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(AddContactActivity.this, "Please enter an email address", Toast.LENGTH_SHORT).show();
            } else {
                if (isContactInLocalDatabase(email)) {
                    Toast.makeText(AddContactActivity.this, "Contact already exists in your contacts", Toast.LENGTH_SHORT).show();
                } else {
                    searchContactInFirebase(email);
                }
            }
        });

        buttonCancel.setOnClickListener(v -> finish());
    }

    private boolean isContactInLocalDatabase(String email) {
        for (Contact contact : databaseHelper.getAllContacts()) {
            if (contact.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    private void searchContactInFirebase(String email) {
        firestore.collection("users")
                .whereEqualTo("EmailAddress", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        task.getResult().getDocuments().forEach(document -> {
                            String name = document.getString("FirstName") + " " + document.getString("LastName");
                            String profileEmail = document.getString("EmailAddress");
                            showContactDialog(name, profileEmail, R.drawable.ic_avatar);
                        });
                    } else {
                        Toast.makeText(AddContactActivity.this, "User not found in Firebase", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddContactActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showContactDialog(String name, String email, int avatarResource) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_contact_details, null);

        ImageView avatar = dialogView.findViewById(R.id.dialogContactAvatar);
        TextView contactName = dialogView.findViewById(R.id.dialogContactName);
        TextView contactEmail = dialogView.findViewById(R.id.dialogContactEmail);
        TextView contactStatus = dialogView.findViewById(R.id.dialogContactStatus);
        Button cancelButton = dialogView.findViewById(R.id.dialogCancel);
        Button addContactButton = dialogView.findViewById(R.id.dialogAddContact);

        avatar.setImageResource(avatarResource);
        contactName.setText(name);
        contactEmail.setText(email);
        contactStatus.setText("This account is not in your contacts yet.");

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        cancelButton.setOnClickListener(v -> alertDialog.dismiss());

        addContactButton.setOnClickListener(v -> {
            Contact newContact = new Contact(0, name, email, avatarResource, false);
            boolean added = databaseHelper.addContact(newContact);

            if (added) {
                Toast.makeText(AddContactActivity.this, "Contact added successfully!", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                finish();
            } else {
                Toast.makeText(AddContactActivity.this, "Contact already exists", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }
}
