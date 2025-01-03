package com.example.expensesplitting.User.Pay;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.Model.Transaction;
import com.example.expensesplitting.R;
import com.example.expensesplitting.User.Transaction.TransactionAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PayActivity extends AppCompatActivity implements TransactionAdapter.OnItemClickListener {
    RecyclerView recyclerView;
    TransactionAdapter adapter;
    List<Transaction> transactionList;
    ImageView addPayButton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        transactionList = new ArrayList<>();

        db.collection("transactions").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    String type = document.getString("type");
                    String username = document.getString("username");
                    String recipient = document.getString("recipient");
                    double amount = document.getDouble("amount");
                    String timestamp = document.getString("timestamp");
                    String note = document.getString("note");

                    Date date = null;
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    try {
                        date = dateFormat.parse(timestamp);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Transaction transaction = new Transaction(type, username, recipient, amount, date, note);
                    transactionList.add(transaction);
                }
                adapter.notifyDataSetChanged();
            } else {
                // Handle error
                Toast.makeText(this, "Error fetching transactions", Toast.LENGTH_SHORT).show();
                Log.e("PayActivity", "Error fetching transactions", task.getException());
                finish();
            }
        });

        adapter = new TransactionAdapter(this, transactionList, this);
        recyclerView.setAdapter(adapter);

        addPayButton = findViewById(R.id.add_pay_button);
        addPayButton.setOnClickListener(v -> {
            NewPaymentBottomSheetFragment bottomSheetFragment = new NewPaymentBottomSheetFragment();
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });
    }

    @Override
    public void onItemClick(Transaction transaction) {
        PaymentBottomSheetFragment bottomSheetFragment = PaymentBottomSheetFragment.newInstance(transaction);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }
}