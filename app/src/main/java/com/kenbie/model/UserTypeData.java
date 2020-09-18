package com.kenbie.model;

import java.io.Serializable;
import java.util.ArrayList;

public class UserTypeData implements Serializable {
    private String type;
    private ArrayList<UserItem> userItems;
    private int totalRecord;
    private int displayCount;
    private int dType;

    public int getdType() {
        return dType;
    }

    public void setdType(int dType) {
        this.dType = dType;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<UserItem> getUserItems() {
        return userItems;
    }

    public void setUserItems(ArrayList<UserItem> userItems) {
        this.userItems = userItems;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        this.totalRecord = totalRecord;
    }

    public int getDisplayCount() {
        return displayCount;
    }

    public void setDisplayCount(int displayCount) {
        this.displayCount = displayCount;
    }
}
