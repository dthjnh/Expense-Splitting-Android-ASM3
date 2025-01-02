package com.example.expensesplitting.AccountInfo;

public class Device {
    private String name;
    private String lastLogin;

    public Device() {
        // Default constructor required for calls to DataSnapshot.getValue(Device.class)
    }

    public Device(String name, String lastLogin) {
        this.name = name;
        this.lastLogin = lastLogin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }
}