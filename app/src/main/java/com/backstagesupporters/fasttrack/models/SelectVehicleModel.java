package com.backstagesupporters.fasttrack.models;

public class SelectVehicleModel {
    private String numberSelectVehicle;
    private String imagePath;
    private int image_id;


    public SelectVehicleModel(String name, int image_id) {
        this.numberSelectVehicle = name;
        this.image_id = image_id;
    }

    public String getNumberSelectVehicle() {
        return numberSelectVehicle;
    }

    public void setNumberSelectVehicle(String numberSelectVehicle) {
        this.numberSelectVehicle = numberSelectVehicle;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }
}
