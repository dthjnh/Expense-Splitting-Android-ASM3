package com.example.expensesplitting.FragmentForUser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.expensesplitting.Model.Wallet;
import com.example.expensesplitting.R;
import com.example.expensesplitting.User.Pay.PayActivity;
import com.example.expensesplitting.User.Request.RequestActivity;
import com.example.expensesplitting.User.TopUp.TopUpActivity;
import com.example.expensesplitting.User.TopUp.TopUpSummaryActivity;
import com.example.expensesplitting.User.Withdraw.WithdrawActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class TopSectionFragment extends Fragment implements TopUpSummaryActivity.WalletBalanceUpdateListener {
    ImageButton payButton, requestButton, topUpButton, withdrawButton, historyButton;
    TextView balanceAmount;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_section, container, false);

        payButton = view.findViewById(R.id.pay_button);
        requestButton = view.findViewById(R.id.request_button);
        topUpButton = view.findViewById(R.id.top_up_button);
        withdrawButton = view.findViewById(R.id.withdraw_button);
        historyButton = view.findViewById(R.id.history_button);

        balanceAmount = view.findViewById(R.id.balanceAmount);

        checkAndCreateWallet();

        payButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PayActivity.class);
            startActivity(intent);
        });

        requestButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RequestActivity.class);
            startActivity(intent);
        });

        topUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TopUpActivity.class);
            startActivity(intent);
        });

        withdrawButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WithdrawActivity.class);
            startActivity(intent);
        });

        historyButton.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "History", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onWalletBalanceUpdated(double newBalance) {
        balanceAmount.setText(String.format("$%.2f", newBalance));
    }

    @SuppressLint("DefaultLocale")
    private void checkAndCreateWallet() {
        assert currentUser != null;
        db.collection("wallets")
                .whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            createWallet();
                        } else {
                            // User already has a wallet, set the balance amount
                            Wallet wallet = task.getResult().getDocuments().get(0).toObject(Wallet.class);
                            if (wallet != null) {
                                balanceAmount.setText(String.format("$%.2f", wallet.getBalance()));
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error checking wallet: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("DefaultLocale")
    private void createWallet() {
        Wallet newWallet = new Wallet(currentUser.getUid(), 0.0, new ArrayList<>());
        db.collection("wallets")
                .add(newWallet)
                .addOnSuccessListener(documentReference -> {
                    balanceAmount.setText(String.format("$%.2f", newWallet.getBalance()));
                    Toast.makeText(getActivity(), "Wallet created successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error creating wallet: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("Error creating wallet", Objects.requireNonNull(e.getMessage()));
                });
    }
}