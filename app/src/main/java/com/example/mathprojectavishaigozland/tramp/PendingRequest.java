package com.example.mathprojectavishaigozland.tramp;

public class PendingRequest {
    private final String userId;
    private final String userName;
    private final String status;
    private final int requestedSeats;

    public PendingRequest(String userId, String userName, int requestedSeats, String status) {
        this.userId = userId;
        this.userName = userName;
        this.requestedSeats = requestedSeats;
        this.status = status;
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
}