package com.kenbie;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kenbie.databinding.KenbieActivityBinding;
import com.kenbie.databinding.TopHeaderBarBinding;
import com.kenbie.events.ProfilePicEvent;
import com.kenbie.events.TitleBarEvent;
import com.kenbie.fragments.AboutEditFragment;
import com.kenbie.fragments.BaseFragment;
import com.kenbie.fragments.CastingDetailsFragment;
import com.kenbie.fragments.CastingFragment;
import com.kenbie.fragments.EditBasicInfoFragment;
import com.kenbie.fragments.EditUserInfoFragment;
import com.kenbie.fragments.ExtraFragment;
import com.kenbie.fragments.LanguageFragment;
import com.kenbie.fragments.MemberShipInfoFragment;
import com.kenbie.fragments.MessageUserListFragment;
import com.kenbie.fragments.ModelsFragment;
import com.kenbie.fragments.PasswordChangeFragment;
import com.kenbie.fragments.PhotoGalleryFragment;
import com.kenbie.fragments.ReportAbuseToUserFragment;
import com.kenbie.fragments.SearchFragment;
import com.kenbie.fragments.SettingFragment;
import com.kenbie.fragments.SettingUpdateFragment;
import com.kenbie.fragments.SocialLinksFragment;
import com.kenbie.fragments.SortFragment;
import com.kenbie.fragments.SubscriptionPmtFragment;
import com.kenbie.fragments.UpdateLanguageListFragment;
import com.kenbie.fragments.UserListFragment;
import com.kenbie.fragments.UserStatisticsFragment;
import com.kenbie.listeners.APIResponseHandler;
import com.kenbie.model.CastingUser;
import com.kenbie.model.MsgUserItem;
import com.kenbie.model.OptionsData;
import com.kenbie.model.ProfileInfo;
import com.kenbie.model.UserItem;
import com.kenbie.model.UserTypeData;
import com.kenbie.util.BadgeUtils;
import com.kenbie.util.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KenbieActivity extends KenbieBaseActivity implements APIResponseHandler {
    public static final String NOTIFY_ACTIVITY_ACTION = "notify_message";
    public int type = 0, sortBy = 0, currentPage = 1, moreTab = 0, castingCurrentPage = 1;
    public int lastVisiblePosition, msgLastVisiblePosition, castingLastVisiblePosition;
    public ArrayList<UserTypeData> userData;
    public ArrayList<UserItem> celebritiesList = null, randomCelebrities = null;
    public ArrayList<MsgUserItem> msgUserAllList;
    public ArrayList<CastingUser> castingUserArrayList;
    public String imgPath = "";
    public FrameLayout container;
    public BottomNavigationView navigationView;

    private String token = null;
    private int navType = 0;
    private ProgressDialog mProgress;
    private LinearLayout guestBottomOptions;
    private Fragment currentFragment;
    private FragmentManager fragmentManager;
    private Fragment lastFragment;
    private TopHeaderBarBinding mTopBarBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KenbieActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_kenbie_main);
        mTopBarBinding = binding.topHeaderBar;
        setupTitleBar(mTopBarBinding);

        mProgress = new ProgressDialog(this, R.style.MyAlertDialogStyle);
//        mProgress.setMessage("Please wait...");
        mProgress.setIndeterminate(false);
        mProgress.setCancelable(true);
        mProgress.setCanceledOnTouchOutside(false);

/*        menuBtn = (ImageView) findViewById(R.id.back_button);
        menuBtn.setBackgroundResource(R.drawable.ic_panel);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 0 && !mPref.getBoolean("GuestLogin", false))
                    mNavigationDrawerFragment.closeAndOpenNavPanel();
                else
                    onBackPressed();
            }
        });*/

        guestBottomOptions = findViewById(R.id.guest_bottom_options);

        TextView btnLogin = findViewById(R.id.btn_login);
        btnLogin.setText(mPref.getString("306", "LOG IN"));
        btnLogin.setTypeface(KenbieApplication.S_SEMI_BOLD);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(KenbieActivity.this, LoginActivity.class);
                i.putExtra("Type", 1);
                i.putExtra("IsBack", true);
                startActivity(i);
                finish();
            }
        });

        TextView btnSignUp = findViewById(R.id.btn_signup);
        btnSignUp.setText(mPref.getString("307", "SIGN UP"));
        btnSignUp.setTypeface(KenbieApplication.S_SEMI_BOLD);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KenbieActivity.this, SignUpActivity.class);
                intent.putExtra("social_type", 0);
                intent.putExtra("android_token", token == null ? "" : token);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
//                finish();
            }
        });

        navigationView = findViewById(R.id.navigation);
        container = findViewById(R.id.container);

//        setFragmentListener();
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return handleMenuItemClicked(menuItem);
            }
        });

        navType = getIntent().getIntExtra("NavType", 0);
        handleNavigationType();
    }

    private boolean handleMenuItemClicked(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_home:
//                fragmentStateManager.changeFragment(0);
                navigationView.getMenu().getItem(0).setChecked(true);
                if (fragmentExist("UserListFragment")) {
//                    Fragment activeFragment = getCurrentFragment();
                    Log.i("CurrentFragment", currentFragment.getClass().getName());
                    updateActionBar(0, null, true, false, true);
                    // TODO - check if activeFragment == null
                    if (currentFragment instanceof UserListFragment) {
                        Log.i("CurrentFragment", currentFragment.getClass().getName() + ": Scroll top");

//                              UserListFragment userListFragment = (UserListFragment)
//                                getSupportFragmentManager().findFragmentById(R.id.container);
                        UserListFragment userListFragment = (UserListFragment) currentFragment;
//                             Make sure that such a fragment exists
                        // Send this message to the ReceiverFragment by calling its public method
                        userListFragment.showTopPosition(0);
                        userListFragment.resume();
                        lastVisiblePosition = 0;
                    } else {
                        Log.i("FromStackActive", ":UserListFragment" + "---------CurrentFragment:::::" + currentFragment.getClass().getName());
                        fragmentBackOnFront("UserListFragment", currentFragment);
                    }
                } else {
                    Log.i("StartNew : ", "Start new fragment UserListFragment");
                    replaceFragment(new UserListFragment(), true, false);
                }
                return true;
            case R.id.action_chat:
//                fragmentStateManager.changeFragment(1);
                navigationView.getMenu().getItem(1).setChecked(true);
                if (fragmentExist("MessageUserListFragment")) {
//                        Fragment activeFragment = getCurrentFragment();
                    Log.i("CurrentFragment", currentFragment.getClass().getName());
                    updateActionBar(22, mPref.getString("95", "MESSAGES"), false, false, true);
                    if (currentFragment instanceof MessageUserListFragment) {
                        Log.i("CurrentFragment", currentFragment.getClass().getName() + ": Scroll top");
//                            MessageUserListFragment messageUserListFragment = (MessageUserListFragment)
//                                    getSupportFragmentManager().findFragmentById(R.id.container);
                        MessageUserListFragment messageUserListFragment = (MessageUserListFragment) currentFragment;
                        messageUserListFragment.showTopPosition(0);
                        msgLastVisiblePosition = 0;
                    } else {
                        Log.i("FromStackActive", ":MessageUserListFragment" + "---------CurrentFragment:::::" + currentFragment.getClass().getName());
                        fragmentBackOnFront("MessageUserListFragment", currentFragment);
                    }
                } else {
                    Log.i("StartNew : ", "Start new fragment MessageUserListFragment");
                    replaceFragment(new MessageUserListFragment(), true, false);
                }
                return true;
            case R.id.action_search:
//                fragmentStateManager.changeFragment(2);
                if (fragmentExist("SearchFragment")) {
//                        Fragment activeFragment = getCurrentFragment();
                    if (currentFragment instanceof SearchFragment) {
                        searchBackHandling();
//                        ((SearchFragment) currentFragment).moveToBack();
                        return false;
                    } else {
                        navigationView.getMenu().getItem(2).setChecked(true);
                        lastFragment = currentFragment;
                        Log.i("FromStackActive", ":SearchFragment" + "---------CurrentFragment:::::" + currentFragment.getClass().getSimpleName());
                        fragmentBackOnFront("SearchFragment", currentFragment);
                        updateActionBar(2, mPref.getString("57", "SEARCH"), false, false, true);
                        return true;
                    }
                } else {
                    lastFragment = currentFragment;
                    Log.i("StartNew : ", "Start new fragment SearchFragment");
                    navigationView.getMenu().getItem(2).setChecked(true);
                    replaceFragment(new SearchFragment(), true, false);
                    return true;
                }
            case R.id.action_casting:
//                fragmentStateManager.changeFragment(3);
                navigationView.getMenu().getItem(3).setChecked(true);
                if (fragmentExist("CastingFragment")) {
//                        Fragment activeFragment = getCurrentFragment();
                    updateActionBar(6, mPref.getString("101", "CASTING"), false, false, true);
                    if (currentFragment instanceof CastingFragment) {
                        Log.i("CurrentFragment", currentFragment.getClass().getSimpleName() + ": Scroll top");
//                            CastingFragment castingFragment = (CastingFragment)
//                                    getSupportFragmentManager().findFragmentById(R.id.container);
                        CastingFragment castingFragment = (CastingFragment) currentFragment;
                        castingFragment.showTopPosition(0);
                        castingLastVisiblePosition = 0;
                    } else {
                        Log.i("FromStackActive", ":CastingFragment" + "---------CurrentFragment:::::" + currentFragment.getClass().getSimpleName());
                        fragmentBackOnFront("CastingFragment", currentFragment);
                    }
                } else {
                    Log.i("StartNew : ", "Start new fragment CastingFragment");
                    launchCasting(1);
                }
                return true;
            case R.id.action_more:
//                fragmentStateManager.changeFragment(4);
                hideKeyboard(KenbieActivity.this);
                navigationView.getMenu().getItem(4).setChecked(true);
                if (fragmentExist("ExtraFragment")) {
//                        Fragment activeFragment = getCurrentFragment();
                    updateActionBar(35, "PROFILE VISITOR", false, false, true);
                    if (currentFragment instanceof ExtraFragment) {
                        Log.i("CurrentFragment", currentFragment.getClass().getName() + ": Scroll top");
//                        ExtraFragment extraFragment = (ExtraFragment)
//                                    getSupportFragmentManager().findFragmentById(R.id.container);
                        ExtraFragment extraFragment = (ExtraFragment) currentFragment;
                        extraFragment.showTopPosition(0);
                    } else {
                        Log.i("FromStackActive", ":ExtraFragment" + "---------CurrentFragment:::::" + currentFragment.getClass().getName());
                        fragmentBackOnFront("ExtraFragment", currentFragment);
                    }
                } else {
                    Log.i("StartNew : ", "Start new fragment ExtraFragment");
                    replaceFragment(new ExtraFragment(), true, false);
                }
                return true;
        }
        return true;
    }

    private void handleNavigationType() {
        Bundle bundle;
        switch (navType) {
            case 1:
            case 3:
            case 4:
            case 5:
            case 6:
                // Navigation from message-1, 3-visitors, 4 - fav, 5 - like, 6 - any profile visit
                bindBottomNavigationData();
                Intent intent = new Intent(this, KenbieNavigationActivity.class);
                intent.putExtra("UserData", (UserItem) getIntent().getSerializableExtra("UserItem"));
                intent.putExtra("NavType", navType);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
/*            UserProfileFragment userProfileFragment = new UserProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("UserData", (UserItem) getIntent().getSerializableExtra("UserItem"));
            bundle.putInt("NavType", navType);
            userProfileFragment.setArguments(bundle);
            replaceFragment(userProfileFragment, false, false);*/
//            viewUserProfile((UserItem) getIntent().getSerializableExtra("UserItem"));
                break;
            case 2:
                replaceFragment(new ExtraFragment(), true, false);
                break;
            case 7:
                // Navigation from membership
                MemberShipInfoFragment memInfoFragment = new MemberShipInfoFragment();
                bundle = new Bundle();
                bundle.putSerializable("UserInfo", (UserItem) getIntent().getSerializableExtra("UserItem"));
                bundle.putInt("Type", getIntent().getIntExtra("Type", 1));
                memInfoFragment.setArguments(bundle);
                replaceFragment(memInfoFragment, false, false);
                break;
            case 8:
                replaceFragment(new MessageUserListFragment(), false, false);
                break;
            case 9:
                ReportAbuseToUserFragment reportAbuseToUserFragment = new ReportAbuseToUserFragment();
                bundle = new Bundle();
                bundle.putSerializable("ProfileInfo", getIntent().getSerializableExtra("ProfileInfo"));
                reportAbuseToUserFragment.setArguments(bundle);
                replaceFragment(reportAbuseToUserFragment, false, false);
                break;
            case 10:
                replaceFragment(new SearchFragment(), false, false);
                navigationView.getMenu().getItem(2).setChecked(true);
                break;
            case 11:
                ModelsFragment modelsFragment = new ModelsFragment();
                modelsFragment.setArguments(getIntent().getExtras());
                replaceFragment(modelsFragment, false, false);
                break;
            case 12:
                CastingDetailsFragment castingDetailsFragment = new CastingDetailsFragment();
                bundle = new Bundle();
                bundle.putSerializable("CastingDetails", getIntent().getSerializableExtra("CastingDetails"));
                bundle.putInt("Type", 1);
                castingDetailsFragment.setArguments(bundle);
                replaceFragment(castingDetailsFragment, false, false);
                break;
            case 15:
                SettingFragment settingFragment = new SettingFragment();
                bundle = new Bundle();
                bundle.putInt("Type", 1);
                settingFragment.setArguments(bundle);
                replaceFragment(settingFragment, false, false);
                break;
            default:
//            navigationView.getMenu().getItem(0).setChecked(true);
//            fragmentStateManager.changeFragment(0);
//            replaceFragment(new UserListFragment(), false, false);
                replaceFragment(new UserListFragment(), false, false);
                break;
        }
    }

    public void searchBackHandling() {
        Log.i("SearchBack : ", currentFragment.getClass().getSimpleName());
        if (lastFragment == null)
            isNavigateFragmentBackFromTop();
        else {
            Log.i("LastActive", ":SearchFragment" + "---------lastFragment:::::" + lastFragment.getClass().getSimpleName());
            fragmentBackOnFront(lastFragment.getClass().getSimpleName(), currentFragment);
        }
    }

    public void replaceFragment(Fragment fragment, boolean needToAddBackStack, boolean isReplace) {
        if (fragmentManager == null)
            fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (currentFragment != null) {
            fragmentTransaction.hide(currentFragment);
        }
        if (isReplace)
            fragmentTransaction.show(fragment);
        else {
            fragmentTransaction.add(container.getId(), fragment, fragment.getClass().getSimpleName());
            if (needToAddBackStack)
                fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.setPrimaryNavigationFragment(fragment);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();
        currentFragment = fragment;
    }

    private boolean fragmentExist(String tag) {
        boolean isAvailable = false;
        if (fragmentManager == null)
            fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null)
            isAvailable = true;

        return isAvailable;
    }

    private void fragmentBackOnFront(String tag, Fragment activeFragment) {
        if (fragmentManager == null)
            fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            replaceFragment(fragment, false, true);
            resumeFragment(fragment);
        }
    }

    private boolean isNavigateFragmentBack() {
        try {
            if (fragmentManager == null)
                fragmentManager = getSupportFragmentManager();
            String fragmentTag = null;
            if (fragmentManager.getBackStackEntryCount() > 0) {
                for (int i = fragmentManager.getBackStackEntryCount(); i > 0; ) {
                    String tag = fragmentManager.getBackStackEntryAt(--i).getName();
                    Fragment fragment = fragmentManager.findFragmentByTag(tag);
                    if (fragment != null && fragment.isVisible()) {
                        fragmentTag = tag;
                        break;
                    }
                }
            }
            if (fragmentTag == null)
                fragmentTag = "UserListFragment";

            if (fragmentTag != null) {
                currentFragment = fragmentManager.findFragmentByTag(fragmentTag);
                resumeFragment(currentFragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void resumeFragment(Fragment fragment) {
        if (fragment != null) {
            if (fragment instanceof BaseFragment)
                ((BaseFragment) fragment).resume();
            else
                fragment.onResume();
        }

        /*int index;
        if (currentFragment instanceof UserListFragment) {
            index = 0;
        } else if (fragment instanceof MessageUserListFragment) {
            index = 1;
        } else if (fragment instanceof SearchFragment) {
            index = 2;
        } else if (fragment instanceof CastingFragment) {
            index = 3;
        } else if (fragment instanceof ExtraFragment) {
            index = 4;
        } else {
            return;
        }
        navigationView.getMenu().getItem(index).setChecked(true);*/
    }

    public boolean isNavigateFragmentBackFromTop() {
        try {
            if (fragmentManager == null)
                fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            String fragmentTag = "";
            if (fragmentManager.getBackStackEntryCount() > 1)
                fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 2).getName();
            else
                fragmentTag = "UserListFragment";

            Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);

            fragmentTransaction.show(fragment);

            if (currentFragment != null && currentFragment != fragment)
                fragmentTransaction.hide(currentFragment);

            Log.i("SearchCurrentFragment", currentFragment.getClass().getName() + "::::::::SearchBackFragment" + fragment.getClass().getName());

            fragmentTransaction.setPrimaryNavigationFragment(fragment);
            fragmentTransaction.setReorderingAllowed(true);
            fragmentTransaction.commit();
            resumeFragment(fragment);
//            mFragmentManager.executePendingTransactions();
            currentFragment = fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPref.getBoolean("isLogin", false)) {
            navigationView.setVisibility(View.VISIBLE);
            guestBottomOptions.setVisibility(View.GONE);

            refreshUserProfile(mPref.getString("ProfilePic", ""));

//            if (navType == 3) {
//                navigationView.getMenu().getItem(4).setChecked(true);
//            }

            // Sync bottom notification data
            gettingProfileComplete();
//            isNavigateFragmentBack();
        } else {
            token = FirebaseInstanceId.getInstance().getToken();
            guestBottomOptions.setVisibility(View.VISIBLE);
            navigationView.setVisibility(View.GONE);
        }
        try {
            if (mMessageReceiver != null)
                registerReceiver(mMessageReceiver, new IntentFilter(NOTIFY_ACTIVITY_ACTION));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        try {
            if (mMessageReceiver != null)
                unregisterReceiver(mMessageReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TitleBarEvent event) {
        updateTitleBar(mTopBarBinding, event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ProfilePicEvent event) {
        updateUserImage(mTopBarBinding, event);
    }

    public void setupTitleBar(TopHeaderBarBinding binding) {
        if (binding != null) {
            binding.mTitle.setTypeface(KenbieApplication.S_NORMAL);
            binding.actionSearch.setVisibility(View.VISIBLE);
            if (mPref.getBoolean("isLogin", false))
                binding.backButton.setBackgroundResource(R.drawable.ic_v_back);
            else
                binding.backButton.setBackgroundResource(R.drawable.ic_v_gray_back);

            binding.backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == 0) {
                        if (!mPref.getBoolean("isLogin", false)) {
                            Intent i = new Intent(KenbieActivity.this, LoginOptionsActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }
                        finish();
                    } else {
                        //if (type == 66) // Casting Details
//                        if (getFragmentManager().getBackStackEntryCount() > 0) {
//                            getFragmentManager().popBackStack();
//                        } else {
//                            onBackPressed();
//                        }
                        onBackPressed();
                    }
                }
            });
            binding.actionSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPref.getBoolean("isLogin", false)) {
//                        viewSettings();
                        Intent intent = new Intent(KenbieActivity.this, KenbieNavigationActivity.class);
                        intent.putExtra("NavType", 15);
                        intent.putExtra("Type", 1);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else
                        replaceFragment(new SearchFragment(), true, false);
                }
            });
            binding.userImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewEditProfile();
                }
            });
        }
    }

    public void updateTitleBar(TopHeaderBarBinding binding, TitleBarEvent event) {
        try {
            if (binding != null) {
                if (event.isBackEnable())
                    binding.backButton.setVisibility(View.VISIBLE);
                else
                    binding.backButton.setVisibility(View.GONE);

                if (mPref.getBoolean("isLogin", false)) {
                    if (type == 0) {
                        binding.userImg.setVisibility(View.VISIBLE);
                        binding.actionSearch.setVisibility(View.VISIBLE);
                        binding.actionSearch.setBackgroundResource(R.drawable.ic_setting);
                    } else if (event.isSearchEnable()) {
                        binding.userImg.setVisibility(View.GONE);
                        binding.actionSearch.setVisibility(View.VISIBLE);
                        binding.actionSearch.setBackgroundResource(R.drawable.ic_v_search);
                    } else {
                        binding.actionSearch.setVisibility(View.INVISIBLE);
                        binding.userImg.setVisibility(View.GONE);
                    }

                    if (event.getTitle() == null) {
                        binding.mTitle.setVisibility(View.INVISIBLE);
                        binding.appLogo.setVisibility(View.VISIBLE);
                    } else {
                        binding.mTitle.setText(event.getTitle());
                        binding.mTitle.setVisibility(View.VISIBLE);
                        binding.appLogo.setVisibility(View.INVISIBLE);
                    }
                } else if (type == 0) { // Home
                    binding.backButton.setVisibility(View.VISIBLE);
                    binding.actionSearch.setVisibility(View.VISIBLE);
                    binding.actionSearch.setBackgroundResource(R.drawable.ic_v_search);
                    binding.userImg.setVisibility(View.GONE);
                    binding.mTitle.setVisibility(View.INVISIBLE);
                    binding.appLogo.setVisibility(View.VISIBLE);
                } else {
                    binding.userImg.setVisibility(View.GONE);
                    binding.actionSearch.setVisibility(View.INVISIBLE);
                    if (event.getTitle() == null) {
                        binding.mTitle.setVisibility(View.INVISIBLE);
                        binding.appLogo.setVisibility(View.VISIBLE);
                    } else {
                        binding.mTitle.setText(event.getTitle());
                        binding.mTitle.setVisibility(View.VISIBLE);
                        binding.appLogo.setVisibility(View.INVISIBLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUserImage(TopHeaderBarBinding binding, ProfilePicEvent event) {
        try {
            if (binding != null) {
                RequestOptions options = new RequestOptions()
                        .optionalCircleCrop()
                        .placeholder(getResources().getDrawable(R.drawable.img_c_user_dummy))
                        .priority(Priority.HIGH);
                Glide.with(this).load(event.getImageUrl()).apply(options).into(binding.userImg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // Extract data included in the Intent
//            String message = intent.getStringExtra("message");

            if (intent != null && intent.getAction() != null && intent.getAction().equals(NOTIFY_ACTIVITY_ACTION)) {
                gettingProfileComplete();
                BadgeUtils.clearBadge(KenbieActivity.this);

                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                assert notificationManager != null;
                notificationManager.cancelAll();

                try {
//                    MessageUserListFragment messageUserListFragment = (MessageUserListFragment)
//                            getSupportFragmentManager().findFragmentById(R.id.container);
                    if (currentFragment != null && currentFragment instanceof MessageUserListFragment)
                        ((MessageUserListFragment) currentFragment).refreshFromNotification();
//                    notificationManager.cancel(NOTIFICATION_ID);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            //do other stuff here
        }
    };

    public void launchCasting(int type) {
        CastingFragment castingFragment = new CastingFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("Type", type);
        castingFragment.setArguments(bundle);
        replaceFragment(castingFragment, true, false);
    }

    public void viewSettings() {
//        mNavigationDrawerFragment.closeAndOpenNavPanel();
        viewSettingOptions(1);
    }

    public void viewEditProfile() {
//        mNavigationDrawerFragment.closeAndOpenNavPanel();
        UserItem userItem = new UserItem();
        userItem.setId(Integer.valueOf(mPref.getString("UserId", "0")));
        viewUserProfile(userItem);
//        Intent intent = new Intent(KenbieActivity.this, KenbieNavigationActivity.class);
//        intent.putExtra("UserItem", userItem);
//        intent.putExtra("NavType", 1);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
    }

    public void viewSettingOptions(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("Type", type);
        Intent intent = new Intent(this, KenbieNavigationActivity.class);
        intent.putExtra("NavType", 15);
        intent.putExtras(bundle);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

//        SettingFragment settingFragment = new SettingFragment();
//        Bundle bundle = new Bundle();
//        bundle.putInt("Type", type);
//        settingFragment.setArguments(bundle);
//        replaceFragment(settingFragment, true, false);
    }

    // Edit options fragment
    public void seeEditOptions(ProfileInfo profileInfo) {
//        EditProfileFragment profileFragment = new EditProfileFragment();
//        Bundle b = new Bundle();
//        b.putSerializable("ProfileInfo", profileInfo);
//        profileFragment.setArguments(b);
//        replaceFragment(profileFragment, true, false);
        Intent intent = new Intent(KenbieActivity.this, KenbieNavigationActivity.class);
        intent.putExtra("ProfileInfo", profileInfo);
        intent.putExtra("NavType", 16);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // Edit basic info
    public void editBasicInfo(ProfileInfo profileInfo, int type) {
        EditBasicInfoFragment editBasicProfileFragment = new EditBasicInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("ProfileInfo", profileInfo);
        bundle.putInt("NType", type);
        editBasicProfileFragment.setArguments(bundle);
        replaceFragment(editBasicProfileFragment, true, false);
    }

    // Celebrate Payment Process
    public void startPaymentProcess(int type) {
        SubscriptionPmtFragment subscriptionPmtFragment = new SubscriptionPmtFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("PaymentType", type);
        subscriptionPmtFragment.setArguments(bundle);
        replaceFragment(subscriptionPmtFragment, true, false);
    }

    // Logout functionality
    public void logoutProcess() {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("UserId", "0");
        editor.putString("LoginKey", "");
        editor.putString("LoginToken", "");
        editor.putString("ProfilePic", "");
        editor.putString("DeviceId", "");
        editor.putBoolean("isLogin", false);
        editor.putBoolean("GuestLogin", false);
        editor.apply();
        Intent intent = new Intent(KenbieActivity.this, LoginOptionsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }

    // View user profile
    public void viewUserProfile(UserItem userItem) {
        Intent intent = new Intent(this, KenbieNavigationActivity.class);
        intent.putExtra("UserItem", userItem);
        intent.putExtra("NavType", 1);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
//        UserProfileFragment userProfileFragment = new UserProfileFragment();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("UserData", userItem);
//        userProfileFragment.setArguments(bundle);
//        replaceFragment(userProfileFragment, true, false);
    }

    // View celebrities payment screen
    public void showMemberDialog(final UserItem userItem) {
        try {
            final Dialog dialog = new Dialog(KenbieActivity.this);
            dialog.setCancelable(true);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_with_cross_btn);
            if (dialog.getWindow() != null)
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView mTitle = (TextView) dialog.findViewById(R.id.m_title);
            mTitle.setTypeface(KenbieApplication.S_BOLD);

            TextView mSubTitle = (TextView) dialog.findViewById(R.id.m_sub_title);
            mSubTitle.setTypeface(KenbieApplication.S_SEMI_BOLD);

            TextView mMsg = (TextView) dialog.findViewById(R.id.m_msg);
            mMsg.setTypeface(KenbieApplication.S_SEMI_LIGHT);

            TextView mMemBtn = (TextView) dialog.findViewById(R.id.m_mem_btn);
            mMemBtn.setTypeface(KenbieApplication.S_NORMAL);
            mMemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    showMemberShipInfo(userItem, 1); // Membership
                }
            });

            ((ImageView) dialog.findViewById(R.id.m_cross_btn)).setOnClickListener(new View.OnClickListener() {
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

    // Show celebrity info
    public void showCelebrityInfo(UserItem userItem, int type) {
        Intent intent = new Intent(this, KenbieNavigationActivity.class);
        intent.putExtra("UserInfo", userItem);
        intent.putExtra("CelebrityData", celebritiesList);
        intent.putExtra("RandomCelebrityData", randomCelebrities);
        intent.putExtra("Type", type);
        intent.putExtra("NavType", 21);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
//        CelebrityInfoFragment memInfoFragment = new CelebrityInfoFragment();
//        Bundle b = new Bundle();
//        b.putSerializable("UserInfo", userItem);
//        b.putInt("Type", type);
//        memInfoFragment.setArguments(b);
//        replaceFragment(memInfoFragment, true, false);
    }

    // Show membership info
    public void showMemberShipInfo(UserItem userItem, int type) {
        Intent intent = new Intent(this, KenbieNavigationActivity.class);
        intent.putExtra("NavType", 7);
        intent.putExtra("Type", type);
        intent.putExtra("UserItem", userItem);
        startActivity(intent);

//        MemberShipInfoFragment memInfoFragment = new MemberShipInfoFragment();
//        Bundle b = new Bundle();
//        b.putSerializable("UserInfo", userItem);
//        b.putInt("Type", type);
//        memInfoFragment.setArguments(b);
//        replaceFragment(memInfoFragment, true, false);
    }

    // Action bar update details
    public void updateActionBar(int type, String title, boolean isSearchEnable, boolean backEnable, boolean actionBarHide) {
        this.type = type;
        if (mPref.getBoolean("isLogin", false)) {
            if (type == 0) {
                navigationView.getMenu().getItem(0).setChecked(true);
            } else if (type == 22) {
                navigationView.getMenu().getItem(1).setChecked(true);
            } else if (type == 6) {
                navigationView.getMenu().getItem(3).setChecked(true);
            } else if (type == 35) {
                navigationView.getMenu().getItem(4).setChecked(true);
            } else if (type == 2)
                navigationView.getMenu().getItem(2).setChecked(true);
        }
        actionBarHide = actionBarHide || currentFragment instanceof UserListFragment || currentFragment instanceof MessageUserListFragment;
        mTopBarBinding.topBar.setVisibility(actionBarHide ? View.GONE : View.VISIBLE);
        EventBus.getDefault().post(new TitleBarEvent(title, isSearchEnable, backEnable));
        hideKeyboard(KenbieActivity.this);
    }

    public boolean isExtraFragment(int index) {
        if (currentFragment instanceof ExtraFragment) {
            return currentFragment.isVisible() && moreTab == index;
        }
        return false;
    }

    public void shareApp(String message) {
        try {
            Intent sendIntent1 = new Intent();
            sendIntent1.setAction(Intent.ACTION_SEND);
            sendIntent1.putExtra(Intent.EXTRA_SUBJECT, "Kenbie App");
//            sendIntent1.putExtra(Intent.EXTRA_TEXT, "Please download app for more fun. https://play.google.com/store/apps/details?id=com.kenbie&hl=en");
            sendIntent1.putExtra(Intent.EXTRA_TEXT, "https://kenbie.com/" + message);
            sendIntent1.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent1, "Invite A Friend"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Start chat with user
    public void startMessaging(ProfileInfo profileInfo) {
        MsgUserItem msgUserItem = new MsgUserItem();
        msgUserItem.setUid(profileInfo.getId());
        msgUserItem.setUser_name(profileInfo.getFirst_name());
        msgUserItem.setUser_img(profileInfo.getUser_pic());
        Intent intent = new Intent(KenbieActivity.this, MessageConvActivity.class);
        intent.putExtra("MsgItem", msgUserItem);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

//        replaceFragment(new MessageUserListFragment(), true, false);
    }

    // Social Fragments
    public void seeSocialOptions(ArrayList<OptionsData> userSocial) {
        SocialLinksFragment socialLinksFragment = new SocialLinksFragment();
        Bundle b = new Bundle();
        b.putSerializable("SocialData", userSocial);
        socialLinksFragment.setArguments(b);
        replaceFragment(socialLinksFragment, true, false);
    }

    // User Statistics Fragment
    public void seeUserStatisticsFragment(ProfileInfo profileInfo) {
        UserStatisticsFragment userStatisticsFragment = new UserStatisticsFragment();
        Bundle b = new Bundle();
        b.putSerializable("ProfileInfo", profileInfo);
        userStatisticsFragment.setArguments(b);
        replaceFragment(userStatisticsFragment, true, false);
    }

    // Language List
    public void viewLanguageUpdateList(ProfileInfo profileInfo) {
        UpdateLanguageListFragment updateLanguageListFragment = new UpdateLanguageListFragment();
        Bundle b = new Bundle();
        b.putSerializable("ProfileInfo", profileInfo);
        updateLanguageListFragment.setArguments(b);
        replaceFragment(updateLanguageListFragment, true, false);
    }

    // 3-"Casting Settings", 5 - "Who Can See The Profile?", 8 - Discipline, 9 - Categories
    public void updateUserSettings(int type, ProfileInfo profileInfo) {
        SettingUpdateFragment settingUpdateFragment = new SettingUpdateFragment();
        Bundle b = new Bundle();
        b.putInt("Type", type);
        b.putSerializable("ProfileInfo", profileInfo);
        settingUpdateFragment.setArguments(b);
        replaceFragment(settingUpdateFragment, true, false);
    }

    public void updateUserInformation(ProfileInfo profileInfo) {
        EditUserInfoFragment editUserInfoFragment = new EditUserInfoFragment();
        Bundle b = new Bundle();
        b.putSerializable("ProfileInfo", profileInfo);
        editUserInfoFragment.setArguments(b);
        replaceFragment(editUserInfoFragment, true, false);
    }

    public void updateUserAboutInfo(ProfileInfo profileInfo) {
        AboutEditFragment aboutEditFragment = new AboutEditFragment();
        Bundle b = new Bundle();
        b.putSerializable("ProfileInfo", profileInfo);
        aboutEditFragment.setArguments(b);
        replaceFragment(aboutEditFragment, true, false);
    }

    public void viewPhotoGallery(ProfileInfo profileInfo) {
        PhotoGalleryFragment photoGalleryFragment = new PhotoGalleryFragment();
        Bundle b = new Bundle();
        b.putSerializable("ProfileInfo", profileInfo);
        photoGalleryFragment.setArguments(b);
        replaceFragment(photoGalleryFragment, true, false);
    }

    public void callChangePwdFragment() {
        replaceFragment(new PasswordChangeFragment(), true, false);
    }

    public void updateLanguage() {
        replaceFragment(new LanguageFragment(), true, false);
    }

    // Launch casting info page
    public void startAddingCasting(CastingUser castingUser, int type, boolean paymentNow) {
        Intent intent = new Intent(this, KenbieNavigationActivity.class);
        intent.putExtra("CastingUser", castingUser);
        intent.putExtra("NavType", 20);
        intent.putExtra("Type", type);
        intent.putExtra("PaymentNow", paymentNow);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

//        AddCastingInfoFragment addCastingInfoFragment = new AddCastingInfoFragment();
//        Bundle b = new Bundle();
//        b.putSerializable("CastingUser", castingUser);
//        b.putInt("Type", type);
//        b.putBoolean("PaymentNow", paymentNow);
//        addCastingInfoFragment.setArguments(b);
//        replaceFragment(addCastingInfoFragment, true, false);
    }

    // Apply sort on list data
    public void applySort(int type) { // 1 - sort, 2 - type
        SortFragment sortFragment = new SortFragment();
        Bundle b = new Bundle();
        b.putInt("Type", type);
        sortFragment.setArguments(b);
        replaceFragment(sortFragment, true, false);
    }

    // Action report abuse
    public void userReportAbuse(ProfileInfo profileInfo) {
        Intent intent = new Intent(this, KenbieNavigationActivity.class);
        intent.putExtra("ProfileInfo", profileInfo);
        intent.putExtra("NavType", 9);
        startActivity(intent);
    }

    public void gettingProfileComplete() {
        if (isOnline() && mPref.getBoolean("isLogin", false)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_id", mPref.getString("UserId", ""));
            params.put("login_key", mPref.getString("LoginKey", ""));
            params.put("login_token", mPref.getString("LoginToken", ""));
            mConnection.postRequestWithHttpHeaders(this, "getUserProfileComplete", this, params, 101);
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
                        moreCount = moreCount + jProfile.getInt("visitor_count");
                    }

                    if (jProfile.has("fav_count")) {
                        editor.putInt("FavCount", jProfile.getInt("fav_count"));
                        moreCount = moreCount + jProfile.getInt("fav_count");
                    }

                    if (jProfile.has("is_paid"))
                        editor.putInt("MemberShip", jProfile.getInt("is_paid"));

                    if (jProfile.has("subs_payment"))
                        editor.putInt("SubsPayment", (int) (jProfile.getDouble("subs_payment") * 100));
                    if (jProfile.has("celeb_payment"))
                        editor.putInt("CelebPayment", (int) (jProfile.getDouble("celeb_payment") * 100));
                    if (jProfile.has("cast_payment"))
                        editor.putInt("CastPayment", (int) (jProfile.getDouble("cast_payment") * 100));

                    if (jProfile.has("user_pic")) {
                        refreshCelebrityImage(jProfile.getString("user_pic"));
                        editor.putString("ProfilePic", jProfile.getString("user_pic"));
                    }

                    editor.putInt("MoreCount", moreCount);
                    editor.apply();

                    bindBottomNavigationData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshCelebrityImage(String user_pic) {
        try {
            if (user_pic != null && !mPref.getString("ProfilePic", "").equalsIgnoreCase(user_pic)) {
                refreshUserProfile(user_pic);
//                UserListFragment userListFragment = (UserListFragment)
//                        getSupportFragmentManager().findFragmentById(R.id.container);
                if (currentFragment != null && currentFragment instanceof UserListFragment)
                    ((UserListFragment) currentFragment).refreshCelebrityData(user_pic);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void networkError(String error, int APICode) {

    }

    public void refreshUserProfile(String profile) {
        EventBus.getDefault().post(new ProfilePicEvent(Constants.BASE_IMAGE_URL + profile));
    }

    public void bindBottomNavigationData() {
        try {
            clearNotification();
            if (mPref.getInt("UnreadMsg", 0) > 0) {
                BadgeDrawable badge = navigationView.getOrCreateBadge(R.id.action_chat);
                if (badge != null) {
                    badge.setNumber(mPref.getInt("UnreadMsg", 0));
                    badge.setBackgroundColor(getResources().getColor(R.color.red_g_color));
                    badge.setBadgeTextColor(getResources().getColor(R.color.white));
                }
            } else
                navigationView.removeBadge(R.id.action_chat);

            if (mPref.getInt("MoreCount", 0) > 0) {
                BadgeDrawable badge = navigationView.getOrCreateBadge(R.id.action_more);
                if (badge != null) {
                    if (mPref.getInt("MoreCount", 0) > 0)
                        badge.setNumber(mPref.getInt("MoreCount", 0));
                    else
                        navigationView.removeBadge(R.id.action_more);

                    badge.setBackgroundColor(getResources().getColor(R.color.red_g_color));
                    badge.setBadgeTextColor(getResources().getColor(R.color.white));
                }
            } else
                navigationView.removeBadge(R.id.action_more);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveProfileUpdateCount(String other_data, int type) {
        clearNotification();
        try {
            JSONObject jo = new JSONObject(other_data);
            int moreCount = 0;
            SharedPreferences.Editor editor = mPref.edit();

            if (jo.has("unread_msg_count"))
                editor.putInt("UnreadMsg", jo.getInt("unread_msg_count"));

            if (jo.has("totalliked")) {
                editor.putInt("TotalLiked", jo.getInt("totalliked"));
                moreCount = jo.getInt("totalliked");
            }

            if (jo.has("visitor_count")) {
                editor.putInt("VisitorCount", jo.getInt("visitor_count"));
                moreCount = moreCount + jo.getInt("visitor_count");
            }

            if (jo.has("fav_count")) {
                editor.putInt("FavCount", jo.getInt("fav_count"));
                moreCount = moreCount + jo.getInt("fav_count");
            }

            editor.putInt("MoreCount", moreCount);
            editor.apply();

            if (type == 1) // From Profile
                bindBottomNavigationData();

            if (type == 2) { // From Visitors
                bindBottomNavigationData();

//                ExtraFragment extraFragment = (ExtraFragment)
//                        getSupportFragmentManager().findFragmentById(R.id.container);

                if (currentFragment != null && currentFragment instanceof ExtraFragment)
                    resumeFragment(currentFragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshTopAndBottomCount(int favStatus, int type) {
        clearNotification();
        try {
            SharedPreferences.Editor editor = mPref.edit();
            int favCount = mPref.getInt("FavCount", 0);
            int moreCount = mPref.getInt("MoreCount", 0);
            if (favStatus == -1) {
                if (favCount > 0 && moreCount > 0) {
                    editor.putInt("FavCount", (mPref.getInt("FavCount", 0) + favStatus));
                    editor.putInt("MoreCount", (mPref.getInt("MoreCount", 0) + favStatus));
                }
            } else {
                editor.putInt("FavCount", (mPref.getInt("FavCount", 0) + favStatus));
                editor.putInt("MoreCount", (mPref.getInt("MoreCount", 0) + favStatus));
            }
            editor.apply();

            bindBottomNavigationData();
            if (type == 2 || type == 3) {
//                ExtraFragment extraFragment = (ExtraFragment)
//                        getSupportFragmentManager().findFragmentById(R.id.container);
                if (currentFragment != null && currentFragment instanceof ExtraFragment)
                    resumeFragment(currentFragment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showProgressDialog(boolean isShow) {
        try {
            if (isShow) {
//                mProgress.setMessage("Please wait...");
                mProgress.show();
            } else
                mProgress.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgress != null && mProgress.isShowing())
            mProgress.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("Notification", false)) {
//                removeAllNotificationData();
            getIntent().putExtra("Notification", false);
            getIntent().putExtra("type", 0);
            replaceFragment(new UserListFragment(), false, false);
        } else {
            try {
                super.onBackPressed();
                isNavigateFragmentBack();
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }
    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return super.onNavigateUp();
    }

    public void clearNotification() {
        try {
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.cancelAll();
            BadgeUtils.clearBadge(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Parse search data
    public ArrayList<UserItem> getParseSearchData(String data, int type) {
        ArrayList<UserItem> values = new ArrayList<>();
        try {
            JSONArray jData = new JSONArray(data);
            for (int i = 0; i < jData.length(); i++) {
                JSONObject jo = new JSONObject(jData.getString(i));
                UserItem value = new UserItem();
                value.setId(jo.getInt("id"));
                value.setFirstName(jo.getString("first_name"));
                if (jo.getString("birth_year") != null && !jo.getString("birth_year").equalsIgnoreCase("null") && jo.getString("birth_year").length() > 0) {
                    value.setBirthYear(utility.getYearsCountFromDate(jo.getString("birth_year"), jo.getString("birth_month"), jo.getString("birth_day")));
                }
//                if (jo.getString("birth_year") != null && !jo.getString("birth_year").equalsIgnoreCase("null") && jo.getString("birth_year").length() > 0)
//                    value.setBirthYear(jo.getInt("birth_year"));
//                value.setBirthMonth(jo.getInt("birth_month"));
//                value.setBirthDay(jo.getInt("birth_day"));
                value.setTotalImage(jo.getInt("total_image"));
                value.setCity(jo.getString("city"));
                value.setCountry(jo.getString("country"));
                value.setIsActive(jo.getInt("is_active"));
                value.setUserPic(jo.getString("user_pic"));
                value.setType(type);
                values.add(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }
}
