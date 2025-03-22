package com.example.menaccessoriesshop.data.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String productId;
    private String productName;
    private String productImage;
    private int quantity;
    private double productPrice;
    private boolean isSelected;

    public CartItem(String productId, String productName, String productImage, int quantity, double productPrice, boolean isSelected) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.quantity = quantity;
        this.productPrice = productPrice;
        this.isSelected = isSelected;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
