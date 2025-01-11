package com.example.expensesplitting.Model;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction implements Serializable {
    private String documentId;
    private String type;
    private String username;
    private String userEmail;
    private String recipient;
    private String recipientEmail;
    private double amount;
    private Date timestamp;
    private String note;
    private String status;
    private String group;

    public Transaction(String documentId, String type, String username, String userEmail ,String recipient, String recipientEmail, double amount, Date timestamp, String note, String status) {
        this.documentId = documentId;
        this.type = type;
        this.username = username;
        this.userEmail = userEmail;
        this.recipient = recipient;
        this.recipientEmail = recipientEmail;
        this.amount = (amount);
        this.timestamp = timestamp;
        this.note = note;
        this.status = status;
    }

    public Transaction() {
    }

    private String formatAmount(double amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormat.format(amount);
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
