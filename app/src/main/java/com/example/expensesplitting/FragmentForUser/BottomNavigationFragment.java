package com.example.expensesplitting.FragmentForUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.expensesplitting.AccountInfo.AccountActivity;
import com.example.expensesplitting.Contacts.ContactsActivity;
import com.example.expensesplitting.R;
import com.example.expensesplitting.UserActivity;

public class BottomNavigationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_navigation, container, false);

        // Initialize ImageButtons
        ImageButton homeButton = view.findViewById(R.id.navHome);
        ImageButton groupsButton = view.findViewById(R.id.navGroups);
        ImageButton contactsButton = view.findViewById(R.id.navContacts);
        ImageButton accountButton = view.findViewById(R.id.navAccount);

        // Set click listeners
        homeButton.setOnClickListener(v -> openHome());
        groupsButton.setOnClickListener(v -> openGroups());
        contactsButton.setOnClickListener(v -> openContacts());
        accountButton.setOnClickListener(v -> openAccount());

        return view;
    }

    private void openHome() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), UserActivity.class);
            startActivity(intent);
        }
    }

    private void openGroups() {
        if (getActivity() != null) {
            // Replace this with your GroupsActivity if you have one
            Intent intent = new Intent(getActivity(), UserActivity.class);
            startActivity(intent);
        }
    }

    private void openContacts() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), ContactsActivity.class);
            startActivity(intent);
        }
    }

    private void openAccount() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), AccountActivity.class);
            startActivity(intent);
        }
    }
}