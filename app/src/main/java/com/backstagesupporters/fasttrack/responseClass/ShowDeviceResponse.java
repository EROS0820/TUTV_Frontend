package com.backstagesupporters.fasttrack.responseClass;

import com.backstagesupporters.fasttrack.models.DeviceShow;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShowDeviceResponse {

    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("devices")
    @Expose
    private List<DeviceShow> devices = null;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<DeviceShow> getDevices() {
        return devices;
    }

    public void setDevices(List<DeviceShow> devices) {
        this.devices = devices;
    }

}
