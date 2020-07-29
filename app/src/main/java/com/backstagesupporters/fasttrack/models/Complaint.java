package com.backstagesupporters.fasttrack.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Complaint {
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("complaint")
    @Expose
    private String complaint;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mobile")
    @Expose
    private String phone;

    @SerializedName("vehicle_no")
    @Expose
    private String vehicle_no;

    public Complaint(String token, String complaint, String name, String phone) {
        this.token = token;
        this.complaint = complaint;
        this.name = name;
        this.phone = phone;
    }

    public Complaint(String token, String complaint, String name, String phone, String vehicle_no) {
        this.token = token;
        this.complaint = complaint;
        this.name = name;
        this.phone = phone;
        this.vehicle_no = vehicle_no;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVehicle_no() {
        return vehicle_no;
    }

    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }
}

