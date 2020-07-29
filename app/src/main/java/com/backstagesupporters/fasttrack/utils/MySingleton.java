package com.backstagesupporters.fasttrack.utils;

import android.net.Uri;

import com.backstagesupporters.fasttrack.models.DriverShow;
import com.backstagesupporters.fasttrack.models.Vehicle;
import com.backstagesupporters.fasttrack.responseClass.HistoryReplyResponse;

import java.util.List;

public class MySingleton {
    private static MySingleton instance;
    private String data;
    private List myListData;
    private DriverShow driverShow;
    private Uri buzzerRington;
    private Uri notifiRington;
    private HistoryReplyResponse historyReplyResponse;
    List<Vehicle> vehicleArrayList;

    private Vehicle vehicle;

    private MySingleton(){
    }

    public static MySingleton getInstance(){
        if (instance == null){
            instance = new MySingleton();
        }
        return instance;
    }
    // getter and setter here

    public String getData() {
        return data;
    }

    public void setData(String data) {
        instance.data = data;
    }

    public List getMyListData() {
        return myListData;
    }

    public void setMyListData(List myListData) {
        instance.myListData = myListData;
    }

    public DriverShow getDriverShow() {
        return driverShow;
    }

    public void setDriverShow(DriverShow driverShow) {
        instance.driverShow = driverShow;
    }

    public Uri getBuzzerRington() {
        return buzzerRington;
    }

    public void setBuzzerRington(Uri buzzerRington) {
        this.buzzerRington = buzzerRington;
    }

    public Uri getNotifiRington() {
        return notifiRington;
    }

    public void setNotifiRington(Uri notifiRington) {
        this.notifiRington = notifiRington;
    }

    public HistoryReplyResponse getHistoryReplyResponse() {
        return historyReplyResponse;
    }

    public void setHistoryReplyResponse(HistoryReplyResponse historyReplyResponse) {
        this.historyReplyResponse = historyReplyResponse;
    }

    public List<Vehicle> getVehicleArrayList() {
        return vehicleArrayList;
    }

    public void setVehicleArrayList(List<Vehicle> vehicleArrayList) {
        this.vehicleArrayList = vehicleArrayList;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
