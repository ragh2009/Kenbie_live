package com.kenbie.fragments;


import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.adapters.SettingUpdateAdapter;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.listeners.ProfileOptionListener;
import com.kenbie.model.OptionsData;
import com.kenbie.model.ProfileInfo;
import com.kenbie.util.Constants;

import org.json.JSONArray;
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

public class EditCastingCategoriesFragment extends BaseFragment implements ProfileOptionListener, APIResponseHandler, IPaymentProcessStateListener, View.OnClickListener {
    private ProfileInfo profileInfo;
    private int type;
    private ArrayList<OptionsData> mData;
    private SettingUpdateAdapter mAdapter;
    private String boundary = "---------------------------" + System.currentTimeMillis();
    private ListView mList = null;
    private TextView screenTitle, mSaveBtn;
    private boolean isCastingEdit;
    private String payRefId = "";

    public EditCastingCategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            try {
                profileInfo = (ProfileInfo) getArguments().getSerializable("ProfileInfo");
            } catch (Exception e) {
                e.printStackTrace();
            }
            type = getArguments().getInt("Type", 3);

            if (type == 25) // casting categories
                mData = (ArrayList<OptionsData>) getArguments().getSerializable("CastCategories");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout topBar = view.findViewById(R.id.top_bar);
        ImageView backBtn = view.findViewById(R.id.m_back_button);
        backBtn.setOnClickListener(this);

        screenTitle = view.findViewById(R.id.m_title);
        screenTitle.setTypeface(KenbieApplication.S_NORMAL);

        mSaveBtn = view.findViewById(R.id.m_save_btn);
        mSaveBtn.setTypeface(KenbieApplication.S_NORMAL);
        mSaveBtn.setOnClickListener(this);

        mList = (ListView) view.findViewById(R.id.setting_list);

     /*   TextView saveBtn = (TextView) view.findViewById(R.id.save_btn);
        if (type == 25) {
            topBar.setVisibility(View.GONE);
            saveBtn.setText(mActivity.mPref.getString("144", "ADD CASTING"));
            saveBtn.setVisibility(View.VISIBLE);
        } else
            saveBtn.setText(mActivity.mPref.getString("225", "SAVE"));

        saveBtn.setTypeface(KenbieApplication.S_NORMAL);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 25)
                    addCasting(105);
                else
                    updateInfoToServer();
            }
        });*/

        if (type != 25) {
            mSaveBtn.setText(mActivity.mPref.getString("225", "Save"));
            mData = bindContent();
        } else
            mSaveBtn.setText("+ " + mActivity.mPref.getString("144", "Casting"));


        if (type == 20) {
            topBar.setVisibility(View.GONE);
            getNotificationSetting();
        } else {
            topBar.setVisibility(View.VISIBLE);
            if (type != 25)
                mSaveBtn.setVisibility(View.INVISIBLE);

            mAdapter = new SettingUpdateAdapter(mActivity, mData, this);
            mList.setAdapter(mAdapter);
        }
    }


    private ArrayList<OptionsData> bindContent() {
        ArrayList<OptionsData> values = new ArrayList<>();
        try {
            if (type == 3) { // 3-"Casting Settings"
                OptionsData od = new OptionsData();
                od.setId(1);
                od.setName("Allow Casting Invitations");
                if (profileInfo.getDiscipline() != null && !profileInfo.getDiscipline().equalsIgnoreCase("null"))
                    od.setActive(Integer.valueOf(profileInfo.getDiscipline()) == 1);
                values.add(od);
            } else if (type == 5) { // 5 - "Who Can See The Profile?"
                String[] seenArray = {"Professionals", "Friends", "All Public"};
                ArrayList<Integer> value = new ArrayList<>();
                try {
                    if (profileInfo.getSeen_type() != null && !profileInfo.getSeen_type().equalsIgnoreCase("null")) {
                        String[] mData = profileInfo.getSeen_type().replace(",", "-").split("-");
                        if (mData != null)
                            for (int i = 0; i < mData.length; i++)
                                value.add(Integer.valueOf(mData[i]));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < seenArray.length; i++) {
                    OptionsData od = new OptionsData();
                    od.setId((i + 1));
                    od.setName(seenArray[i]);
                    if (value.indexOf(od.getId()) != -1)
                        od.setActive(true);

                    values.add(od);
                }
            } else if (type == 8) {  // 8 - Discipline
                values = getMyData(profileInfo.getArrdisciplines(), profileInfo.getUser_disciplines());
                if (values != null && values.size() > 0)
                    values.remove(0);
            } else if (type == 9) { // 9 - Categories
                values = getMyData(profileInfo.getCategoriesData(), profileInfo.getUser_categories());
                if (values != null && values.size() > 0)
                    values.remove(0);
            } else if (type == 20) { // 20 - Notification
                String[] seenArray = {"Someone likes", "Someone favorite", "Send me a new message", "A new casting is posted in your country", "Other notifications", "Profile Visitor"};
                for (int i = 0; i < seenArray.length; i++) {
                    OptionsData od = new OptionsData();
                    od.setId((i + 1));
                    od.setName(seenArray[i]);
//                    if (value.indexOf(od.getId()) != -1)
                    od.setActive(true);
                    values.add(od);
                }
            } else if (type == 21) { // 21 - Privacy
                String[] seenArray = {"All user view my profile", "Members view my profile", "Lets other share my profile", "Show online", "Show Distance", "Show me only people i like", "Show me only people i visit", "Allow search by email"};
                for (int i = 0; i < seenArray.length; i++) {
                    OptionsData od = new OptionsData();
                    od.setId((i + 1));
                    od.setName(seenArray[i]);
//                    if (value.indexOf(od.getId()) != -1)
//                        od.setActive(true);
                    values.add(od);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }


    private void addCasting(final int APICode) {
        mActivity.showProgressDialog(true);
        String url = "";
        if (mActivity.castingParams != null && mActivity.castingParams.containsKey("casting_id")) {
            isCastingEdit = true;
            url = MConnection.API_BASE_URL + "editCasting";
        } else
            url = MConnection.API_BASE_URL + "addCasting";

        Log.d("casting_req_url", url);

        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            if (response != null) {
                                Log.d("casting_response", response);
                                JSONObject jResponse = new JSONObject(response);
                                if (jResponse.has("status") && jResponse.getBoolean("status"))
                                    getResponse(response, APICode);
                                else if (jResponse.has("error"))
                                    getError(jResponse.getString("error"), APICode);
                                else
                                    getError(mActivity.mPref.getString("270", "Something Wrong! Please try later."), APICode);

                                Log.d("Response", response);
                            } else
                                getError(mActivity.mPref.getString("270", "Something Wrong! Please try later."), APICode);
                        } catch (Exception e) {
                            getError(mActivity.mPref.getString("270", "Something Wrong! Please try later."), APICode);
                            e.printStackTrace();
                        }
                        mActivity.showProgressDialog(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null && error.getMessage() != null)
                    Toast.makeText(mActivity, error.getMessage(), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(mActivity, mActivity.mPref.getString("270", "Something Wrong! Please try later."), Toast.LENGTH_LONG).show();
                mActivity.showProgressDialog(false);
            }
        });

        // TODO - commented headers in new lib
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("Content-Type", "multipart/form-data;boundary=" + boundary);
//        params.put("X-API-KEY", MConnection.API_KEY);
//        smr.setHeaders(params);

        smr.addStringParam("X-API-KEY", MConnection.API_KEY);
        smr.addStringParam("user_id", mActivity.mPref.getString("UserId", ""));
        smr.addStringParam("login_key", mActivity.mPref.getString("LoginKey", ""));
        smr.addStringParam("login_token", mActivity.mPref.getString("LoginToken", ""));
        smr.addStringParam("lang", mActivity.mPref.getString("UserSavedLangCode", "en")); // Language
        smr.addStringParam("casting_title", mActivity.castingParams.get("casting_title"));
        smr.addStringParam("casting_categories", getCheckedCategories());
        smr.addStringParam("casting_face", mActivity.castingParams.get("casting_face"));
        smr.addStringParam("casting_gender", mActivity.castingParams.get("casting_gender"));
        smr.addStringParam("requirements", mActivity.castingParams.get("requirements"));
        smr.addStringParam("casting_location", mActivity.castingParams.get("casting_location"));
        smr.addStringParam("casting_from_age", mActivity.castingParams.get("casting_from_age"));
        smr.addStringParam("casting_to_age", mActivity.castingParams.get("casting_to_age"));
        smr.addStringParam("casting_from_time", mActivity.castingParams.get("casting_from_time"));
        smr.addStringParam("casting_to_time", mActivity.castingParams.get("casting_to_time"));
        smr.addStringParam("casting_fees", mActivity.castingParams.get("casting_fees"));
        smr.addStringParam("starting_date", mActivity.castingParams.get("starting_date"));
        smr.addStringParam("starting_month", mActivity.castingParams.get("starting_month"));
        smr.addStringParam("starting_year", mActivity.castingParams.get("starting_year"));
        smr.addStringParam("end_date", mActivity.castingParams.get("end_date"));
        smr.addStringParam("end_month", mActivity.castingParams.get("end_month"));
        smr.addStringParam("end_year", mActivity.castingParams.get("end_year"));
        smr.addStringParam("device_id", mActivity.deviceId);
        smr.addStringParam("android_token", FirebaseInstanceId.getInstance().getToken());
        smr.addStringParam("casting_address", mActivity.castingParams.get("casting_address"));
        smr.addStringParam("casting_country", mActivity.castingParams.get("casting_country"));
        if (isCastingEdit && mActivity.castingParams.containsKey("casting_id"))
            smr.addStringParam("casting_id", mActivity.castingParams.get("casting_id"));
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (mActivity.imgPath != null) {
            smr.addFile("casting_img", mActivity.imgPath);
            Log.i("Image_Path", mActivity.imgPath);
        }
        Volley.newRequestQueue(mActivity).add(smr);
    }

    private String getCheckedCategories() {
        String value = "";
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).isActive()) {
                if (value.length() == 0)
                    value = mData.get(i).getId() + "";
                else
                    value = value + "," + mData.get(i).getId() + "";
            }
        }
        return value;
    }


    // Update info into server
    private void updateInfoToServer() {
        try {
            if (mActivity.isOnline()) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", mActivity.mPref.getString("UserId", ""));
                params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
                params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
                MConnection mc = new MConnection();
                if (type == 3) { // 3-"Casting Settings"
                    mActivity.showProgressDialog(true);
                    params.put("discipline", (mData.get(0).isActive() ? 1 : 0) + "");
                    mc.postRequestWithHttpHeaders(mActivity, "updateUserCastingSetting", this, params, 101);
                } else if (type == 5) { // 5 - "Who Can See The Profile?"
                    mActivity.showProgressDialog(true);
                    String seenType = "";
                    for (int i = 0; i < mData.size(); i++) {
                        if (mData.get(i).isActive()) {
                            if (seenType.length() == 0)
                                seenType = mData.get(i).getId() + "";
                            else
                                seenType = seenType + "," + mData.get(i).getId() + "";
                        }
                    }
                    params.put("seen_type", seenType + "");
                    mc.postRequestWithHttpHeaders(mActivity, "updateProfilSeenBy", this, params, 102);
                } else if (type == 8) {  // 8 - Discipline
                    String seenType = "";
                    for (int i = 0; i < mData.size(); i++) {
                        if (mData.get(i).isActive()) {
                            if (seenType.length() == 0)
                                seenType = mData.get(i).getId() + "";
                            else
                                seenType = seenType + "," + mData.get(i).getId() + "";
                        }
                    }
                    params.put("user_disciplines", seenType + "");
                    mc.postRequestWithHttpHeaders(mActivity, "updateUserDisciplines", this, params, 103);
                } else if (type == 9) {  // 9 - Categories
                    String seenType = "";
                    for (int i = 0; i < mData.size(); i++) {
                        if (mData.get(i).isActive()) {
                            if (seenType.length() == 0)
                                seenType = mData.get(i).getId() + "";
                            else
                                seenType = seenType + "," + mData.get(i).getId() + "";
                        }
                    }
                    params.put("user_categories", seenType + "");
                    mc.postRequestWithHttpHeaders(mActivity, "updateUserCategory", this, params, 104);
                } else if (type == 20) {  // 20 - Notifications settings
                    // TODO
                    mActivity.showProgressDialog(false);
                } else if (type == 21) {  // 21 - Privacy settings
                    // TODO
                    mActivity.showProgressDialog(false);
                }
            } else {
                mActivity.showProgressDialog(false);
                mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getNotificationSetting() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("device_id", mActivity.deviceId);
            new MConnection().postRequestWithHttpHeaders(mActivity, "getNotification", this, params, 106);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void updateNotificationSetting(OptionsData optionsData) {
        if (mActivity.isOnline()) {
//            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("device_id", mActivity.deviceId);
            params.put("notification_id", optionsData.getId() + "");
            params.put("status", (optionsData.isActive() ? 1 : 0) + "");
            new MConnection().postRequestWithHttpHeaders(mActivity, "updateNotification", this, params, 107);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private ArrayList<OptionsData> getMyData(String options, String myData) {
        ArrayList<Integer> values = new ArrayList<>();
        ArrayList<OptionsData> optionsDataEthnicity = new ArrayList<>();
        try {
            if (myData != null && !myData.equalsIgnoreCase("null")) {
                String[] mData = myData.replace(",", "-").split("-");
                if (mData != null)
                    for (int i = 0; i < mData.length; i++)
                        try {
                            values.add(Integer.valueOf(mData[i]));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
            }

            JSONArray userEthnicity = new JSONArray(options);
            for (int i = 0; i < userEthnicity.length(); i++) {
                OptionsData od = new OptionsData();
                JSONObject jod = new JSONObject(userEthnicity.getString(i));
                od.setId(jod.getInt("id"));
                od.setName(jod.getString("name"));
                if (jod.has("shortname"))
                    od.setOptionCode(jod.getString("shortname"));

                if (values.indexOf(od.getId()) != -1)
                    od.setActive(true);
                else
                    od.setActive(false);

                optionsDataEthnicity.add(od);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return optionsDataEthnicity;
    }


    @Override
    public void onResume() {
        // 3-"Casting Settings", 5 - "Who Can See The Profile?", 9 - Discipline, 10 - Categories
        if (type == 3)
            mActivity.updateActionBar(10, "CASTING SETTINGS", false, true, false);
        else if (type == 5)
            mActivity.updateActionBar(11, "WHO CAN SEE THE PROFILE?", false, true, false);
        else if (type == 8) {
            screenTitle.setText(mActivity.mPref.getString("331", "DISCIPLINE"));
            mActivity.updateActionBar(12, "DISCIPLINE", false, true, true);
        } else if (type == 9) {
            screenTitle.setText(mActivity.mPref.getString("332", "CATEGORIES"));
            mActivity.updateActionBar(13, "CATEGORIES", false, true, true);
        } else if (type == 20)
            mActivity.updateActionBar(20, mActivity.mPref.getString("163", "NOTIFICATIONS"), false, true, false);
        else if (type == 21)
            mActivity.updateActionBar(21, "PRIVACY", false, true, false);
        else if (type == 25) {
            screenTitle.setText(mActivity.mPref.getString("332", "CATEGORIES"));
            mActivity.updateActionBar(21, "ADD CATEGORIES", false, true, true);
        }
        super.onResume();
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

                if (APICode == 106) {
                    mData = bindNotificationData(jo.getString("data"));
                    mAdapter = new SettingUpdateAdapter(mActivity, mData, this);
                    mList.setAdapter(mAdapter);
                } else if (APICode == 105) {
                    if (isCastingEdit)
                        mActivity.launchCasting(3);
                    else
                        castingPaymentProcess(jo.getString("data"));
                } else if (APICode == 108) {
                    JSONObject jData = new JSONObject(jo.getString("data"));
                    if (jData.has("pay_ref_id"))
                        payRefId = jData.getString("pay_ref_id");

                    startPaymentProcess(3);
                } else if (APICode == 102) {
                    mActivity.launchCasting(4);
                        /*Intent i = new Intent(mActivity, KenbieActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        mActivity.startActivity(i);*/
                } else
                    mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
            }
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
    public void getAction(OptionsData value) {

    }

    @Override
    public void getDataList(ArrayList<OptionsData> value, int position, int type1) {
        try {
            mData.get(position).setActive(!mData.get(position).isActive());
            mAdapter.refreshData(mData);
            if (type == 20)
                updateNotificationSetting(mData.get(position));
            else if (type != 25)
                mSaveBtn.performClick();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ArrayList<OptionsData> bindNotificationData(String data) {
        ArrayList<OptionsData> values = new ArrayList<>();
        try {
            JSONObject jData = new JSONObject(data);
            JSONArray nData = new JSONArray(jData.getString("notifications"));
            for (int i = 0; i < nData.length(); i++) {
                JSONObject jo = nData.getJSONObject(i);
                OptionsData od = new OptionsData();
                od.setId(jo.getInt("id"));
                od.setName(jo.getString("title"));
                od.setActive(jo.getInt("status") == 1);
                values.add(od);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    private void castingPaymentProcess(String data) {
        try {
            JSONObject jo = new JSONObject(data);
            if (jo.has("id")) {
                SharedPreferences.Editor editor = mActivity.mPref.edit();
                editor.putInt("CastingPmtId", jo.getInt("id"));
                editor.apply();
                getCastingRefNumber(jo.getInt("id"));

//                startPaymentProcess(2);
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


    private void saveTransactionDetailsOnServer(String payResponse, String refNO, int type) {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
//            Param: @"user_id=%@&login_key=%@&login_token=%@&device_id=%@&android_token=%@&casting_id=%@&action=%@&pay_response=%@&ip=%@"
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("casting_id", mActivity.mPref.getInt("CastingPmtId", 0) + "");
            params.put("pay_ref_id", payRefId);
            params.put("pay_response", payResponse);
            params.put("action", type == 1 ? "success" : "error");
            params.put("ip", mActivity.ip);
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "paycastUpdate", this, params, 102);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void getCastingRefNumber(int id) {
        if (mActivity.isOnline()) {
//            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("casting_id", id + "");
            params.put("ip", mActivity.ip);
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "paycastRefNo", this, params, 108);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.m_save_btn)
            if (type == 25)
                addCasting(105);
            else
                updateInfoToServer();
        else if (v.getId() == R.id.m_back_button)
            mActivity.onBackPressed();
    }
}
