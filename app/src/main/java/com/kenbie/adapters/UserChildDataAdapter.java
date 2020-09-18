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
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
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

public class UserChildDataAdapter extends RecyclerView.Adapter<UserChildDataAdapter.ViewHolder> {
    private ArrayList<UserItem> mUserData;
    private UserActionListener myListeners;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mType;
    private RequestOptions options = null;

    public UserChildDataAdapter(Context context, ArrayList<UserItem> userItemArrayList, UserActionListener mListeners, int type) {
        this.mUserData = userItemArrayList;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.myListeners = mListeners;
        if (mUserData == null)
            mUserData = new ArrayList<>();
        mType = type;
        options = new RequestOptions()
                .placeholder(R.drawable.no_img)
                .priority(Priority.HIGH);
    }

    @Override
    public UserChildDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = null;
        if (mType == 1)
            v = mLayoutInflater.inflate(R.layout.user_new_item, parent, false);
        else
            v = mLayoutInflater.inflate(R.layout.user_new_horz_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Glide.with(mContext)
                .load(Constants.BASE_IMAGE_URL + mUserData.get(position).getUserPic()).apply(options)
                .into(holder.userImg);

//        holder.llTransparent.setBackgroundColor(Utility.getColorWithAlpha(Color.BLACK, 0.2f));
        holder.llTransparent.setBackgroundResource(R.drawable.overlay_profiles);

        holder.userPhotoTxt.setText(mUserData.get(position).getTotalImage() + "");
        holder.userPhotoTxt.setTypeface(KenbieApplication.S_NORMAL);

        String userData = "", firstName = "";
        firstName = mUserData.get(position).getFirstName();
        if (firstName != null && firstName.length() > 14)
            firstName = firstName.substring(0, 14);
        else
            firstName = mUserData.get(position).getFirstName();

        if (mUserData.get(position).getBirthYear() != 0)
            if (mUserData.get(position).getIsActive() == 1) {
                holder.uStatus.setVisibility(View.VISIBLE);
                userData = userData + "<font color=#404040>" + firstName + "</font>";
//                userData = userData + "<font color=#404040>" + firstName + ", " + mUserData.get(position).getBirthYear()  + "</font>";
            } else {
                holder.uStatus.setVisibility(View.INVISIBLE);
                userData = userData + "<font color=#404040>" + firstName + "</font>";
            }
        else if (mUserData.get(position).getIsActive() == 1) {
            holder.uStatus.setVisibility(View.VISIBLE);
            userData = userData + "<font color=#404040>" + firstName + "</font>";
        } else {
            holder.uStatus.setVisibility(View.INVISIBLE);
            userData = userData + "<font color=#404040>" + firstName + "</font>";
        }

        holder.uNameTxt.setText(Html.fromHtml(userData), TextView.BufferType.SPANNABLE);
        holder.uNameTxt.setTypeface(KenbieApplication.S_SEMI_BOLD);

        String location = "";

        if (mUserData.get(position).getCity() != null && !mUserData.get(position).getCity().equalsIgnoreCase("null"))
            location = mUserData.get(position).getCity();
        else
            location = "";

        if (mUserData.get(position).getCountry() != null && !mUserData.get(position).getCountry().equalsIgnoreCase("null")) {
            if (location.length() > 3)
                location = location + ", " + mUserData.get(position).getCountry();
            else
                location = mUserData.get(position).getCountry();
        } else
            location = location + "";

        holder.uLocationTxt.setText(location);
        holder.uLocationTxt.setTypeface(KenbieApplication.S_NORMAL);


        holder.userView.setOnClickListener(new View.OnClickListener() {
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
        private ImageView userImg;
        private TextView userPhotoTxt, uNameTxt, uLocationTxt, uStatus;
        private LinearLayout llTransparent;
        private CardView userView;

        public ViewHolder(View mView) {
            super(mView);
            userImg = (ImageView) mView.findViewById(R.id.u_image);
            llTransparent = (LinearLayout) mView.findViewById(R.id.ll_transparent);
            userPhotoTxt = (TextView) mView.findViewById(R.id.u_photo);
            uNameTxt = (TextView) mView.findViewById(R.id.u_name);
            uLocationTxt = (TextView) mView.findViewById(R.id.u_location);
            uStatus = (TextView) mView.findViewById(R.id.u_status);
            userView = (CardView) mView.findViewById(R.id.user_cv);
        }
    }
}
