package com.kenbie;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.kenbie.util.Constants;

import java.util.List;

public class KenbieWebActivity extends KenbieBaseActivity {
    private String webUrl;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkWebView();
        setContentView(R.layout.activity_kenbie_web);

        initUi();
    }

    private void initUi() {
        type = getIntent().getIntExtra("Type", 0);
        if (type == 4)
            webUrl = getIntent().getStringExtra("URL");
        else
            webUrl = getIntent().getStringExtra("URL") + "/" + mPref.getString("UserSavedLangCode", "en");
        Log.v("WebUrl", webUrl);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        LinearLayout topBar = findViewById(R.id.top_bar);
        if (type == 4)
            topBar.setVisibility(View.GONE);
        ((ImageView) findViewById(R.id.app_logo)).setVisibility(View.GONE);
        ((ImageView) findViewById(R.id.back_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(LoginActivity.this, LoginOptionsActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                finish();
                System.gc();
            }
        });

//        TextView okBtn = ((TextView) findViewById(R.id.ok_btn));

        TextView titleTxt = ((TextView) findViewById(R.id.m_title));
        titleTxt.setTypeface(KenbieApplication.S_NORMAL);
        titleTxt.setVisibility(View.VISIBLE);
        if (type == 1) // Privacy
            titleTxt.setText(mPref.getString("9", "Privacy Policy"));
        else if (type == 2) // Terms
            titleTxt.setText(mPref.getString("8", "Terms of Service"));
        else if (type == 3) { // sponsor link
//            okBtn.setVisibility(View.GONE);
            titleTxt.setText("");
        } else if (type == 4) { // Social
//            okBtn.setVisibility(View.GONE);
            titleTxt.setText("");
        }

        titleTxt.setTextColor(getResources().getColor(R.color.red_text_color));

        WebView webView = findViewById(R.id.web_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(webUrl);

//        okBtn.setTypeface(KenbieApplication.S_BOLD);
//        okBtn.setText(mPref.getString("21", "Yes"));
//        okBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            if (Uri.parse(url).getHost().equals("https://kenbie.com/member/payment_success")) { // success
//            if (url != null && url.equals("https://kenbie.com/member/payment_success")) { // success
//                Intent i = new Intent(mActivity, KenbieActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(i);
//                return false;
//            } else if (url != null && url.equals("https://kenbie.com/member/payment_fail")) { // failure
//                Intent i = new Intent(mActivity, KenbieActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(i);
//                return false;
//            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs

            // https://kenbie.com/member/insta_oauth2?
            if (type == 4 && url != null && url.startsWith("https://kenbie.com/member")) {
//            if (type == 4 && url != null && url.contains("https%3A%2F%2Fkenbie.com%2Fmember%2Finsta_oauth2%3Fcode%3D")){
                Intent intent = new Intent();
                intent.putExtra("AuthUrl", url);
                intent.putExtra("InstaRedirect", true);
                intent.putExtra("Success", true);
                setResult(Constants.INSTA_REDIRECT_URL, intent);
                finish();
//                view.loadUrl("https://api.instagram.com/oauth/access_token?client_id=3110479392371069&client_secret=70492f026ad27d8a31614fb01d6d8155&redirect_uri=https://kenbie.com/member/insta_oauth2&code="+url.replace("https://kenbie.com/member/insta_oauth2?code=","")+"&grant_type=authorization_code");
                return false;
            } else if (type == 4 && url != null && url.contains("https%3A%2F%2Fkenbie.com%2Fmember%2Finsta_oauth2%3Fcode%3D")) {
//                view.loadUrl(url);
                return false;
            } else {
                view.loadUrl(url);
                return true;
            }
        }
    }

    private void checkWebView() {
        try {
            if (getPackageManager().hasSystemFeature("android.software.webview") == true && isPackageExisted("com.google.android.webview")) {
            } else {
                Toast.makeText(KenbieWebActivity.this, "Please install the webview", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public boolean isPackageExisted(String targetPackage) {
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm = getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(targetPackage))
                return true;
        }
        return false;
    }
}
