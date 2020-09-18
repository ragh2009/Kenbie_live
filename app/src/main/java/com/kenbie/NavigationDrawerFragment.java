package com.kenbie;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.adapters.LeftPanelAdapter;
import com.kenbie.adapters.NearByGridAdapter;
import com.kenbie.connection.MConnection;
import com.kenbie.fragments.BaseFragment;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.listeners.NavigationDrawerCallbacks;
import com.kenbie.model.LeftItem;
import com.kenbie.model.UserItem;
import com.kenbie.util.Constants;
import com.kenbie.util.RuntimePermissionUtils;
import com.kenbie.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class NavigationDrawerFragment extends BaseFragment implements NavigationDrawerCallbacks, APIResponseHandler {
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static Uri IMAGE_CAPTURE_URI;
    private String imgPath;
    private NavigationDrawerCallbacks mCallbacks;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;
    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private ArrayList<LeftItem> navList;
    //    private String[] nTitle = {"Credits", "Message", "Profile Visitor", "Liked You", "Favorites", "Casting", "Invite Your Friend", "Logout"};
//    private int[] nImage = {R.drawable.ic_credits, R.drawable.ic_msg, R.drawable.ic_profile, R.drawable.ic_white_dislike, R.drawable.ic_fav, R.drawable.ic_casting, R.drawable.ic_invite_friend, R.drawable.ic_refine};
    private String[] nTitle = {"Message", "Profile Visitor", "Liked You", "Favorites", "Casting"};
    private int[] nImage = {R.drawable.ic_msg, R.drawable.ic_profile, R.drawable.ic_white_dislike, R.drawable.ic_fav, R.drawable.ic_casting};
    private SharedPreferences mPref;
    private ImageView profileImg = null;
    private ProgressDialog mProgress;
    private GridView nearGridView;
    private TextView profileCompleteTxt;
    private SeekBar profileSeekBar;
    private LeftPanelAdapter mAdapter = null;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        initUtils();
        initData();
        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    private void initData() {
        if (getActivity() != null) {
            mPref = getActivity().getSharedPreferences("kPrefs", MODE_PRIVATE);
        }
    }

    private void bindLeftPanelData() {
        navList = new ArrayList<>();
        for (int i = 0; i < nTitle.length; i++) {
            LeftItem lt = new LeftItem();
            lt.setTitle(nTitle[i]);
            lt.setImage(nImage[i]);
            if (i == 0)
                lt.setCount(mPref.getInt("UnreadMsg", 0));
            else if (i == 1)
                lt.setCount(mPref.getInt("VisitorCount", 0));
            else if (i == 2)
                lt.setCount(mPref.getInt("TotalLiked", 0));
            navList.add(lt);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.drawer_main, container, false);

        profileImg = (ImageView) view.findViewById(R.id.profile_img);
        RequestOptions options = new RequestOptions()
                .centerInsideTransform()
                .placeholder(R.drawable.img_place_holder)
                .priority(Priority.HIGH);
        if (mPref.getString("ProfilePic", "") != null && mPref.getString("ProfilePic", "").length() < 10)
            Glide.with(getActivity()).load(Constants.BASE_IMAGE_URL + mPref.getString("ProfilePic", "")).apply(options).into(profileImg);
        else
            Glide.with(getActivity()).load(Constants.BASE_IMAGE_URL + mPref.getString("ProfilePic", "")).apply(RequestOptions.circleCropTransform()).into(profileImg);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.viewEditProfile();
//                selectProfileImage();
            }
        });

        TextView nTitleTxt = (TextView) view.findViewById(R.id.u_name);
        nTitleTxt.setTypeface(KenbieApplication.S_NORMAL);
        nTitleTxt.setText(mPref.getString("Name", ""));

        TextView uLocation = (TextView) view.findViewById(R.id.u_location);
        uLocation.setTypeface(KenbieApplication.S_NORMAL);
        uLocation.setText("");

        ((ImageView) view.findViewById(R.id.u_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.viewSettings();
            }
        });


        profileCompleteTxt = (TextView) view.findViewById(R.id.profile_complete);
        profileCompleteTxt.setTypeface(KenbieApplication.S_NORMAL);
        profileSeekBar = (SeekBar) view.findViewById(R.id.u_seekBar);
        profileSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        bindUserInfoData();

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_options);
//        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        bindLeftPanelData();

        // specify an adapter (see also next example)
        mAdapter = new LeftPanelAdapter(navList, this);
        mRecyclerView.setAdapter(mAdapter);

        TextView nearByTxt = (TextView) view.findViewById(R.id.pg_near_txt);
        nearByTxt.setTypeface(KenbieApplication.S_NORMAL);
        if (mActivity.uType == 1)
            nearByTxt.setText("MODEL NEARBY");
        else if (mActivity.uType == 2)
            nearByTxt.setText("PHOTOGRAPHER NEARBY");
        else
            nearByTxt.setText("AGENCY NEARBY");

        nearGridView = (GridView) view.findViewById(R.id.near_list);
        nearGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                closeAndOpenNavPanel();
                mActivity.viewUserProfile(mActivity.nearByList.get(position));
            }
        });

        gettingProfileComplete();

        return view;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     * @param mToolbar
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar mToolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

       /* ActionBar actionBar = getActivity().get
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }*/

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                mToolbar,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

//        mDrawerToggle.setDrawerIndicatorEnabled(true);
        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
       /* if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }*/
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
//        if (mCallbacks != null) {
//            mCallbacks.onNavigationDrawerItemSelected(position);
//        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_example) {
            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationDrawerItemSelected(LeftItem data) {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
        mCallbacks.onNavigationDrawerItemSelected(data);
    }

    @Override
    public void viewSettings() {

    }

    @Override
    public void viewEditProfile() {

    }

    public void closeAndOpenNavPanel() {
        if (mDrawerLayout.isDrawerOpen(mFragmentContainerView))
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        else {
            mDrawerLayout.openDrawer(mFragmentContainerView);
            gettingProfileComplete();
        }
    }

    public void closeNavPanel() {
//        if (mDrawerLayout.isDrawerOpen(mFragmentContainerView))
        mDrawerLayout.closeDrawer(mFragmentContainerView);
//        else
//            mDrawerLayout.openDrawer(mFragmentContainerView);
    }


    /**
     * Callbacks interface that all activities using this fragment must implement.
     */


    // Getting near by data
    private void gettingProfileComplete() {
        if (mActivity.isOnline()) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mPref.getString("UserId", ""));
            params.put("login_key", mPref.getString("LoginKey", ""));
            params.put("login_token", mPref.getString("LoginToken", ""));
            new MConnection().postRequestWithHttpHeaders(getActivity(), "getUserProfileComplete", this, params, 101);
        }
    }

    // Getting near by data
    private void gettingNearByData() {
        if (mActivity.isOnline()) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mPref.getString("UserId", ""));
            params.put("login_key", mPref.getString("LoginKey", ""));
            params.put("login_token", mPref.getString("LoginToken", ""));
            params.put("longitude", mActivity.longitude + "");
            params.put("latitude", mActivity.latitude + "");
            params.put("user_type", mActivity.uType + "");
            new MConnection().postRequestWithHttpHeaders(getActivity(), "getUsersNearBy", this, params, 102);
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void selectProfileImage() {
        Utility.showPictureDialog(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    startCamera();
                }
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RuntimePermissionUtils.checkPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openPhoneGallery();
                } else {
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, RuntimePermissionUtils.REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            }
        });
    }

    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        IMAGE_CAPTURE_URI = Utility.getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, IMAGE_CAPTURE_URI);
        startActivityForResult(intent, Constants.CAMERA_CLICK);
    }

    private void openPhoneGallery() {
        try {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, Constants.GALLERY_CLICK);
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, Constants.GALLERY_CLICK);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RuntimePermissionUtils.REQUEST_CAMERA:
                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    if (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        startCamera();
                    } else {
                        startCamera();
                    }
                break;
            case RuntimePermissionUtils.REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openPhoneGallery();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CAMERA_CLICK && resultCode == getActivity().RESULT_OK) {
            if (IMAGE_CAPTURE_URI != null) {
                imgPath = IMAGE_CAPTURE_URI.getPath();
                if (imgPath != null) {
                    imageUpload(imgPath);
                }
            }
        } else if (requestCode == Constants.GALLERY_CLICK && resultCode == getActivity().RESULT_OK) {
            try {
                if (data != null) {
                    Uri _uri = data.getData();
                    if (_uri != null) {
                        Cursor cursor = getActivity().getContentResolver().query(_uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                        if (cursor != null)
                            cursor.moveToFirst();
                        try {
                            imgPath = cursor.getString(0);
                            cursor.close();

                            if (imgPath != null) {
                                imageUpload(imgPath);
                            }

                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private boolean checkPermissions() {
        List<String> neededPermissions = new ArrayList<>();

        if (RuntimePermissionUtils.checkPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(android.Manifest.permission.CAMERA);
        }
        if (RuntimePermissionUtils.checkPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            neededPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        String[] permissions = neededPermissions.toArray(new String[neededPermissions.size()]);
        if (neededPermissions.size() > 0) {
            requestPermissions(permissions, RuntimePermissionUtils.REQUEST_CAMERA);
        } else {
            return true;
        }
        return false;
    }

    // Image upload
    private void imageUpload(String imagePath) {
        showProgressDialog(true);
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, MConnection.API_BASE_URL + "uploadPic",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.d("Response", response);
                        try {
                            JSONObject jObj = new JSONObject(response);
                            SharedPreferences.Editor editor = mPref.edit();
                            if (jObj.has("status") && jObj.getBoolean("status")) {
                                JSONObject jImg = new JSONObject(jObj.getString("data"));
                                if (jImg.has("user_pic"))
                                    editor.putString("ProfilePic", jImg.getString("user_pic"));
                                editor.apply();
                                Glide.with(getActivity()).load(Constants.BASE_IMAGE_URL + mPref.getString("ProfilePic", "")).apply(RequestOptions.circleCropTransform()).into(profileImg);
                                if (jObj.has("success"))
                                    Toast.makeText(getActivity().getApplicationContext(), jObj.getString("success"), Toast.LENGTH_LONG).show();
                            } else if (jObj.has("error"))
                                Toast.makeText(getActivity().getApplicationContext(), jObj.getString("error"), Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(getActivity().getApplicationContext(), Constants.GENERAL_FAIL_MSG, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showProgressDialog(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null && error.getMessage() != null)
                    Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getActivity().getApplicationContext(), Constants.GENERAL_FAIL_MSG, Toast.LENGTH_LONG).show();
                showProgressDialog(false);
            }

        });

        // TODO - commented headers in new lib
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("Content-Type", "multipart/form-data;boundary=" + boundary);
//        params.put("X-API-KEY", MConnection.API_KEY);
//        smr.setHeaders(params);

        smr.addStringParam("X-API-KEY", MConnection.API_KEY);
        smr.addStringParam("user_id", mPref.getString("UserId", ""));
        smr.addStringParam("login_key", mPref.getString("LoginKey", ""));
        smr.addStringParam("login_token", mPref.getString("LoginToken", ""));
        smr.addStringParam("device_id", mPref.getString("DeviceId", ""));
        smr.addFile("userimg", imagePath);
        Volley.newRequestQueue(getActivity()).add(smr);
    }

    private void initUtils() {
        try {
            mProgress = new ProgressDialog(getActivity());
            mProgress.setMessage("Please wait...");
            mProgress.setIndeterminate(false);
            mProgress.setCancelable(true);
            mProgress.setCanceledOnTouchOutside(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void getError(String error, int APICode) {

    }

    @Override
    public void getResponse(String response, int APICode) {
        try {
            if (APICode == 101) {
                JSONObject jo = new JSONObject(response);
                if (jo.has("status") && jo.getBoolean("status") && jo.has("data")) {
                    JSONObject jProfile = jo.getJSONObject("data");
                    int moreCount = 0;
                    SharedPreferences.Editor editor = mPref.edit();
                    if (jProfile.has("profile_complete"))
                        editor.putInt("PComplete", jProfile.getInt("profile_complete"));
                    if (jProfile.has("unread_msg_count"))
                        editor.putInt("UnreadMsg", jProfile.getInt("unread_msg_count"));
                    if (jProfile.has("totalliked")) {
                        editor.putInt("TotalLiked", jProfile.getInt("totalliked"));
                        moreCount = jProfile.getInt("totalliked");
                    }
                    if (jProfile.has("visitor_count")) {
                        editor.putInt("VisitorCount", jProfile.getInt("visitor_count"));
                        moreCount = moreCount+jProfile.getInt("visitor_count");
                    }

                    if (jProfile.has("fav_count")) {
                        editor.putInt("FavCount", jProfile.getInt("fav_count"));
                        moreCount = moreCount+jProfile.getInt("fav_count");
                    }

                    editor.putInt("MoreCount", moreCount);
                    editor.apply();
                }

                bindUserInfoData();
                bindLeftPanelData();
                if (mAdapter != null)
                    mAdapter.refreshData(navList);

                gettingNearByData();
            } else if (APICode == 102) {
                JSONObject jo = new JSONObject(response);
                if (jo.has("data")) {
                    mActivity.nearByList = getParseUserList(jo.getString("data"));
                    if (mActivity.nearByList == null)
                        mActivity.nearByList = new ArrayList<>();

                    NearByGridAdapter nearByGridAdapter = new NearByGridAdapter(getActivity(), mActivity.nearByList);
                    nearGridView.setAdapter(nearByGridAdapter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void bindUserInfoData() {
        try {
            profileCompleteTxt.setText("" + mPref.getInt("PComplete", 0) + "%");
            profileSeekBar.setProgress(mPref.getInt("PComplete", 0));
            profileSeekBar.setPadding(0, 0, 0, 0);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                profileSeekBar.getThumb().mutate().setAlpha(0);
//            }
//            profileSeekBar.getProgressDrawable().setColorFilter(
//                    Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void networkError(String error, int APICode) {

    }

    // Parse user list
    private ArrayList<UserItem> getParseUserList(String data) {
        ArrayList<UserItem> values = new ArrayList<>();
        try {
            JSONArray jData = new JSONArray(data);
            for (int i = 0; i < jData.length(); i++) {
                JSONObject jo = new JSONObject(jData.getString(i));
                UserItem value = new UserItem();
                value.setId(jo.getInt("id"));
                value.setFirstName(jo.getString("first_name"));
                if (jo.getString("birth_year") != null && !jo.getString("birth_year").equalsIgnoreCase("null"))
                    value.setBirthYear(jo.getInt("birth_year"));
//                value.setBirthMonth(jo.getInt("birth_month"));
//                value.setBirthDay(jo.getInt("birth_day"));
                value.setTotalImage(jo.getInt("total_image"));
                value.setCity(jo.getString("city"));
                value.setCountry(jo.getString("country"));
                value.setIsActive(jo.getInt("is_active"));
                value.setUserPic(jo.getString("user_pic"));
                values.add(value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }
}
