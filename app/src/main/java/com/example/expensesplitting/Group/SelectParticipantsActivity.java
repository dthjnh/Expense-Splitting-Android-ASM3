package com.example.expensesplitting.Group;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Contacts.Contact;
import com.example.expensesplitting.Database.ContactDatabaseHelper;
import com.example.expensesplitting.Database.GroupHelper;
import com.example.expensesplitting.R;

import java.util.ArrayList;
import java.util.List;

public class SelectParticipantsActivity extends AppCompatActivity implements ContactAdapter2.OnContactSelectionListener {

    private RecyclerView recyclerView;
    private ContactAdapter2 contactAdapter;
    private List<Contact> selectedContacts = new ArrayList<>();
    private GroupHelper groupDbHelper;
    private long groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_participants);

        // Get Group ID from intent
        groupId = getIntent().getLongExtra("GROUP_ID", -1);
        if (groupId == -1) {
            Toast.makeText(this, "Invalid Group ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize GroupHelper
        groupDbHelper = new GroupHelper(this);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch and display contacts
        ContactDatabaseHelper contactDbHelper = new ContactDatabaseHelper(this);
        List<Contact> contactList = contactDbHelper.getAllContacts();

        contactAdapter = new ContactAdapter2(contactList, this);
        recyclerView.setAdapter(contactAdapter);

        // Set up Save and Cancel buttons
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveSelectedParticipants());

        Button btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveSelectedParticipants() {
        if (selectedContacts.isEmpty()) {
            Toast.makeText(this, "No participants selected", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Contact contact : selectedContacts) {
            groupDbHelper.addParticipantToGroup(groupId, contact.getName());
        }

        Toast.makeText(this, "Participants saved successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onContactSelected(Contact contact) {
        if (!selectedContacts.contains(contact)) {
            selectedContacts.add(contact);
        }
    }

    @Override
    public void onContactDeselected(Contact contact) {
        selectedContacts.remove(contact);
    }
}
