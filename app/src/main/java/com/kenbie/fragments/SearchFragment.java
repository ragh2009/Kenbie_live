package com.kenbie.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.widget.NestedScrollView;

import com.kenbie.KenbieApplication;
import com.kenbie.KenbieNavigationActivity;
import com.kenbie.R;
import com.kenbie.adapters.AutoCompleteAdapter;
import com.kenbie.adapters.SearchSingleChoiceAdapter;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.model.LocationItem;
import com.kenbie.model.OptionsData;
import com.kenbie.model.UserItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SearchFragment extends BaseFragment implements View.OnClickListener, APIResponseHandler {
    private TextView resetAll, sModels, sAgencies, sPhotographers, withinValue, ageValueDisplay, genderGuys, genderGirls, genderBoth, btnAdvancedSearch, aNewFace, aProf, aCelebrity, aHeightValue;
    private EditText sName;
    private AutoCompleteTextView sLoc;
    private AppCompatSpinner aCategoryValue, aDisciplineValue, aWeight1, aWeightTo, aBust1, aBustTo, aWaist1, aWaistTo, aHips1, aHipsTo, aDress1, aDressTo, aEyesColorValue, aHairColorValue, aEthnicityValue, aLanguageValue;
    private SeekBar progressAge, progressWithin, aProgressHeight;
    private LinearLayout advanceLayout;
    private ArrayList<LocationItem> locationSearchData;
    private ArrayList<OptionsData> categoryFields, disciplineFields, weightFields, weightFieldsTo, bustFields, bustFieldsTo, waistFields, waistFieldsTo, hipsFields, hipsFieldsTo, dressFields, dressFieldsTo, eyeColorFields, hairColorFields, ethnicityFields, languageFields;
    private AutoCompleteAdapter adapter;
    private int userType, genderType, facesType;
    private String distance, years, cmTitle;
    private NestedScrollView searchScroll;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        // Required empty public constructor
        return new SearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        distance = kActivity.mPref.getString("66", "KM");
        years = kActivity.mPref.getString("68", "years");
        cmTitle = kActivity.mPref.getString("84", "cm");

        ImageView backBtn = view.findViewById(R.id.back_button);
        backBtn.setOnClickListener(this);

        searchScroll = view.findViewById(R.id.search_scroll);

        TextView screenTitle = view.findViewById(R.id.m_title);
        screenTitle.setTypeface(KenbieApplication.S_NORMAL);
        screenTitle.setText(kActivity.mPref.getString("57", "SEARCH"));

        TextView actionApply = view.findViewById(R.id.action_apply);
        actionApply.setTypeface(KenbieApplication.S_SEMI_BOLD);
        actionApply.setText(kActivity.mPref.getString("58", "Search")); // APPLY
        actionApply.setOnClickListener(this);
        actionApply.setVisibility(View.INVISIBLE);

        TextView sFilterBy = view.findViewById(R.id.s_filter_by);
        sFilterBy.setText(kActivity.mPref.getString("60", "Filter By"));
        sFilterBy.setTypeface(KenbieApplication.S_NORMAL);

        resetAll = (TextView) view.findViewById(R.id.s_reset_all);
        resetAll.setText(kActivity.mPref.getString("59", "RESET ALL"));
        resetAll.setTypeface(KenbieApplication.S_NORMAL);
        resetAll.setOnClickListener(this);

        sModels = (TextView) view.findViewById(R.id.s_models);
        sModels.setText(kActivity.mPref.getString("53", "Models"));
        sModels.setTypeface(KenbieApplication.S_NORMAL);
        sModels.setOnClickListener(this);

        sAgencies = (TextView) view.findViewById(R.id.s_agencies);
        sAgencies.setText(kActivity.mPref.getString("61", "Agencies"));
        sAgencies.setTypeface(KenbieApplication.S_NORMAL);
        sAgencies.setOnClickListener(this);

        sPhotographers = (TextView) view.findViewById(R.id.s_photographers);
        sPhotographers.setText(kActivity.mPref.getString("62", "Photographers"));
        sPhotographers.setTypeface(KenbieApplication.S_NORMAL);
        sPhotographers.setOnClickListener(this);

        TextView tSearch = view.findViewById(R.id.t_search);
        tSearch.setText(kActivity.mPref.getString("58", "Search"));
        tSearch.setTypeface(KenbieApplication.S_NORMAL);

        sName = (EditText) view.findViewById(R.id.s_name);
        sName.setHint(kActivity.mPref.getString("63", "Search by name"));
        sName.setTypeface(KenbieApplication.S_NORMAL);

        TextView tLocation = view.findViewById(R.id.t_location);
        tLocation.setText(kActivity.mPref.getString("39", "Location"));
        tLocation.setTypeface(KenbieApplication.S_NORMAL);

        sLoc = (AutoCompleteTextView) view.findViewById(R.id.s_loc);
        sLoc.setHint(kActivity.mPref.getString("64", "Enter Location"));
        sLoc.setTypeface(KenbieApplication.S_NORMAL);
        sLoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LocationItem cityValue = adapter.getItem(position);
                sLoc.setText(cityValue.getCity() + ", " + cityValue.getStateProv() + ", " + cityValue.getCountryName());
                sLoc.setTag(cityValue);
                sLoc.setSelection(sLoc.getText().toString().trim().length());
            }
        });

        sLoc.addTextChangedListener(new TextWatcher() {
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

        TextView sWithin = view.findViewById(R.id.s_within);
        sWithin.setText(kActivity.mPref.getString("65", "Within"));
        sWithin.setTypeface(KenbieApplication.S_NORMAL);

        withinValue = (TextView) view.findViewById(R.id.s_within_value);
        withinValue.setTypeface(KenbieApplication.S_NORMAL);
        withinValue.setText("0 " + distance + " - 200 " + distance);

        progressWithin = (SeekBar) view.findViewById(R.id.progress_within);
        progressWithin.setMax(200); // Max 100 KM
        progressWithin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
//                withinValue.setText(progress + " KM - 200 KM");
                withinValue.setText(progress + " " + distance + " - 200 " + distance);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        TextView sAgeTitle = view.findViewById(R.id.s_age_title);
        sAgeTitle.setText(kActivity.mPref.getString("67", "Age"));
        sAgeTitle.setTypeface(KenbieApplication.S_NORMAL);

        ageValueDisplay = (TextView) view.findViewById(R.id.s_age_value);
        ageValueDisplay.setTypeface(KenbieApplication.S_NORMAL);
        ageValueDisplay.setText("16 " + years + " - 99 " + years);

        progressAge = (SeekBar) view.findViewById(R.id.progress_age);
        progressAge.setMax(83); // Max 54 counts
        progressAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                ageValueDisplay.setText((16 + progress) + " " + years + " - 99 " + years);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        TextView sGender = view.findViewById(R.id.s_gender);
        sGender.setText(kActivity.mPref.getString("69", "Gender"));
        sGender.setTypeface(KenbieApplication.S_NORMAL);

        genderGuys = (TextView) view.findViewById(R.id.g_guys);
        genderGuys.setText(kActivity.mPref.getString("70", "Guys"));
        genderGuys.setTypeface(KenbieApplication.S_NORMAL);
        genderGuys.setOnClickListener(this);

        genderGirls = (TextView) view.findViewById(R.id.g_girls);
        genderGirls.setText(kActivity.mPref.getString("71", "Girls"));
        genderGirls.setTypeface(KenbieApplication.S_NORMAL);
        genderGirls.setOnClickListener(this);

        genderBoth = (TextView) view.findViewById(R.id.g_both);
        genderBoth.setText(kActivity.mPref.getString("72", "Both"));
        genderBoth.setTypeface(KenbieApplication.S_NORMAL);
        genderBoth.setOnClickListener(this);

        btnAdvancedSearch = (TextView) view.findViewById(R.id.btn_advanced_search);
        btnAdvancedSearch.setText(kActivity.mPref.getString("73", "Advance Search"));
        btnAdvancedSearch.setTypeface(KenbieApplication.S_NORMAL);
        btnAdvancedSearch.setOnClickListener(this);

        advanceLayout = view.findViewById(R.id.advance_layout);

        TextView aFilterBy = view.findViewById(R.id.a_filter_by);
        aFilterBy.setText(kActivity.mPref.getString("75", "I am search here"));
        aFilterBy.setTypeface(KenbieApplication.S_NORMAL);

        aNewFace = (TextView) view.findViewById(R.id.a_new_face);
        aNewFace.setText(kActivity.mPref.getString("76", "New Face"));
        aNewFace.setTypeface(KenbieApplication.S_NORMAL);
        aNewFace.setOnClickListener(this);

        aProf = (TextView) view.findViewById(R.id.a_prof);
        aProf.setText(kActivity.mPref.getString("77", "Professional"));
        aProf.setTypeface(KenbieApplication.S_NORMAL);
        aProf.setOnClickListener(this);

        aCelebrity = (TextView) view.findViewById(R.id.a_celebrity);
        aCelebrity.setText(kActivity.mPref.getString("78", "Celebrity"));
        aCelebrity.setTypeface(KenbieApplication.S_NORMAL);
        aCelebrity.setOnClickListener(this);

        TextView aCategory = view.findViewById(R.id.a_category);
        aCategory.setText(kActivity.mPref.getString("79", "Category"));
        aCategory.setTypeface(KenbieApplication.S_NORMAL);

        aCategoryValue = (AppCompatSpinner) view.findViewById(R.id.a_category_value);
        aCategoryValue.setPrompt(kActivity.mPref.getString("79", "Category"));
        aCategoryValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((SearchSingleChoiceAdapter) aCategoryValue.getAdapter()).updateSelection(position);
//                aCategoryValue.setTag(categoryFields.get(position));
//                aCategoryValue.setText(categoryFields.get(i).getName());
//                aCategoryValue.setSelection(aCategoryValue.getText().toString().trim().length());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        addedAutoCompleteTextViewProperty(aCategoryValue, 0);


        TextView aDisciplineTitle = view.findViewById(R.id.a_discipline_title);
        aDisciplineTitle.setText(kActivity.mPref.getString("81", "Discipline"));
        aDisciplineTitle.setTypeface(KenbieApplication.S_NORMAL);

        aDisciplineValue = (AppCompatSpinner) view.findViewById(R.id.a_discipline_value);
        aDisciplineValue.setPrompt(kActivity.mPref.getString("81", "Discipline"));
        aDisciplineValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((SearchSingleChoiceAdapter) aDisciplineValue.getAdapter()).updateSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView aHeightTitle = view.findViewById(R.id.a_height_title);
        aHeightTitle.setText(kActivity.mPref.getString("83", "Height"));
        aHeightTitle.setTypeface(KenbieApplication.S_NORMAL);

        aHeightValue = (TextView) view.findViewById(R.id.a_height_value);
        aHeightValue.setTypeface(KenbieApplication.S_NORMAL);
        aHeightValue.setText("120 " + cmTitle + " - 210 " + cmTitle);
        aProgressHeight = (SeekBar) view.findViewById(R.id.a_progress_height);
        aProgressHeight.setMax(90); // Max 90 counts
        aProgressHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                aHeightValue.setText((120 + progress) + " " + cmTitle + " - 210 " + cmTitle);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        TextView aWeightTitle = view.findViewById(R.id.a_weight_title);
        aWeightTitle.setText(kActivity.mPref.getString("85", "Weight"));
        aWeightTitle.setTypeface(KenbieApplication.S_NORMAL);

        aWeight1 = (AppCompatSpinner) view.findViewById(R.id.a_weight_1);
        aWeight1.setPrompt(kActivity.mPref.getString("85", "Weight"));
        aWeight1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((SearchSingleChoiceAdapter) aWeight1.getAdapter()).updateSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        aWeightTo = (AppCompatSpinner) view.findViewById(R.id.a_weight_to);
        aWeightTo.setPrompt(kActivity.mPref.getString("85", "Weight"));
        aWeightTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((SearchSingleChoiceAdapter) aWeightTo.getAdapter()).updateSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView aBustTitle = view.findViewById(R.id.a_bust_title);
        aBustTitle.setText(kActivity.mPref.getString("86", "Bust"));
        aBustTitle.setTypeface(KenbieApplication.S_NORMAL);

        aBust1 = (AppCompatSpinner) view.findViewById(R.id.a_bust_1);
        aBust1.setPrompt(kActivity.mPref.getString("86", "Bust"));
        aBust1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((SearchSingleChoiceAdapter) aBust1.getAdapter()).updateSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        aBustTo = (AppCompatSpinner) view.findViewById(R.id.a_bust_to);
        aBustTo.setPrompt(kActivity.mPref.getString("86", "Bust"));
        aBustTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((SearchSingleChoiceAdapter) aBustTo.getAdapter()).updateSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView aWaistTitle = view.findViewById(R.id.a_waist_title);
        aWaistTitle.setText(kActivity.mPref.getString("87", "Waist"));
        aWaistTitle.setTypeface(KenbieApplication.S_NORMAL);

        aWaist1 = (AppCompatSpinner) view.findViewById(R.id.a_waist_1);
        aWaist1.setPrompt(kActivity.mPref.getString("87", "Waist"));
        aWaist1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((SearchSingleChoiceAdapter) aWaist1.getAdapter()).updateSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        aWaistTo = (AppCompatSpinner) view.findViewById(R.id.a_waist_to);
        aWaistTo.setPrompt(kActivity.mPref.getString("87", "Waist"));
        aWaistTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((SearchSingleChoiceAdapter) aWaistTo.getAdapter()).updateSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView aHipsTitle = view.findViewById(R.id.a_hips_title);
        aHipsTitle.setText(kActivity.mPref.getString("88", "Hips"));
        aHipsTitle.setTypeface(KenbieApplication.S_NORMAL);

        aHips1 = (AppCompatSpinner) view.findViewById(R.id.a_hips_1);
        aHips1.setPrompt(kActivity.mPref.getString("88", "Hips"));
        aHips1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((SearchSingleChoiceAdapter) aHips1.getAdapter()).updateSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        aHipsTo = (AppCompatSpinner) view.findViewById(R.id.a_hips_to);
        aHipsTo.setPrompt(kActivity.mPref.getString("88", "Hips"));
        aHipsTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((SearchSingleChoiceAdapter) aHipsTo.getAdapter()).updateSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView aDressTitle = view.findViewById(R.id.a_dress_title);
        aDressTitle.setText(kActivity.mPref.getString("89", "Dress"));
        aDressTitle.setTypeface(KenbieApplication.S_NORMAL);

        aDress1 = (AppCompatSpinner) view.findViewById(R.id.a_dress_1);
        aDress1.setPrompt(kActivity.mPref.getString("89", "Dress"));
        aDress1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((SearchSingleChoiceAdapter) aDress1.getAdapter()).updateSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        aDressTo = (AppCompatSpinner) view.findViewById(R.id.a_dress_to);
        aDressTo.setPrompt(kActivity.mPref.getString("89", "Dress"));
        aDressTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((SearchSingleChoiceAdapter) aDressTo.getAdapter()).updateSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView aEyeColorTitle = view.findViewById(R.id.a_eye_color_title);
        aEyeColorTitle.setText(kActivity.mPref.getString("90", "Eye Colour"));
        aEyeColorTitle.setTypeface(KenbieApplication.S_NORMAL);

        aEyesColorValue = (AppCompatSpinner) view.findViewById(R.id.a_eyes_color_value);
        aEyesColorValue.setPrompt(kActivity.mPref.getString("90", "Eye Colour"));
        aEyesColorValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((SearchSingleChoiceAdapter) aEyesColorValue.getAdapter()).updateSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView aHairColorTitle = view.findViewById(R.id.a_hair_color_title);
        aHairColorTitle.setText(kActivity.mPref.getString("91", "Hair Colour"));
        aHairColorTitle.setTypeface(KenbieApplication.S_NORMAL);

        aHairColorValue = (AppCompatSpinner) view.findViewById(R.id.a_hair_color_value);
        aHairColorValue.setPrompt(kActivity.mPref.getString("91", "Hair Colour"));
        aHairColorValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((SearchSingleChoiceAdapter) aHairColorValue.getAdapter()).updateSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView aEthnicityTitle = view.findViewById(R.id.a_ethnicity_title);
        aEthnicityTitle.setText(kActivity.mPref.getString("92", "Ethnicity"));
        aEthnicityTitle.setTypeface(KenbieApplication.S_NORMAL);

        aEthnicityValue = (AppCompatSpinner) view.findViewById(R.id.a_ethnicity_value);
        aEthnicityValue.setPrompt(kActivity.mPref.getString("92", "Ethnicity"));
        aEthnicityValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((SearchSingleChoiceAdapter) aEthnicityValue.getAdapter()).updateSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView aLanguageTitle = view.findViewById(R.id.a_language_title);
        aLanguageTitle.setText(kActivity.mPref.getString("93", "Languages"));
        aLanguageTitle.setTypeface(KenbieApplication.S_NORMAL);

        aLanguageValue = (AppCompatSpinner) view.findViewById(R.id.a_language_value);
        aLanguageValue.setPrompt(kActivity.mPref.getString("93", "Languages"));
        aLanguageValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((SearchSingleChoiceAdapter) aLanguageValue.getAdapter()).updateSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView searchBtn = view.findViewById(R.id.search_btn);
        searchBtn.setText(kActivity.mPref.getString("57", "SEARCH"));
        searchBtn.setTypeface(KenbieApplication.S_SEMI_BOLD);
        searchBtn.setOnClickListener(this);
    }

    private void gettingSearchFieldsData() {
        if (kActivity.isOnline()) {
//            kActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
//            params.put("user_id", kActivity.mPref.getString("UserId", ""));
//            params.put("login_key", kActivity.mPref.getString("LoginKey", ""));
//            params.put("login_token", kActivity.mPref.getString("LoginToken", ""));
            kActivity.mConnection.postRequestWithHttpHeaders(kActivity, "getProfileDetail", this, params, 101);
        } else {
            kActivity.showProgressDialog(false);
            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void gettingLocationData(String searchStr) {
        try {
            Map<String, String> params = new HashMap<String, String>();
            if (searchStr == null) {
                params.put("ip", kActivity.ip);
            } else
                params.put("city_search", searchStr);
            params.put("device_id", kActivity.deviceId == null ? "" : kActivity.deviceId);
            kActivity.mConnection.postRequestWithHttpHeaders(kActivity, "getLocation", this, params, 102);
        } catch (Exception e) {
            Log.d("HUS", "EXCEPTION " + e);
        }
    }

    @Override
    public void getError(String error, int APICode) {
        if (APICode == 103) {
            if (error != null)
                if (error.equalsIgnoreCase("com.android.volley.error.AuthFailureError"))
                    kActivity.logoutProcess();
                else {
                    Intent intent = new Intent(kActivity, KenbieNavigationActivity.class);
//                    ModelsFragment modelsFragment = new ModelsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Models", new ArrayList<UserItem>());
                    bundle.putInt("DataType", 2);
                    bundle.putInt("NavType", 11);
                    bundle.putString("Error", error);
//                    modelsFragment.setArguments(bundle);
                    intent.putExtras(bundle);
                    startActivity(intent);
//                    kActivity.replaceFragment(modelsFragment, true, false);
                }
        } else if (APICode != 102) {
            if (error != null)
                if (error.equalsIgnoreCase("com.android.volley.error.AuthFailureError"))
                    kActivity.logoutProcess();
                else
                    kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), error);
            else
                kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("270", "Something Wrong! Please try later."));
        }

        kActivity.showProgressDialog(false);
    }

    @Override
    public void getResponse(String response, int APICode) {
        try {
            JSONObject jo = new JSONObject(response);

            if (APICode == 101) {
                if (jo.has("data")) {
                    parseFieldsData(jo.getString("data"));
                    kActivity.showProgressDialog(false);
                }
            } else if (APICode == 102) {
                JSONArray jsonArray = jo.getJSONArray("data");
                locationSearchData = new ArrayList<>();
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
                    locationSearchData.add(locationItem);
                }
                bindLocationSearchData();
            } else if (APICode == 103) {
                kActivity.hideKeyboard(kActivity);
                ArrayList<UserItem> searchUsersData = null;
                Bundle bundle = new Bundle();

                if (jo.has("data")) {
                    bundle.putString("Error", kActivity.mPref.getString("162", "Data is not found."));
                    searchUsersData = kActivity.getParseSearchData(jo.getString("data"), userType);
                } else
                    bundle.putString("Error", response);
//                    if (searchUsersData != null && searchUsersData.size() > 0) {
                if (searchUsersData == null)
                    searchUsersData = new ArrayList<>();
//                ModelsFragment modelsFragment = new ModelsFragment();
                bundle.putSerializable("Models", searchUsersData);
                bundle.putInt("DataType", 2);
//                modelsFragment.setArguments(bundle);
//                kActivity.replaceFragment(modelsFragment, true, false);

                Intent intent = new Intent(kActivity, KenbieNavigationActivity.class);
//                    ModelsFragment modelsFragment = new ModelsFragment();
                bundle.putInt("NavType", 11);
//                    modelsFragment.setArguments(bundle);
                intent.putExtras(bundle);
                startActivity(intent);

//                    }
//                    kActivity.showMessageWithTitle(kActivity, "Search", "No data found!");
//                } else
//                    kActivity.showMessageWithTitle(kActivity, "Search", "No data found!");
                kActivity.showProgressDialog(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void networkError(String error, int APICode) {
        kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("269", "Network failed! Please try later."));
        kActivity.showProgressDialog(false);
    }

    private void bindLocationSearchData() {
        if (adapter == null) {
//            adapter = new AutoCompleteAdapter(activity, android.R.layout.simple_dropdown_item_1line, locationItemArrayList);
            adapter = new AutoCompleteAdapter(kActivity, android.R.layout.simple_spinner_dropdown_item, locationSearchData);
            sLoc.setAdapter(adapter);
        } else {
            adapter.refreshData(locationSearchData);
        }
    }

    // Parsing form data
    private void parseFieldsData(String data) {
        try {
            JSONObject jo = new JSONObject(data);
            if (jo.has("list_options")) {
                JSONObject jFormData = jo.getJSONObject("list_options");
                categoryFields = getFieldsData(jFormData.getString("categories"));
                disciplineFields = getFieldsData(jFormData.getString("arrdisciplines"));
                weightFields = getFieldsData(jFormData.getString("weight"));
                weightFieldsTo = (ArrayList<OptionsData>) weightFields.clone();
                bustFields = getFieldsData(jFormData.getString("bust"));
                bustFieldsTo = (ArrayList<OptionsData>) bustFields.clone();
                waistFields = getFieldsData(jFormData.getString("waist"));
                waistFieldsTo = (ArrayList<OptionsData>) waistFields.clone();
                hipsFields = getFieldsData(jFormData.getString("hips"));
                hipsFieldsTo = (ArrayList<OptionsData>) hipsFields.clone();
                dressFields = getFieldsData(jFormData.getString("user_dress"));
                dressFieldsTo = (ArrayList<OptionsData>) dressFields.clone();
                eyeColorFields = getFieldsData(jFormData.getString("user_eyes"));
                hairColorFields = getFieldsData(jFormData.getString("user_hair_color"));
                ethnicityFields = getFieldsData(jFormData.getString("user_ethinicity"));
                languageFields = getFieldsData(jFormData.getString("lang_list"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        bindFieldData();
    }

    private void bindFieldData() {
        LayoutInflater mLayoutInflater = LayoutInflater.from(kActivity);

//        aCategoryValue.setAdapter(new FormAdapter(kActivity, R.layout.location_cell_view, categoryFields));
        aCategoryValue.setAdapter(new SearchSingleChoiceAdapter(mLayoutInflater, categoryFields, -1));
        aDisciplineValue.setAdapter(new SearchSingleChoiceAdapter(mLayoutInflater, disciplineFields, -1));
        aWeight1.setAdapter(new SearchSingleChoiceAdapter(mLayoutInflater, weightFields, -1));
        aWeightTo.setAdapter(new SearchSingleChoiceAdapter(mLayoutInflater, weightFieldsTo, -1));
        aBust1.setAdapter(new SearchSingleChoiceAdapter(mLayoutInflater, bustFields, -1));
        aBustTo.setAdapter(new SearchSingleChoiceAdapter(mLayoutInflater, bustFieldsTo, -1));
        aWaist1.setAdapter(new SearchSingleChoiceAdapter(mLayoutInflater, waistFields, -1));
        aWaistTo.setAdapter(new SearchSingleChoiceAdapter(mLayoutInflater, waistFieldsTo, -1));
        aHips1.setAdapter(new SearchSingleChoiceAdapter(mLayoutInflater, hipsFields, -1));
        aHipsTo.setAdapter(new SearchSingleChoiceAdapter(mLayoutInflater, hipsFieldsTo, -1));
        aDress1.setAdapter(new SearchSingleChoiceAdapter(mLayoutInflater, dressFields, -1));
        aDressTo.setAdapter(new SearchSingleChoiceAdapter(mLayoutInflater, dressFieldsTo, -1));
        aEyesColorValue.setAdapter(new SearchSingleChoiceAdapter(mLayoutInflater, eyeColorFields, -1));
        aHairColorValue.setAdapter(new SearchSingleChoiceAdapter(mLayoutInflater, hairColorFields, -1));
        aEthnicityValue.setAdapter(new SearchSingleChoiceAdapter(mLayoutInflater, ethnicityFields, -1));
        aLanguageValue.setAdapter(new SearchSingleChoiceAdapter(mLayoutInflater, languageFields, -1));

//        OLD Adapter
//        aLanguageValue.setAdapter(new FormAdapter(kActivity, R.layout.location_cell_view, languageFields));
    }

    private ArrayList<OptionsData> getFieldsData(String options) {
        ArrayList<OptionsData> values = new ArrayList<>();
        try {
            JSONArray userEthnicity = new JSONArray(options);
            for (int i = 0; i < userEthnicity.length(); i++) {
                OptionsData od = new OptionsData();
                JSONObject jod = new JSONObject(userEthnicity.getString(i));
                od.setId(jod.getInt("id"));
                od.setName(jod.getString("name"));
                if (jod.has("shortname"))
                    od.setOptionCode(jod.getString("shortname"));
                values.add(od);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.s_reset_all:
                kActivity.replaceFragment(new SearchFragment(), true, false);
                break;
            case R.id.s_models:
                if (userType == 1) {
                    userType = 0;
                    sModels.setBackgroundResource(R.color.white);
                    sModels.setTextColor(getResources().getColor(R.color.gray));
                } else {
                    userType = 1;
                    sModels.setBackgroundResource(R.color.red_g_color);
                    sModels.setTextColor(getResources().getColor(R.color.white));
                    sAgencies.setBackgroundResource(R.color.white);
                    sAgencies.setTextColor(getResources().getColor(R.color.gray));
                    sPhotographers.setBackgroundResource(R.color.white);
                    sPhotographers.setTextColor(getResources().getColor(R.color.gray));
                }
                break;
            case R.id.s_agencies:
                if (userType == 2) {
                    userType = 0;
                    sAgencies.setBackgroundResource(R.color.white);
                    sAgencies.setTextColor(getResources().getColor(R.color.gray));
                } else {
                    userType = 2;
                    sAgencies.setBackgroundResource(R.color.red_g_color);
                    sAgencies.setTextColor(getResources().getColor(R.color.white));
                    sModels.setBackgroundResource(R.color.white);
                    sModels.setTextColor(getResources().getColor(R.color.gray));
                    sPhotographers.setBackgroundResource(R.color.white);
                    sPhotographers.setTextColor(getResources().getColor(R.color.gray));
                }
                break;
            case R.id.s_photographers:
                if (userType == 3) {
                    userType = 0;
                    sPhotographers.setBackgroundResource(R.color.white);
                    sPhotographers.setTextColor(getResources().getColor(R.color.gray));
                } else {
                    userType = 3;
                    sPhotographers.setBackgroundResource(R.color.red_g_color);
                    sPhotographers.setTextColor(getResources().getColor(R.color.white));
                    sModels.setBackgroundResource(R.color.white);
                    sModels.setTextColor(getResources().getColor(R.color.gray));
                    sAgencies.setBackgroundResource(R.color.white);
                    sAgencies.setTextColor(getResources().getColor(R.color.gray));
                }
                break;
            case R.id.g_guys:
                if (genderType == 1) {
                    genderType = 0;
                    genderGuys.setBackgroundResource(R.color.white);
                    genderGuys.setTextColor(getResources().getColor(R.color.gray));
                } else {
                    genderType = 1;
                    genderGuys.setBackgroundResource(R.color.red_g_color);
                    genderGuys.setTextColor(getResources().getColor(R.color.white));
                    genderGirls.setBackgroundResource(R.color.white);
                    genderGirls.setTextColor(getResources().getColor(R.color.gray));
                    genderBoth.setBackgroundResource(R.color.white);
                    genderBoth.setTextColor(getResources().getColor(R.color.gray));
                }
                break;
            case R.id.g_girls:
                if (genderType == 2) {
                    genderType = 0;
                    genderGirls.setBackgroundResource(R.color.white);
                    genderGirls.setTextColor(getResources().getColor(R.color.gray));
                } else {
                    genderType = 2;
                    genderGirls.setBackgroundResource(R.color.red_g_color);
                    genderGirls.setTextColor(getResources().getColor(R.color.white));
                    genderGuys.setBackgroundResource(R.color.white);
                    genderGuys.setTextColor(getResources().getColor(R.color.gray));
                    genderBoth.setBackgroundResource(R.color.white);
                    genderBoth.setTextColor(getResources().getColor(R.color.gray));
                }
                break;
            case R.id.g_both:
                if (genderType == 3) {
                    genderType = 0;
                    genderBoth.setBackgroundResource(R.color.white);
                    genderBoth.setTextColor(getResources().getColor(R.color.gray));
                } else {
                    genderType = 3;
                    genderBoth.setBackgroundResource(R.color.red_g_color);
                    genderBoth.setTextColor(getResources().getColor(R.color.white));
                    genderGuys.setBackgroundResource(R.color.white);
                    genderGuys.setTextColor(getResources().getColor(R.color.gray));
                    genderGirls.setBackgroundResource(R.color.white);
                    genderGirls.setTextColor(getResources().getColor(R.color.gray));
                }
                break;
            case R.id.btn_advanced_search:
                // Advance search view enable/disable
                if (advanceLayout.getVisibility() == View.VISIBLE) {
                    btnAdvancedSearch.setText(kActivity.mPref.getString("73", "Advance Search"));
                    advanceLayout.setVisibility(View.GONE);
                } else {
                    btnAdvancedSearch.setText(kActivity.mPref.getString("74", "Advance Search Close"));
                    advanceLayout.setVisibility(View.VISIBLE);
                    focusOnView();
                    gettingSearchFieldsData();
                }
                break;
            case R.id.a_new_face:
                if (facesType == 1) {
                    facesType = 0;
                    aNewFace.setBackgroundResource(R.color.white);
                    aNewFace.setTextColor(getResources().getColor(R.color.gray));
                } else {
                    facesType = 1;
                    aNewFace.setBackgroundResource(R.color.red_g_color);
                    aNewFace.setTextColor(getResources().getColor(R.color.white));
                    aProf.setBackgroundResource(R.color.white);
                    aProf.setTextColor(getResources().getColor(R.color.gray));
                    aCelebrity.setBackgroundResource(R.color.white);
                    aCelebrity.setTextColor(getResources().getColor(R.color.gray));
                }
                break;
            case R.id.a_prof:
                if (facesType == 2) {
                    facesType = 0;
                    aProf.setBackgroundResource(R.color.white);
                    aProf.setTextColor(getResources().getColor(R.color.gray));
                } else {
                    facesType = 2;
                    aProf.setBackgroundResource(R.color.red_g_color);
                    aProf.setTextColor(getResources().getColor(R.color.white));
                    aNewFace.setBackgroundResource(R.color.white);
                    aNewFace.setTextColor(getResources().getColor(R.color.gray));
                    aCelebrity.setBackgroundResource(R.color.white);
                    aCelebrity.setTextColor(getResources().getColor(R.color.gray));
                }
                break;
            case R.id.a_celebrity:
                if (facesType == 3) {
                    facesType = 0;
                    aCelebrity.setBackgroundResource(R.color.white);
                    aCelebrity.setTextColor(getResources().getColor(R.color.gray));
                } else {
                    facesType = 3;
                    aCelebrity.setBackgroundResource(R.color.red_g_color);
                    aCelebrity.setTextColor(getResources().getColor(R.color.white));
                    aNewFace.setBackgroundResource(R.color.white);
                    aNewFace.setTextColor(getResources().getColor(R.color.gray));
                    aProf.setBackgroundResource(R.color.white);
                    aProf.setTextColor(getResources().getColor(R.color.gray));
                }
                break;
            case R.id.back_button:
                moveToBack();
                break;
            case R.id.search_btn:
                applySearch();
                break;
            default:
                break;
        }
    }

    public void moveToBack() {
        if (kActivity.mPref.getBoolean("isLogin", false))
            kActivity.searchBackHandling();
        else
            kActivity.onBackPressed();
    }

    private final void focusOnView() {
        searchScroll.post(new Runnable() {
            @Override
            public void run() {
                searchScroll.scrollTo(0, aDisciplineValue.getBottom());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isInit || isVisible()) {
            isInit = false;
            kActivity.updateActionBar(2, kActivity.mPref.getString("57", "SEARCH"), false, false, true);
            kActivity.hideKeyboard(kActivity);
        }
    }


    private void applySearch() {
        if (kActivity.isOnline()) {
            kActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            if (kActivity.mPref.getBoolean("isLogin", false)) {
                params.put("user_id", kActivity.mPref.getString("UserId", ""));
                params.put("login_key", kActivity.mPref.getString("LoginKey", ""));
                params.put("login_token", kActivity.mPref.getString("LoginToken", ""));
            }

            params.put("usertype", userType != 0 ? userType + "" : "");
            if (genderType == 3)
                params.put("gender", "1,2");
            else
                params.put("gender", genderType != 0 ? genderType + "" : "");

            params.put("within", progressWithin.getProgress() == 0 ? "" : progressWithin.getProgress() + "");
            if (sLoc.getText().toString().equalsIgnoreCase("")) {
                params.put("city", "");
                params.put("latitude", "");
                params.put("longitude", "");
            } else {
                LocationItem item = (LocationItem) sLoc.getTag();
                if (item != null && item.getCity() != null) {
                    params.put("city", item.getCity());
                    params.put("country", item.getCountryId() + "");
                    params.put("latitude", item.getLatitude() + "");
                    params.put("longitude", item.getLongitude() + "");
                } else {
                    params.put("city", "");
                    params.put("country", "");
                    params.put("latitude", "");
                    params.put("longitude", "");
                }

            }

            // Max 16-99....83steps
            params.put("minModelAge", (progressAge.getProgress() == 0 ? "" : "16"));
            params.put("maxModelAge", (progressAge.getProgress() == 0 ? "" : (16 + progressAge.getProgress()) + ""));
            params.put("fullname", sName.getText().toString());

            if (advanceLayout.getVisibility() == View.VISIBLE) {
                params.put("user_face", facesType != 0 ? facesType + "" : "");
                params.put("categories", aCategoryValue.getSelectedItemPosition() != 0 ? categoryFields.get(aCategoryValue.getSelectedItemPosition()).getId() + "" : "");
                params.put("discipline", aDisciplineValue.getSelectedItemPosition() != 0 ? disciplineFields.get(aDisciplineValue.getSelectedItemPosition()).getId() + "" : "");
                // Height - 120-> 210,  stps - 90
                params.put("fromheight", (aProgressHeight.getProgress() == 0 ? "" : "120"));
                params.put("toheight", (aProgressHeight.getProgress() == 0 ? "" : (120 + aProgressHeight.getProgress()) + ""));
                params.put("fromweight", aWeight1.getSelectedItemPosition() != 0 ? weightFields.get(aWeight1.getSelectedItemPosition()).getId() + "" : "");
                params.put("toweight", aWeightTo.getSelectedItemPosition() != 0 ? weightFieldsTo.get(aWeightTo.getSelectedItemPosition()).getId() + "" : "");
                params.put("frombust", aBust1.getSelectedItemPosition() != 0 ? bustFields.get(aBust1.getSelectedItemPosition()).getId() + "" : "");
                params.put("tobust", aBustTo.getSelectedItemPosition() != 0 ? bustFieldsTo.get(aBustTo.getSelectedItemPosition()).getId() + "" : "");
                params.put("fromwaist", aWaist1.getSelectedItemPosition() != 0 ? waistFields.get(aWaist1.getSelectedItemPosition()).getId() + "" : "");
                params.put("towaist", aWaistTo.getSelectedItemPosition() != 0 ? waistFieldsTo.get(aWaistTo.getSelectedItemPosition()).getId() + "" : "");
                params.put("fromdress", aDress1.getSelectedItemPosition() != 0 ? dressFields.get(aDress1.getSelectedItemPosition()).getId() + "" : "");
                params.put("todress", aDressTo.getSelectedItemPosition() != 0 ? dressFieldsTo.get(aDressTo.getSelectedItemPosition()).getId() + "" : "");
                params.put("haircolor", aHairColorValue.getSelectedItemPosition() != 0 ? hairColorFields.get(aHairColorValue.getSelectedItemPosition()).getId() + "" : "");
                params.put("user_eye_color", aEyesColorValue.getSelectedItemPosition() != 0 ? eyeColorFields.get(aEyesColorValue.getSelectedItemPosition()).getId() + "" : "");
                params.put("ethnicity", aEthnicityValue.getSelectedItemPosition() != 0 ? ethnicityFields.get(aEthnicityValue.getSelectedItemPosition()).getId() + "" : "");
                params.put("language", aLanguageValue.getSelectedItemPosition() != 0 ? languageFields.get(aLanguageValue.getSelectedItemPosition()).getId() + "" : "");
//                params.put("user_from_agency", "1");
//                params.put("sort", "1");
            } else {
                params.put("user_face", "");
                params.put("categories", "");
                params.put("discipline", "");
                params.put("fromheight", "");
                params.put("toheight", "");
                params.put("fromweight", "");
                params.put("toweight", "");
                params.put("frombust", "");
                params.put("tobust", "");
                params.put("fromwaist", "");
                params.put("towaist", "");
                params.put("fromdress", "");
                params.put("todress", "");
                params.put("haircolor", "");
                params.put("user_eye_color", "");
                params.put("ethnicity", "");
                params.put("language", "");
//                params.put("user_from_agency", "");
//                params.put("sort", "");
            }
            kActivity.searchParams = params;
            kActivity.mConnection.postRequestWithHttpHeaders(kActivity, "search/page/1", this, params, 103);
        } else {
            kActivity.showProgressDialog(false);
            kActivity.showMessageWithTitle(kActivity, kActivity.mPref.getString("20", "Alert!"), kActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private String getValueFromSpinner(AppCompatSpinner textValue) {
        String value = "";
        try {
            OptionsData optionsData = (OptionsData) textValue.getTag();
            if (optionsData != null)
                value = optionsData.getId() + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private String getValueFromAutoText(AutoCompleteTextView textValue) {
        String value = "";
        try {
            OptionsData optionsData = (OptionsData) textValue.getTag();
            if (optionsData != null)
                value = optionsData.getId() + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    public void hideKeyboard(Context context, AutoCompleteTextView mEditText) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
