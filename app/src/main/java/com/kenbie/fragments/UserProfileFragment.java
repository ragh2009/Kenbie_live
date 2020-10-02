package com.kenbie.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Html;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.kenbie.KenbieApplication;
import com.kenbie.LoginActivity;
import com.kenbie.MediaFullScreenActivity;
import com.kenbie.R;
import com.kenbie.adapters.ProfileViewStepsAdapter;
import com.kenbie.adapters.ViewPagerAdapter;
import com.kenbie.connection.MConnection;
import com.kenbie.data.ProfileDataParser;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.listeners.ProfileOptionListener;
import com.kenbie.model.OptionsData;
import com.kenbie.model.ProfileInfo;
import com.kenbie.model.UserItem;
import com.kenbie.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.WIFI_SERVICE;

public class UserProfileFragment extends BaseFragment implements APIResponseHandler, View.OnClickListener, ProfileOptionListener {
    private UserItem userItem;
    private boolean isEdit;
    private ProfileInfo profileInfo;
    private ArrayList<OptionsData> myDisplayData;
    private TextView userName, userLocation, userLikes, userFav, reportBtn;
    private LinearLayout editProfile, infoLayout;
    private ImageView editIcon, userShare;
    private RecyclerView rvOptions;
    private ViewPager galleryPager;
    private LinearLayout sliderLayout;
    private int dotsCount, type = 0;
    private ImageView[] dots;
    private NestedScrollView profileView;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            KenbieApplication.galleryIndex = 0;
            userItem = (UserItem) getArguments().getSerializable("UserData");
            if (userItem != null && (userItem.getId() == Integer.valueOf(mActivity.mPref.getString("UserId", "0"))))
                isEdit = true;
            type = getArguments().getInt("NavType", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        mActivity.setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mActivity.navigationView.setVisibility(View.VISIBLE);
                mActivity.onBackPressed();
            }
        });

        ActionBar mActionBar = mActivity.getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowHomeEnabled(true);
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_v_back);
        }

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setExpandedTitleColor(mActivity.getResources().getColor(R.color.transparent));
        collapsingToolbarLayout.setCollapsedTitleTextColor(mActivity.getResources().getColor(R.color.black));
        AppBarLayout appBarLayout = (AppBarLayout) view.findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    if (profileInfo.getUser_type() != null && !profileInfo.getUser_type().equalsIgnoreCase("2")) {
                        if (profileInfo.getCompletedYrs() != 0)
                            collapsingToolbarLayout.setTitle(profileInfo.getFirst_name() + ", " + profileInfo.getCompletedYrs());
                        else
                            collapsingToolbarLayout.setTitle(profileInfo.getFirst_name());
                    } else if (profileInfo.getCompany_name() != null && !profileInfo.getCompany_name().equalsIgnoreCase("null")) {
                        if (profileInfo.getCompletedYrs() != 0)
                            collapsingToolbarLayout.setTitle(profileInfo.getCompany_name() + ", " + profileInfo.getCompletedYrs());
                        else
                            collapsingToolbarLayout.setTitle(profileInfo.getCompany_name());
                    }
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");//careful there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });


        profileView = view.findViewById(R.id.profile_view);

        galleryPager = view.findViewById(R.id.gallery_pager);
        sliderLayout = view.findViewById(R.id.slider_dots);

//        RelativeLayout imageLayout = (RelativeLayout) view.findViewById(R.id.image_layout);
//        imageLayout.setBackgroundColor(Utility.getColorWithAlpha(Color.BLACK, 0.2f));

        userName = (TextView) view.findViewById(R.id.user_name);
        userName.setTypeface(KenbieApplication.S_NORMAL);
        userLocation = (TextView) view.findViewById(R.id.user_location);
        userLocation.setTypeface(KenbieApplication.S_NORMAL);
        userLikes = (TextView) view.findViewById(R.id.user_likes);
        userLikes.setTypeface(KenbieApplication.S_NORMAL);
        userFav = (TextView) view.findViewById(R.id.user_fav);
        userFav.setTypeface(KenbieApplication.S_NORMAL);
        reportBtn = (TextView) view.findViewById(R.id.report_btn);
        reportBtn.setText(mActivity.mPref.getString("217", "Report Abuse"));
        reportBtn.setTypeface(KenbieApplication.S_SEMI_BOLD);
        reportBtn.setOnClickListener(this);
        userShare = (ImageView) view.findViewById(R.id.user_share);
        infoLayout = (LinearLayout) view.findViewById(R.id.info_layout);
        editProfile = (LinearLayout) view.findViewById(R.id.btn_edit);
        editProfile.setOnClickListener(this);
        editIcon = (ImageView) view.findViewById(R.id.edit_icon);

        if (!isEdit) {
            userFav.setVisibility(View.VISIBLE);
            userLikes.setVisibility(View.VISIBLE);
            reportBtn.setVisibility(View.VISIBLE);

            userLikes.setOnClickListener(this);
            userFav.setOnClickListener(this);
            reportBtn.setOnClickListener(this);
//            editProfile.setText(getString(R.string.chat_now_title));
            editIcon.setBackgroundResource(R.drawable.ic_chat_w);
        } else {
            userFav.setVisibility(View.INVISIBLE);
            userLikes.setVisibility(View.INVISIBLE);
            reportBtn.setVisibility(View.INVISIBLE);
            editIcon.setBackgroundResource(R.drawable.ic_edit);
        }

        userShare.setOnClickListener(this);

        if (mActivity.mPref.getBoolean("isLogin", false)) {
            editProfile.setVisibility(View.VISIBLE);
//            reportBtn.setVisibility(View.VISIBLE);
        } else {
            editProfile.setVisibility(View.GONE);
//            reportBtn.setVisibility(View.GONE);
        }

        rvOptions = (RecyclerView) view.findViewById(R.id.rv_options);
//        rvOptions.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false);
        rvOptions.setLayoutManager(linearLayoutManager);

        getUserProfileDetails();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_share:
                if (mActivity.mPref.getBoolean("isLogin", false))
                    if (mActivity.mPref.getInt("MemberShip", 0) != 0)
                        mActivity.shareApp(profileInfo.getUser_name());
                    else
                        mActivity.showMemberShipInfo(userItem, 1);
                else
                    startApp();
                break;
            case R.id.user_fav:
                if (mActivity.mPref.getBoolean("isLogin", false)) {
                    if (mActivity.mPref.getInt("MemberShip", 0) != 0) {
                        int userF = 0;
                        if (userFav.getTag() != null)
                            userF = (int) userFav.getTag();

//                        if (userF == 0)
                        addedUserFavorites();
                    } else
                        mActivity.showMemberShipInfo(userItem, 1);
                } else
                    startApp();
                break;
            case R.id.user_likes:
                if (mActivity.mPref.getBoolean("isLogin", false)) {
                    if (mActivity.mPref.getInt("MemberShip", 0) != 0) {
                        int userLike = 0;
                        if (userLikes.getTag() != null)
                            userLike = (int) userLikes.getTag();
                        if (userLike == 0)
                            updateUserLike();
                    } else
                        mActivity.showMemberShipInfo(userItem, 1);
                } else
                    startApp();
                break;
            case R.id.report_btn:
                if (mActivity.mPref.getBoolean("isLogin", false))
                    mActivity.userReportAbuse(profileInfo);
                else
                    startApp();
                break;
            case R.id.btn_edit:
                if (mActivity.mPref.getBoolean("isLogin", false)) {
                    if (mActivity.mPref.getInt("MemberShip", 0) != 0) {
                        if (isEdit)
                            mActivity.seeEditOptions(profileInfo);
                        else
                            mActivity.startMessaging(profileInfo);
                    } else if (isEdit)
                        mActivity.seeEditOptions(profileInfo);
                    else
                        mActivity.showMemberShipInfo(userItem, 1);
                } else
                    startApp();
                break;
        }
    }

    private void startApp() {
        Intent intent = new Intent(mActivity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        mActivity.finish();
    }

    // Add like to user
    private void updateUserLike() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("user_type", profileInfo.getUser_type() + "");
            params.put("liked_id", profileInfo.getId() + "");
            try {
                WifiManager wm = (WifiManager) mActivity.getApplicationContext().getSystemService(WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                if (ip == null)
                    params.put("ip", "");
                else
                    params.put("ip", ip + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "addLike", this, params, 102);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    // Added into fav list
    private void addedUserFavorites() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("user_type", profileInfo.getUser_type() + "");
            params.put("fav_id", profileInfo.getId() + "");
            try {
                WifiManager wm = (WifiManager) mActivity.getApplicationContext().getSystemService(WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                if (ip == null)
                    params.put("ip", "");
                else
                    params.put("ip", ip + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "addFavourite", this, params, 103);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    // Bind display steps
    private ArrayList<OptionsData> bindDisplaySteps() {
        int userType = 1;
        try {
            userType = Integer.valueOf(profileInfo.getUser_type());
        } catch (Exception e) {
        }

        ArrayList<OptionsData> values = new ArrayList<>();
//        String[] name = {"Gallery", "About", "Connect with me", "Information", "Disciplines", "Categories", "Languages"};
//        Integer[] images = {R.drawable.ic_gallery, R.drawable.ic_about, R.drawable.ic_connect, R.drawable.ic_info, R.drawable.ic_disciplines, R.drawable.ic_categories, R.drawable.ic_language};

        String[] name = {mActivity.mPref.getString("209", "About"), mActivity.mPref.getString("210", "Connect with me"), mActivity.mPref.getString("211", "Information"), mActivity.mPref.getString("216", "Disciplines"), mActivity.mPref.getString("112", "Categories"), mActivity.mPref.getString("93", "Languages")};
        Integer[] images = {R.drawable.ic_about, R.drawable.ic_connect, R.drawable.ic_info, R.drawable.ic_disciplines, R.drawable.ic_categories, R.drawable.ic_language};

        for (int i = 0; i < name.length; i++) {
            OptionsData op = new OptionsData();
            op.setId(i + 1);
            op.setName(name[i]);
            op.setImgId(images[i]);
            if (i == 0) { // About
                /*if (profileInfo.getAbout_user() != null && !profileInfo.getAbout_user().equalsIgnoreCase("null") && profileInfo.getAbout_user().length() > 0) {
                    op.setOptionData(profileInfo.getAbout_user());
                    values.add(op);
                }*/
            } else if (i == 1) { // Connect with me
                if (profileInfo.getUserSocial() != null && profileInfo.getUserSocial().size() > 0) {
                    op.setOptionDataArrayList(profileInfo.getUserSocial());
                    values.add(op);
                }
            } else if (userType == 1 && i == 2) { // Information
                if (profileInfo.getUserInfo() != null && profileInfo.getUserInfo().size() > 0) {
                    op.setOptionDataArrayList(profileInfo.getUserInfo());
                    values.add(op);
                }
            } else if (userType == 1 && i == 3) { // Disciplines
                if (profileInfo.getUserDisciplines() != null && profileInfo.getUserDisciplines().size() > 0) {
                    op.setOptionData(bindTextData(profileInfo.getUserDisciplines()));
//                    op.setOptionDataArrayList(profileInfo.getUserDisciplines());
                    values.add(op);
                }
            } else if (userType == 1 && i == 4) { // Categories
                if (profileInfo.getUserCategories() != null && profileInfo.getUserCategories().size() > 0) {
                    op.setOptionData(bindTextData(profileInfo.getUserCategories()));
//                    op.setOptionDataArrayList(profileInfo.getUserCategories());
                    values.add(op);
                }
            } else if (i == 5) { // Language
                if (profileInfo.getUserLanguages() != null && profileInfo.getUserLanguages().size() > 0) {
                    op.setOptionData(bindTextData(profileInfo.getUserLanguages()));
//                    op.setOptionDataArrayList(profileInfo.getUserLanguages());
                    values.add(op);
                }
            }
        }
        return values;
    }

    private String bindTextData(ArrayList<OptionsData> userDisciplines) {
        String value = "";
        try {
            if (userDisciplines != null) {
                for (int i = 0; i < userDisciplines.size(); i++) {
                    if (userDisciplines.get(i).getName() != null && !userDisciplines.get(i).getName().equalsIgnoreCase("null") && userDisciplines.get(i).getName().length() > 0)
                        if (value.length() > 2)
                            value = value.trim() + "  " + userDisciplines.get(i).getName();
                        else
                            value = userDisciplines.get(i).getName();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private void getUserProfileDetails() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("profile_user_id", userItem.getId() + "");
            // TODO v82
            PackageInfo pInfo = null;
            try {
                pInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);
                params.put("reqAPPVER", pInfo.versionCode + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (type == 3)
                params.put("referred", "visit");
            else if (type == 4)
                params.put("referred", "fav");
            else if (type == 5)
                params.put("referred", "like");

            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "getProfileDetail", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void updateVisitorsInformationToServer() {
        if (!isEdit && mActivity.mPref.getBoolean("isLogin", false) && mActivity.isOnline()) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("user_type", mActivity.mPref.getInt("UserType", 0) + "");
            params.put("visit_user_id", profileInfo.getId() + "");
            params.put("visit_user_type", profileInfo.getUser_type());
            params.put("ip", mActivity.ip);
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "addVisitors", this, params, 104);
        }
    }

    @Override
    public void getError(String error, int APICode) {
        if (error != null)
            if (APICode == 101)
                showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), error);
            else
                mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), error);
        else
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));

        mActivity.showProgressDialog(false);
    }

    @Override
    public void getResponse(String response, int APICode) {
        try {
            if (response != null) {
                JSONObject jo = new JSONObject(response);

                if (APICode == 101) { // profile details
                    if (jo.has("status") && jo.getBoolean("status")) {
                        profileInfo = new ProfileDataParser().parseUserProfileData(jo.getString("data"), mActivity.mPref);
                        myDisplayData = bindDisplaySteps();
                        bindGalleryData();
                        setDataOnUi();
                        updateVisitorsInformationToServer();
                    } else if (jo.has("error"))
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("error"));
                    else
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                } else if (APICode == 102 || APICode == 103) { //102- Add like  // 103-Add favorites
                    if (jo.has("status") && jo.getBoolean("status") && jo.has("success")) {
//                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("success"));
                        if (jo.has("other_data")) {
//                            mActivity.saveProfileUpdateCount(jo.getString("other_data"), 1);
                        }
                        if (APICode == 102) {
                            int userLike = 0;
                            if (userLikes.getTag() != null)
                                userLike = (int) userLikes.getTag();
                            if (userLike == 0) {
                                userLikes.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_liked, 0, 0);
                                userLikes.setTag(1);
                                userLikes.setText((Integer.valueOf(userLikes.getText().toString()) + 1) + "");
                            } else {
                                userLikes.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_dislike, 0, 0);
                                userLikes.setTag(0);
                                userLikes.setText((Integer.valueOf(userLikes.getText().toString()) - 1) + "");
                            }
                        } else {
                            int userF = 0;
                            if (userFav.getTag() != null)
                                userF = (int) userFav.getTag();

                            if (userF == 0) {
                                userFav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_m_fav, 0, 0);
                                userFav.setTag(1);
                                userFav.setText((Integer.valueOf(userFav.getText().toString()) + 1) + "");
                            } else {
                                userFav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_unfav, 0, 0);
                                userFav.setTag(0);
                                userFav.setText((Integer.valueOf(userFav.getText().toString()) - 1) + "");
                            }
                        }

//                        mActivity.bindBottomNavigationData();
                    } else
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                } // if (APICode == 104) ; // Add Visitors API response
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mActivity.showProgressDialog(false);
    }


    private void bindGalleryData() {
        try {
            ArrayList<OptionsData> galleryList = profileInfo.getGalleryList();
            if (galleryList == null)
                galleryList = new ArrayList<>();
            if (galleryList.size() == 0) {
                OptionsData od = new OptionsData();
                od.setId(-1);
                od.setName(Constants.PROFILE_BASE_IMAGE_URL + profileInfo.getUser_pic());
                galleryList.add(od);
            }

            ViewPagerAdapter mediaAdapter = new ViewPagerAdapter(mActivity, profileInfo.getGalleryList(), this);
            galleryPager.setAdapter(mediaAdapter);
            dotsCount = mediaAdapter.getCount();
            if (dotsCount > 1) {
                dots = new ImageView[dotsCount];

                sliderLayout.removeAllViews();
                for (int i = 0; i < dotsCount; i++) {
                    dots[i] = new ImageView(mActivity);
                    dots[i].setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.non_active_dot));

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(8, 0, 8, 0);
                    sliderLayout.addView(dots[i], params);
                }

                dots[0].setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.active_dot));

                galleryPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        for (int i = 0; i < dotsCount; i++) {
                            dots[i].setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.non_active_dot));
                        }

                        dots[position].setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.active_dot));
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void networkError(String error, int APICode) {
        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        mActivity.showProgressDialog(false);
    }

    // Set data
    private void setDataOnUi() {
        try {
//            Calendar mCalendar = Calendar.getInstance();
//            int mYear = mCalendar.get(Calendar.YEAR);
//            Glide.with(mActivity)
//                    .load(Constants.BASE_IMAGE_URL + profileInfo.getUser_pic())
//                    .into(userBlurImage);

//            Glide.with(mActivity).load(Constants.BASE_IMAGE_URL + profileInfo.getUser_pic()).apply(RequestOptions.circleCropTransform()).into(userImage);

            if (profileInfo.getUser_type() != null && !profileInfo.getUser_type().equalsIgnoreCase("2")) {
                if (profileInfo.getCompletedYrs() != 0 && profileInfo.getUser_type().equalsIgnoreCase("1"))
                    userName.setText(profileInfo.getFirst_name() + ", " + profileInfo.getCompletedYrs());
                else
                    userName.setText(profileInfo.getFirst_name());
            } else if (profileInfo.getCompany_name() != null && !profileInfo.getCompany_name().equalsIgnoreCase("null")) {
//                if (profileInfo.getCompletedYrs() != 0)
//                    userName.setText(profileInfo.getCompany_name() + ", " + profileInfo.getCompletedYrs());
//                else
                userName.setText(profileInfo.getCompany_name());
            }


//            toolbar.setTitle(userName.getText().toString());

            String location = "";

            if (profileInfo.getCity() != null && !profileInfo.getCity().equalsIgnoreCase("null"))
                location = profileInfo.getCity();

            if (profileInfo.getCountry() != null && !profileInfo.getCountry().equalsIgnoreCase("null"))
                if (location.length() > 3)
                    location = location + ", " + profileInfo.getCountry();
                else
                    location = profileInfo.getCountry();

            userLocation.setText(location);

            // Set user profile information
            setUserCountInfo(profileInfo.getUserCountData());

            ProfileViewStepsAdapter pvAdapter = new ProfileViewStepsAdapter(mActivity, myDisplayData, this);
            rvOptions.setAdapter(pvAdapter);
            profileView.post(new Runnable() {
                @Override
                public void run() {
//                    profileView.scrollTo(0, infoLayout.getBottom());
                    profileView.scrollTo(0, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUserCountInfo(String userCountData) {
        try {
            if (userCountData != null && !userCountData.equalsIgnoreCase("null")) {
                JSONObject jo = new JSONObject(userCountData);
//                userLikes.setText(jo.getInt("totallike") + " " + getString(R.string.likes_title));
                userLikes.setText(jo.getInt("totallike") + "");
                if (jo.getInt("is_liked") == 1)
                    userLikes.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_liked, 0, 0);
                else
                    userLikes.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_dislike, 0, 0);

                userLikes.setTag(jo.getInt("is_liked"));
//                userFav.setText(jo.getInt("totalfav") + " " + getString(R.string.fav_title));
                userFav.setText(jo.getInt("totalfav") + "");
                if (jo.getInt("is_fav") == 1)
                    userFav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_m_fav, 0, 0);
                else
                    userFav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_unfav, 0, 0);

                userFav.setTag(jo.getInt("is_fav"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void getAction(OptionsData value) {
        if (value.getId() == 1)
            mActivity.viewPhotoGallery(profileInfo);
        else if (value.getId() == 2)
            mActivity.updateUserAboutInfo(profileInfo);
        else if (value.getId() == 3)
            mActivity.seeSocialOptions(profileInfo.getUserSocial());
        else if (value.getId() == 4)
            mActivity.updateUserInformation(profileInfo);
        else if (value.getId() == 5) // 3-"Casting Settings", 5 - "Who Can See The Profile?", 8 - Discipline, 9 - Categories
            mActivity.updateUserSettings(8, profileInfo);
        else if (value.getId() == 6) // 3-"Casting Settings", 5 - "Who Can See The Profile?", 8 - Discipline, 9 - Categories
            mActivity.updateUserSettings(9, profileInfo);
        else if (value.getId() == 7)
            mActivity.updateUserInformation(profileInfo);
    }

    @Override
    public void getDataList(ArrayList<OptionsData> value, int position, int type) {
//        String[] name = {"Gallery", "About", "Connect with me", "Information", "Disciplines", "Categories", "Languages"};
        try {
            if (type == 2) { // Social click action
                if (mActivity.mPref.getBoolean("isLogin", false)) {
                    if (mActivity.mPref.getInt("MemberShip", 0) != 0) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(value.get(position).getOptionData()));
                        startActivity(i);
                    } else
                        mActivity.showMemberShipInfo(userItem, 1);
                } else
                    startApp();
            } else {
                // Type - 1
                if (value.get(position).getId() != -1) {
                    Intent intent = new Intent(mActivity, MediaFullScreenActivity.class);
                    intent.putExtra("MediaData", value);
                    intent.putExtra("SelPos", position);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO
//        if (value.get(position).getId() == 3)
//            mActivity.seeSocialOptions(profileInfo.getUserSocial());
    }

    // Show dialog with message and title
    public void showMessageWithTitle(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle("")
                .setMessage(Html.fromHtml(message))
                .setPositiveButton(mActivity.mPref.getString("21", "Yes"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mActivity.onBackPressed();
                    }
                })
                .setIcon(R.mipmap.ic_stat_notification)
                .show();
    }

    @Override
    public void onResume() {
//        toolbar.setTitle("");
        mActivity.updateActionBar(1, null, false, true, true);
//        mActivity.bindBottomNavigationData();
        updateGallery();
//        mActivity.navigationView.setVisibility(View.GONE);
        super.onResume();
    }

    private void updateGallery() {
        try {
            if (galleryPager != null)
                galleryPager.setCurrentItem(KenbieApplication.galleryIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
