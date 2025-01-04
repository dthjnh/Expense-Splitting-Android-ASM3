package com.example.expensesplitting.Model;

import java.util.List;

public class Wallet {
    private String userId;
    private double balance;
    private List<PaymentMethod> paymentMethods;

    public Wallet() {
        // Default constructor required for calls to DataSnapshot.getValue(Wallet.class)
    }

    public Wallet(String userId, double balance, List<PaymentMethod> paymentMethods) {
        this.userId = userId;
        this.balance = balance;
        this.paymentMethods = paymentMethods;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }
}