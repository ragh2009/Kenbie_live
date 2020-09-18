package com.kenbie.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.adapters.ReportAbuseSpinnerAdapter;
import com.kenbie.adapters.SearchSingleChoiceAdapter;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.model.Option;
import com.kenbie.model.ProfileInfo;
import com.kenbie.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReportAbuseToUserFragment extends BaseFragment implements APIResponseHandler, View.OnClickListener {
    //    private String[] reasonsData = {"Select reason", "Fake User Photo", "Wrong Details", "Others"};
    private ProfileInfo profileInfo;
    private ArrayList<Option> reasonsData;
    private EditText etComments;
    private AppCompatSpinner reasonSpinner;

    public ReportAbuseToUserFragment() {
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
        return inflater.inflate(R.layout.fragment_report_abuse_user, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView backBtn = view.findViewById(R.id.m_back_button);
        backBtn.setOnClickListener(this);

        TextView screenTitle = view.findViewById(R.id.m_title);
        screenTitle.setTypeface(KenbieApplication.S_NORMAL);
        screenTitle.setText(mActivity.mPref.getString("290", "REPORT ABUSE"));

        TextView saveBtn = view.findViewById(R.id.m_save_btn);
        saveBtn.setText(mActivity.mPref.getString("97", "Send"));
        saveBtn.setTypeface(KenbieApplication.S_NORMAL);
        saveBtn.setOnClickListener(this);

        TextView reasonTitle = view.findViewById(R.id.ra_reason_title);
        reasonTitle.setText(mActivity.mPref.getString("291", "Reason"));
        reasonTitle.setTypeface(KenbieApplication.S_NORMAL);

        TextView raCommentTitle = view.findViewById(R.id.ra_comment_title);
        raCommentTitle.setText(mActivity.mPref.getString("292", "Comments"));
        raCommentTitle.setTypeface(KenbieApplication.S_NORMAL);

        reasonSpinner = view.findViewById(R.id.ra_reason_select);
        reasonSpinner.setPrompt(mActivity.mPref.getString("291", "Reason"));
        reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((ReportAbuseSpinnerAdapter) reasonSpinner.getAdapter()).updateSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etComments = (EditText) view.findViewById(R.id.et_ra_comment);
        etComments.setHint(mActivity.mPref.getString("293", "Your Comments"));
        etComments.setTypeface(KenbieApplication.S_NORMAL);

        getReportAbuseParams();
    }

    private void getReportAbuseParams() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));

            new MConnection().postRequestWithHttpHeaders(mActivity, "getReportAbuseParams", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void actionReportAbuse() {
        if (mActivity.isOnline()) {
            mActivity.hideKeyboard(mActivity, etComments);
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("reason", reasonsData.get(reasonSpinner.getSelectedItemPosition()).getId() + "");
            params.put("comment", etComments.getText().toString());
            params.put("block_user_id", profileInfo.getId() + "");
            new MConnection().postRequestWithHttpHeaders(mActivity, "updateReportAbuse", this, params, 102);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private boolean isInfoValid() {
        if (reasonSpinner.getSelectedItemPosition() == 0) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("294", "Please select reason"));
            return false;
        } else if (etComments.getText().toString().equalsIgnoreCase("")) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("295", "Please add your comments."));
            return false;
        }

        return true;
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
                if (APICode == 101) { // reasons
                    if (jo.has("status") && jo.getBoolean("status")) {
                        bindReasonsData(jo.getString("data"));
                    } else if (jo.has("error"))
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("error"));
                    else
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                } else if (APICode == 102) { // Report Abuse
                    if (jo.has("status") && jo.getBoolean("status")) {
                        if (jo.has("success"))
                            showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("success"));
                        else
                            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                    } else if (jo.has("error"))
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("error"));
                    else
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mActivity.showProgressDialog(false);
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
                    }
                })
                .setIcon(R.mipmap.ic_stat_notification)
                .show();
    }

    private void bindReasonsData(String data) {
        try {
            JSONObject jo = new JSONObject(data);
            if (jo.has("reason")) {
                reasonsData = new ArrayList<>();
                Option option = new Option();
                option.setId(-1);
                option.setTitle(mActivity.mPref.getString("94", "Select"));
                reasonsData.add(option);

                JSONArray jsonArray = jo.getJSONArray("reason");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jo1 = jsonArray.getJSONObject(i);
                    Option option1 = new Option();
                    option1.setId(jo1.getInt("id"));
                    option1.setTitle(jo1.getString("label"));
                    reasonsData.add(option1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ReportAbuseSpinnerAdapter reportAbuseSpinnerAdapter = new ReportAbuseSpinnerAdapter(mActivity, reasonsData, 0);
        reasonSpinner.setAdapter(reportAbuseSpinnerAdapter);
    }

    @Override
    public void networkError(String error, int APICode) {
        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        mActivity.showProgressDialog(false);
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(34, mActivity.mPref.getString("290", "REPORT ABUSE"), false, true, true);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.m_save_btn) {
            if (isInfoValid())
                actionReportAbuse();
        } else if (v.getId() == R.id.m_back_button)
            mActivity.onBackPressed();
    }
}
