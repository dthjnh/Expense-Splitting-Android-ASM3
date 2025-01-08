package com.example.expensesplitting.Group;

public class Expense {
    private long groupId;
    private String title;
    private double amount;
    private String category;
    private String paidBy;
    private String splitBy;
    private String notes;

    public Expense(long groupId, String title, double amount, String category, String paidBy, String splitBy, String notes) {
        this.groupId = groupId;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.paidBy = paidBy;
        this.splitBy = splitBy;
        this.notes = notes;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public String getSplitBy() {
        return splitBy;
    }

    public void setSplitBy(String splitBy) {
        this.splitBy = splitBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
