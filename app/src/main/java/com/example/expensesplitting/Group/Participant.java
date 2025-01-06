package com.example.expensesplitting.Group;

import java.io.Serializable;

public class Participant implements Serializable {
    private String name;
    private double amount; // Amount allocated
    private double percentage; // Percentage of the split
    private int shares; // Shares for the participant

    public Participant(String name) {
        this.name = name;
        this.amount = 0;
        this.percentage = 0;
        this.shares = 1; // Default to 1 share
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }
}
