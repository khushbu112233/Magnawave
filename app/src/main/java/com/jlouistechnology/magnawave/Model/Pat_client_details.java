package com.jlouistechnology.magnawave.Model;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by aipxperts on 16/5/17.
 */

public class Pat_client_details {

    String pat_detail_id;
    String bp_title;
    String bp_desc;
    String bp_img;
    Bitmap pet_img;
    String pat_main_id;
    File img_file;
    String id;
    int x;
    int y;
    String is_flip_image;
    String is_change_point;


    public String getPat_detail_id() {
        return pat_detail_id;
    }

    public void setPat_detail_id(String pat_detail_id) {
        this.pat_detail_id = pat_detail_id;
    }

    public String getBp_title() {
        return bp_title;
    }

    public void setBp_title(String bp_title) {
        this.bp_title = bp_title;
    }

    public String getBp_desc() {
        return bp_desc;
    }

    public void setBp_desc(String bp_desc) {
        this.bp_desc = bp_desc;
    }

    public String getBp_img() {
        return bp_img;
    }

    public void setBp_img(String bp_img) {
        this.bp_img = bp_img;
    }

    public Bitmap getPet_img() {
        return pet_img;
    }

    public void setPet_img(Bitmap pet_img) {
        this.pet_img = pet_img;
    }

    public String getPat_main_id() {
        return pat_main_id;
    }

    public void setPat_main_id(String pat_main_id) {
        this.pat_main_id = pat_main_id;
    }

    public File getImg_file() {
        return img_file;
    }

    public void setImg_file(File img_file) {
        this.img_file = img_file;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getIs_change_point() {
        return is_change_point;
    }

    public void setIs_change_point(String is_change_point) {
        this.is_change_point = is_change_point;
    }
}
