package com.example.expensesplitting.User.Transaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Model.Transaction;
import com.example.expensesplitting.R;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private Context context;
    private List<Transaction> transactionList;
    private OnItemClickListener onItemClickListener;

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
        holder.amountTextView.setText(transaction.getAmount());

        if(transaction.getType().equals("pay")) {
            holder.payButton.setText("Pay");
            holder.payButton.setVisibility(View.VISIBLE);
        }

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
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            recipientTextView = itemView.findViewById(R.id.recipientTextView);
            actionTextView = itemView.findViewById(R.id.actionTextView);
            amountTextView = itemView.findViewById(R.id.amountTextView);
            payButton = itemView.findViewById(R.id.payButton);
        }
    }
}