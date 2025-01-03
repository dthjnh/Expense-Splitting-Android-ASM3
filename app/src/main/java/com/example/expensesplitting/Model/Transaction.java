package com.example.expensesplitting.Model;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

public class Transaction implements Serializable {
    private String type;
    private String username;
    private String recipient;
    private String amount;
    private Date timestamp;
    private String note;

    public Transaction(String type, String username, String recipient, double amount, Date timestamp, String note) {
        this.type = type;
        this.username = username;
        this.recipient = recipient;
        this.amount = formatAmount(amount);
        this.timestamp = timestamp;
        this.note = note;
    }

    private String formatAmount(double amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormat.format(amount);
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

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public CharSequence getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
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
}