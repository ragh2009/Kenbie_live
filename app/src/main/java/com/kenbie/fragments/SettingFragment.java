package com.kenbie.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kenbie.R;
import com.kenbie.adapters.SettingAdapter;


public class SettingFragment extends BaseFragment {
    private String[] sOptions = {"My Castings", "Notifications", "Language", "Membership", "Change your password", "Privacy", "Kenbie", "Account"};
//    private String[] sOptions = {mActivity.mPref.getString("150", "My Castings"), mActivity.mPref.getString("151", "Notifications"), mActivity.mPref.getString("93", "Languages"), mActivity.mPref.getString("152", "Membership"),mActivity.mPref.getString("153", "Change Your Password"), mActivity.mPref.getString("154", "Privacy"), mActivity.mPref.getString("155", "Company"),  mActivity.mPref.getString("156", "Account")};
//    private String[] sOptions = {"Change your password", "Notifications", "Language", "Privacy", "Membership", "Company", "Account"};
    private String[] castingOptions = {"Casting Details", "Preferences", "Categories"};
//    private String[] castingOptions = {{mActivity.mPref.getString("150", "Casting Details"), "Preferences", "Categories"};
    private int mType = 1;
    private SettingAdapter mAdapter = null;

    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            mType = getArguments().getInt("Type", 1);
        bindData();
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

        LinearLayout topBar = view.findViewById(R.id.top_bar);
        topBar.setVisibility(View.GONE);

        ListView settingList = (ListView) view.findViewById(R.id.setting_list);
        settingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startAction(position);
            }
        });

        mAdapter = new SettingAdapter(getActivity(), mType == 1 ? sOptions : castingOptions);
        settingList.setAdapter(mAdapter);
    }

    private void startAction(int position) {
//        {"My Castings", "Notifications", "Language", "Membership", "Change your password", "Privacy", "Company", "Account"};
        if (mType == 1) {
            if (position == 0)  // My Castings
                mActivity.launchCasting( 3);
            else if (position == 1)  // Notification
                mActivity.updateUserSettings(20, null);
            else if (position == 2)  // Language
                mActivity.updateLanguage();
            else if (position == 3) // Membership
                mActivity.replaceFragment(new MembershipFragment(), true, false);
            else if (position == 4)  // Change your password
                mActivity.callChangePwdFragment();
            else if (position == 5)  // Privacy
                mActivity.replaceFragment(new PrivacyFragment(), true, false);
            else if (position == 6)  // Company
                mActivity.replaceFragment(new CompanyFragment(), true, false);
            else if (position == 7)  // Account
                mActivity.replaceFragment(new AccountFragment(), true, false);
        } else {
            if(position == 0) // Casting Details
                mActivity.replaceFragment(new EditCastingDetailsFragment(), true, false);
        }
    }

     @Override
    public void onResume() {
        bindData();
        if (mType == 1)
            mActivity.updateActionBar(5, mActivity.mPref.getString("149", "SETTINGS"), false, true, false);
        else
            mActivity.updateActionBar(23, mActivity.mPref.getString("144", "ADD CASTING"), false, true, false);
        refreshData();
        mActivity.hideKeyboard(mActivity);
        super.onResume();
    }

    private void refreshData(){
        mAdapter.refreshData(mType == 1 ? sOptions : castingOptions);
    }

    private void bindData(){
        sOptions[0] = mActivity.mPref.getString("150", "My Castings");
        sOptions[1] = mActivity.mPref.getString("151", "Notifications");
        sOptions[2] = mActivity.mPref.getString("93", "Languages");
        sOptions[3] = mActivity.mPref.getString("152", "Membership");
        sOptions[4] = mActivity.mPref.getString("153", "Change Your Password");
        sOptions[5] = mActivity.mPref.getString("154", "Privacy");
//        sOptions[6] = mActivity.mPref.getString("155", "Kenbie");
        sOptions[6] = "Kenbie";
        sOptions[7] = mActivity.mPref.getString("156", "Account");
    }
}
