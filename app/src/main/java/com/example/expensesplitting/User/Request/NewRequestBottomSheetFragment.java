package com.example.expensesplitting.User.Request;

import android.annotation.SuppressLint;
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
import com.example.expensesplitting.User.Pay.NewPaymentBottomSheetFragment;
import com.example.expensesplitting.User.Pay.UserAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewRequestBottomSheetFragment extends BottomSheetDialogFragment {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final List<User> userList = new ArrayList<>();
    private FirebaseUser currentUser;
    private OnNewRequestListener requestAddedListener;
    private static final String ARG_BALANCE = "balance";
    private String balanceAmount;

    public static NewRequestBottomSheetFragment newInstance(String balance) {
        NewRequestBottomSheetFragment fragment = new NewRequestBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BALANCE, balance);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnNewRequestListener {
        void onNewRequest(Transaction transaction, String transactionId);
    }

    public void setRequestAddedListener(OnNewRequestListener listener) {
        this.requestAddedListener = listener;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_request_bottom_sheet, container, false);

        EditText amountText = view.findViewById(R.id.amount_text);
        Spinner recipientSpinner = view.findViewById(R.id.recipient_spinner);
        TextView noteText = view.findViewById(R.id.notes);

        TextView availableBalanceText = view.findViewById(R.id.available_balance);
        balanceAmount = getArguments().getString(ARG_BALANCE);
        availableBalanceText.setText("Your available balance: " + balanceAmount);

        Button cancelButton = view.findViewById(R.id.continue_button);
        cancelButton.setOnClickListener(v -> dismiss());

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Button newRequestButton = view.findViewById(R.id.new_request_button);
        newRequestButton.setOnClickListener(v -> {
            User selectedRecipient = (User) recipientSpinner.getSelectedItem();
            double amount = Double.parseDouble(amountText.getText().toString());
            String note = noteText.getText().toString();
            Date currentDate = new Date();

            // Create new request transaction
            Transaction newRequestTransaction = new Transaction(
                    null,
                    "request",
                    selectedRecipient.getFirstName() + " " + selectedRecipient.getLastName(),
                    selectedRecipient.getEmailAddress(),
                    currentUser.getDisplayName(),
                    currentUser.getEmail(),
                    amount,
                    currentDate,
                    note,
                    "request"
            );

            // Add request transaction to Firestore
            db.collection("transactions").add(newRequestTransaction).addOnSuccessListener(documentReference -> {
                String requestTransactionId = documentReference.getId();
                newRequestTransaction.setDocumentId(requestTransactionId);

                Toast.makeText(getContext(), "Request created successfully", Toast.LENGTH_SHORT).show();
                if (requestAddedListener != null) {
                    requestAddedListener.onNewRequest(newRequestTransaction, requestTransactionId);
                }
                dismiss();

            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Request creation failed", Toast.LENGTH_SHORT).show();
                Log.e("NewRequestBottomSheetFragment", "Error creating request", e);
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