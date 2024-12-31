package com.example.expensesplitting;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Database.ContactDatabaseHelper;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contactList;
    private ContactDatabaseHelper databaseHelper;

    public ContactAdapter(List<Contact> contactList, ContactDatabaseHelper databaseHelper) {
        this.contactList = contactList;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);

        holder.name.setText(contact.getName());
        holder.email.setText(contact.getEmail());
        holder.avatar.setImageResource(contact.getAvatar());

        // Update favorite icon
        if (contact.isFavorite()) {
            holder.favorite.setImageResource(R.drawable.ic_star_filled); // Filled star for favorite
        } else {
            holder.favorite.setImageResource(R.drawable.ic_star_outline); // Outline for non-favorite
        }

        // Toggle favorite status on click
        holder.favorite.setOnClickListener(v -> {
            // Toggle favorite status in the database and update UI
            contact.setFavorite(!contact.isFavorite());
            databaseHelper.updateContactFavoriteStatus(contact);
            notifyItemChanged(position);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ContactDetailActivity.class);
            intent.putExtra("CONTACT_ID", contact.getId());
            v.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void updateList(List<Contact> updatedList) {
        this.contactList = updatedList;
        notifyDataSetChanged();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView name, email;
        ImageView avatar, favorite;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contactName);
            email = itemView.findViewById(R.id.contactEmail);
            avatar = itemView.findViewById(R.id.contactAvatar);
            favorite = itemView.findViewById(R.id.contactFavorite);
        }
    }
}
