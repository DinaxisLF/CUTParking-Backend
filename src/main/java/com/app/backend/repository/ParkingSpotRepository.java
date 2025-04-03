package com.app.backend.repository;

import com.app.backend.model.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.locationtech.jts.geom.Point;

import java.awt.*;

import java.util.List;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Integer> {

    List<ParkingSpot> findByStatus(ParkingSpot.ParkingStatus status);

    // Find nearest available parking spot within a radius
    @Query("SELECT p FROM ParkingSpot p WHERE p.status = 'AVAILABLE' AND ST_Distance_Sphere(p.location, :userLocation) < :radius")
    List<ParkingSpot> findNearbyAvailableSpots(Point userLocation, double radius);

}
