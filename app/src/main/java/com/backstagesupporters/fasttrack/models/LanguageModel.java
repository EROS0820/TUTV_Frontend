package com.backstagesupporters.fasttrack.models;

public class LanguageModel {
    private String nameLanguage;
    private String imagePath;
    private int image_id;
    private boolean selected;


    public LanguageModel() { }

    public LanguageModel(String nameLanguage, int image_id) {
        this.nameLanguage = nameLanguage;
        this.image_id = image_id;
        selected = false;
    }



    public String getNameLanguage() {
        return nameLanguage;
    }

    public void setNameLanguage(String nameLanguage) {
        this.nameLanguage = nameLanguage;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
