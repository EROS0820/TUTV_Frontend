package com.backstagesupporters.fasttrack.responseClass;


import com.backstagesupporters.fasttrack.models.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LogoutResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("account_status")
    @Expose
    private String accountStatus;
    @SerializedName("error")
    @Expose
    private String error;





}
