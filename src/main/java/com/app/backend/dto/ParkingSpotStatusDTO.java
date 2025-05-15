package com.app.backend.dto;

import com.app.backend.model.ParkingSpot;

public class ParkingSpotStatusDTO {
    private int id;
    private double latitude;
    private double longitude;
    private String status;
    private char section;

    public ParkingSpotStatusDTO(ParkingSpot spot) {
        this.id = spot.getId();
        this.latitude = spot.getLocation().getY(); // Y = latitude
        this.longitude = spot.getLocation().getX(); // X = longitude
        this.status = spot.getStatus().name();
        this.section = spot.getSection();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public char getSection() {
        return section;
    }

    public void setSection(char section) {
        this.section = section;
    }
}
