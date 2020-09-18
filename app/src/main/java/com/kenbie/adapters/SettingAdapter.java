package com.kenbie.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kenbie.KenbieApplication;
import com.kenbie.R;

/**
 * Created by rajaw on 8/29/2017.
 */

public class SettingAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mData;
    private LayoutInflater mLayoutInflater;

    public SettingAdapter(Context context, String[] value) {
        mData = value;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public void refreshData(String[] value){
        mData = value;
        notifyDataSetChanged();
    }

    public int getCount() {
        return mData.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = mLayoutInflater.inflate(R.layout.text_cell_view, null);
        else ;


        TextView mTitle = (TextView) convertView.findViewById(R.id.title);
        mTitle.setText(mData[position]);
        mTitle.setTypeface(KenbieApplication.S_NORMAL);

        return convertView;
    }
}
