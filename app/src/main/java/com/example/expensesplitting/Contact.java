package com.example.expensesplitting;

public class Contact {

    private int id;
    private String name;
    private String email;
    private int avatar;
    private boolean isFavorite;

    public Contact(int id, String name, String email, int avatar, boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.isFavorite = isFavorite;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getAvatar() {
        return avatar;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
