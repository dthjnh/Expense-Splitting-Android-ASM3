package com.example.expensesplitting.User.Pay;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.expensesplitting.Contacts.Contact;
import com.example.expensesplitting.Database.ContactDatabaseHelper;
import com.example.expensesplitting.Model.Transaction;
import com.example.expensesplitting.Model.User;
import com.example.expensesplitting.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewPaymentBottomSheetFragment extends BottomSheetDialogFragment {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<User> userList = new ArrayList<>();
    private FirebaseUser currentUser;

    private OnNewPaymentListener paymentAddedListener;

    public interface OnNewPaymentListener {
        void onNewPayment(Transaction transaction, String transactionId);
    }

    public void setPaymentAddedListener(OnNewPaymentListener listener) {
        this.paymentAddedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_payment_bottom_sheet, container, false);

        EditText amountText = view.findViewById(R.id.amount_text);
        Spinner recipientSpinner = view.findViewById(R.id.recipient_spinner);
        TextView noteText = view.findViewById(R.id.notes);

        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> dismiss());

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Button newPaymentButton = view.findViewById(R.id.new_request_button);
        newPaymentButton.setOnClickListener(v -> {
            User selectedRecipient = (User) recipientSpinner.getSelectedItem();
            Transaction newTransaction = new Transaction(
                    null,
                    "pay",
                    currentUser.getDisplayName(),
                    currentUser.getEmail(),
                    selectedRecipient.getFirstName() + " " + selectedRecipient.getLastName(),
                    selectedRecipient.getEmailAddress(),
                    Double.parseDouble(amountText.getText().toString()),
                    new Date(),
                    noteText.getText().toString(),
                    "unpaid"
                    );

            db.collection("transactions").add(newTransaction).addOnSuccessListener(documentReference -> {
                String transactionId = documentReference.getId();
                newTransaction.setDocumentId(transactionId);
                Toast.makeText(getContext(), "Payment created successfully", Toast.LENGTH_SHORT).show();
                if (paymentAddedListener != null) {
                    paymentAddedListener.onNewPayment(newTransaction, transactionId);
                }
                dismiss();
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Payment creation failed", Toast.LENGTH_SHORT).show();
                Log.e("NewPaymentBottomSheetFragment", "Error creating payment", e);
            });
        });

        fetchUsersFromSQLite(recipientSpinner);

        return view;
    }

    private void fetchUsersFromSQLite(Spinner recipientSpinner) {
        ContactDatabaseHelper dbHelper = new ContactDatabaseHelper(getContext());
        List<Contact> contactList = dbHelper.getAllContacts();

        List<User> userList = new ArrayList<>();
        for (Contact contact : contactList) {
            userList.add(new User(contact.getName(), "", contact.getEmail()));
        }

        UserAdapter adapter = new UserAdapter(getContext(), userList);
        recipientSpinner.setAdapter(adapter);
    }
}