package com.kenbie.listeners;

import com.kenbie.model.LeftItem;

public interface NavigationDrawerCallbacks {
    void onNavigationDrawerItemSelected(LeftItem data);
    void viewSettings();
    void viewEditProfile();
}