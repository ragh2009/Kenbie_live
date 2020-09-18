package com.kenbie.fragments;


import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.kenbie.KenbieApplication;
import com.kenbie.LoginActivity;
import com.kenbie.MediaFullScreenActivity;
import com.kenbie.R;
import com.kenbie.adapters.ProfileViewStepsAdapter;
import com.kenbie.adapters.ViewPagerAdapter;
import com.kenbie.connection.MConnection;
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

public class UserFullProfileFragment extends BaseFragment implements APIResponseHandler, View.OnClickListener, ProfileOptionListener {
    private UserItem userItem;
    private boolean isEdit;
    private ProfileInfo profileInfo;
    private ArrayList<OptionsData> myDisplayData;
    private TextView userName, userLocation, userLikes, userFav, reportBtn;
    private LinearLayout editProfile;
    private ImageView editIcon, userShare;
    private RecyclerView rvOptions;
    private ViewPager galleryPager;
    private LinearLayout sliderLayout;
    private int dotsCount;
    private ImageView[] dots;

    public UserFullProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userItem = (UserItem) getArguments().getSerializable("UserData");
            if (userItem != null && (userItem.getId() == Integer.valueOf(mActivity.mPref.getString("UserId", "0"))))
                isEdit = true;
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
        reportBtn.setTypeface(KenbieApplication.S_SEMI_BOLD);
        reportBtn.setOnClickListener(this);
        userShare = (ImageView) view.findViewById(R.id.user_share);
        editProfile = (LinearLayout) view.findViewById(R.id.btn_edit);
        editProfile.setOnClickListener(this);
        editIcon = (ImageView) view.findViewById(R.id.edit_icon);

        if (!isEdit) {
            userLikes.setOnClickListener(this);
            userFav.setOnClickListener(this);
//            editProfile.setText(getString(R.string.chat_now_title));
            editIcon.setBackgroundResource(R.drawable.ic_chat_w);
            reportBtn.setVisibility(View.VISIBLE);
        } else {
            editIcon.setBackgroundResource(R.drawable.ic_edit);
            reportBtn.setVisibility(View.GONE);
        }

        userShare.setOnClickListener(this);

        if (mActivity.mPref.getBoolean("isLogin", false)) {
            editProfile.setVisibility(View.VISIBLE);
            reportBtn.setVisibility(View.VISIBLE);
        } else {
            editProfile.setVisibility(View.GONE);
            reportBtn.setVisibility(View.GONE);
        }

        rvOptions = (RecyclerView) view.findViewById(R.id.rv_options);
//        rvOptions.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rvOptions.setLayoutManager(linearLayoutManager);

        getUserProfileDetails();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_share:
                if (mActivity.mPref.getBoolean("isLogin", false))
                    mActivity.shareApp("");
                else
                    startApp();
                break;
            case R.id.user_fav:
                if (mActivity.mPref.getBoolean("isLogin", false))
                    addedUserFavorites();
                else
                    startApp();
                break;
            case R.id.user_likes:
                if (mActivity.mPref.getBoolean("isLogin", false))
                    updateUserLike();
                else
                    startApp();
                break;
            case R.id.report_btn:
                //TODO
                break;
            case R.id.btn_edit:
                if (mActivity.mPref.getBoolean("isLogin", false)) {
                    if (isEdit)
                        mActivity.seeEditOptions(profileInfo);
                    else
                        mActivity.startMessaging(profileInfo);
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
            new MConnection().postRequestWithHttpHeaders(getActivity(), "addLike", this, params, 102);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(getActivity(), "Alert", Constants.NETWORK_FAIL_MSG);
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
            new MConnection().postRequestWithHttpHeaders(getActivity(), "addFavourite", this, params, 103);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(getActivity(), "Alert", Constants.NETWORK_FAIL_MSG);
        }
    }

    // Bind display steps
    private ArrayList<OptionsData> bindDisplaySteps() {
        ArrayList<OptionsData> values = new ArrayList<>();
        String[] name = {"About", "Connect with me", "Information", "Disciplines", "Categories", "Language"};
//        String[] name = {"Gallery", "About", "Connect with me", "Information", "Disciplines", "Categories", "Languages"};
//        Integer[] images = {R.drawable.ic_gallery, R.drawable.ic_about, R.drawable.ic_connect, R.drawable.ic_info, R.drawable.ic_disciplines, R.drawable.ic_categories, R.drawable.ic_language};
        Integer[] images = {R.drawable.ic_about, R.drawable.ic_connect, R.drawable.ic_info, R.drawable.ic_disciplines, R.drawable.ic_categories, R.drawable.ic_language};
        for (int i = 0; i < name.length; i++) {
            OptionsData op = new OptionsData();
            op.setId(i + 1);
            op.setName(name[i]);
            op.setImgId(images[i]);
            if (i == 0) { // About
                if (profileInfo.getAbout_user() != null && !profileInfo.getAbout_user().equalsIgnoreCase("null")) {
                    op.setOptionData(profileInfo.getAbout_user());
                    values.add(op);
                }
            } else if (i == 1) { // Connect with me
                if (profileInfo.getUserSocial() != null && profileInfo.getUserSocial().size() > 0) {
                    op.setOptionDataArrayList(profileInfo.getUserSocial());
                    values.add(op);
                }
            } else if (i == 2) { // Information
                if (profileInfo.getUserInfo() != null && profileInfo.getUserInfo().size() > 0) {
                    op.setOptionDataArrayList(profileInfo.getUserInfo());
                    values.add(op);
                }
            } else if (i == 3) { // Disciplines
                if (profileInfo.getUserDisciplines() != null && profileInfo.getUserDisciplines().size() > 0) {
                    op.setOptionData(bindTextData(profileInfo.getUserDisciplines()));
//                    op.setOptionDataArrayList(profileInfo.getUserDisciplines());
                    values.add(op);
                }
            } else if (i == 4) { // Categories
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
                        value = value.trim() + "  " + userDisciplines.get(i).getName();
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
            new MConnection().postRequestWithHttpHeaders(getActivity(), "getProfileDetail", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(getActivity(), "Alert", Constants.NETWORK_FAIL_MSG);
        }
    }

    @Override
    public void getError(String error, int APICode) {
        if (error != null)
            mActivity.showMessageWithTitle(getActivity(), "Alert", error);
        else
            mActivity.showMessageWithTitle(getActivity(), "Alert", Constants.GENERAL_FAIL_MSG);

        mActivity.showProgressDialog(false);
    }

    @Override
    public void getResponse(String response, int APICode) {
        try {
            if (response != null) {
                JSONObject jo = new JSONObject(response);

                if (APICode == 101) { // profile details
                    if (jo.has("status") && jo.getBoolean("status")) {
                        profileInfo = parseUserProfileData(jo.getString("data"));
                        myDisplayData = bindDisplaySteps();
                        bindGalleryData();
                        setDataOnUi();
                    } else if (jo.has("error"))
                        mActivity.showMessageWithTitle(getActivity(), "Alert", jo.getString("error"));
                    else
                        mActivity.showMessageWithTitle(getActivity(), "Alert", Constants.GENERAL_FAIL_MSG);
                } else if (APICode == 102 || APICode == 103) { //102- Add like  // 103-Add favorites
                    if (jo.has("status") && jo.getBoolean("status") && jo.has("success")) {
                        mActivity.showMessageWithTitle(getActivity(), "Alert", jo.getString("success"));
                        if (APICode == 102) {
                            int userLike = 0;
                            if (userLikes.getTag() != null)
                                userLike = (int) userLikes.getTag();
                            if (userLike == 0) {
                                userLikes.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_dislike, 0, 0);
                                userLikes.setTag(1);
                            } else {
                                userLikes.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_dislike, 0, 0);
                                userLikes.setTag(0);
                            }

                        } else {
                            int userF = 0;
                            if (userFav.getTag() != null)
                                userF = (int) userFav.getTag();

                            if (userF == 0) {
                                userFav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_m_fav, 0, 0);
                                userFav.setTag(1);
                            } else {
                                userFav.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_unfav, 0, 0);
                                userFav.setTag(0);
                            }
                        }
                    } else
                        mActivity.showMessageWithTitle(getActivity(), "Alert", Constants.GENERAL_FAIL_MSG);
                }
            } else
                mActivity.showMessageWithTitle(getActivity(), "Alert", Constants.GENERAL_FAIL_MSG);

        } catch (Exception e) {
            e.printStackTrace();
        }

        mActivity.showProgressDialog(false);
    }

    private void bindGalleryData() {
        try {
            if (profileInfo != null && profileInfo.getGalleryList() != null) {
                ViewPagerAdapter mediaAdapter = new ViewPagerAdapter(mActivity, profileInfo.getGalleryList(), this);
                galleryPager.setAdapter(mediaAdapter);

                dotsCount = mediaAdapter.getCount();
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
        mActivity.showMessageWithTitle(getActivity(), "Alert", Constants.NETWORK_FAIL_MSG);
        mActivity.showProgressDialog(false);
    }

    // Set data
    private void setDataOnUi() {
        try {
            Calendar mCalendar = Calendar.getInstance();
            int mYear = mCalendar.get(Calendar.YEAR);
//            Glide.with(getActivity())
//                    .load(Constants.BASE_IMAGE_URL + profileInfo.getUser_pic())
//                    .into(userBlurImage);

//            Glide.with(getActivity()).load(Constants.BASE_IMAGE_URL + profileInfo.getUser_pic()).apply(RequestOptions.circleCropTransform()).into(userImage);

            if (profileInfo.getCompletedYrs() != 0)
                userName.setText(profileInfo.getFirst_name() + ", " + (profileInfo.getCompletedYrs()));
            else
                userName.setText(profileInfo.getFirst_name());

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

            ProfileViewStepsAdapter pvAdapter = new ProfileViewStepsAdapter(getActivity(), myDisplayData, this);
            rvOptions.setAdapter(pvAdapter);
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
                    userLikes.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_dislike, 0, 0);
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

        if (type == 2) { // Social click action
            if (mActivity.mPref.getBoolean("isLogin", false)) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(value.get(position).getOptionData()));
                startActivity(i);
            } else
                startApp();
        } else {
            // Type - 1
            Intent intent = new Intent(mActivity, MediaFullScreenActivity.class);
            intent.putExtra("MediaData", value);
            intent.putExtra("SelPos", position);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        // TODO
//        if (value.get(position).getId() == 3)
//            mActivity.seeSocialOptions(profileInfo.getUserSocial());
    }

    // Profile info
    private ProfileInfo parseUserProfileData(String data) {
        ProfileInfo profileInfo = new ProfileInfo();
        try {
            JSONObject jo = new JSONObject(data);
            if (jo.has("profile_info")) {
                JSONObject jp = new JSONObject(jo.getString("profile_info"));
                profileInfo.setId(jp.getInt("id"));
                profileInfo.setFirst_name(jp.getString("first_name"));
                profileInfo.setUser_name(jp.getString("user_name"));
                profileInfo.setSocial_id(jp.getString("social_id"));
                profileInfo.setSurname(jp.getString("surname"));
                profileInfo.setUser_type(jp.getString("user_type"));
                profileInfo.setUser_pic(jp.getString("user_pic"));
                profileInfo.setEmail_id(jp.getString("email_id"));
                if (jp.getString("birth_year") != null && !jp.getString("birth_year").equalsIgnoreCase("null"))
                    profileInfo.setBirth_year(jp.getString("birth_year"));

                if (jp.getString("birth_month") != null && !jp.getString("birth_month").equalsIgnoreCase("null"))
                    profileInfo.setBirth_month(jp.getString("birth_month"));

                if (jp.getString("birth_day") != null && !jp.getString("birth_day").equalsIgnoreCase("null"))
                    profileInfo.setBirth_day(jp.getString("birth_day"));

                profileInfo.setCompany_name(jp.getString("company_name"));
                profileInfo.setPhone(jp.getString("phone"));
                profileInfo.setWebsite(jp.getString("website"));
                profileInfo.setSeen_type(jp.getString("seen_type"));
                profileInfo.setDiscipline(jp.getString("discipline"));
                profileInfo.setSocialType(jp.getString("social_type"));

                if (jp.getString("gender") != null)
                    if (jp.getString("gender").equalsIgnoreCase("Male"))
                        profileInfo.setGender(1);
                    else
                        profileInfo.setGender(2);

                profileInfo.setIs_active(jp.getInt("is_active"));
                profileInfo.setIs_deleted(jp.getInt("is_deleted"));
                profileInfo.setAdded_by(jp.getInt("added_by"));

                profileInfo.setAbout_user(jp.getString("about_user"));
//                String[] sData = {"Ethnicity", "Gender", "Model", "Agency", "Height", "Eyes", "Hair", "Bust", "Waist", "Hips", "Dress", "Shoes"};

                ArrayList<String> userInfo = new ArrayList<>();
                profileInfo.setUser_ethinicity(jp.getString("user_ethinicity"));
                userInfo.add(jp.getString("user_ethinicity"));
                userInfo.add(profileInfo.getGender() == 1 ? "Male" : "Female");
                profileInfo.setUser_face(jp.getString("user_face"));
                userInfo.add(jp.getString("user_face"));
                userInfo.add(setUserType(jp.getString("user_type")));
                profileInfo.setUser_height(jp.getString("user_height"));
                userInfo.add(jp.getString("user_height"));
                profileInfo.setUser_eye_color(jp.getString("user_eye_color"));
                userInfo.add(jp.getString("user_eye_color"));
                profileInfo.setUser_hair_color(jp.getString("user_hair_color"));
                userInfo.add(jp.getString("user_hair_color"));
                profileInfo.setUser_bust(jp.getString("user_bust"));
                userInfo.add(jp.getString("user_bust"));
                profileInfo.setUser_waist(jp.getString("user_waist"));
                userInfo.add(jp.getString("user_waist"));
                profileInfo.setUser_hips(jp.getString("user_hips"));
                userInfo.add(jp.getString("user_hips"));
                profileInfo.setUser_dress(jp.getString("user_dress"));
                userInfo.add(jp.getString("user_dress"));
                profileInfo.setUser_shoes(jp.getString("user_shoes"));
                userInfo.add(jp.getString("user_shoes"));
                profileInfo.setUser_weight(jp.getString("user_weight"));

                profileInfo.setUser_language(jp.getString("user_language"));
                profileInfo.setUser_from_agency(jp.getString("user_from_agency"));
                profileInfo.setUser_categories(jp.getString("user_categories"));
                profileInfo.setUser_disciplines(jp.getString("user_disciplines"));
                profileInfo.setUserInfo(bindUserInfo(userInfo));

                profileInfo.setAddress1(jp.getString("address1"));
                profileInfo.setAddress2(jp.getString("address2"));
                profileInfo.setCountry(jp.getString("country"));
                profileInfo.setCity(jp.getString("city"));
                profileInfo.setPostal_code(jp.getString("postal_code"));
                ArrayList<String> socialDetails = new ArrayList<>();
                profileInfo.setFb(jp.getString("fb"));
                socialDetails.add(jp.getString("fb"));
                profileInfo.setInsta(jp.getString("insta"));
                socialDetails.add(jp.getString("insta"));
                profileInfo.setTwitter(jp.getString("twitter"));
                socialDetails.add(jp.getString("twitter"));
                profileInfo.setGoogle(jp.getString("google"));
                socialDetails.add(jp.getString("google"));
                profileInfo.setYandex(jp.getString("yandex"));
                socialDetails.add(jp.getString("yandex"));
                profileInfo.setYoutube(jp.getString("youtube"));
                socialDetails.add(jp.getString("youtube"));
                profileInfo.setLinkedin(jp.getString("linkedin"));
                socialDetails.add(jp.getString("linkedin"));
                profileInfo.setPinterest(jp.getString("pinterest"));
                socialDetails.add(jp.getString("pinterest"));
                profileInfo.setUserSocial(bindSocialLink(socialDetails));
            }

            if (jo.has("userCountData"))
                profileInfo.setUserCountData(jo.getString("userCountData"));
            profileInfo.setAllimgs(jo.getString("allimgs"));

            profileInfo.setList_options(jo.getString("list_options"));
            JSONObject listOptions = new JSONObject(profileInfo.getList_options());
            profileInfo.setCategoriesData(listOptions.getString("categories"));
            profileInfo.setArrdisciplines(listOptions.getString("arrdisciplines"));
            profileInfo.setUserDisciplines(getMyData(listOptions.getString("arrdisciplines"), profileInfo.getUser_disciplines()));
            profileInfo.setUserCategories(getMyData(listOptions.getString("categories"), profileInfo.getUser_categories()));
            profileInfo.setUserLanguages(getMyLanguageData(profileInfo.getUser_language()));
            profileInfo.setGalleryList(getGalleryImages(profileInfo.getAllimgs()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return profileInfo;
    }

    private String setUserType(String user_type) {
        String userType = "";
        try {
            int value = Integer.valueOf(user_type);
            if (value == 1)
                userType = "Model";
            else if (value == 2)
                userType = "Photographer";
            else
                userType = "Agency";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userType;
    }

    // Language data
    private ArrayList<OptionsData> getMyLanguageData(String user_language) {
        ArrayList<OptionsData> values = new ArrayList<>();
        try {
            if (user_language != null && !user_language.equalsIgnoreCase("null")) {
                JSONArray ja = new JSONArray(user_language);
                for (int i = 0; i < ja.length(); i++) {
                    OptionsData od = new OptionsData();
                    od.setId((i + 1));
                    od.setName(ja.getJSONObject(i).getString("name"));
                    od.setOptionCode(ja.getJSONObject(i).getString("id"));
                    values.add(od);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    // Gallery images
    private ArrayList<OptionsData> getGalleryImages(String allimgs) {
        ArrayList<OptionsData> values = new ArrayList<>();
        try {
            if (allimgs != null && allimgs.length() > 10) {
                JSONArray imageArray = new JSONArray(allimgs);

                for (int i = 0; i < imageArray.length(); i++) {
                    OptionsData value = new OptionsData();
                    value.setId((i + 1));
                    value.setName(imageArray.getString(i));
                    values.add(value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }

    // Bind user info
    private ArrayList<OptionsData> bindUserInfo(ArrayList<String> userInfo) {
        String[] sData = {"Ethnicity", "Gender", "Model", "Agency", "Height", "Eyes", "Hair", "Bust", "Waist", "Hips", "Dress", "Shoes"};
        ArrayList<OptionsData> values = new ArrayList<>();
        for (int i = 0; i < sData.length; i++) {
            OptionsData od = new OptionsData();
            od.setId(i + 1);
            od.setName(sData[i]);
            String value = userInfo.get(i);
            if (value != null && !value.equalsIgnoreCase("null") && value.length() > 0) {
                od.setOptionData(userInfo.get(i));
                values.add(od);
            }
        }
        return values;
    }

    // Binding social data
    private ArrayList<OptionsData> bindSocialLink(ArrayList<String> socialDetails) {
        ArrayList<OptionsData> values = new ArrayList<>();
        String[] sData = {"Facebook", "Instagram", "Twitter", "Google+", "Yandex", "YouTube", "Linked In", "Pinterest"};
        Integer[] sImages = {R.drawable.is_fb, R.drawable.ic_instagram, R.drawable.is_twitter, R.drawable.is_gplus, R.drawable.ic_yandex, R.drawable.ic_youtube, R.drawable.ic_linkedin, R.drawable.is_pinterest};
        try {
            for (int i = 0; i < sData.length; i++) {
                OptionsData od = new OptionsData();
                od.setId((i + 1));
                od.setName(sData[i]);
                od.setImgId(sImages[i]);
                if (socialDetails.get(i) != null && socialDetails.get(i).length() > 0 && !socialDetails.get(i).equalsIgnoreCase("null")) {
                    od.setOptionData(socialDetails.get(i));
                    od.setActive(true);
                    values.add(od);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }

    private ArrayList<OptionsData> getMyData(String options, String myData) {
        ArrayList<Integer> values = new ArrayList<>();
        ArrayList<OptionsData> optionsDataEthnicity = new ArrayList<>();
        try {
            if (myData != null && !myData.equalsIgnoreCase("null")) {
                String[] mData = myData.replace(",", "-").split("-");
                if (mData != null)
                    for (int i = 0; i < mData.length; i++)
                        try {
                            values.add(Integer.valueOf(mData[i]));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

            }

            JSONArray userEthnicity = new JSONArray(options);
            for (int i = 0; i < userEthnicity.length(); i++) {
                OptionsData od = new OptionsData();
                JSONObject jod = new JSONObject(userEthnicity.getString(i));
                od.setId(jod.getInt("id"));
                od.setName(jod.getString("name"));
                if (jod.has("shortname"))
                    od.setOptionCode(jod.getString("shortname"));

                if (values.indexOf(od.getId()) != -1) {
                    od.setActive(true);
                    optionsDataEthnicity.add(od);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return optionsDataEthnicity;
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(1, null, false, true, false);
        super.onResume();
    }
}
