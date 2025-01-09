package com.example.expensesplitting.Group;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.R;

import java.util.List;

public class BalancesAdapter extends RecyclerView.Adapter<BalancesAdapter.BalanceViewHolder> {

    private final List<Balance> balances;

    public BalancesAdapter(List<Balance> balances) {
        this.balances = balances;
    }

    @NonNull
    @Override
    public BalanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_balance, parent, false);
        return new BalanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceViewHolder holder, int position) {
        Balance balance = balances.get(position);
        holder.balanceNameOwes.setText(String.format("%s owes", balance.getOwer()));
        holder.balanceAmount.setText(String.format("$%.2f", balance.getAmount()));
        holder.balanceNamePaidTo.setText(String.format("Paid to %s", balance.getPayee()));
    }

    @Override
    public int getItemCount() {
        return balances.size();
    }

    static class BalanceViewHolder extends RecyclerView.ViewHolder {
        TextView balanceNameOwes, balanceAmount, balanceNamePaidTo;

        public BalanceViewHolder(@NonNull View itemView) {
            super(itemView);
            balanceNameOwes = itemView.findViewById(R.id.balanceNameOwes);
            balanceAmount = itemView.findViewById(R.id.balanceAmount);
            balanceNamePaidTo = itemView.findViewById(R.id.balanceNamePaidTo);
        }
    }
}
