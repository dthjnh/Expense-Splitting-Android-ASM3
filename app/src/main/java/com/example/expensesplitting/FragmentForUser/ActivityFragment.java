package com.example.expensesplitting.FragmentForUser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Model.Transaction;
import com.example.expensesplitting.R;
import com.example.expensesplitting.User.Activity.TransactionActivity;
import com.example.expensesplitting.User.Transaction.TransactionAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityFragment extends Fragment implements TransactionAdapter.OnItemClickListener {
    RecyclerView recyclerView;
    TransactionAdapter adapter;
    List<Transaction> activityList;
    ImageView emptyStateText;
    LinearLayout viewAll;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        emptyStateText = view.findViewById(R.id.imageView);

        viewAll = view.findViewById(R.id.viewAllContainer);
        viewAll.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), TransactionActivity.class);
            startActivity(intent);
        });

        activityList = new ArrayList<>();

        fetchTransactions();

        adapter = new TransactionAdapter(getContext(), activityList, this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void fetchTransactions() {
        db.collection("transactions")
                .whereEqualTo("userEmail", currentUser.getEmail())
                .whereIn("type", List.of("pay", "request"))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        activityList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            String documentId = document.getId();
                            String type = document.getString("type");
                            String username = document.getString("username");
                            String userEmailAddress = document.getString("userEmail");
                            String recipient = document.getString("recipient");
                            String recipientEmail = document.getString("recipientEmail");
                            double amount = document.getDouble("amount");
                            Timestamp timestamp = document.getTimestamp("timestamp");
                            String note = document.getString("note");
                            Date date = timestamp != null ? timestamp.toDate() : null;
                            String status = document.getString("status");

                            Transaction activity = new Transaction(documentId, type, username, userEmailAddress, recipient, recipientEmail, amount, date, note, status);
                            activityList.add(activity);
                        }
                        adapter.notifyDataSetChanged();
                        updateEmptyState();
                    } else {
                        Toast.makeText(getContext(), "Error fetching activities", Toast.LENGTH_SHORT).show();
                        Log.e("ActivityFragment", "Error fetching activities", task.getException());
                    }
                });
    }

    private void updateEmptyState() {
        if (activityList.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
        } else {
            emptyStateText.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemClick(Transaction activity) {
        // Handle item click
    }
}