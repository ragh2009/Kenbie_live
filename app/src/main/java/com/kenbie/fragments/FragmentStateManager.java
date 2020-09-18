package com.kenbie.fragments;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kenbie.fragments.BaseFragment;


public abstract class FragmentStateManager {
    private static final String TAG = "FragmentStateManager";
    private static final boolean DEBUG = false;

    private final FragmentManager mFragmentManager;
    private BaseFragment activeFragment;
    private ViewGroup container;

    protected FragmentStateManager(ViewGroup container, FragmentManager fm) {
        mFragmentManager = fm;
        this.container = container;
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    public abstract BaseFragment getItem(int position);

    public BaseFragment changeFragment(int position) {
        String tag = makeFragmentName(container.getId(), position);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        BaseFragment fragment = (BaseFragment) mFragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = getItem(position);
            fragmentTransaction.add(container.getId(), fragment, tag);
        } else {
            fragmentTransaction.show(fragment);
        }

        if (activeFragment != null && activeFragment != fragment) {
            fragmentTransaction.hide(activeFragment);
        }

        activeFragment = fragment;

        // Set fragment as primary navigator for child manager back stack to be handled by system
        fragmentTransaction.setPrimaryNavigationFragment(fragment);
        fragmentTransaction.setReorderingAllowed(true);
        fragmentTransaction.commit();

        //  fragment.onFragmentBroughtForward();

        return fragment;
    }

    /**
     * Removes Fragment from Fragment Manager and clears all saved states. Call to changeFragment()
     * will restart fragment from fresh state.
     *
     * @param position
     */
    public void removeFragment(int position) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment fragmentData = mFragmentManager.findFragmentByTag(makeFragmentName(container.getId(), position));
        if (fragmentData != null) {
            fragmentTransaction.remove(fragmentData);
            fragmentTransaction.commitNowAllowingStateLoss();
        }


    }

    /**
     * Return a unique identifier for the item at the given position.
     * <p>
     * <p>The default implementation returns the given position.
     * Subclasses should override this method if the positions of items can change.</p>
     *
     * @param position Position within this adapter
     * @return Unique identifier for the item at position
     */

}