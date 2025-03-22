package com.example.menaccessoriesshop.data.model;

import com.example.menaccessoriesshop.adapter.OrderAdapter;

import java.util.List;

public class Order {
    private String id;
    private String userID;
    private List<CartItem> items;
    private double totalPrice;
    private double discount;
    private double finalPrice;
    private String status;
    private String createdAt;
    private String address;

    public Order( String userID, List<CartItem> items, double totalPrice, double discount, double finalPrice, String status, String createdAt, String address) {
        this.userID = userID;
        this.items = items;
        this.totalPrice = totalPrice;
        this.discount = discount;
        this.finalPrice = finalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.address = address;
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

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
