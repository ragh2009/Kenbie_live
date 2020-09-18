package com.kenbie.events;

public class TitleBarEvent {
    String title;
    boolean isSearchEnable;
    boolean backEnable;

    public TitleBarEvent(String title, boolean isSearchEnable, boolean backEnable) {
        this.title = title;
        this.isSearchEnable = isSearchEnable;
        this.backEnable = backEnable;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSearchEnable() {
        return isSearchEnable;
    }

    public boolean isBackEnable() {
        return backEnable;
    }
}
