package com.farmmanagementpro.modals;

public class Spray {
    private String name;
    private String date;
    private String qty;
    private String number;
    private String supplier;
    private String information;
    private String image;

    public Spray() {
    }

    public Spray(String name, String date, String qty, String number, String supplier, String information, String image) {
        this.name = name;
        this.date = date;
        this.qty = qty;
        this.number = number;
        this.supplier = supplier;
        this.information = information;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
