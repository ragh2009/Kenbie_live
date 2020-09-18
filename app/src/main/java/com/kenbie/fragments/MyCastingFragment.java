package com.kenbie.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
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

import com.kenbie.KenbieActivity;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.adapters.CastingUserAdapter;
import com.kenbie.adapters.CastingUserNewAdapter;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.listeners.CastingUserListeners;
import com.kenbie.model.CastingUser;
import com.kenbie.util.Constants;
import com.kenbie.views.SwipeController;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import ch.datatrans.payment.AliasPaymentMethod;
import ch.datatrans.payment.AliasPaymentMethodCreditCard;
import ch.datatrans.payment.BusinessException;
import ch.datatrans.payment.Customer;
import ch.datatrans.payment.DisplayContext;
import ch.datatrans.payment.IPaymentProcessStateListener;
import ch.datatrans.payment.Payment;
import ch.datatrans.payment.PaymentMethod;
import ch.datatrans.payment.PaymentMethodType;
import ch.datatrans.payment.PaymentProcessAndroid;
import ch.datatrans.payment.ResourceProvider;

import static com.kenbie.util.Constants.PAYMENT_TEST_MODE;


public class MyCastingFragment extends BaseFragment implements APIResponseHandler, CastingUserListeners, SwipeRefreshLayout.OnRefreshListener, IPaymentProcessStateListener {
    private RecyclerView castingRV;
    private LinearLayoutManager layoutManager = null;
    private View view;
    private Paint p = new Paint();
    private CastingUserAdapter userAdapter;
    private CastingUserNewAdapter castingAdapter;
    private LinearLayout castingOptions;
    private int type, selPos, castingLastVisiblePosition, castingCurrentPage = 1;
    private TextView noDataTxt, noText;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private SwipeController swipeController = null;
    private boolean isLastPage, isLoading;
    private ProgressBar mProgressBar;
    public ArrayList<CastingUser> castingUserArrayList;
    private String payRefId = "";
    private CastingUser castingUser;

    public MyCastingFragment() {
        // Required empty public constructor
    }

    public static MyCastingFragment newInstance() {
        return new MyCastingFragment();
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
        noDataTxt.setText(mActivity.mPref.getString("148", "Data is not found. Please pull to refresh."));
        noDataTxt.setTypeface(KenbieApplication.S_SEMI_BOLD);

        castingOptions = (LinearLayout) view.findViewById(R.id.casting_options);

        ImageView backBtn = view.findViewById(R.id.m_back_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });

        TextView castingTitle = view.findViewById(R.id.casting_title);
        castingTitle.setText(mActivity.mPref.getString("101", "CASTING"));
        castingTitle.setTypeface(KenbieApplication.S_NORMAL);

        TextView addCasting = (TextView) view.findViewById(R.id.add_casting_txt);
        addCasting.setText("+ " + mActivity.mPref.getString("102", "Casting"));
        addCasting.setTypeface(KenbieApplication.S_NORMAL);
        addCasting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.startAddingCasting(null, 1, false);
            }
        });

        TextView appliedCastingTxt = (TextView) view.findViewById(R.id.applied_casting_txt);
        String applied = mActivity.mPref.getString("103", "Applied");
        if (applied.length() > 15)
            appliedCastingTxt.setText(mActivity.mPref.getString("103", "Applied").substring(0, 15) + "...");
        else
            appliedCastingTxt.setText(mActivity.mPref.getString("103", "Applied"));

        appliedCastingTxt.setTypeface(KenbieApplication.S_NORMAL);
        appliedCastingTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.launchCasting(2);
            }
        });

        TextView castingAroundDiv = (TextView) view.findViewById(R.id.casting_around_div);

        TextView castingAroundTxt = (TextView) view.findViewById(R.id.casting_around_txt);
//        castingAroundTxt.setText("+ " + mActivity.mPref.getString("103", "Applied"));
        castingAroundTxt.setTypeface(KenbieApplication.S_SEMI_BOLD);

        if (type == 1) { // Casting
            castingOptions.setVisibility(View.VISIBLE);
            castingTitle.setText(mActivity.mPref.getString("101", "CASTING"));
            backBtn.setVisibility(View.GONE);
        } else if (type == 2 || type == 3) { //2 - applied, 3- my casting
            castingOptions.setVisibility(View.GONE);
        }

        noText = (TextView) view.findViewById(R.id.casting_no_txt);
        noText.setText(mActivity.mPref.getString("56", "You've reached the end of the list"));
        noText.setTypeface(KenbieApplication.S_NORMAL);

        mProgressBar = view.findViewById(R.id.progressBar);

        castingRV = (RecyclerView) view.findViewById(R.id.casting_rv);
        castingRV.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false);
        castingRV.setLayoutManager(layoutManager);

        castingRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (layoutManager.findLastCompletelyVisibleItemPosition() > 0)
                    castingLastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();

                if ((type == 1 || type == 3) && !isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - 8)
                            && firstVisibleItemPosition >= 0) {
                        castingCurrentPage++;
                        gettingCastingUserDetails();
                    }
                }
            }
        });

        if (type == 3) {
            castingAroundTxt.setVisibility(View.VISIBLE);
            castingAroundDiv.setVisibility(View.VISIBLE);
            castingAroundTxt.setText(mActivity.mPref.getString("158", "VIEW INTERESTED MODELS"));
            castingAroundTxt.setBackgroundColor(mActivity.getResources().getColor(R.color.red_g_color));
            castingAroundTxt.setTextColor(mActivity.getResources().getColor(R.color.white));
            castingAroundTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InterestedModelsFragment interestedModelsFragment = new InterestedModelsFragment();
                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("CastingDetails", castingUserArrayList.get(pos));
                    bundle.putInt("Type", type);
                    interestedModelsFragment.setArguments(bundle);
                    mActivity.replaceFragment(interestedModelsFragment, true, false);
                }
            });
        } else {
            castingAroundTxt.setVisibility(View.GONE);
            castingAroundDiv.setVisibility(View.GONE);
        }

//        if (castingUserArrayList == null)
//            gettingCastingUserDetails();
//        else if (castingUserArrayList.size() == 0)
//            gettingCastingUserDetails();
//        else if (type == 1 && castingLastVisiblePosition < castingUserArrayList.size())
//            refreshDataOnUi();
//        else
        gettingCastingUserDetails();
    }

    private void refreshDataOnUi() {
        try {
            if (castingLastVisiblePosition < castingUserArrayList.size()) {
                castingRV.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false);
                castingRV.setLayoutManager(layoutManager);
                castingAdapter = new CastingUserNewAdapter(mActivity, castingUserArrayList, this, 1, mActivity.mPref, isLastPage);
                castingRV.setAdapter(castingAdapter);
                if (castingLastVisiblePosition > 0)
                    castingRV.scrollToPosition(castingLastVisiblePosition);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // data bind
    private void refreshData() {
        if (castingUserArrayList == null || (castingUserArrayList != null && castingUserArrayList.size() == 0)) {
            noDataTxt.setVisibility(View.VISIBLE);
            castingRV.setVisibility(View.GONE);
        } else {
            noDataTxt.setVisibility(View.GONE);
            castingRV.setVisibility(View.VISIBLE);
            if (type == 1) {
                if (castingCurrentPage == 1 || castingAdapter == null) {
                    castingAdapter = new CastingUserNewAdapter(mActivity, castingUserArrayList, this, 1, mActivity.mPref, isLastPage);
                    castingRV.setAdapter(castingAdapter);
                } else
                    castingAdapter.refreshData(castingUserArrayList, isLastPage);
            } else if (type == 2) {
                castingAdapter = new CastingUserNewAdapter(mActivity, castingUserArrayList, this, 1, mActivity.mPref, isLastPage);
                castingRV.setAdapter(castingAdapter);
            } else {
                if (castingCurrentPage == 1) {
                    userAdapter = new CastingUserAdapter(mActivity, castingUserArrayList, this, type, mActivity.mPref, isLastPage);
                    castingRV.setAdapter(userAdapter);
                } else
                    userAdapter.refreshData(castingUserArrayList, isLastPage, type);
            }
        }
    }

    private void gettingCastingUserDetails() {
        if (mActivity.isOnline()) {
            isLoading = true;
            if (castingCurrentPage == 1)
                mySwipeRefreshLayout.setRefreshing(true);
            else
                mProgressBar.setVisibility(View.VISIBLE);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            if (type == 3)
                mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "myCasting/page/" + castingCurrentPage, this, params, 101);
            else if (type == 2)
                mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "viewAppliedCastings", this, params, 101);
            else
                mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "viewAllCastings/page/" + castingCurrentPage, this, params, 101);
        } else {
            isLoading = false;
            mySwipeRefreshLayout.setRefreshing(false);
            mProgressBar.setVisibility(View.GONE);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void deleteCasting() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("casting_id", castingUserArrayList.get(selPos).getId() + "");
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "deleteCasting", this, params, 102);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    @Override
    public void getError(String error, int APICode) {
        if (APICode == 101) {
            if (castingCurrentPage == 1)
                refreshData();
            else {
                isLastPage = true;
                refreshData();
            }
        } else if (error != null)
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), error);
        else
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));

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
                        if (castingCurrentPage == 1)
                            castingUserArrayList = getCastUserDetails(jo.getString("data"));
                        else if (castingUserArrayList != null) {
                            ArrayList<CastingUser> tempData = getCastUserDetails(jo.getString("data"));
                            if (tempData != null && tempData.size() > 0)
                                castingUserArrayList.addAll(tempData);
                            else {
                                isLastPage = true;
                                isLoading = false;
                            }
                        }

                        if (castingUserArrayList == null)
                            castingUserArrayList = new ArrayList<>();
                        if (!isLastPage) {
                            isLoading = false;
                            isLastPage = false;
                        }

                        mySwipeRefreshLayout.setRefreshing(false);
                        mProgressBar.setVisibility(View.GONE);

                        refreshData();
                    } else if (jo.has("error"))
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("error"));
                    else
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                } else if (APICode == 102) {
                    userAdapter.castingUsers.remove(selPos);
                    userAdapter.notifyItemRemoved(selPos);
                    userAdapter.notifyItemRangeChanged(selPos, userAdapter.getItemCount());
                    mActivity.showProgressDialog(false);
                } else if (APICode == 103) {
                    JSONObject jData = new JSONObject(jo.getString("data"));
                    if (jData.has("pay_ref_id")) {
                        payRefId = jData.getString("pay_ref_id");
                        startPaymentProcess(3);
                    }
                } else if (APICode == 104) { // Payment success
//                    {"status":true,"data":"","success":"celeb added successfully"}
                    if (jo.has("success"))
                        mActivity.showToast(mActivity, jo.getString("success"));
                    else
                        mActivity.showToast(mActivity, "Payment Successful!");

                    mActivity.hideKeyboard(mActivity);
                    try {
                        castingUserArrayList.get(selPos).setIs_paid(1);
                        refreshData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        mActivity.launchCasting(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        isLoading = false;
        mySwipeRefreshLayout.setRefreshing(false);
        mActivity.showProgressDialog(false);
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
                .setPositiveButton(mActivity.mPref.getString("21", "Yes"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCasting();
                        dialog.dismiss();
                    }
                }).setNegativeButton(mActivity.mPref.getString("22", "Cancel"), new DialogInterface.OnClickListener() {
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
//                    value.setCasting_gender(id == 1 ? mActivity.mPref.getString("31", "Male") : mActivity.mPref.getString("32", "Female"));

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
        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        isLoading = false;
        mActivity.showProgressDialog(false);
    }


    @Override
    public void onResume() {
        if (type == 1)
            mActivity.updateActionBar(6, mActivity.mPref.getString("101", "CASTING"), false, false, true);
        else if (type == 2)
            mActivity.updateActionBar(66, mActivity.mPref.getString("312", "APPLIED CASTING"), false, true, false);
        else
            mActivity.updateActionBar(66, mActivity.mPref.getString("157", "MY CASTING"), false, true, false);
        mActivity.hideKeyboard(mActivity);
//        if (castingCurrentPage != 1)
//            onRefresh();
        super.onResume();
    }

    @Override
    public void getUserDetails(int pos, int type) {
        try {
            if (type == 1) {
                Intent intent = new Intent(mActivity, KenbieActivity.class);
                intent.putExtra("NavType", 12);
                intent.putExtra("CastingDetails", castingUserArrayList.get(pos));
                startActivity(intent);
            } else if (type == 3) {
                CastingDetailsFragment castingDetailsFragment = new CastingDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("CastingDetails", castingUserArrayList.get(pos));
                bundle.putInt("Type", type);
                castingDetailsFragment.setArguments(bundle);
                mActivity.replaceFragment(castingDetailsFragment, true, false);
            } else if (type == 4) { // View interested models
                InterestedModelsFragment interestedModelsFragment = new InterestedModelsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("CastingDetails", castingUserArrayList.get(pos));
                bundle.putInt("Type", type);
                interestedModelsFragment.setArguments(bundle);
                mActivity.replaceFragment(interestedModelsFragment, true, false);
            } else if (type == 5) { // Casting Pay Now
                selPos = pos;
                castingUser = castingUserArrayList.get(selPos);
                getCastingRefNumber();

//                mActivity.startAddingCasting(castingUserArrayList.get(pos), 1, true);
            } else if (type == 6) { // Edit Casting
                selPos = pos;
                initEditCastData(castingUserArrayList.get(pos));
                mActivity.replaceFragment(new EditCastingDetailsFragment(), true, false);
            } else if (type == 7) { // Delete Casting
                selPos = pos;
                showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("358", "Are you sure you want to delete?"), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startPaymentProcess(int subType) {
        try {
            int amount = 0;
            if (subType == 1) { // Membership 149.99
                amount = mActivity.mPref.getInt("SubsPayment", 14999);
            } else if (subType == 2) { // Casting -  Casting t=cast&rid=<Casting_id> - 4.99
                amount = mActivity.mPref.getInt("CelebPayment", 499);
            } else { // Add Celeb - 4.99
                amount = mActivity.mPref.getInt("CastPayment", 499);
            }

            Payment payment = new Payment(Constants.MERCHANT_ID, payRefId, Constants.CURRENCY_CODE, amount, Constants.SIGNATURE);
            DisplayContext dc = new DisplayContext(new ResourceProvider(), mActivity);

            List<PaymentMethod> methods = new ArrayList<>();
            methods.add(new PaymentMethod(PaymentMethodType.VISA));
            methods.add(new PaymentMethod(PaymentMethodType.MASTERCARD));
            methods.add(new PaymentMethod(PaymentMethodType.AMEX));
            methods.add(new PaymentMethod(PaymentMethodType.PAYPAL));
            methods.add(new PaymentMethod(PaymentMethodType.DINERS));
            methods.add(new PaymentMethod(PaymentMethodType.DISCOVER));
            methods.add(new PaymentMethod(PaymentMethodType.ELV));

            PaymentProcessAndroid ppa = new PaymentProcessAndroid(dc, payment, methods);

            String language = mActivity.mPref.getString("UserSavedLangCode", "en").toUpperCase();
            Locale locale = new Locale(language);
//            Resources (SDK dialogs):

            Resources resources = mActivity.getBaseContext().getResources();
            Configuration configuration = resources.getConfiguration();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLocale(locale);
            } else {
                configuration.locale = locale;
            }

            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
//            Locale (SDK string representations + web):
            Locale.setDefault(locale);

            ppa.getPaymentOptions().setAppCallbackScheme("com.kenbie.dtpl");
            ppa.getPaymentOptions().setAutoSettlement(true);
            ppa.getPaymentOptions().setRecurringPayment(true);
//            ppa.getPaymentOptions().getMerchantProperties().putAll(merchantProperties);

            ppa.setTestingEnabled(PAYMENT_TEST_MODE);
            ppa.addStateListener(this);
            ppa.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initEditCastData(CastingUser castingUser) {
        try {
            mActivity.profilePicBitmap = null;
            mActivity.castingParams = new HashMap<String, String>();
            mActivity.castingParams.put("user_id", mActivity.mPref.getString("UserId", ""));
            mActivity.castingParams.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            mActivity.castingParams.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            mActivity.castingParams.put("casting_title", castingUser.getCasting_title());
            mActivity.castingParams.put("requirements", castingUser.getCasting_requirement());
            mActivity.castingParams.put("casting_id", castingUser.getId() + "");

            mActivity.castingParams.put("casting_face", castingUser.getCasting_type() + "");
            mActivity.castingParams.put("casting_location", castingUser.getCasting_location());
            mActivity.castingParams.put("casting_from_age", castingUser.getCasting_start_age());
            mActivity.castingParams.put("casting_to_age", castingUser.getCasting_end_age());
            mActivity.castingParams.put("casting_from_time", castingUser.getCasting_start_time());
            mActivity.castingParams.put("casting_to_time", castingUser.getCasting_end_time());
            mActivity.castingParams.put("casting_fees", castingUser.getCasting_fee() + "");
            mActivity.castingParams.put("casting_gender", castingUser.getCasting_gender());

            String startDate = castingUser.getCasting_start_date();
            if (startDate != null && startDate.length() > 2) {
                String[] sDate = startDate.split("-"); // year + "-" + (month + 1) + "-" + day
                mActivity.castingParams.put("starting_date", sDate[0]);
                mActivity.castingParams.put("starting_month", sDate[1]);
                mActivity.castingParams.put("starting_year", sDate[2]);
            }

            String endDate = castingUser.getCasting_end_date();
            if (endDate != null && endDate.length() > 2) {
                String[] eDate = endDate.split("-"); // year + "-" + (month + 1) + "-" + day
                mActivity.castingParams.put("end_date", eDate[0]);
                mActivity.castingParams.put("end_month", eDate[1]);
                mActivity.castingParams.put("end_year", eDate[2]);
            }

            mActivity.castingParams.put("casting_address", castingUser.getCasting_address());
            mActivity.castingParams.put("casting_country", castingUser.getCountry());
            mActivity.castingParams.put("casting_categories", castingUser.getCasting_categories());
            mActivity.castingParams.put("casting_img", castingUser.getCasting_img());
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
            if (castingUserArrayList != null && castingUserArrayList.size() > 2) {
                Random r = new Random();
                values.add(castingUserArrayList.get(r.nextInt(castingUserArrayList.size())));
                values.add(castingUserArrayList.get(r.nextInt(castingUserArrayList.size())));
            } else
                values = castingUserArrayList;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    @Override
    public void onRefresh() {
        castingCurrentPage = 1;
        isLastPage = false;
        if (!isLoading) {
            gettingCastingUserDetails();
        }
    }

    @Override
    public void paymentProcessStateChanged(PaymentProcessAndroid process) {
        switch (process.getState()) {
            case COMPLETED:
                try {
                    mActivity.hideKeyboard(mActivity);

                    AliasPaymentMethod aliasPaymentMethod = process.getAliasPaymentMethod();
                    if (aliasPaymentMethod != null) {
                        // serialize and securely store pm for reuse
                        String paymentRefNo = aliasPaymentMethod.getAlias();
//                    String paymentRefNo = aliasPaymentMethod.getType().

                        if (aliasPaymentMethod instanceof AliasPaymentMethodCreditCard) {
                            AliasPaymentMethodCreditCard aliasPaymentMethodCreditCard = (AliasPaymentMethodCreditCard) aliasPaymentMethod;
//                        String paymentRefNo = aliasPaymentMethodCreditCard.getAlias();
                            JSONObject jo = new JSONObject();
                            jo.put("type", aliasPaymentMethodCreditCard.getType().getName());
                            jo.put("alias", paymentRefNo);
                            jo.put("paymentRefNo", payRefId);
                            jo.put("maskedCC", aliasPaymentMethodCreditCard.getMaskedCardNumber());
                            jo.put("expY", aliasPaymentMethodCreditCard.getExpiryDateYear() + "");
                            jo.put("expM", aliasPaymentMethodCreditCard.getExpiryDateMonth() + "");
                            jo.put("holder", aliasPaymentMethodCreditCard.getCardHolder());
                            jo.put("ip", mActivity.ip);
                            saveTransactionDetailsOnServer(jo.toString(), paymentRefNo, 1);
//                            String cardHolder = aliasPaymentMethodCreditCard.getCardHolder();
//                            if (cardHolder != null && !cardHolder.isEmpty()) {
//
//                                String firstname = cardHolder.split(" ")[0].replace("+", " ");
//                                String lastname = cardHolder.substring(firstname.length() + 1).replace("+", " ");
////                            successMessage.append("\nfirstname=" + firstname + ", lastname=" + lastname);
//                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CANCELED:
                // ignore, abort checkout, whatever...
                saveTransactionDetailsOnServer("", "", 2);
                break;
            case ERROR:
                Exception e = process.getException();
                if (e instanceof BusinessException) {
                    BusinessException be = (BusinessException) e;
                    int errorCode = be.getErrorCode(); // Datatrans error code if needed
                    // display some error message
                    saveTransactionDetailsOnServer("", "", 2);
//                mActivity.onBackPressed();
                } else {
// unexpected technical exception, either fatal TechnicalException or
// javax.net.ssl.SSLException (certificate error)
                    mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), "Something wrong in Payment. Please try again.");
                    mActivity.onBackPressed();
                }
                break;
        }
    }

    private void saveTransactionDetailsOnServer(String payResponse, String refNO, int type) {
        if (mActivity.isOnline()) {
//            mActivity.showProgressDialog(true);
//            Param: @"user_id=%@&login_key=%@&login_token=%@&device_id=%@&android_token=%@&casting_id=%@&action=%@&pay_response=%@&ip=%@"
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("casting_id", castingUser.getId() + "");
            params.put("pay_response", payResponse);
            params.put("pay_ref_id", payRefId);
            params.put("action", type == 1 ? "success" : "error");
            params.put("ip", mActivity.ip);
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "paycastUpdate", this, params, 104);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void getCastingRefNumber() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("ip", mActivity.ip);
            params.put("casting_id", castingUser != null ? castingUser.getId() + "" : "");
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "paycastRefNo", this, params, 103);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }
}
