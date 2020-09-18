package com.kenbie.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kenbie.KenbieApplication;
import com.kenbie.MessageConvActivity;
import com.kenbie.R;
import com.kenbie.adapters.ModelsUserAdapter;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.listeners.CastingUserListeners;
import com.kenbie.model.CastingUser;
import com.kenbie.model.MsgUserItem;
import com.kenbie.util.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class InterestedModelsFragment extends BaseFragment implements APIResponseHandler, CastingUserListeners {
    private ArrayList<MsgUserItem> modelsUser;
    private RecyclerView castingRV;
    private int castingId = 0, mType;
    private TextView noDataTxt;
    private ModelsUserAdapter userAdapter;
    private CastingUser castingDetails;

    public InterestedModelsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt("Type", 1);
            if (mType != 3) {
                castingDetails = (CastingUser) getArguments().getSerializable("CastingDetails");
                if (castingDetails != null)
                    castingId = castingDetails.getId();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_interested_models, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        castingRV = (RecyclerView) view.findViewById(R.id.models_rv);
        castingRV.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        castingRV.setLayoutManager(layoutManager);

        noDataTxt = (TextView) view.findViewById(R.id.models_no_txt);
        noDataTxt.setText(mActivity.mPref.getString("162", "Data is not found."));
        noDataTxt.setTypeface(KenbieApplication.S_NORMAL);

        gettingModelsUserDetails();
    }

    private void gettingModelsUserDetails() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("casting_id", castingId + "");
            new MConnection().postRequestWithHttpHeaders(mActivity, "viewInterestedCastingModels", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    // data bind
    private void refreshData() {
        if (modelsUser == null || (modelsUser != null && modelsUser.size() == 0)) {
            noDataTxt.setVisibility(View.VISIBLE);
            castingRV.setVisibility(View.GONE);
        } else {
            noDataTxt.setVisibility(View.GONE);
            castingRV.setVisibility(View.VISIBLE);
            userAdapter = new ModelsUserAdapter(mActivity, modelsUser, this, 1, mActivity.mPref.getString("311", "SEND MESSAGE"));
            castingRV.setAdapter(userAdapter);
        }
    }

    @Override
    public void getError(String error, int APICode) {
        if (APICode == 101)
            refreshData();
        else if (error != null)
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
                        modelsUser = getCastUserDetails(jo.getString("data"));
                        if (modelsUser == null)
                            modelsUser = new ArrayList<>();
                        refreshData();
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

    // Parse casting details
    private ArrayList<MsgUserItem> getCastUserDetails(String data) {
        ArrayList<MsgUserItem> values = new ArrayList<>();
        try {
            JSONArray jData = new JSONArray(data);
            for (int i = 0; i < jData.length(); i++) {
                JSONObject jo = new JSONObject(jData.getString(i));
                MsgUserItem value = new MsgUserItem();
                value.setUser_name(jo.getString("first_name"));
                value.setUser_img(jo.getString("user_pic"));
//                value.setBirth_day(jo.getString("birth_day"));
//                value.setBirth_month(jo.getString("birth_month"));
//                value.setBirth_year(jo.getString("birth_year"));
                if (jo.has("applied_user_id"))
                    value.setUid(jo.getInt("applied_user_id"));
                else
                    value.setUid(jo.getInt("id"));
                String address = "";


                if (jo.getString("city") != null && jo.getString("city").length() > 1)
                    address = jo.getString("city");

                if (jo.getString("country") != null && jo.getString("country").length() > 1)
                    address = address + ", " + jo.getString("country");

                value.setLast_response_time(address);

                values.add(value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    @Override
    public void networkError(String error, int APICode) {
        mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        mActivity.showProgressDialog(false);
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(31, mActivity.mPref.getString("161", "INTERESTED MODELS"), false, true, false);
        super.onResume();
    }

    @Override
    public void getUserDetails(int pos, int type) {
        Intent intent = new Intent(mActivity, MessageConvActivity.class);
        intent.putExtra("MsgItem", modelsUser.get(pos));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
