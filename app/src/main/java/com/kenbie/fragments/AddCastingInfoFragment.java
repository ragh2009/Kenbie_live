package com.kenbie.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kenbie.KenbieApplication;
import com.kenbie.KenbieNavigationActivity;
import com.kenbie.KenbieWebActivity;
import com.kenbie.R;
import com.kenbie.adapters.InfoWithCheckAdapter;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.model.CastingUser;
import com.kenbie.model.ProfileInfo;
import com.kenbie.model.UserItem;
import com.kenbie.util.Constants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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


public class AddCastingInfoFragment extends BaseFragment implements APIResponseHandler, IPaymentProcessStateListener {
    private String[] infoOptionsArray = {"Discover new and unknown talents", "Gather inspiration for your next shoot", "Reach many participants with little effort", "Shorten price negotiations", "Save costs", "Find the perfect model", "Increase your popularity at Kenbie"};
    //    private String[] infoOptionsArray = {mActivity.mPref.getString("123", "Discover new and unknown talents"), mActivity.mPref.getString("124", "Gather inspiration for your next shoot"), mActivity.mPref.getString("125", "Reach many paticipants with little effort"),  mActivity.mPref.getString("126", "Shorten price negotiations"), mActivity.mPref.getString("129", "Save costs"), mActivity.mPref.getString("128", "Find the perfect model"), mActivity.mPref.getString("127", "Increase your popularity at Kenbie")};
    private UserItem userItem;
    private ProfileInfo profileInfo;
    private int subType;
    private RecyclerView rvCastingInfo;
    private CheckBox celeAgree, celePrivacy;
    private boolean payNow;
    private CastingUser castingUser;
    private String payRefId = "";

    public AddCastingInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            userItem = (UserItem) getArguments().getSerializable("UserInfo");
            castingUser = (CastingUser) getArguments().getSerializable("CastingUser");
            subType = getArguments().getInt("Type");
            payNow = getArguments().getBoolean("PaymentNow", false);
        }

        infoOptionsArray[0] = mActivity.mPref.getString("123", "Discover new and unknown talents");
        infoOptionsArray[1] = mActivity.mPref.getString("124", "Gather inspiration for your next shoot");
        infoOptionsArray[2] = mActivity.mPref.getString("125", "Reach many participants with little effort");
        infoOptionsArray[3] = mActivity.mPref.getString("126", "Shorten price negotiations");
        infoOptionsArray[4] = mActivity.mPref.getString("129", "Save costs");
        infoOptionsArray[5] = mActivity.mPref.getString("128", "Find the perfect model");
        infoOptionsArray[6] = mActivity.mPref.getString("127", "Increase your popularity at Kenbie");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_casting_info, container, false);
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(29, mActivity.mPref.getString("116", "CASTING FEATURES"), false, true, false);
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView mTitle1Txt = view.findViewById(R.id.m_title1_txt);
        mTitle1Txt.setText(mActivity.mPref.getString("117", getString(R.string.casting_area_title)));
        mTitle1Txt.setTypeface(KenbieApplication.S_SEMI_BOLD);

        TextView mTitle2Txt = view.findViewById(R.id.m_title2_txt);
        mTitle2Txt.setText(mActivity.mPref.getString("118", getString(R.string.casting_info2_title)));
        mTitle2Txt.setTypeface(KenbieApplication.S_SEMI_BOLD);

        TextView mTitle3Txt = view.findViewById(R.id.m_title3_txt);
        mTitle3Txt.setText(mActivity.mPref.getString("119", getString(R.string.cel_info3_title)));
        mTitle3Txt.setTypeface(KenbieApplication.S_NORMAL);

        TextView mTitle4Txt = view.findViewById(R.id.m_title4_txt);
        mTitle4Txt.setText(mActivity.mPref.getString("120", getString(R.string.casting_info4_title)));
        mTitle4Txt.setTypeface(KenbieApplication.S_BOLD);

        TextView mTitle5Txt = view.findViewById(R.id.m_title5_txt);
        mTitle5Txt.setText(mActivity.mPref.getString("121", getString(R.string.casting_info5_title)));
        mTitle5Txt.setTypeface(KenbieApplication.S_NORMAL);

        TextView mTitle6Txt = view.findViewById(R.id.m_title6_txt);
        mTitle6Txt.setText(mActivity.mPref.getString("122", getString(R.string.casting_info6_title)));
        mTitle6Txt.setTypeface(KenbieApplication.S_SEMI_BOLD);

        rvCastingInfo = view.findViewById(R.id.rv_casting_info);
        // binding information
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false);
        rvCastingInfo.setLayoutManager(linearLayoutManager);
        InfoWithCheckAdapter infoWithCheckAdapter = new InfoWithCheckAdapter(mActivity, infoOptionsArray);
        rvCastingInfo.setAdapter(infoWithCheckAdapter);

        celeAgree = view.findViewById(R.id.cele_agree);
        celeAgree.setTypeface(KenbieApplication.S_SEMI_LIGHT);
        celeAgree.setText(mActivity.mPref.getString("345", "I am agree to pay 4.99€"));
        celePrivacy = view.findViewById(R.id.cele_privacy);
        celePrivacy.setTypeface(KenbieApplication.S_SEMI_LIGHT);
        celePrivacy.setText(mActivity.mPref.getString("132", "I have read the"));

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
                    if (payNow)
                        startPaymentProcess(subType);
                    else {
                        Intent intent = new Intent(mActivity, KenbieNavigationActivity.class);
                        intent.putExtra("NavType", 17);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        mActivity.finish();
//                        mActivity.replaceFragment(new EditCastingDetailsFragment(), true, false);
                    }
                else {
                    mActivity.showProgressDialog(false);
                    mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
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

        if (payNow)
            getCastingRefNumber();
    }

    private void getCastingRefNumber() {
        if (mActivity.isOnline()) {
//            mActivity.showProgressDialog(true);
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
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "paycastUpdate", this, params, 102);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    @Override
    public void getError(String error, int APICode) {
        if (error != null)
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), error);
        else
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));

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
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("error"));
                    else
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                } else if (APICode == 102) { // Payment success
//                    {"status":true,"data":"","success":"celeb added successfully"}
                    if (jo.has("success"))
                        mActivity.showToast(mActivity, jo.getString("success"));
                    else
                        mActivity.showToast(mActivity, "Payment Successful!");
                    mActivity.hideKeyboard(mActivity);
                    mActivity.launchCasting(1);
//                    Intent i = new Intent(mActivity, KenbieActivity.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    mActivity.startActivity(i);
                } else if (APICode == 103) { // Casting payment reference
                    JSONObject jData = new JSONObject(jo.getString("data"));
                    if (jData.has("pay_ref_id"))
                        payRefId = jData.getString("pay_ref_id");
                }
            } else
                mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));

        } catch (Exception e) {
            e.printStackTrace();
        }

        mActivity.showProgressDialog(false);
    }

    @Override
    public void networkError(String error, int APICode) {
        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        mActivity.showProgressDialog(false);
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

    private void getUserProfileDetails() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("profile_user_id", userItem.getId() + "");
            new MConnection().postRequestWithHttpHeaders(mActivity, "getProfileDetail", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
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
}
