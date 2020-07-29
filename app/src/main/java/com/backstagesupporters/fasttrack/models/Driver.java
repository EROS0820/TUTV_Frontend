package com.backstagesupporters.fasttrack.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Driver {


    @SerializedName("vehicle_id")
    @Expose
    private String vehicle_id;

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("driver_name")
    @Expose
    private String driverName;
    @SerializedName("driver_phone")
    @Expose
    private String driverPhone;
    @SerializedName("driver_email")
    @Expose
    private String driverEmail;
    @SerializedName("driver_gander")
    @Expose
    private String driverGander;
    @SerializedName("driver_state")
    @Expose
    private String driverState;
    @SerializedName("driver_city")
    @Expose
    private String driverCity;
    @SerializedName("driver_dob")
    @Expose
    private String driverDob;
    @SerializedName("driver_country")
    @Expose
    private String driverCountry;
    @SerializedName("driver_pincode")
    @Expose
    private String driverPincode;
    @SerializedName("driver_pan_no")
    @Expose
    private String driverPanNo;
    @SerializedName("driver_drive_licence_no")
    @Expose
    private String driverDriveLicenceNo;
    @SerializedName("driver_aadhar_no")
    @Expose
    private String driverAadharNo;


    public Driver() {

    }


    public Driver(String driverName, String driverPhone, String driverEmail, String driverGander, String driverState, String driverCity, String driverDob, String driverCountry, String driverPincode, String driverPanNo, String driverDriveLicenceNo, String driverAadharNo, String token) {
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.driverEmail = driverEmail;
        this.driverGander = driverGander;
        this.driverState = driverState;
        this.driverCity = driverCity;
        this.driverDob = driverDob;
        this.driverCountry = driverCountry;
        this.driverPincode = driverPincode;
        this.driverPanNo = driverPanNo;
        this.driverDriveLicenceNo = driverDriveLicenceNo;
        this.driverAadharNo = driverAadharNo;
        this.token = token;
    }

    public Driver(String driverName, String driverPhone, String driverEmail, String driverGander, String driverState, String driverCity, String driverDob, String driverCountry, String driverPincode, String driverPanNo, String driverDriveLicenceNo, String driverAadharNo,String vehicle_id, String token) {
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.driverEmail = driverEmail;
        this.driverGander = driverGander;
        this.driverState = driverState;
        this.driverCity = driverCity;
        this.driverDob = driverDob;
        this.driverCountry = driverCountry;
        this.driverPincode = driverPincode;
        this.driverPanNo = driverPanNo;
        this.driverDriveLicenceNo = driverDriveLicenceNo;
        this.driverAadharNo = driverAadharNo;
        this.vehicle_id = vehicle_id;
        this.token = token;
    }


    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public String getDriverGander() {
        return driverGander;
    }

    public void setDriverGander(String driverGander) {
        this.driverGander = driverGander;
    }

    public String getDriverState() {
        return driverState;
    }

    public void setDriverState(String driverState) {
        this.driverState = driverState;
    }

    public String getDriverCity() {
        return driverCity;
    }

    public void setDriverCity(String driverCity) {
        this.driverCity = driverCity;
    }

    public String getDriverDob() {
        return driverDob;
    }

    public void setDriverDob(String driverDob) {
        this.driverDob = driverDob;
    }

    public String getDriverCountry() {
        return driverCountry;
    }

    public void setDriverCountry(String driverCountry) {
        this.driverCountry = driverCountry;
    }

    public String getDriverPincode() {
        return driverPincode;
    }

    public void setDriverPincode(String driverPincode) {
        this.driverPincode = driverPincode;
    }

    public String getDriverPanNo() {
        return driverPanNo;
    }

    public void setDriverPanNo(String driverPanNo) {
        this.driverPanNo = driverPanNo;
    }

    public String getDriverDriveLicenceNo() {
        return driverDriveLicenceNo;
    }

    public void setDriverDriveLicenceNo(String driverDriveLicenceNo) {
        this.driverDriveLicenceNo = driverDriveLicenceNo;
    }

    public String getDriverAadharNo() {
        return driverAadharNo;
    }

    public void setDriverAadharNo(String driverAadharNo) {
        this.driverAadharNo = driverAadharNo;
    }


}
