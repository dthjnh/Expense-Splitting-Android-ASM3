package com.example.expensesplitting.Group;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.groupImage = convertView.findViewById(R.id.groupImage);
            viewHolder.groupName = convertView.findViewById(R.id.groupName);
            viewHolder.groupDescription = convertView.findViewById(R.id.groupDescription);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Group group = groupList.get(position);

        Log.d("GroupAdapter", "Group Name: " + group.getName() + ", Image URI: " + group.getImage());

        viewHolder.groupName.setText(group.getName());
        viewHolder.groupDescription.setText(group.getDescription());

        if (group.getImage() != null && !group.getImage().isEmpty()) {
            Glide.with(context)
                    .load(Uri.parse(group.getImage())) // Load the image URI
                    .placeholder(R.drawable.ic_placeholder) // Default placeholder
                    .error(R.drawable.error) // Error image
                    .circleCrop() // Apply circular cropping
                    .into(viewHolder.groupImage);
        } else {
            viewHolder.groupImage.setImageResource(R.drawable.ic_placeholder); // Default placeholder
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView groupImage;
        TextView groupName;
        TextView groupDescription;
    }
}
