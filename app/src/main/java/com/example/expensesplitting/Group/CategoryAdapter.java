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

    private final Context context;
    private final ArrayList<String> categories;
    private final CategorySelectionListener listener;

    public interface CategorySelectionListener {
        void onCategorySelected(String selectedCategory);
    }

    public CategoryAdapter(Context context, ArrayList<String> categories, CategorySelectionListener listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
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

        String category = categories.get(position);
        TextView categoryName = convertView.findViewById(R.id.categoryName);
        ImageView categoryIcon = convertView.findViewById(R.id.categoryIcon);

        categoryName.setText(category);

        // Dynamically set icon based on category name
        switch (category.toLowerCase()) {
            case "games":
                categoryIcon.setImageResource(R.drawable.game);
                break;
            case "movies":
                categoryIcon.setImageResource(R.drawable.movie);
                break;
            case "music":
                categoryIcon.setImageResource(R.drawable.music);
                break;
            case "sports":
                categoryIcon.setImageResource(R.drawable.sport);
                break;
            case "groceries":
                categoryIcon.setImageResource(R.drawable.groceries);
                break;
            case "dining out":
                categoryIcon.setImageResource(R.drawable.dining);
                break;
            case "liquor":
                categoryIcon.setImageResource(R.drawable.liquor);
                break;
            case "ticket":
                categoryIcon.setImageResource(R.drawable.airline);
                break;
            case "car":
                categoryIcon.setImageResource(R.drawable.transport);
                break;
            case "shopping":
                categoryIcon.setImageResource(R.drawable.shopping);
                break;
            case "hotel":
                categoryIcon.setImageResource(R.drawable.hotel);
                break;
            case "other":
                categoryIcon.setImageResource(R.drawable.category);
                break;

        }

        // Handle click events
        convertView.setOnClickListener(v -> listener.onCategorySelected(category));

        return convertView;
    }
}
