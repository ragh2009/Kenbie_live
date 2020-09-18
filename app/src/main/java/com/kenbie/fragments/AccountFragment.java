package com.kenbie.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kenbie.KenbieApplication;
import com.kenbie.LoginActivity;
import com.kenbie.R;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.model.UserItem;
import com.kenbie.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AccountFragment extends BaseFragment implements APIResponseHandler {


    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView likeTitle = (TextView) view.findViewById(R.id.like_title);
        likeTitle.setTypeface(KenbieApplication.S_NORMAL);
        likeTitle.setText("Like you deleted it, but you can come back when you like");
        likeTitle.setVisibility(View.GONE);

        TextView emailTitle = (TextView) view.findViewById(R.id.email_title);
        emailTitle.setTypeface(KenbieApplication.S_NORMAL);
        emailTitle.setText(mActivity.mPref.getString("Email", ""));

        TextView infoMsgTitle = (TextView) view.findViewById(R.id.info_msg_title);
        infoMsgTitle.setTypeface(KenbieApplication.S_NORMAL);
        infoMsgTitle.setText(mActivity.mPref.getString("192", "Please remember your password; the next time you log in you will be asked again"));

        TextView signOutBtn = (TextView) view.findViewById(R.id.sign_out_btn);
        signOutBtn.setTypeface(KenbieApplication.S_NORMAL);
        signOutBtn.setText(mActivity.mPref.getString("193", "SIGN OUT"));
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("268", "Are you sure, you want to logout?"), 1);
            }
        });

        TextView forgotTitle = (TextView) view.findViewById(R.id.forgot_title);
        forgotTitle.setTypeface(KenbieApplication.S_SEMI_BOLD);
        forgotTitle.setText(mActivity.mPref.getString("194", "Forgot Password?"));
        forgotTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotProcess();
//                showMessageWithTitle(mActivity, "Are you Sure?", "Do you want reset your password?", 3);
            }
        });

        if (mActivity.mPref.getString("Email", null) != null)
            forgotTitle.setVisibility(View.VISIBLE);
        else
            forgotTitle.setVisibility(View.GONE);

        TextView deleteBtn = (TextView) view.findViewById(R.id.delete_btn);
        deleteBtn.setTypeface(KenbieApplication.S_SEMI_BOLD);
        deleteBtn.setText(mActivity.mPref.getString("195", "Delete Account"));
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("196", "Are you sure you want to delete your account?"), 2);
            }
        });
    }

    // Show dialog with message and title
    public void showMessageWithTitle(Context context, String title, String message, final int type) {
        new AlertDialog.Builder(context)
                .setTitle("")
                .setMessage(Html.fromHtml(message))
                .setPositiveButton(mActivity.mPref.getString("21", "Yes"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (type == 1)
                            signOutProcess();
                        else if (type == 2)
                            deleteAccountProcess();
                        else if (type == 3)
                            forgotProcess();
                        dialog.dismiss();
                    }
                }).setNegativeButton(mActivity.mPref.getString("22", "Cancel"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }

    private void forgotProcess() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("email_id", mActivity.mPref.getString("Email", ""));
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "forgetPassword", this, params, 103);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void deleteAccountProcess() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "memberDelete", this, params, 102);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void signOutProcess() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "signOut", this, params, 101);
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
            if (APICode == 101 || APICode  == 102)
                mActivity.logoutProcess();
/*            else if (APICode == 102) {
                SharedPreferences.Editor editor = mActivity.mPref.edit();
                editor.putBoolean("isLogin", false);
                editor.putBoolean("GuestLogin", false);
                editor.apply();
                Intent intent = new Intent(mActivity, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                mActivity.finish();*/
            else if (APICode == 103) {
                JSONObject jo = new JSONObject(response);
                if (jo.has("success"))
                    mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("success"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mActivity.showProgressDialog(false);
    }

    // Parse user list
    private ArrayList<UserItem> getParseUserList(String data) {
        ArrayList<UserItem> values = new ArrayList<>();
        try {
            JSONArray jData = new JSONArray(data);
            for (int i = 0; i < jData.length(); i++) {
                JSONObject jo = new JSONObject(jData.getString(i));
                UserItem value = new UserItem();
                value.setId(jo.getInt("id"));
                value.setFirstName(jo.getString("first_name"));
                if (jo.getString("birth_year") != null && !jo.getString("birth_year").equalsIgnoreCase("null"))
                    value.setBirthYear(jo.getInt("birth_year"));
//                value.setBirthMonth(jo.getInt("birth_month"));
//                value.setBirthDay(jo.getInt("birth_day"));
                value.setTotalImage(jo.getInt("total_image"));
                value.setCity(jo.getString("city"));
                value.setCountry(jo.getString("country"));
                value.setIsActive(jo.getInt("is_active"));
                value.setUserPic(jo.getString("user_pic"));
                values.add(value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    @Override
    public void networkError(String error, int APICode) {
        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        mActivity.showProgressDialog(false);
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(20, mActivity.mPref.getString("191", "ACCOUNT"), false, true, false);
        super.onResume();
    }
}
