package com.kenbie.model;

import java.io.Serializable;

/**
 * Created by rajaw on 9/21/2017.
 */

public class CastingUser implements Serializable{
    private String first_name;
    private String user_pic;
    private String birth_day;
    private String birth_month;
    private String birth_year;
    private int id;
    private int user_id;
    private String casting_title;
    private String casting_requirement;
    private String casting_type;
    private String casting_location;
    private String casting_start_age;
    private String casting_end_age;
    private String casting_start_time;
    private String casting_end_time;
    private String casting_fee;
    private String casting_gender;
    private String casting_start_date;
    private String casting_end_date;
    private String casting_categories;
    private String casting_img;
    private int is_paid;
    private int is_deleted;
    private String address1;
    private String address2;
    private String country;
    private String city;
    private String postal_code;
    private String casting_address;
    private int applied;

    public int getApplied() {
        return applied;
    }

    public void setApplied(int applied) {
        this.applied = applied;
    }

    public String getCasting_address() {
        return casting_address;
    }

    public void setCasting_address(String casting_address) {
        this.casting_address = casting_address;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getUser_pic() {
        return user_pic;
    }

    public void setUser_pic(String user_pic) {
        this.user_pic = user_pic;
    }

    public String getBirth_day() {
        return birth_day;
    }

    public void setBirth_day(String birth_day) {
        this.birth_day = birth_day;
    }

    public String getBirth_month() {
        return birth_month;
    }

    public void setBirth_month(String birth_month) {
        this.birth_month = birth_month;
    }

    public String getBirth_year() {
        return birth_year;
    }

    public void setBirth_year(String birth_year) {
        this.birth_year = birth_year;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getCasting_title() {
        return casting_title;
    }

    public void setCasting_title(String casting_title) {
        this.casting_title = casting_title;
    }

    public String getCasting_requirement() {
        return casting_requirement;
    }

    public void setCasting_requirement(String casting_requirement) {
        this.casting_requirement = casting_requirement;
    }

    public String getCasting_type() {
        return casting_type;
    }

    public void setCasting_type(String casting_type) {
        this.casting_type = casting_type;
    }

    public String getCasting_location() {
        return casting_location;
    }

    public void setCasting_location(String casting_location) {
        this.casting_location = casting_location;
    }

    public String getCasting_start_age() {
        return casting_start_age;
    }

    public void setCasting_start_age(String casting_start_age) {
        this.casting_start_age = casting_start_age;
    }

    public String getCasting_end_age() {
        return casting_end_age;
    }

    public void setCasting_end_age(String casting_end_age) {
        this.casting_end_age = casting_end_age;
    }

    public String getCasting_start_time() {
        return casting_start_time;
    }

    public void setCasting_start_time(String casting_start_time) {
        this.casting_start_time = casting_start_time;
    }

    public String getCasting_end_time() {
        return casting_end_time;
    }

    public void setCasting_end_time(String casting_end_time) {
        this.casting_end_time = casting_end_time;
    }

    public String getCasting_fee() {
        return casting_fee;
    }

    public void setCasting_fee(String casting_fee) {
        this.casting_fee = casting_fee;
    }

    public String getCasting_gender() {
        return casting_gender;
    }

    public void setCasting_gender(String casting_gender) {
        this.casting_gender = casting_gender;
    }

    public String getCasting_start_date() {
        return casting_start_date;
    }

    public void setCasting_start_date(String casting_start_date) {
        this.casting_start_date = casting_start_date;
    }

    public String getCasting_end_date() {
        return casting_end_date;
    }

    public void setCasting_end_date(String casting_end_date) {
        this.casting_end_date = casting_end_date;
    }

    public String getCasting_categories() {
        return casting_categories;
    }

    public void setCasting_categories(String casting_categories) {
        this.casting_categories = casting_categories;
    }

    public String getCasting_img() {
        return casting_img;
    }

    public void setCasting_img(String casting_img) {
        this.casting_img = casting_img;
    }

    public int getIs_paid() {
        return is_paid;
    }

    public void setIs_paid(int is_paid) {
        this.is_paid = is_paid;
    }

    public int getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(int is_deleted) {
        this.is_deleted = is_deleted;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }
}
