package com.backstagesupporters.fasttrack.responseClass;


import com.backstagesupporters.fasttrack.models.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    //   "success": false,- "success": true,
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private Data data;

    // when account block
    @SerializedName("account_status")
    @Expose
    private String accountStatus;
    @SerializedName("error")
    @Expose
    private String error;



    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


    // =====================================

    public static class Data {
        @SerializedName("token")
        @Expose
        private String token;

        //private ArrayList<> user;
        @SerializedName("user")
        @Expose
        private User user;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }




}
