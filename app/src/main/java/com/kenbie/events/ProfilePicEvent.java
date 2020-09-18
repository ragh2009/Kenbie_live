package com.kenbie.events;

public class ProfilePicEvent {
    String imageUrl;

    public ProfilePicEvent(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
