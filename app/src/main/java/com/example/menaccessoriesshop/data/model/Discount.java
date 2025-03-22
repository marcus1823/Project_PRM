package com.example.menaccessoriesshop.data.model;

public class Discount {
    private String id;
    private String name;
    private double value;
    private String startDate;
    private String endDate;
    private boolean status;
    private int quantity;

    public Discount(String id, String name, double value, String startDate, String endDate, boolean status, int quantity) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
