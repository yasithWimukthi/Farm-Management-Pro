package com.farmmanagementpro.modals;

public class AnimalEvent {
    private String eventDate;
    private String animalId;
    private String eventName;
    private String stockBull;
    private String notes;

    public AnimalEvent() {
    }

    public AnimalEvent(String eventDate, String animalId, String eventName, String stockBull, String notes) {
        this.eventDate = eventDate;
        this.animalId = animalId;
        this.eventName = eventName;
        this.stockBull = stockBull;
        this.notes = notes;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getAnimalId() {
        return animalId;
    }

    public void setAnimalId(String animalId) {
        this.animalId = animalId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getStockBull() {
        return stockBull;
    }

    public void setStockBull(String stockBull) {
        this.stockBull = stockBull;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
