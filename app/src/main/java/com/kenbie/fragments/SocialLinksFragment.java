package com.kenbie.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.kenbie.KenbieApplication;
import com.kenbie.KenbieWebActivity;
import com.kenbie.LoginOptionsActivity;
import com.kenbie.R;
import com.kenbie.adapters.SocialLinkAdapter;
import com.kenbie.connection.MConnection;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.listeners.ProfileOptionListener;
import com.kenbie.model.OptionsData;
import com.kenbie.util.Constants;
import com.kenbie.util.instagram.InstagramApp;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.internal.platform.Platform;

public class SocialLinksFragment extends BaseFragment implements ProfileOptionListener, APIResponseHandler, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private InstagramApp instaObj;
    private String INSTA_URL = "https://api.instagram.com/";
    private String INSTA_APP_ID = "3110479392371069";
    private String INSTA_APP_SECRET = "70492f026ad27d8a31614fb01d6d8155";
    private String INSTA_REDIRECT_URL = "https://kenbie.com/member/insta_oauth2";
    private ArrayList<OptionsData> mSocialList;
    private int selPos;
    private SocialLinkAdapter socialLinkAdapter = null;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private TwitterAuthClient authClient = null;
    private int SOCIAL_TYPE;
    private TextView mSaveBtn;

    public SocialLinksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            if (getArguments() != null)
                mSocialList = (ArrayList<OptionsData>) getArguments().getSerializable("SocialData");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView backBtn = view.findViewById(R.id.m_back_button);
        backBtn.setOnClickListener(this);
        TextView screenTitle = view.findViewById(R.id.m_title);
        screenTitle.setText(mActivity.mPref.getString("241", "SOCIAL LINKS"));
        screenTitle.setTypeface(KenbieApplication.S_NORMAL);
        mSaveBtn = view.findViewById(R.id.m_save_btn);
        mSaveBtn.setTypeface(KenbieApplication.S_NORMAL);
        mSaveBtn.setText(mActivity.mPref.getString("225", "Save"));
        mSaveBtn.setVisibility(View.INVISIBLE);
        mSaveBtn.setOnClickListener(this);

        ListView socialList = (ListView) view.findViewById(R.id.info_list);

        if (mSocialList == null)
            mSocialList = new ArrayList<>();
        mSocialList = bindSocialLink(mSocialList);

        socialLinkAdapter = new SocialLinkAdapter(mActivity, mSocialList, this, mActivity.mPref);
        socialList.setAdapter(socialLinkAdapter);

        // Google disable
/*        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(mActivity, gso);*/

//        initTwitter();
        initFB();
    }

    private void updateSocialLinkOnServer() {
        if (mActivity.isOnline()) {
//            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mActivity.mPref.getString("UserId", ""));
            params.put("login_key", mActivity.mPref.getString("LoginKey", ""));
            params.put("login_token", mActivity.mPref.getString("LoginToken", ""));
            bindSocialLinksWithRequest(params);
            mActivity.mConnection.postRequestWithHttpHeaders(mActivity, "updateUserSocialLinks", this, params, 101);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void bindSocialLinksWithRequest(Map<String, String> params) {
        try {
//            String[] data = {};
//            @"user_id=%@&login_key=%@&login_token=%@&fb=%@&insta=%@&twitter=%@&google=%@&yandex=%@&youtube=%@&linkedin=%@&pinterest=%@&website=%@&device_id=%@"
            for (int i = 0; i < mSocialList.size(); i++) {
                if (mSocialList.get(i).isActive() && mSocialList.get(i).getOptionData() != null && !mSocialList.get(i).getOptionData().equalsIgnoreCase("null"))
                    params.put(mSocialList.get(i).getOptionCode(), mSocialList.get(i).getOptionData());
                else
                    params.put(mSocialList.get(i).getOptionCode(), "");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Integer> mySocialLink(ArrayList<OptionsData> mSocialList) {
        ArrayList<Integer> values = new ArrayList<>();

        for (int i = 0; i < mSocialList.size(); i++)
            values.add(mSocialList.get(i).getId());

        return values;
    }

    // Binding social data
    private ArrayList<OptionsData> bindSocialLink(ArrayList<OptionsData> socialDetails) {
        ArrayList<OptionsData> values = new ArrayList<>();
//        String[] sData = {"Facebook", "Instagram", "Twitter", "Google+", "Yandex", "YouTube", "Linked In", "Pinterest", "Website"};
//        String[] sData = {mActivity.mPref.getString("242", "Facebook"), mActivity.mPref.getString("243", "Instagram"),  mActivity.mPref.getString("244", "Twitter"),  mActivity.mPref.getString("245", "Yandex"), mActivity.mPref.getString("246", "Youtube"), "Linked In",  mActivity.mPref.getString("247", "Pinterest"), mActivity.mPref.getString("248", "Website")};
//        String[] sData = {mActivity.mPref.getString("242", "Facebook"), mActivity.mPref.getString("243", "Instagram"), mActivity.mPref.getString("244", "Twitter"), mActivity.mPref.getString("247", "Pinterest")};
//        String[] codes = {"fb", "insta", "twitter", "pinterest"};
//        String[] codes = {"fb", "insta", "twitter", "google", "yandex", "youtube", "linkedin", "pinterest", "website"};
//        Integer[] sImages = {R.drawable.is_fb, R.drawable.ic_instagram, R.drawable.is_twitter, R.drawable.ic_yandex, R.drawable.ic_youtube, R.drawable.ic_linkedin, R.drawable.is_pinterest, R.drawable.ic_website};
        Integer[] sImages = {R.drawable.is_fb, R.drawable.ic_instagram};
        String[] sData = {mActivity.mPref.getString("242", "Facebook"), mActivity.mPref.getString("243", "Instagram")};
        String[] codes = {"fb", "insta"};

        try {
            for (int i = 0; i < sData.length; i++) {
                OptionsData od = new OptionsData();
                od.setId((i + 1));
                od.setName(sData[i]);
                od.setImgId(sImages[i]);
                for (int j = 0; j < socialDetails.size(); j++)
                    if (od.getId() == socialDetails.get(j).getId()) {
                        od.setOptionData(socialDetails.get(j).getOptionData());
                        od.setActive(true);
                        break;
                    }
                od.setOptionCode(codes[i]);
                values.add(od);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return values;
    }

    @Override
    public void getAction(OptionsData value) {

    }

    @Override
    public void getDataList(ArrayList<OptionsData> value, int position, int type) {
        selPos = position;
        try {
            if (mSocialList.get(selPos).isActive()) {
//                mSocialList.get(selPos).setOptionData("null");
                mSocialList.get(selPos).setActive(false);
                socialLinkAdapter.refreshData(mSocialList);
                mSaveBtn.performClick();
            } else if (position == 0) {
                SOCIAL_TYPE = 1; // FB
                fbLoginProcess();
            } else if (position == 1) {
                SOCIAL_TYPE = 4; //Instagram
//                instagramCustomProcess();
                instagramProcess();
//                startInstaProcess(true);
            } else if (position == 2) {
                SOCIAL_TYPE = 2;
                twitterLoginProcess();
            } else if (position == 3) {
                SOCIAL_TYPE = 3;
                gPlusProcess();
            } else
                alertForSocialLinks(mSocialList.get(selPos));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void instagramCustomProcess() {
        // Instagram Implementation
        instaObj = new InstagramApp(mActivity, INSTA_APP_ID, INSTA_APP_SECRET, INSTA_REDIRECT_URL);
        instaObj.setListener(listener);
        instaObj.authorize();  //add this in your button click or wherever you need to call the instagram api
    }

    InstagramApp.OAuthAuthenticationListener listener = new InstagramApp.OAuthAuthenticationListener() {

        @Override
        public void onSuccess() {

            Log.e("Userid", instaObj.getId());
            Log.e("Name", instaObj.getName());
            Log.e("UserName", instaObj.getUserName());

        }

        @Override
        public void onFail(String error) {
            Toast.makeText(mActivity, error, Toast.LENGTH_SHORT)
                    .show();
        }
    };


    private void updateSocialLink(String link) {
        mSocialList.get(selPos).setOptionData(link);
        mSocialList.get(selPos).setActive(true);
        socialLinkAdapter.refreshData(mSocialList);
        mSaveBtn.performClick();
    }

    // Social links
    private void alertForSocialLinks(OptionsData optionsData) {
        try {
            final Dialog dialog = new Dialog(mActivity, android.R.style.Theme_Holo_Dialog_MinWidth);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
//                    WindowManager.LayoutParams.MATCH_PARENT);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
//        getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
            dialog.setContentView(R.layout.alert_social_dailog);

            TextView alertTitleTxt = (TextView) dialog.findViewById(R.id.alert_title);
            alertTitleTxt.setTypeface(KenbieApplication.S_SEMI_BOLD);
            alertTitleTxt.setText(optionsData.getName());

            final EditText etUserName = (EditText) dialog.findViewById(R.id.et_username);
            // TODO missing URL
            etUserName.setTypeface(KenbieApplication.S_NORMAL);

            if (optionsData.getOptionData() != null && !optionsData.getOptionData().equalsIgnoreCase("null"))
                etUserName.setText(optionsData.getOptionData());


            TextView submitBtn = (TextView) dialog.findViewById(R.id.submit_btn);
            submitBtn.setText(mActivity.mPref.getString("249", "LINK"));
            submitBtn.setTypeface(KenbieApplication.S_NORMAL);
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etUserName.getText().toString().equalsIgnoreCase(""))
                        mActivity.showToast(mActivity, "Please enter the link");
                    else {
                        mActivity.hideKeyboard(mActivity, etUserName);
                        dialog.dismiss();
                        updateSocialLink(etUserName.getText().toString());
                    }
                }
            });

            // TODO missing
            TextView cancelBtn = (TextView) dialog.findViewById(R.id.cancel_button);
            cancelBtn.setText(mActivity.mPref.getString("22", "Cancel"));
            cancelBtn.setTypeface(KenbieApplication.S_NORMAL);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(8, mActivity.mPref.getString("241", "SOCIAL LINKS"), false, true, true);
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
                if (APICode == 101) {
                    JSONObject jo = new JSONObject(response);
                    if (jo.has("status") && jo.getBoolean("status") && jo.has("success")) {
//                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), jo.getString("success"));
                    }else
                        mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("270", "Something Wrong! Please try later."));
                } else if (APICode == 102) {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("status") && jsonObject.getBoolean("status")) {
                        if (jsonObject.has("data")) {
                            JSONObject joo = jsonObject.getJSONObject("data");
                            if (joo.has("username"))
                                checkSocialLogin(joo.getString("username"));
                        }
                    } else
                        getError(mActivity.mPref.getString("270", "Something Wrong! Please try later."), APICode);
                }
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

    private void fbLoginProcess() {
//        LoginManager.getInstance().setLoginBehavior("web_only");
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_link"));
    }

    private void initFB() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        mActivity.showProgressDialog(true);
                        // App code
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("LoginActivity", response.toString());

                                        // Application code
                                        try {
//                                            String sId = object.getString("id");
                                            String link = object.getString("link");
//                                            if (object.has("email"))
//                                                email = object.getString("email");
//                                            if (object.has("birthday"))
//                                                dob = object.getString("birthday");
//                                            if (object.has("gender"))
//                                                gender = object.getString("gender");
                                            checkSocialLogin(link);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "link");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }

    /* Twitter login */
    private void initTwitter() {
//        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
//        TwitterAuthToken authToken = session.getAuthToken();
//        String token = authToken.token;
//        String secret = authToken.secret;

        TwitterConfig config = new TwitterConfig.Builder(mActivity)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(Constants.API_KEY, Constants.API_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }


    private void twitterLoginProcess() {
        mActivity.showProgressDialog(true);
        authClient = new TwitterAuthClient();
        authClient.authorize(mActivity, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d("", "onConnectionFailed:" + result.toString());
                TwitterSession session = result.data;
                String name = session.getUserName();
                String sId = session.getUserId() + "";
                checkSocialLogin(sId);
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(mActivity, exception.getMessage(), Toast.LENGTH_SHORT).show();
                mActivity.showProgressDialog(false);
//                Log.d("", "onConnectionFailed:" + exception.toString());
            }
        });
    }

    /* Google Plus Login Process */
    private void gPlusProcess() {
        mActivity.showProgressDialog(true);
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
//        startActivityForResult(signInIntent, RC_SIGN_IN);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("", "onConnectionFailed:" + connectionResult);
        Toast.makeText(mActivity, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

   /*   Old code
     GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Google sign-in error. Please try again.", Toast.LENGTH_SHORT).show();
                showProgressDialog(false);
            }*/
        } else if (SOCIAL_TYPE == 1)
            callbackManager.onActivityResult(requestCode, resultCode, data);
        else if (requestCode == Constants.INSTA_REDIRECT_URL) {
            if (data != null && data.getBooleanExtra("InstaRedirect", false)) {
//               startInstaProcess(data.getBooleanExtra("Success", false), data.getStringExtra("AuthUrl"));
                getAccessTokenFromServer(Objects.requireNonNull(data.getStringExtra("AuthUrl")).replace("https://kenbie.com/member/insta_oauth2?code=", "").replace("#_", ""));
            }
        } else if(SOCIAL_TYPE == 2){
            authClient.onActivityResult(requestCode, resultCode, data);
        }
    }

    // [END onactivityresult]
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);
            String sId = acct.getId();
//            String name = acct.getDisplayName();
//            String email = acct.getEmail();
//            if (acct.getPhotoUrl() != null)
//                imageUrl = acct.getPhotoUrl().toString();

            checkSocialLogin(sId);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(mActivity, "Google sign-in error. Please try again.", Toast.LENGTH_SHORT).show();
            mActivity.showProgressDialog(false);
        }
    }

    /*---------------------------------Instagram Process-------------------------*/
    private void instagramProcess() {
//        String authUrl = INSTA_URL + "oauth/authorize?client_id=" + INSTA_APP_ID + "&redirect_uri=" + INSTA_REDIRECT_URL + "&scope=user_profile&response_type=code";
        String authUrl = INSTA_URL + "oauth/authorize?client_id=" + INSTA_APP_ID + "&redirect_uri=" + INSTA_REDIRECT_URL + "&scope=user_profile&response_type=code";
//        mActivity.mConnection.getFullUrlRequestWithHttpHeaders(mActivity, authUrl, this, 102);
        Intent intent = new Intent(mActivity, KenbieWebActivity.class);
        intent.putExtra("Type", 4);
        intent.putExtra("URL", authUrl);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivityForResult(intent, Constants.INSTA_REDIRECT_URL);
    }

    private void getAccessTokenFromServer(String authCode) {
        if (mActivity.isOnline()) {
            mActivity.showProgressDialog(true);
            Map<String, String> params = new HashMap<String, String>();
            params.put("code", authCode);
            mActivity.mConnection.postFullRequestWithHttpHeaders(mActivity, "instaOauth2", this, params, 102);
        } else {
            mActivity.showProgressDialog(false);
            mActivity.showMessageWithTitle(mActivity, mActivity.mPref.getString("20", "Alert!"), mActivity.mPref.getString("269", "Network failed! Please try later."));
        }
    }

    private void checkSocialLogin(String sId) {
        if (SOCIAL_TYPE == 1) { // FB
            mSocialList.get(selPos).setOptionData(sId);
            mSocialList.get(selPos).setActive(true);
            socialLinkAdapter.refreshData(mSocialList);
            mSaveBtn.performClick();
        } else if (SOCIAL_TYPE == 2) { // Twitter
            mSocialList.get(selPos).setOptionData(sId);
            mSocialList.get(selPos).setActive(true);
            socialLinkAdapter.refreshData(mSocialList);
            mSaveBtn.performClick();
        } else if (SOCIAL_TYPE == 3) { // Google
            mSocialList.get(selPos).setOptionData(sId);
            mSocialList.get(selPos).setActive(true);
            socialLinkAdapter.refreshData(mSocialList);
            mSaveBtn.performClick();
        } else if (SOCIAL_TYPE == 4) { // Instagram
            mSocialList.get(selPos).setOptionData(sId);
            mSocialList.get(selPos).setActive(true);
            socialLinkAdapter.refreshData(mSocialList);
            updateSocialLinkOnServer();
        }

        mActivity.showProgressDialog(false);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.m_save_btn) {
            updateSocialLinkOnServer();
        } else if (v.getId() == R.id.m_back_button)
            mActivity.onBackPressed();
    }

    public void startInstaProcess(boolean success, String redirectUrl) {
        if (success) {
//            String authUrl = INSTA_URL + "oauth/authorize?client_id=" + INSTA_APP_ID + "&redirect_uri=" + INSTA_REDIRECT_URL + "&scope=user_profile&response_type=code";
//            String authUrl = INSTA_URL + "oauth/access_token?client_id=" + INSTA_APP_ID + "&client_secret="+INSTA_APP_SECRET +"&redirect_uri=" + INSTA_REDIRECT_URL + "&grant_type=authorization_code&code="+redirectUrl.replace("https://kenbie.com/member/insta_oauth2?code=","");
//            https://api.instagram.com/oauth/access_token?client_id={CLIENT_ID}&client_secret={CLIENT_SECRET}&grant_type=authorization_code&redirect_uri={REDIRECT_URI}&code={CODE}
//            mActivity.mConnection.getFullUrlRequestWithHttpHeaders(mActivity, authUrl, this, 102);
//            Intent intent = new Intent(mActivity, KenbieWebActivity.class);
//            intent.putExtra("Type", 4);
//            intent.putExtra("URL", authUrl);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivityForResult(intent, Constants.INSTA_REDIRECT_URL);

            Map<String, String> params = new HashMap<String, String>();
            params.put("client_id", INSTA_APP_ID);
            params.put("client_secret", INSTA_APP_SECRET);
            params.put("redirect_uri", INSTA_REDIRECT_URL);
            params.put("grant_type", "authorization_code");
            params.put("code", redirectUrl.replace("https://kenbie.com/member/insta_oauth2?code=", "").replace("#_", ""));
            mActivity.mConnection.postFullRequestWithHttpHeaders(mActivity, INSTA_URL + "oauth/access_token", this, params, 102);
        }
    }
}
