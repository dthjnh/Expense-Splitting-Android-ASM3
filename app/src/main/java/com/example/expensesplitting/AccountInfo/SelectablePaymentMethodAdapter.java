package com.example.expensesplitting.AccountInfo;

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

public class SelectablePaymentMethodAdapter extends RecyclerView.Adapter<SelectablePaymentMethodAdapter.ViewHolder> {

    private final List<PaymentMethod> paymentMethods;
    private final OnPaymentMethodSelectedListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnPaymentMethodSelectedListener {
        void onPaymentMethodSelected(PaymentMethod paymentMethod);
    }

    public SelectablePaymentMethodAdapter(List<PaymentMethod> paymentMethods, OnPaymentMethodSelectedListener listener) {
        this.paymentMethods = paymentMethods;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_method2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentMethod paymentMethod = paymentMethods.get(position);
        holder.bind(paymentMethod, position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return paymentMethods.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView cardNumber;
        private final ImageView paymentIcon;
        private final ImageView selectedIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            cardNumber = itemView.findViewById(R.id.card_number);
            paymentIcon = itemView.findViewById(R.id.imageView2);
            selectedIndicator = itemView.findViewById(R.id.selected_indicator);
        }

        void bind(PaymentMethod paymentMethod, boolean isSelected) {
            String formattedCardNumber = formatCardNumber(paymentMethod.getCardNumber());
            cardNumber.setText(formattedCardNumber);

            setPaymentIcon(paymentMethod.getCardNumber());

            selectedIndicator.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            itemView.setOnClickListener(v -> {
                int previousPosition = selectedPosition;
                selectedPosition = getAdapterPosition();
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
                listener.onPaymentMethodSelected(paymentMethod);
            });
        }

        private String formatCardNumber(String cardNumber) {
            if (cardNumber != null && cardNumber.length() >= 4) {
                return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
            } else {
                return "Invalid Card Number";
            }
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
}