package com.example.expensesplitting.Group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Database.GroupHelper;
import com.example.expensesplitting.R;

import java.util.List;

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ViewHolder> {

    private final List<String> participants;
    private final long groupId;
    private final GroupHelper groupHelper;
    private final GroupInfoFragment fragment;

    public ParticipantsAdapter(List<String> participants, long groupId, GroupHelper groupHelper, GroupInfoFragment fragment) {
        this.participants = participants;
        this.groupId = groupId;
        this.groupHelper = groupHelper;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String participantName = participants.get(position);
        holder.participantName.setText(participantName);

        holder.deleteButton.setOnClickListener(v -> {
            boolean success = groupHelper.deleteParticipant(groupId, participantName);
            if (success) {
                Toast.makeText(holder.itemView.getContext(), participantName + " removed from group.", Toast.LENGTH_SHORT).show();
                fragment.refreshParticipants(); // Refresh participants list
            } else {
                Toast.makeText(holder.itemView.getContext(), "Failed to remove " + participantName, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView participantName;
        ImageView deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            participantName = itemView.findViewById(R.id.participantName);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
