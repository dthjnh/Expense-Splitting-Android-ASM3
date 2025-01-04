package com.example.expensesplitting.FragmentForUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.expensesplitting.R;
import com.example.expensesplitting.User.Pay.PayActivity;
import com.example.expensesplitting.User.Request.RequestActivity;

public class TopSectionFragment extends Fragment {
    ImageButton payButton, requestButton, topUpButton, withdrawButton, historyButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_section, container, false);

        payButton = view.findViewById(R.id.pay_button);
        requestButton = view.findViewById(R.id.request_button);
        topUpButton = view.findViewById(R.id.top_up_button);
        withdrawButton = view.findViewById(R.id.withdraw_button);
        historyButton = view.findViewById(R.id.history_button);

        payButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PayActivity.class);
            startActivity(intent);
        });

        requestButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RequestActivity.class);
            startActivity(intent);
        });

        topUpButton.setOnClickListener(v -> {
            // Open TopUpFragment
            Toast.makeText(getActivity(), "Top Up", Toast.LENGTH_SHORT).show();
        });

        withdrawButton.setOnClickListener(v -> {
            // Open WithdrawFragment
            Toast.makeText(getActivity(), "Withdraw", Toast.LENGTH_SHORT).show();
        });

        historyButton.setOnClickListener(v -> {
            // Open HistoryFragment
            Toast.makeText(getActivity(), "History", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}