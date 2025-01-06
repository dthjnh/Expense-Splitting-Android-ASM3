package com.example.expensesplitting.Group;


public class Group {
    private int id;
    private String name;
    private String description;
    private String currency;
    private String category;
    private String image;

    public Group(int id, String name, String description, String currency, String category, String image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.currency = currency;
        this.category = category;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCategory() {
        return category;
    }

    public String getImage() {
        return image;
    }
}
