package com.backstagesupporters.fasttrack.models;

import android.animation.TimeInterpolator;

public class HeadMenuModel {
    private String title;
    private int image_id;
    public   TimeInterpolator interpolator;

    public HeadMenuModel(String title, int image_id, TimeInterpolator interpolator) {
        this.title = title;
        this.image_id = image_id;
        this.interpolator = interpolator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public TimeInterpolator getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(TimeInterpolator interpolator) {
        this.interpolator = interpolator;
    }
}
