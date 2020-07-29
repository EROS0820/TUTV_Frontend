package com.backstagesupporters.fasttrack.responseClass;

import com.backstagesupporters.fasttrack.models.SubUser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubUserResponse {

    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("sub_user")
    @Expose
    private SubUser subUser;

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

    public SubUser getSubUser() {
        return subUser;
    }

    public void setSubUser(SubUser subUser) {
        this.subUser = subUser;
    }




}
