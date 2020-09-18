package com.kenbie.fragments;


import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.kenbie.KenbieActivity;
import com.kenbie.R;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.util.Constants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ch.datatrans.payment.AliasPaymentMethod;
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

public class SubscriptionPmtFragment extends BaseFragment implements APIResponseHandler, IPaymentProcessStateListener {
    private int type = 0;
    private String paymentUrl;
    private WebView pmtWebView = null;

    public SubscriptionPmtFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            type = getArguments().getInt("PaymentType", 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subscription_pmt, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pmtWebView = view.findViewById(R.id.payment_view);

        WebSettings webSettings = pmtWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        pmtWebView.setWebViewClient(new MyWebViewClient());
//        getDataTransPayment();
        getPaymentLink(type);
    }

    private void getDataTransPayment() {
        try {
            String merchantId = "1100017737"; // Datatrans merchant ID
            String refno = "KENBIE AG_mobile"; // supplied by merchant's server
            String currencyCode = "EUR";

            int amount = 0;
            if(type == 1) // Membership 149
                amount = 14900;
            else if(type == 2) // Casting -  Casting t=cast&rid=<Casting_id> - 4.99
                amount = 499;
            else // Add Celeb - 4.99
                amount = 499;
            Payment payment = new Payment(Constants.MERCHANT_ID, "payRefId", Constants.CURRENCY_CODE, amount, Constants.SIGNATURE);
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(1, null, false, true, false);
        super.onResume();
    }

    private void getPaymentLink(int type) {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            if (mActivity.ip == null)
                params.put("ip", "");
            else
                params.put("ip", mActivity.ip);
            if (type == 1) // Membership
                params.put("t", mActivity.utility.getEncodeBase64("t=subs&rid=" + params.get("user_id")));
            else if (type == 2) // Casting -  Casting t=cast&rid=<Casting_id>,
                params.put("t", mActivity.utility.getEncodeBase64("t=subs&rid=" + mActivity.mPref.getInt("CastingPmtId", 0)));
            else if (type == 3)// Add Celeb
                params.put("t", mActivity.utility.getEncodeBase64("t=celeb"));

            params.put("device_id", mActivity.deviceId);

            new MConnection().postRequestWithHttpHeaders(getActivity(), "paynow", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(getActivity(), "Alert", Constants.NETWORK_FAIL_MSG);
        }
    }

    @Override
    public void getError(String error, int APICode) {
        if (error != null)
            if (error.equalsIgnoreCase("com.android.volley.error.AuthFailureError"))
                mActivity.logoutProcess();
            else
                mActivity.showMessageWithTitle(getActivity(), "Alert", error);
        else
            mActivity.showMessageWithTitle(getActivity(), "Alert", Constants.GENERAL_FAIL_MSG);

        mActivity.showProgressDialog(false);
    }

    @Override
    public void getResponse(String response, int APICode) {
        try {
            if (APICode == 101) { // Paynow response
                JSONObject jo = new JSONObject(response);
                if (jo.has("data")) {
                    JSONObject jsonObject = new JSONObject(jo.getString("data"));
                    if (jsonObject.has("payment_url")) {
                        paymentUrl = jsonObject.getString("payment_url");
                        if (paymentUrl != null) {
//                              pmtWebView.loadUrl("www.google.com");
                            pmtWebView.loadUrl(paymentUrl);
//                            pmtWebView.loadDataWithBaseURL(paymentUrl, null, "text/html", "utf-8", null);
                        } else
                            getError(null, APICode);
                    }
                }
            }

            mActivity.showProgressDialog(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void networkError(String error, int APICode) {
        mActivity.showMessageWithTitle(getActivity(), "Alert", Constants.NETWORK_FAIL_MSG);
        mActivity.showProgressDialog(false);
    }

    @Override
    public void paymentProcessStateChanged(PaymentProcessAndroid process) {
        switch (process.getState()) {
            case COMPLETED:
                AliasPaymentMethod pm = process.getAliasPaymentMethod();
                if (pm != null) {
                    // serialize and securely store pm for reuse
                }
                break;
            case CANCELED:
                // ignore, abort checkout, whatever...
                break;
            case ERROR:
                Exception e = process.getException();
                if (e instanceof BusinessException) {
                    BusinessException be = (BusinessException)e;
                    int errorCode = be.getErrorCode(); // Datatrans error code if needed
                    // display some error message
                } else {
// unexpected technical exception, either fatal TechnicalException or
// javax.net.ssl.SSLException (certificate error)
                }
                break;
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            if (Uri.parse(url).getHost().equals("https://kenbie.com/member/payment_success")) { // success
            if (url != null && url.equals("https://kenbie.com/member/payment_success")) { // success
                Intent i = new Intent(mActivity, KenbieActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return false;
            } else if (url != null && url.equals("https://kenbie.com/member/payment_fail")) { // failure
                Intent i = new Intent(mActivity, KenbieActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            pmtWebView.loadUrl(url);
            return true;
        }
    }
}
