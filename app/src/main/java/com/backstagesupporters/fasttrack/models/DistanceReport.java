package com.backstagesupporters.fasttrack.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DistanceReport  {
    @SerializedName("vehicle_no")
    @Expose
    private String vehicleNo;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("date")
    @Expose
    private String date;

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public DistanceReport() {
    }


}