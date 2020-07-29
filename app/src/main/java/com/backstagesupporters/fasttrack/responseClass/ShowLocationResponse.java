package com.backstagesupporters.fasttrack.responseClass;


import com.backstagesupporters.fasttrack.models.MyLocation;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShowLocationResponse {

    @SerializedName("location")
    @Expose
    private List<MyLocation> location = null;
    @SerializedName("todaysdistance")
    @Expose
    private String totalDistance;
    @SerializedName("total_trip")
    @Expose
    private String totalTrip;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("vehicle_type")
    @Expose
    private String vehicleType;


    public List<MyLocation> getLocation() {
        return location;
    }

    public void setLocation(List<MyLocation> location) {
        this.location = location;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getTotalTrip() {
        return totalTrip;
    }

    public void setTotalTrip(String totalTrip) {
        this.totalTrip = totalTrip;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
}
