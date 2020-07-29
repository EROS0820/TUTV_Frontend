package com.backstagesupporters.fasttrack.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DriverEdit {

    @SerializedName("vehicle_id")
    @Expose
    private String vehicle_id;

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("driver_id")
    @Expose
    private String driverId;
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
    @SerializedName("driver_pincode")
    @Expose
    private String driverPincode;
    @SerializedName("driver_country")
    @Expose
    private String driverCountry;
    @SerializedName("driver_pan_no")
    @Expose
    private String driverPanNo;
    @SerializedName("driver_aadhar_no")
    @Expose
    private String driverAadharNo;
    @SerializedName("driver_drive_licence_no")
    @Expose
    private String driverDriveLicenceNo;
    @SerializedName("driver_idproof_image")
    @Expose
    private String driverIdproofImage;
    @SerializedName("driver_pan_image")
    @Expose
    private String driverPanImage;
    @SerializedName("driver_aadhar_image")
    @Expose
    private String driverAadharImage;
    @SerializedName("driver_drive_licence_image")
    @Expose
    private String driverDriveLicenceImage;
    @SerializedName("driver_photo")
    @Expose
    private String driverPhoto;
    @SerializedName("driver_dob")
    @Expose
    private String driverDob;

    @SerializedName("user_id")
    @Expose
    private String userId;


    public DriverEdit() {
    }


    public DriverEdit(String driverName, String driverPhone, String driverCity, String driverDob, String driverCountry, String driverPincode, String driverPanNo, String driverDriveLicenceNo, String driverAadharNo, String token, String driverId,String vehicle_id) {
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.driverCity = driverCity;
        this.driverDob = driverDob;
        this.driverCountry = driverCountry;
        this.driverPincode = driverPincode;
        this.driverPanNo = driverPanNo;
        this.driverDriveLicenceNo = driverDriveLicenceNo;
        this.driverAadharNo = driverAadharNo;
        this.token = token;
        this.driverId = driverId;
        this.vehicle_id = vehicle_id;
    }

    public DriverEdit(String driverName, String driverPhone, String driverEmail, String driverGander, String driverState, String driverCity, String driverDob, String driverCountry, String driverPincode, String driverPanNo, String driverDriveLicenceNo, String driverAadharNo, String token, String driverId) {
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
        this.driverId = driverId;
    }

    public DriverEdit(String driverName, String driverPhone, String driverEmail, String driverGander, String driverState, String driverCity, String driverDob, String driverCountry, String driverPincode, String driverPanNo, String driverDriveLicenceNo, String driverAadharNo,String vehicle_id, String token, String driverId) {
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
        this.driverId = driverId;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
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


    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getDriverIdproofImage() {
        return driverIdproofImage;
    }

    public void setDriverIdproofImage(String driverIdproofImage) {
        this.driverIdproofImage = driverIdproofImage;
    }

    public String getDriverPanImage() {
        return driverPanImage;
    }

    public void setDriverPanImage(String driverPanImage) {
        this.driverPanImage = driverPanImage;
    }

    public String getDriverAadharImage() {
        return driverAadharImage;
    }

    public void setDriverAadharImage(String driverAadharImage) {
        this.driverAadharImage = driverAadharImage;
    }

    public String getDriverDriveLicenceImage() {
        return driverDriveLicenceImage;
    }

    public void setDriverDriveLicenceImage(String driverDriveLicenceImage) {
        this.driverDriveLicenceImage = driverDriveLicenceImage;
    }

    public String getDriverPhoto() {
        return driverPhoto;
    }

    public void setDriverPhoto(String driverPhoto) {
        this.driverPhoto = driverPhoto;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
