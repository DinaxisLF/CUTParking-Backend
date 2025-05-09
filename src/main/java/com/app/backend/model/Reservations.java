package com.app.backend.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
public class Reservations {

    @Converter(autoApply = true)
    public class ReservationStatusConverter implements AttributeConverter<Reservations.ReservationsStatus, String> {

        @Override
        public String convertToDatabaseColumn(Reservations.ReservationsStatus status) {
            return status == null ? null : status.name().toLowerCase(); // saves as "active"
        }

        @Override
        public Reservations.ReservationsStatus convertToEntityAttribute(String dbValue) {
            if (dbValue == null) return null;
            return Reservations.ReservationsStatus.valueOf(dbValue.toUpperCase()); // allows "active"
        }
    }


    public enum ReservationsStatus{
        ACTIVE,
        COMPLETED,
        CANCELLED;

        @JsonCreator
        public static ReservationsStatus fromValue(String value) {
            return ReservationsStatus.valueOf(value.toUpperCase());
        }
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reservation_id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "spot_id", nullable = false)
    private ParkingSpot spot;

    private Timestamp startTime;
    private Timestamp endTime;

    @Convert(converter = ReservationStatusConverter.class)
    @Enumerated(EnumType.STRING)
    private ReservationsStatus status;

    private Timestamp createdAt;

    private String qrCodeUrl;

    @ManyToOne
    @JoinColumn(name = "user_car_id", nullable = false)
    private Car userCar;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<Penalty> penalties;



    public int getReservation_id() {
        return reservation_id;
    }

    public void setReservation_id(int reservation_id) {
        this.reservation_id = reservation_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    public void setSpot(ParkingSpot spot) {
        this.spot = spot;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public ReservationsStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationsStatus status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public Car getUserCar() {
        return userCar;
    }

    public void setUserCar(Car userCar) {
        this.userCar = userCar;
    }
}
