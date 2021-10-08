package com.farmmanagementpro.modals;

public class Medicine {
    private String date;
    private String name;
    private String qty;
    private String prescribedBy;
    private String supplier;
    private String image;

    public Medicine(){}

    public Medicine(String date, String name, String qty, String prescribedBy, String supplier, String image) {
        this.date = date;
        this.name = name;
        this.qty = qty;
        this.prescribedBy = prescribedBy;
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

    public String getPrescribedBy() {
        return prescribedBy;
    }

    public void setPrescribedBy(String prescribedBy) {
        this.prescribedBy = prescribedBy;
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
