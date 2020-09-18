package com.kenbie;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.fragments.AboutEditFragment;
import com.kenbie.fragments.AddCastingInfoFragment;
import com.kenbie.fragments.CastingDetailsFragment;
import com.kenbie.fragments.CastingFragment;
import com.kenbie.fragments.CelebrityInfoFragment;
import com.kenbie.fragments.EditBasicInfoFragment;
import com.kenbie.fragments.EditCastingDetailsFragment;
import com.kenbie.fragments.EditProfileFragment;
import com.kenbie.fragments.EditUserInfoFragment;
import com.kenbie.fragments.ExtraFragment;
import com.kenbie.fragments.LanguageFragment;
import com.kenbie.fragments.MemberShipInfoFragment;
import com.kenbie.fragments.MessageUserListFragment;
import com.kenbie.fragments.ModelsFragment;
import com.kenbie.fragments.MyCastingFragment;
import com.kenbie.fragments.PasswordChangeFragment;
import com.kenbie.fragments.PhotoGalleryFragment;
import com.kenbie.fragments.ReportAbuseToUserFragment;
import com.kenbie.fragments.SearchFragment;
import com.kenbie.fragments.SettingFragment;
import com.kenbie.fragments.SettingUpdateFragment;
import com.kenbie.fragments.SocialLinksFragment;
import com.kenbie.fragments.SortFragment;
import com.kenbie.fragments.SubscriptionPmtFragment;
import com.kenbie.fragments.UpdateLanguageListFragment;
import com.kenbie.fragments.UserListFragment;
import com.kenbie.fragments.UserProfileFragment;
import com.kenbie.fragments.UserStatisticsFragment;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.model.CastingUser;
import com.kenbie.model.MsgUserItem;
import com.kenbie.model.OptionsData;
import com.kenbie.model.ProfileInfo;
import com.kenbie.model.UserItem;
import com.kenbie.util.BadgeUtils;
import com.kenbie.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KenbieNavigationActivity extends KenbieBaseActivity implements APIResponseHandler {
    private ImageView searchBtn, appLogoImage, backButton, userImg;
    private TextView mTitle;
    private ProgressDialog mProgress;
    public int type = 0, navType = 0, sortBy = 0;
    private LinearLayout topBar;
    public Map<String, String> castingParams;
    public Bitmap profilePicBitmap;
    public String imgPath = "";
    public ArrayList<UserItem> nearByList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgress = new ProgressDialog(this, R.style.MyAlertDialogStyle);
//        mProgress.setMessage("Please wait...");
        mProgress.setIndeterminate(false);
        mProgress.setCancelable(true);
        mProgress.setCanceledOnTouchOutside(false);

        topBar = findViewById(R.id.top_bar);

        backButton = findViewById(R.id.back_button);
        if (mPref.getBoolean("isLogin", false))
            backButton.setBackgroundResource(R.drawable.ic_v_back);
        else
            backButton.setBackgroundResource(R.drawable.ic_v_gray_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTitle = (TextView) findViewById(R.id.m_title);
        mTitle.setTypeface(KenbieApplication.S_NORMAL);

        appLogoImage = (ImageView) findViewById(R.id.app_logo);
        userImg = (ImageView) findViewById(R.id.user_img);

        searchBtn = (ImageView) findViewById(R.id.action_search);
        searchBtn.setVisibility(View.VISIBLE);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPref.getBoolean("isLogin", false))
                    viewSettings();
                else
                    replaceFragment(new SearchFragment(), true, false);
            }
        });


        navType = getIntent().getIntExtra("NavType", 0);

        if (navType == 1 || navType == 3 || navType == 4 || navType == 5 || navType == 6) { // Navigation from message-1, 3-visitors, 4 - fav, 5 - like, 6 - any profile visit
            UserProfileFragment userProfileFragment = new UserProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("UserData", (UserItem) getIntent().getSerializableExtra("UserItem"));
            bundle.putInt("NavType", navType);
            userProfileFragment.setArguments(bundle);
            replaceFragment(userProfileFragment, false, false);
//            viewUserProfile((UserItem) getIntent().getSerializableExtra("UserItem"));
        } else if (navType == 7) { // Navigation from membership
            MemberShipInfoFragment memInfoFragment = new MemberShipInfoFragment();
            Bundle b = new Bundle();
            b.putSerializable("UserInfo", (UserItem) getIntent().getSerializableExtra("UserItem"));
            b.putInt("Type", getIntent().getIntExtra("Type", 1));
            memInfoFragment.setArguments(b);
            replaceFragment(memInfoFragment, false, false);
        } else if (navType == 2) { // Navigation from More
            replaceFragment(new ExtraFragment(), true, false);
        } else if (navType == 8) { // Navigation from notification for message
            replaceFragment(new MessageUserListFragment(), false, false);
        } else if (navType == 9) { // Report Abuse
            ReportAbuseToUserFragment reportAbuseToUserFragment = new ReportAbuseToUserFragment();
            Bundle b = new Bundle();
            b.putSerializable("ProfileInfo", getIntent().getSerializableExtra("ProfileInfo"));
            reportAbuseToUserFragment.setArguments(b);
            replaceFragment(reportAbuseToUserFragment, false, false);
        } else if (navType == 10) {
            replaceFragment(new SearchFragment(), false, false);
//            navigationView.getMenu().getItem(2).setChecked(true);
        } else if (navType == 11) {
            ModelsFragment modelsFragment = new ModelsFragment();
            modelsFragment.setArguments(getIntent().getExtras());
            replaceFragment(modelsFragment, false, false);
        } else if (navType == 12) {
            CastingDetailsFragment castingDetailsFragment = new CastingDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("CastingDetails", getIntent().getSerializableExtra("CastingDetails"));
            bundle.putInt("Type", 1);
            castingDetailsFragment.setArguments(bundle);
            replaceFragment(castingDetailsFragment, false, false);
        } else if (navType == 15) { // Setting
            SettingFragment settingFragment = new SettingFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Type", 1);
            settingFragment.setArguments(bundle);
            replaceFragment(settingFragment, false, false);
        } else if (navType == 16) { // Edit Profile
            EditProfileFragment profileFragment = new EditProfileFragment();
            Bundle b = new Bundle();
            b.putSerializable("ProfileInfo",  getIntent().getSerializableExtra("ProfileInfo"));
            profileFragment.setArguments(b);
            replaceFragment(profileFragment, false, false);
        } else if (navType == 17) { // Add/Edit casting
            castingParams = null;
            imgPath = "";
            profilePicBitmap = null;
            replaceFragment(new EditCastingDetailsFragment(), false, false);
        } else if (navType == 18) { // Applied casting
            MyCastingFragment castingFragment = new MyCastingFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Type", 2);
            castingFragment.setArguments(bundle);
            replaceFragment(castingFragment, false, false);
        } else if (navType == 19) { // Casting details
            CastingDetailsFragment castingDetailsFragment = new CastingDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("CastingDetails", getIntent().getSerializableExtra("CastingDetails"));
            bundle.putInt("Type", getIntent().getIntExtra("Type", 1));
            castingDetailsFragment.setArguments(bundle);
            replaceFragment(castingDetailsFragment, false, false);
        } else if (navType == 20) { // Add casting
            AddCastingInfoFragment addCastingInfoFragment = new AddCastingInfoFragment();
            Bundle b = new Bundle();
            b.putSerializable("CastingUser", getIntent().getSerializableExtra("CastingUser"));
            b.putInt("Type", getIntent().getIntExtra("Type", 1));
            b.putBoolean("PaymentNow", getIntent().getBooleanExtra("PaymentNow", false));
            addCastingInfoFragment.setArguments(b);
            replaceFragment(addCastingInfoFragment, false, false);
        }  else if (navType == 21) { // Add celebrity info
            CelebrityInfoFragment memInfoFragment = new CelebrityInfoFragment();
            Bundle b = new Bundle();
            b.putSerializable("UserInfo", getIntent().getSerializableExtra("UserInfo"));
            b.putSerializable("CelebrityData", getIntent().getSerializableExtra("CelebrityData"));
            b.putSerializable("RandomCelebrityData", getIntent().getSerializableExtra("RandomCelebrityData"));
            b.putInt("Type", getIntent().getIntExtra("Type", 1));
            memInfoFragment.setArguments(b);
            replaceFragment(memInfoFragment, false, false);
        } /*else {
//            navigationView.getMenu().getItem(0).setChecked(true);
//            fragmentStateManager.changeFragment(0);
//            replaceFragment(new UserListFragment(), false, false);
            replaceFragment(new UserListFragment(), false, false);
        }*/
    }

    public void replaceFragment(Fragment fragment, boolean needToAddBackStack, boolean clearStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (needToAddBackStack && !clearStack) {
//            ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            ft.replace(R.id.container, fragment).addToBackStack(fragment.getClass().getSimpleName()).commit();
        } else {
            ft.replace(R.id.container, fragment).commitAllowingStateLoss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPref.getBoolean("isLogin", false)) {
            refreshUserProfile(mPref.getString("ProfilePic", ""));

            userImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewEditProfile();
                }
            });

            // Sync bottom notification data
            gettingProfileComplete();
//            isNavigateFragmentBack();
        }
    }

    public void launchCasting(int type) {
        MyCastingFragment castingFragment = new MyCastingFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("Type", 3);
        castingFragment.setArguments(bundle);
        replaceFragment(castingFragment, type != 4, false);
    }

    public void viewSettings() {
//        mNavigationDrawerFragment.closeAndOpenNavPanel();
        viewSettingOptions(1);
    }

    public void viewEditProfile() {
//        mNavigationDrawerFragment.closeAndOpenNavPanel();
        UserItem userItem = new UserItem();
        userItem.setId(Integer.valueOf(mPref.getString("UserId", "0")));
        viewUserProfile(userItem);
    }

    public void viewSettingOptions(int type) {
/*        Bundle bundle = new Bundle();
        bundle.putInt("Type", type);
        Intent intent = new Intent(this, KenbieNavigationActivity.class);
        intent.putExtra("NavType", 15);
        intent.putExtras(bundle);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/

        SettingFragment settingFragment = new SettingFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("Type", type);
        settingFragment.setArguments(bundle);
        replaceFragment(settingFragment, true, false);
    }

    // Edit options fragment
    public void seeEditOptions(ProfileInfo profileInfo) {
        EditProfileFragment profileFragment = new EditProfileFragment();
        Bundle b = new Bundle();
        b.putSerializable("ProfileInfo", profileInfo);
        profileFragment.setArguments(b);
        replaceFragment(profileFragment, true, false);
    }

    // Edit basic info
    public void editBasicInfo(ProfileInfo profileInfo, int type) {
        EditBasicInfoFragment editBasicProfileFragment = new EditBasicInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("ProfileInfo", profileInfo);
        bundle.putInt("NType", type);
        editBasicProfileFragment.setArguments(bundle);
        replaceFragment(editBasicProfileFragment, true, false);
    }

    // Celebrate Payment Process
    public void startPaymentProcess(int type) {
        SubscriptionPmtFragment subscriptionPmtFragment = new SubscriptionPmtFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("PaymentType", type);
        subscriptionPmtFragment.setArguments(bundle);
        replaceFragment(subscriptionPmtFragment, true, false);
    }

    // Logout functionality
    public void logoutProcess() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("UserId", "0");
        editor.putString("LoginKey", "");
        editor.putString("LoginToken", "");
        editor.putString("ProfilePic", "");
        editor.putString("DeviceId", "");
        editor.putBoolean("isLogin", false);
        editor.putBoolean("GuestLogin", false);
        editor.apply();
        Intent intent = new Intent(KenbieNavigationActivity.this, LoginOptionsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }

    // View user profile
    public void viewUserProfile(UserItem userItem) {
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("UserData", userItem);
        userProfileFragment.setArguments(bundle);
        replaceFragment(userProfileFragment, true, false);
    }

    // View celebrities payment screen
    public void showMemberDialog(final UserItem userItem) {
        try {
            final Dialog dialog = new Dialog(KenbieNavigationActivity.this);
            dialog.setCancelable(true);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_with_cross_btn);
            if (dialog.getWindow() != null)
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView mTitle = (TextView) dialog.findViewById(R.id.m_title);
            mTitle.setTypeface(KenbieApplication.S_BOLD);

            TextView mSubTitle = (TextView) dialog.findViewById(R.id.m_sub_title);
            mSubTitle.setTypeface(KenbieApplication.S_SEMI_BOLD);

            TextView mMsg = (TextView) dialog.findViewById(R.id.m_msg);
            mMsg.setTypeface(KenbieApplication.S_SEMI_LIGHT);

            TextView mMemBtn = (TextView) dialog.findViewById(R.id.m_mem_btn);
            mMemBtn.setTypeface(KenbieApplication.S_NORMAL);
            mMemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    showMemberShipInfo(userItem, 1); // Membership
                }
            });

            ((ImageView) dialog.findViewById(R.id.m_cross_btn)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Show celebrity info
    public void showCelebrityInfo(UserItem userItem, int type) {
        CelebrityInfoFragment memInfoFragment = new CelebrityInfoFragment();
        Bundle b = new Bundle();
        b.putSerializable("UserInfo", userItem);
        b.putInt("Type", type);
        memInfoFragment.setArguments(b);
        replaceFragment(memInfoFragment, true, false);
    }

    // Show membership info
    public void showMemberShipInfo(UserItem userItem, int type) {
        MemberShipInfoFragment memInfoFragment = new MemberShipInfoFragment();
        Bundle b = new Bundle();
        b.putSerializable("UserInfo", userItem);
        b.putInt("Type", type);
        memInfoFragment.setArguments(b);
        replaceFragment(memInfoFragment, true, false);
    }

    // Action bar update details
    public void updateActionBar(int type, String title, boolean isSearchEnable, boolean backEnable, boolean actionBarHide) {
        try {
            this.type = type;

            /*if (type == 2) {
                navigationView.getMenu().getItem(2).setChecked(true);
//                navigationView.setSelectedItemId(R.id.action_search);
                topBar.setVisibility(View.GONE);
                return;
            } else */

            if (actionBarHide || type == 35)
                topBar.setVisibility(View.GONE);
            else
                topBar.setVisibility(View.VISIBLE);

            if (backEnable)
                backButton.setVisibility(View.VISIBLE);
            else
                backButton.setVisibility(View.GONE);

            if (mPref.getBoolean("isLogin", false)) {
                if (type == 0) {
                    userImg.setVisibility(View.VISIBLE);
                    searchBtn.setVisibility(View.VISIBLE);
                    searchBtn.setBackgroundResource(R.drawable.ic_setting);
                } else if (isSearchEnable) {
                    userImg.setVisibility(View.GONE);
                    searchBtn.setVisibility(View.VISIBLE);
                    searchBtn.setBackgroundResource(R.drawable.ic_v_search);
                } else {
                    searchBtn.setVisibility(View.INVISIBLE);
                    userImg.setVisibility(View.GONE);
                }

                if (title == null) {
                    mTitle.setVisibility(View.INVISIBLE);
                    appLogoImage.setVisibility(View.VISIBLE);
                } else {
                    mTitle.setText(title);
                    mTitle.setVisibility(View.VISIBLE);
                    appLogoImage.setVisibility(View.INVISIBLE);
                }
            } else if (type == 0) { // Home
                backButton.setVisibility(View.VISIBLE);
                searchBtn.setVisibility(View.VISIBLE);
                searchBtn.setBackgroundResource(R.drawable.ic_v_search);
                userImg.setVisibility(View.GONE);
                mTitle.setVisibility(View.INVISIBLE);
                appLogoImage.setVisibility(View.VISIBLE);
            } else {
                userImg.setVisibility(View.GONE);
                searchBtn.setVisibility(View.INVISIBLE);
                if (title == null) {
                    mTitle.setVisibility(View.INVISIBLE);
                    appLogoImage.setVisibility(View.VISIBLE);
                } else {
                    mTitle.setText(title);
                    mTitle.setVisibility(View.VISIBLE);
                    appLogoImage.setVisibility(View.INVISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        hideKeyboard(KenbieNavigationActivity.this);
    }

    public void shareApp(String message) {
        try {
            Intent sendIntent1 = new Intent();
            sendIntent1.setAction(Intent.ACTION_SEND);
            sendIntent1.putExtra(Intent.EXTRA_SUBJECT, "Kenbie App");
//            sendIntent1.putExtra(Intent.EXTRA_TEXT, "Please download app for more fun. https://play.google.com/store/apps/details?id=com.kenbie&hl=en");
            sendIntent1.putExtra(Intent.EXTRA_TEXT, "https://kenbie.com/" + message);
            sendIntent1.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent1, "Invite A Friend"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Start chat with user
    public void startMessaging(ProfileInfo profileInfo) {
        MsgUserItem msgUserItem = new MsgUserItem();
        msgUserItem.setUid(profileInfo.getId());
        msgUserItem.setUser_name(profileInfo.getFirst_name());
        msgUserItem.setUser_img(profileInfo.getUser_pic());
        Intent intent = new Intent(KenbieNavigationActivity.this, MessageConvActivity.class);
        intent.putExtra("MsgItem", msgUserItem);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

//        replaceFragment(new MessageUserListFragment(), true, false);
    }

    // Social Fragments
    public void seeSocialOptions(ArrayList<OptionsData> userSocial) {
        SocialLinksFragment socialLinksFragment = new SocialLinksFragment();
        Bundle b = new Bundle();
        b.putSerializable("SocialData", userSocial);
        socialLinksFragment.setArguments(b);
        replaceFragment(socialLinksFragment, true, false);
    }

    // User Statistics Fragment
    public void seeUserStatisticsFragment(ProfileInfo profileInfo) {
        UserStatisticsFragment userStatisticsFragment = new UserStatisticsFragment();
        Bundle b = new Bundle();
        b.putSerializable("ProfileInfo", profileInfo);
        userStatisticsFragment.setArguments(b);
        replaceFragment(userStatisticsFragment, true, false);
    }

    // Language List
    public void viewLanguageUpdateList(ProfileInfo profileInfo) {
        UpdateLanguageListFragment updateLanguageListFragment = new UpdateLanguageListFragment();
        Bundle b = new Bundle();
        b.putSerializable("ProfileInfo", profileInfo);
        updateLanguageListFragment.setArguments(b);
        replaceFragment(updateLanguageListFragment, true, false);
    }

    // 3-"Casting Settings", 5 - "Who Can See The Profile?", 8 - Discipline, 9 - Categories
    public void updateUserSettings(int type, ProfileInfo profileInfo) {
        SettingUpdateFragment settingUpdateFragment = new SettingUpdateFragment();
        Bundle b = new Bundle();
        b.putInt("Type", type);
        b.putSerializable("ProfileInfo", profileInfo);
        settingUpdateFragment.setArguments(b);
        replaceFragment(settingUpdateFragment, true, false);
    }

    public void updateUserInformation(ProfileInfo profileInfo) {
        EditUserInfoFragment editUserInfoFragment = new EditUserInfoFragment();
        Bundle b = new Bundle();
        b.putSerializable("ProfileInfo", profileInfo);
        editUserInfoFragment.setArguments(b);
        replaceFragment(editUserInfoFragment, true, false);
    }

    public void updateUserAboutInfo(ProfileInfo profileInfo) {
        AboutEditFragment aboutEditFragment = new AboutEditFragment();
        Bundle b = new Bundle();
        b.putSerializable("ProfileInfo", profileInfo);
        aboutEditFragment.setArguments(b);
        replaceFragment(aboutEditFragment, true, false);
    }

    public void viewPhotoGallery(ProfileInfo profileInfo) {
        PhotoGalleryFragment photoGalleryFragment = new PhotoGalleryFragment();
        Bundle b = new Bundle();
        b.putSerializable("ProfileInfo", profileInfo);
        photoGalleryFragment.setArguments(b);
        replaceFragment(photoGalleryFragment, true, false);
    }

    public void callChangePwdFragment() {
        replaceFragment(new PasswordChangeFragment(), true, false);
    }

    public void updateLanguage() {
        replaceFragment(new LanguageFragment(), true, false);
    }

    // Launch casting info page
    public void startAddingCasting(CastingUser castingUser, int type, boolean paymentNow) {
        AddCastingInfoFragment addCastingInfoFragment = new AddCastingInfoFragment();
        Bundle b = new Bundle();
        b.putSerializable("CastingUser", castingUser);
        b.putInt("Type", type);
        b.putBoolean("PaymentNow", paymentNow);
        addCastingInfoFragment.setArguments(b);
        replaceFragment(addCastingInfoFragment, true, false);
    }

    // Apply sort on list data
    public void applySort(int type) { // 1 - sort, 2 - type
        SortFragment sortFragment = new SortFragment();
        Bundle b = new Bundle();
        b.putInt("Type", type);
        sortFragment.setArguments(b);
        replaceFragment(sortFragment, true, false);
    }

    // Action report abuse
    public void userReportAbuse(ProfileInfo profileInfo) {
        Intent intent = new Intent(this, KenbieNavigationActivity.class);
        intent.putExtra("ProfileInfo", profileInfo);
        intent.putExtra("NavType", 9);
        startActivity(intent);
    }

    public void gettingProfileComplete() {
        if (isOnline() && mPref.getBoolean("isLogin", false)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mPref.getString("UserId", ""));
            params.put("login_key", mPref.getString("LoginKey", ""));
            params.put("login_token", mPref.getString("LoginToken", ""));
            mConnection.postRequestWithHttpHeaders(this, "getUserProfileComplete", this, params, 101);
        }
    }

    @Override
    public void getError(String error, int APICode) {

    }

    @Override
    public void getResponse(String response, int APICode) {
        try {
            if (APICode == 101) {
                JSONObject jo = new JSONObject(response);
                if (jo.has("status") && jo.getBoolean("status") && jo.has("data")) {
                    JSONObject jProfile = jo.getJSONObject("data");
                    int moreCount = 0;
                    SharedPreferences.Editor editor = mPref.edit();
                    if (jProfile.has("profile_complete"))
                        editor.putInt("PComplete", jProfile.getInt("profile_complete"));
                    if (jProfile.has("unread_msg_count"))
                        editor.putInt("UnreadMsg", jProfile.getInt("unread_msg_count"));
                    if (jProfile.has("totalliked")) {
                        editor.putInt("TotalLiked", jProfile.getInt("totalliked"));
                        moreCount = jProfile.getInt("totalliked");
                    }
                    if (jProfile.has("visitor_count")) {
                        editor.putInt("VisitorCount", jProfile.getInt("visitor_count"));
                        moreCount = moreCount + jProfile.getInt("visitor_count");
                    }

                    if (jProfile.has("fav_count")) {
                        editor.putInt("FavCount", jProfile.getInt("fav_count"));
                        moreCount = moreCount + jProfile.getInt("fav_count");
                    }

                    if (jProfile.has("is_paid"))
                        editor.putInt("MemberShip", jProfile.getInt("is_paid"));

                    if (jProfile.has("subs_payment"))
                        editor.putInt("SubsPayment", (int) (jProfile.getDouble("subs_payment") * 100));
                    if (jProfile.has("celeb_payment"))
                        editor.putInt("CelebPayment", (int) (jProfile.getDouble("celeb_payment") * 100));
                    if (jProfile.has("cast_payment"))
                        editor.putInt("CastPayment", (int) (jProfile.getDouble("cast_payment") * 100));

                    if (jProfile.has("user_pic")) {
                        refreshCelebrityImage(jProfile.getString("user_pic"));
                        editor.putString("ProfilePic", jProfile.getString("user_pic"));
                    }

                    editor.putInt("MoreCount", moreCount);
                    editor.apply();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshCelebrityImage(String user_pic) {
        try {
            if (user_pic != null && !mPref.getString("ProfilePic", "").equalsIgnoreCase(user_pic)) {
                refreshUserProfile(user_pic);
                UserListFragment userListFragment = (UserListFragment)
                        getSupportFragmentManager().findFragmentById(R.id.container);
                if (userListFragment != null)
                    userListFragment.refreshCelebrityData(user_pic);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void networkError(String error, int APICode) {

    }

    public void refreshUserProfile(String profile) {
        try {
            RequestOptions options = new RequestOptions()
                    .optionalCircleCrop()
                    .placeholder(getResources().getDrawable(R.drawable.img_c_user_dummy))
                    .priority(Priority.HIGH);

            if (profile != null && profile.length() < 10)
                Glide.with(this).load(Constants.BASE_IMAGE_URL + profile).apply(options).into(userImg);
            else
                Glide.with(this).load(Constants.BASE_IMAGE_URL + profile).apply(options).into(userImg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showProgressDialog(boolean isShow) {
        try {
            if (isShow) {
//                mProgress.setMessage("Please wait...");
                mProgress.show();
            } else
                mProgress.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgress != null && mProgress.isShowing())
            mProgress.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("Notification", false)) {
//            removeAllNotificationData();
            getIntent().putExtra("Notification", false);
            getIntent().putExtra("type", 0);
            replaceFragment(new UserListFragment(), false, false);
        } else
            super.onBackPressed();
    }


    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return super.onNavigateUp();
    }

    public void clearNotification() {
        try {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.cancelAll();
            BadgeUtils.clearBadge(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Parse search data
    public ArrayList<UserItem> getParseSearchData(String data, int type) {
        ArrayList<UserItem> values = new ArrayList<>();
        try {
            JSONArray jData = new JSONArray(data);
            for (int i = 0; i < jData.length(); i++) {
                JSONObject jo = new JSONObject(jData.getString(i));
                UserItem value = new UserItem();
                value.setId(jo.getInt("id"));
                value.setFirstName(jo.getString("first_name"));
                if (jo.getString("birth_year") != null && !jo.getString("birth_year").equalsIgnoreCase("null") && jo.getString("birth_year").length() > 0) {
                    value.setBirthYear(utility.getYearsCountFromDate(jo.getString("birth_year"), jo.getString("birth_month"), jo.getString("birth_day")));
                }
//                if (jo.getString("birth_year") != null && !jo.getString("birth_year").equalsIgnoreCase("null") && jo.getString("birth_year").length() > 0)
//                    value.setBirthYear(jo.getInt("birth_year"));
//                value.setBirthMonth(jo.getInt("birth_month"));
//                value.setBirthDay(jo.getInt("birth_day"));
                value.setTotalImage(jo.getInt("total_image"));
                value.setCity(jo.getString("city"));
                value.setCountry(jo.getString("country"));
                value.setIsActive(jo.getInt("is_active"));
                value.setUserPic(jo.getString("user_pic"));
                value.setType(type);
                values.add(value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    private void initEditCastData(CastingUser castingUser) {
        try {
            profilePicBitmap = null;
            castingParams = new HashMap<String, String>();
            castingParams.put("user_id", mPref.getString("UserId", ""));
            castingParams.put("login_key", mPref.getString("LoginKey", ""));
            castingParams.put("login_token", mPref.getString("LoginToken", ""));
            castingParams.put("casting_title", castingUser.getCasting_title());
            castingParams.put("requirements", castingUser.getCasting_requirement());
            castingParams.put("casting_id", castingUser.getId() + "");

            castingParams.put("casting_face", castingUser.getCasting_type() + "");
            castingParams.put("casting_location", castingUser.getCasting_location());
            castingParams.put("casting_from_age", castingUser.getCasting_start_age());
            castingParams.put("casting_to_age", castingUser.getCasting_end_age());
            castingParams.put("casting_from_time", castingUser.getCasting_start_time());
            castingParams.put("casting_to_time", castingUser.getCasting_end_time());
            castingParams.put("casting_fees", castingUser.getCasting_fee() + "");
            castingParams.put("casting_gender", castingUser.getCasting_gender());

            String startDate = castingUser.getCasting_start_date();
            if (startDate != null && startDate.length() > 2) {
                String[] sDate = startDate.split("-"); // year + "-" + (month + 1) + "-" + day
                castingParams.put("starting_date", sDate[0]);
                castingParams.put("starting_month", sDate[1]);
                castingParams.put("starting_year", sDate[2]);
            }

            String endDate = castingUser.getCasting_end_date();
            if (endDate != null && endDate.length() > 2) {
                String[] eDate = endDate.split("-"); // year + "-" + (month + 1) + "-" + day
                castingParams.put("end_date", eDate[0]);
                castingParams.put("end_month", eDate[1]);
                castingParams.put("end_year", eDate[2]);
            }

            castingParams.put("casting_address", castingUser.getCasting_address());
            castingParams.put("casting_country", castingUser.getCountry());
            castingParams.put("casting_categories", castingUser.getCasting_categories());
            castingParams.put("casting_img", castingUser.getCasting_img());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveProfileUpdateCount(String other_data, int type) {
        clearNotification();
        try {
            JSONObject jo = new JSONObject(other_data);
            int moreCount = 0;
            SharedPreferences.Editor editor = mPref.edit();

            if (jo.has("unread_msg_count"))
                editor.putInt("UnreadMsg", jo.getInt("unread_msg_count"));

            if (jo.has("totalliked")) {
                editor.putInt("TotalLiked", jo.getInt("totalliked"));
                moreCount = jo.getInt("totalliked");
            }

            if (jo.has("visitor_count")) {
                editor.putInt("VisitorCount", jo.getInt("visitor_count"));
                moreCount = moreCount + jo.getInt("visitor_count");
            }

            if (jo.has("fav_count")) {
                editor.putInt("FavCount", jo.getInt("fav_count"));
                moreCount = moreCount + jo.getInt("fav_count");
            }

            editor.putInt("MoreCount", moreCount);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
