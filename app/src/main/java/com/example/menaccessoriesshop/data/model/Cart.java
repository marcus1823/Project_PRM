package com.example.menaccessoriesshop.data.model;

import java.util.List;

public class Cart {
    private String id;
    private String userID;
    private List<CartItem> items;

    public Cart(String userID, List<CartItem> items) {
        this.userID = userID;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}
