package com.backstagesupporters.fasttrack.models;

public class VehicleTypesModel {
    private String name;
    private int imageIcon;
    private int imageBgId;


    public VehicleTypesModel() { }


    public VehicleTypesModel(String name, int imageIcon, int imageBgId) {
        this.name = name;
        this.imageIcon = imageIcon;
        this.imageBgId = imageBgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(int imageIcon) {
        this.imageIcon = imageIcon;
    }

    public int getImageBgId() {
        return imageBgId;
    }

    public void setImageBgId(int imageBgId) {
        this.imageBgId = imageBgId;
    }
}
