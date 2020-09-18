package com.kenbie.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kenbie.KenbieApplication;
import com.kenbie.KenbieNavigationActivity;
import com.kenbie.R;
import com.kenbie.adapters.CastingUserAdapter;
import com.kenbie.adapters.CastingUserNewAdapter;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.listeners.CastingUserListeners;
import com.kenbie.model.CastingUser;
import com.kenbie.views.SwipeController;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class CastingFragment extends BaseFragment implements APIResponseHandler, CastingUserListeners, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView castingRV;
    private LinearLayoutManager layoutManager = null;
    private View view;
    private Paint p = new Paint();
    private CastingUserAdapter userAdapter;
    private CastingUserNewAdapter castingAdapter;
    private LinearLayout castingOptions;
    private int type, selPos;
    private TextView noDataTxt, noText;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private SwipeController swipeController = null;
    private boolean isLastPage, isLoading;
    private ProgressBar mProgressBar;

    public CastingFragment() {
        // Required empty public constructor
    }

    public static CastingFragment newInstance() {
        return new CastingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null)
            type = getArguments().getInt("Type", 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_casting, container, false);
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

        castingOptions = (LinearLayout) view.findViewById(R.id.casting_options);

        ImageView backBtn = view.findViewById(R.id.m_back_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kActivity.onBackPressed();
            }
        });

        TextView castingTitle = view.findViewById(R.id.casting_title);
        castingTitle.setText(kActivity.mPref.getString("101", "CASTING"));
        castingTitle.setTypeface(KenbieApplication.S_NORMAL);

        TextView addCasting = (TextView) view.findViewById(R.id.add_casting_txt);
        addCasting.setText("+ " + kActivity.mPref.getString("102", "Casting"));
        addCasting.setTypeface(KenbieApplication.S_NORMAL);
        addCasting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kActivity.startAddingCasting(null, 1, false);
            }
        });

        TextView appliedCastingTxt = (TextView) view.findViewById(R.id.applied_casting_txt);
        String applied = kActivity.mPref.getString("103", "Applied");
        if (applied.length() > 15)
            appliedCastingTxt.setText(kActivity.mPref.getString("103", "Applied").substring(0, 15) + "...");
        else
            appliedCastingTxt.setText(kActivity.mPref.getString("103", "Applied"));

        appliedCastingTxt.setTypeface(KenbieApplication.S_NORMAL);
        appliedCastingTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(kActivity, KenbieNavigationActivity.class);
                intent.putExtra("NavType", 18);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
//                kActivity.launchCasting(2);
            }
        });

        TextView castingAroundDiv = (TextView) view.findViewById(R.id.casting_around_div);

        TextView castingAroundTxt = (TextView) view.findViewById(R.id.casting_around_txt);
//        castingAroundTxt.setText("+ " + kActivity.mPref.getString("103", "Applied"));
        castingAroundTxt.setTypeface(KenbieApplication.S_SEMI_BOLD);

        if (type == 1) { // Casting
            castingOptions.setVisibility(View.VISIBLE);
            castingTitle.setText(kActivity.mPref.getString("101", "CASTING"));
            backBtn.setVisibility(View.GONE);
        } else if (type == 2 || type == 3) { //2 - applied, 3- my casting
            castingOptions.setVisibility(View.GONE);
        }

        noText = (TextView) view.findViewById(R.id.casting_no_txt);
        noText.setText(kActivity.mPref.getString("56", "You've reached the end of the list"));
        noText.setTypeface(KenbieApplication.S_NORMAL);

        mProgressBar = view.findViewById(R.id.progressBar);

        castingRV = (RecyclerView) view.findViewById(R.id.casting_rv);
        castingRV.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(kActivity, RecyclerView.VERTICAL, false);
        castingRV.setLayoutManager(layoutManager);

        castingRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (layoutManager.findLastCompletelyVisibleItemPosition() > 0)
                    kActivity.castingLastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();

                if ((type == 1 || type == 3) && !isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - 8)
                            && firstVisibleItemPosition >= 0) {
                        kActivity.castingCurrentPage++;
                        gettingCastingUserDetails();
                    }
                }
            }
        });

        if (type == 3) {
            castingAroundTxt.setVisibility(View.VISIBLE);
            castingAroundDiv.setVisibility(View.VISIBLE);
            castingAroundTxt.setText(kActivity.mPref.getString("158", "VIEW INTERESTED MODELS"));
            castingAroundTxt.setBackgroundColor(kActivity.getResources().getColor(R.color.red_g_color));
            castingAroundTxt.setTextColor(kActivity.getResources().getColor(R.color.white));
            castingAroundTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InterestedModelsFragment interestedModelsFragment = new InterestedModelsFragment();
                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("CastingDetails", kActivity.castingUserArrayList.get(pos));
                    bundle.putInt("Type", type);
                    interestedModelsFragment.setArguments(bundle);
                    kActivity.replaceFragment(interestedModelsFragment, true, false);
                }
            });
        } else {
            castingAroundTxt.setVisibility(View.GONE);
            castingAroundDiv.setVisibility(View.GONE);
        }

        if (kActivity.castingUserArrayList == null)
            gettingCastingUserDetails();
        else if (kActivity.castingUserArrayList.size() == 0)
            gettingCastingUserDetails();
        else if (type == 1 && kActivity.castingLastVisiblePosition < kActivity.castingUserArrayList.size())
            refreshDataOnUi();
        else
            gettingCastingUserDetails();
    }

    private void refreshDataOnUi() {
        try {
            if (kActivity.castingLastVisiblePosition < kActivity.castingUserArrayList.size()) {
                castingRV.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(kActivity, RecyclerView.VERTICAL, false);
                castingRV.setLayoutManager(layoutManager);
                castingAdapter = new CastingUserNewAdapter(kActivity, kActivity.castingUserArrayList, this, 1, kActivity.mPref, isLastPage);
                castingRV.setAdapter(castingAdapter);
                if (kActivity.castingLastVisiblePosition > 0)
                    castingRV.scrollToPosition(kActivity.castingLastVisiblePosition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // data bind
    private void refreshData() {
        if (kActivity.castingUserArrayList == null || (kActivity.castingUserArrayList != null && kActivity.castingUserArrayList.size() == 0)) {
            noDataTxt.setVisibility(View.VISIBLE);
            castingRV.setVisibility(View.GONE);
        } else {
            noDataTxt.setVisibility(View.GONE);
            castingRV.setVisibility(View.VISIBLE);
            if (type == 1) {
                if (kActivity.castingCurrentPage == 1 || castingAdapter == null) {
                    castingAdapter = new CastingUserNewAdapter(kActivity, kActivity.castingUserArrayList, this, 1, kActivity.mPref, isLastPage);
                    castingRV.setAdapter(castingAdapter);
                } else
                    castingAdapter.refreshData(kActivity.castingUserArrayList, isLastPage);
            } else if (type == 2) {
                castingAdapter = new CastingUserNewAdapter(kActivity, kActivity.castingUserArrayList, this, 1, kActivity.mPref, isLastPage);
                castingRV.setAdapter(castingAdapter);
            } else {
                if (kActivity.castingCurrentPage == 1) {
                    userAdapter = new CastingUserAdapter(kActivity, kActivity.castingUserArrayList, this, type, kActivity.mPref, isLastPage);
                    castingRV.setAdapter(userAdapter);
                } else
                    userAdapter.refreshData(kActivity.castingUserArrayList, isLastPage, type);
            }
        }
    }

    private void gettingCastingUserDetails() {
        if (kActivity.isOnline()) {
            isLoading = true;
            if (kActivity.castingCurrentPage == 1)
                mySwipeRefreshLayout.setRefreshing(true);
            else
                mProgressBar.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", kActivity.mPref.getString("UserId", ""));
            params.put("login_key", kActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", kActivity.mPref.getString("LoginToken", ""));
            if (type == 3)
                kActivity.mConnection.postRequestWithHttpHeaders(kActivity, "myCasting/page/" + kActivity.castingCurrentPage, this, params, 101);
            else if (type == 2)
                kActivity.mConnection.postRequestWithHttpHeaders(kActivity, "viewAppliedCastings", this, params, 101);
            else
                kActivity.mConnection.postRequestWithHttpHeaders(kActivity, "viewAllCastings/page/" + kActivity.castingCurrentPage, this, params, 101);
        } else {
            isLoading = false;
            mySwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void deleteCasting() {
        if (kActivity.isOnline()) {
            kActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", kActivity.mPref.getString("UserId", ""));
            params.put("login_key", kActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", kActivity.mPref.getString("LoginToken", ""));
            params.put("casting_id", kActivity.castingUserArrayList.get(selPos).getId() + "");
            kActivity.mConnection.postRequestWithHttpHeaders(kActivity, "deleteCasting", this, params, 102);
        } else {
            kActivity.showProgressDialog(false);
            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    @Override
    public void getError(String error, int APICode) {
        if (APICode == 101) {
            if (kActivity.castingCurrentPage == 1)
                refreshData();
            else {
                isLastPage = true;
                refreshData();
            }
        } else if (error != null)
            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), error);
        else
            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("270", "Something Wrong! Please try later."));

        isLoading = false;
        mySwipeRefreshLayout.setRefreshing(false);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void getResponse(String response, int APICode) {
        try {
            if (response != null) {
                JSONObject jo = new JSONObject(response);

                if (APICode == 101) {
                    if (jo.has("status") && jo.getBoolean("status")) {
                        if (kActivity.castingCurrentPage == 1)
                            kActivity.castingUserArrayList = getCastUserDetails(jo.getString("data"));
                        else if (kActivity.castingUserArrayList != null) {
                            ArrayList<CastingUser> tempData = getCastUserDetails(jo.getString("data"));
                            if (tempData != null && tempData.size() > 0)
                                kActivity.castingUserArrayList.addAll(tempData);
                            else {
                                isLastPage = true;
                                isLoading = false;
                            }
                        }

                        if (kActivity.castingUserArrayList == null)
                            kActivity.castingUserArrayList = new ArrayList<>();
                        if (!isLastPage) {
                            isLoading = false;
                            isLastPage = false;
                        }

                        mySwipeRefreshLayout.setRefreshing(false);
                        mProgressBar.setVisibility(View.GONE);

                        refreshData();
                    } else if (jo.has("error"))
                        kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), jo.getString("error"));
                    else
                        kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("270", "Something Wrong! Please try later."));
                } else if (APICode == 102) {
                    userAdapter.castingUsers.remove(selPos);
                    userAdapter.notifyItemRemoved(selPos);
                    userAdapter.notifyItemRangeChanged(selPos, userAdapter.getItemCount());
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
        try {
            castingRV.scrollToPosition(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Show dialog with message and title
    public void showMessageWithTitle(Context context, String title, String message, final int type) {
        new AlertDialog.Builder(context)
                .setTitle("")
                .setMessage(Html.fromHtml(message))
                .setPositiveButton(kActivity.mPref.getString("21", "Yes"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCasting();
                        dialog.dismiss();
                    }
                }).setNegativeButton(kActivity.mPref.getString("22", "Cancel"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        })
                .setIcon(R.mipmap.ic_launcher)
                .show();
    }


    // Parse casting details
    private ArrayList<CastingUser> getCastUserDetails(String data) {
        ArrayList<CastingUser> values = new ArrayList<>();
        try {
            JSONArray jData = new JSONArray(data);
            for (int i = 0; i < jData.length(); i++) {
                JSONObject jo = new JSONObject(jData.getString(i));
                CastingUser value = new CastingUser();
                value.setFirst_name(jo.getString("first_name"));
                value.setUser_pic(jo.getString("user_pic"));
                value.setUser_id(jo.getInt("user_id"));
                value.setBirth_day(jo.getString("birth_day"));
                value.setBirth_month(jo.getString("birth_month"));
                value.setBirth_year(jo.getString("birth_year"));
                value.setId(jo.getInt("id"));
                value.setCasting_title(jo.getString("casting_title"));
                value.setCasting_requirement(jo.getString("casting_requirement"));
                value.setCasting_type(jo.getString("casting_type"));
                value.setCasting_location(jo.getString("casting_location"));
                value.setCasting_start_age(jo.getString("casting_start_age"));
                value.setCasting_end_age(jo.getString("casting_end_age"));
                value.setCasting_start_time(jo.getString("casting_start_time"));
                value.setCasting_end_time(jo.getString("casting_end_time"));
                value.setCasting_fee(jo.getString("casting_fee"));
                value.setCasting_gender(jo.getString("casting_gender"));
                try {
                    String myData = jo.getString("casting_gender");
                    if (myData != null && !myData.equalsIgnoreCase("null") && myData.length() > 0) {
                        String[] mData = myData.replace(",", "-").split("-");
                        if (mData != null && mData.length > 0) {
                            value.setCasting_gender(mData[0]);
                            if (mData.length > 1)
                                value.setCasting_gender(3 + "");
                        }
                    }

//                    int id = Integer.valueOf(mData[0]);
//                    value.setCasting_gender(id == 1 ? kActivity.mPref.getString("31", "Male") : kActivity.mPref.getString("32", "Female"));

                } catch (Exception e) {
//                    e.printStackTrace();
                    value.setCasting_gender(1 + "");
                }

                value.setCasting_start_date(jo.getString("casting_start_date") + "-" + jo.getString("casting_start_month") + "-" + jo.getString("casting_start_year"));
//                value.setCasting_start_date(jo.getString("casting_start_month") + "-" + jo.getString("casting_start_date") + "-" + jo.getString("casting_start_year"));
                value.setCasting_end_date(jo.getString("casting_end_date") + "-" + jo.getString("casting_end_month") + "-" + jo.getString("casting_end_year"));
//                value.setCasting_end_date(jo.getString("casting_end_month") + "-" + jo.getString("casting_end_date") + "-" + jo.getString("casting_end_year"));

                value.setCasting_categories(jo.getString("casting_categories"));
                value.setCasting_img(jo.getString("casting_img"));
                value.setIs_paid(jo.getInt("is_paid"));
                value.setIs_deleted(jo.getInt("is_deleted"));
                value.setCasting_address(jo.getString("casting_address"));
                if (jo.has("address1"))
                    value.setAddress1(jo.getString("address1"));
                if (jo.has("address2"))
                    value.setAddress2(jo.getString("address2"));
                if (jo.has("country"))
                    value.setCountry(jo.getString("country"));
                if (jo.has("city"))
                    value.setCity(jo.getString("city"));
                if (jo.has("postal_code"))
                    value.setPostal_code(jo.getString("postal_code"));
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
        kActivity.showProgressDialog(false);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isInit || isVisible()) {
            isInit = false;
            if (type == 1)
                kActivity.updateActionBar(6, kActivity.mPref.getString("101", "CASTING"), false, false, true);
            else if (type == 2)
                kActivity.updateActionBar(66, kActivity.mPref.getString("312", "APPLIED CASTING"), false, true, false);
            else
                kActivity.updateActionBar(66, kActivity.mPref.getString("157", "MY CASTING"), false, true, false);
            kActivity.hideKeyboard(kActivity);
        }
//        if (kActivity.castingCurrentPage != 1)
//            onRefresh();
    }

    @Override
    public void getUserDetails(int pos, int type) {
        try {
            if (type == 1 || type == 3) {
                Intent intent = new Intent(kActivity, KenbieNavigationActivity.class);
                intent.putExtra("CastingDetails", kActivity.castingUserArrayList.get(pos));
                intent.putExtra("NavType", 19);
                intent.putExtra("Type", type);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (type == 4) { // View interested models
                InterestedModelsFragment interestedModelsFragment = new InterestedModelsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("CastingDetails", kActivity.castingUserArrayList.get(pos));
                bundle.putInt("Type", type);
                interestedModelsFragment.setArguments(bundle);
                kActivity.replaceFragment(interestedModelsFragment, true, false);
            } else if (type == 5) { // Casting Pay Now
                kActivity.startAddingCasting(kActivity.castingUserArrayList.get(pos), 1, true);
            } else if (type == 6) { // Edit Casting
                selPos = pos;
//                Intent intent = new Intent(kActivity, KenbieNavigationActivity.class);
//                intent.putExtra("CastingDetails", kActivity.castingUserArrayList.get(pos));
//                intent.putExtra("NavType", 20);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                initEditCastData(kActivity.castingUserArrayList.get(pos));

//                kActivity.replaceFragment(new EditCastingDetailsFragment(), true, false);
            } else if (type == 7) { // Delete Casting
                selPos = pos;
                showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("358", "Are you sure you want to delete?"), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    userAdapter.removeItem(position);
                } else {
                    removeView();
                    int delete_position = position;
//                    alertDialog.setTitle("Edit Country");
//                    et_country.setText(countries.get(position));
//                    alertDialog.show();
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
//                        p.setColor(Color.parseColor("#388E3C"));
//                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
//                        c.drawRect(background,p);
//                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
//                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
//                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_msg);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(castingRV);
    }

    private void removeView() {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    private ArrayList<CastingUser> getRandomCastingList() {
        ArrayList<CastingUser> values = new ArrayList<>();
        try {
            if (kActivity.castingUserArrayList != null && kActivity.castingUserArrayList.size() > 2) {
                Random r = new Random();
                values.add(kActivity.castingUserArrayList.get(r.nextInt(kActivity.castingUserArrayList.size())));
                values.add(kActivity.castingUserArrayList.get(r.nextInt(kActivity.castingUserArrayList.size())));
            } else
                values = kActivity.castingUserArrayList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    @Override
    public void onRefresh() {
        kActivity.castingCurrentPage = 1;
        isLastPage = false;
        if (!isLoading) {
            gettingCastingUserDetails();
        }
    }
}
