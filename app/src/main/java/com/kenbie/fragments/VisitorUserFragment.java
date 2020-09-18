package com.kenbie.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kenbie.KenbieActivity;
import com.kenbie.KenbieApplication;
import com.kenbie.KenbieNavigationActivity;
import com.kenbie.LoginActivity;
import com.kenbie.MessageConvActivity;
import com.kenbie.R;
import com.kenbie.adapters.VisitorUserRecycleViewAdapter;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.listeners.MsgUserActionListeners;
import com.kenbie.model.MsgUserItem;
import com.kenbie.model.UserItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by rajaw on 7/30/2017.
 */

public class VisitorUserFragment extends BaseFragment implements APIResponseHandler, MsgUserActionListeners, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView userListView;
    private ArrayList<UserItem> userItemArrayList;
    private int type = 1, currentPage = 1;
    private VisitorUserRecycleViewAdapter userListAdapter = null;
    private boolean isLoading, isLastPage;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private TextView noDataTxt;
    private ProgressBar mProgressBar;
    private LinearLayoutManager linearLayoutManager = null;

    public VisitorUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            type = getArguments().getInt("Type", 1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grids, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noDataTxt = view.findViewById(R.id.no_data);
        noDataTxt.setText(kActivity.mPref.getString("148", "Data is not found. Please pull to refresh."));
        noDataTxt.setTypeface(KenbieApplication.S_SEMI_BOLD);

//        noText = view.findViewById(R.id.no_text);
//        noText.setText(kActivity.mPref.getString("56", "You've reached the end of the list"));
//        noText.setTypeface(KenbieApplication.S_SEMI_BOLD);

        mProgressBar = view.findViewById(R.id.progressBar);

        mySwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        mySwipeRefreshLayout.setColorSchemeResources(
                R.color.red_g_color);
        mySwipeRefreshLayout.setOnRefreshListener(this);

        userListView = (RecyclerView) view.findViewById(R.id.models_grid);
        linearLayoutManager = new LinearLayoutManager(kActivity, RecyclerView.VERTICAL, false);
        userListView.setLayoutManager(linearLayoutManager);
//        userListView.setNestedScrollingEnabled(false);
        userListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
//                        noText.setVisibility(View.GONE);

                        currentPage++;
                        getFavUserList(type);
                    }
                } /*else if (isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) == totalItemCount)
                        noText.setVisibility(View.VISIBLE);
                    else
                        noText.setVisibility(View.GONE);
                }*/
            }
        });


/*        userListView.setOnScrollListener(new LazyLoader() {
            @Override
            public void loadMore(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!isLastPage && !isLoading) {
                    currentPage++;
                    getFavUserList(type);
                }
            }

            @Override
            public void removeLoader(boolean show) {
                if (show)
                    noText.setVisibility(View.VISIBLE);
                else
                    noText.setVisibility(View.GONE);
            }
        });*/

        getFavUserList(type);
    }

    private void getFavUserList(int type) {
        if (kActivity.isOnline()) {
            isLoading = true;
            if (currentPage == 1)
                mySwipeRefreshLayout.setRefreshing(true);
            else
                mProgressBar.setVisibility(View.VISIBLE);

            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", kActivity.mPref.getString("UserId", ""));
            params.put("login_key", kActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", kActivity.mPref.getString("LoginToken", ""));

            if (type == 1) // Favorites
                new MConnection().postRequestWithHttpHeaders(kActivity, "getMyFavourites", this, params, 101);
            else if (type == 2) // Get Liked You
                new MConnection().postRequestWithHttpHeaders(kActivity, "getLikedYou", this, params, 102);
            else if (type == 3) // Get Visitors
                new MConnection().postRequestWithHttpHeaders(kActivity, "getVisitors/page/" + currentPage, this, params, 103);
        } else {
            mySwipeRefreshLayout.setRefreshing(false);
            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    @Override
    public void getError(String error, int APICode) {
//        if (error != null)
//            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), error);
//        else
//            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("270", "Something Wrong! Please try later."));
        if (APICode == 103) {
            if (currentPage == 1)
                displayData();
            else {
                isLastPage = true;
                displayData();
            }
        }

        isLoading = false;
        mProgressBar.setVisibility(View.GONE);
        mySwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void getResponse(String response, int APICode) {
        try {
            JSONObject jo = new JSONObject(response);

            if (APICode == 101 || APICode == 102 || APICode == 103) {
                if (jo.has("data")) {
                    if (currentPage == 1) {
                        userItemArrayList = getParseUserList(jo.getString("data"));
                        if (userItemArrayList == null)
                            userItemArrayList = new ArrayList<>();
                        if (userItemArrayList.size() < 20)
                            isLastPage = true;
                    } else {
                        ArrayList<UserItem> tempData = getParseUserList(jo.getString("data"));
                        if (tempData != null && tempData.size() > 0)
                            userItemArrayList.addAll(tempData);
                        else
                            isLastPage = true;
                    }

                    displayData();
                }
            } else if (APICode == 104) { // Add like and Add fav  || APICode == 105
                if (jo.has("other_data"))
                    kActivity.saveProfileUpdateCount(jo.getString("other_data"), 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        isLoading = false;
        mySwipeRefreshLayout.setRefreshing(false);
        mProgressBar.setVisibility(View.GONE);
    }

    private void displayData() {
        try {
            if (userItemArrayList == null)
                userItemArrayList = new ArrayList<>();

            if (userItemArrayList.size() == 0) {
                noDataTxt.setVisibility(View.VISIBLE);
                userListView.setVisibility(View.GONE);
            } else {
                noDataTxt.setVisibility(View.GONE);
                userListView.setVisibility(View.VISIBLE);
                if (currentPage == 1) {
                    userListAdapter = new VisitorUserRecycleViewAdapter(kActivity, userItemArrayList, this, isLastPage);
                    userListView.setAdapter(userListAdapter);
                } else {
                    userListAdapter.refreshData(userItemArrayList, isLastPage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                if (jo.has("total_image"))
                    value.setTotalImage(jo.getInt("total_image"));
                value.setCity(jo.getString("city"));
                value.setCountry(jo.getString("country"));
                value.setUserPic(jo.getString("user_pic"));

                if (type == 3) { // Visitors data
                    value.setActiveDate(jo.getString("visited_time"));
                    value.setIsActive(jo.getInt("is_active")); // Online status
                    value.setType(jo.getInt("isLiked")); // Liked or dis like
                    value.setBirthMonth(jo.getInt("liked_seen")); // Message read and unread
//                    value.setIsActive(jo.getInt("liked_seen")); // liked seen
                    value.setBirthDay(jo.getInt("isFav")); // Fav status
                }

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
        isLoading = false;
        mySwipeRefreshLayout.setRefreshing(false);
    }

    private void startApp() {
        Intent intent = new Intent(kActivity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        kActivity.finish();
    }

    // Add like to user
    private void updateUserLike(UserItem user) {
        if (kActivity.isOnline()) {
//            kActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", kActivity.mPref.getString("UserId", ""));
            params.put("login_key", kActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", kActivity.mPref.getString("LoginToken", ""));
            params.put("user_type", kActivity.mPref.getInt("UserType", 1) + "");
            params.put("liked_id", user.getId() + "");
            try {
                WifiManager wm = (WifiManager) kActivity.getApplicationContext().getSystemService(WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                if (ip == null)
                    params.put("ip", "");
                else
                    params.put("ip", ip + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MConnection().postRequestWithHttpHeaders(kActivity, "addLike", this, params, 104);
        } else {
            kActivity.showProgressDialog(false);
            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    // Added into fav list
    private void addedUserFavorites(UserItem user) {
        if (kActivity.isOnline()) {
//            kActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", kActivity.mPref.getString("UserId", ""));
            params.put("login_key", kActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", kActivity.mPref.getString("LoginToken", ""));
            params.put("user_type", kActivity.mPref.getInt("UserType", 1) + "");
            params.put("fav_id", user.getId() + "");
            try {
                WifiManager wm = (WifiManager) kActivity.getApplicationContext().getSystemService(WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                if (ip == null)
                    params.put("ip", "");
                else
                    params.put("ip", ip + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MConnection().postRequestWithHttpHeaders(kActivity, "addFavourite", this, params, 105);
        } else {
            kActivity.showProgressDialog(false);
            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    @Override
    public void updateFavStatus(int type, int pos) {
//        value.setIsActive(jo.getInt("is_active")); // Online status
//        value.setType(jo.getInt("isLiked")); // Liked or dis like
//        value.setBirthMonth(jo.getInt("liked_seen")); // Message read and unread
//        value.setBirthDay(jo.getInt("isFav")); //
        try {
            if (type == 3) {
                if (kActivity.mPref.getInt("MemberShip", 0) == 0) {
                    Intent intent = new Intent(kActivity, KenbieNavigationActivity.class);
                    intent.putExtra("NavType", 7);
                    intent.putExtra("Type", 1);
                    intent.putExtra("UserItem", userItemArrayList.get(pos));
                    startActivity(intent);
//                    kActivity.showMemberShipInfo(userItemArrayList.get(position), 1);
                } else {
                    if (userItemArrayList.get(pos).getBirthMonth() == 0) {
                        userItemArrayList.get(pos).setBirthMonth(1);
                        userListAdapter.refreshData(userItemArrayList, isLastPage);
                        SharedPreferences.Editor editor = kActivity.mPref.edit();
                        editor.putInt("VisitorCount", (kActivity.mPref.getInt("VisitorCount", 0) - 1));
                        editor.putInt("MoreCount", (kActivity.mPref.getInt("MoreCount", 0) - 1));
                        editor.apply();
                        kActivity.refreshTopAndBottomCount(0, 3);
                    }
                    Intent intent = new Intent(kActivity, KenbieNavigationActivity.class);
                    intent.putExtra("NavType", 3);
                    intent.putExtra("UserItem", userItemArrayList.get(pos));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

//                    Intent intent = new Intent(kActivity, KenbieActivity.class);
//                    intent.putExtra("NavType", 3);
//                    intent.putExtra("UserItem", userItemArrayList.get(pos));
//                    startActivity(intent);
//                    kActivity.viewUserProfile(userItemArrayList.get(position));
                }
            } else if (!kActivity.mPref.getBoolean("isLogin", false)) {
                startApp();
            } else if (kActivity.mPref.getInt("MemberShip", 0) == 0)
                kActivity.showMemberShipInfo(userItemArrayList.get(pos), 1);
            else {
                if (type == 1) { // Like
                    if (userItemArrayList.get(pos).getType() == 0) {
//                        userItemArrayList.get(pos).setType(userItemArrayList.get(pos).getType() == 1 ? 0 : 1);
                        userItemArrayList.get(pos).setType(1);
                        userListAdapter.refreshData(userItemArrayList, isLastPage);
                        updateUserLike(userItemArrayList.get(pos));
                    }
                } else if (type == 2) { // Favorite
                    kActivity.refreshTopAndBottomCount(userItemArrayList.get(pos).getBirthDay() == 1 ? -1 : 1, 2);
                    userItemArrayList.get(pos).setBirthDay(userItemArrayList.get(pos).getBirthDay() == 1 ? 0 : 1);
                    userListAdapter.refreshData(userItemArrayList, isLastPage);
                    addedUserFavorites(userItemArrayList.get(pos));
                }
            }

//            else // Message
//                addedUserFavorites(kActivity.msgUserFavouritesList.get(pos).getUid());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void userConStart(MsgUserItem msgUserItem) {
        if (!kActivity.mPref.getBoolean("isLogin", false))
            startApp();
        else if (kActivity.mPref.getInt("MemberShip", 0) == 0) {
            UserItem userItem = new UserItem();
            userItem.setId(msgUserItem.getUid());
            userItem.setFirstName(msgUserItem.getUser_name());
            userItem.setUserPic(msgUserItem.getUser_img());
            kActivity.showMemberShipInfo(userItem, 1);
        } else {
            Intent intent = new Intent(kActivity, MessageConvActivity.class);
            intent.putExtra("MsgItem", msgUserItem);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            kActivity.startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Added for hide action bar
        Log.d("Visitors", type + "::::::::::visitors");
        if (kActivity.isExtraFragment(0)) {
            kActivity.updateActionBar(35, "PROFILE VISITOR", false, false, true);
            kActivity.hideKeyboard(kActivity);
            kActivity.gettingProfileComplete();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        isLastPage = false;
//        noText.setVisibility(View.GONE);
        getFavUserList(type);
    }

    public void refreshToStart() {
        if (userListAdapter != null && userListView != null)
            userListView.setAdapter(userListAdapter);
    }
}
