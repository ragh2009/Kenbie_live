package com.kenbie.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.model.ProfileInfo;
import com.kenbie.util.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class AboutEditFragment extends BaseFragment implements APIResponseHandler, View.OnClickListener {
    private ProfileInfo profileInfo;
    private EditText etAbout;


    public AboutEditFragment() {
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
        return inflater.inflate(R.layout.fragment_about_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView backBtn = view.findViewById(R.id.m_back_button);
        backBtn.setOnClickListener(this);
        TextView screenTitle = view.findViewById(R.id.m_title);
        screenTitle.setTypeface(KenbieApplication.S_NORMAL);
        screenTitle.setText(mActivity.mPref.getString("235", "ABOUT ME"));
        TextView saveBtn = view.findViewById(R.id.m_save_btn);
        saveBtn.setText(mActivity.mPref.getString("225", "Save"));
        saveBtn.setTypeface(KenbieApplication.S_NORMAL);
        saveBtn.setOnClickListener(this);

        etAbout = (EditText) view.findViewById(R.id.et_about);
        etAbout.setHint(mActivity.mPref.getString("220", "About Me"));
        etAbout.setTypeface(KenbieApplication.S_NORMAL);
        if (profileInfo.getAbout_user() != null && !profileInfo.getAbout_user().equalsIgnoreCase("null"))
            etAbout.setText(profileInfo.getAbout_user());
    }

    private void updateUserInfoOnServer() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("about_user", etAbout.getText().toString());
            new MConnection().postRequestWithHttpHeaders(mActivity, "updateAboutMe", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(14, "ABOUT ME", false, true, true);
        super.onResume();
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
//                    mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("success"));

                    if (jo.has("status") && jo.getBoolean("status") && jo.has("success"))
                        mActivity.onBackPressed();
                    else
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        mActivity.showProgressDialog(false);
    }

    @Override
    public void networkError(String error, int APICode) {
        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        mActivity.showProgressDialog(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.m_save_btn) {
            if (!etAbout.getText().toString().equalsIgnoreCase(""))
                updateUserInfoOnServer();
            else
                mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("354", "Please write something about yourself"));

        } else if (v.getId() == R.id.m_back_button)
            mActivity.onBackPressed();
    }
}
