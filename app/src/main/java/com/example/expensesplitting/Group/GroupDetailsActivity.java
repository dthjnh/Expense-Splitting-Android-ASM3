package com.example.expensesplitting.Group;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.expensesplitting.Database.GroupHelper;
import com.example.expensesplitting.R;

public class GroupDetailsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int EDIT_GROUP_REQUEST = 2;

    private long groupId;
    private String groupName;
    private String groupImageUri;
    private GroupHelper groupHelper;
    private ImageView groupImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        // Get group ID, name, and image URI from Intent
        groupId = getIntent().getLongExtra("GROUP_ID", -1);
        groupName = getIntent().getStringExtra("GROUP_NAME");
        groupImageUri = getIntent().getStringExtra("GROUP_IMAGE");

        Log.d("GroupDetailsActivity", "Group ID: " + groupId);
        Log.d("GroupDetailsActivity", "Group Name: " + groupName);
        Log.d("GroupDetailsActivity", "Group Image URI: " + groupImageUri);

        // Set group name in toolbar
        TextView groupNameText = findViewById(R.id.groupName);
        groupNameText.setText(groupName);

        // Initialize GroupHelper
        groupHelper = new GroupHelper(this);

        // Set group image
        groupImage = findViewById(R.id.groupImage);
        if (groupImageUri != null) {
            groupImage.setImageURI(Uri.parse(groupImageUri));
        }

        // Allow user to change the group image
        groupImage.setOnClickListener(v -> pickImage());

        // Back button functionality
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Three-dot menu functionality
        ImageView moreOptions = findViewById(R.id.moreOptions);
        moreOptions.setOnClickListener(v -> showMoreOptionsDialog());

        // Default fragment: Group Info
        loadFragment(GroupInfoFragment.newInstance(groupId));

        // Handle tab clicks
        findViewById(R.id.expensesTab).setOnClickListener(v -> loadFragment(ExpensesFragment.newInstance(groupId)));
        findViewById(R.id.balancesTab).setOnClickListener(v -> loadFragment(BalancesFragment.newInstance(groupId)));
        findViewById(R.id.totalsTab).setOnClickListener(v -> loadFragment(TotalsFragment.newInstance(groupId)));
        findViewById(R.id.groupTab).setOnClickListener(v -> loadFragment(GroupInfoFragment.newInstance(groupId)));
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            groupImage.setImageURI(selectedImageUri);

            // Save the selected image URI to the database
            boolean success = groupHelper.updateGroupImage(groupId, selectedImageUri.toString());
            if (success) {
                Toast.makeText(this, "Group image updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update group image.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == EDIT_GROUP_REQUEST && resultCode == RESULT_OK) {
            // Refresh the GroupInfoFragment after editing the group
            loadFragment(GroupInfoFragment.newInstance(groupId));
        }
    }

    private void showMoreOptionsDialog() {
        String[] options = {"Edit Group", "Leave Group", "Delete Group"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Group Options")
                .setItems(options, (DialogInterface dialog, int which) -> {
                    switch (which) {
                        case 0: // Edit Group
                            Intent editIntent = new Intent(GroupDetailsActivity.this, EditGroupActivity.class);
                            editIntent.putExtra("GROUP_ID", groupId);
                            startActivityForResult(editIntent, EDIT_GROUP_REQUEST);
                            break;

                        case 1: // Leave Group
                            confirmLeaveGroup();
                            break;

                        case 2: // Delete Group
                            confirmDeleteGroup();
                            break;
                    }
                });
        builder.show();
    }

    private void confirmLeaveGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to leave this group?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    boolean success = groupHelper.deleteParticipant(groupId, "You");
                    if (success) {
                        Toast.makeText(this, "You have left the group.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to leave the group.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null);

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(android.R.color.black));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.black));
        });

        dialog.show();
    }

    private void confirmDeleteGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this group?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    boolean success = groupHelper.deleteGroup(groupId);
                    if (success) {
                        Toast.makeText(this, "Group deleted successfully.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to delete the group.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null);

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(android.R.color.black));
        });

        dialog.show();
    }
}
