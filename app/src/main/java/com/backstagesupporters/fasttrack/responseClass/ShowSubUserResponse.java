package com.backstagesupporters.fasttrack.responseClass;

import com.backstagesupporters.fasttrack.models.SubUser;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShowSubUserResponse {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("users")
    @Expose
    private List<SubUser> users = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<SubUser> getUsers() {
        return users;
    }

    public void setUsers(List<SubUser> users) {
        this.users = users;
    }

}
