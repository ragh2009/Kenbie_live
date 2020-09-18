package com.kenbie.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kenbie.KenbieActivity;
import com.kenbie.KenbieApplication;
import com.kenbie.KenbieWebActivity;
import com.kenbie.R;
import com.kenbie.adapters.InfoWithCheckAdapter;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
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


public class MemberShipInfoFragment extends BaseFragment implements APIResponseHandler, IPaymentProcessStateListener {
    private String[] infoOptionsArray = {"Become a newcomer model", "Advance your career as a professional model", "Find the perfect models for your bookings as an agency", "Choose the perfect cast for your shoot as a photographer", "Enjoy the large variety of models, agents, photographers,& more", "Make a name for yourself in the international modeling business", "Generate (more) jobs as a model", "Present yourself on a professional platform with international reach", "Contact models, photographers, or agents directly"};
    //    private String[] infoOptionsArray = {mActivity.mPref.getString("281", "Become a newcomer model"), mActivity.mPref.getString("282", "Advance your career as a professional model"), mActivity.mPref.getString("283", "Find the perfect models for your bookings as an agency"), mActivity.mPref.getString("284", "Choose the perfect cast for your shoot as a photographer"), mActivity.mPref.getString("285", "Enjoy the large variety of models, agents, photographers & more"), mActivity.mPref.getString("286", "Make a name for yourself in the international modeling business"), mActivity.mPref.getString("287", "Generate jobs as a model"), mActivity.mPref.getString("288", "Present yourself on a professional platform with international reach"), mActivity.mPref.getString("289", "Contact models, photographers, or agents directly")};
    private UserItem userItem;
    private ProfileInfo profileInfo;
    private int subType;
    private RecyclerView rvMemInfo;
    private CheckBox celeAgree, celePrivacy;
    private String payRefId;
    private NestedScrollView memInfoLayout;
    private String TAG = "PaymentLogs";

    public MemberShipInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userItem = (UserItem) getArguments().getSerializable("UserInfo");
            subType = getArguments().getInt("Type");
        }

        infoOptionsArray[0] = mActivity.mPref.getString("281", "Become a newcomer model");
        infoOptionsArray[1] = mActivity.mPref.getString("282", "Advance your career as a professional model");
        infoOptionsArray[2] = mActivity.mPref.getString("283", "Find the perfect models for your bookings as an agency");
        infoOptionsArray[3] = mActivity.mPref.getString("284", "Choose the perfect cast for your shoot as a photographer");
        infoOptionsArray[4] = mActivity.mPref.getString("285", "Enjoy the large variety of models, agents, photographers & more");
        infoOptionsArray[5] = mActivity.mPref.getString("286", "Make a name for yourself in the international modeling business");
        infoOptionsArray[6] = mActivity.mPref.getString("287", "Generate jobs as a model");
        infoOptionsArray[7] = mActivity.mPref.getString("288", "Present yourself on a professional platform with international reach");
        infoOptionsArray[8] = mActivity.mPref.getString("289", "Contact models, photographers, or agents directly");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_membebr_ship_info, container, false);
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(11, mActivity.mPref.getString("172", "MEMBERSHIP"), false, true, false);
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView mTitleTxt = view.findViewById(R.id.m_title_txt);
        mTitleTxt.setText(mActivity.mPref.getString("271", "BECOME A"));
        mTitleTxt.setTypeface(KenbieApplication.S_SEMI_BOLD);

        memInfoLayout = view.findViewById(R.id.mem_info_layout);
        TextView mTitle1Txt = view.findViewById(R.id.m_title1_txt);
        mTitle1Txt.setText(mActivity.mPref.getString("272", "MEMBER NOW"));
        mTitle1Txt.setTypeface(KenbieApplication.S_SEMI_BOLD);

        TextView mTitle2Txt = view.findViewById(R.id.m_title2_txt);
        mTitle2Txt.setText(mActivity.mPref.getString("275", "ATTRACT MORE ATTENTION WITH YOUR PROFILE RIGHT AWAY."));
        mTitle2Txt.setTypeface(KenbieApplication.S_SEMI_BOLD);

        TextView mTitle3Txt = view.findViewById(R.id.m_title3_txt);
        mTitle3Txt.setText(mActivity.mPref.getString("276", getString(R.string.mem_info3_title)));
        mTitle3Txt.setTypeface(KenbieApplication.S_NORMAL);

        TextView mTitle4Txt = view.findViewById(R.id.m_title4_txt);
        mTitle4Txt.setText(mActivity.mPref.getString("277", "PREMIUM"));
        mTitle4Txt.setTypeface(KenbieApplication.S_BOLD);

        TextView mTitle5Txt = view.findViewById(R.id.m_title5_txt);
        mTitle5Txt.setText(mActivity.mPref.getString("278", "Increase Your possibilities"));
        mTitle5Txt.setTypeface(KenbieApplication.S_NORMAL);

        TextView mPriceTxt = view.findViewById(R.id.m_price_txt);
        mPriceTxt.setTypeface(KenbieApplication.S_BOLD);
//        mPriceTxt.setText(Html.fromHtml("12.49 € p/m"));
//        mPriceTxt.setText(Html.fromHtml(mActivity.mPref.getString("374", "12.49 € p/m")));
        mPriceTxt.setText(mActivity.mPref.getString("364", "12.49 € p/m"));

        TextView mTitle6Txt = view.findViewById(R.id.m_title6_txt);
        mTitle6Txt.setText(mActivity.mPref.getString("279", "Based on annual billing"));
        mTitle6Txt.setTypeface(KenbieApplication.S_NORMAL);

        TextView mTitle7Txt = view.findViewById(R.id.m_title7_txt);
        mTitle7Txt.setText(mActivity.mPref.getString("280", getString(R.string.mem_info7_title)));
        mTitle7Txt.setTypeface(KenbieApplication.S_NORMAL);

        rvMemInfo = view.findViewById(R.id.rv_mem_info);
        // binding information
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false);
        rvMemInfo.setLayoutManager(linearLayoutManager);
        InfoWithCheckAdapter infoWithCheckAdapter = new InfoWithCheckAdapter(mActivity, infoOptionsArray);
        rvMemInfo.setAdapter(infoWithCheckAdapter);

        celeAgree = view.findViewById(R.id.cele_agree);
        celeAgree.setTypeface(KenbieApplication.S_SEMI_LIGHT);
        celeAgree.setText(Html.fromHtml(mActivity.mPref.getString("346", "I am agree to pay 149.99€/Year")));
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
                else if (mActivity.mPref.getBoolean("HasAddress", false))
                    startPaymentProcess(subType);
                else if (mActivity.isOnline())
                    getUserProfileDetails();
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

        memInfoLayout.post(new Runnable() {
            @Override
            public void run() {
                memInfoLayout.scrollTo(0, 0);
//                memInfoLayout.scrollTo(0, mTitle2Txt.getBottom());
            }
        });

        getMemberRefNumber();
    }

    private void getMemberRefNumber() {
        if (mActivity.isOnline()) {
//            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("ip", mActivity.ip);
            new MConnection().postRequestWithHttpHeaders(mActivity, "paySubscription", this, params, 102);
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
            params.put("pay_ref_id", payRefId);
            params.put("pay_response", payResponse);
            params.put("action", type == 1 ? "success" : "error");
            params.put("ip", mActivity.ip);
            new MConnection().postRequestWithHttpHeaders(mActivity, "paySubscriptionUpdate", this, params, 103);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void getUserProfileDetails() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("profile_user_id", mActivity.mPref.getString("UserId", ""));
            new MConnection().postRequestWithHttpHeaders(mActivity, "getProfileDetail", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
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
                    SharedPreferences.Editor editor = mActivity.mPref.edit();
                    editor.putString("PayMemRefId", payRefId);
                    editor.apply();
                } else if (APICode == 103) { // Payment success
//                    {"status":true,"data":"","success":"celeb added successfully"}
//                    if (jo.has("success"))
//                        mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), jo.getString("success"));
//                    else
//                        mActivity.showMessageWithTitle(getActivity(), mActivity.mPref.getString("20", "Alert!"), "Payment Successful!");
//                    mActivity.onBackPressed();
                    SharedPreferences.Editor editor = mActivity.mPref.edit();
                    editor.putInt("MemberShip", 1);
                    editor.apply();
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

    @Override
    public void paymentProcessStateChanged(PaymentProcessAndroid process) {
        switch (process.getState()) {
            case BEFORE_COMPLETION:
                process.complete();
                break;
            case COMPLETED:
                try {
//                    PaymentMethodType paymentMethodType = PaymentMethodType.getPaymentMethodTypeByIdentifier("INT");
//                    PaymentMethodType paymentMethodType = process.getPaymentMethodType();
                    AliasPaymentMethod aliasPaymentMethod = process.getAliasPaymentMethod();
//                    ByjunoPaymentInfo paymentInfo = process.getPaymentOptions().getByjunoPaymentInfo();

                    if (aliasPaymentMethod != null) {
                        // serialize and securely store pm for reuse
                        String json = aliasPaymentMethod.toJson();
                        aliasPaymentMethod = AliasPaymentMethod.fromJson(json);

                        String paymentRefNo = aliasPaymentMethod.getAlias();
//                    String paymentRefNo = aliasPaymentMethod.getType().

                        if (aliasPaymentMethod instanceof AliasPaymentMethodCreditCard) {
                            AliasPaymentMethodCreditCard aliasPaymentMethodCreditCard = (AliasPaymentMethodCreditCard) aliasPaymentMethod;
//                        String paymentRefNo = aliasPaymentMethodCreditCard.getAlias();
                            JSONObject jo = new JSONObject();
                            jo.put("type", aliasPaymentMethodCreditCard.getType().getName());
                            jo.put("paymentRefNo", payRefId);
                            jo.put("alias", paymentRefNo);
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
                Log.e("PaymentCancelled", "payment cancelled");
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
