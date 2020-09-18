package com.kenbie.model;

import java.io.Serializable;

/**
 * Created by rajaw on 6/13/2017.
 */

public class LeftItem implements Serializable{
    private String title;
    private int image;
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

}
