package com.kenbie.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by rajaw on 7/27/2017.
 */

public class ProfileInfo implements Serializable {
    // User info
    private int id;
    private String first_name;
    private String user_name;
    private String social_id;
    private String surname;
    private String user_type;
    private String user_pic;
    private String email_id;
    private String phone;
    private String password;
    private int completedYrs;
    private String birth_month;
    private String birth_day;
    private String birth_year;
    private String company_name;
    private String website;
    private String seen_type;
    private String discipline;
    private String socialType;
    private int gender;
    private int is_active;
    private int is_deleted;
    private int added_by;

    // Profile Info
    private String about_user;
    private String user_ethinicity;
    private String user_face;
    private String user_height;
    private String user_weight;
    private String user_bust;
    private String user_waist;
    private String user_hips;
    private String user_dress;
    private String user_eye_color;
    private String user_shoes;
    private String user_language;
    private String user_hair_color;
    private String user_from_agency;
    private String user_categories;
    private String user_disciplines;
    private String address1;
    private String address2;
    private String country;
    private String city;
    private String postal_code;
    private String fb;
    private String insta;
    private String twitter;
    private String google;
    private String yandex;
    private String youtube;
    private String linkedin;
    private String pinterest;
    private String socialWebsite;
    private String userCountData;
    private String allimgs;
    private String list_options;
    private String arrdisciplines;
    private String categoriesData;
    private String userHairColorsData;
    private String userShoesData;
    private String userDress;
    private String userEyes;
    private String userFace;
    private ArrayList<OptionsData> userDisciplines;
    private ArrayList<OptionsData> userCategories;
    private ArrayList<OptionsData> userLanguages;
    private ArrayList<OptionsData> userSocial;
    private ArrayList<OptionsData> userInfo;
    private ArrayList<OptionsData> galleryList;
    private String userSelLanguage;

    public int getCompletedYrs() {
        return completedYrs;
    }

    public void setCompletedYrs(int completedYrs) {
        this.completedYrs = completedYrs;
    }

    public void setUserSelLanguage(String userSelLanguage) {
        this.userSelLanguage = userSelLanguage;
    }

    public String getUserSelLanguage() {
        return userSelLanguage;
    }


    public String getSocialType() {
        return socialType;
    }

    public void setSocialType(String socialType) {
        this.socialType = socialType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirth_month() {
        return birth_month;
    }

    public void setBirth_month(String birth_month) {
        this.birth_month = birth_month;
    }

    public String getBirth_day() {
        return birth_day;
    }

    public void setBirth_day(String birth_day) {
        this.birth_day = birth_day;
    }

    public ArrayList<OptionsData> getGalleryList() {
        return galleryList;
    }

    public void setGalleryList(ArrayList<OptionsData> galleryList) {
        this.galleryList = galleryList;
    }

    public ArrayList<OptionsData> getUserSocial() {
        return userSocial;
    }

    public void setUserSocial(ArrayList<OptionsData> userSocial) {
        this.userSocial = userSocial;
    }

    public ArrayList<OptionsData> getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(ArrayList<OptionsData> userInfo) {
        this.userInfo = userInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getSocial_id() {
        return social_id;
    }

    public void setSocial_id(String social_id) {
        this.social_id = social_id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getUser_pic() {
        return user_pic;
    }

    public void setUser_pic(String user_pic) {
        this.user_pic = user_pic;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirth_year() {
        return birth_year;
    }

    public void setBirth_year(String birth_year) {
        this.birth_year = birth_year;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getSeen_type() {
        return seen_type;
    }

    public void setSeen_type(String seen_type) {
        this.seen_type = seen_type;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }


    public String getUser_from_agency() {
        return user_from_agency;
    }

    public void setUser_from_agency(String user_from_agency) {
        this.user_from_agency = user_from_agency;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    public int getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(int is_deleted) {
        this.is_deleted = is_deleted;
    }

    public int getAdded_by() {
        return added_by;
    }

    public void setAdded_by(int added_by) {
        this.added_by = added_by;
    }

    public String getAbout_user() {
        return about_user;
    }

    public void setAbout_user(String about_user) {
        this.about_user = about_user;
    }

    public String getUser_ethinicity() {
        return user_ethinicity;
    }

    public void setUser_ethinicity(String user_ethinicity) {
        this.user_ethinicity = user_ethinicity;
    }

    public String getUser_face() {
        return user_face;
    }

    public void setUser_face(String user_face) {
        this.user_face = user_face;
    }

    public String getUser_height() {
        return user_height;
    }

    public void setUser_height(String user_height) {
        this.user_height = user_height;
    }

    public String getUser_weight() {
        return user_weight;
    }

    public void setUser_weight(String user_weight) {
        this.user_weight = user_weight;
    }

    public String getUser_bust() {
        return user_bust;
    }

    public void setUser_bust(String user_bust) {
        this.user_bust = user_bust;
    }

    public String getUser_waist() {
        return user_waist;
    }

    public void setUser_waist(String user_waist) {
        this.user_waist = user_waist;
    }

    public String getUser_hips() {
        return user_hips;
    }

    public void setUser_hips(String user_hips) {
        this.user_hips = user_hips;
    }

    public String getUser_dress() {
        return user_dress;
    }

    public void setUser_dress(String user_dress) {
        this.user_dress = user_dress;
    }

    public String getUser_eye_color() {
        return user_eye_color;
    }

    public void setUser_eye_color(String user_eye_color) {
        this.user_eye_color = user_eye_color;
    }

    public String getUser_shoes() {
        return user_shoes;
    }

    public void setUser_shoes(String user_shoes) {
        this.user_shoes = user_shoes;
    }

    public String getUser_language() {
        return user_language;
    }

    public void setUser_language(String user_language) {
        this.user_language = user_language;
    }

    public String getUser_hair_color() {
        return user_hair_color;
    }

    public void setUser_hair_color(String user_hair_color) {
        this.user_hair_color = user_hair_color;
    }

    public String getUser_categories() {
        return user_categories;
    }

    public void setUser_categories(String user_categories) {
        this.user_categories = user_categories;
    }

    public String getUser_disciplines() {
        return user_disciplines;
    }

    public void setUser_disciplines(String user_disciplines) {
        this.user_disciplines = user_disciplines;
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

    public String getFb() {
        return fb;
    }

    public void setFb(String fb) {
        this.fb = fb;
    }

    public String getInsta() {
        return insta;
    }

    public void setInsta(String insta) {
        this.insta = insta;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getGoogle() {
        return google;
    }

    public void setGoogle(String google) {
        this.google = google;
    }

    public String getYandex() {
        return yandex;
    }

    public void setYandex(String yandex) {
        this.yandex = yandex;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getPinterest() {
        return pinterest;
    }

    public void setPinterest(String pinterest) {
        this.pinterest = pinterest;
    }

    public String getSocialWebsite() {
        return socialWebsite;
    }

    public void setSocialWebsite(String socialWebsite) {
        this.socialWebsite = socialWebsite;
    }
    public String getUserCountData() {
        return userCountData;
    }

    public void setUserCountData(String userCountData) {
        this.userCountData = userCountData;
    }

    public String getAllimgs() {
        return allimgs;
    }

    public void setAllimgs(String allimgs) {
        this.allimgs = allimgs;
    }

    public String getList_options() {
        return list_options;
    }

    public void setList_options(String list_options) {
        this.list_options = list_options;
    }

    public String getArrdisciplines() {
        return arrdisciplines;
    }

    public void setArrdisciplines(String arrdisciplines) {
        this.arrdisciplines = arrdisciplines;
    }

    public String getCategoriesData() {
        return categoriesData;
    }

    public void setCategoriesData(String categoriesData) {
        this.categoriesData = categoriesData;
    }

    public String getUserHairColorsData() {
        return userHairColorsData;
    }

    public void setUserHairColorsData(String userHairColorsData) {
        this.userHairColorsData = userHairColorsData;
    }

    public String getUserShoesData() {
        return userShoesData;
    }

    public void setUserShoesData(String userShoesData) {
        this.userShoesData = userShoesData;
    }

    public String getUserDress() {
        return userDress;
    }

    public void setUserDress(String userDress) {
        this.userDress = userDress;
    }

    public String getUserEyes() {
        return userEyes;
    }

    public void setUserEyes(String userEyes) {
        this.userEyes = userEyes;
    }

    public String getUserFace() {
        return userFace;
    }

    public void setUserFace(String userFace) {
        this.userFace = userFace;
    }


    public ArrayList<OptionsData> getUserDisciplines() {
        return userDisciplines;
    }

    public void setUserDisciplines(ArrayList<OptionsData> userDisciplines) {
        this.userDisciplines = userDisciplines;
    }

    public ArrayList<OptionsData> getUserCategories() {
        return userCategories;
    }

    public void setUserCategories(ArrayList<OptionsData> userCategories) {
        this.userCategories = userCategories;
    }

    public ArrayList<OptionsData> getUserLanguages() {
        return userLanguages;
    }

    public void setUserLanguages(ArrayList<OptionsData> userLanguages) {
        this.userLanguages = userLanguages;
    }
}
