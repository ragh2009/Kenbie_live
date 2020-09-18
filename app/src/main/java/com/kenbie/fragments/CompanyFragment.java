package com.kenbie.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kenbie.KenbieApplication;
import com.kenbie.KenbieWebActivity;
import com.kenbie.R;
import com.kenbie.util.Constants;
import com.kenbie.util.Utility;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyFragment extends BaseFragment {


    public CompanyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_company, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView appTitle = (TextView) view.findViewById(R.id.app_title);
//        appTitle.setText(mActivity.mPref.getString("186", "COMPANY"));
        appTitle.setTypeface(KenbieApplication.S_SEMI_BOLD);

        TextView tStandard = (TextView) view.findViewById(R.id.t_standard);
        tStandard.setTypeface(KenbieApplication.S_NORMAL);
        tStandard.setText(mActivity.mPref.getString("187", "Standard 4.43.0"));

        TextView tCopyright = (TextView) view.findViewById(R.id.t_copyright);
        tCopyright.setTypeface(KenbieApplication.S_NORMAL);
        tCopyright.setText(mActivity.mPref.getString("188", "Copyright 2020 Kenbie AG Services Private Limited all rights reserved"));

        TextView tMore = (TextView) view.findViewById(R.id.t_more);
        tMore.setTypeface(KenbieApplication.S_NORMAL);
        tMore.setText(mActivity.mPref.getString("189", "For more information, visit:"));

        TextView tWebUrl = (TextView) view.findViewById(R.id.t_web_url);
        tWebUrl.setTypeface(KenbieApplication.S_NORMAL);
        tWebUrl.setText(Html.fromHtml("<u>" + mActivity.mPref.getString("190", "https://kenbie.com") + "</u>"));
        tWebUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://kenbie.com"));
                startActivity(browserIntent);
            }
        });

        TextView terms = (TextView) view.findViewById(R.id.t_terms);
        terms.setTypeface(KenbieApplication.S_SEMI_BOLD);
        terms.setText(Html.fromHtml("<u>" + mActivity.mPref.getString("8", "Terms of Service") + "</u>"));
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showInfoDialog("Terms & Conditions", 1);
                Intent intent = new Intent(mActivity, KenbieWebActivity.class);
                intent.putExtra("Type", 2);
                intent.putExtra("URL", Constants.TERMS_URL);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        ((TextView) view.findViewById(R.id.t_end)).setText("&");

        TextView tPrivacy = (TextView) view.findViewById(R.id.t_privacy);
        tPrivacy.setTypeface(KenbieApplication.S_SEMI_BOLD);
        tPrivacy.setText(Html.fromHtml("<u>" + mActivity.mPref.getString("9", "Privacy Policy") + "</u>"));
        tPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showInfoDialog("Privacy Policy", 2);
                Intent pIntent = new Intent(mActivity, KenbieWebActivity.class);
                pIntent.putExtra("Type", 1);
                pIntent.putExtra("URL", Constants.PRIVACY_URL);
                pIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(pIntent);
            }
        });

        LinearLayout moreLayout = (LinearLayout) view.findViewById(R.id.more_layout);
        if (!mActivity.mPref.getString("UserSavedLangCode", "en").equalsIgnoreCase("en"))
            moreLayout.setOrientation(LinearLayout.VERTICAL);
    }

    private void showInfoDialog(String title, int type) {
        new Utility().showInfoDialog(title, type, mActivity, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(19, "KENBIE"/*mActivity.mPref.getString("186", "COMPANY")*/, false, true, false);
        super.onResume();
    }
}
