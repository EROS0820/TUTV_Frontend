package com.backstagesupporters.fasttrack.models;

import android.animation.TimeInterpolator;

public class SuspendModel {
    public String deviceName,deviceSrNumber,licenseNumber,driver,phone ;
    public int image_id;
    public String imagePath;
    public   TimeInterpolator interpolator;


    public SuspendModel() {
    }

    public SuspendModel(String deviceName, String deviceSrNumber, String licenseNumber, String driver, String phone, int image_id) {
        this.deviceName = deviceName;
        this.deviceSrNumber = deviceSrNumber;
        this.licenseNumber = licenseNumber;
        this.driver = driver;
        this.phone = phone;
        this.image_id = image_id;
    }

    public SuspendModel(String deviceName, String deviceSrNumber, String licenseNumber, String driver, String phone, int image_id, TimeInterpolator interpolator) {
        this.deviceName = deviceName;
        this.deviceSrNumber = deviceSrNumber;
        this.licenseNumber = licenseNumber;
        this.driver = driver;
        this.phone = phone;
        this.image_id = image_id;
        this.interpolator = interpolator;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceSrNumber() {
        return deviceSrNumber;
    }

    public void setDeviceSrNumber(String deviceSrNumber) {
        this.deviceSrNumber = deviceSrNumber;
    }


    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    public TimeInterpolator getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        this.interpolator = interpolator;
    }
}
