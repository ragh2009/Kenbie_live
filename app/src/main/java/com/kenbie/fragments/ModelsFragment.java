package com.kenbie.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kenbie.KenbieActivity;
import com.kenbie.KenbieNavigationActivity;
import com.kenbie.R;
import com.kenbie.adapters.AllModelsDataAdapter;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.listeners.UserActionListener;
import com.kenbie.model.UserItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ModelsFragment extends BaseFragment implements APIResponseHandler, UserActionListener, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView modelsGrid;
    private ArrayList<UserItem> userItemArrayList = null;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private int mType = 1, dataType = 1;
    private String errorMessage;
    private int currentPage = 1;
    private boolean isLoading, isLastPage;
    private AllModelsDataAdapter userChildDataAdapter = null;
    private ProgressBar mProgressBar;

    public ModelsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userItemArrayList = (ArrayList<UserItem>) getArguments().getSerializable("Models");
            mType = getArguments().getInt("Type", 4);
            dataType = getArguments().getInt("DataType", 1);
            errorMessage = getArguments().getString("Error", "No data found.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_models, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        modelsGrid = view.findViewById(R.id.models_grid);
//        noText = view.findViewById(R.id.no_text);
//        noText.setText(mActivity.mPref.getString("56", "You've reached the end of the list"));
//        noText.setTypeface(KenbieApplication.S_SEMI_BOLD);

//        modelsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mActivity.viewUserProfile(userItemArrayList.get(position));
//            }
//        });

        mySwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        mySwipeRefreshLayout.setColorSchemeResources(
                R.color.red_g_color);
        mySwipeRefreshLayout.setOnRefreshListener(this);

        mProgressBar = view.findViewById(R.id.progressBar);

        /**
         * add scroll listener while user reach in bottom load more will call
         */
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 2);
        modelsGrid.setLayoutManager(gridLayoutManager);
        modelsGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = gridLayoutManager.getChildCount();
                int totalItemCount = gridLayoutManager.getItemCount();
                int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - 8)
                            && firstVisibleItemPosition >= 0) {
//                        noText.setVisibility(View.GONE);
                        currentPage++;
                        if (dataType == 2)
                            gettingSearchResult();
                        else
                            gettingUserList();
                    }
                }
            }
        });

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (userChildDataAdapter.getItemViewType(position) == 1)
                    return 2;
                else
                    return 1;
            }
        });
 /*       modelsGrid.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = gridLayoutManager.getChildCount();
                int totalItemCount = gridLayoutManager.getItemCount();
                int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();

                if (!isLoading() && !isLastPage()) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        loadMoreItems();
                    }
                }
            }
        });*/

/*        modelsGrid.addOnScrollListener(new PaginationListener(gridLayoutManager, dataType) {
            @Override
            protected void loadMoreItems() {
                if (dataType != 2) {
                    isLoading = true;
                    currentPage++;
                    userChildDataAdapter.addLoading();
                    gettingUserList();
                }
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public void noTextVisible(boolean b) {
                if (b)
                    noText.setVisibility(View.VISIBLE);
                else
                    noText.setVisibility(View.GONE);
            }
        });*/

        if (dataType == 2) {
            if (userItemArrayList != null && userItemArrayList.size() > 0) {
                modelsGrid.setVisibility(View.VISIBLE);
//                noText.setVisibility(View.GONE);
                displayUsersData();
            } else {
                modelsGrid.setVisibility(View.GONE);
                try {
                    new AlertDialog.Builder(mActivity)
                            .setTitle("")
                            .setMessage(errorMessage)
                            .setPositiveButton(mActivity.mPref.getString("21", "Yes"), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    mActivity.onBackPressed();
                                }
                            })
                            .setIcon(R.mipmap.ic_stat_notification)
                            .show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else
            gettingUserList();

        super.onViewCreated(view, savedInstanceState);
    }

    private void gettingSearchResult() {
        if (mActivity.searchParams != null) {
            if (mActivity.isOnline()) { // 1 - models, 2 - photographer, 3 - agency
                isLoading = true;
                if (currentPage == 1)
                    mySwipeRefreshLayout.setRefreshing(true);
                else
                    mProgressBar.setVisibility(View.VISIBLE);
                mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "search/page/" + currentPage, this, mActivity.searchParams, 103);
            } else
                displayUsersData();
        }
    }

    private void gettingUserList() {
        if (mActivity.isOnline()) { // 1 - models, 2 - photographer, 3 - agency
            isLoading = true;
            if (currentPage == 1)
                mySwipeRefreshLayout.setRefreshing(true);
            else
                mProgressBar.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            if (mActivity.mPref.getBoolean("isLogin", false)) {
                params.put("longitude", mActivity.longitude + "");
                params.put("latitude", mActivity.latitude + "");
            } else
                params.put("ip", mActivity.ip + "");

            params.put("user_type", mType + "");
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, (mType == 3 ? "getUsersNearBy/page/" : "getUsers/page/") + currentPage, this, params, 101);
//            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "getUsers/page/" + currentPage, this, params, 101);
        } else {
            displayUsersData();

            mySwipeRefreshLayout.setRefreshing(false);
//            mActivity.showMessageWithTitle(mActivity, "Alert", Constants.NETWORK_FAIL_MSG);
        }
    }

    @Override
    public void getError(String error, int APICode) {
        if (error != null)
            if (error.equalsIgnoreCase("com.android.volley.error.AuthFailureError"))
                mActivity.logoutProcess();
            else {
                if (APICode == 101 || APICode == 103) {
                    if (currentPage == 1)
                        displayUsersData();
                    else {
                        isLastPage = true;
                        displayUsersData();
                    }
                } else
                    mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), error);
            }
        else
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));

        isLoading = false;
        mProgressBar.setVisibility(View.GONE);
        mySwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void getResponse(String response, int APICode) {
        try {
            JSONObject jo = new JSONObject(response);

            if (jo.has("data")) {
                if (APICode == 101) {
                    if (currentPage == 1) {
                        userItemArrayList = getParseUserList(jo.getString("data"), 1);

                        if (userItemArrayList == null)
                            userItemArrayList = new ArrayList<>();
//                        if (userItemArrayList.size() < 20)
//                            isLastPage = true;
                    } else {
                        ArrayList<UserItem> tempData = getParseUserList(jo.getString("data"), 1);
                        if (tempData != null && tempData.size() > 0)
                            userItemArrayList.addAll(tempData);
                        else
                            isLastPage = true;
                    }

                } else if (APICode == 103) {
                    ArrayList<UserItem> tempData = mActivity.getParseSearchData(jo.getString("data"), 1);
                    if (tempData != null && tempData.size() > 0)
                        userItemArrayList.addAll(tempData);
                    else
                        isLastPage = true;
                }

                displayUsersData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        isLoading = false;
        mProgressBar.setVisibility(View.GONE);
        mySwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void networkError(String error, int APICode) {
        isLoading = false;
        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        mySwipeRefreshLayout.setRefreshing(false);
        mProgressBar.setVisibility(View.GONE);
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
                if (jo.getString("birth_year") != null && !jo.getString("birth_year").equalsIgnoreCase("null") && jo.getString("birth_year").length() > 0)
                    value.setBirthYear(mActivity.utility.getYearsCountFromDate(jo.getString("birth_year"), jo.getString("birth_month"), jo.getString("birth_day")));

//                value.setBirthYear(jo.getInt("birth_year"));
//                value.setBirthMonth(jo.getInt("birth_month"));
//                value.setBirthDay(jo.getInt("birth_day"));
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
    public void onResume() {
        super.onResume();
        if (isInit || isVisible()) {
            isInit = false;
            if (mType == 1) // Model
                mActivity.updateActionBar(4, mActivity.mPref.getString("308", "MODELS"), false, true, false);
            else if (mType == 2) // Agency
                mActivity.updateActionBar(4, mActivity.mPref.getString("309", "AGENCIES"), false, true, false);
            else if (mType == 3) // Photographers
                mActivity.updateActionBar(4, mActivity.mPref.getString("310", "PHOTOGRAPHERS"), false, true, false);
            else //Missing
                mActivity.updateActionBar(2, mActivity.mPref.getString("57", "SEARCH RESULTS"), false, true, false);
        }
//        if (dataType != 2 && currentPage != 1)
//            onRefresh();
    }

    private void displayUsersData() {
        try {
//                bundle.putInt("Type", 1); // Model
//                bundle.putInt("Type", 3); // Photographers
//                bundle.putInt("Type", 2); // Agency

            if (currentPage == 1) {
                userChildDataAdapter = new AllModelsDataAdapter(mActivity, userItemArrayList, this, dataType == 2 ? 4 : mType, isLastPage, mActivity.mPref.getString("56", "You've reached the end of the list"));
                modelsGrid.setAdapter(userChildDataAdapter);
            } else
                userChildDataAdapter.refreshData(userItemArrayList, isLastPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getUserAction(UserItem userItem, int type) {
//        mActivity.viewUserProfile(userItemArrayList.get(userItemArrayList.indexOf(userItem)));
//        mActivity.viewUserProfile(userItem);

        Intent intent = new Intent(mActivity, KenbieNavigationActivity.class);
        intent.putExtra("NavType", 3);
        intent.putExtra("UserItem", userItem);
        mActivity.startActivity(intent);
    }

    @Override
    public void onRefresh() {
        if (dataType != 2) {
            currentPage = 1;
            isLastPage = false;
            if (!isLoading)
                gettingUserList();
        } else
            mySwipeRefreshLayout.setRefreshing(false);
    }
}
