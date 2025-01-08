package com.example.expensesplitting.Group;

import android.content.Intent;
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
import com.example.expensesplitting.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ExpensesFragment extends Fragment {

    private static final String TAG = "ExpensesFragment";
    private static final String ARG_GROUP_ID = "GROUP_ID";

    private ExpenseHelper expenseHelper;
    private ArrayList<Expense> expenseList;
    private ExpenseAdapter expenseAdapter;
    private long groupId;

    public static ExpensesFragment newInstance(long groupId) {
        ExpensesFragment fragment = new ExpensesFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getLong(ARG_GROUP_ID, -1);
        }
        expenseHelper = new ExpenseHelper(requireContext());
        expenseList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        // Initialize RecyclerView and FAB
        RecyclerView expenseRecyclerView = view.findViewById(R.id.expenseList);
        FloatingActionButton addExpenseButton = view.findViewById(R.id.addExpenseButton);

        // Setup RecyclerView
        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        expenseAdapter = new ExpenseAdapter(requireContext(), expenseList);
        expenseRecyclerView.setAdapter(expenseAdapter);

        // Add expense button functionality
        addExpenseButton.setOnClickListener(v -> {
            Intent addExpenseIntent = new Intent(requireContext(), AddExpenseActivity.class);
            addExpenseIntent.putExtra("GROUP_ID", groupId);
            startActivity(addExpenseIntent);
        });

        // Load expenses
        loadExpenses();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadExpenses();
    }

    private void loadExpenses() {
        expenseList.clear();
        expenseList.addAll(expenseHelper.getExpensesForGroup(groupId));

        if (expenseList.isEmpty()) {
            Log.e(TAG, "No expenses found for group ID: " + groupId);
        } else {
            Log.d(TAG, "Expenses loaded: " + expenseList.size());
        }

        expenseAdapter.notifyDataSetChanged();
    }
}
