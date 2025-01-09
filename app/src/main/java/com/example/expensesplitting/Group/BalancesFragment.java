package com.example.expensesplitting.Group;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Database.ExpenseHelper;
import com.example.expensesplitting.Database.GroupHelper;
import com.example.expensesplitting.R;

import java.util.ArrayList;
import java.util.List;

public class BalancesFragment extends Fragment {

    private static final String ARG_GROUP_ID = "groupId";
    private static final String TAG = "BalancesFragment";
    private long groupId;
    private ExpenseHelper expenseHelper;
    private GroupHelper groupHelper;
    private RecyclerView recyclerViewBalances;
    private BalancesAdapter balancesAdapter;

    public BalancesFragment() {
        // Required empty public constructor
    }

    public static BalancesFragment newInstance(long groupId) {
        BalancesFragment fragment = new BalancesFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getLong(ARG_GROUP_ID);
        }
        expenseHelper = new ExpenseHelper(requireContext());
        groupHelper = new GroupHelper(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_balances, container, false);

        recyclerViewBalances = view.findViewById(R.id.recyclerViewBalances);
        recyclerViewBalances.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Load balances and set up adapter
        List<Balance> balances = calculateBalances();
        balancesAdapter = new BalancesAdapter(balances);
        recyclerViewBalances.setAdapter(balancesAdapter);

        return view;
    }

    /**
     * Calculate balances based on split details for the group.
     *
     * @return A list of balances showing who owes whom and how much.
     */
    private List<Balance> calculateBalances() {
        List<Balance> balances = new ArrayList<>();
        Cursor cursor = expenseHelper.getExpensesCursorForGroup(groupId); // Correct method call

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String paidBy = cursor.getString(cursor.getColumnIndexOrThrow("paid_by"));
                    String splitBy = cursor.getString(cursor.getColumnIndexOrThrow("split_by"));
                    double totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));

                    if (splitBy != null && !splitBy.isEmpty()) {
                        if (splitBy.equals("Equal")) {
                            // Handle Equal split
                            List<String> participants = getParticipantsForGroup(groupId);
                            if (participants != null && !participants.isEmpty()) {
                                double equalAmount = totalAmount / participants.size();
                                for (String participant : participants) {
                                    if (!participant.equals(paidBy)) {
                                        balances.add(new Balance(participant, paidBy, equalAmount));
                                    }
                                }
                            } else {
                                Log.e(TAG, "No participants found for group ID: " + groupId);
                            }
                        } else if (splitBy.equals("Unequal")) {
                            // Handle Unequal split with proper details
                            String splitDetails = cursor.getString(cursor.getColumnIndexOrThrow("split_details"));
                            if (splitDetails != null && !splitDetails.isEmpty()) {
                                String[] details = splitDetails.split(",");
                                for (String detail : details) {
                                    String[] parts = detail.split(":");
                                    if (parts.length == 2) {
                                        String participant = parts[0].trim();
                                        try {
                                            double amount = Double.parseDouble(parts[1].trim());
                                            if (!participant.equals(paidBy)) {
                                                balances.add(new Balance(participant, paidBy, amount));
                                            }
                                        } catch (NumberFormatException e) {
                                            Log.e(TAG, "Invalid amount in split_details: " + detail, e);
                                        }
                                    } else {
                                        Log.e(TAG, "Invalid split detail format: " + detail);
                                    }
                                }
                            } else {
                                Log.e(TAG, "Empty or null split_details field for Unequal split");
                            }
                        } else {
                            Log.e(TAG, "Invalid split_by field: " + splitBy);
                        }
                    } else {
                        Log.e(TAG, "Empty or null split_by field for expense");
                    }
                }
            } finally {
                cursor.close();
            }
        } else {
            Log.e(TAG, "No expenses found for group ID: " + groupId);
        }

        return balances;
    }

    /**
     * Retrieve participants for the group using GroupHelper.
     *
     * @param groupId The ID of the group.
     * @return A list of participant names.
     */
    private List<String> getParticipantsForGroup(long groupId) {
        List<String> participants = new ArrayList<>();
        Cursor cursor = groupHelper.getParticipantsForGroup(groupId);

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    participants.add(cursor.getString(cursor.getColumnIndexOrThrow(GroupHelper.COLUMN_PARTICIPANT_NAME)));
                }
            } finally {
                cursor.close();
            }
        } else {
            Log.e(TAG, "No participants found for group ID: " + groupId);
        }

        return participants;
    }
}
