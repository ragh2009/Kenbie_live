//package com.kenbie;
//
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Build;
//import android.os.Bundle;
//import android.view.View;
//import android.view.Window;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.widget.Toolbar;
//import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.kenbie.fragments.AboutEditFragment;
//import com.kenbie.fragments.AddCastingInfoFragment;
//import com.kenbie.fragments.CastingFragment;
//import com.kenbie.fragments.EditBasicInfoFragment;
//import com.kenbie.fragments.EditProfileFragment;
//import com.kenbie.fragments.EditUserInfoFragment;
//import com.kenbie.fragments.LanguageFragment;
//import com.kenbie.fragments.CelebrityInfoFragment;
//import com.kenbie.fragments.PasswordChangeFragment;
//import com.kenbie.fragments.PhotoGalleryFragment;
//import com.kenbie.fragments.SettingFragment;
//import com.kenbie.fragments.SettingUpdateFragment;
//import com.kenbie.fragments.SocialLinksFragment;
//import com.kenbie.fragments.SortFragment;
//import com.kenbie.fragments.SubscriptionPmtFragment;
//import com.kenbie.fragments.UserGridFragment;
//import com.kenbie.fragments.UserListFragment;
//import com.kenbie.fragments.UserProfileFragment;
//import com.kenbie.fragments.UserStatisticsFragment;
//import com.kenbie.listeners.NavigationDrawerCallbacks;
//import com.kenbie.model.CastingUser;
//import com.kenbie.model.LeftItem;
//import com.kenbie.model.MsgUserItem;
//import com.kenbie.model.OptionsData;
//import com.kenbie.model.ProfileInfo;
//import com.kenbie.model.UserItem;
//import com.kenbie.util.GPSTracker;
//import com.kenbie.util.RuntimePermissionUtils;
//
//import java.util.ArrayList;
//import java.util.Map;
//
//public class KenbieActivityOld extends KenbieBaseActivity
//        implements NavigationDrawerCallbacks {
//    public ArrayList<UserItem> nearByList;
//    public Map<String, String> castingParams;
//    public double longitude = 0, latitude = 0;
//    public int sortBy = 0;
//    public ArrayList<UserItem> celebritiesList = null;
//    public Bitmap profilePicBitmap;
//    public String imgPath;
//    private Toolbar mToolbar = null;
//    private NavigationDrawerFragment mNavigationDrawerFragment;
//    private int type = 0;
//    private ImageView menuBtn = null, searchBtn, appLogoImage;
//    private TextView mTitle;
//    private ProgressDialog mProgress;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_kenbie_main);
//
//        mProgress = new ProgressDialog(this);
//        mProgress.setMessage("Please wait...");
//        mProgress.setIndeterminate(false);
//        mProgress.setCancelable(true);
//        mProgress.setCanceledOnTouchOutside(false);
//
//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);
//        mToolbar.setNavigationIcon(R.drawable.ic_panel);
////        mToolbar.setTitle("Title");
////        mToolbar.setSubtitle("Sub");
////        mToolbar.setLogo(R.drawable.app_logo);
//
//        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
//
//        // Set up the drawer.
//        mNavigationDrawerFragment.setUp(
//                R.id.navigation_drawer,
//                (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
//
//
//        menuBtn = (ImageView) findViewById(R.id.back_button);
//        menuBtn.setBackgroundResource(R.drawable.ic_panel);
//        menuBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (type == 0 && !mPref.getBoolean("GuestLogin", false))
//                    mNavigationDrawerFragment.closeAndOpenNavPanel();
//                else
//                    onBackPressed();
//            }
//        });
//
////        mNavigationDrawerFragment.closeAndOpenNavPanel();
//        mNavigationDrawerFragment.closeNavPanel();
//
//        mTitle = (TextView) findViewById(R.id.m_title);
//        mTitle.setTypeface(KenbieApplication.S_NORMAL);
//
//        appLogoImage = (ImageView) findViewById(R.id.app_logo);
//
//        searchBtn = (ImageView) findViewById(R.id.action_search);
//        searchBtn.setVisibility(View.VISIBLE);
//        searchBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//
//        /*ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setHomeButtonEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_panel);
////            actionBar.setHomeButtonEnabled(true);
//            actionBar.hide();
//        }*/
//        // check if GPS enabled
//        if (RuntimePermissionUtils.checkPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            GPSTracker gpsTracker = new GPSTracker(this);
//            if (gpsTracker.getIsGPSTrackingEnabled()) {
//                latitude = gpsTracker.latitude;
//                longitude = gpsTracker.longitude;
////                String country = gpsTracker.getCountryName(this);
////                String city = gpsTracker.getLocality(this);
////                String postalCode = gpsTracker.getPostalCode(this);
////                String addressLine = gpsTracker.getAddressLine(this);
//            } else
//                gpsTracker.showSettingsAlert();
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, RuntimePermissionUtils.REQUEST_ACCESS_LOCATION);
//
//        replaceFragment(new UserListFragment(), false, false);
//    }
//
//    public void replaceFragment(final Fragment fragment, final boolean needToAddBackStack, final boolean clearStack) {
//        final FragmentManager fm = getSupportFragmentManager();
//        final FragmentTransaction ft = fm.beginTransaction();
//        if (needToAddBackStack && !clearStack) {
////            ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
//            ft.replace(R.id.container, fragment).addToBackStack(fragment.getClass().getSimpleName()).commit();
//        } else {
//            ft.replace(R.id.container, fragment).commitAllowingStateLoss();
//        }
//    }
//
//    @Override
//    public void onNavigationDrawerItemSelected(LeftItem data) {
//        try {
//            if (data != null) {
////                if (data.getTitle().equalsIgnoreCase("Logout"))
////                    logoutProcess();
//                if (data.getTitle().equalsIgnoreCase("Profile Visitor")) {
//                    UserGridFragment gridFragment = new UserGridFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("Type", 3);
//                    gridFragment.setArguments(bundle);
//                    replaceFragment(gridFragment, true, false);
//                } else if (data.getTitle().equalsIgnoreCase("Liked You")) {
//                    UserGridFragment gridFragment = new UserGridFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("Type", 2);
//                    gridFragment.setArguments(bundle);
//                    replaceFragment(gridFragment, true, false);
//                } else if (data.getTitle().equalsIgnoreCase("Favorites")) {
//                    UserGridFragment gridFragment = new UserGridFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("Type", 1);
//                    gridFragment.setArguments(bundle);
//                    replaceFragment(gridFragment, true, false);
////                    nTitle = {"Credits", "Message", "Profile Visitor", "Liked You", "Favorites", "Casting", "Invite Your Friend", "Logout"};
//
//                } else if (data.getTitle().equalsIgnoreCase("Casting"))
//                    launchCasting(1);
//                else if (data.getTitle().equalsIgnoreCase("Message")) {
//                    Intent msgIntent = new Intent(KenbieActivityOld.this, KenbieMessageActivity.class);
//                    msgIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(msgIntent);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void launchCasting(int type) {
//        CastingFragment castingFragment = new CastingFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt("Type", type);
//        castingFragment.setArguments(bundle);
//        replaceFragment(castingFragment, true, false);
//    }
//
//    @Override
//    public void viewSettings() {
//        mNavigationDrawerFragment.closeAndOpenNavPanel();
//        viewSettingOptions(1);
//    }
//
//    @Override
//    public void viewEditProfile() {
//        mNavigationDrawerFragment.closeAndOpenNavPanel();
//        UserItem userItem = new UserItem();
//        userItem.setId(Integer.valueOf(mPref.getString("UserId", "0")));
//        viewUserProfile(userItem);
//    }
//
//    public void viewSettingOptions(int type) {
//        SettingFragment settingFragment = new SettingFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt("Type", type);
//        settingFragment.setArguments(bundle);
//        replaceFragment(settingFragment, true, false);
//    }
//
//    // Edit options fragment
//    public void seeEditOptions(ProfileInfo profileInfo) {
//        EditProfileFragment profileFragment = new EditProfileFragment();
//        Bundle b = new Bundle();
//        b.putSerializable("ProfileInfo", profileInfo);
//        profileFragment.setArguments(b);
//        replaceFragment(profileFragment, true, false);
//    }
//
//    // Edit basic info
//    public void editBasicInfo(ProfileInfo profileInfo, int type) {
//        EditBasicInfoFragment editBasicProfileFragment = new EditBasicInfoFragment();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("ProfileInfo", profileInfo);
//        bundle.putInt("NType", type);
//        editBasicProfileFragment.setArguments(bundle);
//        replaceFragment(editBasicProfileFragment, true, false);
//    }
//
//    // Celebrate Payment Process
//    public void startPaymentProcess(int type) {
//        SubscriptionPmtFragment subscriptionPmtFragment = new SubscriptionPmtFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt("PaymentType", type);
//        subscriptionPmtFragment.setArguments(bundle);
//        replaceFragment(subscriptionPmtFragment, true, false);
//    }
//
//    // Logout functionality
//    public void logoutProcess() {
//        SharedPreferences.Editor editor = mPref.edit();
//        editor.clear();
//        editor.apply();
//        Intent intent = new Intent(KenbieActivityOld.this, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//        finish();
//    }
//
//    // View user profile
//    public void viewUserProfile(UserItem userItem) {
//        UserProfileFragment userProfileFragment = new UserProfileFragment();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("UserData", userItem);
//        userProfileFragment.setArguments(bundle);
//        replaceFragment(userProfileFragment, true, false);
//    }
//
//    // View celebrities payment screen
//    public void showMemberDialog(final UserItem userItem) {
//        try {
//            final Dialog dialog = new Dialog(KenbieActivityOld.this);
//            dialog.setCancelable(true);
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialog.setContentView(R.layout.popup_with_cross_btn);
//            if (dialog.getWindow() != null)
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//            TextView mTitle = (TextView) dialog.findViewById(R.id.m_title);
//            mTitle.setTypeface(KenbieApplication.S_BOLD);
//
//            TextView mSubTitle = (TextView) dialog.findViewById(R.id.m_sub_title);
//            mSubTitle.setTypeface(KenbieApplication.S_SEMI_BOLD);
//
//            TextView mMsg = (TextView) dialog.findViewById(R.id.m_msg);
//            mMsg.setTypeface(KenbieApplication.S_SEMI_LIGHT);
//
//            TextView mMemBtn = (TextView) dialog.findViewById(R.id.m_mem_btn);
//            mMemBtn.setTypeface(KenbieApplication.S_NORMAL);
//            mMemBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//                    showMemberShipInfo(userItem, 3);
//                }
//            });
//
//            ((ImageView) dialog.findViewById(R.id.m_cross_btn)).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//                }
//            });
//            dialog.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Show membership info
//    public void showMemberShipInfo(UserItem userItem, int type) {
//        CelebrityInfoFragment memInfoFragment = new CelebrityInfoFragment();
//        Bundle b = new Bundle();
//        b.putSerializable("UserInfo", userItem);
//        b.putInt("Type", type);
//        memInfoFragment.setArguments(b);
//        replaceFragment(memInfoFragment, true, false);
//    }
//
//    // Action bar update details
//    public void updateActionBar(int type, String title, boolean isSearchEnable) {
//        try {
//            this.type = type;
//            if (type == 0 && !mPref.getBoolean("GuestLogin", false)) { // Home
//                menuBtn.setBackgroundResource(R.drawable.ic_panel);
//                mToolbar.setNavigationIcon(R.drawable.ic_panel);
//                mTitle.setVisibility(View.INVISIBLE);
//                appLogoImage.setVisibility(View.VISIBLE);
//            } else {
//                menuBtn.setBackgroundResource(R.drawable.back_arrow);
//                mToolbar.setNavigationIcon(R.drawable.back_arrow);
//                if (title == null) {
//                    mTitle.setVisibility(View.INVISIBLE);
//                    appLogoImage.setVisibility(View.VISIBLE);
//                } else {
//                    mTitle.setText(title);
//                    mTitle.setVisibility(View.VISIBLE);
//                    appLogoImage.setVisibility(View.INVISIBLE);
//                }
//            }
//
////            if (isSearchEnable)
////                searchBtn.setVisibility(View.VISIBLE);
////            else
//            searchBtn.setVisibility(View.INVISIBLE);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void shareApp() {
//        try {
//            Intent sendIntent1 = new Intent();
//            sendIntent1.setAction(Intent.ACTION_SEND);
//            sendIntent1.putExtra(Intent.EXTRA_SUBJECT, "Kenbie App");
//            sendIntent1.putExtra(Intent.EXTRA_TEXT, "Please download app for more fun. https://play.google.com/store/apps/details?id=com.kenbie&hl=en");
//            sendIntent1.setType("text/plain");
//            startActivity(Intent.createChooser(sendIntent1, "Invite A Friend"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Start chat with user
//    public void startMessaging(ProfileInfo profileInfo) {
//        MsgUserItem msgUserItem = new MsgUserItem();
//        msgUserItem.setUid(profileInfo.getId());
//        msgUserItem.setUser_name(profileInfo.getFirst_name());
//        msgUserItem.setUser_img(profileInfo.getUser_pic());
//        Intent intent = new Intent(KenbieActivityOld.this, MessageConvActivity.class);
//        intent.putExtra("MsgItem", msgUserItem);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//
////        replaceFragment(new MessageUserListFragment(), true, false);
//    }
//
//    // Social Fragments
//    public void seeSocialOptions(ArrayList<OptionsData> userSocial) {
//        SocialLinksFragment socialLinksFragment = new SocialLinksFragment();
//        Bundle b = new Bundle();
//        b.putSerializable("SocialData", userSocial);
//        socialLinksFragment.setArguments(b);
//        replaceFragment(socialLinksFragment, true, false);
//    }
//
//    // User Statistics Fragment
//    public void seeUserStatisticsFragment(ProfileInfo profileInfo) {
//        UserStatisticsFragment userStatisticsFragment = new UserStatisticsFragment();
//        Bundle b = new Bundle();
//        b.putSerializable("ProfileInfo", profileInfo);
//        userStatisticsFragment.setArguments(b);
//        replaceFragment(userStatisticsFragment, true, false);
//    }
//
//    // 3-"Casting Settings", 5 - "Who Can See The Profile?", 9 - Discipline, 10 - Categories
//    public void updateUserSettings(int type, ProfileInfo profileInfo) {
//        SettingUpdateFragment settingUpdateFragment = new SettingUpdateFragment();
//        Bundle b = new Bundle();
//        b.putInt("Type", type);
//        b.putSerializable("ProfileInfo", profileInfo);
//        settingUpdateFragment.setArguments(b);
//        replaceFragment(settingUpdateFragment, true, false);
//    }
//
//    public void updateUserInformation(ProfileInfo profileInfo) {
//        EditUserInfoFragment editUserInfoFragment = new EditUserInfoFragment();
//        Bundle b = new Bundle();
//        b.putSerializable("ProfileInfo", profileInfo);
//        editUserInfoFragment.setArguments(b);
//        replaceFragment(editUserInfoFragment, true, false);
//    }
//
//    public void updateUserAboutInfo(ProfileInfo profileInfo) {
//        AboutEditFragment aboutEditFragment = new AboutEditFragment();
//        Bundle b = new Bundle();
//        b.putSerializable("ProfileInfo", profileInfo);
//        aboutEditFragment.setArguments(b);
//        replaceFragment(aboutEditFragment, true, false);
//    }
//
//    public void viewPhotoGallery(ProfileInfo profileInfo) {
//        PhotoGalleryFragment photoGalleryFragment = new PhotoGalleryFragment();
//        Bundle b = new Bundle();
//        b.putSerializable("ProfileInfo", profileInfo);
//        photoGalleryFragment.setArguments(b);
//        replaceFragment(photoGalleryFragment, true, false);
//    }
//
//    public void callChangePwdFragment() {
//        replaceFragment(new PasswordChangeFragment(), true, false);
//    }
//
//    public void updateLanguage() {
//        replaceFragment(new LanguageFragment(), true, false);
//    }
//
//    // Launch casting info page
//    public void startAddingCasting(ArrayList<CastingUser> randomCastingList, int type) {
//        AddCastingInfoFragment addCastingInfoFragment = new AddCastingInfoFragment();
//        Bundle b = new Bundle();
//        b.putSerializable("CastingList", randomCastingList);
//        b.putInt("Type", type);
//        addCastingInfoFragment.setArguments(b);
//        replaceFragment(addCastingInfoFragment, true, false);
//    }
//
//    // Apply sort on list data
//    public void applySort(int type) { // 1 - sort, 2 - type
//        SortFragment sortFragment = new SortFragment();
//        Bundle b = new Bundle();
//        b.putInt("Type", type);
//        sortFragment.setArguments(b);
//        replaceFragment(sortFragment, true, false);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case RuntimePermissionUtils.REQUEST_ACCESS_LOCATION:
//                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    GPSTracker gpsTracker = new GPSTracker(this);
//                    if (gpsTracker.getIsGPSTrackingEnabled()) {
//                        latitude = gpsTracker.latitude;
//                        longitude = gpsTracker.longitude;
//                    } else
//                        gpsTracker.showSettingsAlert();
//                }
//                break;
//        }
//    }
//
//    public void showProgressDialog(boolean isShow) {
//        try {
//            if (isShow) {
//                mProgress.setMessage("Please wait...");
//                mProgress.show();
//            } else
//                mProgress.dismiss();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mProgress != null && mProgress.isShowing())
//            mProgress.dismiss();
//
//    }
//}
