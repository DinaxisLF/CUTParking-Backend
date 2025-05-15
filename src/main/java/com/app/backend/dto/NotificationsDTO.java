package com.app.backend.dto;

import java.sql.Timestamp;


public class NotificationsDTO {

    public static class ReservationNotificationDTO {
        private String action; // "CREATED", "UPDATED", "CANCELLED"
        private int reservationId;
        private int userId;
        private int spotId;
        private Timestamp startTime;
        private Timestamp endTime;
        private String status;
        private String qrCodeUrl;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public int getReservationId() {
            return reservationId;
        }

        public void setReservationId(int reservationId) {
            this.reservationId = reservationId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getSpotId() {
            return spotId;
        }

        public void setSpotId(int spotId) {
            this.spotId = spotId;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getQrCodeUrl() {
            return qrCodeUrl;
        }

        public void setQrCodeUrl(String qrCodeUrl) {
            this.qrCodeUrl = qrCodeUrl;
        }
    }
}