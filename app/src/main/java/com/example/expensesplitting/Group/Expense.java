package com.example.expensesplitting.Group;

public class Expense {
    private long groupId;
    private String title;
    private double amount;
    private String category;
    private String paidBy;
    private String splitBy;
    private String splitDetails; // New field
    private String notes;

    public Expense(long groupId, String title, double amount, String category, String paidBy, String splitBy, String splitDetails, String notes) {
        this.groupId = groupId;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.paidBy = paidBy;
        this.splitBy = splitBy;
        this.splitDetails = splitDetails; // Initialize splitDetails
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

    public String getSplitDetails() {
        return splitDetails;
    }

    public void setSplitDetails(String splitDetails) {
        this.splitDetails = splitDetails;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "groupId=" + groupId +
                ", title='" + title + '\'' +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", paidBy='" + paidBy + '\'' +
                ", splitBy='" + splitBy + '\'' +
                ", splitDetails='" + splitDetails + '\'' + // Include splitDetails in toString
                ", notes='" + notes + '\'' +
                '}';
    }
}
