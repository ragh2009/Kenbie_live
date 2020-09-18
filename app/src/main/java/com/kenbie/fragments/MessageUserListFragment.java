package com.kenbie.fragments;

import static android.content.Context.WIFI_SERVICE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kenbie.KenbieApplication;
import com.kenbie.MessageConvActivity;
import com.kenbie.R;
import com.kenbie.adapters.MsgUserRecycleViewAdapter;
import com.kenbie.databinding.FragmentMessageUserBinding;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.listeners.MsgUserActionListeners;
import com.kenbie.model.MsgUserItem;
import com.kenbie.model.UserItem;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageUserListFragment extends BaseFragment implements APIResponseHandler, MsgUserActionListeners, SwipeRefreshLayout.OnRefreshListener {
    private int favPos, msgPos = -1;
    //    private ListView mUserListView;
    private RecyclerView mUserListView;
    private LinearLayoutManager layoutManager;
    private MsgUserRecycleViewAdapter msgUserListAdapter;
    private EditText searchUser;
    private TextView noText;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private boolean isLoading;

    public MessageUserListFragment() {
        // Required empty public constructor
    }

    public static MessageUserListFragment newInstance() {
        return new MessageUserListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
//        if (getArguments() != null)
//            type = getArguments().getInt("Type", 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentMessageUserBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_message_user_list, container, false);
        mTopBarBinding = binding.topHeaderBar;
        EventBus.getDefault().register(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupTitleBar();

        //You need to add the following line for this solution to work; thanks skayred
//        view.setFocusableInTouchMode(true);
//        view.requestFocus();
//        view.setOnKeyListener( new View.OnKeyListener()
//        {
//            @Override
//            public boolean onKey( View v, int keyCode, KeyEvent event )
//            {
//                if( keyCode == KeyEvent.KEYCODE_BACK )
//                {
//                    return true;
//                }
//                return false;
//            }
//        } );

        mySwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        mySwipeRefreshLayout.setColorSchemeResources(
                R.color.red_g_color);
        mySwipeRefreshLayout.setOnRefreshListener(this);

        searchUser = view.findViewById(R.id.search_user);
        searchUser.setHint(kActivity.mPref.getString("58", "Search"));
        searchUser.setTypeface(KenbieApplication.S_NORMAL);
        searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (msgUserListAdapter != null)
                    msgUserListAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        clearFocus();

        mUserListView = (RecyclerView) view.findViewById(R.id.m_user_list);
        mUserListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                int visibleItemCount = linearLayoutManager.getChildCount();
//                int totalItemCount = linearLayoutManager.getItemCount();
//                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if(layoutManager.findLastCompletelyVisibleItemPosition() > 0)
                    kActivity.msgLastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();

/*                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - 8)
                            && firstVisibleItemPosition >= 0) {
                        kActivity.currentPage++;
                        getLandingUserList();
                    }
                } */
                
                /*else if (isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) == totalItemCount)
                        noText.setVisibility(View.VISIBLE);
                    else
                        noText.setVisibility(View.GONE);
                }*/
            }
        });

        noText = (TextView) view.findViewById(R.id.no_text);
        noText.setText(kActivity.mPref.getString("148", "Data is not found. Please pull to refresh."));
        noText.setTypeface(KenbieApplication.S_NORMAL);

        if (kActivity.msgUserAllList == null)
            getMsgUserListFromServer(true);
        else if (kActivity.msgUserAllList.size() == 0)
            getMsgUserListFromServer(true);
        else if (kActivity.msgLastVisiblePosition < kActivity.msgUserAllList.size())
            refreshData();
        else
            getMsgUserListFromServer(true);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    private void getMsgUserListFromServer(boolean b) {
        if (kActivity.isOnline()) {
            isLoading = true;
            mySwipeRefreshLayout.setRefreshing(b);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", kActivity.mPref.getString("UserId", ""));
            params.put("login_key", kActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", kActivity.mPref.getString("LoginToken", ""));
            kActivity.mConnection.postRequestWithHttpHeaders(kActivity, "getChatUserList", this, params, 101);
        } else {
            isLoading = false;
            mySwipeRefreshLayout.setRefreshing(false);
            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    // Refresh user data
    private void refreshData() {
        try {
//        if (type == 1)
            if (kActivity.msgUserAllList == null)
                kActivity.msgUserAllList = new ArrayList<>();

            if (kActivity.msgUserAllList.size() == 0) {
                noText.setVisibility(View.VISIBLE);
                mUserListView.setVisibility(View.GONE);
            } else {
                noText.setVisibility(View.GONE);
                mUserListView.setVisibility(View.VISIBLE);
            }

//            if(msgUserListAdapter == null) {
            layoutManager = new LinearLayoutManager(kActivity, RecyclerView.VERTICAL, false);
            mUserListView.setLayoutManager(layoutManager);
            msgUserListAdapter = new MsgUserRecycleViewAdapter(kActivity, kActivity.msgUserAllList, this, kActivity.mPref.getInt("MemberShip", 0));
            mUserListView.setAdapter(msgUserListAdapter);
            if (kActivity.msgLastVisiblePosition > 0)
                mUserListView.scrollToPosition(kActivity.msgLastVisiblePosition);
            clearFocus();
//            }else
//                msgUserListAdapter.refreshData(kActivity.msgUserAllList);


/*            if(msgUserListAdapter == null || mUserListView.getFirstVisiblePosition() == 0) {
                msgUserListAdapter = new MsgUserListAdapter(kActivity, kActivity.msgUserAllList, this, kActivity.mPref.getInt("MemberShip", 0));
//        else if (type == 2)
//            msgUserListAdapter = new MsgUserListAdapter(kActivity, kActivity.msgUserOnlineList, this, type);
//        else
//            msgUserListAdapter = new MsgUserListAdapter(kActivity, kActivity.msgUserFavouritesList, this, type);

                mUserListView.setAdapter(msgUserListAdapter);
            }else {
                    msgUserListAdapter.refreshData(kActivity.msgUserAllList);
            }*/

       /*     if (kActivity.msgLastVisiblePosition != -1) {
                mUserListView.setSelection(kActivity.msgLastVisiblePosition);
//            msgUserListAdapter.notifyDataSetChanged();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getError(String error, int APICode) {
        if (error != null)
            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), error);
        else
            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("270", "Something Wrong! Please try later."));

        isLoading = false;
        mySwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void getResponse(String response, int APICode) {
        try {
            if (response != null) {
                JSONObject jo = new JSONObject(response);

                if (APICode == 101) { // Message Users List
                    if (jo.has("status") && jo.getBoolean("status")) {
                        if (jo.has("data"))
                            parseUserMsgData(jo.getString("data"));
                        refreshData();
                    } else if (jo.has("error"))
                        kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), jo.getString("error"));
                    else
                        kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("270", "Something Wrong! Please try later."));
                } else if (APICode == 102) {
                    if (jo.has("status") && jo.getBoolean("status")) {
                        /* favRefreshData();*/
//                        if (jo.has("other_data"))
//                            kActivity.saveProfileUpdateCount(jo.getString("other_data"), 1);
                        kActivity.bindBottomNavigationData();
                    }
                } else if (APICode == 103) {
                    kActivity.msgUserAllList.remove(favPos);
                    msgUserListAdapter.refreshFromData(kActivity.msgUserAllList);
                    if (kActivity.msgUserAllList == null)
                        kActivity.msgUserAllList = new ArrayList<>();

                    if (kActivity.msgUserAllList.size() == 0) {
                        noText.setVisibility(View.VISIBLE);
                        mUserListView.setVisibility(View.GONE);
                    } else {
                        noText.setVisibility(View.GONE);
                        mUserListView.setVisibility(View.VISIBLE);
                    }

                    mUserListView.setAdapter(msgUserListAdapter);
                    kActivity.showProgressDialog(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        isLoading = false;
        mySwipeRefreshLayout.setRefreshing(false);
    }

    public void showTopPosition(int i) {
        if (msgUserListAdapter != null && mUserListView != null)
            mUserListView.setAdapter(msgUserListAdapter);
    }

    // Refresh fav data updateFavStatus
    private void favRefreshData() {
        try {
            msgUserListAdapter.refreshData(kActivity.msgUserAllList);

/*            int favStatus = kActivity.kActivity.msgUserAllList.get(favPos).getIsFav() == 1 ? 0 : 1;

            if (this.favType == 1) {
                kActivity.kActivity.msgUserAllList.get(favPos).setIsFav(favStatus);
                msgUserListAdapter.refreshData(kActivity.kActivity.msgUserAllList);
            } else if (this.favType == 2) {
                kActivity.msgUserOnlineList.get(favPos).setIsFav(favStatus);
                msgUserListAdapter.refreshData(kActivity.msgUserOnlineList);
            } else {
                if (favStatus == 0)
                    kActivity.msgUserFavouritesList.remove(favPos);
                msgUserListAdapter.refreshData(kActivity.msgUserFavouritesList);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void networkError(String error, int APICode) {
        kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("269", "Network failed! Please try later."));
        isLoading = false;
        mySwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void updateFavStatus(int type, int pos) {
        if (kActivity.mPref.getInt("MemberShip", 0) != 0) {
            this.favPos = pos;

            if (type == 1) {
//                this.favType = type;
                try {
                    kActivity.refreshTopAndBottomCount(kActivity.msgUserAllList.get(pos).getIsFav() == 1 ? -1 : 1, 1);
                    kActivity.msgUserAllList.get(favPos).setIsFav(kActivity.msgUserAllList.get(pos).getIsFav() == 1 ? 0 : 1);
                    favRefreshData();
                    addedUserFavorites(kActivity.msgUserAllList.get(pos).getUid());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (type == 2) {
                if (kActivity.msgUserAllList.get(pos).getUid() != 1)
                    deleteChatUser(kActivity.msgUserAllList.get(pos).getUid());
            } else if (type == 3) {
                msgPos = pos;
                Intent intent = new Intent(kActivity, MessageConvActivity.class);
                intent.putExtra("MsgItem", kActivity.msgUserAllList.get(pos));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                kActivity.startActivity(intent);
            }
        } else {
            UserItem userItem = new UserItem();
            userItem.setId(kActivity.msgUserAllList.get(pos).getUid());
            userItem.setFirstName(kActivity.msgUserAllList.get(pos).getUser_name());
            userItem.setUserPic(kActivity.msgUserAllList.get(pos).getUser_img());
            kActivity.showMemberShipInfo(userItem, 1);
        }
        clearFocus();
    }


    @Override
    public void userConStart(MsgUserItem msgUserItem) {
        clearFocus();
        if (kActivity.mPref.getInt("MemberShip", 0) != 0) {
            Intent intent = new Intent(kActivity, MessageConvActivity.class);
            intent.putExtra("MsgItem", msgUserItem);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            kActivity.startActivity(intent);
        } else {
            UserItem userItem = new UserItem();
            userItem.setId(msgUserItem.getUid());
            userItem.setFirstName(msgUserItem.getUser_name());
            userItem.setUserPic(msgUserItem.getUser_img());
            kActivity.showMemberShipInfo(userItem, 1);
        }
    }

    private void clearFocus() {
        searchUser.setFocusableInTouchMode(true);
        searchUser.clearFocus();
        searchUser.setCursorVisible(false);
        searchUser.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                searchUser.setCursorVisible(true);
                return false;
            }
        });
        kActivity.hideKeyboard(kActivity);
    }

    // Delete chat user
    private void deleteChatUser(int uid) {
        if (kActivity.isOnline()) {
            kActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", kActivity.mPref.getString("UserId", ""));
            params.put("login_key", kActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", kActivity.mPref.getString("LoginToken", ""));
            params.put("chat_user_id", uid + "");
            kActivity.mConnection.postRequestWithHttpHeaders(kActivity, "deleteChat", this, params, 103);
        } else {
            kActivity.showProgressDialog(false);
            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }


    // Added into fav list
    private void addedUserFavorites(int uid) {
        if (kActivity.isOnline()) {
//            kActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", kActivity.mPref.getString("UserId", ""));
            params.put("login_key", kActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", kActivity.mPref.getString("LoginToken", ""));
            params.put("user_type", kActivity.mPref.getInt("UserType", 1) + "");
            params.put("fav_id", uid + "");
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
            kActivity.mConnection.postRequestWithHttpHeaders(kActivity, "addFavourite", this, params, 102);
        } else {
            kActivity.showProgressDialog(false);
            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    // Parse message user list
    public void parseUserMsgData(String data) {
        try {
            kActivity.msgUserAllList = new ArrayList<>();
//            msgUserOnlineList = new ArrayList<>();
//            msgUserFavouritesList = new ArrayList<>();

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
                kActivity.msgUserAllList.add(value);
                    /*if (value.getCurrent_status() == 1)
                        msgUserOnlineList.add(value);
                    if (value.getIsFav() == 1)
                        msgUserFavouritesList.add(value);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMsgPostedStatus();
//            refreshData();
        if (isInit || isVisible()) {
            isInit = false;
            kActivity.updateActionBar(22, kActivity.mPref.getString("95", "MESSAGES"), false, false, true);
            kActivity.hideKeyboard(kActivity);
            kActivity.bindBottomNavigationData();
        }
        searchUser.clearFocus();
        searchUser.setFocusableInTouchMode(true);
    }

    private void updateMsgPostedStatus() {
        try {
            if (kActivity.mPref.getBoolean("MsgPosted", false) && kActivity.msgUserAllList != null && msgPos != -1) {
                MsgUserItem msgUserItem = kActivity.msgUserAllList.get(msgPos);
                kActivity.msgUserAllList.remove(msgPos);
                kActivity.msgUserAllList.add(0, msgUserItem);
                msgUserListAdapter.refreshData(kActivity.msgUserAllList);
                SharedPreferences.Editor editor = kActivity.mPref.edit();
                editor.putBoolean("MsgPosted", false);
                editor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        if (!isLoading)
            getMsgUserListFromServer(true);
    }

    public void refreshFromNotification() {
        getMsgUserListFromServer(msgUserListAdapter == null);
    }
}
