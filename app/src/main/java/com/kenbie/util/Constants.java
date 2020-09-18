package com.kenbie.util;

/**
 * Created by rajaw on 5/24/2017.
 */

public class Constants {
    public static final int CAMERA_CLICK = 1;
    public static final int GALLERY_CLICK = 2;
    public static final int CAMERA_MANUAL = 5;
    public static final int CAMERA_CANCEL = 6;
    public static final int INSTA_REDIRECT_URL = 7;

    public static String NETWORK_FAIL_MSG = "Network failed! Please try later.";
    public static String GENERAL_FAIL_MSG = "Something Wrong! Please try later.";
    public static String API_KEY = "7Pnd1lKNqfOte9XdHoQSBUYip";
    public static String API_SECRET = "Kf6wlWEsCelKav4XyCBXZNseae9V09OF6WoHJDwERJvl9EFtab";
    public static String CALLBACKURL = "http://kenbie.com/";
    //    public static String BASE_IMAGE_URL = "http://kenbie.com/uploads/users/thumbnail/"; - old
//    public static String BASE_IMAGE_URL = "https://s3.eu-central-1.amazonaws.com/kenbie.uploads/users/thumbnail/";
    public static String BASE_MY_CASTING_IMAGE_URL = "https://d3jgxcdxnzlzyv.cloudfront.net/casting/thumbnail/";
    public static String IMAGE_LARGE_BASE_URL = "https://s3.eu-central-1.amazonaws.com/kenbie.uploads/users/";
    public static String IMAGE_BASE_URL = "https://s3.eu-central-1.amazonaws.com/kenbie.uploads/users/thumbnail/";
    public static String IMAGE_CASTING_BASE_URL = "https://s3.eu-central-1.amazonaws.com/kenbie.uploads/casting/thumbnail/";
    public static final String PRIVACY_URL = "https://kenbie.com/cron/mobileprivacy";
    public static final String TERMS_URL = "https://kenbie.com/cron/mobileterms";



    /*---------------- Previous images base urls -----------------*/
//    public static String SPONSOR_BASE_IMAGE_URL = "https://kenbie.com/uploaded/ads_images/";
//    public static String BASE_IMAGE_URL = "https://d3jgxcdxnzlzyv.cloudfront.net/users/thumbnail/";

    /*---------------- New images base urls -----------------*/
    public static String SPONSOR_BASE_IMAGE_URL = "https://kenbie.com/uploaded/ads_images/";
    public static String BASE_IMAGE_URL = "https://s3.eu-central-1.amazonaws.com/kenbie.uploads/users/thumbnail/";
    public static String PROFILE_BASE_IMAGE_URL = "https://d3jgxcdxnzlzyv.cloudfront.net/users/thumbnail/";

    /*---------Dev Payment Details-------------*/
//    public static final String MERCHANT_ID = "1100017737"; // Datatrans merchant ID 1100017737
//    public static final String CURRENCY_CODE = "EUR";
//    public static final String SIGNATURE = "190408134557665557";

    /*---------Live Payment Details-------------*/
    public static final String MERCHANT_ID = "3000014622"; // Datatrans merchant ID 1100017737
    public static final String CURRENCY_CODE = "EUR";
    public static final String SIGNATURE = "200820235122805839";
//    public static final String SIGNATURE = "190611175100694424";

    // TODO - before publish
    public static final boolean PAYMENT_TEST_MODE = false;
}
