package com.kenbie.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.DialogFragment;

import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.adapters.AutoCompleteAdapter;
import com.kenbie.adapters.ReportAbuseSpinnerAdapter;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.model.LocationItem;
import com.kenbie.model.Option;
import com.kenbie.model.OptionsData;
import com.kenbie.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.internal.Util;

public class EditCastingPreferencesFragment extends BaseFragment implements View.OnClickListener, APIResponseHandler {
    private static TextView cvStartDate, cvEndDate;
    private AppCompatSpinner csGender, csTimeEnd, csTimeStart, csAgeEnd, csAgeStart, csType;
    private EditText feesEt, etAddress;
    private ArrayList<Option> castingTypeList, genderList, cAgeFromList, cAgeToList, cFromTimeList, cToTimeList;
    private ArrayList<OptionsData> categoriesList;
    private LayoutInflater mLayoutInflater;
    private AutoCompleteTextView csLocation;
    private AutoCompleteAdapter adapter;
    private ArrayList<LocationItem> locationItemArrayList;
    public static boolean isEdit;
    private TextView cAgeTitle, cTimeTitle;
    private int ageSel = 0, timeSel = 0;

    public EditCastingPreferencesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_casting_prefrences, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView backBtn = view.findViewById(R.id.m_back_button);
        backBtn.setOnClickListener(this);
        TextView screenTitle = view.findViewById(R.id.m_title);
        screenTitle.setTypeface(KenbieApplication.S_NORMAL);
        screenTitle.setText(mActivity.mPref.getString("139", "ADD PREFERENCES"));
        TextView saveBtn = view.findViewById(R.id.m_save_btn);
        saveBtn.setText(mActivity.mPref.getString("137", "NEXT"));
        saveBtn.setTypeface(KenbieApplication.S_NORMAL);
        saveBtn.setOnClickListener(this);

        TextView cType = view.findViewById(R.id.c_type);
        cType.setText(mActivity.mPref.getString("106", "Type"));
        cType.setTypeface(KenbieApplication.S_NORMAL);
        TextView cLocation = view.findViewById(R.id.c_location);
        cLocation.setText(mActivity.mPref.getString("39", "Location") + getString(R.string.asteriskred));
        cLocation.setTypeface(KenbieApplication.S_NORMAL);
        TextView cAge = view.findViewById(R.id.c_age);
        cAge.setText(mActivity.mPref.getString("67", "Age"));
        cAge.setTypeface(KenbieApplication.S_NORMAL);
        cAgeTitle = view.findViewById(R.id.c_age_title);
        cAgeTitle.setTypeface(KenbieApplication.S_NORMAL);
        cAgeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ageSel == 2) {
                    ageSel = 0;
                    cAgeTitle.setTag(0);
                    csAgeStart.setTag(0);
                    csAgeStart.setVisibility(View.VISIBLE);
                    cAgeTitle.setVisibility(View.GONE);
                    csAgeStart.setPrompt(mActivity.mPref.getString("67", "Age"));
                    ReportAbuseSpinnerAdapter adapter = new ReportAbuseSpinnerAdapter(mActivity, cAgeFromList, 0);
                    csAgeStart.setAdapter(adapter);
                    csAgeStart.performClick();
                }
            }
        });

        cTimeTitle = view.findViewById(R.id.c_time_title);
        cTimeTitle.setTypeface(KenbieApplication.S_NORMAL);
        cTimeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeSel == 2) {
                    timeSel = 0;
                    cTimeTitle.setTag(0);
                    csTimeStart.setTag(0);
                    csTimeStart.setVisibility(View.VISIBLE);
                    cTimeTitle.setVisibility(View.GONE);
                    csTimeStart.setPrompt(mActivity.mPref.getString("109", "Time"));
                    ReportAbuseSpinnerAdapter adapter = new ReportAbuseSpinnerAdapter(mActivity, cFromTimeList, 0);
                    csTimeStart.setAdapter(adapter);
                    csTimeStart.performClick();
                }
            }
        });

        TextView cTo = view.findViewById(R.id.c_to);
        cTo.setText(mActivity.mPref.getString("323", "to"));
        cTo.setTypeface(KenbieApplication.S_NORMAL);
        TextView cTime = view.findViewById(R.id.c_time);
        cTime.setText(mActivity.mPref.getString("109", "Time"));
        cTime.setTypeface(KenbieApplication.S_NORMAL);
        TextView cTimeTo = view.findViewById(R.id.c_time_to);
        cTimeTo.setText(mActivity.mPref.getString("323", "to"));
        cTimeTo.setTypeface(KenbieApplication.S_NORMAL);
        TextView cFees = view.findViewById(R.id.c_fees);
        cFees.setText(mActivity.mPref.getString("111", "Fees"));
        cFees.setTypeface(KenbieApplication.S_NORMAL);
        TextView cGender = view.findViewById(R.id.c_gender);
        cGender.setText(mActivity.mPref.getString("69", "Gender"));
        cGender.setTypeface(KenbieApplication.S_NORMAL);
        TextView cStartDate = view.findViewById(R.id.c_start_date);
        cStartDate.setText(mActivity.mPref.getString("140", "Start Date") + getString(R.string.asteriskred));
        cStartDate.setTypeface(KenbieApplication.S_NORMAL);
        TextView cEndDate = view.findViewById(R.id.c_end_date);
        cEndDate.setText(mActivity.mPref.getString("141", "Closing Date") + getString(R.string.asteriskred));
        cEndDate.setTypeface(KenbieApplication.S_NORMAL);
        TextView cAddress = view.findViewById(R.id.c_address);
        cAddress.setText(mActivity.mPref.getString("142", "Address"));
        cAddress.setTypeface(KenbieApplication.S_NORMAL);

        feesEt = (EditText) view.findViewById(R.id.et_fees);
        feesEt.setTypeface(KenbieApplication.S_NORMAL);
        feesEt.setHint(mActivity.mPref.getString("111", "Fees"));

        csType = (AppCompatSpinner) view.findViewById(R.id.cs_type);
        csType.setPrompt(mActivity.mPref.getString("106", "Type"));
        csType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((ReportAbuseSpinnerAdapter) csType.getAdapter()).updateSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        csLocation = (AutoCompleteTextView) view.findViewById(R.id.cs_location);
        csLocation.setTypeface(KenbieApplication.S_NORMAL);
        csLocation.setHint(mActivity.mPref.getString("39", "Location"));
        csLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationItem cityValue = adapter.getItem(position);
                csLocation.setText(cityValue.getCity() + ", " + cityValue.getCountryName());
                csLocation.setSelection(csLocation.getText().toString().length());
                csLocation.setTag(cityValue);
                mActivity.hideKeyboard(mActivity);
            }
        });

        csLocation.addTextChangedListener(new TextWatcher() {
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

        csAgeStart = (AppCompatSpinner) view.findViewById(R.id.cs_age_start);
        csAgeStart.setPrompt(mActivity.mPref.getString("67", "Age"));
        csAgeStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                ((ReportAbuseSpinnerAdapter) csAgeStart.getAdapter()).updateSelection(position);
                if (position != 0) {
                    if (ageSel == 0) {
                        ageSel = 1;
                        cAgeTitle.setVisibility(View.VISIBLE);
                        cAgeTitle.setText(cAgeFromList.get(position).getTitle() + " " + mActivity.mPref.getString("323", "to") + " ");
                        cAgeTitle.setTag(cAgeFromList.get(position).getId());
                        cAgeToList = bindRemainingAgeData(position, cAgeFromList);
                        csAgeStart.setPrompt(mActivity.mPref.getString("67", "Age") + " " + mActivity.mPref.getString("323", "to"));
                        ReportAbuseSpinnerAdapter adapter = new ReportAbuseSpinnerAdapter(mActivity, cAgeToList, 0);
                        csAgeStart.setAdapter(adapter);
                        csAgeStart.performClick();
                    } else if (ageSel == 1) {
                        ageSel = 2;
                        cAgeTitle.setVisibility(View.VISIBLE);
                        cAgeTitle.setText(cAgeTitle.getText().toString() + "" + cAgeToList.get(position).getTitle());
                        csAgeStart.setTag(cAgeToList.get(position).getId());
                        csAgeStart.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        csAgeEnd = (AppCompatSpinner) view.findViewById(R.id.cs_age_end);
        csTimeStart = (AppCompatSpinner) view.findViewById(R.id.cs_time_start);
        csTimeStart.setPrompt(mActivity.mPref.getString("109", "Time"));
        csTimeStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    if (timeSel == 0) {
                        timeSel = 1;
                        cTimeTitle.setVisibility(View.VISIBLE);
                        cTimeTitle.setText(cFromTimeList.get(position).getTitle() + " " + mActivity.mPref.getString("323", "to") + " ");
                        cTimeTitle.setTag(cFromTimeList.get(position).getId());
                        cToTimeList = bindRemainingAgeData(position, cFromTimeList);
                        csTimeStart.setPrompt(mActivity.mPref.getString("109", "Time") + " " + mActivity.mPref.getString("323", "to"));
                        ReportAbuseSpinnerAdapter adapter = new ReportAbuseSpinnerAdapter(mActivity, cToTimeList, 0);
                        csTimeStart.setAdapter(adapter);
                        csTimeStart.performClick();
                    } else if (timeSel == 1) {
                        timeSel = 2;
                        cTimeTitle.setVisibility(View.VISIBLE);
                        cTimeTitle.setText(cTimeTitle.getText().toString() + "" + cToTimeList.get(position).getTitle());
                        csTimeStart.setTag(cToTimeList.get(position).getId());
                        csTimeStart.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        csTimeEnd = (AppCompatSpinner) view.findViewById(R.id.cs_time_end);
        csGender = (AppCompatSpinner) view.findViewById(R.id.cs_gender);
        csGender.setPrompt(mActivity.mPref.getString("69", "Gender"));
        csGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((ReportAbuseSpinnerAdapter) csGender.getAdapter()).updateSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

       /* TextView saveBtn = (TextView) view.findViewById(R.id.save_btn);
        saveBtn.setText(mActivity.mPref.getString("137", "NEXT"));
        saveBtn.setTypeface(KenbieApplication.S_NORMAL);
        saveBtn.setOnClickListener(this);*/

        cvStartDate = (TextView) view.findViewById(R.id.cv_start_date);
        cvStartDate.setHint(mActivity.mPref.getString("94", "Select"));
        cvStartDate.setTypeface(KenbieApplication.S_NORMAL);
        cvStartDate.setOnClickListener(this);

        cvEndDate = (TextView) view.findViewById(R.id.cv_end_date);
        cvEndDate.setHint(mActivity.mPref.getString("94", "Select"));
        cvEndDate.setTypeface(KenbieApplication.S_NORMAL);
        cvEndDate.setOnClickListener(this);

        etAddress = (EditText) view.findViewById(R.id.et_address);
        etAddress.setTypeface(KenbieApplication.S_NORMAL);
        etAddress.setHint(mActivity.mPref.getString("142", "Address"));

        mLayoutInflater = LayoutInflater.from(mActivity);

        if (mActivity.castingParams != null && mActivity.castingParams.containsKey("casting_fees"))
            isEdit = true;
        else
            isEdit = false;

        if (isEdit)
            updateEditCastingDataOnView();


        gettingPreferencesData();
    }

    private ArrayList<Option> bindRemainingAgeData(int position, ArrayList<Option> data) {
        ArrayList<Option> values = new ArrayList<>();
        try {
            values.add(data.get(0));
            values.addAll(data.subList((position), data.size()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    private void updateEditCastingDataOnView() {
        try {
            csLocation.setText(mActivity.castingParams.get("casting_location"));
            feesEt.setText(mActivity.castingParams.get("casting_fees"));
            String sd = mActivity.castingParams.get("starting_date") + "-" + mActivity.castingParams.get("starting_month") + "-" + mActivity.castingParams.get("starting_year");
            cvStartDate.setTag(sd);
            cvStartDate.setText(sd);
            String ed = mActivity.castingParams.get("end_date") + "-" + mActivity.castingParams.get("end_month") + "-" + mActivity.castingParams.get("end_year");
            cvEndDate.setTag(ed);
            cvEndDate.setText(ed);
            etAddress.setText(mActivity.castingParams.get("casting_address"));
            LocationItem cityValue = new LocationItem();
            cityValue.setCountry(mActivity.castingParams.get("casting_country"));
            csLocation.setTag(cityValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gettingPreferencesData() {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));

            new MConnection().postRequestWithHttpHeaders(mActivity, "getCastingArrLists", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.m_back_button)
            mActivity.onBackPressed();
        else if (v.getId() == R.id.m_save_btn) {
            try {
                if (isInfoValid()) {
                    if (csType.getSelectedItemPosition() > 0)
                        mActivity.castingParams.put("casting_face", castingTypeList.get(csType.getSelectedItemPosition()).getId() + "");
                    else
                        mActivity.castingParams.put("casting_face", "");

                    mActivity.castingParams.put("casting_location", csLocation.getText().toString());

                    if (cAgeTitle.getTag() != null) {
                        int startAge = (int) cAgeTitle.getTag();
                        if (startAge != 0)
                            mActivity.castingParams.put("casting_from_age", startAge + "");
                        else
                            mActivity.castingParams.put("casting_from_age", "");
                    } else
                        mActivity.castingParams.put("casting_from_age", "");

                    if (csAgeStart.getTag() != null) {
                        int toAge = (int) csAgeStart.getTag();
                        if (toAge != 0)
                            mActivity.castingParams.put("casting_to_age", toAge + "");
                        else
                            mActivity.castingParams.put("casting_to_age", "");
                    } else
                        mActivity.castingParams.put("casting_to_age", "");


                   /* if (csAgeStart.getSelectedItemPosition() > 0)
                        mActivity.castingParams.put("casting_from_age", cAgeFromList.get(csAgeStart.getSelectedItemPosition()).getId() + "");
                    else
                        mActivity.castingParams.put("casting_from_age", "");

                    if (csAgeEnd.getSelectedItemPosition() > 0)
                        mActivity.castingParams.put("casting_to_age", cAgeToList.get(csAgeEnd.getSelectedItemPosition()).getId() + "");
                    else
                        mActivity.castingParams.put("casting_to_age", "");*/

                    if (cTimeTitle.getTag() != null) {
                        int startTime = (int) cTimeTitle.getTag();
                        if (startTime != 0)
                            mActivity.castingParams.put("casting_from_time", startTime + "");
                        else
                            mActivity.castingParams.put("casting_from_time", "");
                    } else
                        mActivity.castingParams.put("casting_from_time", "");

                    if (csTimeStart.getTag() != null) {
                        int endTime = (int) csTimeStart.getTag();
                        if (endTime != 0)
                            mActivity.castingParams.put("casting_to_time", endTime + "");
                        else
                            mActivity.castingParams.put("casting_to_time", "");
                    } else
                        mActivity.castingParams.put("casting_to_time", "");

                    mActivity.castingParams.put("casting_fees", feesEt.getText().toString());

                    if (csGender.getSelectedItemPosition() > 0)
                        mActivity.castingParams.put("casting_gender", genderList.get(csGender.getSelectedItemPosition()).getId() + "");
                    else
                        mActivity.castingParams.put("casting_gender", "");

                    // dd-MM-yyyy
                    String startDate = cvStartDate.getText().toString();
                    String[] sDate = startDate.split("-"); // year + "-" + (month + 1) + "-" + day
                    mActivity.castingParams.put("starting_date", sDate[0]);
                    mActivity.castingParams.put("starting_month", sDate[1]);
                    mActivity.castingParams.put("starting_year", sDate[2]);

                    String endDate = cvEndDate.getText().toString();
                    String[] eDate = endDate.split("-"); // year + "-" + (month + 1) + "-" + day
                    mActivity.castingParams.put("end_date", eDate[0]);
                    mActivity.castingParams.put("end_month", eDate[1]);
                    mActivity.castingParams.put("end_year", eDate[2]);

                    mActivity.castingParams.put("casting_address", etAddress.getText().toString());
                    LocationItem locationItem = (LocationItem) csLocation.getTag();
                    if (locationItem != null)
                        mActivity.castingParams.put("casting_country", locationItem.getCountry());

                    EditCastingCategoriesFragment categoriesFragment = new EditCastingCategoriesFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("Type", 25);
                    bundle.putSerializable("CastCategories", categoriesList);
                    categoriesFragment.setArguments(bundle);
                    mActivity.replaceFragment(categoriesFragment, true, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (v.getId() == R.id.cv_start_date) {
            DialogFragment newFragment = new DatePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Type", 1);
            newFragment.setArguments(bundle);
            newFragment.show(mActivity.getSupportFragmentManager(), "datePicker");
        } else if (v.getId() == R.id.cv_end_date) {
            DialogFragment newFragment = new DatePickerFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Type", 2);
            newFragment.setArguments(bundle);
            newFragment.show(mActivity.getSupportFragmentManager(), "datePicker");
        }
    }

    private boolean isInfoValid() {
        /*if (csType.getSelectedItemPosition() < 0) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("304","The Tell us who you are ? field is required."));
            return false;
        } else if (csAgeStart.getSelectedItemPosition() < 0) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("325","The Starting Date field is required."));
            return false;
        } else if (csAgeEnd.getSelectedItemPosition() < 0) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("326","The Closing Date field is required."));
            return false;
        } else if (csTimeStart.getSelectedItemPosition() < 0) { // TODO Missing
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), "Please select start time.");
            return false;
        } else if (csTimeEnd.getSelectedItemPosition() < 0) { // TODO Missing
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), "Please select end time.");
            return false;
        } else if (feesEt.getText().toString().equalsIgnoreCase("")) { // TODO Missing
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), "Please enter fees");
            return false;
        } else if (csGender.getSelectedItemPosition() < 0) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("325","The Starting Date field is required."));
            return false;
        } else if (etAddress.getText().toString().equalsIgnoreCase("")) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("305","The Gender field is required."));
            return false;
        }

        */

        LocationItem locationItem = (LocationItem) csLocation.getTag();
        if (csLocation.getText().toString().equalsIgnoreCase("") || locationItem == null) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("46", "The Location field is required."));
            return false;
        } else if (cvStartDate.getText().toString().equalsIgnoreCase("")) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("325", "The Starting Date field is required."));
            return false;
        } else if ((String) cvStartDate.getTag() == null) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("325", "The Starting Date field is required."));
            return false;
        } else if ((String)  cvEndDate.getTag() == null) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("326", "The Closing Date field is required."));
            return false;
        } else if (cvEndDate.getText().toString().equalsIgnoreCase("")) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("326", "The Closing Date field is required."));
            return false;
        } else if (compareDates()) {
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("326", "The Closing Date field is required."));
            return false;
        }

        return true;
    }

    private boolean compareDates() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date strDate = sdf.parse((String) cvStartDate.getTag());
            Date endDate = sdf.parse((String) cvEndDate.getTag());
            if (strDate.after(endDate))
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
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
                    if (jo.has("data")) {
                        updateDataOnView(jo.getString("data"));
                    } else if (jo.has("error"))
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("error"));
                    else
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                } else if (APICode == 102) {
                    if (jo.has("data")) {
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
                    }
                    bindData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mActivity.showProgressDialog(false);
    }


    private void bindData() {
        if (adapter == null) {
//            adapter = new AutoCompleteAdapter(activity, android.R.layout.simple_dropdown_item_1line, locationItemArrayList);
            adapter = new AutoCompleteAdapter(mActivity, android.R.layout.simple_spinner_dropdown_item, locationItemArrayList);
            csLocation.setAdapter(adapter);
        } else {
            adapter.refreshData(locationItemArrayList);
        }
    }

    private void updateDataOnView(String data) {
        castingTypeList = new ArrayList<>();
        try {
            JSONObject jo = new JSONObject(data);
            if (jo.has("casting_type")) {
                castingTypeList = gettingOptionData(jo.getString("casting_type"));
                int selPos = isEdit ? getValueIndex(castingTypeList, mActivity.castingParams.get("casting_face")) : 0;
                ReportAbuseSpinnerAdapter adapter = new ReportAbuseSpinnerAdapter(mActivity, castingTypeList, selPos);
                csType.setAdapter(adapter);
                csType.setSelection(selPos);

//                    csType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position1, long id) {
//                            mListeners.getInfoValue(position, position1);
//                            mData.get(position).setImgId(position1);
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//
//                        }
//                    });
//                    csType.setSelection(mData.get(position).getImgId());


            }

            if (jo.has("gender")) {
                genderList = gettingOptionData(jo.getString("gender"));
                int selAge = isEdit ? getValueIndex(genderList, mActivity.castingParams.get("casting_gender")) : 0;
                ReportAbuseSpinnerAdapter adapter = new ReportAbuseSpinnerAdapter(mActivity, genderList, selAge);
                csGender.setAdapter(adapter);
                csGender.setSelection(selAge);
            }

            if (jo.has("casting_from_age")) {
                cAgeFromList = gettingOptionData(jo.getString("casting_from_age"));
                csAgeStart.setPrompt(mActivity.mPref.getString("67", "Age"));
                ReportAbuseSpinnerAdapter adapter = new ReportAbuseSpinnerAdapter(mActivity, cAgeFromList, 0);
                csAgeStart.setAdapter(adapter);

                if (isEdit) {
                    int ageIndex = getValueIndex(cAgeFromList, mActivity.castingParams.get("casting_from_age"));
                    if (ageIndex != 0) {
                        ageSel = 1;
                        cAgeTitle.setVisibility(View.VISIBLE);
                        cAgeTitle.setTag(cAgeFromList.get(ageIndex).getId());
                        cAgeTitle.setText(cAgeFromList.get(ageIndex).getTitle() + " " + mActivity.mPref.getString("323", "to") + " ");
                        csAgeStart.setVisibility(View.VISIBLE);
                    }
                }

                //                if (isEdit)
//                    csAgeStart.setSelection(getValueIndex(cAgeFromList, mActivity.castingParams.get("casting_from_age")));
            }


            if (jo.has("casting_to_age")) {
                cAgeToList = gettingOptionData(jo.getString("casting_from_age"));
//                InfoSpinnerAdapter adapter = new InfoSpinnerAdapter(mLayoutInflater, cAgeToList);
//                csAgeEnd.setAdapter(adapter);

                if (isEdit) {
                    int toAgeIndex = getValueIndex(cAgeToList, mActivity.castingParams.get("casting_to_age"));
                    if (toAgeIndex != 0) {
                        ageSel = 2;
                        cAgeTitle.setVisibility(View.VISIBLE);
                        csAgeStart.setVisibility(View.INVISIBLE);
                        csAgeStart.setTag(cAgeToList.get(toAgeIndex).getId());
                        cAgeTitle.setText(cAgeTitle.getText().toString() + cAgeToList.get(toAgeIndex).getTitle());
                    }
                }

/*                if (isEdit)
                    csAgeEnd.setSelection(getValueIndex(cAgeToList, mActivity.castingParams.get("casting_to_age")));*/
            }

            if (jo.has("casting_from_time")) {
                cFromTimeList = gettingOptionData(jo.getString("casting_from_time"));
                csTimeStart.setPrompt(mActivity.mPref.getString("109", "Time"));
                ReportAbuseSpinnerAdapter adapter = new ReportAbuseSpinnerAdapter(mActivity, cFromTimeList, 0);
                csTimeStart.setAdapter(adapter);

                int timeIndex = getValueIndex(cFromTimeList, mActivity.castingParams.get("casting_from_time"));
                if (timeIndex != 0) {
                    timeSel = 1;
                    cTimeTitle.setVisibility(View.VISIBLE);
                    cTimeTitle.setTag(cFromTimeList.get(timeIndex).getId());
                    cTimeTitle.setText(cFromTimeList.get(timeIndex).getTitle() + " " + mActivity.mPref.getString("323", "to") + " ");
                    csTimeStart.setVisibility(View.VISIBLE);
                }

                if (isEdit) {
                    int toTimeIndex = getValueIndex(cFromTimeList, mActivity.castingParams.get("casting_to_time"));
                    if (toTimeIndex != 0) {
                        timeSel = 2;
                        cTimeTitle.setVisibility(View.VISIBLE);
                        csTimeStart.setVisibility(View.INVISIBLE);
                        csTimeStart.setTag(cFromTimeList.get(toTimeIndex).getId());
                        cTimeTitle.setText(cTimeTitle.getText().toString() + cFromTimeList.get(toTimeIndex).getTitle());
                    }
                }

/*                if (isEdit)
                    csTimeStart.setSelection(getValueIndex(cFromTimeList, mActivity.castingParams.get("casting_from_time")));
                csTimeEnd.setAdapter(adapter);
                if (isEdit)
                    csTimeEnd.setSelection(getValueIndex(cFromTimeList, mActivity.castingParams.get("casting_to_time")));*/
            }

            categoriesList = parseOptionData(jo.getString("categories"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getValueIndex(ArrayList<Option> castingTypeList, String casting_face) {
        try {
            if (casting_face != null && casting_face.length() > 0) {
                for (int i = 0; i < castingTypeList.size(); i++) {
                    if (casting_face.equalsIgnoreCase(castingTypeList.get(i).getId() + ""))
                        return i;
                }
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }


    private ArrayList<Option> gettingOptionData(String casting_type) {
        ArrayList<Option> values = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(casting_type);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jType = new JSONObject(jsonArray.getString(i));
                Option op = new Option();
                op.setId(jType.getInt("id"));
                op.setTitle(jType.getString("name"));
                values.add(op);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }

    private ArrayList<OptionsData> parseOptionData(String categoryData) {
        ArrayList<OptionsData> values = new ArrayList<>();
        try {
            ArrayList<Integer> selCategories = getCategories(mActivity.castingParams.get("casting_categories"));
            JSONArray jsonArray = new JSONArray(categoryData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jType = new JSONObject(jsonArray.getString(i));
                OptionsData op = new OptionsData();
                op.setId(jType.getInt("id"));
                op.setName(jType.getString("name"));
                if (selCategories.indexOf(op.getId()) != -1)
                    op.setActive(true);
                values.add(op);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }

    private ArrayList<Integer> getCategories(String casting_categories) {
        ArrayList<Integer> values = new ArrayList<>();
        try {
            if (casting_categories != null && casting_categories.length() > 0) {
                String[] categories = casting_categories.replace(",", "-").split("-");
                for (int i = 0; i < categories.length; i++) {
                    values.add(Integer.valueOf(categories[i]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }


    @Override
    public void networkError(String error, int APICode) {
        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        mActivity.showProgressDialog(false);
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(30, mActivity.mPref.getString("139", "ADD PREFERENCES"), false, true, true);
        super.onResume();
    }

    // Date picker fragment
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        int type = 1;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null)
                type = getArguments().getInt("Type", 1);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            if (isEdit) {
                try {
                    if (type == 1) {
                        String startDate = (String) cvStartDate.getTag(); // dd-MM-yyyy || yyyy-mm-dd
                        if (startDate != null && startDate.length() > 2) {
                            String[] sDate = startDate.split("-"); // year + "-" + (month + 1) + "-" + day
                            day = Integer.valueOf(sDate[0]);
                            month = Integer.valueOf(sDate[1]) - 1;
                            year = Integer.valueOf(sDate[2]);
                        }
                    } else {
                        String endDate = (String) cvEndDate.getTag();
                        if (endDate != null && endDate.length() > 2) {
                            String[] eDate = endDate.split("-"); // year + "-" + (month + 1) + "-" + day
                            day = Integer.valueOf(eDate[0]);
                            month = Integer.valueOf(eDate[1]) - 1;
                            year = Integer.valueOf(eDate[2]);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    R.style.datepicker, this, year, month, day);

            datepickerdialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

//            DatePickerDialog datePicker = new DatePickerDialog(mActivity, this, year, month, day);
//            datepickerdialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datepickerdialog.setTitle("");
            // Create a new instance of DatePickerDialog and return it
            return datepickerdialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            String startDate = day + "-" + (month + 1) + "-" + year;

            // dd-MM-yyyy
            if (type == 1) {
                cvStartDate.setTag(startDate);
                cvStartDate.setText(Utility.getDateFormat(startDate));
//                cvStartDate.setText(year + "-" + (month + 1) + "-" + day);
            }else if (type == 2) {
                cvEndDate.setTag(startDate);
                cvEndDate.setText(Utility.getDateFormat(startDate));
            }
        }
    }
}
