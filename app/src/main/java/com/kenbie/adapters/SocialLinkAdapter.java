package com.kenbie.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.listeners.ProfileOptionListener;
import com.kenbie.model.OptionsData;

import java.util.ArrayList;

/**
 * Created by rajaw on 9/5/2017.
 */

public class SocialLinkAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<OptionsData> mData;
    private LayoutInflater mLayoutInflater;
    private ProfileOptionListener mListeners;
    private  SharedPreferences pref;

    public SocialLinkAdapter(Context context, ArrayList<OptionsData> value, ProfileOptionListener socialLinksFragment, SharedPreferences pref) {
        mData = value;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mListeners = socialLinksFragment;
        this.pref = pref;
    }


    public void refreshData(ArrayList<OptionsData> mSocialList) {
        mData = mSocialList;
        notifyDataSetChanged();
    }

    public int getCount() {
        return mData.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = mLayoutInflater.inflate(R.layout.social_link_cell_view, null);
        else ;

        ImageView sImg = (ImageView) convertView.findViewById(R.id.s_img);
        sImg.setBackgroundResource(mData.get(position).getImgId());

        TextView sTitle = (TextView) convertView.findViewById(R.id.s_title);
        sTitle.setText(mData.get(position).getName());
        sTitle.setTypeface(KenbieApplication.S_SEMI_BOLD);

        TextView sStatus = (TextView) convertView.findViewById(R.id.s_status);
        sStatus.setText(mData.get(position).getName());
        sStatus.setTypeface(KenbieApplication.S_NORMAL);
        sStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListeners.getDataList(mData, position, 1);
            }
        });

        if (mData.get(position).isActive()) {
            sStatus.setText(pref.getString("250", "Remove"));
            sStatus.setBackgroundResource(R.drawable.btn_red_bg_style_15);
            sStatus.setTextColor(Color.WHITE);
        } else {
            sStatus.setText(pref.getString("249", "LINK"));
            sStatus.setBackgroundResource(R.drawable.btn_white_bg_style);
            sStatus.setTextColor(Color.BLACK);
        }

        return convertView;
    }

}
