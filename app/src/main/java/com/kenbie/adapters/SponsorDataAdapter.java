package com.kenbie.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.listeners.UserActionListener;
import com.kenbie.model.UserItem;
import com.kenbie.util.Constants;
import com.kenbie.util.Utility;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by rajaw on 02/06/2019.
 */

public class SponsorDataAdapter extends RecyclerView.Adapter<SponsorDataAdapter.ViewHolder> {
    private ArrayList<UserItem> mUserData;
    private UserActionListener myListeners;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mYear;

    public SponsorDataAdapter(Context context, ArrayList<UserItem> userItemArrayList, UserActionListener mListeners) {
        this.mUserData = userItemArrayList;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.myListeners = mListeners;
        if (mUserData == null)
            mUserData = new ArrayList<>();
        Calendar mCalendar = Calendar.getInstance();
        mYear = mCalendar.get(Calendar.YEAR);
    }

    @Override
    public SponsorDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = mLayoutInflater.inflate(R.layout.sponsor_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Glide.with(mContext)
                .load(Constants.SPONSOR_BASE_IMAGE_URL + mUserData.get(position).getUserPic())
                .into(holder.sImg);

        holder.sponsorAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myListeners.getUserAction(mUserData.get(holder.getAdapterPosition()), mUserData.get(holder.getAdapterPosition()).getType());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mUserData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ImageView sImg;
        private CardView sponsorAction;

        public ViewHolder(View mView) {
            super(mView);
            sImg = (ImageView) mView.findViewById(R.id.s_image);
            sponsorAction = (CardView) mView.findViewById(R.id.sponsor_action);
        }
    }
}
