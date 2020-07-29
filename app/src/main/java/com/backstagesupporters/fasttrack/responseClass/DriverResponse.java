package com.backstagesupporters.fasttrack.responseClass;

import com.backstagesupporters.fasttrack.models.Driver;
import com.backstagesupporters.fasttrack.models.DriverShow;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DriverResponse {

    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("driver")
    @Expose
    private Driver driver;


    @SerializedName("drivers")
    @Expose
    private List<DriverShow> drivers = null;

    @SerializedName("message")
    @Expose
    private String message;

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


    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }


    public List<DriverShow> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<DriverShow> drivers) {
        this.drivers = drivers;
    }

}
