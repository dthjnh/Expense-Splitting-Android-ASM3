package com.example.expensesplitting.Group;

import java.io.Serializable;

public class Participant implements Serializable {
    private String name;
    private double amount; // Amount allocated in the split
    private double paidAmount; // Amount paid by the participant
    private boolean selected; // Tracks if the participant is selected

    public Participant(String name) {
        this.name = name;
        this.amount = 0; // Default: no amount allocated
        this.paidAmount = 0; // Default: no amount paid
        this.selected = false; // Default: not selected
    }

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for allocated amount
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    // Getter and Setter for paid amount
    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    // Getter and Setter for selected status
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "name='" + name + '\'' +
                ", amount=" + amount +
                ", paidAmount=" + paidAmount +
                ", selected=" + selected +
                '}';
    }
}
