package com.example.expensesplitting.Contacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.example.expensesplitting.Database.ContactDatabaseHelper;
import com.example.expensesplitting.R;
import com.example.expensesplitting.UserActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private ContactAdapter contactAdapter;
    private ContactDatabaseHelper databaseHelper;
    private Button btnAllContacts, btnFavorites;
    private List<Contact> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        FloatingActionButton fabAddContact = findViewById(R.id.fabAddContact);
        btnAllContacts = findViewById(R.id.btnAllContacts);
        btnFavorites = findViewById(R.id.btnFavorites);

        databaseHelper = new ContactDatabaseHelper(this);
        contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(contactList, databaseHelper);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactAdapter);

        setSelectedButton(btnAllContacts);
        loadAllContacts();

        btnAllContacts.setOnClickListener(v -> {
            setSelectedButton(btnAllContacts);
            clearSearchQuery();
            loadAllContacts();
        });

        btnFavorites.setOnClickListener(v -> {
            setSelectedButton(btnFavorites);
            clearSearchQuery();
            loadFavoriteContacts();
        });

        fabAddContact.setOnClickListener(v -> {
            startActivity(new Intent(ContactsActivity.this, AddContactActivity.class));
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterContacts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // Refresh the dataset based on the selected tab
                    if (btnAllContacts.isSelected()) {
                        loadAllContacts();
                    } else {
                        loadFavoriteContacts();
                    }
                } else {
                    filterContacts(newText);
                }
                return false;
            }
        });
    }

    private void loadAllContacts() {
        contactList.clear();
        contactList.addAll(databaseHelper.getAllContacts());
        contactAdapter.updateList(contactList);
    }

    private void loadFavoriteContacts() {
        contactList.clear();
        contactList.addAll(databaseHelper.getFavoriteContacts());
        contactAdapter.updateList(contactList);
    }

    private void filterContacts(String query) {
        List<Contact> filteredList = new ArrayList<>();
        for (Contact contact : contactList) {
            if (contact.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(contact);
            }
        }
        contactAdapter.updateList(filteredList);
    }

    private void clearSearchQuery() {
        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.clearFocus();
        }
    }

    private void setSelectedButton(Button selectedButton) {
        if (selectedButton == btnAllContacts) {
            btnAllContacts.setBackgroundResource(R.drawable.rounded_button_yellow);
            btnAllContacts.setTextColor(getResources().getColor(android.R.color.white));
            btnFavorites.setBackgroundResource(R.drawable.rounded_button);
            btnFavorites.setTextColor(getResources().getColor(android.R.color.black));
            btnAllContacts.setSelected(true);
            btnFavorites.setSelected(false);
        } else {
            btnFavorites.setBackgroundResource(R.drawable.rounded_button_yellow);
            btnFavorites.setTextColor(getResources().getColor(android.R.color.white));
            btnAllContacts.setBackgroundResource(R.drawable.rounded_button);
            btnAllContacts.setTextColor(getResources().getColor(android.R.color.black));
            btnFavorites.setSelected(true);
            btnAllContacts.setSelected(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (btnAllContacts.isSelected()) {
            loadAllContacts();
        } else {
            loadFavoriteContacts();
        }
    }
}
