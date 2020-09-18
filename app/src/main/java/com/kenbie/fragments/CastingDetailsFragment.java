package com.kenbie.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.adapters.CastingDetailsAdapter;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.model.CastingUser;
import com.kenbie.model.Option;
import com.kenbie.model.OptionsData;
import com.kenbie.util.Constants;
import com.kenbie.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CastingDetailsFragment extends BaseFragment implements APIResponseHandler {
    private static Uri IMAGE_CAPTURE_URI;
    private RecyclerView cuOptions;
    private CastingUser castingDetails;
    private ArrayList<OptionsData> myDisplayData;
    private String imgPath;
    private TextView applyBtn;
    private int type;
    private RequestOptions options = null;

    public CastingDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            castingDetails = (CastingUser) getArguments().getSerializable("CastingDetails");
            type = getArguments().getInt("Type", 1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_casting_user_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView backBtn = view.findViewById(R.id.m_back_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });

        TextView castingTitle = view.findViewById(R.id.casting_title);
        castingTitle.setText(mActivity.mPref.getString("101", "CASTING"));
        castingTitle.setTypeface(KenbieApplication.S_NORMAL);

        TextView addCasting = (TextView) view.findViewById(R.id.add_casting_txt);
        addCasting.setText("+ " + mActivity.mPref.getString("102", "Casting"));
        addCasting.setTypeface(KenbieApplication.S_NORMAL);
        addCasting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(castingDetails.getApplied() == 0)
                mActivity.castingParams = null;
                mActivity.profilePicBitmap = null;
                mActivity.startAddingCasting(castingDetails, 1, false);
            }
        });

        TextView appliedCastingTxt = (TextView) view.findViewById(R.id.applied_casting_txt);
        appliedCastingTxt.setTypeface(KenbieApplication.S_NORMAL);
        appliedCastingTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.launchCasting(2);
            }
        });

        options = new RequestOptions()
                .optionalCenterCrop()
                .placeholder(mActivity.getResources().getDrawable(R.drawable.no_img))
                .priority(Priority.HIGH);


        applyBtn = (TextView) view.findViewById(R.id.apply_btn);
        applyBtn.setTypeface(KenbieApplication.S_NORMAL);
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 1 && castingDetails.getApplied() == 0)
                    applyCasting();
            }
        });

        if (mActivity.mPref.getString("UserId", "").equalsIgnoreCase(castingDetails.getUser_id() + ""))
            applyBtn.setVisibility(View.GONE);
        else if (type == 1) {
            applyBtn.setText(mActivity.mPref.getString("114", "APPLY"));
        } else if (type == 2) {
            applyBtn.setText(mActivity.mPref.getString("113", "APPLIED"));
            applyBtn.setBackgroundResource(R.drawable.btn_black_bg_style);
        } else if (type == 3)
            applyBtn.setText(mActivity.mPref.getString("322", "EDIT CASTING"));

        ImageView userImage = (ImageView) view.findViewById(R.id.c_user_img);
//        if (type == 3)
//            Glide.with(mActivity).load(Constants.BASE_MY_CASTING_IMAGE_URL + castingDetails.getCasting_img()).apply(options).into(userImage);
//        else

        Glide.with(mActivity).load(castingDetails.getCasting_img()).apply(options).into(userImage);

        TextView cuTitle = (TextView) view.findViewById(R.id.cu_title);
        cuTitle.setTypeface(KenbieApplication.S_BOLD);
        cuTitle.setText(castingDetails.getCasting_title());

        TextView cuLoc = (TextView) view.findViewById(R.id.cu_loc);
        cuLoc.setTypeface(KenbieApplication.S_NORMAL);
/*        String loc = "";
        if (castingDetails.getAddress1() != null && castingDetails.getAddress1().length() > 0 && !castingDetails.getAddress1().equalsIgnoreCase("null"))
            loc = loc + castingDetails.getAddress1();

        if (castingDetails.getCity() != null && castingDetails.getCity().length() > 0 && !castingDetails.getCity().equalsIgnoreCase("null"))
            loc = loc + ", " + castingDetails.getCity();

        if (castingDetails.getCountry() != null && castingDetails.getCountry().length() > 0 && !castingDetails.getCountry().equalsIgnoreCase("null"))
            loc = loc + ", " + castingDetails.getCountry();*/

//        cuLoc.setText(castingDetails.getCity());

        cuOptions = (RecyclerView) view.findViewById(R.id.cu_options);
//        cuOptions.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        cuOptions.setLayoutManager(linearLayoutManager);

        gettingCastingUserDetails();
//        gettingCastingUserDetailsList();
    }

    private void applyCasting() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("casting_id", castingDetails.getId() + "");
            new MConnection().postRequestWithHttpHeaders(mActivity, "applyToCasting", this, params, 102);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void gettingCastingUserDetails() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("casting_id", castingDetails.getId() + "");
            new MConnection().postRequestWithHttpHeaders(mActivity, "viewCastingDetail", this, params, 103);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void gettingCastingUserDetailsList() {
        if (mActivity.isOnline()) {
//            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            new MConnection().postRequestWithHttpHeaders(mActivity, "getCastingArrLists", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
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

                if (APICode == 101) { // casting user details list
                    if (jo.has("status") && jo.getBoolean("status")) {
                        myDisplayData = bindDisplaySteps(jo.getString("data"));
                        setDataOnUi();
                    } else if (jo.has("error"))
                        mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), jo.getString("error"));
                    else
                        mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                    mActivity.showProgressDialog(false);
                } else if (APICode == 102) {
                    if (jo.has("status") && jo.getBoolean("status") && jo.has("success")) {
//                        mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), jo.getString("success"));
                        castingDetails.setApplied(1);
                        applyBtn.setText(mActivity.mPref.getString("113", "APPLIED!"));
                        applyBtn.setBackgroundResource(R.drawable.btn_black_bg_style);
                    } else if (jo.has("error"))
                        mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), jo.getString("error"));
                    else
                        mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                    mActivity.showProgressDialog(false);
                } else if (APICode == 103) { // casting user details
                    if (jo.has("data")) {
                        JSONObject jo1 = new JSONObject(jo.getString("data"));
                        if (jo1.has("applied"))
                            castingDetails.setApplied(jo1.getInt("applied"));
                        if (castingDetails.getApplied() == 1) {
                            applyBtn.setText(mActivity.mPref.getString("113", "APPLIED!"));
                            applyBtn.setBackgroundResource(R.drawable.btn_black_bg_style);
                        } else
                            applyBtn.setText(mActivity.mPref.getString("114", "APPLY CASTING"));
                    }
                    if (jo.has("arrList")) {
                        myDisplayData = bindDisplaySteps(jo.getString("arrList"));
                        setDataOnUi();
                    }
                    mActivity.showProgressDialog(false);
//                    gettingCastingUserDetailsList();
                } else {
                    mActivity.showProgressDialog(false);
                    mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                }
            } else {
                mActivity.showProgressDialog(false);
                mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
            }

        } catch (Exception e) {
            mActivity.showProgressDialog(false);
            e.printStackTrace();
        }
    }

    private void setDataOnUi() {
        CastingDetailsAdapter pvAdapter = new CastingDetailsAdapter(getActivity(), myDisplayData, false);
        cuOptions.setAdapter(pvAdapter);

        if (mActivity.mPref.getString("UserId", "").equalsIgnoreCase(castingDetails.getUser_id() + ""))
            applyBtn.setVisibility(View.GONE);
        else if (Utility.isExpire(castingDetails.getCasting_end_date()))
            applyBtn.setVisibility(View.GONE);
        else
            applyBtn.setVisibility(View.VISIBLE);
    }


    @Override
    public void networkError(String error, int APICode) {
        mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        mActivity.showProgressDialog(false);
    }

    // Bind display steps
    private ArrayList<OptionsData> bindDisplaySteps(String data) {
        ArrayList<OptionsData> values = new ArrayList<>();
        String[] name = {mActivity.mPref.getString("104", "Requirements"), mActivity.mPref.getString("105", "Preferences"), mActivity.mPref.getString("112", "Categories")};
        try {
            for (int i = 0; i < 3; i++) {
                OptionsData op = new OptionsData();
                op.setId(i + 1);
                op.setName(name[i]);
                if (op.getId() == 1) { // Requirement
                    op.setOptionData(castingDetails.getCasting_requirement());
                    values.add(op);
                } else if (op.getId() == 2) { // Preferences
                    op.setOptionArrayList(bindPreferencesData(data));
                    values.add(op);
                } else if (op.getId() == 3) { // Categories
                    JSONObject jo = new JSONObject(data);
                    if (jo.has("categories"))
                        op.setOptionArrayList(getMyData(jo.getString("categories"), castingDetails.getCasting_categories()));
                    if (op.getOptionArrayList() != null && op.getOptionArrayList().size() > 0)
                        values.add(op);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    private ArrayList<Option> getMyData(String options, String myData) {
        ArrayList<Integer> values = new ArrayList<>();
        ArrayList<Option> optionsDataEthnicity = new ArrayList<>();
        try {
            if (myData != null && !myData.equalsIgnoreCase("null")) {
                String[] mData = myData.replace(",", "-").split("-");
                if (mData != null)
                    for (int i = 0; i < mData.length; i++)
                        try {
                            values.add(Integer.valueOf(mData[i]));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

            }

            JSONArray userEthnicity = new JSONArray(options);
            for (int i = 0; i < userEthnicity.length(); i++) {
                Option od = new Option();
                JSONObject jod = new JSONObject(userEthnicity.getString(i));
                od.setId(jod.getInt("id"));
                od.setTitle(jod.getString("name"));
                if (values.indexOf(od.getId()) != -1) {
//                    od.setActive(true);
                    optionsDataEthnicity.add(od);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return optionsDataEthnicity;
    }

    private ArrayList<Option> bindPreferencesData(String data) {
        ArrayList<Option> values = new ArrayList<>();
        String[] mTitles = {mActivity.mPref.getString("106", "Type"), mActivity.mPref.getString("69", "Gender"), mActivity.mPref.getString("107", "Casting Start"), mActivity.mPref.getString("108", "Casting Closed"), mActivity.mPref.getString("109", "Time"), mActivity.mPref.getString("110", "Age Range"), mActivity.mPref.getString("39", "Location"), mActivity.mPref.getString("111", "Fees")};
        try {
            for (int i = 0; i < mTitles.length; i++) {
                Option od = new Option();
                od.setId((i + 1));
                od.setTitle(mTitles[i]);
                od.setCode(bindData(od.getId(), data));
                if (od.getCode() != null && od.getCode().length() > 0)
                    values.add(od);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }

    private String bindData(int id, String data) {
        String value = "";
        try {
            JSONObject jo = new JSONObject(data);

            if (id == 1) {
                if (jo.has("casting_type")) {
                    String casting_type = castingDetails.getCasting_type();
                    String[] castingArr = casting_type.replace(",", "-").split("-");
                    if (castingArr != null && castingArr.length > 0) {
                        int castingType = Integer.valueOf(castingArr[0]);

                        JSONArray jsonArray = new JSONArray(jo.getString("casting_type"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject joo = new JSONObject(jsonArray.getString(i));
                            if (castingType == joo.getInt("id")) {
                                value = joo.getString("name");
                                break;
                            }
                        }
                    }
                }
            } else if (id == 2) {
                if (castingDetails.getCasting_gender() != null && !castingDetails.getCasting_gender().equalsIgnoreCase("0") && castingDetails.getCasting_gender().length() > 0)
                    value = getGenderFromData(castingDetails.getCasting_gender());
                else
                    value = "";
            } else if (id == 3)
                value = castingDetails.getCasting_start_date();
            else if (id == 4)
                value = castingDetails.getCasting_end_date();
            else if (id == 5) {
                if (castingDetails.getCasting_start_time() != null && !castingDetails.getCasting_start_time().equalsIgnoreCase("0"))
                    value = castingDetails.getCasting_start_time() + " " + mActivity.mPref.getString("323", "to") + " " + castingDetails.getCasting_end_time();
                else
                    value = "";
            } else if (id == 6) {
                if (castingDetails.getCasting_start_age() != null && !castingDetails.getCasting_start_age().equalsIgnoreCase("0"))
                    value = castingDetails.getCasting_start_age() + " " + mActivity.mPref.getString("323", "to") + " " + castingDetails.getCasting_end_age();
                else
                    value = "";
            } else if (id == 7) {
                if (castingDetails.getCasting_location() != null && castingDetails.getCasting_location().length() > 0)
                    value = castingDetails.getCasting_location();
                else
                    value = "";
            } else if (id == 8) {
                if (castingDetails.getCasting_fee() != null && castingDetails.getCasting_fee().length() > 0)
                    value = castingDetails.getCasting_fee();
                else
                    value = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public String getGenderFromData(String casting_gender) {
        try {
            int gender = Integer.valueOf(casting_gender);
            if (gender == 1)
                return mActivity.mPref.getString("31", "Male");
            else if (gender == 2)
                return mActivity.mPref.getString("32", "Female");
            else
                return mActivity.mPref.getString("72", "Both");

            //                    value.setCasting_gender(id == 1 ? mActivity.mPref.getString("31", "Male") : mActivity.mPref.getString("32", "Female"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mActivity.mPref.getString("31", "Male");
    }


    @Override
    public void onResume() {
        mActivity.updateActionBar(6, "CASTING DETAIL", false, true, true);
        super.onResume();
    }
}
