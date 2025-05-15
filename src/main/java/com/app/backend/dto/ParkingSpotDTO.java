package com.app.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.app.backend.model.ParkingSpot;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParkingSpotDTO {

    private int id;
    private double latitude;
    private double longitude;
    private String status;
    private Character section;


    public ParkingSpotDTO() {}

    public ParkingSpotDTO(ParkingSpot spot) {
        this.id = spot.getId();
        this.latitude = spot.getLocation().getY();
        this.longitude = spot.getLocation().getX();
        this.status = spot.getStatus().name();
        this.section = spot.getSection();
    }

    @JsonProperty("latitude")
    public double getLatitude() {
        return latitude;
    }

    @JsonProperty("longitude")
    public double getLongitude() {
        return longitude;
    }

    public String getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public Character getSection() {
        return section;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSection(Character section) {
        this.section = section;
    }
}


