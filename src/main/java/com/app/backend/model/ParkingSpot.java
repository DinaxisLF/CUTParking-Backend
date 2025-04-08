package com.app.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;


import java.io.IOException;
import java.sql.Timestamp;

@Entity
@Table(name = "parking_spots", indexes = {
        @Index(name = "idx_location", columnList = "location", unique = false) // Spatial Index
})

public class ParkingSpot {

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


    public enum ParkingStatus {
        RESERVED,
        AVAILABLE,
        OCCUPIED,
        CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spot_id")
    private int id;


    @Column(name = "location", columnDefinition = "POINT NOT NULL")
    @JsonSerialize(using = PointSerializer.class)
    private Point location;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ParkingStatus status = ParkingStatus.AVAILABLE;

    @Column(name = "created_at", updatable = false, insertable = false)
    private Timestamp createdAt;

    @Column(name = "section" ,nullable = false)
    private Character section;

    public ParkingSpot() {}

    public ParkingSpot(Point location, ParkingStatus status, Character section) {
        this.location = location;
        this.status = status;
        this.section = section;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Point getLocation() { return location; }
    public void setLocation(Point location) { this.location = location; }

    public ParkingStatus getStatus() { return status; }
    public void setStatus(ParkingStatus status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Character getSection() {
        return section;
    }

    public void setSection(Character section) {
        this.section = section;
    }
}
