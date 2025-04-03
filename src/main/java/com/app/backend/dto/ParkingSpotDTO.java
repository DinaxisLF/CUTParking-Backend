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

    public ParkingSpotDTO() {}

    public ParkingSpotDTO(ParkingSpot spot) {
        this.id = spot.getId();
        this.latitude = spot.getLocation().getY();
        this.longitude = spot.getLocation().getX();
        this.status = spot.getStatus().name();
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
}


