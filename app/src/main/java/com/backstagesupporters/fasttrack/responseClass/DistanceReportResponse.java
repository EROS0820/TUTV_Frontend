package com.backstagesupporters.fasttrack.responseClass;

import android.os.Parcel;
import android.os.Parcelable;

import com.backstagesupporters.fasttrack.models.DistanceReport;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.util.ArrayList;
import java.util.List;

public class DistanceReportResponse {

    @SerializedName("total")
    @Expose
    private String total;
    @SerializedName("data")
    @Expose
//    private List<Datum> data = null;
    private List<DistanceReport> data = null;
    @SerializedName("from_date")
    @Expose
    private String fromDate;
    @SerializedName("to_date")
    @Expose
    private String toDate;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("report")
    @Expose
    private String report;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<DistanceReport> getData() {
        return data;
    }

    public void setData(List<DistanceReport> data) {
        this.data = data;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

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

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}