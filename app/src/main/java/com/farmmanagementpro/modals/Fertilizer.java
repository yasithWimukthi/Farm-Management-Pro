package com.farmmanagementpro.modals;

public class Fertilizer {
    private String name;
    private String date;
    private String quantity;
    private String supplier;
    private String batchNo;
    private String additionalInfo;
    private String image;

    public Fertilizer() {
    }

    public Fertilizer(String name, String date, String quantity, String supplier, String batchNo, String additionalInfo, String image) {
        this.name = name;
        this.date = date;
        this.quantity = quantity;
        this.supplier = supplier;
        this.batchNo = batchNo;
        this.additionalInfo = additionalInfo;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}


