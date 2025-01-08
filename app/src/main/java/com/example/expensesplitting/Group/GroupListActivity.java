package com.example.expensesplitting.Group;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensesplitting.Contacts.ContactsActivity;
import com.example.expensesplitting.Database.GroupHelper;
import com.example.expensesplitting.R;
import com.example.expensesplitting.UserActivity;
import com.example.expensesplitting.AccountInfo.AccountActivity;

import java.util.ArrayList;

public class GroupListActivity extends AppCompatActivity {

    private static final String TAG = "GroupListActivity";

    private ListView listView;
    private TextView emptyStateText;
    private ArrayList<Group> groupList;
    private GroupAdapter adapter;
    private GroupHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        Log.d(TAG, "GroupListActivity launched");

        // Initialize views
        listView = findViewById(R.id.groupListView);
        emptyStateText = findViewById(R.id.emptyStateTitle);
        Button addGroupButton = findViewById(R.id.addGroupButton);

        // Initialize database helper
        dbHelper = new GroupHelper(this);
        groupList = new ArrayList<>();

        // Load groups from the database
        loadGroups();

        // Add group button listener
        addGroupButton.setOnClickListener(v -> {
            Log.d(TAG, "Add Group button clicked");
            Intent intent = new Intent(GroupListActivity.this, AddGroupActivity.class);
            startActivity(intent);
        });

        // Item click listener to open GroupDetailsActivity
        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Group selectedGroup = groupList.get(position);
            Intent intent = new Intent(GroupListActivity.this, GroupDetailsActivity.class);
            intent.putExtra("GROUP_ID", (long) selectedGroup.getId()); // Explicitly cast to long
            intent.putExtra("GROUP_NAME", selectedGroup.getName());
            intent.putExtra("GROUP_DESCRIPTION", selectedGroup.getDescription());
            intent.putExtra("GROUP_IMAGE", selectedGroup.getImage());
            startActivity(intent);
        });

        // Add a long-click listener to navigate to SplitByActivity
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Group selectedGroup = groupList.get(position);
            Intent intent = new Intent(GroupListActivity.this, SplitByActivity.class);
            intent.putExtra("GROUP_ID", (long) selectedGroup.getId()); // Explicitly cast to long
            intent.putExtra("GROUP_NAME", selectedGroup.getName());
            startActivity(intent);
            return true;
        });

        // Set up bottom navigation
        setupBottomNavigation();
    }

    private void loadGroups() {
        Log.d(TAG, "Loading groups from database");
        groupList.clear();

        try (Cursor cursor = dbHelper.getAllGroups()) {
            if (cursor == null) {
                Log.e(TAG, "Cursor is null. Database query failed.");
                return;
            }

            if (cursor.moveToFirst()) {
                do {
                    // Extract data from the cursor
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    String currency = cursor.getString(cursor.getColumnIndexOrThrow("currency"));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                    String image = cursor.getString(cursor.getColumnIndexOrThrow("image")); // Retrieve the group image URI

                    // Add group to the list
                    groupList.add(new Group(id, name, description, currency, category, image));
                } while (cursor.moveToNext());
            } else {
                Log.d(TAG, "No groups found in the database.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while loading groups: ", e);
        }

        // Update the UI based on the group list
        updateUI();
    }

    private void updateUI() {
        if (groupList.isEmpty()) {
            Log.d(TAG, "Group list is empty. Showing empty state.");
            emptyStateText.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "Group list has items. Updating list view.");
            emptyStateText.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            if (adapter == null) {
                adapter = new GroupAdapter(this, groupList);
                listView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void setupBottomNavigation() {
        // Home button (UserActivity)
        ImageButton homeButton = findViewById(R.id.navHome);
        homeButton.setOnClickListener(v -> {
            Log.d(TAG, "Home button clicked");
            Intent intent = new Intent(GroupListActivity.this, UserActivity.class);
            startActivity(intent);
            finish();
        });

        // Groups button (stay in current activity)
        ImageButton groupsButton = findViewById(R.id.navGroups);
        groupsButton.setOnClickListener(v -> Log.d(TAG, "Groups button clicked (current activity)"));

        // Contacts button
        ImageButton contactsButton = findViewById(R.id.navContacts);
        contactsButton.setOnClickListener(v -> {
            Log.d(TAG, "Contacts button clicked");
            Intent intent = new Intent(GroupListActivity.this, ContactsActivity.class);
            startActivity(intent);
            finish();
        });

        // Account button
        ImageButton accountButton = findViewById(R.id.navAccount);
        accountButton.setOnClickListener(v -> {
            Log.d(TAG, "Account button clicked");
            Intent intent = new Intent(GroupListActivity.this, AccountActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called. Reloading groups.");
        loadGroups();
    }
}
