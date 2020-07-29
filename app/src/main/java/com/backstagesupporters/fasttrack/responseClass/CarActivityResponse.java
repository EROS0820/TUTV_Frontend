package com.backstagesupporters.fasttrack.responseClass;


import com.backstagesupporters.fasttrack.models.CarAllActivity;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class CarActivityResponse {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("caractivity")
    @Expose
    private List<CarAllActivity> caractivity = null;

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

    public List<CarAllActivity> getCaractivity() {
        return caractivity;
    }

    public void setCaractivity(List<CarAllActivity> caractivity) {
        this.caractivity = caractivity;
    }


}