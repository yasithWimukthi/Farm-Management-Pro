package com.farmmanagementpro.modals;

public class Feed {
    private String date;
    private String name;
    private String qty;
    private String desctiption;
    private String supplier;
    private String image;

    public Feed() {
    }

    public Feed(String date, String name, String qty, String desctiption, String supplier, String image) {
        this.date = date;
        this.name = name;
        this.qty = qty;
        this.desctiption = desctiption;
        this.supplier = supplier;
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getDesctiption() {
        return desctiption;
    }

    public void setDesctiption(String desctiption) {
        this.desctiption = desctiption;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
