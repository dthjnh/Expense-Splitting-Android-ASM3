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

public class SplitAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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

    @Override
    public int getItemViewType(int position) {
        if (splitType == SplitType.EQUAL) {
            return R.layout.item_split_equal;
        } else {
            return R.layout.item_split_unequal;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new SplitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Participant participant = participants.get(position);
        SplitViewHolder viewHolder = (SplitViewHolder) holder;

        viewHolder.name.setText(participant.getName());

        if (splitType == SplitType.EQUAL) {
            // Handle Equal Split
            if (viewHolder.checkBox != null && viewHolder.amount != null) {
                viewHolder.checkBox.setVisibility(View.VISIBLE);
                viewHolder.amount.setVisibility(View.VISIBLE);
                if (viewHolder.input != null) {
                    viewHolder.input.setVisibility(View.GONE);
                }

                viewHolder.checkBox.setChecked(participant.getAmount() > 0);
                viewHolder.amount.setText(String.format("$%.2f", participant.getAmount()));

                viewHolder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    int checkedCount = getCheckedCount();
                    if (isChecked) {
                        participant.setAmount(totalAmount / checkedCount);
                    } else {
                        participant.setAmount(0);
                    }
                    recalculateEqualAmounts();
                    notifyDataSetChanged();
                    amountChangedListener.onAmountChanged();
                });
            }
        } else if (splitType == SplitType.UNEQUAL) {
            // Handle Unequal Split
            if (viewHolder.input != null) {
                if (viewHolder.checkBox != null) {
                    viewHolder.checkBox.setVisibility(View.GONE);
                }
                if (viewHolder.amount != null) {
                    viewHolder.amount.setVisibility(View.GONE);
                }
                viewHolder.input.setVisibility(View.VISIBLE);

                viewHolder.input.setText(String.format("%.2f", participant.getAmount()));
                viewHolder.input.setOnFocusChangeListener((v, hasFocus) -> {
                    if (!hasFocus) {
                        try {
                            double enteredAmount = Double.parseDouble(viewHolder.input.getText().toString());
                            participant.setAmount(enteredAmount);
                        } catch (NumberFormatException e) {
                            participant.setAmount(0);
                        }
                        amountChangedListener.onAmountChanged();
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    private int getCheckedCount() {
        int count = 0;
        for (Participant participant : participants) {
            if (participant.getAmount() > 0) {
                count++;
            }
        }
        return count;
    }

    private void recalculateEqualAmounts() {
        int checkedCount = getCheckedCount();
        if (checkedCount > 0) {
            double newAmount = totalAmount / checkedCount;
            for (Participant participant : participants) {
                if (participant.getAmount() > 0) {
                    participant.setAmount(newAmount);
                }
            }
        }
    }

    public static class SplitViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView amount;
        EditText input;
        CheckBox checkBox;

        public SplitViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.participantName);
            try {
                checkBox = itemView.findViewById(R.id.participantCheckBox);
                amount = itemView.findViewById(R.id.participantAmount);
                input = itemView.findViewById(R.id.participantInput);
            } catch (Exception ignored) {
                // Ignore missing views based on layout type
            }
        }
    }
}
