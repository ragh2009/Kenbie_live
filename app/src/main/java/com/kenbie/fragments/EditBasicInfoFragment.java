package com.kenbie.fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

import com.google.zxing.common.StringUtils;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.adapters.AutoCompleteAdapter;
import com.kenbie.adapters.InfoSingleChoiceAdapter;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.model.LocationItem;
import com.kenbie.model.Option;
import com.kenbie.model.ProfileInfo;
import com.kenbie.util.Constants;
import com.kenbie.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ch.datatrans.payment.AliasPaymentMethod;
import ch.datatrans.payment.AliasPaymentMethodCreditCard;
import ch.datatrans.payment.AliasRequest;
import ch.datatrans.payment.BusinessException;
import ch.datatrans.payment.DisplayContext;
import ch.datatrans.payment.IPaymentProcessStateListener;
import ch.datatrans.payment.Payment;
import ch.datatrans.payment.PaymentMethod;
import ch.datatrans.payment.PaymentMethodType;
import ch.datatrans.payment.PaymentProcessAndroid;
import ch.datatrans.payment.ResourceProvider;

import static com.kenbie.util.Constants.PAYMENT_TEST_MODE;

public class EditBasicInfoFragment extends BaseFragment implements View.OnClickListener, APIResponseHandler, IPaymentProcessStateListener {
    private static TextView etDob;
    private EditText etName, etCompany, etMobile, etAddress, etStreet, etPostalCode;
    private AppCompatSpinner sWho, sGender;
    private SwitchCompat agencySwitch;
    private ProfileInfo profileInfo;
    private AutoCompleteTextView locationSearch;
    private AutoCompleteAdapter adapter;
    private ArrayList<LocationItem> locationItemArrayList;
    private int nType = 0, userType = 1;
    private LayoutInflater mLayoutInflater;
    private TextView etEmail;
    private String payRefId = "";

    public EditBasicInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            profileInfo = (ProfileInfo) getArguments().getSerializable("ProfileInfo");
            nType = getArguments().getInt("NType", 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_basic_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLayoutInflater = LayoutInflater.from(mActivity);

        if (profileInfo.getUser_type() != null && profileInfo.getUser_type().length() > 0)
            userType = Integer.valueOf(profileInfo.getUser_type());
//        1 - Model, 3-Photographer, 2-Agency

        ImageView backBtn = view.findViewById(R.id.m_back_button);
        backBtn.setOnClickListener(this);
        TextView screenTitle = view.findViewById(R.id.m_title);
        screenTitle.setTypeface(KenbieApplication.S_NORMAL);
        screenTitle.setText(mActivity.mPref.getString("219", "Your Basic Information"));
        TextView saveBtn = view.findViewById(R.id.m_save_btn);
        saveBtn.setText(mActivity.mPref.getString("225", "Save"));
        saveBtn.setTypeface(KenbieApplication.S_NORMAL);
        saveBtn.setOnClickListener(this);

        TextView tName = view.findViewById(R.id.t_name);
        tName.setText(mActivity.mPref.getString("35", "Name") + getString(R.string.asteriskred));
        tName.setTypeface(KenbieApplication.S_NORMAL);
        TextView tWhoName = view.findViewById(R.id.t_who_name);
        tWhoName.setText(mActivity.mPref.getString("226", "Who I am") + getString(R.string.asteriskred));
        tWhoName.setTypeface(KenbieApplication.S_NORMAL);
        TextView tEmail = view.findViewById(R.id.t_email);
        tEmail.setText(mActivity.mPref.getString("16", "Email Address") + getString(R.string.asteriskred));
        tEmail.setTypeface(KenbieApplication.S_NORMAL);
        TextView tDob = view.findViewById(R.id.t_dob);
        tDob.setText(mActivity.mPref.getString("227", "Birth Date") + getString(R.string.asteriskred));
        tDob.setTypeface(KenbieApplication.S_NORMAL);
        TextView tGender = view.findViewById(R.id.t_gender);
        tGender.setText(mActivity.mPref.getString("69", "Gender") + getString(R.string.asteriskred));
        tGender.setTypeface(KenbieApplication.S_NORMAL);
        TextView tCompany = view.findViewById(R.id.t_company);
        tCompany.setTypeface(KenbieApplication.S_NORMAL);
        TextView tMobile = view.findViewById(R.id.t_mobile);
        tMobile.setText(mActivity.mPref.getString("228", "Mobile Number") + getString(R.string.asteriskred));
        tMobile.setTypeface(KenbieApplication.S_NORMAL);
        TextView tLocation = view.findViewById(R.id.t_location);
        tLocation.setText(mActivity.mPref.getString("39", "Location") + getString(R.string.asteriskred));
        tLocation.setTypeface(KenbieApplication.S_NORMAL);
        TextView tAddress = view.findViewById(R.id.t_address);
        tAddress.setText(mActivity.mPref.getString("142", "Address") + getString(R.string.asteriskred));
        tAddress.setTypeface(KenbieApplication.S_NORMAL);
        TextView tStreet = view.findViewById(R.id.t_street);
        tStreet.setText(mActivity.mPref.getString("229", "Street") + getString(R.string.asteriskred));
        tStreet.setTypeface(KenbieApplication.S_NORMAL);
        TextView tPostalCode = view.findViewById(R.id.t_postal_code);
        tPostalCode.setText(mActivity.mPref.getString("230", "Postal Code"));
        tPostalCode.setTypeface(KenbieApplication.S_NORMAL);
        TextView tFromAgency = view.findViewById(R.id.t_from_agency);
        tFromAgency.setText(mActivity.mPref.getString("231", "Are You From Agency?"));
        tFromAgency.setTypeface(KenbieApplication.S_NORMAL);

        etName = (EditText) view.findViewById(R.id.et_name);
        etName.setHint(mActivity.mPref.getString("35", "Name"));
        etName.setTypeface(KenbieApplication.S_NORMAL);

        sWho = (AppCompatSpinner) view.findViewById(R.id.spinner_who);
        sWho.setPrompt(mActivity.mPref.getString("226", "Who I am"));
        sWho.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((InfoSingleChoiceAdapter) sWho.getAdapter()).updateSelection(position);
                if (position == 2)
                    tCompany.setText(mActivity.mPref.getString("155", "Company") + getString(R.string.asteriskred));
                else
                    tCompany.setText(mActivity.mPref.getString("155", "Company"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etEmail = (TextView) view.findViewById(R.id.et_email);
        etEmail.setHint(mActivity.mPref.getString("16", "Email Address"));
        etEmail.setTypeface(KenbieApplication.S_NORMAL);

        etDob = (TextView) view.findViewById(R.id.et_dob);
        etDob.setHint(mActivity.mPref.getString("227", "Birth Date"));
        etDob.setTypeface(KenbieApplication.S_NORMAL);
        etDob.setOnClickListener(this);

        sGender = (AppCompatSpinner) view.findViewById(R.id.spinner_gender);
        sGender.setPrompt(mActivity.mPref.getString("69", "Gender"));
        sGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((InfoSingleChoiceAdapter) sGender.getAdapter()).updateSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etCompany = (EditText) view.findViewById(R.id.et_company);
        etCompany.setHint(mActivity.mPref.getString("155", "Company"));
        etCompany.setTypeface(KenbieApplication.S_NORMAL);

        if (userType == 2)
            tCompany.setText(mActivity.mPref.getString("155", "Company") + getString(R.string.asteriskred));
        else
            tCompany.setText(mActivity.mPref.getString("155", "Company"));

        etMobile = (EditText) view.findViewById(R.id.et_mobile);
        etMobile.setHint(mActivity.mPref.getString("228", "Mobile Number"));
        etMobile.setTypeface(KenbieApplication.S_NORMAL);

        locationSearch = (AutoCompleteTextView) view.findViewById(R.id.et_location);
        locationSearch.setHint(mActivity.mPref.getString("39", "Location"));
        locationSearch.setTypeface(KenbieApplication.S_NORMAL);
        locationSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationItem cityValue = adapter.getItem(position);
                locationSearch.setText(cityValue.getCity() + ", " + cityValue.getCountryName());
//                locationSearch.setSelection(locationSearch.getText().toString().length());
                locationSearch.setTag(cityValue);
                etAddress.setSelected(true);
                etPostalCode.setText(cityValue.getZipCode());
            }
        });

        locationSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                gettingLocationData(s.toString());

//                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
//                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
//                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etAddress = (EditText) view.findViewById(R.id.et_address);
        etAddress.setHint(mActivity.mPref.getString("142", "Address"));
        etAddress.setTypeface(KenbieApplication.S_NORMAL);
        etStreet = (EditText) view.findViewById(R.id.et_street);
        etStreet.setHint(mActivity.mPref.getString("229", "Street"));
        etStreet.setTypeface(KenbieApplication.S_NORMAL);
        etPostalCode = (EditText) view.findViewById(R.id.et_postal_code);
        etPostalCode.setHint(mActivity.mPref.getString("230", "Postal Code"));
        etPostalCode.setTypeface(KenbieApplication.S_NORMAL);

        agencySwitch = (SwitchCompat) view.findViewById(R.id.agency_switch);

        setProfileData();
    }

    private void setProfileData() {
        try {
            int who = -1;
            if (profileInfo.getUser_type() != null && !profileInfo.getUser_type().equalsIgnoreCase("null") && profileInfo.getUser_type().length() > 0)
                who = (Integer.valueOf(profileInfo.getUser_type()));

            InfoSingleChoiceAdapter adapter = new InfoSingleChoiceAdapter(mLayoutInflater, bindWhoAdapterData(), who);
            sWho.setAdapter(adapter);
            sWho.setSelection(who);
//            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mActivity,
//                    R.array.type_1_array, R.layout.spinner_item);
//            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);


            InfoSingleChoiceAdapter genderAdapter = new InfoSingleChoiceAdapter(mLayoutInflater, bindGenderAdapterData(), profileInfo.getGender());
            sGender.setAdapter(genderAdapter);
            sGender.setSelection(profileInfo.getGender());

//            ArrayAdapter<CharSequence> genderSpinner = ArrayAdapter.createFromResource(mActivity,
//                    R.array.gender_array, R.layout.spinner_item);
//            genderSpinner.setDropDownViewResource(R.layout.spinner_dropdown_item);
//            sGender.setAdapter(genderSpinner);

            etName.setText(profileInfo.getFirst_name());

            if (profileInfo.getEmail_id() != null && !profileInfo.getEmail_id().equalsIgnoreCase("null"))
                etEmail.setText(profileInfo.getEmail_id());

            if (profileInfo.getBirth_year() != null && profileInfo.getBirth_day() != null && profileInfo.getBirth_month() != null)
                etDob.setText(new StringBuilder().append(profileInfo.getBirth_day()).append("-")
                        .append(profileInfo.getBirth_month()).append("-").append(profileInfo.getBirth_year()));

            if (profileInfo.getCompany_name() != null && !profileInfo.getCompany_name().equalsIgnoreCase("null"))
                etCompany.setText(profileInfo.getCompany_name());

            if (profileInfo.getPhone() != null && !profileInfo.getPhone().equalsIgnoreCase("null"))
                etMobile.setText(profileInfo.getPhone());

            if (profileInfo.getAddress1() != null && !profileInfo.getAddress1().equalsIgnoreCase("null"))
                etAddress.setText(profileInfo.getAddress1());

            if (profileInfo.getAddress2() != null && !profileInfo.getAddress2().equalsIgnoreCase("null"))
                etStreet.setText(profileInfo.getAddress2());

            String location = "";
            if (profileInfo.getCountry() != null && !profileInfo.getCountry().equalsIgnoreCase("null")) {
                location = profileInfo.getCountry();

                if (profileInfo.getCity() != null && !profileInfo.getCity().equalsIgnoreCase("null") && profileInfo.getCity().length() > 1)
                    location = profileInfo.getCity() + ", " + location;
            }

            locationSearch.setText(location);

            if (profileInfo.getPostal_code() != null && !profileInfo.getPostal_code().equalsIgnoreCase("null") && !profileInfo.getPostal_code().equalsIgnoreCase("0"))
                etPostalCode.setText(profileInfo.getPostal_code());

            if (profileInfo.getUser_from_agency() != null && profileInfo.getUser_from_agency().equalsIgnoreCase("Yes"))
                agencySwitch.setChecked(true);
            else
                agencySwitch.setChecked(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        gettingLocationData(null);
    }

    private ArrayList<Option> bindWhoAdapterData() {
        ArrayList<Option> values = new ArrayList<>();
        Option op1 = new Option();
        op1.setId(0);
        op1.setTitle(mActivity.mPref.getString("94", "Select"));
        values.add(op1);

        for (int i = 0; i < 3; i++) {
            Option op = new Option();
            op.setId(i + 1);
            if (i == 0) op.setTitle(mActivity.mPref.getString("28", "Model"));
            else if (i == 1) op.setTitle(mActivity.mPref.getString("30", "Agency"));
            else op.setTitle(mActivity.mPref.getString("29", "Photographer"));
            values.add(op);
        }
        return values;
    }

    private ArrayList<Option> bindGenderAdapterData() {
        ArrayList<Option> values = new ArrayList<>();
        Option op1 = new Option();
        op1.setId(0);
        op1.setTitle(mActivity.mPref.getString("94", "Select"));
        values.add(op1);

        for (int i = 0; i < 2; i++) {
            Option op = new Option();
            op.setId(i + 1);
            if (i == 0) op.setTitle(mActivity.mPref.getString("31", "Male"));
            else op.setTitle(mActivity.mPref.getString("32", "Female"));
            values.add(op);
        }

        return values;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.et_dob) {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(mActivity.getSupportFragmentManager(), "datePicker");
        } else if (v.getId() == R.id.m_save_btn) {
            if (isInfoValid())
                updateBasicInfo();
        } else if (v.getId() == R.id.m_back_button)
            mActivity.onBackPressed();
    }

    private void gettingLocationData(String searchStr) {
        try {
            Map<String, String> params = new HashMap<String, String>();
            if (searchStr == null) {
                params.put("ip", mActivity.ip);
            } else
                params.put("city_search", searchStr);
            params.put("device_id", mActivity.deviceId == null ? "" : mActivity.deviceId);
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "getLocation", this, params, 102);
        } catch (Exception e) {
            Log.d("HUS", "EXCEPTION " + e);
        }
    }

    private void updateBasicInfo() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            params.put("user_type", sWho.getSelectedItemPosition() + "");
            params.put("first_name", etName.getText().toString());
            params.put("gender", sGender.getSelectedItemPosition() + "");
            params.put("phone", etMobile.getText().toString());
            params.put("company_name", etCompany.getText().toString());
            params.put("user_from_agency", agencySwitch.isChecked() ? "1" : "0");

            LocationItem cityValue = (LocationItem) locationSearch.getTag();
            if (cityValue != null) {
                params.put("country", cityValue.getCountry());
                params.put("city", cityValue.getCity());
            } else {
                params.put("country", "");
                params.put("city", "");
            }

            params.put("address", etAddress.getText().toString());
            params.put("street", etStreet.getText().toString());
            params.put("pincode", etPostalCode.getText().toString());
            params.put("device_id", mActivity.deviceId);

            params.put("birthday", etDob.getText().toString().replace("-", "/"));
            if (profileInfo.getSocialType() != null && !profileInfo.getSocialType().equalsIgnoreCase("null"))
                params.put("social_type", profileInfo.getSocialType());

            new MConnection().postRequestWithHttpHeaders(mActivity, "updateProfile", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private boolean isInfoValid() {
        if (etName.getText().toString().equalsIgnoreCase("")) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("327", "The Name field is required."));
            return false;
        } else if (sWho.getSelectedItemPosition() == 0) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("349", "Please select who you are"));
            return false;
        } else if (!etEmail.getText().toString().equalsIgnoreCase("") && !mActivity.isValidEmail(etEmail.getText().toString(), mActivity)) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("264", "Please enter valid email id"));
            return false;
        } else if (etDob.getText().toString().equalsIgnoreCase("")) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("264", "The Birthday field is required."));
            return false;
        } else if (sGender.getSelectedItemPosition() == 0) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("350", "Please select your gender"));
            return false;
        } else if (sWho.getSelectedItemPosition() == 2 && etCompany.getText().toString().equalsIgnoreCase("")) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("360", "Company name is empty"));
            return false;
        } else if (etMobile.getText().toString().equalsIgnoreCase("") || etMobile.getText().toString().length() < 6) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("328", "The Mobile Phone Number field is required."));
            return false;
        } else if (locationSearch.getText().toString().equalsIgnoreCase("")) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("46", "The Location field is required."));
            return false;
        } else if (etAddress.getText().toString().equalsIgnoreCase("")) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("329", "The Address Line-1 field is required."));
            return false;
        } else if (etStreet.getText().toString().equalsIgnoreCase("")) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("351", "Please enter your street name"));
            return false;
        } /*else if (etPostalCode.getText().toString().equalsIgnoreCase("")) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), "Please enter postal code.");
            return false;
        }*/

        return true;
    }

    @Override
    public void getError(String error, int APICode) {
        if (APICode != 102) {
            if (error != null)
                mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), error);
            else
                mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
        }

        mActivity.showProgressDialog(false);
    }

    @Override
    public void getResponse(String response, int APICode) {
        try {
            if (response != null) {
                JSONObject jo = new JSONObject(response);

                if (APICode == 101) { // profile details
                    if (jo.has("status") && jo.getBoolean("status")) {
//                        mActivity.onBackPressed();
                        if (jo.has("success")) {
                            SharedPreferences.Editor editor = mActivity.mPref.edit();
                            editor.putBoolean("HasAddress", true);
                            editor.apply();
                            if (nType == 1) { // Navigate to payment
                                payRefId = mActivity.mPref.getString("PayMemRefId", "123456");
                                startPaymentProcess(1);
                            }else
                                mActivity.onBackPressed();
//                                mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("success"));
                        } else
                            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("330", "UPDATED SUCCESSFULLY !"));
                    } else if (jo.has("error"))
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("error"));
                    else
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                } else if (APICode == 103) { // Payment success
//                    {"status":true,"data":"","success":"celeb added successfully"}
                    if (jo.has("success"))
                        mActivity.showToast(mActivity, jo.getString("success"));
                    else
                        mActivity.showToast(mActivity, "Payment Successful!");
                    mActivity.hideKeyboard(mActivity);
                    mActivity.replaceFragment(new MembershipFragment(), true, false);
                } else if (jo.getBoolean("status")) {
                    JSONArray jsonArray = jo.getJSONArray("data");
                    locationItemArrayList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo1 = jsonArray.getJSONObject(i);
                        LocationItem locationItem = new LocationItem();
                        locationItem.setCity(jo1.getString("city"));
                        locationItem.setStateProv(jo1.getString("stateprov"));
                        locationItem.setCountry(jo1.getString("country"));
                        locationItem.setZipCode(jo1.getString("zipcode"));
                        locationItem.setLatitude((float) jo1.getDouble("latitude"));
                        locationItem.setLongitude((float) jo1.getDouble("longitude"));
                        locationItem.setCountryName(jo1.getString("country_name_en"));
                        locationItem.setCountryId(jo1.getInt("country_id"));
                        locationItemArrayList.add(locationItem);
                    }
                    bindLocationSearchData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mActivity.showProgressDialog(false);
    }

    private void startPaymentProcess(int subType) {
        try {
            int amount = 0;
            if (subType == 1) { // Membership 149.99
                amount =  mActivity.mPref.getInt("SubsPayment", 14999);
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

    private void startPaymentProcess1(int subType) {
        try {
            String refTitle = "KENBIE AG_mobile"; // supplied by merchant's server

            int amount = 0;
            if (subType == 1) { // Membership 149
                amount = 14900;
                refTitle = "Membership";
            } else if (subType == 2) { // Casting -  Casting t=cast&rid=<Casting_id> - 4.99
                amount = 499;
                refTitle = "Casting";
            } else { // Add Celeb - 4.99
                amount = 499;
                refTitle = "Celebrity";
            }

//            String signature = null;
//            Payment payment = new Payment(Constants.MERCHANT_ID, refTitle, Constants.CURRENCY_CODE,
//                    amount, signature);
            Collection<PaymentMethod> methods = PaymentMethod.createMethods(
                    PaymentMethodType.VISA,
                    PaymentMethodType.MASTERCARD, PaymentMethodType.AMEX, PaymentMethodType.PAYPAL, PaymentMethodType.DINERS, PaymentMethodType.DISCOVER, PaymentMethodType.ELV);
            DisplayContext dc = new DisplayContext(new ResourceProvider(), mActivity);
//            PaymentProcessAndroid ppa = new PaymentProcessAndroid(dc, payment, methods);
            AliasRequest ar = new AliasRequest(Constants.MERCHANT_ID, Constants.CURRENCY_CODE, methods);
            PaymentProcessAndroid ppa = new PaymentProcessAndroid(dc, ar);
            ppa.setTestingEnabled(true);
            ppa.addStateListener(this);
            ppa.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindLocationSearchData() {
        if (adapter == null) {
//            adapter = new AutoCompleteAdapter(activity, android.R.layout.simple_dropdown_item_1line, locationItemArrayList);
            adapter = new AutoCompleteAdapter(mActivity, android.R.layout.simple_spinner_dropdown_item, locationItemArrayList);
            locationSearch.setAdapter(adapter);
        } else {
            adapter.refreshData(locationItemArrayList);
        }
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
                    AliasPaymentMethod aliasPaymentMethod = process.getAliasPaymentMethod();
                    if (aliasPaymentMethod != null) {
                        // serialize and securely store pm for reuse
                        String paymentRefNo = aliasPaymentMethod.getAlias();
                        if (aliasPaymentMethod instanceof AliasPaymentMethodCreditCard) {
                            AliasPaymentMethodCreditCard aliasPaymentMethodCreditCard = (AliasPaymentMethodCreditCard) aliasPaymentMethod;
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
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case CANCELED:
                // ignore, abort checkout, whatever...
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
            params.put("pay_ref_id", mActivity.mPref.getString("PayMemRefId", ""));
            params.put("pay_response", payResponse);
            params.put("action", type == 1 ? "success" : "error");
            params.put("ip", mActivity.ip);
            new MConnection().postRequestWithHttpHeaders(mActivity, "paySubscriptionUpdate", this, params, 103);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }


    @Override
    public void onResume() {
        mActivity.updateActionBar(8, "Your Basic Information", false, true, true);
        super.onResume();
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            if (!etDob.getText().toString().equalsIgnoreCase("")) {
                String startDate = etDob.getText().toString();
                if (startDate.length() > 2) { // dd-MM-yyyy
                    String[] sDate = startDate.split("-"); // year + "-" + (month + 1) + "-" + day
                    day = Integer.valueOf(sDate[0]);
                    month = Integer.valueOf(sDate[1]) - 1;
                    year = Integer.valueOf(sDate[2]);
                }
            }

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    R.style.datepicker, this, year, month, day);
            datepickerdialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
//            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
//                    AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);

            return datepickerdialog;
            // Create a new instance of DatePickerDialog and return it
//            return new DatePickerDialog(mActivity, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            StringBuilder date = new StringBuilder().append(day).append("-")
                    .append(month + 1).append("-").append(year);
            etDob.setText(Utility.getDateFormat(date.toString()));
        }
    }
}
