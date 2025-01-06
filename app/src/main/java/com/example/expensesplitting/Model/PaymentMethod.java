package com.example.expensesplitting.Model;

import java.io.Serializable;

public class PaymentMethod implements Serializable {
    private String userId;
    private String cardNumber;
    private String accountHolderName;
    private String expiryDate;
    private String cvv;
    private boolean selected;

    public PaymentMethod() {
        // Default constructor required for calls to DataSnapshot.getValue(PaymentMethod.class)
    }

    public PaymentMethod(String userId,String cardNumber, String accountHolderName, String expiryDate, String cvv) {
        this.userId = userId;
        this.cardNumber = cardNumber;
        this.accountHolderName = accountHolderName;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.selected = false;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}