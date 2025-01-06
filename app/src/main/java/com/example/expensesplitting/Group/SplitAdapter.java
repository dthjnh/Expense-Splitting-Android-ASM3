package com.example.expensesplitting.Group;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.R;

import java.util.List;

public class SplitAdapter extends RecyclerView.Adapter<SplitAdapter.SplitViewHolder> {

    private List<Participant> participants;
    private double totalAmount;

    public SplitAdapter(List<Participant> participants, double totalAmount) {
        this.participants = participants;
        this.totalAmount = totalAmount;
    }

    public enum SplitType {
        EQUAL,
        UNEQUAL,
        PERCENTAGE
    }

    @NonNull
    @Override
    public SplitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_split_participant, parent, false);
        return new SplitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SplitViewHolder holder, int position) {
        Participant participant = participants.get(position);
        holder.name.setText(participant.getName());
        holder.input.setText(String.valueOf(participant.getAmount()));

        holder.input.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    participant.setAmount(Double.parseDouble(s.toString()));
                } catch (NumberFormatException e) {
                    participant.setAmount(0); // Reset on invalid input
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    public void applySplit(SplitType splitType) {
        switch (splitType) {
            case EQUAL:
                double equalShare = totalAmount / participants.size();
                for (Participant participant : participants) {
                    participant.setAmount(equalShare);
                }
                notifyDataSetChanged();
                break;

            case UNEQUAL:
                // Logic for unequal split can be custom-entered via the input fields.
                // No pre-defined calculation here.
                break;

            case PERCENTAGE:
                double percentageTotal = 0;
                for (Participant participant : participants) {
                    percentageTotal += participant.getAmount();
                }
                if (percentageTotal != 100) {
                    // Validation error for percentages.
                    throw new IllegalArgumentException("Percentages must sum to 100%");
                }
                for (Participant participant : participants) {
                    participant.setAmount(totalAmount * (participant.getAmount() / 100));
                }
                notifyDataSetChanged();
                break;

            default:
                throw new UnsupportedOperationException("Unsupported Split Type");
        }
    }

    public boolean validateSplit() {
        double sum = 0;
        for (Participant participant : participants) {
            sum += participant.getAmount();
        }
        return sum == totalAmount;
    }

    public static class SplitViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        EditText input;

        public SplitViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.participantName);
            input = itemView.findViewById(R.id.participantInput);
        }
    }
}
