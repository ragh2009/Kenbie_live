package com.kenbie.fragments;


import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.adapters.UpdateLanguageListAdapter;
import com.kenbie.connection.MConnection;
import com.kenbie.data.ProfileDataParser;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.listeners.LanguageListener;
import com.kenbie.model.OptionsData;
import com.kenbie.model.ProfileInfo;
import com.kenbie.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class UpdateLanguageListFragment extends BaseFragment implements APIResponseHandler, LanguageListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener{
    private ListView mLanguageListView;
    private UpdateLanguageListAdapter updateLanguageListAdapter;
    private ArrayList<OptionsData> mLanguageData;
    private ProfileInfo profileInfo;
    private EditText searchUser = null;
//    private SwipeRefreshLayout mySwipeRefreshLayout;
    private TextView mSaveBtn = null;

    public UpdateLanguageListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            profileInfo = (ProfileInfo) getArguments().getSerializable("ProfileInfo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_language_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout topBar = view.findViewById(R.id.top_bar);
        topBar.setVisibility(View.VISIBLE);
        ImageView backBtn = view.findViewById(R.id.m_back_button);
        backBtn.setOnClickListener(this);
        TextView screenTitle = view.findViewById(R.id.m_title);
        screenTitle.setTypeface(KenbieApplication.S_NORMAL);
        screenTitle.setText(mActivity.mPref.getString("266", "LANGUAGE"));

        mSaveBtn = view.findViewById(R.id.m_save_btn);
        mSaveBtn.setText( mActivity.mPref.getString("225", "Save"));
        mSaveBtn.setTypeface(KenbieApplication.S_NORMAL);
        mSaveBtn.setOnClickListener(this);
        mSaveBtn.setVisibility(View.INVISIBLE);

//        mySwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
//        mySwipeRefreshLayout.setColorSchemeResources(
//                R.color.red_g_color);
//        mySwipeRefreshLayout.setOnRefreshListener(this);

        searchUser = view.findViewById(R.id.search_user);
        searchUser.setHint(mActivity.mPref.getString("58", "Search"));
        searchUser.setTypeface(KenbieApplication.S_NORMAL);
        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                updateLanguageListAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mLanguageListView = (ListView) view.findViewById(R.id.m_user_list);

        bindLanguageData();
    }

    private void bindLanguageData() {
        try {
            mLanguageData = getMyLanguageData(profileInfo.getUser_language());
            if (mLanguageData == null)
                mLanguageData = new ArrayList<>();

            /* Removed for language list part*/
/*            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mLanguageData.sort(Comparator.comparing(OptionsData::isActive).reversed());
            }*/
//            Collections.sort(mLanguageData);
            refreshData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Language data
    private ArrayList<OptionsData> getMyLanguageData(String user_language) {
        ArrayList<String> myLang = new ArrayList<>();
        ArrayList<OptionsData> values = new ArrayList<>();
        try {
            ArrayList<OptionsData> userLangData = profileInfo.getUserLanguages();
            if (userLangData == null)
                userLangData = new ArrayList<>();
            for (int i = 0; i < userLangData.size(); i++)
                myLang.add(userLangData.get(i).getOptionCode());

            if (user_language != null && !user_language.equalsIgnoreCase("null")) {
                JSONArray ja = new JSONArray(user_language);
                for (int i = 1; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    OptionsData od = new OptionsData();
                    od.setId(jo.getInt("id"));
                    od.setName(jo.getString("name"));
                    if (jo.has("shortname"))
                        od.setOptionCode(jo.getString("shortname"));
                    else
                        od.setOptionCode("");

                    if (myLang.indexOf(od.getOptionCode()) != -1)
                        od.setActive(true);

                    values.add(od);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    @Override
    public void getSelectedMessageInfo(OptionsData selValue) {
        try {
            int selPosition = mLanguageData.indexOf(selValue);
            mLanguageData.get(selPosition).setActive(!mLanguageData.get(selPosition).isActive());
            /* Removed for language list part*/
//            OptionsData value = mLanguageData.get(selPosition);
//            mLanguageData.remove(selPosition);
//            mLanguageData.add(0, value );
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                mLanguageData.sort(Comparator.comparing(OptionsData::isActive).reversed());
//            }
            updateLanguageListAdapter.refreshData(mLanguageData);
            searchUser.setText("");
            mSaveBtn.performClick();

//            Collections.sort(mLanguageData);
//            if(searchUser.getText().toString().equalsIgnoreCase(""))
//                updateLanguageListAdapter.refreshData(mLanguageData);
//            else
//                updateLanguageListAdapter.refreshSearchData(mLanguageData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Refresh user data
    private void refreshData() {
        updateLanguageListAdapter = new UpdateLanguageListAdapter(mActivity, mLanguageData, this);
        mLanguageListView.setAdapter(updateLanguageListAdapter);
    }


    private void updateUserLanguages() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("user_language", bindSelectedLanguage() + "");
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "updateUserLanguage", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private String bindSelectedLanguage() {
        String value = "";
        if (mLanguageData == null)
            mLanguageData = new ArrayList<>();
        for (int i = 0; i < mLanguageData.size(); i++) {
            if (mLanguageData.get(i).isActive()) {
                if (value.length() == 0)
                    value = mLanguageData.get(i).getOptionCode();
                else
                    value = value + "," + mLanguageData.get(i).getOptionCode();
            }
        }
        return value;
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
            new MConnection().postRequestWithHttpHeaders(mActivity, "getProfileDetail", this, params, 102);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    @Override
    public void getError(String error, int APICode) {
        if (error != null)
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

                if (APICode == 101) {
                    if (jo.has("status") && jo.getBoolean("status")) {
                        if(jo.has("success")){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                Collections.sort(mLanguageData, Comparator.comparing(OptionsData::isActive));
//                                mLanguageData.sort(Comparator.comparing(OptionsData::isActive).reversed());
                                getUserProfileDetails();
                            }
                        }
//                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("success"));
                    } else if (jo.has("error"))
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("error"));
                    else
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                    mActivity.showProgressDialog(false);
                } else if (APICode == 102) {
                    profileInfo = new ProfileDataParser().parseUserProfileData(jo.getString("data"), mActivity.mPref);
                    try {
                        mLanguageData = getMyLanguageData(profileInfo.getUser_language());
                        if (mLanguageData == null)
                            mLanguageData = new ArrayList<>();
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            mLanguageData.sort(Comparator.comparing(OptionsData::isActive).reversed());
//                        }
                        updateLanguageListAdapter.refreshData(mLanguageData);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mActivity.showProgressDialog(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mActivity.showProgressDialog(false);
        }
    }


    @Override
    public void networkError(String error, int APICode) {
        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        mActivity.showProgressDialog(false);
    }


    @Override
    public void onResume() {
        super.onResume();
        mActivity.updateActionBar(17,  mActivity.mPref.getString("266", "LANGUAGE"), false, true, true);
    }

    @Override
    public void onRefresh() {
//        mySwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.m_save_btn)
            updateUserLanguages();
        else if (v.getId() == R.id.m_back_button)
            mActivity.onBackPressed();
    }
}
