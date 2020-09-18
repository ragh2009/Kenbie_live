package com.kenbie.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.kenbie.R;
import com.kenbie.adapters.MyMessageFragmentAdapter;

public class MessageFragment extends BaseFragment {


    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = (TabLayout)view. findViewById(R.id.tabs);
        tabLayout.setVisibility(View.VISIBLE);
        final ViewPager msgViewPager = (ViewPager)view. findViewById(R.id.msg_list_pager);
        msgViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Fragment fragment = ((MyMessageFragmentAdapter) msgViewPager.getAdapter()).getFragment(position);
                if (fragment != null)
                    fragment.onResume();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        MyMessageFragmentAdapter mFragmentAdapter = new MyMessageFragmentAdapter(mActivity.getSupportFragmentManager());
        msgViewPager.setAdapter(mFragmentAdapter);
        tabLayout.setupWithViewPager(msgViewPager);
    }

    @Override
    public void onResume() {
        mActivity.updateActionBar(2, "MESSAGES", false, false, false);
        super.onResume();
    }
}
