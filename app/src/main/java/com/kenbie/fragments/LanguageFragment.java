package com.kenbie.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.kenbie.R;
import com.kenbie.adapters.LanguageListAdapter;
import com.kenbie.data.LanguageParser;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.listeners.MsgUserActionListeners;
import com.kenbie.model.MsgUserItem;
import com.kenbie.model.Option;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class LanguageFragment extends BaseFragment implements APIResponseHandler, MsgUserActionListeners {
    private ArrayList<Option> langList;
    private String selLangCode;
    private ListView mLangList;
    private LanguageListAdapter langAdapter;
    private int selIndex = -1;

    public LanguageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_language, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLangList = view.findViewById(R.id.m_lang_list);

        selLangCode = mActivity.mPref.getString("UserSavedLangCode", "en");
//        mActivity.mPref.getString("UserSavedLang", "English")

        langList = bindAvailableLanguageData();
        if (langList == null)
            getLanguage();
        else if (langList.size() == 0)
            getLanguage();

        if (selIndex == -1)
            selIndex = 0;
        else if (selIndex > langList.size())
            selIndex = 0;

        langAdapter = new LanguageListAdapter(mActivity, langList, this, selIndex);
        mLangList.setAdapter(langAdapter);
        mLangList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateFavStatus(1, position);
//                if (!langList.get(position).getCode().equalsIgnoreCase(selLangCode))
//                    saveLanguage(langList.get(position));
            }
        });
    }

    private void saveLanguage(Option value) {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("lang", value.getCode());
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "lang", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void getLanguage() {
        Map<String, String> params = new HashMap<String, String>();
//        params.put("ip", new Utility().getIpAddress(this));
        params.put("lang", Locale.getDefault().getLanguage());
//        new MConnection().postRequestWithHttpHeaders(this, "langLoad", this, params, 101);
        mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "lang", this, params, 102);
    }

    private int findSelPosition() {
        if (langList != null) {
            for (int i = 0; i < langList.size(); i++) {
                if (langList.get(i).getCode().equalsIgnoreCase(selLangCode))
                    return i;
            }
        }
        return -1;
    }

    private ArrayList<Option> bindAvailableLanguageData() {
        ArrayList<Option> values = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(mActivity.mPref.getString("AvailableLang", ""));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                Option option = new Option();
                option.setCode(jo.getString("code"));
                option.setTitle(jo.getString("name"));
                values.add(option);
                if (option.getCode().equalsIgnoreCase(selLangCode))
                    selIndex = i;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }

    private ArrayList<Option> bindLanguageData() {
        ArrayList<Option> values = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Option op = new Option();
//            op.setId(i + 1);
            if (i == 0) {
                op.setCode("en");
                op.setTitle("English");
            } else {
                op.setCode("de");
                op.setTitle("German");
            }
            values.add(op);
        }
        return values;
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

                if (APICode == 101 || APICode == 102) { // language details
                    if (jo.has("status") && jo.getBoolean("status")) {
                        new LanguageParser().saveLanguageData(mActivity.mPref, response);
                        selLangCode = mActivity.mPref.getString("UserSavedLangCode", "en");
                        mActivity.updateActionBar(17, mActivity.mPref.getString("266", "LANGUAGE"), false, true, false);
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
        mActivity.updateActionBar(17, mActivity.mPref.getString("266", "LANGUAGE"), false, true, false);
        super.onResume();
    }

    @Override
    public void updateFavStatus(int type, int pos) {
        if (selIndex != pos) {
            selIndex = pos;
            langAdapter.refreshData(selIndex);
            saveLanguage(langList.get(pos));
        }
    }

    @Override
    public void userConStart(MsgUserItem msgUserItem) {

    }
}
