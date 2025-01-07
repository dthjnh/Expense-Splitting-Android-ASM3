package com.example.expensesplitting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensesplitting.ChatBox.ChatAdapter;
import com.example.expensesplitting.ChatBox.Message;
import com.example.expensesplitting.Login.SignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminReplyChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reply_chat);

        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        db = FirebaseFirestore.getInstance();

        fetchAllUserMessages();
    }

    private void fetchAllUserMessages() {
        CollectionReference chatsRef = db.collection("chats");

        chatsRef.get().addOnSuccessListener(querySnapshot -> {
            messageList.clear();

            for (QueryDocumentSnapshot chatDocument : querySnapshot) {
                String chatId = chatDocument.getId();

                chatsRef.document(chatId).collection("messages")
                        .orderBy("timestamp") // Order messages by timestamp
                        .get()
                        .addOnSuccessListener(messagesSnapshot -> {
                            for (QueryDocumentSnapshot messageDoc : messagesSnapshot) {
                                Message message = messageDoc.toObject(Message.class);

                                if (message != null) {
                                    messageList.add(message);
                                }
                            }
                            chatAdapter.notifyDataSetChanged();
                            chatRecyclerView.scrollToPosition(messageList.size() - 1);
                        })
                        .addOnFailureListener(e -> Log.e("AdminChat", "Failed to fetch messages: " + e.getMessage()));
            }
        }).addOnFailureListener(e -> Log.e("AdminChat", "Failed to fetch chats: " + e.getMessage()));
    }

    public void logOutAdmin(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(AdminReplyChatActivity.this, SignIn.class));
        finish();
    }
}