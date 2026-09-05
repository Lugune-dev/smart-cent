package com.mediqueue.dto;

public class BookingDto {
    private String tokenId;
    private String mqId;
    private String hospitalId;
    private String hospitalName;
    private String appointmentDate;
    private String doctorTime;
    private String arriveByTime;
    private String status;

    public BookingDto() {}

    public BookingDto(String tokenId, String mqId, String hospitalId, String hospitalName, String appointmentDate, String doctorTime, String arriveByTime, String status) {
        this.tokenId = tokenId;
        this.mqId = mqId;
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.appointmentDate = appointmentDate;
        this.doctorTime = doctorTime;
        this.arriveByTime = arriveByTime;
        this.status = status;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getMqId() {
        return mqId;
    }

    public void setMqId(String mqId) {
        this.mqId = mqId;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getDoctorTime() {
        return doctorTime;
    }

    public void setDoctorTime(String doctorTime) {
        this.doctorTime = doctorTime;
    }

    public String getArriveByTime() {
        return arriveByTime;
    }

    public void setArriveByTime(String arriveByTime) {
        this.arriveByTime = arriveByTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
