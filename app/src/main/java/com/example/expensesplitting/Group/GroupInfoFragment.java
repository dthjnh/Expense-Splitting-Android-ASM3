package com.example.expensesplitting.Group;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Database.GroupHelper;
import com.example.expensesplitting.R;

import java.util.ArrayList;
import java.util.List;

public class GroupInfoFragment extends Fragment {

    private static final String ARG_GROUP_ID = "GROUP_ID";

    private long groupId;
    private GroupHelper groupHelper;
    private List<String> participants;

    private TextView groupTitle, groupDescription, groupCurrency, groupCategory;
    private RecyclerView participantsRecyclerView;
    private ParticipantsAdapter participantsAdapter;

    public static GroupInfoFragment newInstance(long groupId) {
        GroupInfoFragment fragment = new GroupInfoFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_info, container, false);

        groupId = getArguments().getLong(ARG_GROUP_ID);

        groupHelper = new GroupHelper(requireContext());

        groupTitle = view.findViewById(R.id.groupTitle);
        groupDescription = view.findViewById(R.id.groupDescription);
        groupCurrency = view.findViewById(R.id.groupCurrency);
        groupCategory = view.findViewById(R.id.groupCategory);
        participantsRecyclerView = view.findViewById(R.id.participantsList);
        ImageView addParticipantButton = view.findViewById(R.id.addParticipantButton);

        loadGroupDetails();

        participantsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        participants = loadParticipants();
        participantsAdapter = new ParticipantsAdapter(participants);
        participantsRecyclerView.setAdapter(participantsAdapter);

        addParticipantButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SelectParticipantsActivity.class);
            intent.putExtra("GROUP_ID", groupId);
            startActivity(intent);
        });

        return view;
    }

    private void loadGroupDetails() {
        Cursor cursor = groupHelper.getGroupById(groupId);
        if (cursor != null && cursor.moveToFirst()) {
            groupTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow(GroupHelper.COLUMN_NAME)));
            groupDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(GroupHelper.COLUMN_DESCRIPTION)));
            groupCurrency.setText(cursor.getString(cursor.getColumnIndexOrThrow(GroupHelper.COLUMN_CURRENCY)));
            groupCategory.setText(cursor.getString(cursor.getColumnIndexOrThrow(GroupHelper.COLUMN_CATEGORY)));
            cursor.close();
        }
    }

    private List<String> loadParticipants() {
        List<String> participantList = new ArrayList<>();
        Cursor cursor = groupHelper.getParticipantsForGroup(groupId);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                participantList.add(cursor.getString(cursor.getColumnIndexOrThrow(GroupHelper.COLUMN_PARTICIPANT_NAME)));
            }
            cursor.close();
        }
        return participantList;
    }
}
