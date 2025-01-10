package com.example.expensesplitting.User.Request;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.example.expensesplitting.User.Transaction.TransactionAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RequestBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_TRANSACTION = "transaction";
    private Transaction transaction;
    private String transactionId;
    private FirebaseUser currentUser;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TransactionAdapter transactionAdapter;

    public interface OnTransactionsUpdatedListener {
        void onTransactionsUpdated(List<Transaction> updatedTransactions);
    }

    private OnTransactionsUpdatedListener transactionsUpdatedListener;

    private void fetchTransactions() {
        db.collection("transactions").
                whereEqualTo("type", "request").
                get().
                addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Transaction> transactionList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Transaction transaction = document.toObject(Transaction.class);
                    transactionList.add(transaction);
                }
                // Notify the activity with the updated list
                if (transactionsUpdatedListener != null) {
                    transactionsUpdatedListener.onTransactionsUpdated(transactionList);
                }
            } else {
                Log.e("RequestBottomSheetFragment", "Error fetching transactions", task.getException());
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTransactionsUpdatedListener) {
            transactionsUpdatedListener = (OnTransactionsUpdatedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnTransactionsUpdatedListener");
        }
    }

    public static RequestBottomSheetFragment newInstance(Transaction transaction) {
        RequestBottomSheetFragment fragment = new RequestBottomSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRANSACTION, transaction);
        args.putString("transactionId", transaction.getDocumentId());
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transaction = (Transaction) getArguments().getSerializable(ARG_TRANSACTION);
            transactionId = getArguments().getString("transactionId");
            Log.d("PaymentBottomSheetFragment", "Transaction ID: " + transactionId);
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_bottom_sheet, container, false);

        if (transaction != null) {
            TextView amountText = view.findViewById(R.id.amount_text);
            TextView requestUsername = view.findViewById(R.id.request_username);
            TextView requestUserEmail = view.findViewById(R.id.request_user_email);
            TextView noteText = view.findViewById(R.id.notes);

            Button cancelButton = view.findViewById(R.id.continue_button);
            cancelButton.setOnClickListener(v -> dismiss());

            currentUser = FirebaseAuth.getInstance().getCurrentUser();

            Button requestBottom = view.findViewById(R.id.new_request_button);
            if (transaction.getStatus() != null && transaction.getStatus().equals("requested")) {
                requestBottom.setText("Request Sent");
                requestBottom.setEnabled(false);
                requestBottom.setBackgroundColor(getResources().getColor(R.color.light_gray));
            }
            requestBottom.setOnClickListener(v -> {
                // Retrieve the request transaction details
                String amountString = amountText.getText().toString().replace("$", "");
                double amount = Double.parseDouble(amountString);
                String note = noteText.getText().toString();
                Date currentDate = new Date();

                // Create new payment transaction
                Transaction newPaymentTransaction = new Transaction(
                        null,
                        "pay",
                        transaction.getUsername(),
                        transaction.getUserEmail(),
                        currentUser.getDisplayName(),
                        currentUser.getEmail(),
                        amount,
                        currentDate,
                        note,
                        "unpaid"
                );

                // Add payment transaction to Firestore
                db.collection("transactions").add(newPaymentTransaction).addOnSuccessListener(documentReference -> {
                    String paymentTransactionId = documentReference.getId();
                    newPaymentTransaction.setDocumentId(paymentTransactionId);

                    // Update the status of the request transaction to "requested"
                    db.collection("transactions").document(transactionId)
                            .update("status", "requested")
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Payment created and request status updated successfully", Toast.LENGTH_SHORT).show();
                                fetchTransactions();
                                dismiss();

                                RequestSuccessBottomFragment successFragment = new RequestSuccessBottomFragment();
                                successFragment.show(getParentFragmentManager(), successFragment.getTag());
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to update request status", Toast.LENGTH_SHORT).show();
                                Log.e("RequestBottomSheetFragment", "Error updating request status", e);
                            });
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Payment creation failed", Toast.LENGTH_SHORT).show();
                    Log.e("RequestBottomSheetFragment", "Error creating payment", e);
                });
            });

            double amount = transaction.getAmount();
            @SuppressLint("DefaultLocale") String formattedAmount = String.format("$%.2f", amount);
            amountText.setText(formattedAmount);
            requestUsername.setText(transaction.getUsername());
            if (transaction.getRecipientEmail() != null) {
                requestUserEmail.setText(transaction.getUserEmail());
            } else {
                requestUserEmail.setVisibility(View.GONE);
            }
            noteText.setText(transaction.getNote());

            amountText.setEnabled(false);
            requestUsername.setEnabled(false);
            requestUserEmail.setEnabled(false);
            noteText.setEnabled(false);
        } else {
            Toast.makeText(getContext(), "Transaction is null", Toast.LENGTH_SHORT).show();
            dismiss();
        }

        return view;
    }
}
