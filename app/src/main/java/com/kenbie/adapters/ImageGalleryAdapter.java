package com.kenbie.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.vision.text.Line;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.listeners.ProfileOptionListener;
import com.kenbie.model.OptionsData;
import com.kenbie.util.Constants;

import java.util.ArrayList;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ViewHolder> {
    private ArrayList<OptionsData> mOptionsData;
    private ProfileOptionListener myListeners;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int selIndex = -1;
    private String addPhotoTitle;

    public ImageGalleryAdapter(Context context, ArrayList<OptionsData> optionsData, ProfileOptionListener mListeners, SharedPreferences mPref) {
        this.mOptionsData = optionsData;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        addPhotoTitle = mPref.getString("237", "Add Photos");
        this.myListeners = mListeners;
        if (mOptionsData == null)
            mOptionsData = new ArrayList<>();
    }

    public void refreshView(int sel) {
        this.selIndex = sel;
        notifyDataSetChanged();
    }

    @Override
    public ImageGalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = mLayoutInflater.inflate(R.layout.photo_gallery_item, parent, false);
//        ImageGalleryAdapter.ViewHolder vh = new ImageGalleryAdapter.ViewHolder(v);
        return new ImageGalleryAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageGalleryAdapter.ViewHolder holder, int position) {
        // gImg - g_photo, gBg - g_bg, g_camera

        if (position == 0) {
            holder.gImg.setVisibility(View.GONE);
            holder.gBg.setVisibility(View.VISIBLE);
            holder.gBg.setAlpha(1);
            holder.gCameraTitle.setVisibility(View.VISIBLE);
            holder.gCameraTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_camera, 0, 0);
            holder.gCameraTitle.setText(addPhotoTitle);
        } else if (position == selIndex) {
            holder.gBg.setVisibility(View.VISIBLE);
            holder.gImg.setVisibility(View.VISIBLE);
            holder.gCameraTitle.setVisibility(View.VISIBLE);
            holder.gBg.setAlpha((float) 0.5);
            holder.gCameraTitle.setText("");
            holder.gCameraTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_white_tick, 0, 0);
            Glide.with(mContext).load(mOptionsData.get(position).getName()).into(holder.gImg);
        } else {
            holder.gImg.setVisibility(View.VISIBLE);
            holder.gBg.setVisibility(View.GONE);
            holder.gCameraTitle.setVisibility(View.GONE);
            holder.gCameraTitle.setText("");
            Glide.with(mContext).load(mOptionsData.get(position).getName()).into(holder.gImg);
        }

        holder.gTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() == 0)
                    myListeners.getDataList(mOptionsData, holder.getAdapterPosition(), 1);
                else
                    myListeners.getDataList(mOptionsData, holder.getAdapterPosition(), 2);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mOptionsData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private RelativeLayout gTakePic;
        private ImageView gImg;
        private LinearLayout gBg;
        private TextView gCameraTitle;

        public ViewHolder(View mView) {
            super(mView);
            gImg = (ImageView) mView.findViewById(R.id.g_photo);
            gTakePic = (RelativeLayout) mView.findViewById(R.id.g_take_pic);
            gBg = (LinearLayout) mView.findViewById(R.id.g_bg);
            gCameraTitle = (TextView) mView.findViewById(R.id.g_camera);
            gCameraTitle.setTypeface(KenbieApplication.S_NORMAL);
        }
    }
}
