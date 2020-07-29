package com.backstagesupporters.fasttrack.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Support {

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("support")
    @Expose
    private String support;

    public Support(){}

    public Support(String name, String phone, String support) {
        this.name = name;
        this.phone = phone;
        this.support = support;
    }

    public Support(String token,String support, String name, String phone) {
        this.token = token;
        this.name = name;
        this.phone = phone;
        this.support = support;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        this.support = support;
    }
}
