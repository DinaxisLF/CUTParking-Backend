package com.app.backend.service;

import com.app.backend.dto.PenaltyDTO;
import com.app.backend.model.Penalty;
import com.app.backend.model.Reservations;
import com.app.backend.model.User;
import com.app.backend.repository.PenaltyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        penalty.setStatus(Penalty.PenaltyStatus.PENDING);
        penaltyRepository.save(penalty);
    }

    public List<PenaltyDTO> getPenaltyDTOsByUserId(int userId) {
        List<Penalty> penalties = penaltyRepository.findByUserId(userId);
        return penalties.stream()
                .map(p -> new PenaltyDTO(
                        p.getPenaltyId(),
                        p.getReservation().getReservation_id(),
                        p.getAmount(),
                        p.getReason(),
                        p.getPenaltyTime(),
                        p.getStatus()
                ))
                .collect(Collectors.toList());
    }

    public Optional<PenaltyDTO> getPenaltyById(int penaltyId) {
        Optional<Penalty> penaltyOpt = penaltyRepository.findById(penaltyId);

        return penaltyOpt.map(penalty -> new PenaltyDTO(
                penalty.getPenaltyId(),
                penalty.getReservation().getReservation_id(),
                penalty.getAmount(),
                penalty.getReason(),
                penalty.getPenaltyTime(),
                penalty.getStatus()
        ));
    }


}

