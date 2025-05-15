package com.app.backend.controllers;



import com.app.backend.model.ParkingSpot;

import com.app.backend.service.ParkingSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/parking-spots")
public class ParkingSpotController {

    @Autowired
    private ParkingSpotService parkingSpotService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;



    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }


    @GetMapping("/all")
    public ResponseEntity<List<ParkingSpot>> getAllSpots() {
        return ResponseEntity.ok(parkingSpotService.getAllSpots());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingSpot> getSpotById(@PathVariable int id) {
        Optional<ParkingSpot> spot = parkingSpotService.getSpotById(id);
        return spot.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/available")
    public ResponseEntity<List<ParkingSpot>> getAvailableSpots() {
        return ResponseEntity.ok(parkingSpotService.getAvailableSpots());
    }

    @PostMapping("/create")
    public ResponseEntity<ParkingSpot> createSpot(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam Character section) {

        // Ejemplo de cómo obtener información del usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        System.out.println("Spot created by: " + username);

        return ResponseEntity.ok(parkingSpotService.createSpot(latitude, longitude, section));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<ParkingSpot>> findNearbySpots(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radius) {
        return ResponseEntity.ok(parkingSpotService.findNearbySpots(latitude, longitude, radius));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Solo administradores pueden eliminar
    public ResponseEntity<String> deleteSpot(@PathVariable int id) {
        boolean deleted = parkingSpotService.deleteSpot(id);
        if(deleted) {
            return ResponseEntity.ok("Parking spot deleted correctly");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking spot not found");
        }
    }


}