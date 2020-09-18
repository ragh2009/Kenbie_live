package com.kenbie.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.kenbie.fragments.ExtraFragment;
import com.kenbie.fragments.MessageUserListFragment;
import com.kenbie.fragments.UserGridFavFragment;
import com.kenbie.fragments.UserGridFragment;
import com.kenbie.fragments.VisitorUserFragment;
import com.kenbie.listeners.InfoListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by rajaw on 9/12/2017.
 */

public class ExtraFragmentAdapter extends FragmentPagerAdapter {
    private ArrayList<String> mTabs = null;
    private HashMap<Integer, String> mFragmentTags = null;
    private FragmentManager mFragmentManager;

    public ExtraFragmentAdapter(FragmentManager fm, Context context) {
        super(fm);
        mFragmentManager = fm;
        mFragmentTags = new HashMap<Integer, String>();
        SharedPreferences mPref = context.getSharedPreferences("kPrefs", MODE_PRIVATE);
        mTabs = new ArrayList<>();
        mTabs.add(mPref.getString("145","VISITORS")+" ");
        mTabs.add(mPref.getString("146","LIKED YOU")+" ");
        mTabs.add(mPref.getString("147","FAVOURITE")+" ");
//        if (type == 1) // Favorites
//        if (type == 2) // Get Liked You
//        if (type == 3) // Get Visitors
    }

    @Override
    public Fragment getItem(int index) {
        if (index == 0) {
//            mInfoListener.getInfoValue((3 - index), 0);
            VisitorUserFragment visitorUserFragment = new VisitorUserFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Type", 3);
            visitorUserFragment.setArguments(bundle);
            return visitorUserFragment;
        } else if(index == 1){
            UserGridFragment gridFragment = new UserGridFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Type", 2);
            gridFragment.setArguments(bundle);
            return gridFragment;
        } else {
            UserGridFavFragment gridFavFragment = new UserGridFavFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Type", 1);
            gridFavFragment.setArguments(bundle);
            return gridFavFragment;
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof Fragment) {
            // record the fragment tag here.
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            mFragmentTags.put(position, tag);
        }
        return obj;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
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
