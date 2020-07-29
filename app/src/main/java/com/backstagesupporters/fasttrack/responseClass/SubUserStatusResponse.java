package com.backstagesupporters.fasttrack.responseClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubUserStatusResponse {

    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("sub_user_status")
    @Expose
    private int subUserStatus;

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

    public int getSubUserStatus() {
        return subUserStatus;
    }

    public void setSubUserStatus(int subUserStatus) {
        this.subUserStatus = subUserStatus;
    }


}