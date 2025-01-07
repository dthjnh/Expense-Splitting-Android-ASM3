package com.example.expensesplitting.ChatBox;

public class Message {
    private String sender; // Sender type: "user" or "admin"
    private String message; // Message content
    private long timestamp; // Timestamp of the message
    private String senderName; // Name of the user

    public Message() {
        // Required empty constructor for Firestore
    }

    public Message(String sender, String message, long timestamp, String senderName) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
        this.senderName = senderName;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSenderName() {
        return senderName;
    }
}