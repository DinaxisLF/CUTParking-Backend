package com.app.backend.repository;

import com.app.backend.model.ParkingSpot;
import com.app.backend.model.Reservations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservations, Integer> {

    Optional<Reservations> findBySpot_IdAndStatus(int spotId, Reservations.ReservationsStatus status);

    boolean existsBySpotAndStatusAndStartTimeBeforeAndEndTimeAfter(
            ParkingSpot spot,
            Reservations.ReservationsStatus status,
            Timestamp endTime,
            Timestamp startTime
    );

}
