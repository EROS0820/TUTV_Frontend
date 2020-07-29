package com.backstagesupporters.fasttrack.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubUser2 {

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("mobile")
    @Expose
    private String mobile;

    @SerializedName("vehicle_id")
    @Expose
    private String vehicle_id;

    // token,name,email,password,mobile
    public SubUser2(String token, String name, String email, String password, String mobile) {
        this.token = token;
        this.name = name;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
    }

    public SubUser2(String token, String name, String email, String password, String mobile, String vehicle_id) {
        this.token = token;
        this.name = name;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
        this.vehicle_id = vehicle_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }
}
