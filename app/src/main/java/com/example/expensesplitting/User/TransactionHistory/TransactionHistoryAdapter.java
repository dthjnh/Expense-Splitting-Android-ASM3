package com.example.expensesplitting.User.TransactionHistory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Model.Transaction;
import com.example.expensesplitting.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.ItemViewHolder> {
    private final List<Transaction> transactions;
    private String transactionId;

    public TransactionHistoryAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_history, parent, false);
        return new ItemViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.userName.setText(transaction.getRecipient());
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
            Date date = inputFormat.parse(transaction.getTimestamp().toString());
            String formattedTime = outputFormat.format(date);
            holder.time.setText(formattedTime);
        } catch (Exception e) {
            holder.time.setText("Invalid time");
        }
        switch (transaction.getType()) {
            case "topup":
                holder.amount.setText(String.format("$%.2f", transaction.getAmount()));
                holder.imageView.setImageResource(R.drawable.transaction_topup);
                holder.transactionType.setText("Top Up");
                holder.userName.setText("Top Up");
                break;
            case "pay":
                holder.amount.setText(String.format("- $%.2f", transaction.getAmount()));
                holder.transactionType.setText("Pay");
                break;
            case "withdraw":
                holder.amount.setText(String.format("- $%.2f", transaction.getAmount()));
                holder.imageView.setImageResource(R.drawable.transaction_withdraw);
                holder.transactionType.setText("Withdraw");
                holder.userName.setText("Withdraw");
                break;
            case "request":
                holder.transactionType.setText("Request");
                holder.amount.setText(String.format("$%.2f", transaction.getAmount()));
                break;
            default:
                holder.transactionType.setText("Unknown");
                Log.e("TransactionHistoryAdapter", "Unknown transaction type: " + transaction.getType());
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), TransactionHistoryDetailActivity.class);
            intent.putExtra("transaction", transaction);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView userName, time, amount, transactionType;
        ImageView imageView;

        ItemViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name_display);
            time = itemView.findViewById(R.id.time);
            amount = itemView.findViewById(R.id.amount);
            transactionType = itemView.findViewById(R.id.transaction_type);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }


}