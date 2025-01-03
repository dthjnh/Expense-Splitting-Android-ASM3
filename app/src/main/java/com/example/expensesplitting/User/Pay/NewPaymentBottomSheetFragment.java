package com.example.expensesplitting.User.Pay;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.expensesplitting.Model.Transaction;
import com.example.expensesplitting.Model.User;
import com.example.expensesplitting.R;
import com.example.expensesplitting.User.Transaction.UserAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewPaymentBottomSheetFragment extends BottomSheetDialogFragment {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<User> userList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_payment_bottom_sheet, container, false);

        EditText amountText = view.findViewById(R.id.amount_text);
        Spinner recipientSpinner = view.findViewById(R.id.recipient_spinner);
        TextView noteText = view.findViewById(R.id.notes);

        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> dismiss());

        Button newPaymentButton = view.findViewById(R.id.new_payment_button);
        newPaymentButton.setOnClickListener(v -> {
            User selectedRecipient = (User) recipientSpinner.getSelectedItem();
            Transaction newTransaction = new Transaction("pay", "current_user", selectedRecipient.getFirstName() + " " + selectedRecipient.getLastName(), Double.parseDouble(amountText.getText().toString()), new Date(), noteText.getText().toString());
            db.collection("transactions").add(newTransaction).addOnSuccessListener(documentReference -> {
                Toast.makeText(getContext(), "Payment created successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Payment creation failed", Toast.LENGTH_SHORT).show();
                Log.e("NewPaymentBottomSheetFragment", "Error creating payment", e);
            });
        });

        fetchUsers(recipientSpinner);

        return view;
    }

    private void fetchUsers(Spinner recipientSpinner) {
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String address = document.getString("address");
                    String confirmPassword = document.getString("confirmPassword");
                    String dateOfBirth = document.getString("dateOfBirth");
                    String emailAddress = document.getString("emailAddress");
                    String firstName = document.getString("firstName");
                    String lastName = document.getString("lastName");
                    String password = document.getString("password");
                    String phone = document.getString("phone");
                    userList.add(new User(address, confirmPassword, dateOfBirth, emailAddress, firstName, lastName, password, phone));
                }
                UserAdapter adapter = new UserAdapter(getContext(), userList);
                recipientSpinner.setAdapter(adapter);
            } else {
                Log.e("NewPaymentBottomSheetFragment", "Error fetching users", task.getException());
            }
        });
    }
}