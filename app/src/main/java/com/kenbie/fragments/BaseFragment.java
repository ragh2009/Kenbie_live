package com.kenbie.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.kenbie.KenbieActivity;
import com.kenbie.KenbieFragmentActivity;
import com.kenbie.KenbieMessageActivity;
import com.kenbie.KenbieNavigationActivity;
import com.kenbie.SignUpActivity;
import com.kenbie.databinding.TopHeaderBarBinding;
import com.kenbie.events.ProfilePicEvent;
import com.kenbie.events.TitleBarEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BaseFragment extends Fragment {
    public boolean isInit = true;
    public SignUpActivity activity;
    public KenbieActivity kActivity;
    public KenbieNavigationActivity mActivity;
    public KenbieFragmentActivity kfActivity;
    public KenbieMessageActivity msgActivity;
    protected TopHeaderBarBinding mTopBarBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof SignUpActivity)
            activity = ((SignUpActivity) getActivity());
        else if (getActivity() instanceof KenbieActivity)
            kActivity = ((KenbieActivity) getActivity());
        else if (getActivity() instanceof KenbieNavigationActivity)
            mActivity = ((KenbieNavigationActivity) getActivity());
        else if (getActivity() instanceof KenbieFragmentActivity)
            kfActivity = ((KenbieFragmentActivity) getActivity());
        else if (getActivity() instanceof KenbieMessageActivity)
            msgActivity = ((KenbieMessageActivity) getActivity());
    }

    public void resume() {
        isInit = true;
        onResume();
    }

    protected void setupTitleBar() {
        kActivity.setupTitleBar(mTopBarBinding);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TitleBarEvent event) {
        kActivity.updateTitleBar(mTopBarBinding, event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ProfilePicEvent event) {
        kActivity.updateUserImage(mTopBarBinding, event);
    }
}
