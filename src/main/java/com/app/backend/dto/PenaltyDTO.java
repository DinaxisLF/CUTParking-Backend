package com.app.backend.dto;

import com.app.backend.model.Penalty;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class PenaltyDTO {
    private int penaltyId;
    private int reservationId;
    private BigDecimal amount;
    private Penalty.Reason reason;
    private Timestamp penaltyTime;
    private Penalty.PenaltyStatus status;

    // Constructor
    public PenaltyDTO(int penaltyId, int reservationId, BigDecimal amount, Penalty.Reason reason, Timestamp penaltyTime, Penalty.PenaltyStatus status) {
        this.penaltyId = penaltyId;
        this.reservationId = reservationId;
        this.amount = amount;
        this.reason = reason;
        this.penaltyTime = penaltyTime;
        this.status = status;
    }

    // Getters
    public int getPenaltyId() {
        return penaltyId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Penalty.Reason getReason() {
        return reason;
    }

    public Timestamp getPenaltyTime() {
        return penaltyTime;
    }

    public Penalty.PenaltyStatus getStatus() {
        return status;
    }
}
