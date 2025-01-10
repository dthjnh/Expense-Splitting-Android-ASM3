package com.example.expensesplitting.Group;

import android.text.Editable;
import android.text.TextWatcher;
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

    private final List<Participant> participants;
    private final double totalAmount;
    private SplitType splitType;
    private final OnAmountChangedListener amountChangedListener;

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
            setupEqualSplit(holder, participant);
        } else { // UNEQUAL
            setupUnequalSplit(holder, participant);
        }
    }

    private void setupEqualSplit(SplitViewHolder holder, Participant participant) {
        if (holder.amount != null && holder.checkBox != null) {
            double equalAmount = totalAmount / participants.size();
            holder.amount.setText(String.format("$%.2f", equalAmount));
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(participant.getAmount() > 0);

            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                participant.setAmount(isChecked ? equalAmount : 0);
                amountChangedListener.onAmountChanged();
            });
        }

        if (holder.input != null) {
            holder.input.setVisibility(View.GONE);
        }
    }

    private void setupUnequalSplit(SplitViewHolder holder, Participant participant) {
        if (holder.input != null) {
            holder.input.setVisibility(View.VISIBLE);
            holder.input.setText(String.format("%.2f", participant.getAmount()));

            holder.input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        double value = Double.parseDouble(s.toString());
                        participant.setAmount(value);
                        amountChangedListener.onAmountChanged();
                    } catch (NumberFormatException ignored) {
                        participant.setAmount(0); // Default to 0 if invalid input
                    }
                }
            });
        }

        if (holder.amount != null && holder.checkBox != null) {
            holder.amount.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.GONE);
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