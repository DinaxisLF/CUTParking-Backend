package com.app.backend.dto;


import java.sql.Timestamp;

public class ReservationDTO {
    private int reservationId;
    private int userId;
    private int spotId;
    private Character spotSection;
    private Timestamp startTime;
    private Timestamp endTime;
    private String qrCodeUrl;
    private int userCarId;
    private String carModel;
    private String carPlates;
    private Timestamp createdAt;
    private String status;


    public ReservationDTO(int userId, int spotId, Timestamp startTime, Timestamp endTime, int userCarId){
        this.userId = userId;
        this.spotId = spotId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userCarId = userCarId;
    }

    public ReservationDTO(int reservationId, int userId, int spotId, Timestamp startTime, Timestamp endTime, String qrCodeUrl, int userCarId, String carModel, String carPlates, Character spotSection) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.spotId = spotId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.qrCodeUrl = qrCodeUrl;
        this.userCarId = userCarId;
        this.carModel = carModel;
        this.carPlates = carPlates;
        this.spotSection = spotSection;
    }

    public ReservationDTO(){

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

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public int getUserCarId() {
        return userCarId;
    }

    public void setUserCarId(int userCarId) {
        this.userCarId = userCarId;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarPlates() {
        return carPlates;
    }

    public void setCarPlates(String carPlates) {
        this.carPlates = carPlates;
    }

    public Character getSpotSection() {
        return spotSection;
    }

    public void setSpotSection(Character spotSection) {
        this.spotSection = spotSection;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
