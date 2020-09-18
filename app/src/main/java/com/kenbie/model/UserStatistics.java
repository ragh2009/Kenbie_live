package com.kenbie.model;

import java.util.ArrayList;

public class UserStatistics {
    private int profile_visitor;
    private int notifications;
    private int message;
    private int profile_view;
    private int personal_detail;
    private int photo_upload;
    private int privacy_section;
    private String credit;
    private int profile_complete;
    private ArrayList<Option> report;

    public int getProfile_visitor() {
        return profile_visitor;
    }

    public void setProfile_visitor(int profile_visitor) {
        this.profile_visitor = profile_visitor;
    }

    public int getNotifications() {
        return notifications;
    }

    public void setNotifications(int notifications) {
        this.notifications = notifications;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public int getProfile_view() {
        return profile_view;
    }

    public void setProfile_view(int profile_view) {
        this.profile_view = profile_view;
    }

    public int getPersonal_detail() {
        return personal_detail;
    }

    public void setPersonal_detail(int personal_detail) {
        this.personal_detail = personal_detail;
    }

    public int getPhoto_upload() {
        return photo_upload;
    }

    public void setPhoto_upload(int photo_upload) {
        this.photo_upload = photo_upload;
    }

    public int getPrivacy_section() {
        return privacy_section;
    }

    public void setPrivacy_section(int privacy_section) {
        this.privacy_section = privacy_section;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public int getProfile_complete() {
        return profile_complete;
    }

    public void setProfile_complete(int profile_complete) {
        this.profile_complete = profile_complete;
    }

    public ArrayList<Option> getReport() {
        return report;
    }

    public void setReport(ArrayList<Option> report) {
        this.report = report;
    }
}
