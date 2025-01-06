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
    private final OnPaymentMethodClickListener listener;

    public interface OnPaymentMethodClickListener {
        void onDeletePaymentMethod(PaymentMethod paymentMethod);
    }

    public PaymentMethodAdapter(List<PaymentMethod> paymentMethods, OnPaymentMethodClickListener listener) {
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
        holder.cardNumber.setText(formatCardNumber(paymentMethod.getCardNumber()));
        holder.setPaymentIcon(paymentMethod.getCardNumber());

        holder.itemView.setOnLongClickListener(v -> {
            listener.onDeletePaymentMethod(paymentMethod);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return paymentMethods.size();
    }

    static class PaymentMethodViewHolder extends RecyclerView.ViewHolder {
        TextView cardNumber;
        ImageView paymentIcon;

        public PaymentMethodViewHolder(@NonNull View itemView) {
            super(itemView);
            cardNumber = itemView.findViewById(R.id.item_card_number);
            paymentIcon = itemView.findViewById(R.id.imageView2);
        }

        private void setPaymentIcon(String cardNumber) {
            if (cardNumber.startsWith("4")) {
                paymentIcon.setImageResource(R.drawable.ic_visa);
            } else if (cardNumber.startsWith("5")) {
                paymentIcon.setImageResource(R.drawable.ic_mastercard);
            } else {
                paymentIcon.setImageResource(R.drawable.ic_default_card);
            }
        }
    }

    private String formatCardNumber(String cardNumber) {
        if (cardNumber.length() > 4) {
            return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
        } else {
            return cardNumber;
        }
    }
}