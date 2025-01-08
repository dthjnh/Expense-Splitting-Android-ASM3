package com.example.expensesplitting.Group;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.expensesplitting.Database.ExpenseHelper;
import com.example.expensesplitting.R;

public class TotalsFragment extends Fragment {

    private static final String ARG_GROUP_ID = "groupId";
    private long groupId;
    private ExpenseHelper expenseHelper;

    private TextView totalSpendingText;
    private TextView totalPaidText;

    public TotalsFragment() {
        // Required empty public constructor
    }

    public static TotalsFragment newInstance(long groupId) {
        TotalsFragment fragment = new TotalsFragment();
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
        expenseHelper = new ExpenseHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_totals, container, false);

        totalSpendingText = view.findViewById(R.id.totalSpendingText);
        totalPaidText = view.findViewById(R.id.totalPaidText);

        loadTotals();

        return view;
    }

    private void loadTotals() {
        double totalSpending = 0;
        double totalPaid = 0;

        Cursor cursor = expenseHelper.getExpensesCursorForGroup(groupId);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String paidBy = cursor.getString(cursor.getColumnIndexOrThrow("paid_by"));

                totalSpending += amount;

                if ("You".equals(paidBy)) {
                    totalPaid += amount;
                }
            }
            cursor.close();
        }

        totalSpendingText.setText(String.format("$%.2f", totalSpending));
        totalPaidText.setText(String.format("$%.2f", totalPaid));
    }
}
