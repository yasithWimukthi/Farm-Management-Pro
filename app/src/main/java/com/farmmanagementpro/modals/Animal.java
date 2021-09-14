package com.farmmanagementpro.modals;

public class Animal {
    private String animalId;
    private String status;
    private String registeredDate;
    private String dob;
    private String gender;
    private String breed;
    private String sire;
    private String note;

    public Animal() {
    }

    public Animal(String animalId, String status, String registeredDate, String dob, String gender, String breed, String sire, String note) {
        this.animalId = animalId;
        this.status = status;
        this.registeredDate = registeredDate;
        this.dob = dob;
        this.gender = gender;
        this.breed = breed;
        this.sire = sire;
        this.note = note;
    }

    public String getAnimalId() {
        return animalId;
    }

    public void setAnimalId(String animalId) {
        this.animalId = animalId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getSire() {
        return sire;
    }

    public void setSire(String sire) {
        this.sire = sire;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
