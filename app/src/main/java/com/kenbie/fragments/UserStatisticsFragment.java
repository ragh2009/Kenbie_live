package com.kenbie.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.app.adprogressbarlib.AdCircleProgress;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.model.Option;
import com.kenbie.model.ProfileInfo;
import com.kenbie.model.UserStatistics;
import com.kenbie.util.Constants;
import com.kenbie.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class UserStatisticsFragment extends BaseFragment implements APIResponseHandler, View.OnClickListener {
    private ProfileInfo profileInfo;
    private UserStatistics userStatistics;
    private TextView profileDuration, pMonths, pWeeks, profileDurationStats, profileVisitorValue, profileNotificationValue, profileCommentsValue, profileViewsValue, profileCompValue;
    private int durationType = 1;
    private LineChartView lineChartView;
    private ProgressBar profCompleteProgress;
    private AdCircleProgress profileSectionProgress, photoUploadProgress, privacySectionProgress;
    private TextView profileSectionAction, photoUploadAction, privacySectionAction, profileClick, photoClick, privacyClick;

    public UserStatisticsFragment() {
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
        return inflater.inflate(R.layout.fragment_user_statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView profileTitle = view.findViewById(R.id.profile_title);
        profileTitle.setText(mActivity.mPref.getString("252", "View of your profile"));
        profileTitle.setTypeface(KenbieApplication.S_SEMI_BOLD);
        profileDuration = (TextView) view.findViewById(R.id.duration_txt);
        profileDuration.setTypeface(KenbieApplication.S_NORMAL);
        pWeeks = (TextView) view.findViewById(R.id.p_weeks);
        pWeeks.setText(mActivity.mPref.getString("253", "Week"));
        pWeeks.setTypeface(KenbieApplication.S_NORMAL);
        pWeeks.setOnClickListener(this);
        pMonths = (TextView) view.findViewById(R.id.p_months);
        pMonths.setText(mActivity.mPref.getString("254", "Month"));
        pMonths.setTypeface(KenbieApplication.S_NORMAL);
        pMonths.setOnClickListener(this);

        lineChartView = view.findViewById(R.id.line_chart);

        TextView profileStatsTitle = view.findViewById(R.id.profile_stats_title);
        profileStatsTitle.setText(mActivity.mPref.getString("255", "Your Profile Stats"));
        profileStatsTitle.setTypeface(KenbieApplication.S_SEMI_BOLD);
        profileDurationStats = (TextView) view.findViewById(R.id.profile_stats_duration_txt);
        profileDurationStats.setTypeface(KenbieApplication.S_NORMAL);

        TextView profileVisitorTitle = view.findViewById(R.id.profile_visitor_title);
        profileVisitorTitle.setText(mActivity.mPref.getString("256", "Profile Visitor"));
        profileVisitorTitle.setTypeface(KenbieApplication.S_SEMI_BOLD);
        profileVisitorValue = (TextView) view.findViewById(R.id.profile_visitor_value);
        profileVisitorValue.setTypeface(KenbieApplication.S_SEMI_BOLD);

        TextView profileNotificationTitle = view.findViewById(R.id.profile_notification_title);
        profileNotificationTitle.setText(mActivity.mPref.getString("151", "Notifications"));
        profileNotificationTitle.setTypeface(KenbieApplication.S_SEMI_BOLD);
        profileNotificationValue = (TextView) view.findViewById(R.id.profile_notification_value);
        profileNotificationValue.setTypeface(KenbieApplication.S_SEMI_BOLD);

        TextView profileCommentsTitle = view.findViewById(R.id.profile_comments_title);
        profileCommentsTitle.setText(mActivity.mPref.getString("292", "Comments"));
        profileCommentsTitle.setTypeface(KenbieApplication.S_SEMI_BOLD);
        profileCommentsValue = (TextView) view.findViewById(R.id.profile_comments_value);
        profileCommentsValue.setTypeface(KenbieApplication.S_SEMI_BOLD);

        TextView profileViewsTitle = view.findViewById(R.id.profile_views_title);
        profileViewsTitle.setText(mActivity.mPref.getString("258", "Profile Views"));
        profileViewsTitle.setTypeface(KenbieApplication.S_SEMI_BOLD);
        profileViewsValue = (TextView) view.findViewById(R.id.profile_views_value);
        profileViewsValue.setTypeface(KenbieApplication.S_SEMI_BOLD);

        TextView profileCompTitle = view.findViewById(R.id.profile_comp_title);
        profileCompTitle.setText(mActivity.mPref.getString("259", "Your Profile Completeness"));
        profileCompTitle.setTypeface(KenbieApplication.S_SEMI_BOLD);
        profileCompValue = (TextView) view.findViewById(R.id.profile_comp_value);
        profileCompValue.setTypeface(KenbieApplication.S_SEMI_BOLD);

        profCompleteProgress = view.findViewById(R.id.profile_comp_progress);

        profileSectionProgress = view.findViewById(R.id.profile_section_progress);
        TextView profileSectionTitle = view.findViewById(R.id.profile_section_title);
        profileSectionTitle.setText(mActivity.mPref.getString("260", "Personal Details"));
        profileSectionTitle.setTypeface(KenbieApplication.S_NORMAL);
        profileSectionAction = (TextView) view.findViewById(R.id.profile_section_action);
        profileSectionAction.setText(mActivity.mPref.getString("262", "Please complete this"));
        profileSectionAction.setTypeface(KenbieApplication.S_NORMAL);
        profileClick = (TextView) view.findViewById(R.id.profile_section_click);
        profileClick.setText(mActivity.mPref.getString("265", "Click Here"));
        profileClick.setTypeface(KenbieApplication.S_NORMAL);

        photoUploadProgress = view.findViewById(R.id.photo_upload_progress);
        TextView photoUploadTitle = (TextView) view.findViewById(R.id.photo_upload_title);
        photoUploadTitle.setText(mActivity.mPref.getString("221", "Photo Gallery"));
        photoUploadTitle.setTypeface(KenbieApplication.S_NORMAL);
        photoUploadAction = (TextView) view.findViewById(R.id.photo_upload_action);
        photoUploadAction.setText(mActivity.mPref.getString("262", "Please complete this"));
        photoUploadAction.setTypeface(KenbieApplication.S_NORMAL);
        photoClick = (TextView) view.findViewById(R.id.photo_upload_click);
        photoClick.setText(mActivity.mPref.getString("265", "Click Here"));
        photoClick.setTypeface(KenbieApplication.S_NORMAL);

        privacySectionProgress = view.findViewById(R.id.privacy_section_progress);
        TextView privacySectionTitle = (TextView) view.findViewById(R.id.privacy_section_title);
        privacySectionTitle.setText(mActivity.mPref.getString("263", "Privacy Settings"));
        privacySectionTitle.setTypeface(KenbieApplication.S_NORMAL);
        privacySectionAction = (TextView) view.findViewById(R.id.privacy_section_action);
        privacySectionAction.setText(mActivity.mPref.getString("262", "Please complete this"));
        privacySectionAction.setTypeface(KenbieApplication.S_NORMAL);
        privacyClick = (TextView) view.findViewById(R.id.privacy_section_click);
        privacyClick.setText(mActivity.mPref.getString("265", "Click Here"));
        privacyClick.setTypeface(KenbieApplication.S_NORMAL);

//        pWeeks.setBackgroundResource(R.color.red_g_color);
//        pWeeks.setTextColor(getResources().getColor(R.color.white));
//        pMonths.setBackgroundResource(R.color.white);
//        pMonths.setTextColor(getResources().getColor(R.color.gray));

        getStatisticsDetails();
    }

    private void getStatisticsDetails() {
        if (mActivity.isOnline()) {
//            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
//            params.put("profile_user_id", profileInfo.getId() + "");
            params.put("rtype", durationType == 1 ? "7days" : "1month");
            new MConnection().postRequestWithHttpHeaders(mActivity, "getUserStats", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
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
                    if (jo.has("data")) {
                        getStatisticsDetails(jo.getString("data"));
                        profileDuration.setText(Utility.getMonthDateFormat(userStatistics.getReport().get(0).getTitle()) + " - " + Utility.getMonthDateFormat(userStatistics.getReport().get(userStatistics.getReport().size() - 1).getTitle()));

//                        updateGraph();
                        updateDataOnScreen();
                    } else
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        mActivity.showProgressDialog(false);
    }

    private void updateDataOnScreen() {
        try {
            if (userStatistics != null) {
                if (userStatistics.getReport() != null && userStatistics.getReport().size() > 0) {
                    profileDuration.setText(Utility.getMonthDateFormat(userStatistics.getReport().get(0).getTitle()) + " - " + Utility.getMonthDateFormat(userStatistics.getReport().get(userStatistics.getReport().size() - 1).getTitle()));
                    profileDurationStats.setText(Utility.getMonthDateFormat(userStatistics.getReport().get(0).getTitle()) + " - " + Utility.getMonthDateFormat(userStatistics.getReport().get(userStatistics.getReport().size() - 1).getTitle()));

                    updateGraphRecord();

                    profileVisitorValue.setText(userStatistics.getProfile_visitor() + "");
                    profileNotificationValue.setText(userStatistics.getNotifications() + "");
                    profileCommentsValue.setText(userStatistics.getMessage() + "");
                    profileViewsValue.setText(userStatistics.getProfile_view() + "");

                    profileCompValue.setText(userStatistics.getProfile_complete() + "%");
                    profCompleteProgress.setProgress(userStatistics.getProfile_complete());

                    profileSectionProgress.setProgress(userStatistics.getPersonal_detail());
                    photoUploadProgress.setProgress(userStatistics.getPhoto_upload());
                    privacySectionProgress.setProgress(userStatistics.getPrivacy_section());

                    if (userStatistics.getProfile_complete() == 100) {
                        profileSectionAction.setText("");
                        profileSectionAction.setVisibility(View.INVISIBLE);
                        profileClick.setText("");
                        profileClick.setVisibility(View.INVISIBLE);
                    } else
                        profileClick.setOnClickListener(this);

                    if (userStatistics.getPhoto_upload() == 100) {
                        photoUploadAction.setText("");
                        photoUploadAction.setVisibility(View.INVISIBLE);
                        photoClick.setText("");
                        photoClick.setVisibility(View.INVISIBLE);
                    } else
                        photoClick.setOnClickListener(this);

                    if (userStatistics.getPrivacy_section() == 100) {
                        privacySectionAction.setText("");
                        privacySectionAction.setVisibility(View.INVISIBLE);
                        privacyClick.setText("");
                        privacyClick.setVisibility(View.INVISIBLE);
                    } else
                        privacyClick.setOnClickListener(this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateGraphRecord() {
        try {
            List yAxisValues = new ArrayList();
            List axisValues = new ArrayList();

            Line line = new Line(yAxisValues).setColor(Color.parseColor("#b8312f"));
//    java.lang.ClassCastException: lecho.lib.hellocharts.model.PointValue cannot be cast to lecho.lib.hellocharts.model.AxisValue
            int dataSize = userStatistics.getReport().size();
            for (int i = 0; i < dataSize; i++) {
                if (durationType == 1) {
                    axisValues.add(i, new AxisValue(i).setLabel(Utility.getMonthFormat(userStatistics.getReport().get(i).getTitle())));
                    yAxisValues.add(new PointValue(i, userStatistics.getReport().get(i).getId()));
                } else {
                    if (i == 0 || ((i + 1) == dataSize)) {
                        axisValues.add(i, new AxisValue(i).setLabel(Utility.getMonthFormat(userStatistics.getReport().get(i).getTitle())));
                        yAxisValues.add(new PointValue(i, userStatistics.getReport().get(i).getId()));
                    } else {
                        axisValues.add(i, new AxisValue(i).setLabel(""));
                        yAxisValues.add(new PointValue(i, userStatistics.getReport().get(i).getId()));
                    }
                }
            }

            List lines = new ArrayList();
            lines.add(line);

            LineChartData data = new LineChartData();
            data.setLines(lines);

            Axis axis = new Axis();
            axis.setValues(axisValues);
            axis.setTextSize(10);
            axis.setTextColor(Color.parseColor("#808080"));
            data.setAxisXBottom(axis);

            Axis yAxis = new Axis();
//                    yAxis.setValues(yAxisValues);
            axis.setTextSize(10);
            axis.setTextColor(Color.parseColor("#808080"));
            data.setAxisYLeft(yAxis);

            lineChartView.setLineChartData(data);
            Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
            viewport.top = 10;
            lineChartView.setMaximumViewport(viewport);
            lineChartView.setCurrentViewport(viewport);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getStatisticsDetails(String response) {
        try {
            userStatistics = new UserStatistics();
            JSONObject jo = new JSONObject(response);
            if (jo.has("profile_stats")) {
                JSONObject profile_stats = jo.getJSONObject("profile_stats");
                userStatistics.setProfile_visitor(profile_stats.getInt("profile_visitor"));
                userStatistics.setNotifications(profile_stats.getInt("notifications"));
                userStatistics.setMessage(profile_stats.getInt("message"));
                userStatistics.setProfile_view(profile_stats.getInt("profile_view"));
            }

            if (jo.has("profile_complete_sections")) {
                JSONObject profile_complete_sections = jo.getJSONObject("profile_complete_sections");
                userStatistics.setPersonal_detail(profile_complete_sections.getInt("personal_detail"));
                userStatistics.setPhoto_upload(profile_complete_sections.getInt("photo_upload"));
                userStatistics.setPrivacy_section(profile_complete_sections.getInt("privacy_section"));
                userStatistics.setCredit(profile_complete_sections.getString("credit"));
            }

            userStatistics.setProfile_complete(jo.getInt("profile_complete"));
            if (jo.has("report")) {
                ArrayList<Option> reportData = new ArrayList<>();

                JSONArray report = jo.getJSONArray("report");
                for (int i = 0; i < report.length(); i++) {
                    JSONObject data = report.getJSONObject(i);
                    Option option = new Option();
                    option.setId(data.getInt("value"));
                    option.setTitle(data.getString("label"));
                    reportData.add(option);
                }

                userStatistics.setReport(reportData);
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


    @Override
    public void onResume() {
        mActivity.updateActionBar(9, mActivity.mPref.getString("251", "STATISTICS"), false, true, false);
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.p_weeks:
                if (durationType != 1) {
                    durationType = 1;
                    pWeeks.setBackgroundResource(R.drawable.red_left_rounded_corner);
                    pWeeks.setTextColor(getResources().getColor(R.color.white));
                    pMonths.setBackgroundResource(R.drawable.white_right_rounded_corner);
                    pMonths.setTextColor(getResources().getColor(R.color.gray));
                    getStatisticsDetails();
                }
                break;
            case R.id.p_months:
                if (durationType != 2) {
                    durationType = 2;
                    pMonths.setBackgroundResource(R.drawable.red_right_rounded_corner);
                    pMonths.setTextColor(getResources().getColor(R.color.white));
                    pWeeks.setBackgroundResource(R.drawable.white_left_rounded_corner);
                    pWeeks.setTextColor(getResources().getColor(R.color.gray));
                    getStatisticsDetails();
                }
                break;
            case R.id.profile_section_click:
                mActivity.seeEditOptions(profileInfo);
                break;
            case R.id.photo_upload_click:
                mActivity.viewPhotoGallery(profileInfo);
                break;
            case R.id.privacy_section_click:
                mActivity.replaceFragment(new PrivacyFragment(), true, false);
                break;
        }
    }
}
