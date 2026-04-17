package com.example.mathprojectavishaigozland.tramp;

public class Ride {
    private String origin, destination, date, time, seats, notes, driverId, type;

    // חובה להוסיף קונסטרקטור ריק עבור Firebase
    public Ride() {
    }

    public Ride(String origin, String destination, String date, String time, String seats, String type, String notes, String driverId) {
        this.origin = origin;
        this.destination = destination;
        this.date = date;
        this.time = time;
        this.seats = seats;
        this.driverId = driverId;
        this.notes = notes;
        this.type = type;

    }

    // Getters
    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getType() {
        return type;
    }

    // שימוש בבדיקת null כדי שלא יופיע הכיתוב null במסך
    public String getSeats() {
        return seats != null ? seats : "";
    }

    public String getNotes() {
        return notes != null ? notes : "";
    }
}