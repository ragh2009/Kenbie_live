package com.kenbie.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.adapters.UserInfoAdapter;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.listeners.InfoListener;
import com.kenbie.model.Option;
import com.kenbie.model.OptionsData;
import com.kenbie.model.ProfileInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditUserInfoFragment extends BaseFragment implements APIResponseHandler, InfoListener, View.OnClickListener {
    private ProfileInfo profileInfo;
    private ArrayList<OptionsData> mData;
    private UserInfoAdapter mAdapter;
    private ListView mList = null;

    public EditUserInfoFragment() {
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
        return inflater.inflate(R.layout.fragment_info_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView backBtn = view.findViewById(R.id.m_back_button);
        backBtn.setOnClickListener(this);
        TextView screenTitle = view.findViewById(R.id.m_title);
        screenTitle.setTypeface(KenbieApplication.S_NORMAL);
        screenTitle.setText(mActivity.mPref.getString("232", "INFORMATION"));
        TextView saveBtn = view.findViewById(R.id.m_save_btn);
        saveBtn.setText(mActivity.mPref.getString("225", "Save"));
        saveBtn.setTypeface(KenbieApplication.S_NORMAL);
        saveBtn.setOnClickListener(this);

        mList = (ListView) view.findViewById(R.id.info_list);

        mData = bindContent();
        updateDataOnUI();
    }

    private void updateDataOnUI() {
        mActivity.showProgressDialog(false);
        mAdapter = new UserInfoAdapter(mActivity, mData, this);
        mList.setAdapter(mAdapter);
    }

    private ArrayList<OptionsData> bindContent() {
        mActivity.showProgressDialog(true);
        ArrayList<OptionsData> values = new ArrayList<>();
        String[] sData = {mActivity.mPref.getString("92", "Ethnicity"), mActivity.mPref.getString("226", "Who I am"), mActivity.mPref.getString("83", "Height"), mActivity.mPref.getString("85", "Weight"), mActivity.mPref.getString("86", "Bust"), mActivity.mPref.getString("87", "Waist"), mActivity.mPref.getString("88", "Hips"), mActivity.mPref.getString("89", "Dress"), mActivity.mPref.getString("90", "Eye Colour"), mActivity.mPref.getString("215", "Shoes"), mActivity.mPref.getString("91", "Hair Colour")};
//        String[] sData = {mActivity.mPref.getString("92", "Ethnicity"), mActivity.mPref.getString("226", "Who I am"), mActivity.mPref.getString("83", "Height"), mActivity.mPref.getString("85", "Weight"), mActivity.mPref.getString("86", "Bust"), mActivity.mPref.getString("87", "Waist"), mActivity.mPref.getString("88", "Hips"), mActivity.mPref.getString("89", "Dress"), mActivity.mPref.getString("90", "Eye Colour"), mActivity.mPref.getString("215", "Shoes"), mActivity.mPref.getString("170", "Language"), mActivity.mPref.getString("91", "Hair Colour")};
//        String[] sData = {"Ethnicity", "Who i am", "Height", "Weight", "Bust", "Waist", "Hips", "Dress", "Eye Colour", "Shoes", "Language", "Hair Colour"};
//        String[] sCode = {"user_ethinicity", "user_face", "user_height", "user_weight", "user_bust", "user_waist", "user_hips", "user_dress", "user_eye_color", "user_shoes", "user_language", "user_hair_color"};
        String[] sCode = {"user_ethinicity", "user_face", "user_height", "user_weight", "user_bust", "user_waist", "user_hips", "user_dress", "user_eye_color", "user_shoes", "user_hair_color"};
        for (int i = 0; i < sData.length; i++) {
            OptionsData od = new OptionsData();
            od.setId(i + 1);
            od.setName(sData[i]);
            getParseData(od);
            od.setOptionCode(sCode[i]);
            values.add(od);
        }
        return values;
    }

    // parse options data
    private void getParseData(OptionsData od) {
        ArrayList<Option> values = new ArrayList<>();

        try {
            JSONObject listOptions = new JSONObject(profileInfo.getList_options());

            JSONArray jsonArray = null;
            if (od.getId() == 1) {
                od.setOptionData(profileInfo.getUser_ethinicity());
                jsonArray = new JSONArray(listOptions.getString("user_ethinicity"));
            } else if (od.getId() == 2) {
                od.setOptionData(profileInfo.getUser_face());
                jsonArray = new JSONArray(listOptions.getString("user_face"));
            } else if (od.getId() == 3) {
                od.setOptionData(profileInfo.getUser_height());
                jsonArray = new JSONArray(listOptions.getString("height"));
            } else if (od.getId() == 4) {
                od.setOptionData(profileInfo.getUser_weight());
                jsonArray = new JSONArray(listOptions.getString("weight"));
            } else if (od.getId() == 5) {
                od.setOptionData(profileInfo.getUser_bust());
                jsonArray = new JSONArray(listOptions.getString("bust"));
            } else if (od.getId() == 6) {
                od.setOptionData(profileInfo.getUser_waist());
                jsonArray = new JSONArray(listOptions.getString("waist"));
            } else if (od.getId() == 7) {
                od.setOptionData(profileInfo.getUser_hips());
                jsonArray = new JSONArray(listOptions.getString("hips"));
            } else if (od.getId() == 8) {
                od.setOptionData(profileInfo.getUser_dress());
                jsonArray = new JSONArray(listOptions.getString("user_dress"));
            } else if (od.getId() == 9) {
                od.setOptionData(profileInfo.getUser_eye_color());
                jsonArray = new JSONArray(listOptions.getString("user_eyes"));
            } else if (od.getId() == 10) {
                od.setOptionData(profileInfo.getUser_shoes());
                jsonArray = new JSONArray(listOptions.getString("user_shoes"));
            }/* else if (od.getId() == 11) {
                od.setOptionData(profileInfo.getUserSelLanguage());
                jsonArray = new JSONArray(listOptions.getString("lang_list"));
            }*/ else if (od.getId() == 11) {
                od.setOptionData(profileInfo.getUser_hair_color());
                jsonArray = new JSONArray(listOptions.getString("user_hair_color"));
            }

     /*       Option option = new Option();
            option.setId(0);
            option.setTitle(mActivity.mPref.getString("94", "Select"));
            values.add(option);*/

            if (jsonArray != null) {
//                String[] data = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jo = new JSONObject(jsonArray.getString(i));
                    Option op = new Option();
                    op.setId(jo.getInt("id"));
                    op.setTitle(jo.getString("name"));
//                    data[i] = op.getTitle();
                    if (od.getId() == 11 && jo.has("shortname")) {
                        op.setCode(jo.getString("shortname"));
                        if (od.getOptionData() != null && op.getCode().equalsIgnoreCase(od.getOptionData()))
                            od.setImgId((i));
                    } else if (od.getOptionData() != null && op.getTitle().equalsIgnoreCase(od.getOptionData()))
                        od.setImgId(i);

                    values.add(op);
                }

//                od.setData(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        od.setOptionArrayList(values);
    }

    // Update user information on server
    private void updateInfoToServer() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));

            try {
                for (int i = 0; i < mData.size(); i++)
                    if (mData.get(i).getOptionCode() != null && mData.get(i).getImgId() > 0)
                        params.put(mData.get(i).getOptionCode(), mData.get(i).getOptionArrayList().get(mData.get(i).getImgId()).getId() + "");
//                        if (mData.get(i).getId() == 11)
//                            params.put(mData.get(i).getOptionCode(), mData.get(i).getOptionArrayList().get(mData.get(i).getImgId()).getCode() + "");
//                        else
            } catch (Exception e) {
                e.printStackTrace();
            }

            new MConnection().postRequestWithHttpHeaders(mActivity, "updateUserInformation", this, params, 101);
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
                    if (jo.has("status") && jo.getBoolean("status")) {
//                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("success"));
                        if (jo.has("success"))
                            mActivity.onBackPressed();
                        else
                            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), "Details updated successfully!");
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

    @Override
    public void networkError(String error, int APICode) {
        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        mActivity.showProgressDialog(false);
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(13, "INFORMATION", false, true, true);
        super.onResume();
    }


    @Override
    public void getInfoValue(int parentPos, int childPos) {
        try {
            mData.get(parentPos).setImgId(childPos);
            mAdapter.refreshData(mData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.m_save_btn)
            updateInfoToServer();
        else if (v.getId() == R.id.m_back_button)
            mActivity.onBackPressed();
    }
}
