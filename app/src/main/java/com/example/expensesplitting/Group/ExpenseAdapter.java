package com.example.expensesplitting.Group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.R;

import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private final Context context;
    private final ArrayList<Expense> expenseList;

    public ExpenseAdapter(Context context, ArrayList<Expense> expenseList) {
        this.context = context;
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);

        holder.title.setText(expense.getTitle());
        holder.amount.setText(String.format("$%.2f", expense.getAmount()));
        holder.paidBy.setText("Paid by: " + expense.getPaidBy());
        holder.date.setText("Today"); // Replace with actual date logic

        // Dynamically set icon based on category name
        switch (expense.getCategory().toLowerCase()) {
            case "games":
                holder.categoryIcon.setImageResource(R.drawable.game);
                break;
            case "movies":
                holder.categoryIcon.setImageResource(R.drawable.movie);
                break;
            case "music":
                holder.categoryIcon.setImageResource(R.drawable.music);
                break;
            case "sports":
                holder.categoryIcon.setImageResource(R.drawable.sport);
                break;
            case "groceries":
                holder.categoryIcon.setImageResource(R.drawable.groceries);
                break;
            case "dining out":
                holder.categoryIcon.setImageResource(R.drawable.dining);
                break;
            case "liquor":
                holder.categoryIcon.setImageResource(R.drawable.liquor);
                break;
            case "ticket":
                holder.categoryIcon.setImageResource(R.drawable.airline);
                break;
            case "car":
                holder.categoryIcon.setImageResource(R.drawable.transport);
                break;
            case "shopping":
                holder.categoryIcon.setImageResource(R.drawable.shopping);
                break;
            case "hotel":
                holder.categoryIcon.setImageResource(R.drawable.hotel);
                break;
            case "other":
            default:
                holder.categoryIcon.setImageResource(R.drawable.category);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount, paidBy, date;
        ImageView categoryIcon;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.expenseTitle);
            amount = itemView.findViewById(R.id.expenseAmount);
            paidBy = itemView.findViewById(R.id.expensePaidBy);
            date = itemView.findViewById(R.id.expenseDate);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
        }
    }
}
