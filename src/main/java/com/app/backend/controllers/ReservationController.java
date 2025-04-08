package com.app.backend.controllers;

import com.app.backend.dto.ReservationDTO;
import com.app.backend.model.Reservations;
import com.app.backend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;



    @PostMapping("/create")
    public ResponseEntity<Reservations> createReservation(@RequestBody ReservationDTO dto) throws Exception {

        return ResponseEntity.ok(reservationService.createReservation(dto));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyReservation(
            @RequestParam int reservationId,
            @RequestParam int userId
    ) {
        boolean isValid = reservationService.verifyQRCode(reservationId, userId);
        if (isValid) {
            return ResponseEntity.ok("Reservation is valid!");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid QR or spot!");
        }
    }
}
