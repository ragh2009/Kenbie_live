package com.kenbie;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.kenbie.adapters.MyMessageFragmentAdapter;
import com.kenbie.model.MsgUserItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class KenbieMessageActivity extends KenbieBaseActivity {
    public boolean isAPICall = true;
    public ArrayList<MsgUserItem> msgUserAllList, msgUserOnlineList, msgUserFavouritesList;
    private ImageView menuBtn = null, searchBtn, appLogoImage;
    private TextView mTitle;
    private int type = 0;
    private Toolbar mToolbar = null;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kenbie_message);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Please wait...");
        mProgress.setIndeterminate(false);
        mProgress.setCancelable(true);
        mProgress.setCanceledOnTouchOutside(false);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        menuBtn = (ImageView) findViewById(R.id.back_button);
        menuBtn.setBackgroundResource(R.drawable.ic_panel);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mTitle = (TextView) findViewById(R.id.m_title);
        mTitle.setTypeface(KenbieApplication.S_BOLD);

        appLogoImage = (ImageView) findViewById(R.id.app_logo);

        searchBtn = (ImageView) findViewById(R.id.action_search);
        searchBtn.setVisibility(View.VISIBLE);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setVisibility(View.VISIBLE);
        final ViewPager msgViewPager = (ViewPager) findViewById(R.id.msg_list_pager);
        msgViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Fragment fragment = ((MyMessageFragmentAdapter) msgViewPager.getAdapter()).getFragment(position);

                if (fragment != null)
                    fragment.onResume();

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        MyMessageFragmentAdapter mFragmentAdapter = new MyMessageFragmentAdapter(getSupportFragmentManager());
        msgViewPager.setAdapter(mFragmentAdapter);
        tabLayout.setupWithViewPager(msgViewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    // Parse message user list
    public void parseUserMsgData(String data) {
        try {
            msgUserAllList = new ArrayList<>();
            msgUserOnlineList = new ArrayList<>();
            msgUserFavouritesList = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = new JSONObject(jsonArray.getString(i));
                MsgUserItem value = new MsgUserItem();
                value.setUid(jo.getInt("uid"));
                value.setCurrent_status(jo.getInt("current_status"));
                value.setIsFav(jo.getInt("isFav"));
                value.setUser_name(jo.getString("first_name"));
                value.setUser_img(jo.getString("user_img"));
                value.setLast_response_time(jo.getString("last_response_time"));
                value.setNew_msg_count(jo.getInt("new_msg_count"));
                msgUserAllList.add(value);
                if (value.getCurrent_status() == 1)
                    msgUserOnlineList.add(value);
                if (value.getIsFav() == 1)
                    msgUserFavouritesList.add(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Action bar update details
    public void updateActionBar(int type, String title, boolean isSearchEnable) {
        try {
            this.type = type;
            if (type == 0) { // Home
                menuBtn.setBackgroundResource(R.drawable.ic_panel);
                mToolbar.setNavigationIcon(R.drawable.ic_panel);
                mTitle.setVisibility(View.INVISIBLE);
                appLogoImage.setVisibility(View.VISIBLE);
            } else {
                menuBtn.setBackgroundResource(R.drawable.back_arrow);
                mToolbar.setNavigationIcon(R.drawable.back_arrow);
                if (title == null) {
                    mTitle.setVisibility(View.INVISIBLE);
                    appLogoImage.setVisibility(View.VISIBLE);
                } else {
                    mTitle.setText(title);
                    mTitle.setVisibility(View.VISIBLE);
                    appLogoImage.setVisibility(View.INVISIBLE);
                }
            }

            if (isSearchEnable)
                searchBtn.setVisibility(View.VISIBLE);
            else
                searchBtn.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showProgressDialog(boolean isShow) {
        try {
            if (isShow) {
                mProgress.setMessage("Please wait...");
                mProgress.show();
            } else
                mProgress.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
