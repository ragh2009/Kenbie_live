package com.kenbie.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.kenbie.R;
import com.kenbie.adapters.ExtraFragmentAdapter;

public class ExtraFragment extends BaseFragment {
    private TabLayout tabLayout = null;
    private ExtraFragmentAdapter mFragmentAdapter = null;
    private ViewPager msgViewPager = null;

    public ExtraFragment() {
        // Required empty public constructor
    }

    public static ExtraFragment newInstance() {
        // Required empty public constructor
         return new ExtraFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_extra, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setVisibility(View.VISIBLE);

        msgViewPager = (ViewPager) view.findViewById(R.id.extra_option_pager);
        msgViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("scrolled::::::::::::", position + "");
//                kActivity.moreTab = position;
//                Fragment fragment = mFragmentAdapter.getFragment(position);
//
//                if (fragment != null)
//                    fragment.onResume();
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("selected::::::::::::", position + "");
                kActivity.moreTab = position;

//                Fragment fragment = ((ExtraFragmentAdapter) msgViewPager.getAdapter()).getFragment(position);
//
//                if (fragment != null)
//                    fragment.onResume();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("state::::::::::::", state + "");
            }
        });
//        msgViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        mFragmentAdapter = new ExtraFragmentAdapter(getChildFragmentManager(), kActivity);
//        mFragmentAdapter = new ExtraFragmentAdapter(kActivity.getChildFragmentManager, kActivity);
        msgViewPager.setAdapter(mFragmentAdapter);
        tabLayout.setupWithViewPager(msgViewPager);
        msgViewPager.setCurrentItem(kActivity.moreTab);
        kActivity.updateActionBar(35, "PROFILE VISITOR", false, false, false);
    }

    private void updatedBadges() {
        try {
            if (kActivity.mPref.getInt("VisitorCount", 0) > 0) {
                BadgeDrawable badgeDrawable = tabLayout.getTabAt(0).getOrCreateBadge();
                badgeDrawable.setVisible(true);
                badgeDrawable.setBackgroundColor(getResources().getColor(R.color.red_g_color));
                badgeDrawable.setBadgeTextColor(getResources().getColor(R.color.white));
                if (kActivity.mPref.getInt("VisitorCount", 0) > 0)
                    badgeDrawable.setNumber(kActivity.mPref.getInt("VisitorCount", 0));
                else
                    tabLayout.getTabAt(0).removeBadge();
            } else
                tabLayout.getTabAt(0).removeBadge();

            if (kActivity.mPref.getInt("TotalLiked", 0) > 0) {
                BadgeDrawable badgeDrawable = tabLayout.getTabAt(1).getOrCreateBadge();
                badgeDrawable.setVisible(true);
                badgeDrawable.setBackgroundColor(getResources().getColor(R.color.red_g_color));
                badgeDrawable.setBadgeTextColor(getResources().getColor(R.color.white));
                if (kActivity.mPref.getInt("TotalLiked", 0) > 0)
                    badgeDrawable.setNumber(kActivity.mPref.getInt("TotalLiked", 0));
                else
                    tabLayout.getTabAt(1).removeBadge();
            } else
                tabLayout.getTabAt(1).removeBadge();

            if (kActivity.mPref.getInt("FavCount", 0) > 0) {
                BadgeDrawable badgeDrawable = tabLayout.getTabAt(2).getOrCreateBadge();
                badgeDrawable.setVisible(true);
                badgeDrawable.setBackgroundColor(getResources().getColor(R.color.red_g_color));
                badgeDrawable.setBadgeTextColor(getResources().getColor(R.color.white));
                if (kActivity.mPref.getInt("FavCount", 0) > 0)
                    badgeDrawable.setNumber(kActivity.mPref.getInt("FavCount", 0));
                else
                    tabLayout.getTabAt(2).removeBadge();
            } else
                tabLayout.getTabAt(2).removeBadge();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updatedBadges();
        if (isInit || isVisible()) {
            isInit = false;
            kActivity.bindBottomNavigationData();
            kActivity.updateActionBar(35, "PROFILE VISITOR", false, false, true);
        }
    }

    public void showTopPosition(int i) {
        try {
            switch (tabLayout.getSelectedTabPosition()) {
                case 0:
                    msgViewPager.setCurrentItem(0);
                    VisitorUserFragment visitorUserFragment = (VisitorUserFragment) mFragmentAdapter.getFragment(0);
                    visitorUserFragment.refreshToStart();
                    break;
                case 1:
                    msgViewPager.setCurrentItem(1);
                    UserGridFragment userGridFragment = (UserGridFragment) mFragmentAdapter.getFragment(1);
                    userGridFragment.refreshToStart();
                    break;
                case 2:
                    msgViewPager.setCurrentItem(2);
                    UserGridFavFragment userGridFavFragment = (UserGridFavFragment) mFragmentAdapter.getFragment(2);
                    userGridFavFragment.refreshToStart();
                    break;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLastFragment(int type) {
        try {
            switch (type) {
                case 0:
                    msgViewPager.setCurrentItem(0);
                    VisitorUserFragment visitorUserFragment = (VisitorUserFragment) mFragmentAdapter.getFragment(0);
                    visitorUserFragment.refreshToStart();
                    break;
                case 1:
                    msgViewPager.setCurrentItem(1);
                    UserGridFragment userGridFragment = (UserGridFragment) mFragmentAdapter.getFragment(1);
                    userGridFragment.refreshToStart();
                    break;
                case 2:
                    msgViewPager.setCurrentItem(2);
                    UserGridFavFragment userGridFavFragment = (UserGridFavFragment) mFragmentAdapter.getFragment(2);
                    userGridFavFragment.refreshToStart();
                    break;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*   @Override
    public void onResume() {
//        kActivity.updateActionBar(2, "PROFILE VISITOR", false, false);
        msgViewPager.setCurrentItem(0);
//        mFragmentAdapter.set
        super.onResume();
    }*/


/*    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }*/

}
