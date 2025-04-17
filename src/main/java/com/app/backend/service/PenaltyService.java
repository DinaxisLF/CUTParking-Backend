package com.app.backend.service;

import com.app.backend.model.Penalty;
import com.app.backend.model.Reservations;
import com.app.backend.model.User;
import com.app.backend.repository.PenaltyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Service
public class PenaltyService {

    @Autowired
    private PenaltyRepository penaltyRepository;

    public void createPenalty(User user, Reservations reservation, BigDecimal amount, Penalty.Reason reason) {
        Penalty penalty = new Penalty();
        penalty.setUser(user);
        penalty.setReservation(reservation);
        penalty.setAmount(amount);
        penalty.setReason(reason);
        penalty.setPenaltyTime(new Timestamp(System.currentTimeMillis()));
        penaltyRepository.save(penalty);
    }

    public List<Penalty> getPenaltiesByUserId(int userId) {
        return penaltyRepository.findByUserId(userId);
    }
}

