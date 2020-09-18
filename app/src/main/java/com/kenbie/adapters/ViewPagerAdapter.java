package com.kenbie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.R;
import com.kenbie.listeners.ProfileOptionListener;
import com.kenbie.model.OptionsData;

import java.util.ArrayList;

import static com.kenbie.util.Constants.IMAGE_LARGE_BASE_URL;

public class ViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private LayoutInflater layoutInflater;
    private ArrayList<OptionsData> imageList;
    private ProfileOptionListener mediaListeners;
    private RequestOptions options = null, options1 = null;

    public ViewPagerAdapter(Context context, ArrayList<OptionsData> imgArray, ProfileOptionListener mediaListeners) {
        this.mContext = context;
        this.imageList = imgArray;
        if (imageList == null)
            imgArray = new ArrayList<>();
        this.mediaListeners = mediaListeners;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        options = new RequestOptions()
                .optionalFitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        options1 = new RequestOptions()
                .optionalFitCenter()
                .placeholder(R.drawable.no_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

//                .error(R.drawable.ic_pic_error)
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        View view = layoutInflater.inflate(R.layout.gallery_layout_item, null);

        ImageView imgGallery = view.findViewById(R.id.img_gallery);
        ImageView imgWatermark = view.findViewById(R.id.img_watermark);

//        if (imageList.get(position).getMediaPath() != null && (imageList.get(position).getMediaPath().endsWith(".mp4") || imageList.get(position).getMediaPath().endsWith(".m4a")))
//            imageView.setBackgroundResource(R.drawable.ic_video_play);
//        else
//            Glide.with(mContext).load(imageList.get(position).getMediaPath()).apply(RequestOptions.centerInsideTransform()).into(imageView);

//        Glide.with(mContext).load(IMAGE_LARGE_BASE_URL + imageList.get(position).getName()).apply(options).into(imageView);
        if (imageList.get(position).getId() != -1) {
            imgWatermark.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(imageList.get(position).getName()).thumbnail(0.25f).transition(DrawableTransitionOptions.withCrossFade()).apply(options).into(imgGallery);
        }else {
            imgWatermark.setVisibility(View.GONE);
            Glide.with(mContext).load(imageList.get(position).getName()).thumbnail(0.25f).transition(DrawableTransitionOptions.withCrossFade()).apply(options1).into(imgGallery);
        }

        imgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaListeners.getDataList(imageList, position, 1);
            }
        });

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}