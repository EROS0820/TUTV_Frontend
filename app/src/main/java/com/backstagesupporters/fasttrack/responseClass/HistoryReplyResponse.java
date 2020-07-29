package com.backstagesupporters.fasttrack.responseClass;

import com.backstagesupporters.fasttrack.models.HistoryData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HistoryReplyResponse {
    @SerializedName("vehicle_type")
    @Expose
    private String vehicleType;

    @SerializedName("data")
    private List<HistoryData> dataHistory = null;

    @SerializedName("from_date")
    private String fromDate;

    @SerializedName("to_date")
    private String toDate;

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;


    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("avg_speed")
    @Expose
    private String avgSpeed;
    @SerializedName("max_speed")
    @Expose
    private String maxSpeed;



    public HistoryReplyResponse() { }



    public List<HistoryData> getDataHistory() {
        return dataHistory;
    }

    public void setDataHistory(List<HistoryData> dataHistory) {
        this.dataHistory = dataHistory;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(String avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public String getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(String maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
}

