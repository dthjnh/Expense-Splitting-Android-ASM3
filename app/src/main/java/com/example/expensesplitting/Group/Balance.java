package com.example.expensesplitting.Group;

public class Balance {
    private final String ower; // The participant who owes money
    private final String payee; // The participant who is owed money
    private final double amount;

    public Balance(String ower, String payee, double amount) {
        this.ower = ower;
        this.payee = payee;
        this.amount = amount;
    }

    public String getOwer() {
        return ower;
    }

    public String getPayee() {
        return payee;
    }

    public double getAmount() {
        return amount;
    }
}
