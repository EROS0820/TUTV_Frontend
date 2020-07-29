package com.backstagesupporters.fasttrack.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyLocation {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("location_date")
    @Expose
    private String locationDate;
    @SerializedName("location_time")
    @Expose
    private String locationTime;
    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("direction")
    @Expose
    private String direction;
    @SerializedName("speed")
    @Expose
    private String speed;
    @SerializedName("mcc")
    @Expose
    private String mcc;
    @SerializedName("mnc")
    @Expose
    private String mnc;
    @SerializedName("lac")
    @Expose
    private String lac;
    @SerializedName("cellid")
    @Expose
    private String cellid;
    @SerializedName("gps_length")
    @Expose
    private String gpsLength;
    @SerializedName("satellites")
    @Expose
    private String satellites;
    @SerializedName("terminal")
    @Expose
    private String terminal;
    @SerializedName("voltage")
    @Expose
    private String voltage;
    @SerializedName("gsm")
    @Expose
    private String gsm;
    @SerializedName("alarm")
    @Expose
    private String alarm;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("vehicle_type")
    @Expose
    private String type;
    @SerializedName("signal_strength")
    @Expose
    private String signalStrength;
    @SerializedName("position")
    @Expose
    private String position;
    @SerializedName("position_color")
    @Expose
    private String positionColor;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("parking_status")
    @Expose
    private String parkingStatus;
    @SerializedName("engine_status")
    @Expose
    private String engineStatus;
    @SerializedName("duration")
    @Expose
    private String duration;

    // TODO: 5/14/2020
    @SerializedName("status_from")
    @Expose
    private String statusFrom;
    @SerializedName("todaysdistance")
    @Expose
    private String todaysdistance;
    @SerializedName("average_speed")
    @Expose
    private String averageSpeed;
    @SerializedName("max_speed")
    @Expose
    private String maxSpeed;
    @SerializedName("address")
    @Expose
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocationDate() {
        return locationDate;
    }

    public void setLocationDate(String locationDate) {
        this.locationDate = locationDate;
    }

    public String getLocationTime() {
        return locationTime;
    }

    public void setLocationTime(String locationTime) {
        this.locationTime = locationTime;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getLac() {
        return lac;
    }

    public void setLac(String lac) {
        this.lac = lac;
    }

    public String getCellid() {
        return cellid;
    }

    public void setCellid(String cellid) {
        this.cellid = cellid;
    }

    public String getGpsLength() {
        return gpsLength;
    }

    public void setGpsLength(String gpsLength) {
        this.gpsLength = gpsLength;
    }

    public String getSatellites() {
        return satellites;
    }

    public void setSatellites(String satellites) {
        this.satellites = satellites;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getGsm() {
        return gsm;
    }

    public void setGsm(String gsm) {
        this.gsm = gsm;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(String signalStrength) {
        this.signalStrength = signalStrength;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPositionColor() {
        return positionColor;
    }

    public void setPositionColor(String positionColor) {
        this.positionColor = positionColor;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getParkingStatus() {
        return parkingStatus;
    }

    public void setParkingStatus(String parkingStatus) {
        this.parkingStatus = parkingStatus;
    }

    public String getEngineStatus() {
        return engineStatus;
    }

    public void setEngineStatus(String engineStatus) {
        this.engineStatus = engineStatus;
    }

    public String getStatusFrom() {
        return statusFrom;
    }

    public void setStatusFrom(String statusFrom) {
        this.statusFrom = statusFrom;
    }

    public String getTodaysdistance() {
        return todaysdistance;
    }

    public void setTodaysdistance(String todaysdistance) {
        this.todaysdistance = todaysdistance;
    }

    public String getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(String averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public String getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(String maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
}
