package com.backstagesupporters.fasttrack.models;

import android.animation.TimeInterpolator;

public class HomeModel2 {
    private String titleHead1,titleHead2, title1,title2,title3;
    private int image_id;
    private String imagePath;

    public String description;
    public int colorId1;
    public int colorId2;
    public   TimeInterpolator interpolator;



    public HomeModel2(String description, int colorId1, int colorId2, TimeInterpolator interpolator) {
        this.description = description;
        this.colorId1 = colorId1;
        this.colorId2 = colorId2;
        this.interpolator = interpolator;
    }

    public HomeModel2() {
    }


    public HomeModel2(String titleHead1, String titleHead2, String title1, String title2, String title3, int image_id, TimeInterpolator interpolator) {
        this.titleHead1 = titleHead1;
        this.titleHead2 = titleHead2;
        this.title1 = title1;
        this.title2 = title2;
        this.title3 = title3;
        this.image_id = image_id;
        this.interpolator = interpolator;
    }

    public String getTitleHead1() {
        return titleHead1;
    }

    public void setTitleHead1(String titleHead1) {
        this.titleHead1 = titleHead1;
    }

    public String getTitleHead2() {
        return titleHead2;
    }

    public void setTitleHead2(String titleHead2) {
        this.titleHead2 = titleHead2;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getTitle2() {
        return title2;
    }

    public void setTitle2(String title2) {
        this.title2 = title2;
    }

    public String getTitle3() {
        return title3;
    }

    public void setTitle3(String title3) {
        this.title3 = title3;
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
