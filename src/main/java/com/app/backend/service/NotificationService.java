package com.app.backend.service;

import com.app.backend.dto.ParkingSpotStatusDTO;
import com.app.backend.dto.NotificationsDTO;
import com.app.backend.model.ParkingSpot;
import com.app.backend.model.Reservations;
import com.app.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void broadcastStatusChange(ParkingSpot spot) {
        ParkingSpotStatusDTO dto = new ParkingSpotStatusDTO(spot);
        messagingTemplate.convertAndSend("/topic/parking-status", dto);
    }

    public void notifyNewReservation(Reservations reservation) {
        NotificationsDTO.ReservationNotificationDTO dto = new NotificationsDTO.ReservationNotificationDTO();
        dto.setAction("CREATED");
        dto.setReservationId(reservation.getReservation_id());
        dto.setUserId(reservation.getUser().getId());
        dto.setSpotId(reservation.getSpot().getId());
        dto.setStartTime(reservation.getStartTime());
        dto.setEndTime(reservation.getEndTime());
        dto.setStatus(reservation.getStatus().name());
        dto.setQrCodeUrl(reservation.getQrCodeUrl());

        messagingTemplate.convertAndSendToUser(
                String.valueOf(reservation.getUser().getId()),
                "/queue/reservations",
                dto
        );
    }






}
