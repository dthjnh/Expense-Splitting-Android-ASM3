package com.example.expensesplitting.User.PaymentMethod;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Model.PaymentMethod;
import com.example.expensesplitting.R;

import java.util.List;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.PaymentMethodViewHolder> {
    private final List<PaymentMethod> paymentMethods;
    private final OnItemClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnItemClickListener {
        void onItemClick(PaymentMethod paymentMethod);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public PaymentMethodAdapter(List<PaymentMethod> paymentMethods, OnItemClickListener listener) {
        this.paymentMethods = paymentMethods;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PaymentMethodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_method, parent, false);
        return new PaymentMethodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentMethodViewHolder holder, int position) {
        PaymentMethod paymentMethod = paymentMethods.get(position);
        holder.bind(paymentMethod, listener);

        if (position == selectedPosition) {
            holder.connectedText.setVisibility(View.GONE);
            holder.tickMark.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundResource(R.drawable.selected_item_border);
        } else {
            holder.connectedText.setVisibility(View.VISIBLE);
            holder.tickMark.setVisibility(View.GONE);
            holder.itemView.setBackgroundResource(R.drawable.linear_layout_border);
        }

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
            listener.onItemClick(paymentMethod);
        });
    }

    @Override
    public int getItemCount() {
        return paymentMethods.size();
    }

    static class PaymentMethodViewHolder extends RecyclerView.ViewHolder {
        TextView cardNumber;
        TextView connectedText;
        ImageView tickMark;

        public PaymentMethodViewHolder(@NonNull View itemView) {
            super(itemView);
            cardNumber = itemView.findViewById(R.id.item_card_number);
            connectedText = itemView.findViewById(R.id.connected_text);
            tickMark = itemView.findViewById(R.id.tick_mark);
        }

        public void bind(final PaymentMethod paymentMethod, final OnItemClickListener listener) {
            String maskedCardNumber = "•••• •••• •••• " + paymentMethod.getCardNumber().substring(paymentMethod.getCardNumber().length() - 4);
            cardNumber.setText(maskedCardNumber);
            itemView.setOnClickListener(v -> listener.onItemClick(paymentMethod));
        }
    }
}