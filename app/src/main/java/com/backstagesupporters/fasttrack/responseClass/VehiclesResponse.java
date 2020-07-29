package com.backstagesupporters.fasttrack.responseClass;

import com.backstagesupporters.fasttrack.models.Vehicle;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VehiclesResponse {

    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("vehicles")
    @Expose
    private List<Vehicle> vehicles = null;
    @SerializedName("message")
    @Expose
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
