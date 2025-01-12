package com.example.expensesplitting.User.Pay;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PaymentBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_TRANSACTION = "transaction";
    private Transaction transaction;
    private String transactionId;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    public static PaymentBottomSheetFragment newInstance(Transaction transaction) {
        PaymentBottomSheetFragment fragment = new PaymentBottomSheetFragment();
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
        View view = inflater.inflate(R.layout.fragment_payment_bottom_sheet, container, false);

        if (transaction != null) {
            TextView amountText = view.findViewById(R.id.amount_text);
            TextView recipientName = view.findViewById(R.id.request_username);
            TextView recipientEmail = view.findViewById(R.id.recipient_detail);
            TextView noteText = view.findViewById(R.id.notes);

            Button cancelButton = view.findViewById(R.id.continue_button);
            cancelButton.setOnClickListener(v -> dismiss());

            Button payButton = view.findViewById(R.id.new_request_button);
            payButton.setOnClickListener(v -> {
                assert currentUser != null;
                updateWalletBalance(currentUser.getUid(), transaction.getRecipientEmail(), transaction.getAmount(),
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                db.collection("transactions").document(transactionId)
                                        .update("status", "paid")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid1) {
                                                Toast.makeText(getContext(), "Payment successful", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getContext(), PaymentReceiptActivity.class);
                                                intent.putExtra("transaction", transaction);
                                                intent.putExtra("transactionId", transactionId);
                                                startActivity(intent);
                                                dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), "Failed to update recipient's balance", Toast.LENGTH_SHORT).show();
                                                Log.e("PaymentBottomSheetFragment", "Failed to update recipient's balance: ", e);
                                                dismiss();
                                            }
                                        });
                            }
                        },
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to update payer's balance", Toast.LENGTH_SHORT).show();
                                Log.e("PaymentBottomSheetFragment", "Failed to update payer's balance: ", e);
                                dismiss();
                            }
                        });
            });

            double amount = transaction.getAmount();
            @SuppressLint("DefaultLocale") String formattedAmount = String.format("$%.2f", amount);
            amountText.setText(formattedAmount);
            recipientName.setText(transaction.getRecipient());
            if (transaction.getRecipientEmail() != null) {
                recipientEmail.setText(transaction.getRecipientEmail());
            } else {
                recipientEmail.setVisibility(View.GONE);
            }
            noteText.setText(transaction.getNote());

            amountText.setEnabled(false);
            recipientName.setEnabled(false);
            recipientEmail.setEnabled(false);
            noteText.setEnabled(false);

            if (transaction.getStatus().equals("paid")) {
                payButton.setEnabled(false);
                payButton.setText("Paid");
                payButton.setBackgroundColor(getResources().getColor(R.color.light_gray));
            }
        } else {
            Toast.makeText(getContext(), "Transaction is null", Toast.LENGTH_SHORT).show();
            dismiss();
        }

        return view;
    }

    private void updateWalletBalance(String payerId, String recipientEmail, double amount, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        // Update payer's balance
        db.collection("wallets")
                .whereEqualTo("userId", payerId)
                .get()
                .addOnSuccessListener(payerQueryDocumentSnapshots -> {
                    if (!payerQueryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot payerDocument = payerQueryDocumentSnapshots.getDocuments().get(0);
                        double payerCurrentBalance = payerDocument.getDouble("balance");
                        double payerNewBalance = payerCurrentBalance - amount;

                        db.collection("wallets").document(payerDocument.getId())
                                .update("balance", payerNewBalance)
                                .addOnSuccessListener(aVoid -> {
                                    // Fetch recipient's user ID using email
                                    db.collection("users")
                                            .whereEqualTo("EmailAddress", recipientEmail)
                                            .get()
                                            .addOnSuccessListener(userQueryDocumentSnapshots -> {
                                                if (!userQueryDocumentSnapshots.isEmpty()) {
                                                    DocumentSnapshot userDocument = userQueryDocumentSnapshots.getDocuments().get(0);
                                                    String recipientId = userDocument.getId();

                                                    // Update recipient's balance
                                                    db.collection("wallets")
                                                            .whereEqualTo("userId", recipientId)
                                                            .get()
                                                            .addOnSuccessListener(recipientQueryDocumentSnapshots -> {
                                                                if (!recipientQueryDocumentSnapshots.isEmpty()) {
                                                                    DocumentSnapshot recipientDocument = recipientQueryDocumentSnapshots.getDocuments().get(0);
                                                                    double recipientCurrentBalance = recipientDocument.getDouble("balance");
                                                                    double recipientNewBalance = recipientCurrentBalance + amount;

                                                                    db.collection("wallets").document(recipientDocument.getId())
                                                                            .update("balance", recipientNewBalance)
                                                                            .addOnSuccessListener(onSuccessListener)
                                                                            .addOnFailureListener(onFailureListener);
                                                                } else {
                                                                    onFailureListener.onFailure(new Exception("Recipient's wallet not found"));
                                                                }
                                                            })
                                                            .addOnFailureListener(onFailureListener);
                                                } else {
                                                    onFailureListener.onFailure(new Exception("Recipient not found"));
                                                }
                                            })
                                            .addOnFailureListener(onFailureListener);
                                })
                                .addOnFailureListener(onFailureListener);
                    } else {
                        onFailureListener.onFailure(new Exception("Payer's wallet not found"));
                    }
                })
                .addOnFailureListener(onFailureListener);
    }
}
