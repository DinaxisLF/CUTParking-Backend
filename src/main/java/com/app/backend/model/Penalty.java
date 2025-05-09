package com.app.backend.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "penalties")
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int penaltyId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservations reservation;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Reason reason;

    @Column(name = "penalty_time")
    private Timestamp penaltyTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PenaltyStatus status;


    public enum Reason {
        LATE_ARRIVAL,
        UNAUTHORIZED_USAGE,
        CANCELLED
    }

    public enum PenaltyStatus {
        PENDING,
        PAID
    }

    public int getPenaltyId() {
        return penaltyId;
    }

    public void setPenaltyId(int penaltyId) {
        this.penaltyId = penaltyId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Reservations getReservation() {
        return reservation;
    }

    public void setReservation(Reservations reservation) {
        this.reservation = reservation;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public Timestamp getPenaltyTime() {
        return penaltyTime;
    }

    public void setPenaltyTime(Timestamp penaltyTime) {
        this.penaltyTime = penaltyTime;
    }

    public PenaltyStatus getStatus() {
        return status;
    }

    public void setStatus(PenaltyStatus status) {
        this.status = status;
    }
}
