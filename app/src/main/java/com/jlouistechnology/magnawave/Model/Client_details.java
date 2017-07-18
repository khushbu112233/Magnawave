package com.jlouistechnology.magnawave.Model;

import java.util.ArrayList;

/**
 * Created by aipxperts on 16/5/17.
 */

public class Client_details {

    String pet_id;
    String client_id;
    String user_id;
    String cat_name;
    String pet_name;
    String pet_img;
    String pet_swip_img;
    String created;
    String dates;
    String time_info;
    String time_info_visible;
    String screen_width;
    String screen_height;

    ArrayList<Pat_client_details> pat_client_detailsArrayList=new ArrayList<>();

    public String getPet_id() {
        return pet_id;
    }

    public void setPet_id(String pet_id) {
        this.pet_id = pet_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getPet_name() {
        return pet_name;
    }

    public void setPet_name(String pet_name) {
        this.pet_name = pet_name;
    }

    public String getPet_img() {
        return pet_img;
    }

    public void setPet_img(String pet_img) {
        this.pet_img = pet_img;
    }

    public String getPet_swip_img() {
        return pet_swip_img;
    }

    public void setPet_swip_img(String pet_swip_img) {
        this.pet_swip_img = pet_swip_img;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public String getTime_info() {
        return time_info;
    }

    public void setTime_info(String time_info) {
        this.time_info = time_info;
    }

    public ArrayList<Pat_client_details> getPat_client_detailsArrayList() {
        return pat_client_detailsArrayList;
    }

    public void setPat_client_detailsArrayList(ArrayList<Pat_client_details> pat_client_detailsArrayList) {
        this.pat_client_detailsArrayList = pat_client_detailsArrayList;
    }

    public String getTime_info_visible() {
        return time_info_visible;
    }

    public void setTime_info_visible(String time_info_visible) {
        this.time_info_visible = time_info_visible;
    }

    public String getScreen_width() {
        return screen_width;
    }

    public void setScreen_width(String screen_width) {
        this.screen_width = screen_width;
    }

    public String getScreen_height() {
        return screen_height;
    }

    public void setScreen_height(String screen_height) {
        this.screen_height = screen_height;
    }
}
