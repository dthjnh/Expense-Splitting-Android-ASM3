package com.example.expensesplitting.Contacts;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Database.ContactDatabaseHelper;
import com.example.expensesplitting.R;

public class ContactDetailActivity extends AppCompatActivity {

    private ImageView contactAvatar;
    private TextView contactName, contactEmail;
    private Button btnDeleteContact, btnRequest, btnPay;
    private ImageButton btnBack, btnFavorite;
    private ContactDatabaseHelper databaseHelper;
    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        // Initialize components
        contactAvatar = findViewById(R.id.contactAvatar);
        contactName = findViewById(R.id.contactName);
        contactEmail = findViewById(R.id.contactEmail);
        btnDeleteContact = findViewById(R.id.btnDeleteContact);
        btnRequest = findViewById(R.id.btnRequest);
        btnPay = findViewById(R.id.btnPay);
        btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnFavorite);

        databaseHelper = new ContactDatabaseHelper(this);

        // Get contact ID from intent
        int contactId = getIntent().getIntExtra("CONTACT_ID", -1);
        if (contactId != -1) {
            contact = databaseHelper.getContactById(contactId);

            if (contact != null) {
                // Set contact details
                contactAvatar.setImageResource(contact.getAvatar());
                contactName.setText(contact.getName());
                contactEmail.setText(contact.getEmail());
                btnFavorite.setImageResource(contact.isFavorite() ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
            }
        }

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Favorite toggle
        btnFavorite.setOnClickListener(v -> {
            contact.setFavorite(!contact.isFavorite());
            databaseHelper.updateContactFavoriteStatus(contact);
            btnFavorite.setImageResource(contact.isFavorite() ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
            Toast.makeText(this, "Favorite updated", Toast.LENGTH_SHORT).show();
        });

        // Delete button
        btnDeleteContact.setOnClickListener(v -> {
            if (contact != null) {
                showCustomDeleteDialog(contact);
            } else {
                Toast.makeText(this, "Contact not found", Toast.LENGTH_SHORT).show();
            }
        });


        // Request button
        btnRequest.setOnClickListener(v -> Toast.makeText(this, "Request clicked", Toast.LENGTH_SHORT).show());

        // Pay button
       //btnPay.setOnClickListener(v -> startActivity(new Intent(this, AmountToPayActivity.class)));

    }

    private void deleteContact(Contact contact) {
        databaseHelper.deleteContactById(contact.getId());
        Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showCustomDeleteDialog(Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.custom_dialog_delete, null);
        builder.setView(customView);

        // Set dialog views
        TextView dialogMessage = customView.findViewById(R.id.dialogMessage);
        dialogMessage.setText("Delete \"" + contact.getName() + "\" from your contacts?");

        Button cancelButton = customView.findViewById(R.id.cancelButton);
        Button confirmDeleteButton = customView.findViewById(R.id.confirmDeleteButton);

        AlertDialog alertDialog = builder.create();

        // Cancel action
        cancelButton.setOnClickListener(v -> alertDialog.dismiss());

        // Confirm delete action
        confirmDeleteButton.setOnClickListener(v -> {
            deleteContact(contact);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

}
