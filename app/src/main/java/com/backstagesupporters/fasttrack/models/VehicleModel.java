package com.backstagesupporters.fasttrack.models;

import android.animation.TimeInterpolator;

public class VehicleModel {
    private String nameVehicle, rangeNumber, mileage;
    private int image_id;
    private String imagePath;

    public String description;
    public int colorId1;
    public int colorId2;
    public   TimeInterpolator interpolator;



    public VehicleModel(String description, int colorId1, int colorId2, TimeInterpolator interpolator) {
        this.description = description;
        this.colorId1 = colorId1;
        this.colorId2 = colorId2;
        this.interpolator = interpolator;
    }

    public VehicleModel() {
    }


    public VehicleModel(String nameVehicle, String rangeNumber, String mileage, int image_id, TimeInterpolator interpolator) {
        this.nameVehicle = nameVehicle;
        this.rangeNumber = rangeNumber;
        this.mileage = mileage;
        this.image_id = image_id;
        this.interpolator = interpolator;
    }

    public String getNameVehicle() {
        return nameVehicle;
    }

    public void setNameVehicle(String nameVehicle) {
        this.nameVehicle = nameVehicle;
    }

    public String getRangeNumber() {
        return rangeNumber;
    }

    public void setRangeNumber(String rangeNumber) {
        this.rangeNumber = rangeNumber;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getColorId1() {
        return colorId1;
    }

    public void setColorId1(int colorId1) {
        this.colorId1 = colorId1;
    }

    public int getColorId2() {
        return colorId2;
    }

    public void setColorId2(int colorId2) {
        this.colorId2 = colorId2;
    }

    public TimeInterpolator getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        this.interpolator = interpolator;
    }
}
