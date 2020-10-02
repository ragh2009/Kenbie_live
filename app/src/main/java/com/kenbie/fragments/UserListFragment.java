package com.kenbie.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kenbie.KenbieApplication;
import com.kenbie.KenbieNavigationActivity;
import com.kenbie.R;
import com.kenbie.adapters.CelebritiesAdapter;
import com.kenbie.adapters.ModelsDataAdapter;
import com.kenbie.adapters.UserDataAdapter;
import com.kenbie.databinding.FragmentUserListBinding;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.listeners.InfoListener;
import com.kenbie.listeners.UserActionListener;
import com.kenbie.model.UserItem;
import com.kenbie.model.UserTypeData;
import com.kenbie.util.Constants;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserListFragment extends BaseFragment implements APIResponseHandler, InfoListener, UserActionListener, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView celebritiesRV, parentLayout;
    private int uId = 0;
    private ModelsDataAdapter userChildDataAdapter = null;
    private TextView celeDivTxt;
    private boolean isLastPage, isLoading;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager = null;
    private UserDataAdapter userAdapter = null;
    private ProgressBar mProgressBar;
    private CelebritiesAdapter celebritiesAdapter = null;
    private String sponsors_ids = "";

    public UserListFragment() {
        // Required empty public constructor
    }

    public static UserListFragment newInstance() {
        return new UserListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentUserListBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_list, container, false);
        mTopBarBinding = binding.topHeaderBar;
        EventBus.getDefault().register(this);
        return binding.getRoot();
    /*
        super.onCreateView(inflater, container, savedInstanceState);

        if (fragmentView != null) {
            return fragmentView;
        }
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        fragmentView = view;
        return view;*/
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);
        setupTitleBar();

//        noText = view.findViewById(R.id.no_text);
//        noText.setText(kActivity.mPref.getString("56", "You've reached the end of the list"));
//        noText.setTypeface(KenbieApplication.S_SEMI_BOLD);

        celeDivTxt = view.findViewById(R.id.cele_div);
        celeDivTxt.setVisibility(View.GONE);

        mProgressBar = view.findViewById(R.id.progressBar);
        mySwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        mySwipeRefreshLayout.setColorSchemeResources(
                R.color.red_g_color);
        mySwipeRefreshLayout.setOnRefreshListener(this);

        TextView userTypeAction = (TextView) view.findViewById(R.id.user_type_action);
        userTypeAction.setTypeface(KenbieApplication.S_NORMAL);
        userTypeAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kActivity.applySort(2);
            }
        });

        if (kActivity.position == 0) userTypeAction.setText("ALL");
        else if (kActivity.position == 1) userTypeAction.setText("MODEL");
        else if (kActivity.position == 2) userTypeAction.setText("AGENCY");
        else if (kActivity.position == 3) userTypeAction.setText("PHOTOGRAPHER");

        TextView sortAction = (TextView) view.findViewById(R.id.sort_action);
        sortAction.setTypeface(KenbieApplication.S_NORMAL);
        sortAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kActivity.applySort(1);
            }
        });

        TextView refineAction = (TextView) view.findViewById(R.id.refine_action);
        refineAction.setTypeface(KenbieApplication.S_NORMAL);
        refineAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        celebritiesRV = (RecyclerView) view.findViewById(R.id.rv_celebrities);
        parentLayout = (RecyclerView) view.findViewById(R.id.parent_layout);
        parentLayout.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
//                if(linearLayoutManager.getInitialPrefetchItemCount() > 0)
//                    kActivity.lastVisiblePosition = linearLayoutManager.getInitialPrefetchItemCount();
//                Log.d("LastComplete::::", linearLayoutManager.getInitialPrefetchItemCount()+"");
//                if(linearLayoutManager.findFirstVisibleItemPosition() > 0) {
//                    kActivity.lastVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
//                }

                if (linearLayoutManager.findLastCompletelyVisibleItemPosition() > 0)
                    kActivity.lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - 8)
                            && firstVisibleItemPosition >= 0) {
                        kActivity.currentPage++;
                        getLandingUserList();
                    }
                } /*else if (isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) == totalItemCount)
                        noText.setVisibility(View.VISIBLE);
                    else
                        noText.setVisibility(View.GONE);
                }*/
            }
        });

        if (kActivity.celebritiesList == null)
            gettingCelebritiesList();
        else if (kActivity.celebritiesList.size() == 0)
            gettingCelebritiesList();
        else
            refreshDataOnUi();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    private void refreshDataOnUi() {
        try {
            bindCelebritiesData();
            celeDivTxt.setVisibility(View.VISIBLE);
            if (kActivity.lastVisiblePosition < kActivity.userData.size()) {
                linearLayoutManager = new LinearLayoutManager(kActivity, RecyclerView.VERTICAL, false);
//                linearLayoutManager.setSmoothScrollbarEnabled(true);
                parentLayout.setLayoutManager(linearLayoutManager);
                parentLayout.setHasFixedSize(true);
                userAdapter = new UserDataAdapter(kActivity, kActivity.userData, this, isLastPage);
                parentLayout.setAdapter(userAdapter);
                if (kActivity.lastVisiblePosition > 0) {
                    parentLayout.scrollToPosition(kActivity.lastVisiblePosition);
                    //    linearLayoutManager.setInitialPrefetchItemCount(2);
                }

//                linearLayoutManager.setInitialPrefetchItemCount(kActivity.lastVisiblePosition);
//                parentLayout.smoothScrollToPosition(kActivity.lastVisiblePosition);
            } else
                gettingCelebritiesList();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*
    @Override
    public void onDestroy() {
        if (fragmentView.getParent() != null) {
            ((ViewGroup)fragmentView.getParent()).removeView(fragmentView);
        }
        super.onDestroy();
    }
*/

    private void gettingCelebritiesList() {
        if (kActivity.isOnline()) {
            kActivity.currentPage = 1;
            isLastPage = false;
            mySwipeRefreshLayout.setRefreshing(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", kActivity.mPref.getString("UserId", ""));
            params.put("login_key", kActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", kActivity.mPref.getString("LoginToken", ""));
            kActivity.mConnection.postRequestWithHttpHeaders(kActivity, "getCelebs", this, params, 102);
        } else {
            mySwipeRefreshLayout.setRefreshing(false);
            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void getLandingUserList() {
        if (kActivity.isOnline()) {
            isLoading = true;
            if (kActivity.currentPage == 1) {
                sponsors_ids = "";
                mySwipeRefreshLayout.setRefreshing(true);
            } else
                mProgressBar.setVisibility(View.VISIBLE);

            Map<String, String> params = new HashMap<String, String>();
            if (kActivity.mPref.getBoolean("isLogin", false)) {
                params.put("user_id", kActivity.mPref.getString("UserId", ""));
                params.put("login_key", kActivity.mPref.getString("LoginKey", ""));
                params.put("login_token", kActivity.mPref.getString("LoginToken", ""));
                params.put("latitude", kActivity.latitude + "");
                params.put("longitude", kActivity.longitude + "");
            } else
                params.put("ip", kActivity.ip + "");

            if (kActivity.sortBy != 3)
                params.put("sort", kActivity.sortBy + "");
            else
                params.put("sort", "0");

            params.put("sponsors_ids", sponsors_ids);
            kActivity.mConnection.postRequestWithHttpHeaders(kActivity, "getLanding/page/" + kActivity.currentPage, this, params, 101);
        } else {
            mySwipeRefreshLayout.setRefreshing(false);
            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void gettingProfileComplete() {
        if (kActivity.isOnline()) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", kActivity.mPref.getString("UserId", ""));
            params.put("login_key", kActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", kActivity.mPref.getString("LoginToken", ""));
            kActivity.mConnection.postRequestWithHttpHeaders(kActivity, "getUserProfileComplete", this, params, 103);
        }
    }

    private void getUserList(int type) {
        if (kActivity.isOnline()) {
            kActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", kActivity.mPref.getString("UserId", ""));
            params.put("login_key", kActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", kActivity.mPref.getString("LoginToken", ""));
            if (kActivity.position != 0)
                params.put("user_type", kActivity.position + "");
            else
                params.put("user_type", "1,2,3");

            if (kActivity.sortBy != 3)
                params.put("sort", kActivity.sortBy + "");
            else
                params.put("sort", "0");
            kActivity.mConnection.postRequestWithHttpHeaders(kActivity, "getUsers", this, params, 101);
        } else {
            kActivity.showProgressDialog(false);
            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    @Override
    public void getError(String error, int APICode) {
        if (error != null)
            if (error.equalsIgnoreCase("com.android.volley.error.AuthFailureError"))
                kActivity.logoutProcess();
            else if (kActivity.currentPage == 1) {
                displayModelsData();
                isLastPage = true;
                isLoading = false;
//                noText.setVisibility(View.VISIBLE);
                mySwipeRefreshLayout.setRefreshing(false);
                mProgressBar.setVisibility(View.GONE);
            } else
                kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), error);
        else
            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("270", "Something Wrong! Please try later."));

        kActivity.showProgressDialog(false);
    }

    @Override
    public void getResponse(String response, int APICode) {
        try {
            if (APICode == 102) {
                kActivity.celebritiesList = parseCelebritiesList(response);
                bindCelebritiesData();
                celeDivTxt.setVisibility(View.VISIBLE);
                getLandingUserList();
            } else if (APICode == 101) {
                JSONObject jo = new JSONObject(response);
                if (jo.has("data")) {
                    if (kActivity.currentPage == 1)
                        kActivity.userData = getParseNewLandingUserList(jo.getString("data"));
                    else if (kActivity.userData != null) {
                        ArrayList<UserTypeData> tempData = getParseNewLandingUserList(jo.getString("data"));
                        if (tempData != null && tempData.size() > 0)
                            kActivity.userData.addAll(tempData);
                        else {
                            isLastPage = true;
                            isLoading = false;
//                            noText.setVisibility(View.VISIBLE);
                            mySwipeRefreshLayout.setRefreshing(false);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }

                    if (kActivity.userData == null)
                        kActivity.userData = new ArrayList<>();

                    if (!isLastPage) {
                        isLoading = false;
                        isLastPage = false;
                    }

                    displayModelsData();

                    mySwipeRefreshLayout.setRefreshing(false);
                    mProgressBar.setVisibility(View.GONE);
                }

                refreshCelebrityData(kActivity.mPref.getString("ProfilePic", ""));
//                gettingProfileComplete();
            } else if (APICode == 103) {
                JSONObject jo = new JSONObject(response);
                if (jo.has("status") && jo.getBoolean("status") && jo.has("data")) {
                    JSONObject jProfile = jo.getJSONObject("data");
                    int moreCount = 0;
                    SharedPreferences.Editor editor = kActivity.mPref.edit();
                    if (jProfile.has("profile_complete"))
                        editor.putInt("PComplete", jProfile.getInt("profile_complete"));
                    if (jProfile.has("unread_msg_count"))
                        editor.putInt("UnreadMsg", jProfile.getInt("unread_msg_count"));
                    if (jProfile.has("totalliked")) {
                        editor.putInt("TotalLiked", jProfile.getInt("totalliked"));
                        moreCount = jProfile.getInt("totalliked");
                    }
                    if (jProfile.has("visitor_count")) {
                        editor.putInt("VisitorCount", jProfile.getInt("visitor_count"));
                        moreCount = moreCount + jProfile.getInt("visitor_count");
                    }

                    if (jProfile.has("fav_count")) {
                        editor.putInt("FavCount", jProfile.getInt("fav_count"));
                        moreCount = moreCount + jProfile.getInt("fav_count");
                    }
                    if (jProfile.has("is_paid"))
                        editor.putInt("MemberShip", jProfile.getInt("is_paid"));

                    if (jProfile.has("user_pic")) {
                        refreshCelebrityData(jProfile.getString("user_pic"));
                        editor.putString("ProfilePic", jProfile.getString("user_pic"));
                    }

                    editor.putInt("MoreCount", moreCount);
                    editor.apply();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void displayModelsData() {
        try {
//            UserListAdapter userListAdapter = new UserListAdapter(kActivity, userItemArrayList);
//            userGrid.setAdapter(userListAdapter);
//            parentLayout.setHasFixedSize(true);
//            parentLayout.setLayoutManager(new GridLayoutManager(kActivity, 2));

            if (kActivity.currentPage == 1) {
//                parentLayout.setHasFixedSize(true);
                linearLayoutManager = new LinearLayoutManager(kActivity, RecyclerView.VERTICAL, false);
                parentLayout.setHasFixedSize(false);
                parentLayout.setLayoutManager(linearLayoutManager);
                userAdapter = new UserDataAdapter(kActivity, kActivity.userData, this, isLastPage);
                parentLayout.setAdapter(userAdapter);
            } else {
                userAdapter.addItems(kActivity.userData, isLastPage);
               /* if(parentLayout != null && parentLayout.getAdapter() != null)
                    userAdapter.addItems(userData);
                else{
                    linearLayoutManager = new LinearLayoutManager(kActivity, RecyclerView.VERTICAL, false);
                    parentLayout.setLayoutManager(linearLayoutManager);
                    userAdapter = new UserDataAdapter(kActivity, userData, this);
                    parentLayout.setAdapter(userAdapter);
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Bind celebrities data on UI
    private void bindCelebritiesData() {
        try {
//            celebritiesRV.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(kActivity, RecyclerView.HORIZONTAL, false);
            celebritiesRV.setLayoutManager(linearLayoutManager);
            celebritiesAdapter = new CelebritiesAdapter(kActivity, kActivity.celebritiesList, this, kActivity.mPref.getString("UserId", "0"), kActivity.mPref.getString("51", "Celeb"));
            celebritiesRV.setAdapter(celebritiesAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Parse celebrities data
    private ArrayList<UserItem> parseCelebritiesList(String response) {
        ArrayList<UserItem> values = new ArrayList<>();
        try {
            if (kActivity.mPref.getBoolean("isLogin", false)) {
                uId = Integer.valueOf(kActivity.mPref.getString("UserId", "0"));
                UserItem mUserItem = new UserItem();
                mUserItem.setId(uId);
                mUserItem.setFirstName(kActivity.mPref.getString("Name", ""));
                if (kActivity.mPref.getString("ProfilePic", "") != null && kActivity.mPref.getString("ProfilePic", "").length() < 10)
                    mUserItem.setUserPic(Constants.BASE_IMAGE_URL + kActivity.mPref.getString("ProfilePic", ""));
                else
                    mUserItem.setUserPic(Constants.BASE_IMAGE_URL + kActivity.mPref.getString("ProfilePic", ""));
                values.add(mUserItem);
            }

            JSONObject jMain = new JSONObject(response);
            if (jMain.has("is_paid_celeb")) {
                SharedPreferences.Editor editor = kActivity.mPref.edit();
                editor.putBoolean("IsPaidCeleb", jMain.getBoolean("is_paid_celeb"));
                editor.apply();
            }

            if (jMain.has("data")) {
                JSONArray jData = new JSONArray(jMain.getString("data"));
                for (int i = 0; i < jData.length(); i++) {
                    JSONObject jo = new JSONObject(jData.getString(i));
                    UserItem value = new UserItem();
                    value.setId(jo.getInt("id"));
                    value.setFirstName(jo.getString("user_name"));
                    value.setUserPic(jo.getString("user_pic"));
                    values.add(value);
//                    if (value.getId() == uId)
//                        isAvailable = true;
                }
            }

            if (jMain.has("random_celebs")) {
                kActivity.randomCelebrities = new ArrayList<>();
                JSONArray jData = new JSONArray(jMain.getString("random_celebs"));
                for (int i = 0; i < jData.length(); i++) {
                    JSONObject jo = new JSONObject(jData.getString(i));
                    UserItem value = new UserItem();
                    value.setId(jo.getInt("id"));
                    value.setFirstName(jo.getString("user_name"));
                    value.setUserPic(jo.getString("user_pic"));
                    kActivity.randomCelebrities.add(value);
                }
            }
//            if (!kActivity.mPref.getBoolean("isLogin", false))
//                values.remove(0);
//            else if (kActivity.mPref.getBoolean("isLogin", false) && isAvailable) // TODO check celebrities concept
//                values.remove(0);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }

    // Parse landing user data
    private ArrayList<UserTypeData> getParseLandingUserList(String data) {
        String modelTitle = kActivity.mPref.getString("53", "Models");
        String photographerTitle = kActivity.mPref.getString("316", "Photographer Nearby");
        String agencyTitle = kActivity.mPref.getString("61", "Agencies");
        String sponsoredTitle = kActivity.mPref.getString("54", "Sponsored");

        ArrayList<UserTypeData> values = new ArrayList<>();
        ArrayList<UserItem> userItemArrayList = new ArrayList<>();
        ArrayList<UserItem> photographerArrayList = new ArrayList<>();
        ArrayList<UserItem> sponsorList = new ArrayList<>();
        ArrayList<UserItem> agencyArrayList = new ArrayList<>();

        try {
            JSONObject jo = new JSONObject(data);
            //        1 - models, 2 - photographer, 3 - sponsored, 4 - agency
            if (jo.has("models") && jo.getString("models").length() > 0) { // bind models
                userItemArrayList = getParseUserList(jo.getString("models"), 1);
                if (userItemArrayList == null)
                    userItemArrayList = new ArrayList<>();

                if (jo.has("photographer") && jo.getString("photographer").length() > 0)
                    photographerArrayList = getParseUserList(jo.getString("photographer"), 2);
                if (jo.has("sponsored") && !jo.getString("sponsored").equalsIgnoreCase("null"))
                    sponsorList = getParseSponsorData(jo.getString("sponsored"), 3);
                if (jo.has("agency") && jo.getString("agency").length() > 0)
                    agencyArrayList = getParseUserList(jo.getString("agency"), 4);

                if (userItemArrayList.size() > 0) {
                    int totalModels = userItemArrayList.size();
                    int steps = totalModels / 4;
                    int display = 1;
                    if (steps == 0) {
                        UserTypeData remainModel = new UserTypeData();
                        remainModel.setdType(1); // Models
                        remainModel.setType(modelTitle);
                        remainModel.setUserItems(new ArrayList<UserItem>(userItemArrayList.subList(0, totalModels)));
                        remainModel.setTotalRecord(totalModels);
                        if (remainModel.getUserItems() != null && remainModel.getUserItems().size() > 0)
                            values.add(remainModel);

                        if (jo.has("photographer")) { // Bind photographer
                            UserTypeData userTypeData2 = new UserTypeData();
                            userTypeData2.setdType(2); // Photographers
                            userTypeData2.setType(photographerTitle);
                            userTypeData2.setUserItems(photographerArrayList);
                            userTypeData2.setTotalRecord(photographerArrayList.size());

                            if (userTypeData2.getUserItems() != null && userTypeData2.getUserItems().size() > 0) {
                                userTypeData2.setTotalRecord(userTypeData2.getUserItems().size());
                                values.add(userTypeData2);
                            }
                        }

                        // Bind remaining data for sponsored and agency
                        if (jo.has("sponsored")) { // Bind sponsored
                            UserTypeData userTypeData3 = new UserTypeData();
                            userTypeData3.setdType(3); // sponsored
                            userTypeData3.setType(sponsoredTitle);
                            userTypeData3.setUserItems(sponsorList);
                            if (userTypeData3.getUserItems() != null && userTypeData3.getUserItems().size() > 0) {
                                userTypeData3.setTotalRecord(userTypeData3.getUserItems().size());
                                values.add(userTypeData3);
                            }
                        }

                        if (jo.has("agency")) { // Bind agency
                            UserTypeData userTypeData4 = new UserTypeData();
                            userTypeData4.setdType(4); // agency
                            userTypeData4.setType(agencyTitle);
                            userTypeData4.setUserItems(agencyArrayList);
                            userTypeData4.setTotalRecord(agencyArrayList.size());
                            if (userTypeData4.getUserItems() != null && userTypeData4.getUserItems().size() > 0) {
                                userTypeData4.setTotalRecord(userTypeData4.getUserItems().size());
                                values.add(userTypeData4);
                            }
                        }
                    } else {
                        for (int i = 0; i < steps; i++) {
                            UserTypeData userTypeData = new UserTypeData();
                            userTypeData.setdType(1); // Models
                            userTypeData.setType(modelTitle);
                            Log.d("value row::", "" + (i * 4) + " ," + ((i * 4) + 4));
                            userTypeData.setUserItems(new ArrayList<UserItem>(userItemArrayList.subList((i * 4), ((i * 4) + 4))));
                            userTypeData.setTotalRecord((i * 4));
                            values.add(userTypeData);

                            display++;
                            if (display == 2) {
                                if (jo.has("photographer")) { // Bind photographer
                                    UserTypeData userTypeData2 = new UserTypeData();
                                    userTypeData2.setdType(2); // Photographers
                                    userTypeData2.setType(photographerTitle);
                                    userTypeData2.setUserItems(photographerArrayList);
                                    userTypeData2.setTotalRecord(jo.getInt("total_photographer"));

                                    if (userTypeData2.getUserItems() != null && userTypeData2.getUserItems().size() > 0) {
                                        userTypeData2.setTotalRecord(userTypeData2.getUserItems().size());
                                        values.add(userTypeData2);
                                    }
                                }
                            } else if (display == 3) {
                                if (jo.has("sponsored")) { // Bind sponsored
                                    UserTypeData userTypeData3 = new UserTypeData();
                                    userTypeData3.setdType(3); // sponsored
                                    userTypeData3.setType(sponsoredTitle);
                                    userTypeData3.setUserItems(sponsorList);
                                    if (userTypeData3.getUserItems() != null && userTypeData3.getUserItems().size() > 0) {
                                        userTypeData3.setTotalRecord(userTypeData3.getUserItems().size());
                                        values.add(userTypeData3);
                                    }
                                }
                            } else if (display == 4) {
                                if (jo.has("agency")) { // Bind agency
                                    UserTypeData userTypeData4 = new UserTypeData();
                                    userTypeData4.setdType(4); // agency
                                    userTypeData4.setType(agencyTitle);
                                    userTypeData4.setUserItems(agencyArrayList);
                                    userTypeData4.setTotalRecord(jo.getInt("total_agency"));
                                    if (userTypeData4.getUserItems() != null && userTypeData4.getUserItems().size() > 0) {
                                        userTypeData4.setTotalRecord(userTypeData4.getUserItems().size());
                                        values.add(userTypeData4);
                                    }
                                }
                            }

                            // Missed records
                            if ((i + 1) == steps) {
                                int bindModels = ((i * 4) + 4);
                                if (totalModels > bindModels) {
                                    UserTypeData remainModel = new UserTypeData();
                                    remainModel.setdType(1); // Models
                                    remainModel.setType(modelTitle);
                                    remainModel.setUserItems(new ArrayList<UserItem>(userItemArrayList.subList(((i * 4) + 4), totalModels)));
                                    remainModel.setTotalRecord(totalModels - bindModels);
                                    Log.d("value remain::", "" + ((i * 4) + 4) + " ," + totalModels);
                                    if (remainModel.getUserItems() != null && remainModel.getUserItems().size() > 0)
                                        values.add(remainModel);
                                }

                                // Bind remaining data for sponsored and agency
                                if (steps == 1) {
                                    if (jo.has("sponsored")) { // Bind sponsored
                                        UserTypeData userTypeData3 = new UserTypeData();
                                        userTypeData3.setdType(3); // sponsored
                                        userTypeData3.setType(sponsoredTitle);
                                        userTypeData3.setUserItems(sponsorList);
                                        if (userTypeData3.getUserItems() != null && userTypeData3.getUserItems().size() > 0) {
                                            userTypeData3.setTotalRecord(userTypeData3.getUserItems().size());
                                            values.add(userTypeData3);
                                        }
                                    }

                                    if (jo.has("agency")) { // Bind agency
                                        UserTypeData userTypeData4 = new UserTypeData();
                                        userTypeData4.setdType(4); // agency
                                        userTypeData4.setType(agencyTitle);
                                        userTypeData4.setUserItems(agencyArrayList);
                                        userTypeData4.setTotalRecord(jo.getInt("total_agency"));
                                        if (userTypeData4.getUserItems() != null && userTypeData4.getUserItems().size() > 0) {
                                            userTypeData4.setTotalRecord(userTypeData4.getUserItems().size());
                                            values.add(userTypeData4);
                                        }
                                    }
                                } else if (steps == 2) {
                                    if (jo.has("agency")) { // Bind agency
                                        UserTypeData userTypeData4 = new UserTypeData();
                                        userTypeData4.setdType(4); // agency
                                        userTypeData4.setType(agencyTitle);
                                        userTypeData4.setUserItems(agencyArrayList);
                                        userTypeData4.setTotalRecord(jo.getInt("total_agency"));
                                        if (userTypeData4.getUserItems() != null && userTypeData4.getUserItems().size() > 0) {
                                            userTypeData4.setTotalRecord(userTypeData4.getUserItems().size());
                                            values.add(userTypeData4);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    // Parse landing user data with new format
    private ArrayList<UserTypeData> getParseNewLandingUserList(String data) {
        String modelTitle = kActivity.mPref.getString("53", "Models");
        String photographerTitle = kActivity.mPref.getString("316", "Photographer Nearby");
        String agencyTitle = kActivity.mPref.getString("61", "Agencies");
        String sponsoredTitle = kActivity.mPref.getString("54", "Sponsored");

        ArrayList<UserTypeData> values = new ArrayList<>();

        try {
            JSONObject jo = new JSONObject(data);
            //        1 - models, 2 - photographer, 3 - sponsored, 4 - agency
            JSONArray jMain = jo.getJSONArray("allnewdata");
            for (int i = 0; i < jMain.length(); i++) {
                JSONObject jData = jMain.getJSONObject(i);
                if (jData.has("models")) {
                    UserTypeData userTypeData = new UserTypeData();
                    userTypeData.setdType(1); // Models
                    userTypeData.setType(modelTitle);
                    userTypeData.setUserItems(getParseUserList(jData.getString("models"), 1));
                    if (userTypeData.getUserItems() != null && userTypeData.getUserItems().size() > 0)
                        values.add(userTypeData);
                } else if (jData.has("photographer")) {
                    UserTypeData userTypeData2 = new UserTypeData();
                    userTypeData2.setdType(2); // Photographers
                    userTypeData2.setType(photographerTitle);
                    userTypeData2.setUserItems(getParseUserList(jData.getString("photographer"), 2));
                    if (userTypeData2.getUserItems() != null && userTypeData2.getUserItems().size() > 0)
                        values.add(userTypeData2);
                } else if (jData.has("sponsord")) {
                    UserTypeData userTypeData3 = new UserTypeData();
                    userTypeData3.setdType(3); // sponsored
                    userTypeData3.setType(sponsoredTitle);
                    userTypeData3.setUserItems(getParseSponsorData(jData.getString("sponsord"), 3));
                    if (userTypeData3.getUserItems() != null && userTypeData3.getUserItems().size() > 0)
                        values.add(userTypeData3);
                } else if (jData.has("agency")) {
                    UserTypeData userTypeData4 = new UserTypeData();
                    userTypeData4.setdType(4); // agency
                    userTypeData4.setType(agencyTitle);
                    userTypeData4.setUserItems(getParseUserList(jData.getString("agency"), 4));
                    if (userTypeData4.getUserItems() != null && userTypeData4.getUserItems().size() > 0)
                        values.add(userTypeData4);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    // Parse sponsored data
    private ArrayList<UserItem> getParseSponsorData(String data, int type) {
        ArrayList<UserItem> values = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                UserItem value = new UserItem();
                JSONObject jo = jsonArray.getJSONObject(i);
                value.setId(jo.getInt("id"));
                if (sponsors_ids != null && sponsors_ids.length() == 0)
                    sponsors_ids = jo.getString("id");
                else
                    sponsors_ids = sponsors_ids + "," + jo.getString("id");

                if (jo.has("img"))
                    value.setUserPic(jo.getString("img"));
                value.setFirstName(jo.getString("redirect_link")); // Sponsored click link
                value.setCity(jo.getString("start_date"));
                value.setCountry(jo.getString("expiry_date"));
                value.setIsActive(jo.getInt("status"));
                value.setType(type);
                values.add(value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }


    // Parse user list
    private ArrayList<UserItem> getParseUserList(String data, int type) {
        ArrayList<UserItem> values = new ArrayList<>();
        try {
            JSONArray jData = new JSONArray(data);
            for (int i = 0; i < jData.length(); i++) {
                JSONObject jo = new JSONObject(jData.getString(i));
                UserItem value = new UserItem();
                value.setId(jo.getInt("id"));
                value.setFirstName(jo.getString("first_name"));
                if (jo.getString("birth_year") != null && !jo.getString("birth_year").equalsIgnoreCase("null") && jo.getString("birth_year").length() > 0) {
                    value.setBirthYear(kActivity.utility.getYearsCountFromDate(jo.getString("birth_year"), jo.getString("birth_month"), jo.getString("birth_day")));
                }
                value.setTotalImage(jo.getInt("total_image"));
                value.setCity(jo.getString("city"));
                value.setCountry(jo.getString("country"));
                value.setIsActive(jo.getInt("is_active"));
                value.setUserPic(jo.getString("user_pic"));
                value.setType(type);
                values.add(value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    @Override
    public void networkError(String error, int APICode) {
        kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("269", "Network failed! Please try later."));
        kActivity.showProgressDialog(false);
    }

    @Override
    public void onResume() {
        super.onResume();
//        kActivity.navigationView.getMenu().getItem(0).setChecked(true);
        if (isInit || isVisible()) {
            isInit = false;
            kActivity.updateActionBar(0, null, true, false, true);
            kActivity.hideKeyboard(kActivity);
            kActivity.gettingProfileComplete();
            refreshCelebrityData(kActivity.mPref.getString("ProfilePic", ""));
//            if (userAdapter != null && userData.size() > 0)
//                userAdapter.notifyDataSetChanged();
//            else if (kActivity.currentPage != 1)
//                onRefresh();
        }
    }

    public void refreshCelebrityData(String user_pic) {
        try {
            if (kActivity.mPref.getBoolean("isLogin", false) && celebritiesAdapter != null && kActivity.celebritiesList != null && kActivity.celebritiesList.size() > 0) {
                kActivity.celebritiesList.get(0).setUserPic(Constants.BASE_IMAGE_URL + user_pic);
                celebritiesAdapter.refreshData(kActivity.celebritiesList);
                kActivity.refreshUserProfile(user_pic);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getInfoValue(int parentPos, int childPos) {
        try {
            // TODO - check with ankit
//            if (uId == kActivity.celebritiesList.get(parentPos).getId()) {
            if (parentPos == 0 && uId != 0 && uId == kActivity.celebritiesList.get(parentPos).getId()) {
//                if (!kActivity.mPref.getBoolean("IsPaidCeleb", false))
//                    kActivity.showMemberDialog(kActivity.celebritiesList.get(parentPos));
//                else
                kActivity.showCelebrityInfo(kActivity.celebritiesList.get(parentPos), 3);
            } else {
                Intent intent = new Intent(kActivity, KenbieNavigationActivity.class);
                intent.putExtra("NavType", 6); // 1 -fav,2 - like
                intent.putExtra("UserItem", kActivity.celebritiesList.get(parentPos));
                startActivity(intent);
//                kActivity.viewUserProfile(kActivity.celebritiesList.get(parentPos));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getUserAction(UserItem userItem, int type) {
//        1 - models, 2 - photographer, 3 - sponsored, 4 - agency, 5 - view All models
        //  View user profile data
        if (type == 3) {
            String url = userItem.getFirstName();
            if (url != null && url.length() > 5) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }

//            Intent intent = new Intent(kActivity, KenbieWebActivity.class);
//            intent.putExtra("Type", 3);
//            intent.putExtra("URL", userItem.getFirstName());
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
        } else if (type == 5 || type == 6 || type == 7) { //  5 - models, 6 - photographer, 7 - agency
            ArrayList<UserItem> userItemArrayList = new ArrayList<>();
            for (int i = 0; i < kActivity.userData.size(); i++) {
                if (type == 5 && kActivity.userData.get(i).getdType() == 1) // Models
                    userItemArrayList.addAll(kActivity.userData.get(i).getUserItems());
                else if (type == 6 && kActivity.userData.get(i).getdType() == 2) // photographer
                    userItemArrayList.addAll(kActivity.userData.get(i).getUserItems());
                else if (type == 7 && kActivity.userData.get(i).getdType() == 4) // agency
                    userItemArrayList.addAll(kActivity.userData.get(i).getUserItems());
            }


            ModelsFragment modelsFragment = new ModelsFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("Models", userItemArrayList);
            if (type == 5)
                bundle.putInt("Type", 1); // Model
            else if (type == 6)
                bundle.putInt("Type", 3); // Photographers
            else // if(type == 7)
                bundle.putInt("Type", 2); // Agency
            modelsFragment.setArguments(bundle);
//            kActivity.replaceFragment(modelsFragment, true, false);

            Intent intent = new Intent(kActivity, KenbieNavigationActivity.class);
            intent.putExtra("NavType", 22);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Intent intent = new Intent(kActivity, KenbieNavigationActivity.class);
            intent.putExtra("NavType", 6); // 1 -fav,2 - like
            intent.putExtra("UserItem", userItem);
            startActivity(intent);
//            kActivity.viewUserProfile(userItem);
        }
    }

    @Override
    public void onRefresh() {
        kActivity.currentPage = 1;
        isLastPage = false;
//        noText.setVisibility(View.GONE);
        if (!isLoading)
            getLandingUserList();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public void showTopPosition(int i) {
        try {
            parentLayout.scrollToPosition(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
