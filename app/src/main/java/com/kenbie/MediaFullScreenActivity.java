package com.kenbie;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.kenbie.adapters.FullScreenImageAdapter;
import com.kenbie.listeners.ProfileOptionListener;
import com.kenbie.listeners.SwipeListenerInterface;
import com.kenbie.model.OptionsData;
import com.kenbie.views.SwipeListener;

import java.util.ArrayList;

public class MediaFullScreenActivity extends AppCompatActivity implements ProfileOptionListener, SwipeListenerInterface {
    private int selPosition = 0;
    private ArrayList<OptionsData> imageList;
    private ViewPager mediaPager;
    private FullScreenImageAdapter mediaAdapter;
    private int dotsCount;
    private ImageView[] dots;
    private LinearLayout sliderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_full_screen);

        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.hide();

        initData();
        initViewIds();
    }

    private void initData() {
        try {
            selPosition = getIntent().getIntExtra("SelPos", 0);
            KenbieApplication.galleryIndex = selPosition;
            imageList = (ArrayList<OptionsData>) getIntent().getSerializableExtra("MediaData");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initViewIds() {
        ImageView imgCross = (ImageView) findViewById(R.id.img_cross);
        imgCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAction(null);
            }
        });

//        galleryViewLayout = findViewById(R.id.gallery_view);
        sliderLayout = findViewById(R.id.slider_dots);
        mediaPager = findViewById(R.id.media_pager);
        SwipeListener sl = new SwipeListener(this);
//        galleryViewLayout.setOnTouchListener(sl);
        mediaAdapter = new FullScreenImageAdapter(this, imageList, this, sl);
        mediaPager.setAdapter(mediaAdapter);
        mediaPager.setCurrentItem(selPosition);
        dotsCount = mediaAdapter.getCount();
        if (dotsCount > 1) {
            dots = new ImageView[dotsCount];

            for (int i = 0; i < dotsCount; i++) {
                dots[i] = new ImageView(this);
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.non_active_dot));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 0, 8, 0);
                sliderLayout.addView(dots[i], params);
            }

            dots[selPosition].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dot));

            mediaPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    KenbieApplication.galleryIndex = position;
                    for (int i = 0; i < dotsCount; i++) {
                        dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                    }

                    dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void getAction(OptionsData value) {
        finish();
        overridePendingTransition(0, android.R.anim.fade_out);
    }

    @Override
    public void getDataList(ArrayList<OptionsData> value, int position, int type) {

    }

    @Override
    public void onRightToLeftSwipe(View v) {
        finish();
        overridePendingTransition(0, R.anim.slide_down);
//        overridePendingTransition(0, android.R.anim.fade_out);
    }

    @Override
    public void onLeftToRightSwipe(View v) {
        finish();
        overridePendingTransition(0, R.anim.slide_down);
    }

    @Override
    public void onTopToBottomSwipe(View v) {

    }

    @Override
    public void onBottomToTopSwipe(View v) {

    }
}
