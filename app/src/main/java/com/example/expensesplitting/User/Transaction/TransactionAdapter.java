package com.example.expensesplitting.User.Transaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Model.Transaction;
import com.example.expensesplitting.R;
import com.example.expensesplitting.User.Pay.PaymentReceiptActivity;
import com.example.expensesplitting.User.Request.RequestBottomSheetFragment;
import com.example.expensesplitting.User.Request.RequestSuccessBottomFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private Context context;
    private List<Transaction> transactionList;
    private OnItemClickListener onItemClickListener;
    private FirebaseUser currentUser;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    public TransactionAdapter(Context context, List<Transaction> transactionList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.transactionList = transactionList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        holder.usernameTextView.setText(transaction.getUsername());
        holder.recipientTextView.setText(transaction.getRecipient());
        @SuppressLint("DefaultLocale") String formattedAmount = String.format("$%.2f", transaction.getAmount());
        holder.amountTextView.setText(formattedAmount);


        switch (transaction.getStatus()) {
            case "paid":
                holder.payButton.setText("Paid");
                holder.payButton.setBackgroundColor(context.getResources().getColor(R.color.light_gray));
                holder.payButton.setEnabled(false);
                break;
            case "unpaid":
                holder.payButton.setText("Pay");
                break;
            case "requested":
                holder.payButton.setText("Requested");
                holder.payButton.setBackgroundColor(context.getResources().getColor(R.color.light_gray));
                holder.payButton.setEnabled(false);
                break;
            default:
                holder.payButton.setText("Request");
                break;
        }

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        holder.payButton.setOnClickListener(v -> {
            if (transaction.getType().equals("pay")) {
                assert currentUser != null;
                db.collection("wallets")
                        .whereEqualTo("userId", currentUser.getUid())
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                                double currentBalance = document.getDouble("balance");
                                double newBalance = currentBalance - transaction.getAmount();

                                // Update the balance in the database
                                db.collection("wallets").document(document.getId())
                                        .update("balance", newBalance)
                                        .addOnSuccessListener(aVoid -> {
                                            // Balance updated successfully, proceed with the payment
                                            Intent intent = new Intent(context, PaymentReceiptActivity.class);
                                            intent.putExtra("transaction", transaction);
                                            intent.putExtra("transactionId", transaction.getDocumentId());
                                            context.startActivity(intent);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(context, "Failed to update balance", Toast.LENGTH_SHORT).show();
                                            Log.e("TransactionAdapter", "Failed to update balance: ", e);
                                        });
                            } else {
                                Toast.makeText(context, "Wallet not found", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle the error
                            Toast.makeText(context, "Failed to retrieve wallet", Toast.LENGTH_SHORT).show();
                        });
            } else if (transaction.getType().equals("request")) {
                // Retrieve the request transaction details
                Date currentDate = new Date();

                // Create new payment transaction
                Transaction newPaymentTransaction = new Transaction(
                        null,
                        "pay",
                        transaction.getUsername(),
                        transaction.getUserEmail(),
                        currentUser.getDisplayName(),
                        currentUser.getEmail(),
                        transaction.getAmount(),
                        currentDate,
                        transaction.getNote(),
                        "unpaid"
                );

                // Add payment transaction to Firestore
                db.collection("transactions").add(newPaymentTransaction).addOnSuccessListener(documentReference -> {
                    String paymentTransactionId = documentReference.getId();
                    newPaymentTransaction.setDocumentId(paymentTransactionId);

                    // Update the status of the request transaction to "requested"
                    db.collection("transactions").document(transaction.getDocumentId())
                            .update("status", "requested")
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Payment created and request status updated successfully", Toast.LENGTH_SHORT).show();
                                fetchTransactions();

                                RequestSuccessBottomFragment successFragment = new RequestSuccessBottomFragment();
                                successFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), successFragment.getTag());
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to update request status", Toast.LENGTH_SHORT).show();
                                Log.e("RequestBottomSheetFragment", "Error updating request status", e);
                            });
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Payment creation failed", Toast.LENGTH_SHORT).show();
                    Log.e("RequestBottomSheetFragment", "Error creating payment", e);
                });
            }
        });

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(transaction));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, recipientTextView, actionTextView, amountTextView;
        Button payButton;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.time);
            recipientTextView = itemView.findViewById(R.id.recipientTextView);
            actionTextView = itemView.findViewById(R.id.actionTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            payButton = itemView.findViewById(R.id.payButton);
        }
    }

    private RequestBottomSheetFragment.OnTransactionsUpdatedListener transactionsUpdatedListener;
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

    public void setTransactionsUpdatedListener(RequestBottomSheetFragment.OnTransactionsUpdatedListener listener) {
        this.transactionsUpdatedListener = listener;
    }
}