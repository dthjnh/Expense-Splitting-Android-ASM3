package com.example.expensesplitting.Group;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.R;

import java.util.List;

public class SplitAdapter extends RecyclerView.Adapter<SplitAdapter.SplitViewHolder> {

    public interface OnAmountChangedListener {
        void onAmountChanged();
    }

    private List<Participant> participants;
    private double totalAmount;
    private SplitType splitType;
    private OnAmountChangedListener amountChangedListener;

    public enum SplitType {
        EQUAL,
        UNEQUAL
    }

    public SplitAdapter(List<Participant> participants, double totalAmount, SplitType splitType, OnAmountChangedListener amountChangedListener) {
        this.participants = participants;
        this.totalAmount = totalAmount;
        this.splitType = splitType;
        this.amountChangedListener = amountChangedListener;
    }

    public void setSplitType(SplitType splitType) {
        this.splitType = splitType;
        notifyDataSetChanged();
    }

    public SplitType getSplitType() {
        return splitType;
    }

    @NonNull
    @Override
    public SplitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false);
        return new SplitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SplitViewHolder holder, int position) {
        Participant participant = participants.get(position);

        holder.name.setText(participant.getName());

        if (splitType == SplitType.EQUAL) {
            if (holder.amount != null && holder.checkBox != null) {
                holder.amount.setText(String.format("$%.2f", totalAmount / participants.size()));
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(participant.getAmount() > 0);

                holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        participant.setAmount(totalAmount / participants.size());
                    } else {
                        participant.setAmount(0);
                    }
                    amountChangedListener.onAmountChanged();
                });
            }
            if (holder.input != null) {
                holder.input.setVisibility(View.GONE);
            }
        } else { // UNEQUAL
            if (holder.input != null) {
                holder.input.setVisibility(View.VISIBLE);
                holder.input.setText(String.format("%.2f", participant.getAmount()));

                holder.input.setOnFocusChangeListener((v, hasFocus) -> {
                    if (!hasFocus) {
                        try {
                            double value = Double.parseDouble(holder.input.getText().toString());
                            participant.setAmount(value);
                            amountChangedListener.onAmountChanged();
                        } catch (NumberFormatException ignored) {
                            participant.setAmount(0);
                        }
                    }
                });
            }
            if (holder.amount != null && holder.checkBox != null) {
                holder.amount.setVisibility(View.GONE);
                holder.checkBox.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return splitType == SplitType.EQUAL ? R.layout.item_split_equal : R.layout.item_split_unequal;
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    public static class SplitViewHolder extends RecyclerView.ViewHolder {
        TextView name, amount;
        EditText input;
        CheckBox checkBox;

        public SplitViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.participantName);
            amount = itemView.findViewById(R.id.participantAmount);
            input = itemView.findViewById(R.id.participantInput);
            checkBox = itemView.findViewById(R.id.participantCheckBox);
        }
    }
}
