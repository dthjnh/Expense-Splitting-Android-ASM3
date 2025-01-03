package com.example.expensesplitting.User.Pay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.expensesplitting.Model.Transaction;
import com.example.expensesplitting.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PaymentBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_TRANSACTION = "transaction";

    private Transaction transaction;

    public static PaymentBottomSheetFragment newInstance(Transaction transaction) {
        PaymentBottomSheetFragment fragment = new PaymentBottomSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRANSACTION, transaction);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_bottom_sheet, container, false);

        if (getArguments() != null) {
            transaction = (Transaction) getArguments().getSerializable(ARG_TRANSACTION);
        }

        TextView amountText = view.findViewById(R.id.amount_text);
        TextView recipientName = view.findViewById(R.id.recipient_name);
        TextView recipientEmail = view.findViewById(R.id.recipient_email);
        TextView noteText = view.findViewById(R.id.notes);

        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> dismiss());

        Button payButton = view.findViewById(R.id.new_payment_button);
        payButton.setOnClickListener(v -> {
            // Handle pay button click
            Toast.makeText(getContext(), "Payment successful", Toast.LENGTH_SHORT).show();
        });

        if (transaction != null) {
            amountText.setText(transaction.getAmount());
            recipientName.setText(transaction.getRecipient());
            recipientEmail.setText(transaction.getUsername());
            noteText.setText(transaction.getNote());

            amountText.setEnabled(false);
            recipientName.setEnabled(false);
            recipientEmail.setEnabled(false);
            noteText.setEnabled(false);
        }

        return view;
    }
}