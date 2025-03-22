package com.example.menaccessoriesshop.data.model;

public class Category {
    private String id;
    private String categoryName;
    private String categoryDescription;
    private boolean status;
    private String categoryImage;

    public Category(String id, String categoryName, String categoryDescription, boolean status, String categoryImage) {
        this.id = id;
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
        this.status = status;
        this.categoryImage = categoryImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }
}

