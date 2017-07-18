package com.jlouistechnology.magnawave.Model;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by aipxperts on 11/5/17.
 */

public class Pat_details {

    int id;
    String heading;
    String description;
    Bitmap bmp_image;
    File file;
    int x;
    int y;
    String is_flip_image;

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Bitmap getBmp_image() {
        return bmp_image;
    }

    public void setBmp_image(Bitmap bmp_image) {
        this.bmp_image = bmp_image;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getIs_flip_image() {
        return is_flip_image;
    }

    public void setIs_flip_image(String is_flip_image) {
        this.is_flip_image = is_flip_image;
    }
}
