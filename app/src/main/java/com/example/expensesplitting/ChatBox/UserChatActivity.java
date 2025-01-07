package com.example.expensesplitting.ChatBox;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList = new ArrayList<>();
    private EditText messageInput;
    private Button sendButton;

    private FirebaseFirestore db;
    private String chatId;
    private String userId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);

        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(messageList);
        chatRecyclerView.setAdapter(chatAdapter);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fetchUserName();

        sendButton.setEnabled(false);
        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendButton.setEnabled(!TextUtils.isEmpty(s.toString().trim()));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
            }
        });
    }

    private void fetchUserName() {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("FirstName");
                        String lastName = documentSnapshot.getString("LastName");
                        userName = firstName + " " + lastName;
                    }
                    checkOrCreateChat();
                })
                .addOnFailureListener(e -> {
                    userName = "User"; // Fallback to default
                    checkOrCreateChat();
                });
    }

    private void checkOrCreateChat() {
        db.collection("chats")
                .whereEqualTo("users." + userId, true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        chatId = querySnapshot.getDocuments().get(0).getId();
                        loadMessages();
                    } else {
                        createNewChat();
                    }
                });
    }

    private void createNewChat() {
        Map<String, Object> chatData = new HashMap<>();
        Map<String, Boolean> users = new HashMap<>();
        users.put(userId, true);
        users.put("admin", true);

        chatData.put("users", users);
        chatData.put("last_message", "");
        chatData.put("last_message_time", System.currentTimeMillis());

        db.collection("chats")
                .add(chatData)
                .addOnSuccessListener(documentReference -> {
                    chatId = documentReference.getId();
                    loadMessages();
                });
    }

    private void loadMessages() {
        db.collection("chats").document(chatId).collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) return;

                    if (queryDocumentSnapshots != null) {
                        messageList.clear();
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Message message = snapshot.toObject(Message.class);
                            if (message != null) {
                                messageList.add(message);
                            }
                        }
                        chatAdapter.notifyDataSetChanged();
                        chatRecyclerView.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

    private void sendMessage(String text) {
        if (userName == null) userName = "User"; // Fallback in case name is not fetched

        Message userMessage = new Message(userId, text, System.currentTimeMillis(), userName);
        db.collection("chats").document(chatId).collection("messages")
                .add(userMessage)
                .addOnSuccessListener(documentReference -> {
                    db.collection("chats").document(chatId)
                            .update("last_message", text, "last_message_time", System.currentTimeMillis());
                    messageInput.setText("");

                    // Send automatic admin reply
                    sendAutoReply();
                });
    }

    private void sendAutoReply() {
        String adminReply = "Thank you for reaching out. Our team will review your issue and get back to you via email shortly.";
        String adminName = "Admin";

        Message adminMessage = new Message("admin", adminReply, System.currentTimeMillis(), adminName);
        db.collection("chats").document(chatId).collection("messages")
                .add(adminMessage)
                .addOnSuccessListener(documentReference -> {
                    db.collection("chats").document(chatId)
                            .update("last_message", adminReply, "last_message_time", System.currentTimeMillis());
                });
    }
}