package com.example.expensesplitting.Group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.expensesplitting.R;

import java.util.ArrayList;

public class GroupAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<Group> groupList;

    public GroupAdapter(Context context, ArrayList<Group> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @Override
    public int getCount() {
        return groupList.size();
    }

    @Override
    public Object getItem(int position) {
        return groupList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return groupList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            // Inflate the item layout for a single group item
            convertView = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);

            // Initialize the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.groupName = convertView.findViewById(R.id.groupName);
            viewHolder.groupDescription = convertView.findViewById(R.id.groupDescription);
            viewHolder.groupImage = convertView.findViewById(R.id.groupImage);

            convertView.setTag(viewHolder);
        } else {
            // Reuse the ViewHolder
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the current group
        Group group = groupList.get(position);

        // Populate the views with data
        viewHolder.groupName.setText(group.getName());
        viewHolder.groupDescription.setText(group.getDescription());

        // Placeholder logic for the group image
        if (group.getImage() != null && !group.getImage().isEmpty()) {
            // Use a library like Glide or Picasso for dynamic image loading
            // Glide.with(context).load(group.getImage()).into(viewHolder.groupImage);
            viewHolder.groupImage.setImageResource(R.drawable.ic_placeholder); // Placeholder image
        } else {
            viewHolder.groupImage.setImageResource(R.drawable.ic_placeholder); // Default placeholder
        }

        return convertView;
    }

    // Static class to hold the views
    private static class ViewHolder {
        TextView groupName;
        TextView groupDescription;
        ImageView groupImage;
    }
}
