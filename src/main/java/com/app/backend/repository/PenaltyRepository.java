package com.app.backend.repository;

import com.app.backend.model.Penalty;
import com.app.backend.model.Reservations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PenaltyRepository extends JpaRepository<Penalty, Integer> {
    List<Penalty> findByUserId(int userId);

    Optional<Penalty> findByReservationAndReason(Reservations reservation, Penalty.Reason reason);
}
