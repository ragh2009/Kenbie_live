package com.kenbie.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.adapters.SettingUpdateAdapter;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.listeners.ProfileOptionListener;
import com.kenbie.model.OptionsData;
import com.kenbie.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PrivacyFragment extends BaseFragment implements APIResponseHandler, ProfileOptionListener {
    private RadioGroup pRadioGroup;
    private RadioButton firstRb, secRb, thirdRb;
    private TextView pViewTitle;
    private int status;
    private ArrayList<OptionsData> pSettingData = new ArrayList<>();
    private SettingUpdateAdapter mAdapter = null;
    private ListView privacyList;

    public PrivacyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_privacy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pRadioGroup = ((RadioGroup) view.findViewById(R.id.p_rb_options));
        firstRb = ((RadioButton) view.findViewById(R.id.p_no_one_rb));
        secRb = ((RadioButton) view.findViewById(R.id.p_only_mem_rb));
        thirdRb = ((RadioButton) view.findViewById(R.id.p_all_see_rb));
        pViewTitle = view.findViewById(R.id.p_view_title);
        pViewTitle.setTypeface(KenbieApplication.S_NORMAL);
        firstRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status != 1) {
                    status = 1;
                    updatePrivacySettings(status);
                }
            }
        });

        secRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status != 2) {
                    status = 2;
                    updatePrivacySettings(status);
                }
            }
        });

        thirdRb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status != 0) {
                    status = 0;
                    updatePrivacySettings(status);
                }
            }
        });

        privacyList = (ListView) view.findViewById(R.id.privacy_list);
        privacyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                startAction(position);
            }
        });


//        ((TextView) view.findViewById(R.id.save_btn)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int selectedId = pRadioGroup.getCheckedRadioButtonId();
//                if (selectedId < 0)
//                    mActivity.showMessageWithTitle(mActivity, "Alert!", "Please select a option.");
//                else {
//                    if (selectedId == R.id.p_no_one_rb)
//                        status = 1;
//                    else if (selectedId == R.id.p_only_mem_rb)
//                        status = 2;
//                    else if (selectedId == R.id.p_all_see_rb)
//                        status = 0;
//                    updatePrivacySettings(status);
//                }
//            }
//        });

        getPrivacySettings();
    }

    private void getPrivacySettings() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            new MConnection().postRequestWithHttpHeaders(mActivity, "getPrivacySetting", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void updatePrivacySettings(int option) {
        if (mActivity.isOnline()) {
//            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("option", option + ""); // Option 1 - 1, Option 2 - 2, Option 3 - 0
            new MConnection().postRequestWithHttpHeaders(mActivity, "updatePrivacySetting", this, params, 102);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void getOtherPrivacySettings() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            new MConnection().postRequestWithHttpHeaders(mActivity, "getOtherPrivacySetting", this, params, 103);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void updateOtherPrivacySetting(OptionsData optionsData) {
        if (mActivity.isOnline()) {
//            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("privacy_id", optionsData.getId() + "");
            params.put("option", optionsData.isActive() ? "1" : "0");
            new MConnection().postRequestWithHttpHeaders(mActivity, "updateOtherPrivacySetting", this, params, 104);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    @Override
    public void getError(String error, int APICode) {
        if (error != null)
            if (error.equalsIgnoreCase("com.android.volley.error.AuthFailureError"))
                mActivity.logoutProcess();
            else
                mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), error);
        else
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));

        mActivity.showProgressDialog(false);
    }

    @Override
    public void getResponse(String response, int APICode) {
        try {
            JSONObject jMain = new JSONObject(response);
            if (jMain.has("data")) {
                if (APICode == 101) { // Getting privacy setting
                    JSONObject jData = jMain.getJSONObject("data");
                    JSONArray jPrivacy = new JSONArray(jData.getString("privacy_setting"));
                    for (int i = 0; i < jPrivacy.length(); i++) {
                        JSONObject jo = jPrivacy.getJSONObject(i);
                        pViewTitle.setText(jo.getString("label"));
                        firstRb.setText(jo.getString("option1"));
                        secRb.setText(jo.getString("option2"));
                        thirdRb.setText(jo.getString("option3"));
                        status = jo.getInt("status");
                        firstRb.setVisibility(View.VISIBLE);
                        secRb.setVisibility(View.VISIBLE);
                        thirdRb.setVisibility(View.VISIBLE);
                        if (status == 1) // No One
                            firstRb.setChecked(true);
                        else if (status == 2) // Only Member
                            secRb.setChecked(true);
                        else // All See
                            thirdRb.setChecked(true);
                    }

                    parseOtherPrivacySettings(jData.getString("other_privacy_setting"));
                    mActivity.showProgressDialog(false);
//                    getOtherPrivacySettings();
                } else if (APICode == 102) { // Update privacy setting
                    if (status == 1) // No One
                        firstRb.setChecked(true);
                    else if (status == 2) // Only Member
                        secRb.setChecked(true);
                    else // All See
                        thirdRb.setChecked(true);
                    mActivity.showProgressDialog(false);
                } else if (APICode == 103) { // Getting other privacy settings
                    parseOtherPrivacySettings(jMain.getString("data"));
                    mActivity.showProgressDialog(false);
                } else if (APICode == 104) { // Getting other privacy settings
//                    parseOtherPrivacySettings(jMain.getString("data"));
                    mActivity.showProgressDialog(false);
                }
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


    private void parseOtherPrivacySettings(String data) {
        try {
            pSettingData = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 1; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                OptionsData od = new OptionsData();
                od.setId(jsonObject.getInt("id"));
                od.setName(jsonObject.getString("label"));
                od.setActive(jsonObject.getInt("status") == 1);
                pSettingData.add(od);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        mAdapter = new SettingUpdateAdapter(mActivity, pSettingData, this);
        privacyList.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(100, mActivity.mPref.getString("185", "PRIVACY"), false, true, false);
        super.onResume();
    }

    @Override
    public void getAction(OptionsData value) {

    }

    @Override
    public void getDataList(ArrayList<OptionsData> value, int position, int type) {
        try {
            pSettingData.get(position).setActive(!value.get(position).isActive());
            mAdapter.refreshData(pSettingData);
            updateOtherPrivacySetting(pSettingData.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
