package com.example.mathprojectavishaigozland.tramp;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@IgnoreExtraProperties
public class Ride {
    private String rideId, driverId, origin, destination, type;
    private Timestamp timestamp;
    private int totalSeats;
    private Map<String, Integer> pendingUsers = new HashMap<>();
    private Map<String, Integer> confirmedUsers = new HashMap<>();

    // --- שדות חדשים שהוספנו כדי לתמוך בשיחה ובשמות ---
    private String phoneNumber;
    private String driverName;

    public Ride() {
    } // חובה עבור Firebase

    public Ride(String rideId, String driverId, String origin, String destination, String type, Timestamp timestamp, int totalSeats) {
        this.rideId = rideId;
        this.driverId = driverId;
        this.origin = origin;
        this.destination = destination;
        this.type = type;
        this.timestamp = timestamp;
        this.totalSeats = totalSeats;
    }

    // --- Getters & Setters מקוריים (לא לגעת) ---
    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Map<String, Integer> getPendingUsers() {
        return pendingUsers != null ? pendingUsers : new HashMap<>();
    }

    public void setPendingUsers(Map<String, Integer> pendingUsers) {
        this.pendingUsers = pendingUsers;
    }

    public Map<String, Integer> getConfirmedUsers() {
        return confirmedUsers != null ? confirmedUsers : new HashMap<>();
    }

    public void setConfirmedUsers(Map<String, Integer> confirmedUsers) {
        this.confirmedUsers = confirmedUsers;
    }

    // --- Getters & Setters חדשים ---
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDriverName() {
        return driverName != null ? driverName : "מפרסם";
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    // --- פונקציות עזר (Helper Methods) כדי שה-Adapter לא יהיה אדום ---

    // ה-Adapter חיפש getSource, אז נפנה אותו ל-getOrigin
    public String getSource() {
        return getOrigin();
    }

    // ה-Adapter חיפש getTime ו-getDate מה-Timestamp
    public String getTime() {
        if (timestamp == null) return "";
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp.toDate());
    }

    public String getDate() {
        if (timestamp == null) return "";
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(timestamp.toDate());
    }

    public int getAvailableSeats() {
        int occupied = 0;
        if (confirmedUsers != null) {
            for (Object val : confirmedUsers.values()) {
                if (val instanceof Long) occupied += ((Long) val).intValue();
                else if (val instanceof Integer) occupied += (Integer) val;
            }
        }
        return totalSeats - occupied;
    }
}