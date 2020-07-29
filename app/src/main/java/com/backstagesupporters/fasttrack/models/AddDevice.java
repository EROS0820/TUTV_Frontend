package com.backstagesupporters.fasttrack.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddDevice {
    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("deviceSrNumber")
    @Expose
    private String deviceSrNumber;

    @SerializedName("sim_no")
    @Expose
    private String sim_no;


    public AddDevice(String token, String deviceSrNumber, String sim_no) {
        this.token = token;
        this.deviceSrNumber = deviceSrNumber;
        this.sim_no = sim_no;
    }

}
