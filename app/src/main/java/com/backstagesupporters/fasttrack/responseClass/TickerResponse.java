package com.backstagesupporters.fasttrack.responseClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TickerResponse {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("ticker")
    @Expose
    private String ticker;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
