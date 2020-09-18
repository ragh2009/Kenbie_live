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
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.model.UserItem;
import com.kenbie.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MembershipFragment extends BaseFragment implements APIResponseHandler {
    private TextView getMemValue, memValue, mRevokeTitle;
    private UserItem userItem = null;

    public MembershipFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_membership, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView mTitle = view.findViewById(R.id.m_title);
        mTitle.setText(mActivity.mPref.getString("152", "Membership"));
        mTitle.setTypeface(KenbieApplication.S_NORMAL);

        memValue = (TextView) view.findViewById(R.id.m_value);
        memValue.setTypeface(KenbieApplication.S_SEMI_BOLD);

        getMemValue = (TextView) view.findViewById(R.id.get_mem_btn);
        getMemValue.setTypeface(KenbieApplication.S_BOLD);

        mRevokeTitle = (TextView) view.findViewById(R.id.m_revoke_title);
        mRevokeTitle.setTypeface(KenbieApplication.S_NORMAL);

        getMemValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActivity.mPref.getInt("MemberShip", 0) == 0)
                    // start membership payment process
                    mActivity.showMemberShipInfo(userItem, 1);
                else
                    showConfirmDialog(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("176", "Would you like to stop your auto renew subscription?"));
            }
        });

        refreshMemberShipInfo();
        getMemberShipProcess();
    }

    private void getMemberShipProcess() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("device_id", mActivity.deviceId);
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "getMembershipDetail", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void cancelMemberShipProcess() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("device_id", mActivity.deviceId);
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "cancelMembership", this, params, 102);
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
                JSONArray jData = new JSONArray(jMain.getString("data"));
                for (int i = 0; i < jData.length(); i++) {
                    JSONObject jo = new JSONObject(jData.getString(i));
                    userItem = new UserItem();
                    userItem.setId(jo.getInt("id"));
                    userItem.setFirstName(jo.getString("user_name"));
                    userItem.setUserPic(jo.getString("user_pic"));
                    userItem.setIsActive(jo.getInt("is_paid"));
                    if (jo.has("active_period"))
                        userItem.setActiveDate(jo.getString("active_period"));

                    SharedPreferences.Editor editor = mActivity.mPref.edit();
                    editor.putInt("MemberShip", userItem.getIsActive()); // 1- active, 0 - no
                    editor.putString("ActivePeriod", userItem.getActiveDate());
                    if (jo.has("stop_renew") && !jo.getString("stop_renew").equalsIgnoreCase("null")) {
                        userItem.setTotalImage(jo.getInt("stop_renew"));
                        editor.putInt("RenewStop", userItem.getTotalImage()); // 1- hide, 0 - show stop btn
                    }
                    editor.apply();
                }

                if (userItem == null || userItem.getId() == 0)
                    mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                else if (APICode == 101)  // start membership payment process
                    refreshMemberShipInfo();
                else if (APICode == 102) {
                    if (jMain.has("success"))
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jMain.getString("success"));
                    SharedPreferences.Editor editor = mActivity.mPref.edit();
                    userItem.setTotalImage(1);
                    editor.putInt("RenewStop", 1);
                    editor.apply();
                    refreshMemberShipInfo();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mActivity.showProgressDialog(false);
    }

    private void refreshMemberShipInfo() {
        if (mActivity.mPref.getInt("MemberShip", 0) == 1) {
            if (mActivity.mPref.getInt("RenewStop", 0) == 0) {
                mRevokeTitle.setText(mActivity.mPref.getString("176", getString(R.string.mem_renew_title)));
                getMemValue.setText(mActivity.mPref.getString("175", "Stop"));
                getMemValue.setVisibility(View.VISIBLE);
                mRevokeTitle.setVisibility(View.VISIBLE);
            } else {
                getMemValue.setVisibility(View.GONE);
                mRevokeTitle.setVisibility(View.GONE);
            }
            memValue.setText(mActivity.mPref.getString("ActivePeriod", ""));
        } else {
            mRevokeTitle.setText("");
            getMemValue.setText(mActivity.mPref.getString("173", "GET MEMBERSHIP"));
            memValue.setText(mActivity.mPref.getString("174", "Not A Member"));
        }
    }

    @Override
    public void networkError(String error, int APICode) {
        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        mActivity.showProgressDialog(false);
    }

    // Show dialog with message and title
    public void showConfirmDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle("")
                .setMessage(Html.fromHtml(message))
                .setPositiveButton(mActivity.mPref.getString("21", "Yes"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        cancelMemberShipProcess();
                    }
                }).setNegativeButton(mActivity.mPref.getString("22", "Cancel"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setIcon(R.mipmap.ic_stat_notification).show();
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(18, mActivity.mPref.getString("172", "MEMBERSHIP"), false, true, false);
        super.onResume();
    }
}
