package com.app.backend.controllers;

import com.app.backend.model.ParkingSpot;
import com.app.backend.service.ParkingSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/parking-spots")
public class ParkingSpotController {

    @Autowired
    private ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @GetMapping("/all")
    public List<ParkingSpot> getAllSpots() {
        return parkingSpotService.getAllSpots();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingSpot> getSpotById(@PathVariable int id) {
        Optional<ParkingSpot> spot = parkingSpotService.getSpotById(id);
        return spot.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/available")
    public List<ParkingSpot> getAvailableSpots() {
        return parkingSpotService.getAvailableSpots();
    }

    @PostMapping("/create")
    public ParkingSpot createSpot(@RequestParam double latitude, @RequestParam double longitude) {
        return parkingSpotService.createSpot(latitude, longitude);
    }

    @GetMapping("/nearby")
    public List<ParkingSpot> findNearbySpots(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius
    ) {
        return parkingSpotService.findNearbySpots(latitude, longitude, radius);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSpot(@PathVariable int id){
        boolean deleted = parkingSpotService.deleteSpot(id);
        if(deleted){
            return ResponseEntity.ok("Parking spot deleted correctly");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking spot not found");
        }
    }
}