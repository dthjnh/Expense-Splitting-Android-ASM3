package com.example.expensesplitting.User.Transaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.expensesplitting.Model.User;
import com.example.expensesplitting.R;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {
    private final LayoutInflater inflater;

    public UserAdapter(@NonNull Context context, @NonNull List<User> users) {
        super(context, 0, users);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @SuppressLint("SetTextI18n")
    private View createView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }

        User user = getItem(position);
        if (user != null) {
            TextView recipientName = convertView.findViewById(R.id.recipient_name);
            TextView recipientEmail = convertView.findViewById(R.id.recipient_email);

            recipientName.setText(user.getFirstName() + " " + user.getLastName());
            recipientEmail.setText(user.getEmailAddress());
        }

        return convertView;
    }
}
