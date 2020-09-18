package com.kenbie.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.KenbieActivity;
import com.kenbie.KenbieApplication;
import com.kenbie.KenbieWebActivity;
import com.kenbie.R;
import com.kenbie.adapters.CelebritiesAdapter;
import com.kenbie.adapters.InfoWithCheckAdapter;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.listeners.InfoListener;
import com.kenbie.model.ProfileInfo;
import com.kenbie.model.UserItem;
import com.kenbie.util.Constants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import ch.datatrans.payment.AliasPaymentMethod;
import ch.datatrans.payment.AliasPaymentMethodCreditCard;
import ch.datatrans.payment.BusinessException;
import ch.datatrans.payment.DisplayContext;
import ch.datatrans.payment.IPaymentProcessStateListener;
import ch.datatrans.payment.Payment;
import ch.datatrans.payment.PaymentMethod;
import ch.datatrans.payment.PaymentMethodType;
import ch.datatrans.payment.PaymentProcessAndroid;
import ch.datatrans.payment.ResourceProvider;


import static com.kenbie.util.Constants.PAYMENT_TEST_MODE;


public class CelebrityInfoFragment extends BaseFragment implements APIResponseHandler, InfoListener, IPaymentProcessStateListener {
    private String[] celebrityOptionsArray = {"Be listed at the very top as a celeb at Kenbie.", "Attract the attention of agents, photographers, models, and clients right away.", "Increase the number of your profile visitors", "Accelerate your career and get more exciting jobs.", "Generate more attention and benefit from more frequent bookings."};
    //    private String[] celebrityOptionsArray = {mActivity.mPref.getString("203", "Be listed at the very top as a celeb at Kenbie"), mActivity.mPref.getString("204", "Attract the attention of the agents, photographers, models, and clients right away."), mActivity.mPref.getString("205", "Increase the number of your profile visitors."), mActivity.mPref.getString("206", "Accelerate your career and get more exciting jobs."), mActivity.mPref.getString("207", "Generate more attention and benefits from more frequent bookings.")};
    private UserItem userItem;
    private ProfileInfo profileInfo;
    private int subType;
    private RecyclerView celebritiesRV, rvCelebrityInfo;
    private CheckBox celeAgree, celePrivacy;
    public ArrayList<UserItem> celebritiesData = null, randomCelebrities = null;
    private String payRefId = "";

    public CelebrityInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userItem = (UserItem) getArguments().getSerializable("UserInfo");
            subType = getArguments().getInt("Type");
            try {
                celebritiesData = (ArrayList<UserItem>) getArguments().getSerializable("CelebrityData");
                if (celebritiesData.size() > 1)
                    celebritiesData.remove(0);
                randomCelebrities = (ArrayList<UserItem>) getArguments().getSerializable("RandomCelebrityData");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        celebrityOptionsArray[0] = mActivity.mPref.getString("203", "Be listed at the very top as a celeb at Kenbie");
        celebrityOptionsArray[1] = mActivity.mPref.getString("204", "Attract the attention of the agents, photographers, models, and clients right away.");
        celebrityOptionsArray[2] = mActivity.mPref.getString("205", "Increase the number of your profile visitors.");
        celebrityOptionsArray[3] = mActivity.mPref.getString("206", "Accelerate your career and get more exciting jobs.");
        celebrityOptionsArray[4] = mActivity.mPref.getString("207", "Generate more attention and benefits from more frequent bookings.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_celebrity_info, container, false);
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(11, mActivity.mPref.getString("197", "CELEBRITY AREA"), false, true, false);
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        celebritiesRV = (RecyclerView) view.findViewById(R.id.rv_celebrities);
        bindCelebritiesData();

        TextView mTitle1Txt = view.findViewById(R.id.m_title1_txt);
        mTitle1Txt.setText(mActivity.mPref.getString("197", getString(R.string.cel_area_title)));
        mTitle1Txt.setTypeface(KenbieApplication.S_SEMI_BOLD);

        ImageView cel1 = view.findViewById(R.id.cel_1);
        ImageView cel2 = view.findViewById(R.id.cel_2);
        ImageView cel3 = view.findViewById(R.id.cel_3);
        ImageView cel4 = view.findViewById(R.id.cel_4);
        ImageView cel5 = view.findViewById(R.id.cel_5);
        if (randomCelebrities != null && randomCelebrities.size() > 4) {
            Glide.with(mActivity).load(randomCelebrities.get(0).getUserPic()).apply(RequestOptions.circleCropTransform()).into(cel1);
            Glide.with(mActivity).load(randomCelebrities.get(1).getUserPic()).apply(RequestOptions.circleCropTransform()).into(cel2);
            Glide.with(mActivity).load(randomCelebrities.get(2).getUserPic()).apply(RequestOptions.circleCropTransform()).into(cel3);
            Glide.with(mActivity).load(randomCelebrities.get(3).getUserPic()).apply(RequestOptions.circleCropTransform()).into(cel4);
            Glide.with(mActivity).load(randomCelebrities.get(4).getUserPic()).apply(RequestOptions.circleCropTransform()).into(cel5);
        }

        TextView mTitle2Txt = view.findViewById(R.id.m_title2_txt);
        mTitle2Txt.setText(mActivity.mPref.getString("198", getString(R.string.cel_info2_title)));
        mTitle2Txt.setTypeface(KenbieApplication.S_SEMI_BOLD);

        TextView mTitle3Txt = view.findViewById(R.id.m_title3_txt);
        mTitle3Txt.setText(mActivity.mPref.getString("199", getString(R.string.cel_info3_title)));
        mTitle3Txt.setTypeface(KenbieApplication.S_NORMAL);

        TextView mTitle4Txt = view.findViewById(R.id.m_title4_txt);
        mTitle4Txt.setText(mActivity.mPref.getString("200", getString(R.string.cel_info4_title)));
        mTitle4Txt.setTypeface(KenbieApplication.S_BOLD);

        TextView mTitle5Txt = view.findViewById(R.id.m_title5_txt);
        mTitle5Txt.setText(mActivity.mPref.getString("201", getString(R.string.cel_info5_title)));
        mTitle5Txt.setTypeface(KenbieApplication.S_NORMAL);

        TextView mTitle6Txt = view.findViewById(R.id.m_title6_txt);
        mTitle6Txt.setText(mActivity.mPref.getString("202", getString(R.string.how_title)));
        mTitle6Txt.setTypeface(KenbieApplication.S_NORMAL);

        rvCelebrityInfo = view.findViewById(R.id.rv_celebrity_info);
        // binding information
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false);
        rvCelebrityInfo.setLayoutManager(linearLayoutManager);
        InfoWithCheckAdapter infoWithCheckAdapter = new InfoWithCheckAdapter(mActivity, celebrityOptionsArray);
        rvCelebrityInfo.setAdapter(infoWithCheckAdapter);
        celeAgree = view.findViewById(R.id.cele_agree);
        celeAgree.setTypeface(KenbieApplication.S_SEMI_LIGHT);
        celeAgree.setText(Html.fromHtml(mActivity.mPref.getString("345", "I am agree to pay 4.99€")));
        celePrivacy = view.findViewById(R.id.cele_privacy);
        celePrivacy.setText(mActivity.mPref.getString("132", "I have read the"));
        celePrivacy.setTypeface(KenbieApplication.S_SEMI_LIGHT);

        TextView btnBuyNow = view.findViewById(R.id.btn_buy_now);
        btnBuyNow.setText(mActivity.mPref.getString("130", "BUY NOW"));
        btnBuyNow.setTypeface(KenbieApplication.S_BOLD);
        btnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!celeAgree.isChecked())
                    mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("273", "Please accept the field to further payment process"));
                else if (!celePrivacy.isChecked())
                    mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("274", "Please accept privacy & terms"));
                else if (mActivity.isOnline())
                    startPaymentProcess(subType);
//                    startOptionsPaymentProcess(subType);
//                else if (mActivity.mPref.getBoolean("HasAddress", false))
//                    getUserProfileDetails();
                else {
                    mActivity.showProgressDialog(false);
                    mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
                }
            }
        });

        TextView celePPAction = view.findViewById(R.id.cele_pp_action);
        celePPAction.setText(mActivity.mPref.getString("154", "Privacy"));
        celePPAction.setTypeface(KenbieApplication.S_SEMI_LIGHT);
        celePPAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pIntent = new Intent(mActivity, KenbieWebActivity.class);
                pIntent.putExtra("Type", 1);
                pIntent.putExtra("URL", Constants.PRIVACY_URL);
                pIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(pIntent);
            }
        });

        TextView endTitle = view.findViewById(R.id.cele_end);
        endTitle.setTypeface(KenbieApplication.S_SEMI_LIGHT);

        TextView celeTermsAction = view.findViewById(R.id.cele_terms_action);
        celeTermsAction.setText(mActivity.mPref.getString("347", "Terms"));
        celeTermsAction.setTypeface(KenbieApplication.S_SEMI_LIGHT);
        celeTermsAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, KenbieWebActivity.class);
                intent.putExtra("Type", 2);
                intent.putExtra("URL", Constants.TERMS_URL);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        TextView celeTerms2Title = view.findViewById(R.id.cele_terms2_title);
        celeTerms2Title.setText(mActivity.mPref.getString("133", "for online purchase and I agree with them"));
        celeTerms2Title.setTypeface(KenbieApplication.S_SEMI_LIGHT);

        if (subType == 3)
            getCelebritiesRefNumber();
    }

    private void getCelebritiesRefNumber() {
        if (mActivity.isOnline()) {
//            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("ip", mActivity.ip);
            new MConnection().postRequestWithHttpHeaders(mActivity, "payceleb", this, params, 102);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void saveTransactionDetailsOnServer(String payResponse, String paymentRefNo, int type) {
        if (mActivity.isOnline()) {
//            mActivity.showProgressDialog(true);
//            @“user_id=%@&login_key=%@&login_token=%@&device_id=%@&android_token=%@&pay_ref_id=%@&action=%@&pay_response=%@&ip=%@"
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("pay_ref_id", payRefId);
            params.put("pay_response", payResponse);
            params.put("action", type == 1 ? "success" : "error");
            params.put("ip", mActivity.ip);
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "paycelebUpdate", this, params, 103);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
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

    // Bind celebrities data on UI
    private void bindCelebritiesData() {
        try {
//            celebritiesRV.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false);
            celebritiesRV.setLayoutManager(linearLayoutManager);
            CelebritiesAdapter celebritiesAdapter = new CelebritiesAdapter(mActivity, celebritiesData, this, mActivity.mPref.getString("UserId", "0"), null);
            celebritiesRV.setAdapter(celebritiesAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUserProfileDetails() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("profile_user_id", userItem.getId() + "");
            new MConnection().postRequestWithHttpHeaders(getActivity(), "getProfileDetail", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
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

                if (APICode == 101) { // profile details
                    if (jo.has("status") && jo.getBoolean("status")) {
                        profileInfo = parseUserProfileData(jo.getString("data"));
                        mActivity.editBasicInfo(profileInfo, 1);
                    } else if (jo.has("error"))
                        mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), jo.getString("error"));
                    else
                        mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                } else if (APICode == 102) { // Get celebrities ref no
                    JSONObject jData = new JSONObject(jo.getString("data"));
                    if (jData.has("pay_ref_id"))
                        payRefId = jData.getString("pay_ref_id");
                } else if (APICode == 103) { // Payment success
//                    {"status":true,"data":"","success":"celeb added successfully"}
//                    if (jo.has("success"))
//                        mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), jo.getString("success"));
//                    else
//                        mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), "Payment Successful!");
                    mActivity.hideKeyboard(mActivity);
                    Intent i = new Intent(mActivity, KenbieActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mActivity.startActivity(i);
                }
            } else
                mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));

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

    // Profile info
    private ProfileInfo parseUserProfileData(String data) {
        ProfileInfo profileInfo = new ProfileInfo();
        try {
            JSONObject jo = new JSONObject(data);
            if (jo.has("profile_info")) {
                JSONObject jp = new JSONObject(jo.getString("profile_info"));
                profileInfo.setId(jp.getInt("id"));
                profileInfo.setFirst_name(jp.getString("first_name"));
                profileInfo.setEmail_id(jp.getString("email_id"));
                if (jp.getString("birth_year") != null && !jp.getString("birth_year").equalsIgnoreCase("null"))
                    profileInfo.setBirth_year(jp.getString("birth_year"));

                if (jp.getString("birth_month") != null && !jp.getString("birth_month").equalsIgnoreCase("null"))
                    profileInfo.setBirth_month(jp.getString("birth_month"));

                if (jp.getString("birth_day") != null && !jp.getString("birth_day").equalsIgnoreCase("null"))
                    profileInfo.setBirth_day(jp.getString("birth_day"));

                profileInfo.setCompany_name(jp.getString("company_name"));
                profileInfo.setPhone(jp.getString("phone"));

                profileInfo.setAddress1(jp.getString("address1"));
                profileInfo.setAddress2(jp.getString("address2"));
                profileInfo.setCountry(jp.getString("country"));
                profileInfo.setCity(jp.getString("city"));
                profileInfo.setPostal_code(jp.getString("postal_code"));
                profileInfo.setUser_from_agency(jp.getString("user_from_agency"));
                profileInfo.setUser_type(jp.getString("user_type"));
                if (jp.getString("gender") != null)
                    if (jp.getString("gender").equalsIgnoreCase("Male"))
                        profileInfo.setGender(1);
                    else
                        profileInfo.setGender(2);
                profileInfo.setSocialType(jp.getString("social_type"));
                profileInfo.setSocial_id(jp.getString("social_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return profileInfo;
    }

    @Override
    public void getInfoValue(int parentPos, int childPos) {
        mActivity.viewUserProfile(celebritiesData.get(parentPos));
    }

    @Override
    public void paymentProcessStateChanged(PaymentProcessAndroid process) {
        switch (process.getState()) {
            case COMPLETED:
                try {
                    AliasPaymentMethod aliasPaymentMethod = process.getAliasPaymentMethod();
                    if (aliasPaymentMethod != null) {
                        // serialize and securely store pm for reuse
                        String paymentRefNo = aliasPaymentMethod.getAlias();
                        if (aliasPaymentMethod instanceof AliasPaymentMethodCreditCard) {
                            AliasPaymentMethodCreditCard aliasPaymentMethodCreditCard = (AliasPaymentMethodCreditCard) aliasPaymentMethod;
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
                } else {
                    mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), "Something wrong in Payment. Please try again.");
                    mActivity.onBackPressed();
                }
                break;
        }
    }
}
