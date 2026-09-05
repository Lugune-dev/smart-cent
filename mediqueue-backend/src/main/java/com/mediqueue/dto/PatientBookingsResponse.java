package com.mediqueue.dto;

import java.util.List;

public class PatientBookingsResponse {
    private String mqId;
    private List<BookingDto> bookings;

    public PatientBookingsResponse() {}

    public PatientBookingsResponse(String mqId, List<BookingDto> bookings) {
        this.mqId = mqId;
        this.bookings = bookings;
    }

    public String getMqId() {
        return mqId;
    }

    public void setMqId(String mqId) {
        this.mqId = mqId;
    }

    public List<BookingDto> getBookings() {
        return bookings;
    }

    public void setBookings(List<BookingDto> bookings) {
        this.bookings = bookings;
    }
}
