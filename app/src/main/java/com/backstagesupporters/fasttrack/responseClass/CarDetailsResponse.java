package com.backstagesupporters.fasttrack.responseClass;

import com.backstagesupporters.fasttrack.models.VehicleDetails;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CarDetailsResponse {


    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("vehicle_details")
    @Expose
    private VehicleDetails vehicleDetails;
    @SerializedName("message")
    @Expose
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public VehicleDetails getVehicleDetails() {
        return vehicleDetails;
    }

    public void setVehicleDetails(VehicleDetails vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
