package com.kenbie.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kenbie.KenbieActivity;
import com.kenbie.KenbieApplication;
import com.kenbie.KenbieNavigationActivity;
import com.kenbie.R;
import com.kenbie.adapters.UserGridDataAdapter;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.listeners.UserActionListener;
import com.kenbie.model.UserItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rajaw on 7/30/2017.
 */

public class UserGridFavFragment extends BaseFragment implements APIResponseHandler, UserActionListener, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView userGrid;
    private ArrayList<UserItem> userItemArrayList;
    private UserGridDataAdapter userListAdapter = null;
    private int type = 1, currentPage = 1;
    private boolean isLoading, isLastPage;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private GridLayoutManager gridLayoutManager = null;
    private TextView noDataTxt;
    private ProgressBar mProgressBar;

    public UserGridFavFragment() {
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mySwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        mySwipeRefreshLayout.setColorSchemeResources(
                R.color.red_g_color);
        mySwipeRefreshLayout.setOnRefreshListener(this);

        noDataTxt = view.findViewById(R.id.no_data);
        noDataTxt.setText(kActivity.mPref.getString("148", "Data is not found. Please pull to refresh."));
        noDataTxt.setTypeface(KenbieApplication.S_SEMI_BOLD);

//        noText = view.findViewById(R.id.no_text);
//        noText.setText(kActivity.mPref.getString("56", "You've reached the end of the list"));
//        noText.setTypeface(KenbieApplication.S_SEMI_BOLD);

        mProgressBar = view.findViewById(R.id.progressBar);

        userGrid = (RecyclerView) view.findViewById(R.id.models_grid);
        userGrid.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(kActivity, 2);
        userGrid.setLayoutManager(gridLayoutManager);
        userGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = gridLayoutManager.getChildCount();
                int totalItemCount = gridLayoutManager.getItemCount();
                int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
//                        noText.setVisibility(View.GONE);
                        currentPage++;
                        getFavUserList(type);
                    }
                }/* else if (isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) == totalItemCount)
                        noText.setVisibility(View.VISIBLE);
                    else
                        noText.setVisibility(View.GONE);
                }*/
            }
        });

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (userListAdapter.getItemViewType(position) == 1)
                    return 2;
                else
                    return 1;
            }
        });

       /* userGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (kActivity.mPref.getInt("MemberShip", 0) == 0)
                    kActivity.showMemberShipInfo(userItemArrayList.get(position), 1);
                else
                    kActivity.viewUserProfile(userItemArrayList.get(position));
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
                new MConnection().postRequestWithHttpHeaders(kActivity, "getMyFavourites/page/" + currentPage, this, params, 101);
            else if (type == 2) // Get Liked You
                new MConnection().postRequestWithHttpHeaders(kActivity, "getLikedYou/page/" + currentPage, this, params, 102);
            else if (type == 3) // Get Visitors
                new MConnection().postRequestWithHttpHeaders(kActivity, "getVisitors", this, params, 103);
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

        if (APICode == 101 || APICode == 102) {
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
            if (APICode == 101 || APICode == 102 || APICode == 103) {
                JSONObject jo = new JSONObject(response);
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

                    mProgressBar.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mySwipeRefreshLayout.setRefreshing(false);
        isLoading = false;
    }

    private void displayData() {
        try {
            if (userItemArrayList == null)
                userItemArrayList = new ArrayList<>();

            if (userItemArrayList.size() == 0) {
                noDataTxt.setVisibility(View.VISIBLE);
                userGrid.setVisibility(View.GONE);
            } else {
                noDataTxt.setVisibility(View.GONE);
                userGrid.setVisibility(View.VISIBLE);
                if (currentPage == 1) {
                    userListAdapter = new UserGridDataAdapter(kActivity, userItemArrayList, this, isLastPage);
                    userGrid.setAdapter(userListAdapter);
                } else {
                    userListAdapter.refreshData(userItemArrayList, isLastPage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshToStart() {
        try {
            userGrid.scrollToPosition(0);
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
                if (jo.getString("birth_year") != null && jo.getString("birth_year").length() > 0 && !jo.getString("birth_year").equalsIgnoreCase("null"))
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
                    value.setBirthDay(jo.getInt("isFav")); // Fav status
                } else if (type == 1) {
                    if (jo.has("fav_seen"))
                        value.setIsActive(jo.getInt("fav_seen"));
                } else if (type == 2) {
                    if (jo.has("liked_seen"))
                        value.setIsActive(jo.getInt("liked_seen"));
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

    @Override
    public void onResume() {
        super.onResume();
        // Added for hide action bar
//        currentPage = 1;
        Log.d("Favorites", type + "::::::::::Favorites");
        if (kActivity.isExtraFragment(2)) {
            kActivity.updateActionBar(35, "PROFILE VISITOR", false, false, true);
            kActivity.gettingProfileComplete();
        }
        if (currentPage != 1)
            onRefresh();
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

        if (!isLoading)
            getFavUserList(type);
    }

    @Override
    public void getUserAction(UserItem userItem, int position) {
        if (kActivity.mPref.getInt("MemberShip", 0) == 0) {
            Intent intent = new Intent(kActivity, KenbieNavigationActivity.class);
            intent.putExtra("NavType", 7);
            intent.putExtra("Type", 1);
            intent.putExtra("UserItem", userItemArrayList.get(position));
            startActivity(intent);
//            kActivity.showMemberShipInfo(userItemArrayList.get(position), 1);
        } else {
            if (userItemArrayList.get(position).getIsActive() == 0) {
                userItemArrayList.get(position).setIsActive(1);
                userListAdapter.refreshData(userItemArrayList, isLastPage);
                SharedPreferences.Editor editor = kActivity.mPref.edit();
                if (type == 1)
                    editor.putInt("FavCount", kActivity.mPref.getInt("FavCount", 0) - 1);
                else
                    editor.putInt("TotalLiked", kActivity.mPref.getInt("TotalLiked", 0) - 1);
                editor.apply();
            }
            Intent intent = new Intent(kActivity, KenbieNavigationActivity.class);
            intent.putExtra("NavType", type == 1 ? 4 : 5); // 1 -fav,2 - like
            intent.putExtra("UserItem", userItem);
            startActivity(intent);
//            kActivity.viewUserProfile(userItemArrayList.get(pos));
        }
    }
}
