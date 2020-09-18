package com.kenbie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.kenbie.KenbieApplication;
import com.kenbie.R;
import com.kenbie.listeners.ProfileOptionListener;
import com.kenbie.model.OptionsData;

import java.util.ArrayList;

/**
 * Created by rajaw on 9/6/2017.
 */

public class SettingUpdateAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<OptionsData> mData;
    private LayoutInflater mLayoutInflater;
    private ProfileOptionListener mListeners;

    public SettingUpdateAdapter(Context context, ArrayList<OptionsData> value, ProfileOptionListener socialLinksFragment) {
        mData = value;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mListeners = socialLinksFragment;
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
            convertView = mLayoutInflater.inflate(R.layout.text_cell_view, null);
        else ;

        TextView sTitle = (TextView) convertView.findViewById(R.id.title);
        sTitle.setText(mData.get(position).getName());
        sTitle.setTypeface(KenbieApplication.S_NORMAL);

        ((ImageView) convertView.findViewById(R.id.arrow_img)).setVisibility(View.GONE);

        SwitchCompat switchCompat = (SwitchCompat) convertView.findViewById(R.id.m_switch);
        switchCompat.setVisibility(View.VISIBLE);
        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListeners.getDataList(mData, position, 1);
            }
        });

       /* switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mListeners.getDataList(mData, position, isChecked ? 1 : 0);
            }
        });
*/
        if (mData.get(position).isActive())
            switchCompat.setChecked(true);
        else
            switchCompat.setChecked(false);

        return convertView;
    }
}
