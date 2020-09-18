package com.kenbie.adapters;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.kenbie.fragments.MessageUserListFragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rajaw on 9/12/2017.
 */

public class MyMessageFragmentAdapter extends FragmentStatePagerAdapter {
    private ArrayList<String> mTabs = null;
    private HashMap<Integer, String> mFragmentTags = null;
    private FragmentManager mFragmentManager;

    public MyMessageFragmentAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
        mFragmentTags = new HashMap<Integer, String>();
        mTabs = new ArrayList<>();
        mTabs.add("ALL");
        mTabs.add("ONLINE");
        mTabs.add("FAVOURITE");
    }

    @Override
    public Fragment getItem(int index) {
        MessageUserListFragment msgUserListFragment = new MessageUserListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("Type", (index + 1));
        msgUserListFragment.setArguments(bundle);
        return msgUserListFragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof Fragment) {
            // record the fragment tag here.
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            mFragmentTags.put(position, tag);
        }
        return obj;
    }

    public Fragment getFragment(int position) {
        String tag = mFragmentTags.get(position);
        if (tag == null)
            return null;
        return mFragmentManager.findFragmentByTag(tag);
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position);
    }

}
