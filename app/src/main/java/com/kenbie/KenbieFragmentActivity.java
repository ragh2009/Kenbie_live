package com.kenbie;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kenbie.fragments.UserListFragment;

public class KenbieFragmentActivity extends KenbieBackActivity {
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kenbie_fragment);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();

        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setTitle(getIntent().getStringExtra("Title"));
        }

        type = getIntent().getIntExtra("Type", 0);

        if(type == 1)
            replaceFragment(new UserListFragment(), false, false);
    }

    public void replaceFragment(final Fragment fragment, final boolean needToAddBackStack, final boolean clearStack) {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        if (needToAddBackStack && !clearStack) {
//            ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            ft.replace(R.id.container, fragment).addToBackStack(fragment.getClass().getSimpleName()).commit();
        } else {
            ft.replace(R.id.container, fragment).commitAllowingStateLoss();
        }
    }


}
