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

public class CategoryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> categories;
    private OnCategoryClickListener categoryClickListener;

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    public CategoryAdapter(Context context, ArrayList<String> categories, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.categoryClickListener = listener;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        }

        TextView categoryName = convertView.findViewById(R.id.categoryName);
        ImageView categoryIcon = convertView.findViewById(R.id.categoryIcon);

        String category = categories.get(position);
        categoryName.setText(category);

        // Optionally set icons or colors based on category
        if (category.equals("Groceries")) {
            categoryIcon.setImageResource(R.drawable.ic_groceries); // Replace with actual drawable
        } else if (category.equals("Sports")) {
            categoryIcon.setImageResource(R.drawable.ic_sports); // Replace with actual drawable
        }

        convertView.setOnClickListener(v -> categoryClickListener.onCategoryClick(category));

        return convertView;
    }
}
