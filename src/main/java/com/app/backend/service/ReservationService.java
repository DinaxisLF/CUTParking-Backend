package com.app.backend.service;

import com.app.backend.dto.ReservationDTO;
import com.app.backend.model.ParkingSpot;
import com.app.backend.model.Reservations;
import com.app.backend.model.User;
import com.app.backend.repository.ParkingSpotRepository;
import com.app.backend.repository.ReservationRepository;
import com.app.backend.repository.UserRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);


    public Reservations createReservation(ReservationDTO dto) {



        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        Optional<ParkingSpot> spotOpt = parkingSpotRepository.findById(dto.getSpotId());

        if (userOpt.isEmpty() || spotOpt.isEmpty()) {
            throw new RuntimeException("User or parking spot not found.");
        }

        User user = userOpt.get();
        ParkingSpot spot = spotOpt.get();

        // Check if the spot is already reserved during the requested time
        boolean isReserved = reservationRepository.existsBySpotAndStatusAndStartTimeBeforeAndEndTimeAfter(
                spot, Reservations.ReservationsStatus.ACTIVE, dto.getEndTime(), dto.getStartTime());

        if (isReserved) {
            throw new RuntimeException("Parking spot is already reserved for the selected time.");
        }

        // Create reservation
        Reservations reservation = new Reservations();
        reservation.setUser(user);
        reservation.setSpot(spot);
        reservation.setStartTime(dto.getStartTime());
        reservation.setEndTime(dto.getEndTime());
        reservation.setStatus(Reservations.ReservationsStatus.ACTIVE);

        // Create Point from lat/lng
        Point userLocation = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
        reservation.setUserLocation(userLocation);

        reservation.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        return reservationRepository.save(reservation);
    }

    public boolean verifyQRCode(int reservationId, int userId){
        Optional<Reservations> res = reservationRepository.findById(reservationId);
        if(res.isPresent()){
            Reservations r = res.get();
            return r.getUser().getId() == (userId);
        }

        return false;
    }



}
