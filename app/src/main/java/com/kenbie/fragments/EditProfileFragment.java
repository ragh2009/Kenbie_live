package com.kenbie.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.kenbie.R;
import com.kenbie.adapters.SettingAdapter;
import com.kenbie.connection.MConnection;
import com.kenbie.data.ProfileDataParser;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.model.ProfileInfo;
import com.kenbie.util.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class EditProfileFragment extends BaseFragment implements APIResponseHandler {
    // V 1.2 removed About Me
//    private String[] editOptions = {"Your Basic Information", "Information", "About Me", "Photo Gallery", "Discipline", "Categories", "Social Links", "Statistics", "Language"};

    private String[] editOptions = {"Your Basic Information", "Information", "Photo Gallery", "Discipline", "Categories", "Social Links", "Statistics", "Language"};

    //    private String[] editOptions = {mActivity.mPref.getString("219", "Your Basic Information"), mActivity.mPref.getString("211", "Information"), mActivity.mPref.getString("220", "About Me"), mActivity.mPref.getString("221", "Photo Gallery"),  mActivity.mPref.getString("81", "Discipline"), mActivity.mPref.getString("112", "Categories"),  mActivity.mPref.getString("222", "Social Links"), mActivity.mPref.getString("223", "Statistics"), mActivity.mPref.getString("93", "Language")};
    //Old- private String[] editOptions = {"Your Basic Information", "Social Links", "Statistics", "Casting Settings", "Photo Gallery", "Who Can See The Profile?", "About Me", "Information", "Discipline", "Categories"};
    private ProfileInfo profileInfo;
    private int userType;
    private ListView settingList = null;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            profileInfo = (ProfileInfo) getArguments().getSerializable("ProfileInfo");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (profileInfo.getUser_type() != null && profileInfo.getUser_type().length() > 0)
            userType = Integer.valueOf(profileInfo.getUser_type());
//        1 - Model, 3-Photographer, 2-Agency


        LinearLayout topBar = view.findViewById(R.id.top_bar);
        topBar.setVisibility(View.GONE);

        settingList = (ListView) view.findViewById(R.id.setting_list);
        settingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//New ("Your Basic Information", "Information", "About Me", "Photo Gallery", "Discipline", "Categories", "Social Links", "Statistics", "Language"};
//Old - {"Your Basic Information", "Social Links", "Statistics", "Casting Settings", "Photo Gallery", "Who Can See The Profile?", "About Me", "Information", "Discipline", "Categories"}

                if (userType == 1) {
                    if (position == 0) // Your Basic Information
                        mActivity.editBasicInfo(profileInfo, 0);
                    else if (position == 1) // Information
                        mActivity.updateUserInformation(profileInfo);
                        // V 1.2 removed About Me
                   /* else if (position == 2) // About Me
                        mActivity.updateUserAboutInfo(profileInfo);*/
                    else if (position == 2) // Photo Gallery
                        mActivity.viewPhotoGallery(profileInfo);
                    else if (position == 3) // 8 - Discipline
                        mActivity.updateUserSettings(8, profileInfo);
                    else if (position == 4) //  9 - Categories
                        mActivity.updateUserSettings(9, profileInfo);
                    else if (position == 5) // Social Links
                        mActivity.seeSocialOptions(profileInfo.getUserSocial());
                    else if (position == 6) { // Statistics
                        mActivity.seeUserStatisticsFragment(profileInfo);
                    } else if (position == 7)
                        mActivity.viewLanguageUpdateList(profileInfo);
                } else {
                    if (position == 0) // Your Basic Information
                        mActivity.editBasicInfo(profileInfo, 0);
                        // V 1.2 removed About Me
                  /*  else if (position == 1) // About Me
                        mActivity.updateUserAboutInfo(profileInfo);*/
                    else if (position == 1) // Photo Gallery
                        mActivity.viewPhotoGallery(profileInfo);
                    else if (position == 2) // Social Links
                        mActivity.seeSocialOptions(profileInfo.getUserSocial());
                    else if (position == 3) { // Statistics
                        mActivity.seeUserStatisticsFragment(profileInfo);
                    } else if (position == 4)
                        mActivity.viewLanguageUpdateList(profileInfo);
                }
//                else if(position == 3 || position == 5 || position == 8 || position == 9) // 3-"Casting Settings", 5 - "Who Can See The Profile?", 8 - Discipline, 9 - Categories
//                    mActivity.updateUserSettings(position, profileInfo);
            }
        });
    }

    @Override
    public void onResume() {
        optionAdded();
        mActivity.updateActionBar(7, mActivity.mPref.getString("218", "EDIT PROFILE"), false, true, false);
        getUserProfileDetails();
        super.onResume();
    }

    @Override
    public void networkError(String error, int APICode) {
        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        mActivity.showProgressDialog(false);
    }

    private void getUserProfileDetails() {
        if (mActivity.isOnline()) {
            //   mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            if (profileInfo != null)
                params.put("profile_user_id", profileInfo.getId() != 0 ? profileInfo.getId() + "" : mActivity.mPref.getString("UserId", ""));
            else
                params.put("profile_user_id", mActivity.mPref.getString("UserId", ""));
            new MConnection().postRequestWithHttpHeaders(mActivity, "getProfileDetail", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    @Override
    public void getResponse(String response, int APICode) {
        try {
            if (response != null) {
                JSONObject jo = new JSONObject(response);

                if (APICode == 101) // profile details
                    if (jo.has("status") && jo.getBoolean("status")) {
                        profileInfo = new ProfileDataParser().parseUserProfileData(jo.getString("data"), mActivity.mPref);
                        if (profileInfo.getUser_type() != null && profileInfo.getUser_type().length() > 0)
                            userType = Integer.valueOf(profileInfo.getUser_type());
                        optionAdded();
                    }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mActivity.showProgressDialog(false);
    }


    @Override
    public void getError(String error, int APICode) {
        if (error != null)
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), error);
        else
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));

        mActivity.showProgressDialog(false);
    }


    private void optionAdded() {
        if (userType == 1) {
            editOptions = new String[8];
            editOptions[0] = mActivity.mPref.getString("219", "Your Basic Information");
            editOptions[1] = mActivity.mPref.getString("211", "Information");
            // V 1.2 removed About Me
         //   editOptions[2] = mActivity.mPref.getString("220", "About Me");
            editOptions[2] = mActivity.mPref.getString("221", "Photo Gallery");
            editOptions[3] = mActivity.mPref.getString("81", "Discipline");
            editOptions[4] = mActivity.mPref.getString("112", "Categories");
            editOptions[5] = mActivity.mPref.getString("222", "Social Links");
            editOptions[6] = mActivity.mPref.getString("223", "Statistics");
            editOptions[7] = mActivity.mPref.getString("93", "Language");
        } else {
            editOptions = new String[5];
            editOptions[0] = mActivity.mPref.getString("219", "Your Basic Information");
            // V 1.2 removed About Me
//            editOptions[1] = mActivity.mPref.getString("220", "About Me");
            editOptions[1] = mActivity.mPref.getString("221", "Photo Gallery");
            editOptions[2] = mActivity.mPref.getString("222", "Social Links");
            editOptions[3] = mActivity.mPref.getString("223", "Statistics");
            editOptions[4] = mActivity.mPref.getString("93", "Language");
        }

        SettingAdapter mAdapter = new SettingAdapter(mActivity, editOptions);
        settingList.setAdapter(mAdapter);
    }
}

