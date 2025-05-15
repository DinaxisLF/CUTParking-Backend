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


    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeReservation(@PathVariable Integer id) {
        reservationService.completeReservation(id);
        return ResponseEntity.ok("Reservation marked as completed and spot is now available.");
    }

    @PutMapping("/{id}/arrived")
    public ResponseEntity<?> markUserArrival(@PathVariable Integer id) {
        reservationService.markUserArrival(id);
        return ResponseEntity.ok("User arrival confirmed. Spot marked as OCCUPIED, reservation marked as ARRIVE.");
    }

    @PutMapping("/{id}/extend")
    public ResponseEntity<?> extendReservation(@PathVariable Integer id, @RequestBody ReservationDTO.ExtendReservationRequest request) {
        try {
            reservationService.extendReservation(id, request.getNewEndTime());
            return ResponseEntity.ok("Reservation extended successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred.");
        }
    }



}
