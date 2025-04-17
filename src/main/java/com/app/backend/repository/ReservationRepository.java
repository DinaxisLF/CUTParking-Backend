package com.app.backend.repository;

import com.app.backend.model.ParkingSpot;
import com.app.backend.model.Reservations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservations, Integer> {

    Optional<Reservations> findBySpot_IdAndStatus(int spotId, Reservations.ReservationsStatus status);

    List<Reservations> findTop5ByUserIdOrderByCreatedAtDesc(int userId);

    @Query("SELECT r FROM Reservations r WHERE r.status = 'ACTIVE' AND r.startTime < :now")
    List<Reservations> findActiveReservationsPastStartTime(@Param("now") Timestamp now);


    boolean existsBySpotAndStatusAndStartTimeBeforeAndEndTimeAfter(
            ParkingSpot spot,
            Reservations.ReservationsStatus status,
            Timestamp endTime,
            Timestamp startTime
    );

}
