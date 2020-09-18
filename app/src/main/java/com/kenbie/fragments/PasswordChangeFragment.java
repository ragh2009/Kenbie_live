package com.kenbie.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
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
import com.kenbie.util.Constants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PasswordChangeFragment extends BaseFragment implements APIResponseHandler, View.OnClickListener {
    private EditText etOldPwd, etNewPwd, etConfirmPwd;

    public PasswordChangeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password_change, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView backBtn = view.findViewById(R.id.m_back_button);
        backBtn.setOnClickListener(this);
        TextView screenTitle = view.findViewById(R.id.m_title);
        screenTitle.setTypeface(KenbieApplication.S_NORMAL);
        screenTitle.setText(mActivity.mPref.getString("177", "CHANGE PASSWORD"));
        TextView saveBtn = view.findViewById(R.id.m_save_btn);
        saveBtn.setText(mActivity.mPref.getString("225", "Save"));
        saveBtn.setTypeface(KenbieApplication.S_NORMAL);
        saveBtn.setOnClickListener(this);

        TextView oldPwdTxt = (TextView) view.findViewById(R.id.t_old_pwd);
        oldPwdTxt.setTypeface(KenbieApplication.S_NORMAL);
        oldPwdTxt.setText(mActivity.mPref.getString("178", "Old Password"));

        etOldPwd = (EditText) view.findViewById(R.id.et_old_pwd);
        etOldPwd.setHint(mActivity.mPref.getString("178", "Old Password"));
        etOldPwd.setTypeface(KenbieApplication.S_NORMAL);
//        etOldPwd.setText(mActivity.mPref.getString("Password", ""));

        TextView newPwdTxt = (TextView) view.findViewById(R.id.t_new_pwd);
        newPwdTxt.setTypeface(KenbieApplication.S_NORMAL);
        newPwdTxt.setText(mActivity.mPref.getString("180", "New Password"));

        etNewPwd = (EditText) view.findViewById(R.id.et_new_pwd);
        etNewPwd.setHint(mActivity.mPref.getString("181", "Enter new password"));
        etNewPwd.setTypeface(KenbieApplication.S_NORMAL);

        TextView tConfirmTxt = (TextView) view.findViewById(R.id.t_confirm_pwd);
        tConfirmTxt.setTypeface(KenbieApplication.S_NORMAL);
        tConfirmTxt.setText(mActivity.mPref.getString("183", "Confirm Password"));

        etConfirmPwd = (EditText) view.findViewById(R.id.et_confirm_pwd);
        etConfirmPwd.setHint(mActivity.mPref.getString("183", "Confirm Password"));
        etConfirmPwd.setTypeface(KenbieApplication.S_NORMAL);
    }

    private void updatePassword() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("new_password", mActivity.utility.md5(etNewPwd.getText().toString()));
            params.put("confirm_password", mActivity.utility.md5(etConfirmPwd.getText().toString()));
            params.put("old_password", mActivity.utility.md5(etOldPwd.getText().toString()));
            new MConnection().postRequestWithHttpHeaders(mActivity, "changePassword", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private boolean isInfoValid() {
        if (etOldPwd.getText().toString().equalsIgnoreCase("")) {
            mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("179", "Old Password field is required"));
            return false;
        } else if (etNewPwd.getText().toString().equalsIgnoreCase("")) {
            mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("182", "Please enter new password"));
            return false;
        } else if (etConfirmPwd.getText().toString().equalsIgnoreCase("")) {
            mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("183", "Confirm Password"));
            return false;
        } else if (!etNewPwd.getText().toString().equalsIgnoreCase(etConfirmPwd.getText().toString())) {
            mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("184", "The password does not match"));
            return false;
        }

        return true;
    }

    @Override
    public void getError(String error, int APICode) {
        if (error != null)
            mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), error);
        else
            mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));

        mActivity.showProgressDialog(false);
    }

    @Override
    public void getResponse(String response, int APICode) {
        try {
            if (response != null) {
                JSONObject jo = new JSONObject(response);

                if (APICode == 101) { // profile details
                    if (jo.has("status") && jo.getBoolean("status")) {
                        SharedPreferences.Editor editor = mActivity.mPref.edit();
                        if (mActivity.mPref.getString("Password", "").equalsIgnoreCase(""))
                            editor.putString("Password", etConfirmPwd.getText().toString());
                        editor.apply();
                        if (jo.has("success"))
                            showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("success"));
                        else
                            showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), "Details updated successfully!");
                    } else if (jo.has("error"))
                        mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), jo.getString("error"));
                    else
                        mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mActivity.showProgressDialog(false);
    }

    @Override
    public void networkError(String error, int APICode) {
        mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        mActivity.showProgressDialog(false);
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(16, mActivity.mPref.getString("177", "CHANGE PASSWORD"), false, true, true);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.m_back_button:
                mActivity.onBackPressed();
                break;
            case R.id.m_save_btn:
                if (isInfoValid())
                    updatePassword();
                break;
        }
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
                        mActivity.hideKeyboard(mActivity);
                    }
                })
                .setIcon(R.mipmap.ic_stat_notification)
                .show();
    }
}
