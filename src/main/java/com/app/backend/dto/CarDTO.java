package com.app.backend.dto;

public class CarDTO {
    private String carPlates;
    private String model;
    private int ownerId;  // Only store the user ID

    // Getters and Setters
    public String getCarPlates() {
        return carPlates;
    }

    public void setCarPlates(String carPlates) {
        this.carPlates = carPlates;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
}