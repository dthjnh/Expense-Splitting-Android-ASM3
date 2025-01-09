package com.example.expensesplitting.Group;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.expensesplitting.R;

public class BalancesFragment extends Fragment {

    private static final String ARG_GROUP_ID = "groupId";
    private long groupId;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_balances, container, false);
    }
}