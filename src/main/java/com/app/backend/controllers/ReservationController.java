package com.app.backend.controllers;

import com.app.backend.dto.ReservationDTO;
import com.app.backend.model.Reservations;
import com.app.backend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;



    @PostMapping("/create")
    public ResponseEntity<ReservationDTO> createReservation(@RequestBody ReservationDTO dto) throws Exception {

        return ResponseEntity.ok(reservationService.createReservation(dto));
    }


    @PutMapping("/cancel")
    public ResponseEntity<String> cancelReservation(
            @RequestParam int reservationId,
            @RequestParam int userId
    ) {
        String result = reservationService.cancelReservation(reservationId, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable("id") int id) {
        ReservationDTO dto = reservationService.getReservationById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/user/{userId}/reservations")
    public ResponseEntity<List<ReservationDTO>> getAllReservationsByUser(@PathVariable int userId) {
        List<ReservationDTO> reservations = reservationService.getAllReservationsByUserId(userId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/user/{userId}/active-reservation")
    public ResponseEntity<Reservations> getActiveReservation(@PathVariable int userId) {
        Optional<Reservations> activeRes = reservationService.getActiveReservation(userId);
        return activeRes
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }



    @GetMapping("/verify")
    public ResponseEntity<String> verifyReservation(
            @RequestParam int reservationId,
            @RequestParam int userId
    ) {
        ReservationService.VerificationResult result = reservationService.verifyQRCode(reservationId, userId);
        if (result.isSuccess()) {
            return ResponseEntity.ok(result.getMessage());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result.getMessage());
        }
    }

}
