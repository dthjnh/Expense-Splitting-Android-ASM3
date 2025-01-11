package com.example.expensesplitting.User.TopUp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Model.PaymentMethod;
import com.example.expensesplitting.Model.Wallet;
import com.example.expensesplitting.R;
import com.example.expensesplitting.User.PaymentMethod.NewPaymentActivity;
import com.example.expensesplitting.User.PaymentMethod.PaymentMethodAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SelectPaymentMethodActivity extends AppCompatActivity {
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<PaymentMethod> paymentMethods;
    private PaymentMethodAdapter paymentMethodAdapter;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup_select_payment_method);

        auth = FirebaseAuth.getInstance();
        paymentMethods = new ArrayList<>();
        paymentMethodAdapter = new PaymentMethodAdapter(paymentMethods, paymentMethod -> {
            // Handle payment method selection
        });

        RecyclerView paymentMethodRecyclerView = findViewById(R.id.recycler_view);
        paymentMethodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        paymentMethodRecyclerView.setAdapter(paymentMethodAdapter);

        fetchPaymentMethods();

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        ImageView addPaymentMethodButton = findViewById(R.id.add_payment_method_button);
        addPaymentMethodButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, NewPaymentActivity.class);
            intent.putExtra("previousActivity", "SelectPaymentMethodActivity");
            startActivityForResult(intent, 1);
        });

        String topUpAmount = getIntent().getStringExtra("topUpAmount");

        Button continueButton = findViewById(R.id.topup_cancel_button);
        continueButton.setOnClickListener(v -> {
            if (paymentMethodAdapter.getSelectedPosition() == RecyclerView.NO_POSITION) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, TopUpSummaryActivity.class);
                intent.putExtra("paymentMethod", paymentMethods.get(paymentMethodAdapter.getSelectedPosition()));
                intent.putExtra("topUpAmount", topUpAmount);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchPaymentMethods() {
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        db.collection("wallets")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (!task.getResult().isEmpty()) {
                            paymentMethods.clear();
                            paymentMethods.addAll(Objects.requireNonNull(task.getResult().getDocuments().get(0).toObject(Wallet.class)).getPaymentMethods());
                            paymentMethodAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("SelectPaymentMethod", "No payment methods found");
                            Toast.makeText(this, "No payment methods found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("SelectPaymentMethod", "Error fetching payment methods", task.getException());
                        Toast.makeText(this, "Error fetching payment methods", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            fetchPaymentMethods();
        }
    }
}