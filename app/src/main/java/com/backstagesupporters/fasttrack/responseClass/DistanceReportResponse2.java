package com.backstagesupporters.fasttrack.responseClass;

import android.os.Parcel;
import android.os.Parcelable;

import com.backstagesupporters.fasttrack.models.DistanceReport;

import java.util.ArrayList;
import java.util.List;

public class DistanceReportResponse2 implements Parcelable {

    private String total;

    private List<DistanceReport> data = null;

    private String fromDate;

    private String toDate;

    private int status;

    private String message;



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





    protected DistanceReportResponse2(Parcel in) {
        total = in.readString();
        if (in.readByte() == 0x01) {
            data = new ArrayList<DistanceReport>();
            in.readList(data, DistanceReport.class.getClassLoader());
        } else {
            data = null;
        }
        fromDate = in.readString();
        toDate = in.readString();
        status = in.readInt();
        message = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(total);
        if (data == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(data);
        }
        dest.writeString(fromDate);
        dest.writeString(toDate);
        dest.writeInt(status);
        dest.writeString(message);
    }

    @SuppressWarnings("unused")
    public static final Creator<DistanceReportResponse2> CREATOR = new Creator<DistanceReportResponse2>() {
        @Override
        public DistanceReportResponse2 createFromParcel(Parcel in) {
            return new DistanceReportResponse2(in);
        }

        @Override
        public DistanceReportResponse2[] newArray(int size) {
            return new DistanceReportResponse2[size];
        }
    };
}