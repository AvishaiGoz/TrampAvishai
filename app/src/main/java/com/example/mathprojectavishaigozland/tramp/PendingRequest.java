package com.example.mathprojectavishaigozland.tramp;

public class PendingRequest {
    private final String userId;
    private final String userName;
    private final String status;
    private final int requestedSeats;
    private final String phoneNumber; // שדה חדש למספר הטלפון

    public PendingRequest(String userId, String userName, int requestedSeats, String status, String phoneNumber) {
        this.userId = userId;
        this.userName = userName;
        this.requestedSeats = requestedSeats;
        this.status = status;
        this.phoneNumber = phoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getStatus() {
        return status;
    }

    public int getRequestedSeats() {
        return requestedSeats;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}