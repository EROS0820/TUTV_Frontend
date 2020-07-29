package com.backstagesupporters.fasttrack.models;


import android.os.Parcel;
import android.os.Parcelable;

public class DistanceReport2 implements Parcelable {

    private String vehicleNo;  // vehicle_no
    private String distance;   // distance
    private String date;      // date

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

    public DistanceReport2() {
    }

    protected DistanceReport2(Parcel in) {
        vehicleNo = in.readString();
        distance = in.readString();
        date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(vehicleNo);
        dest.writeString(distance);
        dest.writeString(date);
    }

    @SuppressWarnings("unused")
    public static final Creator<DistanceReport2> CREATOR = new Creator<DistanceReport2>() {
        @Override
        public DistanceReport2 createFromParcel(Parcel in) {
            return new DistanceReport2(in);
        }

        @Override
        public DistanceReport2[] newArray(int size) {
            return new DistanceReport2[size];
        }
    };
}