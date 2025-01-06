package com.example.expensesplitting.Group;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Contacts.Contact;
import com.example.expensesplitting.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactAdapter2 extends RecyclerView.Adapter<ContactAdapter2.ContactViewHolder> {

    private List<Contact> contactList;
    private Set<Contact> selectedContacts;
    private OnContactSelectionListener contactSelectionListener;

    public interface OnContactSelectionListener {
        void onContactSelected(Contact contact);
        void onContactDeselected(Contact contact);
    }

    public ContactAdapter2(List<Contact> contactList, OnContactSelectionListener listener) {
        this.contactList = contactList;
        this.selectedContacts = new HashSet<>();
        this.contactSelectionListener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact2, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);

        holder.name.setText(contact.getName());
        holder.email.setText(contact.getEmail());
        holder.avatar.setImageResource(contact.getAvatar());

        // Update selection indicator
        holder.selectionIndicator.setVisibility(selectedContacts.contains(contact) ? View.VISIBLE : View.GONE);

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            if (selectedContacts.contains(contact)) {
                selectedContacts.remove(contact);
                contactSelectionListener.onContactDeselected(contact);
            } else {
                selectedContacts.add(contact);
                contactSelectionListener.onContactSelected(contact);
            }
            notifyItemChanged(position);
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
        ImageView avatar, selectionIndicator;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contactName);
            email = itemView.findViewById(R.id.contactEmail);
            avatar = itemView.findViewById(R.id.contactAvatar);
            selectionIndicator = itemView.findViewById(R.id.contactSelectedIndicator);
        }
    }
}
