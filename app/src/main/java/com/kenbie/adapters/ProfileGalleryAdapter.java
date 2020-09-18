package com.kenbie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.R;
import com.kenbie.listeners.ProfileOptionListener;
import com.kenbie.model.OptionsData;
import com.kenbie.util.Constants;

import java.util.ArrayList;

/**
 * Created by rajaw on 7/29/2017.
 */

public class ProfileGalleryAdapter extends RecyclerView.Adapter<ProfileGalleryAdapter.ViewHolder> {
    private ArrayList<OptionsData> mOptionsData;
    private ProfileOptionListener myListeners;
    private Context mContext;
    private int mType;
    private LayoutInflater mLayoutInflater;

    public ProfileGalleryAdapter(Context context, ArrayList<OptionsData> optionsData, ProfileOptionListener mListeners, int type) {
        this.mOptionsData = optionsData;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.myListeners = mListeners;
        this.mType = type;
        if (mOptionsData == null)
            mOptionsData = new ArrayList<>();
    }

    @Override
    public ProfileGalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = mLayoutInflater.inflate(R.layout.gallery_item, parent, false);

        ProfileGalleryAdapter.ViewHolder vh = new ProfileGalleryAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ProfileGalleryAdapter.ViewHolder holder, int position) {

        if (mType == 2)
            holder.gImg.setBackgroundResource(mOptionsData.get(position).getImgId());
        else
            Glide.with(mContext).load(Constants.BASE_IMAGE_URL + mOptionsData.get(position).getName()).apply(RequestOptions.overrideOf(100, 100)).into(holder.gImg);

        holder.gImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myListeners.getDataList(mOptionsData, holder.getAdapterPosition(), mType);
//                myListeners.getAction(mOptionsData.get(holder.getAdapterPosition()));
            }
        });
    }


    @Override
    public int getItemCount() {
        return mOptionsData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ImageView gImg;

        public ViewHolder(View mView) {
            super(mView);
            gImg = (ImageView) mView.findViewById(R.id.gallery_img);
        }
    }
}
