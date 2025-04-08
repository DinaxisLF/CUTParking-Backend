package com.app.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;

import org.locationtech.jts.geom.Point;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;

@Entity
public class Reservations {

    public enum ReservationsStatus{
        ACTIVE,
        COMPLETED,
        CANCELLED
    }

    public static class PointSerializer extends JsonSerializer<Point> {

        @Override
        public void serialize(Point point, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (point != null) {
                gen.writeStartObject();
                gen.writeNumberField("latitude", point.getY());  // Latitude is Y
                gen.writeNumberField("longitude", point.getX()); // Longitude is X
                gen.writeEndObject();
            }
        }
    }



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reservation_id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "spot_id", nullable = false)
    private ParkingSpot spot;

    private Timestamp startTime;
    private Timestamp endTime;

    @Enumerated(EnumType.STRING)
    private ReservationsStatus status;

    @Column(columnDefinition = "POINT SRID 4236")
    @JsonProperty("userLocation")
    @JsonSerialize(using = Reservations.PointSerializer.class)
    private Point userLocation;

    private Timestamp createdAt;

    private String qrCodeUrl;

    public int getReservation_id() {
        return reservation_id;
    }

    public void setReservation_id(int reservation_id) {
        this.reservation_id = reservation_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    public void setSpot(ParkingSpot spot) {
        this.spot = spot;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public ReservationsStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationsStatus status) {
        this.status = status;
    }

    public Point getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Point userLocation) {
        this.userLocation = userLocation;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
}
