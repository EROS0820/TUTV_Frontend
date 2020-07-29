package com.backstagesupporters.fasttrack.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TripModel {
    /*@SerializedName("trip")
    @Expose
    private int trip;
    @SerializedName("vehicle_no")
    @Expose
    private String vehicleNo;
    @SerializedName("start_lat")
    @Expose
    private String startLat;
    @SerializedName("start_long")
    @Expose
    private String startLong;
    @SerializedName("end_lat")
    @Expose
    private String endLat;
    @SerializedName("end_long")
    @Expose
    private String endLong;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("date")
    @Expose
    private String date;
*/


    @SerializedName("trip_no")
    @Expose
    private String tripNo;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("start_latitude")
    @Expose
    private String startLatitude;
    @SerializedName("start_longitude")
    @Expose
    private String startLongitude;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_latitude")
    @Expose
    private String endLatitude;
    @SerializedName("end_longitude")
    @Expose
    private String endLongitude;
    @SerializedName("end_date")
    @Expose
    private String endDate;
    @SerializedName("end_time")
    @Expose
    private String endTime;


    public String getTripNo() {
        return tripNo;
    }

    public void setTripNo(String tripNo) {
        this.tripNo = tripNo;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(String startLatitude) {
        this.startLatitude = startLatitude;
    }

    public String getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(String startLongitude) {
        this.startLongitude = startLongitude;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndLatitude() {
        return endLatitude;
    }

    public void setEndLatitude(String endLatitude) {
        this.endLatitude = endLatitude;
    }

    public String getEndLongitude() {
        return endLongitude;
    }

    public void setEndLongitude(String endLongitude) {
        this.endLongitude = endLongitude;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}