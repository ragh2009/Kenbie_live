package com.kenbie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.listeners.InfoListener;
import com.kenbie.model.UserItem;

import java.util.ArrayList;

/**
 * Created by rajaw on 11/4/2017.
 */

public class CelebritiesAdapter extends RecyclerView.Adapter<CelebritiesAdapter.ViewHolder> {
    private ArrayList<UserItem> mOptionsData;
    private InfoListener myListeners;
    private Context mContext;
    private int mUid;
    private LayoutInflater mLayoutInflater;
    private RequestOptions options;
    private String celeTitle;
    private boolean celebInfo;

    public CelebritiesAdapter(Context context, ArrayList<UserItem> optionsData, InfoListener mListeners, String uid, String celeTitle1) {
        this.mOptionsData = optionsData;
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.myListeners = mListeners;
        if (uid != null && uid.length() > 0)
            mUid = Integer.valueOf(uid);
        else
            mUid = 0;

        this.celeTitle = celeTitle1;
        if(celeTitle == null) {
            celebInfo = true;
            celeTitle = "";
        }

        if (mOptionsData == null)
            mOptionsData = new ArrayList<>();
        options = new RequestOptions()
                .circleCrop()
                .placeholder(R.drawable.img_c_user_dummy)
                .priority(Priority.HIGH);
    }


    public void refreshData(ArrayList<UserItem> celebritiesList) {
        this.mOptionsData = celebritiesList;
        if (mOptionsData == null)
            mOptionsData = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public CelebritiesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = mLayoutInflater.inflate(R.layout.celebrities_cell_view, parent, false);

        return new CelebritiesAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CelebritiesAdapter.ViewHolder holder, int position) {
//        if (mUid == mOptionsData.get(position).getId())

        holder.mCeleTxt.setText(celeTitle);

        if (!celebInfo && position == 0 && mUid != 0 && (mUid == mOptionsData.get(position).getId())) {
            holder.celeImgAdd.setVisibility(View.VISIBLE);
            holder.celeRedBg.setVisibility(View.VISIBLE);
        } else {
            holder.celeImgAdd.setVisibility(View.GONE);
            holder.celeRedBg.setVisibility(View.GONE);
        }

        Glide.with(mContext).load(mOptionsData.get(position).getUserPic()).apply(options).into(holder.celeImg);

//        Glide.with(mContext).load(Constants.BASE_IMAGE_URL + mOptionsData.get(position).getUserPic()).apply(RequestOptions.overrideOf(100, 100).optionalCircleCrop()).into(holder.celeImg);

        holder.addCeleAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myListeners.getInfoValue(holder.getAdapterPosition(), 0);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mOptionsData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private ImageView celeImg;
        private RelativeLayout addCeleAction;
        private LinearLayout celeImgAdd, celeRedBg;
        private TextView mCeleTxt;

        public ViewHolder(View mView) {
            super(mView);
            celeImg = (ImageView) mView.findViewById(R.id.cele_img);
            celeImgAdd = (LinearLayout) mView.findViewById(R.id.cele_img_add);
            celeRedBg = (LinearLayout) mView.findViewById(R.id.cele_red_bg);
            addCeleAction = (RelativeLayout) mView.findViewById(R.id.add_cele_action);
            mCeleTxt = (TextView) mView.findViewById(R.id.cele_title);
            mCeleTxt.setTypeface(KenbieApplication.S_NORMAL);
        }
    }
}
