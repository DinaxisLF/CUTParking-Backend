package com.app.backend.service;

import com.app.backend.model.ParkingSpot;
import com.app.backend.repository.ParkingSpotRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(); // Used to create Point objects


    @Autowired
    public ParkingSpotService(ParkingSpotRepository parkingSpotRepository){
        this.parkingSpotRepository = parkingSpotRepository;
    }

    public List<ParkingSpot> getAllSpots() {
        return parkingSpotRepository.findAll();
    }

    public Optional<ParkingSpot> getSpotById(int id) {
        return parkingSpotRepository.findById(id);
    }

    public List<ParkingSpot> getAvailableSpots() {
        return parkingSpotRepository.findByStatus(ParkingSpot.ParkingStatus.AVAILABLE);
    }

    public ParkingSpot createSpot(double latitude, double longitude) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326); // Set SRID 4326
        Point location = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        location.setSRID(4326); // Ensures spatial compatibility

        ParkingSpot spot = new ParkingSpot(location, ParkingSpot.ParkingStatus.AVAILABLE);
        return parkingSpotRepository.save(spot);
    }

    public List<ParkingSpot> findNearbySpots(double latitude, double longitude, double radius) {
        Point userLocation = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        return parkingSpotRepository.findNearbyAvailableSpots(userLocation, radius);
    }

    public boolean deleteSpot(int id){
        if(parkingSpotRepository.existsById(id)){
            parkingSpotRepository.deleteById(id);
            return true;
        }
        return false;
    }

}

