package com.kenbie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.kenbie.connection.MConnection;
import com.kenbie.db.TranslateDataSource;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.util.Constants;

import java.util.ArrayList;

public class LanguageSelectActivity extends KenbieBaseActivity implements APIResponseHandler {
    private ArrayList<String> titleArray;
    private Spinner spinner = null;
    private TranslateDataSource mData;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_select);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        mData = new TranslateDataSource(getApplicationContext());
        mData.open();

        bindTitle();
        initUiSetup();
    }

    private void bindTitle() {
        titleArray = new ArrayList<>();
        titleArray.add("SELECT YOUR LANGUAGE");
        titleArray.add("SUBMIT");
    }

    private void initUiSetup() {
        try {
            mProgress = new ProgressDialog(this);
            mProgress.setMessage("Please wait...");
            mProgress.setIndeterminate(false);
            mProgress.setCancelable(true);
            mProgress.setCanceledOnTouchOutside(false);

            TextView languageTxt = (TextView) findViewById(R.id.title_sel_language);
            languageTxt.setText(titleArray.get(0));
            languageTxt.setTypeface(KenbieApplication.S_SEMI_LIGHT);

            spinner = (Spinner) findViewById(R.id.language_spinner);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.language_array, R.layout.spinner_item);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinner.setAdapter(adapter);

            TextView submitBtn = (TextView) findViewById(R.id.submit_btn);
            submitBtn.setTypeface(KenbieApplication.S_BOLD);
            submitBtn.setText(titleArray.get(1));
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gettingSelectedTitles((String) spinner.getAdapter().getItem(spinner.getSelectedItemPosition()));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gettingSelectedTitles(String lName) {
        if (isOnline()) {
            showProgressDialog(true);
            new MConnection().getRequestWithHttpHeaders(LanguageSelectActivity.this, "lang", this, 101);
        } else
            showMessageWithTitle(this, "Alert", Constants.NETWORK_FAIL_MSG);
    }

    @Override
    public void getError(String error, int APICode) {
        showProgressDialog(false);
    }

    @Override
    public void getResponse(String response, int APICode) {
        if (response != null) {
            if (mData.saveTitles(response)) {
                SharedPreferences.Editor editor = mPref.edit();
                editor.putBoolean("selLanguage", false);
                editor.putString("Language", (String) spinner.getAdapter().getItem(spinner.getSelectedItemPosition()));
                editor.apply();

                Intent i = null;
                if (mPref.getBoolean("isLogin", true))
                    i = new Intent(LanguageSelectActivity.this, LoginOptionsActivity.class);
                else
                    i = new Intent(LanguageSelectActivity.this, KenbieActivity.class);

                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            } else
                showMessageWithTitle(this, "Alert", Constants.GENERAL_FAIL_MSG);
        } else
            showMessageWithTitle(this, "Alert", Constants.GENERAL_FAIL_MSG);

        showProgressDialog(false);
    }

    @Override
    public void networkError(String error, int APICode) {
        showMessageWithTitle(this, "Alert", Constants.NETWORK_FAIL_MSG);
        showProgressDialog(false);
    }

    @Override
    protected void onDestroy() {
        if (mData != null)
            mData.close();
        if (mProgress != null && mProgress.isShowing())
            mProgress.dismiss();

        super.onDestroy();
    }

    public void showProgressDialog(boolean isShow) {
        try {
            if (isShow) {
                mProgress.setMessage("Please wait...");
                mProgress.show();
            } else
                mProgress.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
